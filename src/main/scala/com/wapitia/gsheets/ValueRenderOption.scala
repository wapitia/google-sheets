package com.wapitia
package gsheets

import com.wapitia.common.{Enum,EValue}

/** enum ValueRenderOption {
 *    FORMATTED_VALUE, UNFORMATTED_VALUE, FORMULA
 *  }
 */
sealed trait ValueRenderOption extends ValueRenderOption.Value with EValue[ValueRenderOption]

object ValueRenderOption extends Enum[ValueRenderOption] {
  case object FORMATTED_VALUE extends ValueRenderOption
  case object UNFORMATTED_VALUE extends ValueRenderOption
  case object FORMULA extends ValueRenderOption
  
  val values = List(FORMATTED_VALUE, UNFORMATTED_VALUE, FORMULA)
}

