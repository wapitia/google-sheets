package com.wapitia
package spreadsheet
package marshal

/** RowAccumulator which bundles the accumulated rows into a sequence.
 *  @tparam A Type of element that has been marshalled from a spreadsheet row.
 */
trait SeqRowAccumulator[A] extends RowAccumulator[A] {

  /** Append a row to this accumulator. */
  override def add(row: A)

  /** Return the accumulated rows as a sequence. */
  def results: Seq[A]
}
