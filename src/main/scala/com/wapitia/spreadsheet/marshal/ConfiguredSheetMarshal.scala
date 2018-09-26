package com.wapitia
package spreadsheet
package marshal

import com.wapitia.common.marshal.InMarshal
import com.wapitia.common.BLDR

import com.wapitia.spreadsheet.marshal.RowMarshal.{CellMarshal}
import com.wapitia.spreadsheet.marshal.ConfiguredRowMarshal.{BoundedSetFunc,UnboundSetFunc}

class ConfiguredRowBuilder[A,M <: ConfiguredRowMarshal[A,M],B <: BLDR[A]](mcRepo: MarshalSetRepo[A,M], newBuilder: B)
extends ConfiguredRowMarshal[A,M](mcRepo) {
  var rb: B = newBuilder
  override def make(): A = rb.build()
}

/** Marshals a spreadsheet of data into rows of objects of some type `A`.
 *  This is configured with marshals and binders for each labeled column.
 *
 *  @tparam A finished row object type
 */
abstract class ConfiguredSheetMarshal[A] {

  type RM <: RowMarshal[A]

  import ConfiguredSheetMarshal._

  /** Start a new row marshal to ingest incoming raw values to produce its object */
  def makeRowMarshaller(): RM

  def newMarshalChainLibraryInstance() = SheetMarshalRepo.makeDefaultMarshalChainLibrary[A,RM]()

  val mcRepo: MarshalChainLibrary[A,RM] = newMarshalChainLibraryInstance()

  def addCellMarshal[C](name: String, marshal: CellMarshal[C]) {
    mcRepo.addCellMarshal(name, marshal)
  }

  def addSetFunc[C](name: String, binder: UnboundSetFunc[A,RM,C]) {
    mcRepo.addSetFunc[C](name, binder)
  }

  def marshalChainRepo: MarshalSetRepo[A,RM] = mcRepo

  /** Convenience method to add both a marshal and a binder for one named cell */
  def marshalChain[C](name: String, marshal: CellMarshal[C], binder: UnboundSetFunc[A,RM,C])  {
    addCellMarshal(name, marshal)
    addSetFunc(name, binder)
  }
}

object ConfiguredSheetMarshal {

}
