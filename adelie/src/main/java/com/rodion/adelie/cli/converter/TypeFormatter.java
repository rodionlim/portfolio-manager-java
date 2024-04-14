package com.rodion.adelie.cli.converter;

/**
 * This interface can be used to give a converter the capability to format the converted value back
 * to its CLI form
 *
 * @param <V> the type of the CLI converted runtime value
 */
public interface TypeFormatter<V> {
  /**
   * Format a converted value back to its CLI form
   *
   * @param value the converted value
   * @return the textual CLI form of the value
   */
  String format(V value);
}
