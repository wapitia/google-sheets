package com.wapitia
package gsheets

import java.time.LocalDate

import com.wapitia.common.marshal.InMarshal

/** marshal package level constants. */
package object marshal {

  /** The date by which the Google Sheets date and time BigDecimals are relative.
   *  This according to e.g. https://en.wikipedia.org/wiki/Epoch_(reference_date)#1899-12-30
   */
  val GoogleSheetsEpoch = LocalDate.of(1899, 12, 30)

  /** Marshal a Google Sheets Date value into a [[java.time.LocalDate]].
   *  The input value type is expected to be a [[java.math.BigDecimal]] interpreted
   *  as number of days since the Google Sheets epoch 1899-12-30.
   *  Nulls are not allowed.
   *  This singleton may be shared as it is immutable and thread safe.
   */
  val simpleDateMarshal: InMarshal[Any,java.time.LocalDate] = GSheetsDateMarshaller.simpleMarshal

  /** Marshal a Google Sheets Date value into a [[java.time.LocalDate]].
   *  The input value type is expected to be a [[java.math.BigDecimal]] or `null`
   *  or an empty string indicating `null`.
   *  This singleton may be shared as it is immutable and thread safe.
   */
  val nullableDateMarshal: InMarshal[Any,java.time.LocalDate] = GSheetsDateMarshaller.nullableMarshal
}
