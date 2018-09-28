package com.wapitia.spreadsheet.marshalx

import java.time.LocalDate

import com.wapitia.gsheets.marshal.{nullableDateMarshal => intoDate}
import com.wapitia.spreadsheet.marshal.RowDocElementAccumulator
import com.wapitia.spreadsheet.marshal.XmlMarshaller
import com.wapitia.spreadsheet.marshal.ElementRowMarshal
import com.wapitia.spreadsheet.marshal.XmlItemPackagePolicy

import XmlMarshaller._
import AcctMockElementMarshaller._

/** Test for marshalling a google spreadsheet's data into a mock Acct instance
 */
class AcctMockElementMarshaller(containerBldr: RowDocElementAccumulator)
extends XmlMarshaller(containerBldr, xmlNormName _, incomeEltPolicy _) {

  import XmlMarshaller._

  private[this] def init() {
    marshalChain("Date", intoDate, setter[LocalDate,ElementRowMarshal])
  }

  init()

}

object AcctMockElementMarshaller {

  def incomeEltPolicy(s: String): XmlItemPackagePolicy = s match {
    case "income" => XmlItemPackagePolicy.ChildElement
    case _ => XmlItemPackagePolicy.Attr
  }

}
