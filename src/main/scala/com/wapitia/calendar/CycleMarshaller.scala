package com.wapitia
package calendar

/**
 * Marshals a name of a cycle ("Daily", "BiDaily", etc) into its corresponding
 * [[Cycle]] instance.
 *
 * @param defaultCycle backup function to call if input is not recognized
 * @param caseSensitive whether to be strict while checking the case of the string value
 */
class CycleMarshaller(defaultCycle: String => Cycle, caseSensitive: Boolean) extends
  com.wapitia.common.marshal.EnumMarshaller[Cycle](Cycle.enumValueNamed, defaultCycle, caseSensitive)

object CycleMarshaller {

  import com.wapitia.common.marshal.MarshalInException

  def onMissingCycle(value: String) =
    throw new MarshalInException(s"Missing or unrecognized Cycle having name: `$value`")

  // default Cycle marshaller singleton, immutable reusable and thread-safe
  val IntoCycle = apply()

  /** Make a strict new `CycleMarshaller`, case sensitive which throws
   *  a `MarshalInException` on an unrecognizable lookup string.
   */
  def apply(): CycleMarshaller = apply(onMissingCycle, true)

  def apply(defaultCycle: String => Cycle, caseSensitive: Boolean): CycleMarshaller =
    new CycleMarshaller(defaultCycle, caseSensitive)
}
