package com.wapitia.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class Collections {

	/** Add a new list item to a list in a map for a particular key.
	 *  The map is of the form `Map<K,List<V>>` for keys of type `K` and values of type `V`.
	 *  The given value is appended to the list in the map for a given key K.
	 *  If the entry does not yet exist, a new ArrayList is created for that slot and value 
	 *  becomes the first and only entry in the new list.  
	 */
	public static <K,V> void addToMapOfLists(final Map<K,List<V>> map, K key, V value) {
		addToMapOfLists(map, key, value, (K k) -> new ArrayList<V>());
	}
	
	/** Add a new list item to a list in a map for a particular key.
	 *  The map is of the form `Map<K,List<V>>` for keys of type `K` and values of type `V`.
	 *  The given value is appended to the list in the map for a given key K.
	 *  If the entry does not yet exist, the given listMaker supplier of Lists will be invoked
	 *  to create a new list for that slot and the value becomes the first and only
	 *  entry in the new list.
	 */
	public static <K,V> void addToMapOfLists(final Map<K,List<V>> map, K key, V value, final Function<K,List<V>> listMaker) {
		Collections.<K,List<V>> getOrCreateMapValue(map, key, listMaker).add(value);
	}
	
	/**
	 * Get or create the value from the given map, or create and install a new one if it doesn't yet exist.
	 */
	public static <K,V> V getOrCreateMapValue(final Map<K,V> map, final K key, final Function<K,V> defaultValue) {
		return Optional.ofNullable(map.get(key)).orElseGet(() -> { 
			final V newItem = defaultValue.apply(key); 
			map.put(key, newItem); 
			return newItem; 
		});
	}
}
