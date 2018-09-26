package com.wapitia.spreadsheet.marshalx

import com.wapitia.spreadsheet.marshal.SimpleSheetReader
import com.wapitia.spreadsheet.marshal.SimpleSheetReader.seqRead
import com.wapitia.spreadsheet.marshal.SheetRow
import com.wapitia.spreadsheet.marshal.SeqRowAccumulator
import com.wapitia.spreadsheet.marshal.makeSeqRowAccumulator

import com.wapitia.gsheets.marshal.GSheetsDateMarshaller.toGoogleEpochDay

object AcctMockSample {

  import com.wapitia.calendar._

  // 38957
  val gepoch20060828 = BigDecimal(toGoogleEpochDay("2006-08-28"))
  // 38997
  val gepoch20061007 = new java.math.BigDecimal(toGoogleEpochDay("2006-10-07"))

  val sampleSheet: List[SheetRow] = List(
    List(),
    List("Acct", "Cycle", "Date", "Age", "Income"),
    List("Acct-1", "Monthly", gepoch20060828, 32, BigDecimal(35000.0D)),
    List("Acct-2", "BiWeekly", gepoch20061007, 14.asInstanceOf[Integer], BigDecimal(14500.0D))
  )

  val expectedSheet: List[String] = List(
    "AcctMock(Acct-1,Monthly,2006-08-28,32,Some(35000.0))",
    "AcctMock(Acct-2,BiWeekly,2006-10-07,14,Some(14500.0))"
  )

}
