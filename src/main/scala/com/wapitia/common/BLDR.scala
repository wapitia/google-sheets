package com.wapitia.common

/** Builder class defines `build()` returning an instance of the thing it's building */
trait BLDR[A] {

  def build(): A
}
