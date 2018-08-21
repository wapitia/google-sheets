package com.wapitia

package object io {

  /** what java.io considers to be the integer defining the
   *  end-of-input "character", -1.
   */
  val EndOfInput = -1

  /**
   * Wrapper of an `InputStream`
   */
  trait InputStreamSupplier {
    def inputStream: java.io.InputStream
  }

}
