package com.wapitia.spreadsheet.marshalx

import com.wapitia.spreadsheet.marshal.SimpleSheetReader

object TestSpreadsheetX extends App {

  val sampleSheet: List[List[Any]] = List(
    List(),
    List("Acct", "Cycle", "Date", "Age", "Income"),
    List("Acct-1", "Monthly", BigDecimal(38957), 32, BigDecimal(35000.0D)),
    List("Acct-2", "BiWeekly", new java.math.BigDecimal(38997), 14.asInstanceOf[Integer], BigDecimal(14500.0D))
  )

  def testGoogleSheets() {
    val rdr = SimpleSheetReader[AcctMock,AcctMock.Builder](new AcctMockMarshaller)
    val rows: Seq[AcctMock] = rdr.read(sampleSheet) // .map(_.build())
    rows.foreach(println)
  }

  testGoogleSheets()
}
