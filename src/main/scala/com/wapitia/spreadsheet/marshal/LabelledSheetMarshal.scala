package com.wapitia
package spreadsheet
package marshal

import com.wapitia.common.marshal.InMarshal
import LabelledSheetMarshal._
import com.wapitia.common.ImmutableBuilder

/** A Repository of setter functions keyed by column name and type. */
trait SetterRepo[A,M <: RowMarshal[A]] {
  def getBoundSetFunc[C](marshal: M, name: String): (String,C) => Unit
}

/** Accumulator and Builder for each row of data in the spreadsheet */
abstract class RowMarshal[A] {

  import LabelledSheetMarshal._

  /** Make the instance of the resultant object for this row */
  def make(): A

  def cellMarshal[C](name: String): CellMarshal[C]

  def setMarshalled[C](name: String, value: C): Unit

  /** Set the value for a named slot in the accumulating result object */
  def setRaw[C](name: String, rawvalue: Any): Unit = {

    val cm = cellMarshal[C](name)

    if (!cm.isNull(rawvalue)) {
      val mval: C = cm.unmarshal(rawvalue)
      setMarshalled[C](name, mval)
    }
  }

}

trait CellMarshalRepo {

  import LabelledSheetMarshal._

  def getCellMarshal[C](name: String): CellMarshal[C]
}

abstract class ConfiguredRowMarshal[A,M <: ConfiguredRowMarshal[A,M]](cellMarshalRepo: CellMarshalRepo, setterRepo: SetterRepo[A,M]) extends RowMarshal[A] {

  import LabelledSheetMarshal._

  override def make(): A

  def defaultSetMarshalled[C](name: String, value: C): Unit =
      throw new RuntimeException(s"Unsupported value named $name, value: $value")

  override def cellMarshal[C](name: String): CellMarshal[C] = cellMarshalRepo.getCellMarshal[C](name)

  override def setMarshalled[C](name: String, value: C) {
    val sf = setterRepo.getBoundSetFunc(this.asInstanceOf[M], name)
    sf(name, value)
  }

}

class BoundSetFunc[A,M <: RowMarshal[A],C](marshal: M, deffunc: UnboundSetFunc[A,M,C]) {

  def set(name: String, value: C) {
    deffunc(marshal, name, value)
  }
}

class LabelledSetterLibrary[A,M <: RowMarshal[A]](deffunc: UnboundSetFunc[A,M,_]) extends SetterRepo[A,M] {

  val objMarshalMap = scala.collection.mutable.Map[String,UnboundSetFunc[A,M,_]]()

  def getBoundSetFunc[C](marshal: M, name: String): (String,C) => Unit = {
    val buildFunc: UnboundSetFunc[A,M,C] = objMarshalMap.getOrElse(name, deffunc ).asInstanceOf[UnboundSetFunc[A,M,C]]
    new BoundSetFunc[A,M,C](marshal, buildFunc).set
  }

  def put[C](name: String, binder: UnboundSetFunc[A,M,C]) {
    objMarshalMap.put(name, binder)
  }

}

class LabelledCellMarshalLibrary(defaultMarshal: InMarshal[_,_]) extends CellMarshalRepo {
  import LabelledSheetMarshal._

  val cellMarshalMap = scala.collection.mutable.Map[String,CellMarshal[_]]()

  override def getCellMarshal[C](name: String): CellMarshal[C] = {
    cellMarshalMap.getOrElse(name, defaultMarshal).asInstanceOf[InMarshal[Any,C]]
  }

  def put[C](name: String, cellMarshal: CellMarshal[C]) {
    cellMarshalMap.put(name, cellMarshal)
  }
}

class ConfiguredRowBuilder[A,M <: ConfiguredRowMarshal[A,M],B <: ImmutableBuilder[A]]
(cellMarshalRepo: CellMarshalRepo, setterRepo: SetterRepo[A,M], newBuilder: () => B)
extends ConfiguredRowMarshal[A,M](cellMarshalRepo, setterRepo) {

  var rb: B = newBuilder()
  override def make(): A = rb.build()
}

/** Marshals a spreadsheet of data into rows of objects of some type `A`.
 *  This is configured with marshals and binders for each labeled column.
 *
 *  @tparam A finished row object type
 */
abstract class LabelledSheetMarshal[A] {

  type RM <: RowMarshal[A]

  import LabelledSheetMarshal._

  /** Start a new row marshal to ingest incoming raw values to produce its object */
  def makeRowMarshaller(): RM

  val setterLib: LabelledSetterLibrary[A,RM] = new LabelledSetterLibrary[A,RM](NOOP[RM,Any])

  val cellMarshalLib: LabelledCellMarshalLibrary = new LabelledCellMarshalLibrary(MarshalIdentity)

  def addMarshal[C](name: String, marshal: CellMarshal[C]) {
    cellMarshalLib.put(name, marshal)
  }

  def addBinder[C](name: String, binder: UnboundSetFunc[A,RM,C]) {
    setterLib.put[C](name, binder)
  }

  /** Convenience method to add both a marshal and a binder for one named cell */
  def marshalChain[C](name: String, marshal: CellMarshal[C], binder: UnboundSetFunc[A,RM,C])  {
    addMarshal(name, marshal)
    addBinder(name, binder)
  }

  def cellMarshalRepo: CellMarshalRepo = cellMarshalLib
  def setterRepo: SetterRepo[A,RM] = setterLib
}

/** LabelledSheetMarshal helper objects */
object LabelledSheetMarshal {

  /** Marshals a cell value from its original value into the internal type before
   *  binding via the binder function.
   */
  type CellMarshal[C] = InMarshal[Any,C]

  /** Function taking a name and value and setting a value via a RowMarshal */
  type UnboundSetFunc[A,M <: RowMarshal[A],C] = (M,String,C) => Unit

  val MarshalIdentity = new InMarshal[Any,Any] {
    override def unmarshal(v: Any) = v
  }

  def NOOP[MR,CT](msh: MR, n: String, v: CT) {}
}
