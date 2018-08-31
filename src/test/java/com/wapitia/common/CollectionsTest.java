package com.wapitia.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class CollectionsTest {

	@Test
	public void testMapOfLists() {
		Map<Integer,List<String>> myMap = new HashMap<>();
		
		Collections.addToMapOfLists(myMap, 23, "Twenty-Three A");
		Collections.addToMapOfLists(myMap, 23, "Twenty-Three B");
		Collections.addToMapOfLists(myMap, 129, "One-Twenty-Nine A");
		Assert.assertEquals(2, myMap.entrySet().size());
		List<String> list23 = myMap.get(23);
		Assert.assertNotNull(list23);
		Assert.assertArrayEquals(new String[] {"Twenty-Three A", "Twenty-Three B"}, list23.toArray());
		List<String> list129 = myMap.get(129);
		Assert.assertNotNull(list129);
		Assert.assertArrayEquals(new String[] {"One-Twenty-Nine A"}, list129.toArray());
	}
}
