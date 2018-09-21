package com.wapitia
package spreadsheet

import scala.collection.mutable.ListBuffer

import com.wapitia.common.marshal.{InMarshal, StringInMarshal, NumberMarshals}
import com.wapitia.spreadsheet.marshal.SeqRowAccumulator

/** spreadsheet.marshal Constants and commonly shared functions. */
package object marshal {

  type SheetRow = List[Any]

  /** Filter returns true if the given row is to be processed.
   *  This function takes a row number and the sheet row as a sequential list of values.
   */
  type RowFilter = (Int, SheetRow) => Boolean

  val simpleStringMarshal = new StringInMarshal

  val simpleNumberMarshal: InMarshal[Any,BigDecimal] = NumberMarshals.simpleMarshal

  val nullableNumberMarshal: InMarshal[Any,BigDecimal] = NumberMarshals.nullableMarshal

  val simpleCurrencyMarshal: InMarshal[Any,BigDecimal] = NumberMarshals.simpleCurrencyMarshal

  val nullableCurrencyMarshal: InMarshal[Any,BigDecimal] = NumberMarshals.nullableCurrencyMarshal

  val intMarshal: InMarshal[Any,Int] = NumberMarshals.intMarshal

  val boolMarshal: InMarshal[Any,Boolean] = NumberMarshals.boolMarshal

  /** Create and return a fresh SeqRowAccumulator which is backed by a ListBuffer */
  def seqRowAccumulator[A](): SeqRowAccumulator[A] = new ListRowAccumulator[A]()

}

/** A SeqRowAccumulator using a ListBuffer upon which rows are appended. */
class ListRowAccumulator[A] extends SeqRowAccumulator[A] {

  val buf = new ListBuffer[A]()

  override def add(row: A) {
    buf += row
  }

  override def results: Seq[A] = buf.toSeq
}
