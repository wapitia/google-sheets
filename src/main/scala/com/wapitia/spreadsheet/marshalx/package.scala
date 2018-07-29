package com.wapitia
package spreadsheet

import com.wapitia.common.marshal.InMarshal
import com.wapitia.gsheets.marshal.GSheetsDateMarshaller
import com.wapitia.common.marshal.StringInMarshal
import com.wapitia.spreadsheet.marshal.NumberMarshaller

/** spreadsheet.marshal Constants and commonly shared functions. */
package object marshalx {

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

  val simpleNumberMarshal: InMarshal[Any,BigDecimal] = NumberMarshaller.simpleMarshal

  val nullableNumberMarshal: InMarshal[Any,BigDecimal] = NumberMarshaller.nullableMarshal

  val simpleCurrencyMarshal: InMarshal[Any,BigDecimal] = NumberMarshaller.simpleCurrencyMarshal

  val nullableCurrencyMarshal: InMarshal[Any,BigDecimal] = NumberMarshaller.nullableCurrencyMarshal

  val intMarshal: InMarshal[Any,Int] = NumberMarshaller.intMarshal

  val boolMarshal: InMarshal[Any,Boolean] = NumberMarshaller.boolMarshal
}
