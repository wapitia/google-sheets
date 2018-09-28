package com.wapitia.spreadsheet.marshalx

import java.time.LocalDate

import com.wapitia.spreadsheet.marshal.CellMarshal

import com.wapitia.calendar.{Cycle, CycleMarshaller}
import com.wapitia.spreadsheet.marshal.{ConfiguredRowBuilder, CellMarshalRepo, SetFuncRepo, MarshalSetRepo, ConfiguredSheetMarshal}
import com.wapitia.spreadsheet.marshal.{intMarshal => intoInt}
import com.wapitia.spreadsheet.marshal.{nullableCurrencyMarshal => intoCurrency}
import com.wapitia.spreadsheet.marshal.{simpleStringMarshal => intoString}
import com.wapitia.calendar.CycleMarshaller.{Into => intoCycle}
import com.wapitia.gsheets.marshal.{nullableDateMarshal => intoDate}

/**
 * Test for marshalling a google spreadsheet's data into a mock Acct instance
 */
class AcctMockMarshaller extends ConfiguredSheetMarshal[AcctMock]  {

  import com.wapitia.spreadsheet.marshal.ConfiguredSheetMarshal._

  class RowBuilder(mcRepo: MarshalSetRepo[AcctMock,RowBuilder])
  extends ConfiguredRowBuilder[AcctMock,RowBuilder,AcctMock.Builder](mcRepo, AcctMock.builder(), (a: String) => a)

  type RM = RowBuilder

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
      (m: RM, name: String, income: BigDecimal) => m.rb = m.rb.addIncome(income))
  }

  init()

  override def makeRowMarshaller() = new RM(marshalChainRepo)
}
