package com.wapitia.rb
package model

import com.wapitia.common.Enum
import com.wapitia.common.EValue

/** enum BlackRoll { case Odd, Even }  */
sealed trait BlackRoll extends BlackRoll.Value with EValue[BlackRoll]

object BlackRoll extends Enum[BlackRoll] {
  case object Odd extends BlackRoll
  case object Even extends BlackRoll
  val values = List(Odd,Even)
}

sealed trait WhiteRoll extends WhiteRoll.Value with EValue[WhiteRoll]

object WhiteRoll extends Enum[WhiteRoll] {
  case object R2 extends WhiteRoll
  case object R3 extends WhiteRoll
  case object R4 extends WhiteRoll
  case object R5 extends WhiteRoll
  case object R6 extends WhiteRoll
  case object R7 extends WhiteRoll
  case object R8 extends WhiteRoll
  case object R9 extends WhiteRoll
  case object R10 extends WhiteRoll
  case object R11 extends WhiteRoll
  case object R12 extends WhiteRoll
  val values = List(R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12)
}

class RegionRoll(blackRoll: BlackRoll, whiteRoll: WhiteRoll, region: Region)

object RegionRoll {
  
  def apply(blackRoll: BlackRoll, whiteRoll: WhiteRoll, region: Region) = 
    new RegionRoll(blackRoll, whiteRoll, region)
  
}
