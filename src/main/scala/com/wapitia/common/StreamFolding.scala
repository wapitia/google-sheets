package com.wapitia
package common

/** Combine a sequence of iterators into one grand iterator.
 *  The iterator picks the "lowest" head among its set of iterators until
 *  all of its iterators are exhausted.  The ordering of the iterator elements
 *  is provided by an implicit parameter.
 *
 *  @tparam A common type of all iterators, whose elements will be compared
 *            against each other.
 *  @constructor makes a new `Iterator[A]` wrapping the given sequence
 *               of iterators in `iters`, using the implicit ording
 *               to compare those iterators' head elements.
 *  @param iters collection of iterators, each of which will be emptied
 *               as this stream folding iterator steps through them.
 *  @param tComp implicit Ordering used to compare the heads of the iterators.
 *  @returns Iterator[A] delivering each element of the given iterators
 *               from `iter` in turn according to the implicit ordering.
 */
class StreamFolding[A](iters: Seq[Iterator[A]])(implicit tComp: Ordering[A]) extends Iterator[A] {

  /** remIterList is variable because the list is reduced by function
   *  [[next()]] when one of its iterator elements becomes exhausted.
   *  Each of the incoming iterators is wrapped as a BufferedIterator since
   *  we'll want to peek at the heads of the iterators without popping them.
   */
  private var remIterList: List[BufferedIterator[A]] =
    iters.map(_.buffered).filter(_.hasNext).toList

  /** INVARIANCE REQUIREMENT: None of the BufferedIterators in remIterList are empty. */
  private def invariance = !remIterList.exists(_.isEmpty)
  assert(invariance)

  /** There is a next item iff there are any remaining `BufferedIterators`
   *  in `remIterList` since any iterators in that collection are non-empty
   *  according to the invariance requirement.
   */
  override def hasNext: Boolean = ! remIterList.isEmpty

  /** Get the next iteration element from it's list of remaining iterators.
   *  This refreshes the remaining list of iterators, which is reduced as
   *  iterators become exhausted.
   */
  override def next(): A = {
    val (a,r) = remIterList match {
      case h :: t => nextOfCandidate(h, t, Nil)
      case _      => throw new NoSuchElementException("next on empty StreamFolding")
    }
    this.remIterList = r
    assert(invariance)
    a
  }

  /** Bubble to the top the iterator having the lowest item among the heads
   *  of all iterators, popping and returning the lowest head item and
   *  the new replacement list of iterators.
   *  The lowest item is determined by the `tComp` Ordering
   */
  def nextOfCandidate(
    best: BufferedIterator[A],
    rest: List[BufferedIterator[A]],
    accum: List[BufferedIterator[A]]) : (A,List[BufferedIterator[A]]) =
    rest match {
      case h :: t  =>
        if (tComp.compare(best.head, h.head) <= 0)
          // best.head < rest.head, so best is still best
          nextOfCandidate(best, t, h :: accum)
        else
          // best.head > rest.head so rest.head is the new best
          nextOfCandidate(h, t, best :: accum)
      case Nil => {
        // ran to the end, best is the one.
        // pop best's value and add best back in only if it still isn't empty
        val r = best.next()
        var nl = if (best.hasNext) best :: accum else accum
        (r, nl)
      }
    }

}

object StreamFolding {

  /** Interleave a sequence of iterators producing a combined iterator. */
  def interleave[A](seq: Seq[Iterator[A]])(implicit tComp: Ordering[A]): Iterator[A] =
    new StreamFolding(seq)(tComp)

  /** Interleave a sequence of streams producing a combined stream. */
  def interleave[A](seq: Seq[Stream[A]])(implicit tComp: Ordering[A]): Stream[A] =
    interleave(seq.map(_.toIterator))(tComp).toStream

}
