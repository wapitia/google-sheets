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
    apply(reader, encoding, CharBufSize, true)

  def apply(reader: Reader, encoding: String, bufSize: Int, closeReaderWhenFinished: Boolean): InputStream = {
    val pipeEngine = new RunnableReaderToStream(reader, encoding, bufSize, closeReaderWhenFinished)
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
class RunnableReaderToStream(reader: Reader, encoding: String, bufSize: Int, closeReader: Boolean)
extends java.lang.Runnable with InputStreamSupplier {

  val pipein = new java.io.PipedInputStream(bufSize)
  // other end of Piped stream must be established in constructor before
  // run thread is invoked, otherwise an unconnected exception will be thrown
  val pipeout = new java.io.PipedOutputStream(pipein)

  /** The generated inputStream is a PipedInputStream which will block reads
   *  while the reader's input is not finished but also not available.
   */
  override def inputStream: InputStream = pipein

  /** Transfer of characters begins and ends with this run method. */
  override def run() {

    val writer = new java.io.OutputStreamWriter(pipeout, encoding)
    val buf = new Array[Char](bufSize)
    try {
      Stream.continually(reader.read(buf))
      .takeWhile(_ != EndOfInput)
      .foreach { n =>
        pipein.synchronized { writer.write(buf, 0, n) }
      }
      pipein.synchronized { writer.flush() }
      if (closeReader) reader.close()
    } finally {
      // close the output pipes, ignoring IOException
      try {
        writer.close()
      } catch {
        case ioe: java.io.IOException => ()
      }
      try {
        // writer should have closed pipeout too, but who knows
        pipeout.close()
      } catch {
        case ioe: java.io.IOException => ()
      }
    }
  }
}
