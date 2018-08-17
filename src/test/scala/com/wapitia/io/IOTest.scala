package com.wapitia
package io

import java.io.{InputStream, StringReader}

import org.junit.Test
import org.junit.Assert._

class IOTest {

  @Test
  def testReaderInputStream() {

    trial1ReaderInputStream(" foo baz bat", 12000)
    trial1ReaderInputStream(" foo baz bat", 10)
    def veryLong = """PipedInputStream and PipedOutputStream in Java with example
      |A piped I/O is based on the producer-consumer pattern, where the producer produces data and the consumer
      |consumes it. A piped output stream can be connected to a piped input stream to create a communication pipe.
      |The piped output stream is the sending end of the pipe. Typically, data is written to a PipedOutputStream
      |object by one thread and data is read from the connected PipedInputStream by some other thread.
      |Attempting to use both objects from a single thread is not recommended as it may deadlock the thread.
      |The pipe is said to be broken if a thread that was reading data bytes from the connected piped input
      |stream is no longer alive. In this post we will see the usage of PipedInputStream and PipedOutputStream
      |in Java with example programs.
      """.stripMargin
    trial1ReaderInputStream(veryLong, 1)
  }

  def trial1ReaderInputStream(ins: String, bufsize: Int) {

    val rdr: StringReader = new StringReader(ins)
    val is: InputStream = ReaderInputStream(rdr, ReaderInputStream.defaultCharsetName, bufsize, true)
    var str: String = ""
    Stream.continually(is.read()).takeWhile(_ != -1).map(_.asInstanceOf[Char]).foreach { ch =>
      str += ch.asInstanceOf[Char]
    }
    is.close()
    assertEquals(str, ins)
  }
}
