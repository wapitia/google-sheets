package com.wapitia
package spreadsheet

import com.wapitia.common.marshal.{InMarshal, StringInMarshal, NumberMarshals}
import com.wapitia.gsheets.marshal.GSheetsDateMarshaller

/** spreadsheet.marshal Constants and commonly shared functions. */
package object marshal {

  /** returns true only if the `row` is not blank. `rowNo` is ignored. */
  def isNonEmptyRow(rowNo: Int, row: List[Any]): Boolean = !isBlankRow(row)

  /** true if any and all cells in the row's list are blank. */
  def isBlankRow(row: List[Any]): Boolean = row match {
    case Nil => true
    case h :: t => isBlankCell(h) && isBlankRow(t)
  }

  /** return true if the given cell is blank.
   *  The cell is considered blank if it is null or an empty string.
   *
   *  In Google sheets, a cell can be empty if and only if it is an
   *  empty java String. Never seen a null.
   */
  def isBlankCell(cell: Any): Boolean = cell match {
    case null => true
    case s: String => s.isEmpty
    case _ => false
  }

  val simpleStringMarshal = new StringInMarshal

  val simpleNumberMarshal: InMarshal[Any,BigDecimal] = NumberMarshals.simpleMarshal

  val nullableNumberMarshal: InMarshal[Any,BigDecimal] = NumberMarshals.nullableMarshal

  val simpleCurrencyMarshal: InMarshal[Any,BigDecimal] = NumberMarshals.simpleCurrencyMarshal

  val nullableCurrencyMarshal: InMarshal[Any,BigDecimal] = NumberMarshals.nullableCurrencyMarshal

  val intMarshal: InMarshal[Any,Int] = NumberMarshals.intMarshal

  val boolMarshal: InMarshal[Any,Boolean] = NumberMarshals.boolMarshal
}
