package com.wapitia
package spreadsheet
package marshal

/** Accumulates generated objects coming from each data row of a spreadsheet.
 *  @tparam A Type of element that has been marshalled from a spreadsheet row.
 */
trait RowAccumulator[A] {

  /** Append a row to this accumulator. */
  def add(row: A)
}
