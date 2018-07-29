package com.wapitia.spreadsheet.marshalx

import java.time.LocalDate

import com.wapitia.common.marshal.InMarshal
import com.wapitia.calendar.CycleMarshaller
import com.wapitia.calendar.Cycle

/**
 * Test for marshalling a google spreadsheet's data into a mock Acct instance
 */
class AcctMockMarshaller extends LabelledSheetMarshallerX[AcctMock,AcctMock.Builder]  {

  trait RowBuilder {
    var rb: AcctMock.Builder = AcctMock.builder()
  }

  val marshalKit = new MarshalKit() {
    val intoDate = com.wapitia.gsheets.marshal.nullableDateMarshal.asInstanceOf[InMarshal[Any,Any]]
    val intoString = com.wapitia.spreadsheet.marshal.simpleStringMarshal
    val intoCash = com.wapitia.spreadsheet.marshal.nullableCurrencyMarshal
    val intoInt = com.wapitia.spreadsheet.marshal.intMarshal
    val intoCycle = CycleMarshaller.Into
    marshalChain("Acct",   intoString, (m: RowBuilder, str: String) => m.rb = m.rb.acctName(str))
    marshalChain("Cycle",  intoCycle,  (m: RowBuilder, v: Cycle) => m.rb = m.rb.cycle(v))
    marshalChain("Date",   intoDate,   (m: RowBuilder, date: LocalDate) => m.rb = m.rb.date(date))
    marshalChain("Age",    intoInt,    (m: RowBuilder, i: Int) => m.rb = m.rb.age(i))
    marshalChain("Income", intoCash,   (m: RowBuilder, currency: BigDecimal) => m.rb = m.rb.income(currency))
  }

  override def startNewRow() = new LabelledRowMarshalX[AcctMock, AcctMock.Builder] with RowBuilder {
    override val cellMarshallMap: LabelledInMarshalMapX = marshalKit.marshalMap
    override val rowObjectMarshals: Map[String, (_,_) => Unit] = marshalKit.rowObjectMarshals
    override def build(): AcctMock = rb.build()
  }
}
