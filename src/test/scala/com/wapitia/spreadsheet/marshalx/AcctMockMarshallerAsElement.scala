package com.wapitia.spreadsheet.marshalx

import java.time.LocalDate
import org.w3c.dom.{Document, Element}
import javax.xml.parsers.{DocumentBuilderFactory, DocumentBuilder}

import com.wapitia.spreadsheet.marshal.{RowMarshal, RowAccumulator, LabelledSheetMarshal}
import com.wapitia.gsheets.marshal.nullableDateMarshal

/** Accumulates generated objects coming from each data row of a spreadsheet.
 *  @tparam A Type of element that has been marshalled from a spreadsheet row.
 */
class RowDocElementAccumulator extends RowAccumulator[Element] {

  val dbf: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
  val db: DocumentBuilder = dbf.newDocumentBuilder()
  val document: Document = db.newDocument()
  val container: Element = document.createElement("container")

  /** Append a row to this accumulator. */
  override def add(row: Element) {
    container.appendChild(row)
  }

  def results: Element = container
}

class ERowBldr(parent: MarshallerToElement, containerBldr: RowDocElementAccumulator) extends RowMarshal[Element](parent) {

  val resultElt: Element = containerBldr.document.createElement("item")

  override def make(): Element = {
    resultElt
  }

  def setItem[C](name: String, value: C) {
    val attrName = toAttributeName(name)
    resultElt.setAttribute(attrName, value.toString)
  }

  def toAttributeName(name: String): String = {
    name.trim().toLowerCase().map {
      case ' ' => '-'
      case ch => ch
    }
  }
}

abstract class MarshallerToElement(containerBldr: RowDocElementAccumulator) extends LabelledSheetMarshal[Element] {

  override def defBuild[C,ERowBldr](erb: ERowBldr, name: String, v: C) {
    erb.setItem[C](name, v)
  }
}

/**
 * Test for marshalling a google spreadsheet's data into a mock Acct instance
 */
class AcctMockMarshallerAsElement(containerBldr: RowDocElementAccumulator) extends MarshallerToElement(containerBldr) {

  private[this] def init() {
    marshalChain("Date",   nullableDateMarshal, (m: ERowBldr, name: String, date: LocalDate) => m.setItem(name, date))
  }

  init()

  override def makeRowMarshaller[Any]() = new ERowBldr(this, containerBldr)
}
