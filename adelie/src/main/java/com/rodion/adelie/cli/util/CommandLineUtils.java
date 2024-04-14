package com.rodion.adelie.cli.util;

import com.google.common.base.Strings;
import com.rodion.adelie.cli.converter.TypeFormatter;
import com.rodion.adelie.cli.options.OptionParser;
import com.rodion.adelie.util.StringUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import picocli.CommandLine;

/** The Command line utils. */
public class CommandLineUtils {
  /** The constant DEPENDENCY_WARNING_MSG. */
  public static final String DEPENDENCY_WARNING_MSG =
      "{} has been ignored because {} was not defined on the command line.";

  /** The constant MULTI_DEPENDENCY_WARNING_MSG. */
  public static final String MULTI_DEPENDENCY_WARNING_MSG =
      "{} ignored because none of {} was defined.";

  /** The constant DEPRECATION_WARNING_MSG. */
  public static final String DEPRECATION_WARNING_MSG = "{} has been deprecated, use {} instead.";

  /** The constant DEPRECATED_AND_USELESS_WARNING_MSG. */
  public static final String DEPRECATED_AND_USELESS_WARNING_MSG =
      "{} has been deprecated and is now useless, remove it.";

  /**
   * Check if options are passed that require an option to be true to have any effect and log a
   * warning with the list of affected options.
   *
   * <p>Note that in future version of PicoCLI some options dependency mechanism may be implemented
   * that could replace this. See https://github.com/remkop/picocli/issues/295
   *
   * @param logger the logger instance used to log the warning
   * @param commandLine the command line containing the options we want to check
   * @param mainOptionName the name of the main option to test dependency against. Only used for
   *     display.
   * @param isMainOptionCondition the condition to test the options dependencies, if true will test
   *     if not won't
   * @param dependentOptionsNames a list of option names that can't be used if condition is met.
   *     Example: if --miner-coinbase is in the list and condition is that --miner-enabled should
   *     not be false, we log a warning.
   */
  public static void checkOptionDependencies(
      final Logger logger,
      final CommandLine commandLine,
      final String mainOptionName,
      final boolean isMainOptionCondition,
      final List<String> dependentOptionsNames) {
    if (isMainOptionCondition) {
      final String affectedOptions = getAffectedOptions(commandLine, dependentOptionsNames);

      if (!affectedOptions.isEmpty()) {
        logger.warn(DEPENDENCY_WARNING_MSG, affectedOptions, mainOptionName);
      }
    }
  }

  /**
   * Check if options are passed that require an option to be true to have any effect and log a
   * warning with the list of affected options. Multiple main options may be passed to check
   * dependencies against.
   *
   * <p>Note that in future version of PicoCLI some options dependency mechanism may be implemented
   * that could replace this. See https://github.com/remkop/picocli/issues/295
   *
   * @param logger the logger instance used to log the warning
   * @param commandLine the command line containing the options we want to check display.
   * @param stringToLog the string that is going to be logged.
   * @param isMainOptionCondition the conditions to test dependent options against. If all
   *     conditions are true, dependent options will be checked.
   * @param dependentOptionsNames a list of option names that can't be used if condition is met.
   *     Example: if --min-gas-price is in the list and condition is that --miner-enabled should not
   *     be false, we log a warning.
   */
  public static void checkMultiOptionDependencies(
      final Logger logger,
      final CommandLine commandLine,
      final String stringToLog,
      final List<Boolean> isMainOptionCondition,
      final List<String> dependentOptionsNames) {
    if (isMainOptionCondition.stream().allMatch(isTrue -> isTrue)) {
      final String affectedOptions = getAffectedOptions(commandLine, dependentOptionsNames);

      if (!affectedOptions.isEmpty()) {
        logger.warn(stringToLog);
      }
    }
  }

  /**
   * Fail if option doesn't meet requirement.
   *
   * @param commandLine the command line
   * @param errorMessage the error message
   * @param requirement the requirement
   * @param dependentOptionsNames the dependent options names
   */
  public static void failIfOptionDoesntMeetRequirement(
      final CommandLine commandLine,
      final String errorMessage,
      final boolean requirement,
      final List<String> dependentOptionsNames) {
    if (!requirement) {
      final String affectedOptions = getAffectedOptions(commandLine, dependentOptionsNames);

      if (!affectedOptions.isEmpty()) {
        throw new CommandLine.ParameterException(
            commandLine, errorMessage + " [" + affectedOptions + "]");
      }
    }
  }

  /**
   * Return all the option names declared in a class. Note this will recursively check in any inner
   * option class if present.
   *
   * @param optClass the class to look for options
   * @return a list of option names found in the class
   */
  public static List<String> getCLIOptionNames(final Class<?> optClass) {
    final List<String> cliOpts = new ArrayList<>();
    final Field[] fields = optClass.getDeclaredFields();
    for (Field field : fields) {
      field.setAccessible(true);
      Annotation ann = field.getAnnotation(CommandLine.Option.class);
      if (ann != null) {
        final var optAnn = CommandLine.Option.class.cast(ann);
        cliOpts.add(optAnn.names()[0]);
      } else {
        ann = field.getAnnotation(CommandLine.ArgGroup.class);
        if (ann != null) {
          cliOpts.addAll(getCLIOptionNames(field.getType()));
        }
      }
    }
    return cliOpts;
  }

  /**
   * Converts the runtime options into their CLI representation. Options with a value equals to its
   * default are not included in the result since redundant. Note this will recursively check in any
   * inner option class if present.
   *
   * @param currOptions the actual runtime options
   * @param defaults the default option values
   * @return a list of CLI arguments
   */
  public static List<String> getCLIOptions(final Object currOptions, final Object defaults) {
    final List<String> cliOpts = new ArrayList<>();
    final Field[] fields = currOptions.getClass().getDeclaredFields();
    for (Field field : fields) {
      field.setAccessible(true);
      Annotation ann = field.getAnnotation(CommandLine.Option.class);
      if (ann != null) {
        try {
          var optVal = field.get(currOptions);
          if (!Objects.equals(optVal, field.get(defaults))) {
            var optAnn = CommandLine.Option.class.cast(ann);
            final var optConverter = optAnn.converter();
            cliOpts.add(optAnn.names()[0] + "=" + formatValue(optConverter, optVal));
          }
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      } else {
        ann = field.getAnnotation(CommandLine.ArgGroup.class);
        if (ann != null) {
          try {
            cliOpts.addAll(getCLIOptions(field.get(currOptions), field.get(defaults)));
          } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
          }
        }
      }
    }
    return cliOpts;
  }

  /**
   * There are different ways to format an option value back to its CLI form, the first is to use a
   * {@link TypeFormatter} if present, otherwise the formatting it is delegated to {@link
   * OptionParser#format(Object)}
   *
   * @param optConverter the list of converter types for the option
   * @param optVal the value of the options
   * @return a string with the CLI form of the value
   */
  @SuppressWarnings("unchecked")
  private static String formatValue(
      final Class<? extends CommandLine.ITypeConverter<?>>[] optConverter, final Object optVal) {
    return Arrays.stream(optConverter)
        .filter(c -> Arrays.stream(c.getInterfaces()).anyMatch(i -> i.equals(TypeFormatter.class)))
        .findFirst()
        .map(
            ctf -> {
              try {
                return (TypeFormatter) ctf.getDeclaredConstructor().newInstance();
              } catch (InstantiationException
                  | IllegalAccessException
                  | InvocationTargetException
                  | NoSuchMethodException e) {
                throw new RuntimeException(e);
              }
            })
        .map(tf -> tf.format(optVal))
        .orElseGet(() -> OptionParser.format(optVal));
  }

  private static String getAffectedOptions(
      final CommandLine commandLine, final List<String> dependentOptionsNames) {
    return commandLine.getCommandSpec().options().stream()
        .filter(option -> Arrays.stream(option.names()).anyMatch(dependentOptionsNames::contains))
        .filter(CommandLineUtils::isOptionSet)
        .map(option -> option.names()[0])
        .collect(
            Collectors.collectingAndThen(
                Collectors.toList(), StringUtils.joiningWithLastDelimiter(", ", " and ")));
  }

  private static boolean isOptionSet(final CommandLine.Model.OptionSpec option) {
    final CommandLine commandLine = option.command().commandLine();
    try {
      return !option.stringValues().isEmpty()
          || !Strings.isNullOrEmpty(commandLine.getDefaultValueProvider().defaultValue(option));
    } catch (final Exception e) {
      return false;
    }
  }

  /**
   * Is the option with that name set on the command line?
   *
   * @param commandLine the command line
   * @param optionName the option name to check
   * @return true if set
   */
  public static boolean isOptionSet(final CommandLine commandLine, final String optionName) {
    return commandLine.getCommandSpec().options().stream()
        .filter(optionSpec -> Arrays.stream(optionSpec.names()).anyMatch(optionName::equals))
        .anyMatch(CommandLineUtils::isOptionSet);
  }
}
