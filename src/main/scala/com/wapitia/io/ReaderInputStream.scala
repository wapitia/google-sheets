package com.wapitia
package io

import java.io.{InputStream => JInStream, Reader => JReader, PipedInputStream => JPipedIn}

/** Creates an InputStream wrapping a user supplied Reader */
object ReaderInputStream {

  val NoEncoding: String = null.asInstanceOf[String]

  def apply(reader: JReader, encoding: String): JInStream = readerStreamEngine(reader, encoding)

  def apply(reader: JReader): JInStream = readerStreamEngine(reader, NoEncoding)

  def readerStreamEngine(reader: JReader, encoding: String): JPipedIn = {
    val in = new JPipedIn()
    val piper = new PipeReaderToStreamRunnable(in, reader, encoding)
    new Thread(piper).start()
    in
  }

  class PipeReaderToStreamRunnable(sink: JPipedIn, reader: JReader, encoding: String) extends java.lang.Runnable {

    val CharBufSize = 16384
    val EndOfInput = -1

    val out = new java.io.PipedOutputStream(sink)
    val writer = if (encoding == NoEncoding)
      new java.io.OutputStreamWriter(out)
    else
      new java.io.OutputStreamWriter(out, encoding)

    val buf = new Array[Char](CharBufSize)

    override def run() {
      try {
        Stream.continually(reader.read(buf)).takeWhile(_ != -1).foreach { n =>
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
