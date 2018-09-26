package com.wapitia
package spreadsheet
package marshal

import com.wapitia.common.marshal.InMarshal

import com.wapitia.spreadsheet.marshal.RowMarshal.{CellMarshal}
import com.wapitia.spreadsheet.marshal.ConfiguredRowMarshal.{BoundedSetFunc,UnboundSetFunc}

class CellMarshalLibrary(defaultMarshal: InMarshal[_,_]) extends CellMarshalRepo {
  import ConfiguredSheetMarshal._

  val cellMarshalByName = scala.collection.mutable.Map[String,CellMarshal[_]]()

  override def getCellMarshal[C](name: String): CellMarshal[C] = {
    cellMarshalByName.getOrElse(name, defaultMarshal).asInstanceOf[InMarshal[Any,C]]
  }

  def addCellMarshal[C](name: String, cellMarshal: CellMarshal[C]) {
    cellMarshalByName.put(name, cellMarshal)
  }
}

class SetFuncLibrary[A,M <: RowMarshal[A]](defaultFunc: UnboundSetFunc[A,M,_]) extends SetFuncRepo[A,M] {

  val setFuncByName = scala.collection.mutable.Map[String,UnboundSetFunc[A,M,_]]()

  def getBoundSetFunc[C](marshal: M, name: String): BoundedSetFunc[C] = {
    val buildFunc  = setFuncByName.getOrElse(name, defaultFunc).asInstanceOf[UnboundSetFunc[A,M,C]]
    (name: String, value: C) => buildFunc(marshal, name, value)
  }

  def addSetFunc[C](name: String, setFunc: UnboundSetFunc[A,M,C]) {
    setFuncByName.put(name, setFunc)
  }

}

class MarshalChainLibrary[A,M <: RowMarshal[A]](cellMarshalLib: CellMarshalLibrary, setterLib: SetFuncLibrary[A,M])
extends MarshalSetRepo[A,M] {

  import ConfiguredSheetMarshal._

  override def repoCellMarshal: CellMarshalRepo = cellMarshalLib

  override def repoSetFunc: SetFuncRepo[A,M] = setterLib

  def addCellMarshal[C](name: String, cellMarshal: CellMarshal[C])  = cellMarshalLib.addCellMarshal[C](name, cellMarshal)

  def addSetFunc[C](name: String, setFunc: UnboundSetFunc[A,M,C]) = setterLib.addSetFunc[C](name, setFunc)
}

object SheetMarshalRepo {

  val MarshalIdentity = new InMarshal[Any,Any] {
    override def unmarshal(v: Any) = v
  }

  def NOOP[MR,CT](msh: MR, n: String, v: CT) {}

  def makeCellMarshalLibrary(defaultMarshal: InMarshal[_,_]): CellMarshalLibrary =
    new CellMarshalLibrary(defaultMarshal)

  def makeSetFuncLibrary[A,M <: RowMarshal[A]](defaultFunc: UnboundSetFunc[A,M,_]): SetFuncLibrary[A,M] =
    new SetFuncLibrary[A,M](defaultFunc)

  def makeMarshalChainLibrary[A,M <: RowMarshal[A]](cellMarshalLib: CellMarshalLibrary, setterLib: SetFuncLibrary[A,M]): MarshalChainLibrary[A,M] =
    new MarshalChainLibrary[A,M](cellMarshalLib, setterLib)

  def makeDefaultCellMarshalLibrary() =
    makeCellMarshalLibrary(MarshalIdentity)

  def makeDefaultSetFuncLibrary[A,M <: RowMarshal[A]]() =
    makeSetFuncLibrary[A,M](NOOP[M,Any])

  def makeMarshalChainLibrary[A,M <: RowMarshal[A]](defaultMarshal: InMarshal[_,_], defaultFunc: UnboundSetFunc[A,M,_]): MarshalChainLibrary[A,M] =
    makeMarshalChainLibrary[A,M](makeCellMarshalLibrary(defaultMarshal), makeSetFuncLibrary[A,M](defaultFunc))

  def makeDefaultMarshalChainLibrary[A,M <: RowMarshal[A]]() =
    makeMarshalChainLibrary[A,M](makeDefaultCellMarshalLibrary(), makeDefaultSetFuncLibrary[A,M]())
}
