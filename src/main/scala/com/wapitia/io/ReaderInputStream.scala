package com.wapitia
package io

import java.io.{InputStream, Reader, PipedInputStream}

/** Creates a `java.io.InputStream` instance wrapping a user
 *  supplied `java.io.Reader`
 */
object ReaderInputStream {

  /** Default transfer chunk size in number of characters */
  val CharBufSize = 16384

  /** Default character set name as provided by `Charset.defaultCharset()`.
   */
  def defaultCharsetName = java.nio.charset.Charset.defaultCharset().name()

  /** Read all of the characters from the Reader, creating and providing those
   *  characters as a new `InputStream` instance.
   *  The mapped character set is provided by `defaultCharsetName`.
   *
   *  @param reader the reader whose characters will be transferred to the sink,
   *                until the reader is exhausted. Closed when finished.
   */
  def apply(reader: Reader): PipedInputStream = apply(reader, defaultCharsetName)

  /** Read all of the characters from the Reader, creating and providing those
   *  characters as a new `InputStream` instance, mapped according to the user
   *  defined character encoding. The encoding must not be null.
   *
   *  @param reader the reader whose characters will be transferred to the sink,
   *                until the reader is exhausted. Closed when finished.
   *  @param encoding the character set encoding to use to write the characters
   *                  to the sink.  Must not be null. Must be a recognized set.
   */
  def apply(reader: Reader, encoding: String): PipedInputStream = {
    val in = new PipedInputStream()
    val pipeEngine = new PipeReaderToStreamRunnable(in, reader, encoding, CharBufSize)
    new Thread(pipeEngine).start()
    in
  }
}

/** Engine takes some `Reader` of characters and pipes that to the
 *  supplied `PipedInputStream` until the source Reader is exhausted.
 *  The reader is closed upon completion.
 *  The sink is flushed but not closed upon completion.
 *
 *  Designed as a `Runnable` to be run in a separate thread.
 *
 *  Reader -> Array[Char] -> OutputStreamWriter -> PipedOutputStream |-> sink
 *
 *  @param sink supplied PipedInputStream will be sourced by
 *              a PipedOutputStream from the contents of the given reader.
 *  @param reader the reader whose characters will be transferred to the sink,
 *                until the reader is exhausted. Closed when finished.
 *  @param encoding the character set encoding to use to write the characters
 *                  to the sink.  Must not be null.
 *  @param charBufSize size of the character transfer buffer, in number of
 *                characters. Should be a large-ish power of two, but doesn't
 *                need to be larger than the number of incoming characters.
 */
class PipeReaderToStreamRunnable(sink: PipedInputStream, reader: Reader,
  encoding: String, charBufSize: Int)
extends java.lang.Runnable {

  override def run() {
    val out = new java.io.PipedOutputStream(sink)
    val writer = new java.io.OutputStreamWriter(out, encoding)
    val buf = new Array[Char](charBufSize)

    try {
      Stream.continually(reader.read(buf))
      .takeWhile(_ != EndOfInput)
      .foreach { n =>
        sink.synchronized { writer.write(buf, 0, n) }
      }
      sink.synchronized { writer.flush() }
      reader.close()
    } finally {
      // close the output pipes, ignoring IOException
      try {
        writer.close()
      } catch {
        case ioe: java.io.IOException => ()
      }
      try {
        out.close()
      } catch {
        case ioe: java.io.IOException => ()
      }
    }
  }
}
