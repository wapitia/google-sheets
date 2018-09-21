package com.wapitia
package rb

import com.wapitia.financial.TransactionTemplate
import com.wapitia.financial.marshal.TransactionTemplateMarshaller
import com.wapitia.gsheets.GoogleSheetsAccess
import com.wapitia.spreadsheet.marshal.SimpleSheetReader.seqRead
import com.wapitia.spreadsheet.marshal.SheetRow

object GSheetsSandbox1 extends App {

  val sheetSID = "1qU3idNFQkVUyfpoF7WTOgbeghM_WNlHV-EoW7KY9hPU"
  val sheetRange = "Corey's Budget 2018!A:P"

  val acc = GoogleSheetsAccess.readOnlyAccess("Google Sheets API Java Quickstart")
  val values: List[SheetRow] = acc.loadSheetUnformattedValues(sheetSID, sheetRange)
  val rows = seqRead[TransactionTemplate](values, new TransactionTemplateMarshaller)
  rows.foreach(println)
}
