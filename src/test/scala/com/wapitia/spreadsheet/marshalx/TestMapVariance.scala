package com.wapitia.spreadsheet.marshalx

object TestMapVariance {

  class Base

  type F = (T => Unit) forSome { type T <: Base }

  var baseList = List[F]()

  def addBase[B <: Base](b: B => Unit) {
    baseList = b :: baseList
  }

}
