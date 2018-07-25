package com.wapitia
package common

/** Combine a sequence of iterators into one grand iterator.
 *  The `FoldingIterator` picks the "lowest" head among its set of iterators
 *  until all of its "child" iterators are exhausted.
 *  The ordering of the elements is provided by an implicit parameter.
 *
 *  @note the comparison is done on just the heads of these iterators,
 *  and if the elements in each iterator are not themselves sorted,
 *  then the output of this iterator will not be sorted either.
 *
 *  @tparam A common type of all iterators, whose elements will be compared
 *            against each other.
 *  @constructor makes a new `Iterator[A]` wrapping the given sequence
 *               of iterators in `iters`, using the implicit ordering
 *               to compare those iterators' head elements.
 *  @param iters collection of iterators, each of which will be emptied
 *               as this stream folding iterator steps through them.
 *  @param tComp implicit Ordering used to compare the heads of the iterators.
 *  @return Iterator[A] delivering each element of the given iterators
 *               from `iter` in turn according to the implicit ordering.
 */
class FoldingIterator[A](iters: Seq[Iterator[A]])(implicit tComp: Ordering[A]) extends Iterator[A] {

  /** "BI" is shorthand for BufferedIterator[A] used throughout this class */
  type BI = BufferedIterator[A]

  /** Remaining list of populated iterators from which to draw.
   *  This list is initialized from the given `iters`, wrapped
   *  in BufferedIterators and only those that are populated are
   *  retained.
   *  `remIters` is variable because the list is replaced by the [[next()]] 
   *  function as its iterator elements become empty.
   *  Each of the iterator items `iter` are wrapped as 
   *  `BufferedIterator`s (BI's) since we'll want to peek at the heads of the
   *  iterators while comparing them to others and before popping them.
   */
  private var remIters: List[BI] = iters.map(_.buffered).filter(_.hasNext).toList

  /** INVARIANCE REQUIREMENT: None of the BI's in `remIters` are empty. */
  private def invariance = ! remIters.exists(_.isEmpty)
  assert(invariance)  // initial condition going in

  /** There is a next item only when there are any remaining `Iterators`
   *  in `remIters` since any iterators in that collection are non-empty
   *  according to the invariance requirement.
   */
  override def hasNext: Boolean = ! remIters.isEmpty

  /** Get the next iteration element from its list of remaining iterators.
   *  This refreshes the remaining list of iterators, which is reduced as
   *  iterators become exhausted.
   */
  override def next(): A = {
    // List constructor will fail if no BI's remain (when remIters is empty)
    val top :: rest =
      bubbleUp[BI](remIters, (i: BI, j: BI) => tComp.compare(i.head, j.head))
    val res = top.next()
    // drop the top iterator if it's now empty
    this.remIters = if (top.isEmpty) rest else top :: rest
    assert(invariance)
    res
  }
}

object FoldingIterator {

  /** Interleave a sequence of iterators producing a combined iterator. */
  def interleave[A](seq: Seq[Iterator[A]])(implicit tComp: Ordering[A]): Iterator[A] =
    new FoldingIterator(seq)(tComp)

  /** Interleave a sequence of streams producing a combined stream. */
  def interleave[A](seq: Seq[Stream[A]])(implicit tComp: Ordering[A]): Stream[A] =
    interleave(seq.map(_.toIterator))(tComp).toStream

}
