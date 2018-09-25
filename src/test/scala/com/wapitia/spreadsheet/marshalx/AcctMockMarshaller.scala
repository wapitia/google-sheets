package com.wapitia.spreadsheet.marshalx

import java.time.LocalDate

import com.wapitia.common.marshal.InMarshal
import com.wapitia.spreadsheet.marshal.{simpleStringMarshal, nullableCurrencyMarshal, intMarshal}
import com.wapitia.gsheets.marshal.nullableDateMarshal
import com.wapitia.calendar.{Cycle, CycleMarshaller}
import com.wapitia.spreadsheet.marshal.LabelledSheetMarshal
import com.wapitia.spreadsheet.marshal.RowMarshal

/**
 * Test for marshalling a google spreadsheet's data into a mock Acct instance
 */
class AcctMockMarshaller extends LabelledSheetMarshal[AcctMock]  {

  class RowBuilder(parent: AcctMockMarshaller) extends RowMarshal[AcctMock](parent) {
    var rb: AcctMock.Builder = AcctMock.builder()
    override def make(): AcctMock = rb.build()
  }

  private[this] def init() {
    // Columns    named ... marshalled ...  then bound into builder instance...
    marshalChain("Acct",   simpleStringMarshal, (m: RowBuilder, name: String, str: String) => m.rb = m.rb.acctName(str))
    marshalChain("Cycle",  CycleMarshaller.Into,  (m: RowBuilder, name: String, v: Cycle) => m.rb = m.rb.cycle(v))
    marshalChain("Date",   nullableDateMarshal,   (m: RowBuilder, name: String, date: LocalDate) => m.rb = m.rb.date(date))
    marshalChain("Age",    intMarshal,    (m: RowBuilder, name: String, i: Int) => m.rb = m.rb.age(i))
    marshalChain("Income", nullableCurrencyMarshal,   (m: RowBuilder, name: String, currency: BigDecimal) => m.rb = m.rb.income(currency))
  }

  init()

  override def makeRowMarshaller[Any]() = new RowBuilder(this)
}
