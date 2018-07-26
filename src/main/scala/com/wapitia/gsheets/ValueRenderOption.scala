package com.wapitia
package gsheets

import com.wapitia.common.{Enum,EValue}

/** Formal list of the possible value render options as
 *  expected in [[com.google.api.services.sheets.v4.Sheets]].
 *  These enums use Java-style capitalization as the names
 *  match the expected tags of the Google API.
 *
 *  @note the google API doesn't take an enum value as expected,
 *        but rather a String corresponding to the name one of these
 *        enums.
 *        This is probably for flexibility in derived SPIs.
 *
 *  {{{
 *    enum ValueRenderOption {
 *      case FORMATTED_VALUE
 *      case UNFORMATTED_VALUE
 *      case FORMULA
 *    }
 *  }}}
 */
sealed trait ValueRenderOption extends ValueRenderOption.Value with EValue[ValueRenderOption]

object ValueRenderOption extends Enum[ValueRenderOption] {

  /** Formatted values are the strings as seen in on the screen. */
  case object FORMATTED_VALUE extends ValueRenderOption

  /** Unformatted cell values come across as Strings and "raw" BigDecimals */
  case object UNFORMATTED_VALUE extends ValueRenderOption

  /** Delivers pre-calculated entries where formulas are given as strings starting with '=' */
  case object FORMULA extends ValueRenderOption

  val enumValues = List(FORMATTED_VALUE, UNFORMATTED_VALUE, FORMULA)
}

