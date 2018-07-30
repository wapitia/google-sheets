package com.wapitia
package rb

import com.wapitia.financial.TransactionTemplate
import com.wapitia.gsheets.GoogleSheetsAccess
import com.wapitia.spreadsheet.marshal.SimpleSheetReader
import com.wapitia.financial.marshal.TransactionTemplateMarshaller


object GSheetsSandbox1 extends App {

  val sheetSID = "1qU3idNFQkVUyfpoF7WTOgbeghM_WNlHV-EoW7KY9hPU"
  val sheetRange = "Corey's Budget 2018!A:P"

  val acc = GoogleSheetsAccess.readOnlyAccess("Google Sheets API Java Quickstart")
  val values = acc.loadSheetUnformattedValues(sheetSID, sheetRange)
//  val rdr = SimpleSheetReader[TransactionTemplate.Builder](new TransactionTemplateMarshaller)
//  val rows: List[TransactionTemplate] = rdr.read(values).map(_.build())
  val rdr = SimpleSheetReader[TransactionTemplate,TransactionTemplate.Builder](new TransactionTemplateMarshaller)
  val rows: List[TransactionTemplate] = rdr.read(values)
  rows.foreach(println)

}
