package com.wapitia
package spreadsheet

import com.wapitia.common.marshal.{InMarshal, StringInMarshal, NumberMarshals}
import com.wapitia.gsheets.marshal.GSheetsDateMarshaller

/** spreadsheet.marshal Constants and commonly shared functions. */
package object marshal {

  val simpleStringMarshal = new StringInMarshal

  val simpleNumberMarshal: InMarshal[Any,BigDecimal] = NumberMarshals.simpleMarshal

  val nullableNumberMarshal: InMarshal[Any,BigDecimal] = NumberMarshals.nullableMarshal

  val simpleCurrencyMarshal: InMarshal[Any,BigDecimal] = NumberMarshals.simpleCurrencyMarshal

  val nullableCurrencyMarshal: InMarshal[Any,BigDecimal] = NumberMarshals.nullableCurrencyMarshal

  val intMarshal: InMarshal[Any,Int] = NumberMarshals.intMarshal

  val boolMarshal: InMarshal[Any,Boolean] = NumberMarshals.boolMarshal
}
