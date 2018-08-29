package com.wapitia.spreadsheet.marshalx

import java.time.LocalDate

import com.wapitia.common.marshal.InMarshal
import com.wapitia.spreadsheet.marshal.{simpleStringMarshal, nullableCurrencyMarshal, intMarshal}
import com.wapitia.gsheets.marshal.nullableDateMarshal
import com.wapitia.calendar.{CycleKind, CycleKindMarshaller}
import com.wapitia.spreadsheet.marshal.LabelledSheetMarshal

/**
 * Test for marshalling a google spreadsheet's data into a mock Acct instance
 */
class AcctMockMarshaller extends LabelledSheetMarshal[AcctMock,AcctMock.Builder]  {

  class RowBuilder extends RowMarshal {
    var rb: AcctMock.Builder = AcctMock.builder()
    override def make(): AcctMock = rb.build()
  }

  private[this] def init() {
    val intoDate = nullableDateMarshal.asInstanceOf[InMarshal[Any,Any]]
    val intoString = simpleStringMarshal
    val intoCash = nullableCurrencyMarshal
    val intoInt = intMarshal
    val intoCycle = CycleKindMarshaller.Into

    // Columns    named ... marshalled ...  then bound into builder instance...
    marshalChain("Acct",   intoString, (m: RowBuilder, str: String) => m.rb = m.rb.acctName(str))
    marshalChain("Cycle",  intoCycle,  (m: RowBuilder, v: CycleKind) => m.rb = m.rb.cycle(v))
    marshalChain("Date",   intoDate,   (m: RowBuilder, date: LocalDate) => m.rb = m.rb.date(date))
    marshalChain("Age",    intoInt,    (m: RowBuilder, i: Int) => m.rb = m.rb.age(i))
    marshalChain("Income", intoCash,   (m: RowBuilder, currency: BigDecimal) => m.rb = m.rb.income(currency))
  }

  init()

  override def makeRow() = new RowBuilder
}
