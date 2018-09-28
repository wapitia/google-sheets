package com.wapitia
package spreadsheet
package marshal

import org.w3c.dom.{Document, Element}
import javax.xml.parsers.{DocumentBuilderFactory, DocumentBuilder}

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
