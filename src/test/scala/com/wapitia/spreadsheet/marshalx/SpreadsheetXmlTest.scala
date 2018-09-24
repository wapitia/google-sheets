package com.wapitia.spreadsheet.marshalx

import org.junit.Assert._
import org.junit.Test

import com.wapitia.spreadsheet.marshal.SimpleSheetReader
import com.wapitia.spreadsheet.marshal.SimpleSheetReader.seqRead
import com.wapitia.spreadsheet.marshal.SheetRow
import com.wapitia.spreadsheet.marshal.SeqRowAccumulator
import com.wapitia.spreadsheet.marshal.makeSeqRowAccumulator

import com.wapitia.gsheets.marshal.GSheetsDateMarshaller.toGoogleEpochDay
import org.junit.Ignore
import com.wapitia.spreadsheet.marshal.RowAccumulator
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.Document
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

class SpreadsheetXmlTest {

  import com.wapitia.calendar._

  @Test @Ignore
  def testGoogleSheetsAsXml() {

    val samp = AcctMockSample

    val rowBuilder = new AcctMockMarshallerAsElement
    val rdr = SimpleSheetReader[Element](rowBuilder)
    val accum: RowDocElementAccumulator = new RowDocElementAccumulator()
    rdr.read(samp.sampleSheet, accum)
    val items: Element = accum.results
    println(items.toString())
//    assertEquals(samp.expectedSheet, rows.map(_.toString()))
  }

}
