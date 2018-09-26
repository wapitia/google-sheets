package com.wapitia
package spreadsheet
package marshal

import com.wapitia.spreadsheet.marshal.RowMarshal.{CellMarshal}

trait CellMarshalRepo {

  def getCellMarshal[C](name: String): CellMarshal[C]
}

/** A Repository of setter functions keyed by column name and type. */
trait SetFuncRepo[A,M <: RowMarshal[A]] {

  import ConfiguredRowMarshal.BoundedSetFunc

  def getBoundSetFunc[C](marshal: M, name: String): BoundedSetFunc[C]
}

trait MarshalSetRepo[A,M <: RowMarshal[A]] {

  def repoCellMarshal: CellMarshalRepo
  def repoSetFunc: SetFuncRepo[A,M]
}

abstract class ConfiguredRowMarshal[A,M <: ConfiguredRowMarshal[A,M]](mcRepo: MarshalSetRepo[A,M]) extends RowMarshal[A] {

  import ConfiguredRowMarshal._

  override def make(): A

  def defaultSetMarshalled[C](name: String, value: C): Unit =
      throw new RuntimeException(s"Unsupported value named $name, value: $value")

  override def cellMarshal[C](name: String): CellMarshal[C] = mcRepo.repoCellMarshal.getCellMarshal[C](name)

  override def setMarshalled[C](name: String, value: C) {
    val sf: BoundedSetFunc[C] = mcRepo.repoSetFunc.getBoundSetFunc(this.asInstanceOf[M], name)
    sf(name, value)
  }

}

object ConfiguredRowMarshal {

  /** Function taking a name and value and setting a value via a RowMarshal */
  type UnboundSetFunc[A,M <: RowMarshal[A],C] = (M,String,C) => Unit

  /** Function taking a name and value and setting a value via a RowMarshal */
  type BoundedSetFunc[C] = (String,C) => Unit

}
