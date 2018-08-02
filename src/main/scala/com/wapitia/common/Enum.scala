package com.wapitia
package common

/** A value of an `Enum`.
 *  Each `EValue` may be implicitly converted to a `String`,
 *  and has a `name` function returning the name of the base enum
 *  object as a `String`.
 *
 * ==Example==
 * {{{
 *   import com.wapitia.common.{Enum,EValue}
 *
 *   sealed trait Toss extends Toss.Value with EValue[Toss]
 *
 *   object Toss extends Enum[Toss] {
 *     case object Heads extends Toss
 *     case object Tails extends Toss
 *     val enumValues = List(Heads, Tails)
 *   }
 * }}}
 *
 * Elsewhere ...
 *
 * {{{
 *
 *   import Toss._
 *
 *   def flip(toss: Toss): String = toss match {
 *     case Heads => "I win"
 *     case Tails => "You lose"
 *   }
 *
 * }}}
 *
 *  @tparam A The type of the enum, self-reflective as in
 *            trait `Z extends Z.Value with EValue[Z]`
 */
@deprecated("Scala 3 introduces Enumeration at which point most of this is moot", "Scala 3")
trait EValue[A] {
  self: A=>

  /** the name of the "raw" enum object as a string.
   *  The Evalue's class name, when used correctly, will be of the form
   *  `com.wapitia.common.Toss$Heads` so we split the name at the '$' and take
   *  the last string which in this case is 'Heads'.
   *  Regular expression "\\$" matches the literal dollar sign.
   */
  def name: String = self.getClass.getName.split("\\$").last

  /** the enum object's name */
  override def toString = name

  /** implicit string conversion */
  implicit def eval2String(x: EValue[_]): String = x.name
}

/**
 * Enum contains object instances of each EValue element for a given enum type.
 *
 * ==Example==
 * {{{
 *   import com.wapitia.common.{Enum,EValue}
 *
 *   sealed trait Cycle extends Cycle.Value with EValue[Cycle] {
 *     val daysInCycle: Int
 *   }
 *
 *   object Cycle extends Enum[Cycle] {
 *     case object Daily extends Cycle{ val daysInCycle = 1 }
 *     case object Weekly extends Cycle{ val daysInCycle = 7 }
 *     case object BiWeekly extends Cycle{ val daysInCycle = 14 }
 *
 *     val enumValues = List(Daily, Weekly, BiWeekly)
 *   }
 * }}}
 *
 * Elsewhere ...
 *
 * {{{
 *
 *   import Cycle._
 *
 *   def cycleDescription(c: Cycle) = c match {
 *     case Cycle.Daily    => println(s"$c.name means every day")
 *     case Cycle.Weekly   => println(s"$c.name means every week ($c.daysInCycle days)")
 *     case Cycle.BiWeekly => println(s"$c.name means every two weeks ($c.daysInCycle days)")
 *   }
 *
 * }}}
 *
 * The `EValue` extension is sealed so that the compiler can determine
 * that the cases match exhaustively.
 * The `EValue` extension is a trait rather than a class so that the
 * pattern `Cycle()` cannot be used, so that the cases match exhaustively.
 * The `EValue` extension extends `Cycle.Value` to tie into the
 * expected `ENum` object extension.
 *
 * @tparam A The type of the enum which must also extend `EValue` as in
 *           object `Z` extends `Enum[Z]`
 */
trait Enum[A <: EValue[A]] {
  /** Enum values must extend the type parameter */
  trait Value { self: A=> }

  /** List of `Enum` objects suitable for iteration as in
   *  `Z.values.foreach { z => println(s"$z.name") }.`
   *  This mirrors the Scala 3 Enumeration construct, which has an
   *  `enumValues()` method returning its list.
   */
  val enumValues: Iterable[A]

  /** Look up the given name of the enum which must match the object's base name. */
  def valueOf(name: String): A = enumValueNamed(name)

  /** Map of `Enum` indexes to corresponding enum values, 0 to n */
  lazy val enumValue: Map[Int,A] = enumValues.zipWithIndex.map {case (v,i) => i -> v }.toMap

  /** Map of `Enum` names to corresponding instances */
  lazy val enumValueNamed: Map[String,A] = enumValues.map(v => v.name -> v).toMap

}
