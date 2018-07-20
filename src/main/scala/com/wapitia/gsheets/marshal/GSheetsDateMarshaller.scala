package com.wapitia
package gsheets.marshal

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import com.wapitia.common.marshal.MarshalIn
import com.wapitia.spreadsheet.marshal.SpreadsheetMarshalException

/** Unmarshal a ''Google Sheets'' date into a `java.time.LocalDate`.
 *  The type is expected to a `BigDecimal`, which is then considered
 *  to be a value relative to the Google Sheets Epoch of 1899-12-30
 *  If the cell value is not a `BigDecimal` a 
 *  `SpreadsheetMarshalException` is thrown.
 */
class SimpleDateMarshal extends MarshalIn[Any,LocalDate] {
  
  import GSheetsDateMarshaller._
  
  /** Unmarshal a cell value as a LocalDate.
   *  the cell may come in as any object, and so `AnyRef` is chosen as a base type.
   *  If the cell is not a `BigDecimal` a `SpreadsheetMarshalException` is thrown.
   *  
   *  @throws(classOf[SpreadsheetMarshalException])
   */
  override def unmarshal(cell: Any): LocalDate = cell match {
    case n: java.math.BigDecimal => unmarshalEpochDay(n.longValue())
    case _  => throw new SpreadsheetMarshalException("unparsable date %s:%s".format(cell, cell.getClass))
  }
    
}

/** A `SimpleDataMarshal` that also accepts an empty string as its cell value to indicate `null`.
 */
class NullableDateMarshal extends SimpleDateMarshal {

  /** true iff the cell value is an empty `String` */
  override def isNull(cell: Any): Boolean = cell match {
    case s: String if s.isEmpty() => true
    case _                        => false
  }
}

/** Constants in the service of Google Sheets dates. */
object GSheetsDateMarshaller {
  
  /** SimpleDateMarshal singleton may be shared as it is immutable and thread safe. */
  val simpleMarshal: MarshalIn[Any,LocalDate] = new SimpleDateMarshal  
  
  /** NullableDateMarshal singleton may be shared as it is immutable and thread safe. */
  val nullableMarshal: MarshalIn[Any,LocalDate] = new NullableDateMarshal
  
  /** The date by which the Google Sheets data and time BigDecimals are relative.
   *  This according to e.g. https://en.wikipedia.org/wiki/Epoch_(reference_date)#1899-12-30 
   */
  val GoogleSheetsEpoch = LocalDate.of(1899, 12, 30)
  
  /** The number of days between Google sheets and java time epochs.
   *  Works out to be 25569 days.
   */
	val JavaVsGoogleDay0Diff: Long = 
	  ChronoUnit.DAYS.between(GoogleSheetsEpoch, com.wapitia.calendar.Epoch)
	
  /** Convert into a `LocalDate` the value of a Google Sheets date, 
   *  an integer number of days relative to the `GoogleSheetsEpoch`.
	 */
  def unmarshalEpochDay(googleSheetsDay: Long): java.time.LocalDate = {
      val epochday: Long = googleSheetsDay - JavaVsGoogleDay0Diff
      val res = java.time.LocalDate.ofEpochDay(epochday)
      res
  }
  
}