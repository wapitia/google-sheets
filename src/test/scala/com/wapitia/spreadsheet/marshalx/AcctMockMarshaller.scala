package com.wapitia.spreadsheet.marshalx

import java.time.LocalDate

import com.wapitia.calendar.{Cycle, CycleMarshaller}
import com.wapitia.spreadsheet.marshal.{ConfiguredRowBuilder, CellMarshalRepo, SetterRepo, LabelledSheetMarshal}
import com.wapitia.spreadsheet.marshal.{intMarshal, nullableCurrencyMarshal, simpleStringMarshal}
import com.wapitia.gsheets.marshal.nullableDateMarshal

class AcctMockRowBuilder(cellMarshalRepo: CellMarshalRepo, setterRepo: SetterRepo[AcctMock,AcctMockRowBuilder])
extends ConfiguredRowBuilder[AcctMock,AcctMockRowBuilder,AcctMock.Builder](cellMarshalRepo, setterRepo, AcctMock.builder _)

/**
 * Test for marshalling a google spreadsheet's data into a mock Acct instance
 */
class AcctMockMarshaller extends LabelledSheetMarshal[AcctMock]  {

  import com.wapitia.spreadsheet.marshal.LabelledSheetMarshal._

  type RM = AcctMockRowBuilder

  private[this] def init() {
    // Columns    named ... marshalled ...  then bound into builder instance...
    marshalChain("Acct",   simpleStringMarshal, (m: RM, name: String, str: String) => m.rb = m.rb.acctName(str))
    marshalChain("Cycle",  CycleMarshaller.Into,  (m: RM, name: String, v: Cycle) => m.rb = m.rb.cycle(v))
    marshalChain("Date",   nullableDateMarshal,   (m: RM, name: String, date: LocalDate) => m.rb = m.rb.date(date))
    marshalChain("Age",    intMarshal,    (m: RM, name: String, i: Int) => m.rb = m.rb.age(i))
    marshalChain("Income", nullableCurrencyMarshal,   (m: RM, name: String, currency: BigDecimal) => m.rb = m.rb.income(currency))
  }

  init()

  override def makeRowMarshaller() = new RM(cellMarshalRepo, setterRepo)
}
