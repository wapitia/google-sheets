package com.wapitia
package io

import java.io.{InputStream, Reader}

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
   *  characters as a new `InputStream`.
   *  The mapped character set is provided by `defaultCharsetName`.
   *
   *  @param reader the reader whose characters will be read until exhausted.
   *                Closed when finished.
   */
  def apply(reader: Reader): InputStream = apply(reader, defaultCharsetName)

  /** Read all of the characters from the Reader, creating and providing those
   *  characters as a new `InputStream` instance, mapped according to the user
   *  defined character encoding. The encoding must not be null.
   *
   *  @param reader the reader whose characters will be read until exhausted.
   *                Closed when finished.
   *  @param encoding the character set encoding to use to transfer characters
   *                to the input stream.  Must not be null.
   *                Must be a recognized set.
   */
  def apply(reader: Reader, encoding: String): InputStream =
    apply(reader, encoding, CharBufSize)

  def apply(reader: Reader, encoding: String, bufSize: Int): InputStream = {
    val pipeEngine = new RunnableReaderToStream(reader, encoding, bufSize)
    new Thread(pipeEngine).start()
    pipeEngine.inputStream
  }
}

/** Engine takes some `Reader` of characters and pipes that to a new
 *  `InputStream` until the source Reader is exhausted.
 *  The reader is closed upon completion.
 *
 *  Designed as a `Runnable` to be run in a separate thread in accordance
 *  with the `java.io.PipedOutputStream` / `java.io.PipedInputStream` pattern.
 *  The piped pattern is used so that the whole input need not be loaded into
 *  memory at once.
 *
 *  Reader -> Array[Char] -> OutputStreamWriter -> PipedOutputStream |-> PipedOutputStream
 *
 *  @param reader the reader whose characters will be transferred to input
 *              stream until reader is exhausted. Closed when finished.
 *  @param encoding the encoding to in the character transfer.  Must not be null.
 *  @param bufSize size of the character transfer buffer, in number of
 *              characters. Should be a large-ish power of two, but doesn't
 *              need to be larger than the number of incoming characters.
 */
class RunnableReaderToStream(reader: Reader, encoding: String, bufSize: Int)
extends java.lang.Runnable with InputStreamSupplier {
  private val sink = new java.io.PipedInputStream(bufSize)
  private val out = new java.io.PipedOutputStream(sink)
  private val writer = new java.io.OutputStreamWriter(out, encoding)
  private val buf = new Array[Char](bufSize)

  /** The generated inputStream is a PipedInputStream which will block reads
   *  while the reader's input is not finished but also not available.
   */
  override def inputStream: InputStream = sink

  /** Transfer of characters begins and ends with this run method. */
  override def run() {

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
