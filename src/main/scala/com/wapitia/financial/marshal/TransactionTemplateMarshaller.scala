package com.wapitia
package financial
package marshal

import java.time.LocalDate

import com.wapitia.calendar.Cycle
import com.wapitia.calendar.CycleMarshaller
import com.wapitia.financial.TransactionTemplate
import com.wapitia.spreadsheet.marshal.{intMarshal => intoInt}
import com.wapitia.spreadsheet.marshal.{boolMarshal => intoBool}
import com.wapitia.spreadsheet.marshal.{simpleStringMarshal => intoString}
import com.wapitia.spreadsheet.marshal.{nullableCurrencyMarshal => intoCurrency}
import com.wapitia.gsheets.marshal.{nullableDateMarshal => intoDate}
import com.wapitia.calendar.CycleMarshaller.{Into => intoCycle}
import com.wapitia.spreadsheet.marshal.{RowMarshal, MarshalSetRepo, ConfiguredRowMarshal, ConfiguredRowBuilder, ConfiguredSheetMarshal}

/** Tool for translating a spreadsheet containing rows of financial transaction template data
 *  into an ordered list of `TransactionTemplate`s.
 */
class TransactionTemplateMarshaller extends ConfiguredSheetMarshal[TransactionTemplate]  {

  import com.wapitia.spreadsheet.marshal.ConfiguredSheetMarshal._

  class RowBuilder(mcRepo: MarshalSetRepo[TransactionTemplate,RowBuilder])
  extends ConfiguredRowBuilder[TransactionTemplate,RowBuilder,TransactionTemplate.Builder](mcRepo, TransactionTemplate.builder(), (a: String) => a)

  type RM = RowBuilder

  private[this] def init() {

    marshalChain("Item", intoString, (m: RM, name: String, str: String) => m.rb = m.rb.item(str) )
    marshalChain("Next Transaction", intoDate, (m: RM, name: String, date: LocalDate) => m.rb = m.rb.nextTrans(date) )
    marshalChain("Amount", intoCurrency, (m: RM, name: String, currency: BigDecimal) => m.rb = m.rb.amount(currency) )
    marshalChain("Cycle", CycleMarshaller.Into, (m: RM, name: String, v: Cycle) => m.rb = m.rb.cycleKind(v) )
    marshalChain("CycleRefDate", intoDate, (m: RM, name: String, date: LocalDate) => m.rb = m.rb.cycleRefDate(date) )
    marshalChain("Max", intoCurrency, (m: RM, name: String, currency: BigDecimal) => m.rb = m.rb.max(currency) )
    marshalChain("Last Pmt Date", intoDate, (m: RM, name: String, date: LocalDate) => m.rb = m.rb.lastPmtDate(date) )
    marshalChain("Variable",  intoBool, (m: RM, name: String, bool: Boolean) => m.rb = m.rb.variable(bool) )
    marshalChain("Source", intoString, (m: RM, name: String, str: String) => m.rb = m.rb.source(Account(str)) )
    marshalChain("Target", intoString, (m: RM, name: String, str: String) => m.rb = m.rb.target(Account(str)) )
    marshalChain("Pmt Method", intoString, (m: RM, name: String, str: String) => m.rb = m.rb.pmtMethod(str) )
    marshalChain("cat-ndays", intoInt, (m: RM, name: String, i: Int) => m.rb = m.rb.catNDays(i) )
    marshalChain("cat-nmonths", intoInt, (m: RM, name: String, i: Int) => m.rb = m.rb.catNMonths(i) )
  }

  init()

  override def makeRowMarshaller() = new RM(marshalChainRepo)
}
