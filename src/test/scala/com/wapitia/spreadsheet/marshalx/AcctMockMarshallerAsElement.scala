package com.wapitia.spreadsheet.marshalx

import java.time.LocalDate
import org.w3c.dom.{Document, Element}
import javax.xml.parsers.{DocumentBuilderFactory, DocumentBuilder}

import com.wapitia.spreadsheet.marshal.{RowMarshal, RowAccumulator, CellMarshalRepo, ConfiguredSheetMarshal}
import com.wapitia.gsheets.marshal.nullableDateMarshal

/** Accumulates generated objects coming from each data row of a spreadsheet.
 *  @tparam A Type of element that has been marshalled from a spreadsheet row.
 */
class RowDocElementAccumulator(containerEltName: String, itemEltName: String) extends RowAccumulator[Element] {

  val dbf: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
  val db: DocumentBuilder = dbf.newDocumentBuilder()
  val document: Document = db.newDocument()
  val container: Element = document.createElement(containerEltName)

  val itemName = itemEltName

  /** Append a row to this accumulator. */
  override def add(row: Element) {
    container.appendChild(row)
  }

  def newResultElement() = document.createElement(itemEltName)

  def results: Element = container
}

class ERowBldr(cellMarshalRepo: CellMarshalRepo, containerBldr: RowDocElementAccumulator) extends RowMarshal[Element] {

  import com.wapitia.spreadsheet.marshal.RowMarshal._

  val resultElt: Element = containerBldr.newResultElement()

  override def make(): Element = {
    resultElt
  }

  override def cellMarshal[C](name: String): CellMarshal[C] = cellMarshalRepo.getCellMarshal[C](name)

  override def setMarshalled[C](name: String, value: C) {
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

abstract class MarshallerToElement(containerBldr: RowDocElementAccumulator) extends ConfiguredSheetMarshal[Element] {

}

/**  Test for marshalling a google spreadsheet's data into a mock Acct instance
 */
class AcctMockMarshallerAsElement(containerBldr: RowDocElementAccumulator) extends MarshallerToElement(containerBldr) {

  type RM = ERowBldr

  private[this] def init() {
    marshalChain("Date", nullableDateMarshal,
        (m: RM, name: String, date: LocalDate) => m.setMarshalled(name, date))
  }

  init()

  override def makeRowMarshaller() = new ERowBldr(marshalChainRepo.repoCellMarshal, containerBldr)
}
