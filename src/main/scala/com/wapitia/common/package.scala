package com.wapitia

/** Common package level functions and constants. */
package object common {

  /** Repackage and return the sequence of items as a sorted, monotonically
   *  increasing list of those items without duplicates.
   *  Uniqueness is determined by the '==' operator  and the implicit ClassTag.
   *  The ordering is determined by implicit `math.Ordering[T]`
   *
   *  @param seq a sequence of any type
   *
   */
  def canonicalList[A](seq: A*)(implicit arg0: math.Ordering[A], m: scala.reflect.ClassTag[A]): List[A] = {
    val array: Array[A] = seq.toArray
    util.Sorting.quickSort(array)
    array.foldRight(List.empty[A]) {
      case (item, acc@(h :: t)) if item == h => acc         // ignore duplicates
      case (item, acc)                       => item :: acc // otherwise include the item. acc may be Nil.
    }
  }

  /** map a sequence of AnyRef's into a sequence of strings via their toString method
   *
   *  Note:
   *    This is NOT that same declaration as def stringsOf(items: AnyRef*) .
   */
//  def stringsOf(items: AnyRef*): Seq[String] = items.map(_.toString)
  def stringsOf(items: Seq[AnyRef]): Seq[String] = items.map(_.toString)

  /** Bubble to the top the lowest element according to the
   *  given `comp` comparator function.
   */
  def bubbleUp[E](rest: List[E], comp: (E,E) => Int): List[E] =
    bubbleUp[E](rest, Nil.asInstanceOf[List[E]], comp)

  def bubbleUp[E](rest: List[E], accum: List[E], comp: (E,E) => Int): List[E] =
  rest match {
    // list has multiple items, compare the top two
    case h1 :: h2 :: t  =>
      if (comp(h1, h2) <= 0)
        // first <= second, so first survives and second is accumulated
        bubbleUp(h1 :: t, h2 :: accum, comp)
      else
        // first > second, so second survives and first is accumulated
        bubbleUp(h2 :: t, h1 :: accum, comp)

    // list has just one element, having survived all comparisons, if any
    // it's returned as the top of the accumulated list
    case h :: Nil => h :: accum

    // only ever gets here if originally invoked with `List()`,
    // since the recursive calls never send `Nil` as the first element
    case Nil => accum
  }

}
