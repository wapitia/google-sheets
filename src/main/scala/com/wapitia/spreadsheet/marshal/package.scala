package com.wapitia
package spreadsheet

import java.math.BigDecimal

import com.wapitia.common.marshal.MarshalIn
import com.wapitia.gsheets.marshal.GSheetsDateMarshaller
import com.wapitia.common.marshal.StringMarshalIn

/** spreadsheet.marshal Constants and commonly shared functions. */
package object marshal {
  
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
	
	val simpleStringMarshal = new StringMarshalIn
	
  val simpleNumberMarshal: MarshalIn[Any,BigDecimal] = NumberMarshaller.simpleMarshal
  
  val nullableNumberMarshal: MarshalIn[Any,BigDecimal] = NumberMarshaller.nullableMarshal  
	
  val simpleCurrencyMarshal: MarshalIn[Any,BigDecimal] = NumberMarshaller.simpleCurrencyMarshal
  
  val nullableCurrencyMarshal: MarshalIn[Any,BigDecimal] = NumberMarshaller.nullableCurrencyMarshal  
	
  val intMarshal: MarshalIn[Any,Int] = NumberMarshaller.intMarshal
  
  val boolMarshal: MarshalIn[Any,Boolean] = NumberMarshaller.boolMarshal
}