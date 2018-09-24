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

/** Accumulates generated objects coming from each data row of a spreadsheet.
 *  @tparam A Type of element that has been marshalled from a spreadsheet row.
 */
class RowDocElementAccumulator extends RowAccumulator[Element] {

  val dbf: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
  val db: DocumentBuilder = dbf.newDocumentBuilder()
  val document: Document = db.newDocument()
  //    val containerElt: document.createElement("container")
  val container: Element = document.createElement("container")

  /** Append a row to this accumulator. */
  override def add(row: Element) {
    container.appendChild(row)
  }

  def results: Element = container
}

abstract class MarshallerToElement extends LabelledSheetMarshal[Element] {

  val containerBldr: RowDocElementAccumulator = new RowDocElementAccumulator()

  class RowBuilder[C] extends RowMarshal[C] {

    val resultElt: Element = containerBldr.document.createElement("item")

    override def make(): Element = resultElt
    
    override def defBuildFunc: (RowBuilder[C],String,C) => Unit = defBuild _

    def defBuild(m: RowBuilder[C], name: String, v: C) = { 
      defBuildT(name, v)
    }
    
    def defBuildT(name: String, v: C) = { 
      
    }
    
  }
  
//  def defBuildFunc[M <: RowMarshal[C],C]: (M,String,C) => Unit = NOOP[M,C] _
    
  

  def toAttributeName(name: String): String = {
    name.trim().toLowerCase().map { case ' ' => '-' }
  }

}

/**
 * Test for marshalling a google spreadsheet's data into a mock Acct instance
 */
class AcctMockMarshallerAsElement extends MarshallerToElement  {


  private[this] def init() {
//    val intoDate = nullableDateMarshal.asInstanceOf[InMarshal[Any,Any]]
//    val intoString = simpleStringMarshal
//    val intoCash = nullableCurrencyMarshal
//    val intoInt = intMarshal
//    val intoCycle = CycleMarshaller.Into
//
//    def toAcct(m: RowBuilder, name: String, str: String) = m.rb = m.rb.acctName(str)
//
//    // Columns    named ... marshalled ...  then bound into builder instance...
//    marshalChain("Acct",   intoString, (m: RowBuilder, name: String, str: String) => m.rb = m.rb.acctName(str))
//    marshalChain("Cycle",  intoCycle,  (m: RowBuilder, name: String, v: Cycle) => m.rb = m.rb.cycle(v))
//    marshalChain("Date",   intoDate,   (m: RowBuilder, name: String, date: LocalDate) => m.rb = m.rb.date(date))
//    marshalChain("Age",    intoInt,    (m: RowBuilder, name: String, i: Int) => m.rb = m.rb.age(i))
//    marshalChain("Income", intoCash,   (m: RowBuilder, name: String, currency: BigDecimal) => m.rb = m.rb.income(currency))
  }

  init()

  override def makeRowMarshaller[Any]() = new RowBuilder
}
