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
import javax.xml.transform.TransformerFactory
import javax.xml.transform.TransformerException
import java.io.IOException
import java.io.OutputStream
import javax.xml.transform.Transformer
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.OutputKeys
import java.io.OutputStreamWriter
import java.io.StringWriter
import java.io.Writer
import org.w3c.dom.ls.DOMImplementationLS
import org.w3c.dom.ls.LSOutput
import org.w3c.dom.ls.LSSerializerFilter
import org.w3c.dom.traversal.NodeFilter
import org.w3c.dom.DOMConfiguration

class SpreadsheetXmlTest {

  import com.wapitia.calendar._
  import SpreadsheetXmlTest._

  val xmlExpResult =
  """<container>
  |    <item acct="Acct-1" age="32" cycle="Monthly" date="2006-08-28" income="35000.0"/>
  |    <item acct="Acct-2" age="14" cycle="BiWeekly" date="2006-10-07" income="14500.0"/>
  |</container>
  |""".stripMargin

  @Test
  def testGoogleSheetsAsXml() {

    val samp = AcctMockSample

    val accum: RowDocElementAccumulator = new RowDocElementAccumulator()
    val rowBuilder = new AcctMockMarshallerAsElement(accum)
    val rdr = SimpleSheetReader[Element](rowBuilder)
    rdr.read(samp.sampleSheet, accum)
    val items: Element = accum.results
    val sw = new StringWriter()
    writeDoc(items, sw)
    val res = sw.toString()
    val exp = xmlExpResult.toString()
//    println(s"res:{$res}, size: ${res.length()}")
//    println(s"exp:{$exp}, size: ${exp.length()}")
    assertEquals(exp, res)
  }

}

object SpreadsheetXmlTest {

  def writeDoc(node: Node, out: Writer) {
    val document = node.getOwnerDocument
    val domls = document.getImplementation.asInstanceOf[DOMImplementationLS]
    val serializer = domls.createLSSerializer()
    val domConfig: DOMConfiguration = serializer.getDomConfig
    domConfig.setParameter("xml-declaration", false)
    domConfig.setParameter("format-pretty-print", true)
    val outp = domls.createLSOutput()
    outp.setCharacterStream(out)
    val result = serializer.write(node, outp)
  }

}
