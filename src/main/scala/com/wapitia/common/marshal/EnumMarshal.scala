package com.wapitia
package common
package marshal

/**
 * Marshals the name of some enumeration type into its corresponding
 * enum instance.
 *
 * Parameters define how input values are read in and matched with the
 * corresponding `EValue`s.
 *
 * @tparam A           The Wapitia Enumeration derived type, some `Enum` object
 *
 * @param enumNameMap  Map of expected input strings to their corresponding
 *                     `Enum` values.
 * @param defaultValue function to call if the input value is `null`
 *                     or otherwise unmatchable with the enumNameMap.
 *                     Usually some `RuntimeException` callback.
 * @param caseSensitive whether or not to treat name case strictly.
 *
 */
class EnumMarshal[A <: EValue[A]](
    enumNameMap: Map[String,A],
    defaultValue: String => A,
    caseSensitive: Boolean)
  extends InMarshal[Any,A] {

  import EnumMarshal._

  // translate the given `enumNameMap` into a case-sensitive (all caps) version
  // or not, depending on `caseSensitive` flag.
  val trueMap = genTrueMap[A](enumNameMap, caseSensitive)

  override def unmarshal(enumName: Any): A =
    unmarshalString[A](enumName.toString, defaultValue, trueMap, caseSensitive, true)
}

/** Objects for marshalling derived Enum collections extend this template
 *  to provide an `EnumMarshal` instance, rather than extending `EnumMarshal`
 *  directly.
 *
 *  @usage
 *  {{{
 *    object RollMarshaller extends EnumMarshallerTemplate {
 *      override type EnumType = Roll
 *      override def enumValueMap = Roll.enumValueNamed
 *    }
 *
 *    val myRollMarshal: EnumMarshaller[Roll] = RollMarshaller.Into
 *  }}}
 */
trait EnumMarshallerTemplate {
  import com.wapitia.common.marshal.UnmarshalException

  type EnumType <: EValue[EnumType]
  def enumValueMap: Map[String,EnumType]

  type MarshallerType = EnumMarshal[EnumType]
  def collectionName = scala.reflect.runtime.universe.weakTypeTag[EnumType].tpe.toString

  val Into = apply()

  val onMissingValue = (value: String) =>
    throw new UnmarshalException(s"Missing or unrecognized $collectionName enum having name: `$value`")

  /** Make a strict new `Marshaller`, case sensitive which throws
   *  a `UnmarshalException` on an unrecognizable lookup string.
   */
  def apply(): MarshallerType = apply(onMissingValue, true)

  def apply(defaultCycle: String => EnumType, caseSensitive: Boolean): MarshallerType =
    new EnumMarshal[EnumType](enumValueMap, defaultCycle, caseSensitive)
}

/** EnumMarshaller helper functions */
object EnumMarshal {

  def genTrueMap[A <: EValue[A]](lookup: Map[String,A], caseSensitive: Boolean) = {
    if (caseSensitive) lookup else lookup map { case (k, v) => (normValue(k), v) }
  }

  def unmarshalString[A <: EValue[A]](
      enumName: String,
      defaultValue: String => A,
      enumNameMap: Map[String,A],
      caseSensitive: Boolean,
      trimInputValue: Boolean): A =
  {
    if (enumName == null)
      defaultValue("null")
    else {
      val normName = if (trimInputValue) enumName.trim else enumName
      val lookupName = if (caseSensitive) normName else normValue(normName)
      unmarshalOrElse[String,A](enumNameMap.get(_))(lookupName, defaultValue)
    }
  }

  def unmarshalOrElse[LU <: AnyRef,A <: EValue[A]](lookup: LU => Option[A])(enumName: LU, defaultValue: LU => A): A = {
    val res = lookup(enumName)
    res.getOrElse(defaultValue(enumName))
  }

  def normValue(s: String) = s.toUpperCase
}
