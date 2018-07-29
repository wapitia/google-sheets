package com.wapitia
package spreadsheet
package marshalx

import com.wapitia.common.marshal.InMarshal

/**
 *  @tparam A finished row object or builder/marshaller
 *            of such row objects
 */
trait LabelledSheetMarshallerX[A,B] {

  def startNewRow(): LabelledRowMarshalX[A,B]
}

