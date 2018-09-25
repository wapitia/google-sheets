package com.wapitia.spreadsheet.marshalx

import java.time.LocalDate

import com.wapitia.common.marshal.InMarshal
import com.wapitia.spreadsheet.marshal.{simpleStringMarshal, nullableCurrencyMarshal, intMarshal}
import com.wapitia.gsheets.marshal.nullableDateMarshal
import com.wapitia.calendar.{Cycle, CycleMarshaller}
import com.wapitia.spreadsheet.marshal.LabelledSheetMarshal

/**
 * Test for marshalling a google spreadsheet's data into a mock Acct instance
 */
class AcctMockMarshaller extends LabelledSheetMarshal[AcctMock]  {

  class RowBuilder extends RowMarshal {
    var rb: AcctMock.Builder = AcctMock.builder()
    override def make(): AcctMock = rb.build()
  }

  private[this] def init() {
    val intoDate = nullableDateMarshal.asInstanceOf[InMarshal[Any,LocalDate]]
    val intoString = simpleStringMarshal
    val intoCash = nullableCurrencyMarshal
    val intoInt = intMarshal
    val intoCycle = CycleMarshaller.Into

    // Columns    named ... marshalled ...  then bound into builder instance...
    marshalChain("Acct",   intoString, (m: RowBuilder, name: String, str: String) => m.rb = m.rb.acctName(str))
    marshalChain("Cycle",  intoCycle,  (m: RowBuilder, name: String, v: Cycle) => m.rb = m.rb.cycle(v))
    marshalChain("Date",   intoDate,   (m: RowBuilder, name: String, date: LocalDate) => m.rb = m.rb.date(date))
    marshalChain("Age",    intoInt,    (m: RowBuilder, name: String, i: Int) => m.rb = m.rb.age(i))
    marshalChain("Income", intoCash,   (m: RowBuilder, name: String, currency: BigDecimal) => m.rb = m.rb.income(currency))
  }

  init()

  override def makeRowMarshaller[Any]() = new RowBuilder
}
