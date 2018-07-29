package com.wapitia
package gsheets

import com.wapitia.common.marshal.InMarshal

/** marshal package level constants. */
package object marshal {
  
  /** Marshal a Google Sheets Date value into a [[java.time.LocalDate]].
   *  The input value type is expected to be a [[java.math.BigDecimal]].  
   *  Nulls are not allowed.
   *  @tparam AnyRef input value type allows any incoming type to be tested. 
   */
  val simpleDateMarshal: InMarshal[Any,java.time.LocalDate] = GSheetsDateMarshaller.simpleMarshal  
  
  /** Marshal a Google Sheets Date value into a [[java.time.LocalDate]]. 
   *  The input value type is expected to be a [[java.math.BigDecimal]]
   *  or an empty string indicating `null`.  
   *  @tparam AnyRef input value type allows any incoming type to be tested. 
   */
  val nullableDateMarshal: InMarshal[Any,java.time.LocalDate] = GSheetsDateMarshaller.nullableMarshal
}