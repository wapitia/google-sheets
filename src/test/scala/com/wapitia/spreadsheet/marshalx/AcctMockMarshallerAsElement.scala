package com.wapitia.spreadsheet.marshalx

import java.time.LocalDate

import com.wapitia.common.marshal.InMarshal
import com.wapitia.spreadsheet.marshal.{simpleStringMarshal, nullableCurrencyMarshal, intMarshal}
import com.wapitia.gsheets.marshal.nullableDateMarshal
import com.wapitia.calendar.{Cycle, CycleMarshaller}
import com.wapitia.spreadsheet.marshal.LabelledSheetMarshal
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import com.wapitia.spreadsheet.marshal.RowAccumulator
import org.w3c.dom.Document
import java.lang.reflect.Method
import java.lang.annotation.Annotation
import java.lang.reflect.Constructor

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

abstract class MarshallerToElement(containerBldr: RowDocElementAccumulator) extends LabelledSheetMarshal[Element] {

//  val containerBldr: RowDocElementAccumulator = new RowDocElementAccumulator()

  class RowBuilder[C] extends RowMarshal[C] {

    val resultElt: Element = containerBldr.document.createElement("item")

    override def make(): Element = {
      resultElt
    }

    def defBuildT[C](name: String, value: C) = {
      val attrName = toAttributeName(name)
      resultElt.setAttribute(attrName, value.toString)
    }

  }

//  override def defBuildFunc[C,M <: RowMarshal[C]]: (M,String,C) => Unit = defBuild[C,RowMarshal[C]] _

  override def defBuild[C,M <: RowMarshal[C]](m: M, name: String, v: C) = {
    m.asInstanceOf[RowBuilder[C]].defBuildT[C](name, v)
  }

  def toAttributeName(name: String): String = {
    name.trim().toLowerCase().map {
      case ' ' => '-'
      case ch => ch
    }
  }

}

/**
 * Test for marshalling a google spreadsheet's data into a mock Acct instance
 */
class AcctMockMarshallerAsElement(containerBldr: RowDocElementAccumulator) extends MarshallerToElement(containerBldr) {

  private[this] def init() {
    marshalChain("Date",   nullableDateMarshal,   (m: RowBuilder[LocalDate], name: String, date: LocalDate) => m.defBuildT(name, date))
  }

  init()

  override def makeRowMarshaller[Any]() = new RowBuilder
}
