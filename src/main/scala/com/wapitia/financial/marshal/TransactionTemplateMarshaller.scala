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
import com.wapitia.spreadsheet.marshal.SetterRepo
import com.wapitia.spreadsheet.marshal.CellMarshalRepo
import com.wapitia.spreadsheet.marshal.ConfiguredRowMarshal
import com.wapitia.spreadsheet.marshal.ConfiguredRowBuilder

class RowBuilder(cellMarshalRepo: CellMarshalRepo, setterRepo: SetterRepo[TransactionTemplate,RowBuilder])
extends ConfiguredRowBuilder[TransactionTemplate,RowBuilder,TransactionTemplate.Builder](cellMarshalRepo, setterRepo, TransactionTemplate.builder _)

/** Tool for translating a spreadsheet containing rows of financial transaction template data
 *  into an ordered list of `TransactionTemplate`s.
 */
class TransactionTemplateMarshaller extends LabelledSheetMarshal[TransactionTemplate]  {

  import com.wapitia.spreadsheet.marshal.LabelledSheetMarshal._

  type RM = RowBuilder

  private[this] def init() {
    marshalChain("Item", simpleStringMarshal, (m: RM, name: String, str: String) =>
      m.rb = m.rb.item(str) )
    marshalChain("Next Transaction", nullableDateMarshal, (m: RM, name: String, date: LocalDate) =>
      m.rb = m.rb.nextTrans(date) )
    marshalChain("Amount", nullableCurrencyMarshal, (m: RM, name: String, currency: BigDecimal) =>
      m.rb = m.rb.amount(currency) )
    marshalChain("Cycle", CycleMarshaller.Into, (m: RM, name: String, v: Cycle) =>
      m.rb = m.rb.cycleKind(v) )
    marshalChain("CycleRefDate", nullableDateMarshal, (m: RM, name: String, date: LocalDate) =>
      m.rb = m.rb.cycleRefDate(date) )
    marshalChain("Max", nullableCurrencyMarshal, (m: RM, name: String, currency: BigDecimal) =>
      m.rb = m.rb.max(currency) )
    marshalChain("Last Pmt Date", nullableDateMarshal, (m: RM, name: String, date: LocalDate) =>
      m.rb = m.rb.lastPmtDate(date) )
    marshalChain("Variable",  boolMarshal, (m: RM, name: String, bool: Boolean) =>
      m.rb = m.rb.variable(bool) )
    marshalChain("Source", simpleStringMarshal, (m: RM, name: String, str: String) =>
      m.rb = m.rb.source(Account(str)) )
    marshalChain("Target", simpleStringMarshal, (m: RM, name: String, str: String) =>
      m.rb = m.rb.target(Account(str)) )
    marshalChain("Pmt Method", simpleStringMarshal, (m: RM, name: String, str: String) =>
      m.rb = m.rb.pmtMethod(str) )
    marshalChain("cat-ndays", intMarshal, (m: RM, name: String, i: Int) =>
      m.rb = m.rb.catNDays(i) )
    marshalChain("cat-nmonths", intMarshal, (m: RM, name: String, i: Int) =>
      m.rb = m.rb.catNMonths(i) )
  }

  init()

  override def makeRowMarshaller() = new RM(cellMarshalRepo, setterRepo)
}
