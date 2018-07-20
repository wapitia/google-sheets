package com.wapitia
package common

object TestStreamFolding extends App {
  
  
  val list1 = List[Int](6, 8, 10, 11)
  val list2 = List[Int](2, 7, 12, 18)
  val list3 = List[Int](1, 9, 13, 14)
  val list4 = List[Int](4)
  
//  val list1 = List[Int](1)
//  val list2 = List[Int](2)
//  val list3 = List[Int](3)
   
//  val foldLists = List(list1.toStream, list2.toStream, list3.toStream);
//  val foldLists = List(list2.toStream);
//  val foldLists = List(list0.toStream, list1.toStream);
  {
    val foldLists = List(list1.toStream, list2.toStream, list3.toStream, list4.toStream);
    val re : Stream[Int] = StreamFolding.interleave[Int](foldLists)
    re foreach {
      a =>
        println(a.toString())
    }
  }
  
  {
    val foldLists = List[Stream[Int]]()
    val re : Stream[Int] = StreamFolding.interleave[Int](foldLists)
    re foreach {
      a =>
        println(a.toString())
    }
  }
  
  {
    val foldLists = List[Stream[Int]](List[Int](4).toStream,List[Int](1).toStream,List[Int]().toStream)
    val re : Stream[Int] = StreamFolding.interleave[Int](foldLists)
    re foreach {
      a =>
        println(a.toString())
    }
  }
  
}