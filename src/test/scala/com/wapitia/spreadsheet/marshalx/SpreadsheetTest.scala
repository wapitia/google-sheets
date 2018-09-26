package com.wapitia.spreadsheet.marshalx

import com.wapitia.spreadsheet.marshal.SimpleSheetReader.seqRead
import com.wapitia.spreadsheet.marshal.SheetRow

import org.junit.Assert._
import org.junit.Test
import com.wapitia.gsheets.marshal.GSheetsDateMarshaller.toGoogleEpochDay

class SpreadsheetTest {

  import com.wapitia.calendar._

  val samp = AcctMockSample

  @Test
  def testGoogleSheets() {
    val rows: Seq[AcctMock] = seqRead[AcctMock](samp.sampleSheet, new AcctMockMarshaller)
    assertEquals(samp.expectedSheet, rows.map(_.toString()))
  }


}
