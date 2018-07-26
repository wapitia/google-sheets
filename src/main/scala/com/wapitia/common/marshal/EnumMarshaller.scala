package com.wapitia
package common
package marshal

/**
 * Marshals a name of some enumeration type into its corresponding instance.
 *
 * Parameters define how input values are read in and matched with the
 * corresponding `EValue`s.
 *
 * @tparam A           The Wapitia Enumeration derived type, some `Enum` object
 *
 * @param defaultValue The `Enum` object to return if the input value is `null`
 *                     or an empty string. Usually some `RuntimeException`
 *                     callback.
 * @param whenBad      The `Enum` object to return if the input value is a non-null
 *                     String that matches nothing from the lookup map.
 *                     Usually some `RuntimeException` callback.
 * @param enumNameMap  Map of expected input strings to their corresponding
 *                     `Enum` values.
 *
 */
class EnumMarshaller[A <: EValue[A]](
  enumNameMap: Map[String,A],
  defaultValue: String => A,
  caseSensitive: Boolean)
  extends MarshalIn[Any,A] {

  import EnumMarshaller._

  val trueMap = genTrueMap[A](enumNameMap, caseSensitive)

  override def unmarshal(enumName: Any): A =
    unmarshalString[A](enumName.toString, defaultValue, trueMap, caseSensitive, true)
}

object EnumMarshaller {

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
