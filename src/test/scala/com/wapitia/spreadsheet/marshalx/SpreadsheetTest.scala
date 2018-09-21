package com.wapitia.spreadsheet.marshalx

import com.wapitia.spreadsheet.marshal.SimpleSheetReader.seqRead
import com.wapitia.spreadsheet.marshal.SheetRow

import org.junit.Assert._
import org.junit.Test

class SpreadsheetTest {

  val sampleSheet: List[SheetRow] = List(
    List(),
    List("Acct", "Cycle", "Date", "Age", "Income"),
    List("Acct-1", "Monthly", BigDecimal(38957), 32, BigDecimal(35000.0D)),
    List("Acct-2", "BiWeekly", new java.math.BigDecimal(38997), 14.asInstanceOf[Integer], BigDecimal(14500.0D))
  )

  val expectedSheet: List[String] = List(
    "AcctMock(Acct-1,Monthly,2006-08-28,32,Some(35000.0))",
    "AcctMock(Acct-2,BiWeekly,2006-10-07,14,Some(14500.0))"
  )

  @Test
  def testGoogleSheets() {
    val rows: Seq[AcctMock] = seqRead[AcctMock](sampleSheet, new AcctMockMarshaller)
    assertEquals(expectedSheet, rows.map(_.toString()))
  }

}
