package com.wapitia
package common

/** Combine a sequence of iterators into one grand iterator.
 *  The iterator picks the "lowest" head among its set of iterators until
 *  all of its iterators are exhausted.  The ordering of the iterator elements
 *  is provided by an implicit parameter.
 *
 *  @note that the comparison is done on just the heads of these iterators,
 *  and if the elements in each iterator are not themselves sorted,
 *  then the output of this iterator will not be sorted either.
 *
 *  @tparam A common type of all iterators, whose elements will be compared
 *            against each other.
 *  @constructor makes a new `Iterator[A]` wrapping the given sequence
 *               of iterators in `iters`, using the implicit ording
 *               to compare those iterators' head elements.
 *  @param iters collection of iterators, each of which will be emptied
 *               as this stream folding iterator steps through them.
 *  @param tComp implicit Ordering used to compare the heads of the iterators.
 *  @return Iterator[A] delivering each element of the given iterators
 *               from `iter` in turn according to the implicit ordering.
 */
class StreamFolding[A](iters: Seq[Iterator[A]])(implicit tComp: Ordering[A]) extends Iterator[A] {

  /** remIterList is variable because the list is replaced by function
   *  [[next()]] as recent iterators bubble to the top and as iterators
   *  become exhausted.
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
    // List constructor will fail if no iterators to draw from
    val hIter :: tIter =  nextR(remIterList, Nil)
    val res = hIter.next()
    this.remIterList = if (hIter.isEmpty) tIter else hIter :: tIter
    assert(invariance)
    res
  }

  /** Bubble to the top the iterator having the lowest item among the heads
   *  of all iterators returning the resultant new List.
   *  The lowest item is determined by the `tComp` Ordering
   */
  def nextR(
    rest: List[BufferedIterator[A]],
    accum: List[BufferedIterator[A]]) : List[BufferedIterator[A]] =
    rest match {
      // list has multiple items, compare the heads of the top two iterators
      case h1 :: h2 :: t  =>
        if (tComp.compare(h1.head, h2.head) <= 0)
          // first <= second, so first survives and second is accumulated
          nextR(h1 :: t, h2 :: accum)
        else
          // first > second, so second survives and first is accumulated
          nextR(h2 :: t, h1 :: accum)

      // list has just one iterator left, having survived all comparisons
      case h :: Nil => h :: accum

      // only ever gets here if originally invoked with Nil, which shouldn't
      // happen, but just return accum, which should also be Nil
      case Nil => accum
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
