package com.wapitia.spreadsheet.marshalx

import java.time.LocalDate

import com.wapitia.calendar.{Cycle, CycleMarshaller}
import com.wapitia.spreadsheet.marshal.{ConfiguredRowBuilder, CellMarshalRepo, SetFuncRepo, ConfiguredSheetMarshal}
import com.wapitia.spreadsheet.marshal.{intMarshal => intoInt}
import com.wapitia.spreadsheet.marshal.{nullableCurrencyMarshal => intoCurrency}
import com.wapitia.spreadsheet.marshal.{simpleStringMarshal => intoString}
import com.wapitia.calendar.CycleMarshaller.{Into => intoCycle}
import com.wapitia.gsheets.marshal.{nullableDateMarshal => intoDate}
import com.wapitia.spreadsheet.marshal.MarshalSetRepo

class AcctMockRowBuilder(mcRepo: MarshalSetRepo[AcctMock,AcctMockRowBuilder])
extends ConfiguredRowBuilder[AcctMock,AcctMockRowBuilder,AcctMock.Builder](mcRepo, AcctMock.builder())

/**
 * Test for marshalling a google spreadsheet's data into a mock Acct instance
 */
class AcctMockMarshaller extends ConfiguredSheetMarshal[AcctMock]  {

  import com.wapitia.spreadsheet.marshal.ConfiguredSheetMarshal._

  type RM = AcctMockRowBuilder

  private[this] def init() {
    // Columns    named ... marshalled ...  then bound into builder instance...
    marshalChain("Acct", intoString,
      (m: RM, name: String, str: String) => m.rb = m.rb.acctName(str))
    marshalChain("Cycle", intoCycle,
      (m: RM, name: String, v: Cycle) => m.rb = m.rb.cycle(v))
    marshalChain("Date", intoDate,
      (m: RM, name: String, date: LocalDate) => m.rb = m.rb.date(date))
    marshalChain("Age", intoInt,
      (m: RM, name: String, a: Int) => m.rb = m.rb.age(a))
    marshalChain("Income", intoCurrency,
      (m: RM, name: String, currency: BigDecimal) => m.rb = m.rb.income(currency))
  }

  init()

  override def makeRowMarshaller() = new RM(marshalChainRepo)
}
