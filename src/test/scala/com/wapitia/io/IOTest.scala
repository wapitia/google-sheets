package com.wapitia
package io

import java.io.{InputStream, StringReader}

import org.junit.Test
import org.junit.Assert._

class IOTest {

  @Test
  def testReaderInputStream() {

    val rdr: StringReader = new StringReader(" foo baz bat")
    val is: InputStream = ReaderInputStream(rdr)
    var str: String = ""
    print("'")
    Stream.continually(is.read()).takeWhile(_ != -1).map(_.asInstanceOf[Char]).foreach { ch =>
      print(ch.asInstanceOf[Char])
      str += ch.asInstanceOf[Char]
    }
    println("'")
    is.close()
    assertEquals(str, " foo baz bat")
  }
}
