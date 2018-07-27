package com.wapitia.spreadsheet.marshalx

object TestSpreadsheetX extends App {

  val sampleSheet = List(
     List(),
     List("Acct", "Cycle", "Date", "Age", "Income"),
     List("Acct-1", "Monthly", BigDecimal(38957), 32, BigDecimal(35000.0D)),
     List("Acct-2", "BiWeekly", BigDecimal(38997), 14, BigDecimal(14500.0D))
  )

  def testGoogleSheets() {
//  	val rdr = SimpleSheetReaderX[TransactionTemplate.Builder](new TransactionTemplateMarshaller)
//  	val rows: List[TransactionTemplate] = rdr.read(sampleSheet).map(_.build())
//  	rows.foreach(println)
  }
  
  testGoogleSheets()
  
}