package com.wapitia
package spreadsheet
package marshal

/** Accumulates generated objects coming from each data row of a spreadsheet. */
trait RowAccumulator[A] {

  def add(row: A): Unit
}

/** RowAccumulator which bundles the accumulated rows into a sequence  */
trait SeqRowAccumulator[A] extends RowAccumulator[A] {

  override def add(row: A)

  def results: Seq[A]
}
