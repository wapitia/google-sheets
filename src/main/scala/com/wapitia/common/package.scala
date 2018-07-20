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
  
}