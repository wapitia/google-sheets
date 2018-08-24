package com.wapitia.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

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
	public static <K,V> void addToMapOfLists(
			final Map<K,List<V>> map, 
			final K key, 
			V value, 
			final Function<K,List<V>> listMaker) {
		Collections.<K,List<V>> getOrCreateMapValue(map, key, listMaker).add(value);
	}
	
	/**
	 * Get or create the value from the given map, or create and install a new one if it doesn't yet exist.
	 * 
	 * @param <K> Map's key type
	 * @param <C> Map's value type
	 * 
	 * @param map the source map from which to obtain the value or in which to add a newly created value.
	 * @param key the key to the entry in the map from which to return its value.
	 * @param defaultValueProvider function taking a key to generate and return a default value
	 *            when there is not a pre-existing value at key in the map.
	 */
	public static <K,C> C getOrCreateMapValue(Map<K,C> map, K key, Function<K,C> defaultValueProvider) {
		return Optional.ofNullable(map.get(key))
			.orElseGet(new MapUpdater<K,C>(map,key,defaultValueProvider));
	}
	
	/**
	 * Supplier of a default Map Value, having the side effect of creating and replacing
	 * an entry in a Map.
	 *
	 * @param <K> Map's key type
	 * @param <C> Map's value type
	 */
	static class MapUpdater<K,C> implements Supplier<C> {

		final Map<K,C> map; 
		final K key;
		final Function<K,C> defaultValueProvider;
		
		/**
		 * 
		 * @param map the source map from which to obtain the value or in which to add a newly created value.
		 * @param key the key to the entry in the map from which to return its value.
		 * @param defaultValueProvider function taking a key to generate and return a default value
		 *            when there is not a pre-existing value at key in the map.
		 */
		MapUpdater(Map<K,C> map, K key, Function<K,C> defaultValueProvider) {
			this.map = map;
			this.key = key;
			this.defaultValueProvider = defaultValueProvider;
		}
		
		/**
		 * Create a new value from the defaultValueProvider, add it to the map at the given key,
		 * and return that new value.
		 */
		@Override
		public C get() {
			final C newItem = defaultValueProvider.apply(key); 
			map.put(key, newItem); 
			return newItem; 
		}
		
	}
	
}
