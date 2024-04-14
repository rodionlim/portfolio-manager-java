package com.rodion.adelie.util;

import java.util.List;
import java.util.function.Function;

/** some useful tools to display strings in command line help or error messages */
public class StringUtils {

  /**
   * Joins a list into string elements with a delimiter but having a last different delimiter
   * Example: "this thing, that thing and this other thing"
   *
   * @param delimiter delimiter for all the items except before the last one
   * @param lastDelimiter delimiter before the last item
   * @return a delimited string representation of the list
   */
  public static Function<List<String>, String> joiningWithLastDelimiter(
      final String delimiter, final String lastDelimiter) {
    return list -> {
      final int last = list.size() - 1;
      if (last < 1) return String.join(delimiter, list);
      return String.join(
          lastDelimiter, String.join(delimiter, list.subList(0, last)), list.get(last));
    };
  }
}
