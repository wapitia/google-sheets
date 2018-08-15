package com.wapitia
package io

import java.io.{InputStream => JInStream, Reader => JReader, PipedInputStream => JPipedIn}

/** Creates a `java.io.InputStream` instance wrapping a user supplied `java.io.Reader` */
object ReaderInputStream {

  val DefaultCharset: String = java.nio.charset.Charset.defaultCharset().name()

  def apply(reader: JReader): JPipedIn = apply(reader, DefaultCharset)

  def apply(reader: JReader, encoding: String): JPipedIn = {
    val in = new JPipedIn()
    new Thread(new PipeReaderToStreamRunnable(in, reader, encoding)).start()
    in
  }

  class PipeReaderToStreamRunnable(sink: JPipedIn, reader: JReader, encoding: String) extends java.lang.Runnable {

    // read chunk size
    val CharBufSize = 16384

    // what java.io considers to be the integer defining the end-of-input "character"
    val EndOfInput = -1

    private[this] val out = new java.io.PipedOutputStream(sink)
    private[this] val writer = new java.io.OutputStreamWriter(out, encoding)
    private[this] val buf = new Array[Char](CharBufSize)

    override def run() {
      try {
        Stream.continually(reader.read(buf)).takeWhile(_ != EndOfInput).foreach { n =>
          sink.synchronized {
            writer.write(buf, 0, n)
          }
        }
        sink.synchronized {
          writer.flush()
        }
      } finally {
        // close the output pipes, ignoring IOException
        try {
          writer.close()
        }
        catch {
          case ioe: java.io.IOException => ()
        }
        try {
          out.close()
        }
        catch {
          case ioe: java.io.IOException => ()
        }
      }
    }
  }

}
