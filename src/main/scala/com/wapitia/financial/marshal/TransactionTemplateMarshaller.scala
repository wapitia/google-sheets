package com.wapitia
package financial
package marshal

import java.time.LocalDate

import com.wapitia.calendar.Cycle
import com.wapitia.calendar.CycleMarshaller
import com.wapitia.financial.TransactionTemplate
import com.wapitia.common.marshal.InMarshal
import com.wapitia.spreadsheet.marshal.{intMarshal,boolMarshal,simpleStringMarshal,nullableCurrencyMarshal,LabelledSheetMarshal}
import com.wapitia.gsheets.marshal.nullableDateMarshal
import com.wapitia.spreadsheet.marshal.RowMarshal

/**
 * Tool for translating a spreadsheet containing rows of financial transaction template data
 * into an ordered list of `TransactionTemplate`s.
 */
class TransactionTemplateMarshaller extends LabelledSheetMarshal[TransactionTemplate]  {

  class RowBuilder(parent: TransactionTemplateMarshaller) extends RowMarshal[TransactionTemplate](parent) {
    var rb: TransactionTemplate.Builder = TransactionTemplate.builder()
    override def make(): TransactionTemplate = rb.build()
  }

  private[this] def init() {
    val intoDate = nullableDateMarshal.asInstanceOf[InMarshal[Any,LocalDate]]
    val intoString = simpleStringMarshal
    val intoCurrency = nullableCurrencyMarshal
    val intoBool = boolMarshal
    val intoInt = intMarshal
    val intoCycle = CycleMarshaller.Into

    val marshalItem = (m: RowBuilder, name: String, str: String) =>
      m.rb = m.rb.item(str)

    val marshalNextTrans = (m: RowBuilder, name: String, date: LocalDate) =>
      m.rb = m.rb.nextTrans(date)

//    marshalChain("Item",             intoString, (m: RowBuilder, name: String, str: String) => m.rb = m.rb.item(str) )
    marshalChain("Item", intoString, (m: RowBuilder, name: String, str: String) =>
      m.rb = m.rb.item(str) )
    marshalChain("Next Transaction", intoDate, (m: RowBuilder, name: String, date: LocalDate) =>
      m.rb = m.rb.nextTrans(date) )
    marshalChain("Amount", intoCurrency, (m: RowBuilder, name: String, currency: BigDecimal) =>
      m.rb = m.rb.amount(currency) )
    marshalChain("Cycle", intoCycle, (m: RowBuilder, name: String, v: Cycle) =>
      m.rb = m.rb.cycleKind(v) )
    marshalChain("CycleRefDate",   intoDate, (m: RowBuilder, name: String, date: LocalDate) =>
      m.rb = m.rb.cycleRefDate(date) )
    marshalChain("Max", intoCurrency, (m: RowBuilder, name: String, currency: BigDecimal) =>
      m.rb = m.rb.max(currency) )
    marshalChain("Last Pmt Date",  intoDate, (m: RowBuilder, name: String, date: LocalDate) =>
      m.rb = m.rb.lastPmtDate(date) )
    marshalChain("Variable",  intoBool, (m: RowBuilder, name: String, bool: Boolean) =>
      m.rb = m.rb.variable(bool) )
    marshalChain("Source", intoString, (m: RowBuilder, name: String, str: String) =>
      m.rb = m.rb.source(Account(str)) )
    marshalChain("Target", intoString, (m: RowBuilder, name: String, str: String) =>
      m.rb = m.rb.target(Account(str)) )
    marshalChain("Pmt Method", intoString, (m: RowBuilder, name: String, str: String) =>
      m.rb = m.rb.pmtMethod(str) )
    marshalChain("cat-ndays", intoInt, (m: RowBuilder, name: String, i: Int) =>
      m.rb = m.rb.catNDays(i) )
    marshalChain("cat-nmonths", intoInt, (m: RowBuilder, name: String, i: Int) =>
      m.rb = m.rb.catNMonths(i) )
  }

  init()

  override def makeRowMarshaller[Any]() = new RowBuilder(this)

}
