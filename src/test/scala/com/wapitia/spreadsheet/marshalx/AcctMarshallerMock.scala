package com.wapitia.spreadsheet.marshalx

import java.time.LocalDate

import com.wapitia.common.marshal.MarshalIn
import com.wapitia.calendar.CycleMarshaller

/**
 * Tool for translating a spreadsheet containing rows of financial transaction template data
 * into an ordered list of `TransactionTemplate`s.
 */
class AcctMarshallerMock extends LabelledSheetMarshallerX[AcctMock.Builder]  {

  // convenience alias
  type BLDR = AcctMock.Builder

  val transactionCellMarshaller = new LabelledCellMarshallersX {
    // marshallers abstracted here so that types are bound to marshallers in just one place
    val intoDate = com.wapitia.gsheets.marshal.nullableDateMarshal.asInstanceOf[MarshalIn[Any,Any]]
    val intoString = com.wapitia.spreadsheet.marshal.simpleStringMarshal
    val intoCurrency = com.wapitia.spreadsheet.marshal.nullableCurrencyMarshal
    val intoInt = com.wapitia.spreadsheet.marshal.intMarshal
    val intoCycle = CycleMarshaller.Into

    //              cells titled ...   => ... are marshalled into type...
     addCellMarshaller("Acct",   intoString)
     addCellMarshaller("Cycle",  intoCycle)
     addCellMarshaller("Date",   intoDate)
     addCellMarshaller("Age",    intoInt)
     addCellMarshaller("Income", intoCurrency)
  }

//  class TransactionRowMarshaller(transactionCellMarshaller: LabelledCellMarshallersX) extends LabelledRowMarshallerX[BLDR] {
//    val cellMarshaller: LabelledCellMarshallersX = transactionCellMarshaller
//    var rowBuilder: BLDR = TransactionTemplate.builder()
//    override def build(): BLDR = rowBuilder
//
//    //                 cells titled ...   => ... are added to the row builder in the given slot
//     addRowItemMarshaller("Item",             (str: String) => this.rowBuilder = rowBuilder.item(str) )
//     addRowItemMarshaller("Next Transaction", (date: LocalDate) => this.rowBuilder = rowBuilder.nextTrans(date) )
//     addRowItemMarshaller("Amount",           (currency: BigDecimal) => this.rowBuilder = rowBuilder.amount(currency) )
//     addRowItemMarshaller("Cycle",            (v: Cycle) => this.rowBuilder = rowBuilder.cycle(v) )
//     addRowItemMarshaller("CycleRefDate",     (date: LocalDate) => this.rowBuilder = rowBuilder.cycleRefDate(date) )
//     addRowItemMarshaller("Max",              (currency: BigDecimal) => this.rowBuilder = rowBuilder.max(currency) )
//     addRowItemMarshaller("Last Pmt Date",    (date: LocalDate) => this.rowBuilder = rowBuilder.lastPmtDate(date) )
//     addRowItemMarshaller("Variable",         (bool: Boolean) => this.rowBuilder = rowBuilder.variable(bool) )
//     addRowItemMarshaller("Source",           (str: String) => this.rowBuilder = rowBuilder.source(Account(str)) )
//     addRowItemMarshaller("Target",           (str: String) => this.rowBuilder = rowBuilder.target(Account(str)) )
//     addRowItemMarshaller("Pmt Method",       (str: String) => this.rowBuilder = rowBuilder.pmtMethod(str) )
//     addRowItemMarshaller("cat-ndays",        (i: Int) => this.rowBuilder = rowBuilder.catNDays(i) )
//     addRowItemMarshaller("cat-nmonths",      (i: Int) => this.rowBuilder = rowBuilder.catNMonths(i) )
//  }

//  override def startNewRow(): LabelledRowMarshallerX[BLDR] = {
//    new TransactionRowMarshaller(transactionCellMarshaller)
//  }
  override def startNewRow(): LabelledRowMarshallerX[BLDR] = ???

}
