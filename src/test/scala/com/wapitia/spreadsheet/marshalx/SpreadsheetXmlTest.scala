package com.wapitia.spreadsheet.marshalx

import java.io.StringWriter
import org.junit.Assert._
import org.junit.Test

import org.w3c.dom.Element
import com.wapitia.spreadsheet.marshal.{RowDocElementAccumulator, SimpleSheetReader}
import com.wapitia.xml.XMLPrettyPrint

class SpreadsheetXmlTest {

  import com.wapitia.calendar._

  val xmlExpResult =
  """<container>
  |    <item acct="Acct-1" age="32" cycle="Monthly" date="2006-08-28">
  |        <income>35000.0</income>
  |    </item>
  |    <item acct="Acct-2" age="14" cycle="BiWeekly" date="2006-10-07">
  |        <income>14500.0</income>
  |    </item>
  |</container>
  |""".stripMargin

  @Test
  def testGoogleSheetsAsXml() {

    val samp = AcctMockSample

    val accum: RowDocElementAccumulator = new RowDocElementAccumulator("container","item")
    val sheetMarshaller = new AcctMockElementMarshaller(accum)
    val rdr = SimpleSheetReader[Element](sheetMarshaller)
    rdr.read(samp.sampleSheet, accum)
    val items: Element = accum.results
    val sw = new StringWriter()
    XMLPrettyPrint.write(items, sw)
    val res = sw.toString()
    val exp = xmlExpResult.toString()
//    println(s"res:{$res}, size: ${res.length()}")
//    println(s"exp:{$exp}, size: ${exp.length()}")
    assertEquals(exp, res)
  }

}
