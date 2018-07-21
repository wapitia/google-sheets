package com.wapitia
package common

object TestStreamFolding extends App {

  val list1 = List[Int](6, 8, 10, 11)
  val list2 = List[Int](2, 7, 12, 18)
  val list3 = List[Int](1, 9, 13, 14)
  val list4 = List[Int](4)

  {
    val foldLists: List[Stream[Int]] = List(list1.toStream, list2.toStream, list3.toStream, list4.toStream);
    isTester(foldLists, List(1,2,4,6,7,8,9,10,11,12,13,14,18))
  }

  {
    val foldLists = List[Stream[Int]]()
    isTester(foldLists, Nil)
  }

  {
    val foldLists = List[Stream[Int]](List[Int](4).toStream,List[Int](1).toStream,List[Int]().toStream)
    isTester(foldLists, List(1,4))
  }

  def isTester(foldLists: List[Stream[Int]], exp : List[Int]) {
    val re : Stream[Int] = StreamFolding.interleave[Int](foldLists)
    val relist = re.toList
    assert(relist == exp)
  }

}
