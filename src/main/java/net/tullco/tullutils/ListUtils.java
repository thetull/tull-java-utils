package net.tullco.tullutils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 * This is a static class that has some helpful list methods
 * @author Tull Gearreald
 *
 */
public class ListUtils {
	/**
	 * Adds the items from list 2 that are not in list 1 to the end of list 1. Will not create additional duplication.
	 * @param <T> The list type. Must be consistent.
	 * @param list1 The list to modify.
	 * @param list2 The list containing the elements to add.
	 */
	public static <T> void mergeListsUnique(List<T> list1, List<T> list2){
		for(T item: list2){
			if(!list1.contains(item))
				list1.add(item);
		}
	}
	/**
	 * Takes the base list, and adds and removes elements from it until it contains the same elements as the goal list.
	 * No ordering is guaranteed.
	 * @param <T> The list type. Must be consistent.
	 * @param baseList The list to change.
	 * @param goalList The list containing the goal elements.
	 */
	public static <T> void migrateList(List<T> baseList, List<T> goalList){
		baseList.retainAll(goalList);
		mergeListsUnique(baseList,goalList);
	}
	
	/**
	 * Returns a new, deduplicated list that contains all the items that are in both lists.
	 * The items will be added in the order they are in l1
	 * @param l1 The first list to merge.
	 * @param l2 The second list to merge.
	 * @return A list containing any elements in the first list that are also in the second.
	 */
	public static <T> List<T> intersect(List<T> l1, List<T> l2){
		HashSet<T> listSet = new HashSet<T>(l2);
		HashSet<T> addedElements = new HashSet<T>();
		List<T> result = new ArrayList<T>();
		for(T item: l1){
			if (listSet.contains(item) && !addedElements.contains(item)){
				result.add(item);
				addedElements.add(item);
			}
		}
		return result;
	}
	
	/**
	 * Returns a new, deduplicated list containing all the items that are in either list
	 * The items will be added in the order they are in l1, and then l2.
	 * @param l1 The first list to merge.
	 * @param l2 The second list to merge.
	 * @return A list containing any elements in either list.
	 */
	public static <T> List<T> union(List<T> l1, List<T> l2){
		HashSet<T> addedElements = new HashSet<T>();
		List<T> result = new ArrayList<T>();
		for(T item: concatenate(l1, l2)){
			if (!addedElements.contains(item)){
				result.add(item);
				addedElements.add(item);
			}
		}
		return result;
	}
	
	/**
	 * Returns a new list containing all the items in l1 with l2 appended.
	 * The list will be in the order of L1 and then L2.
	 * @param l1 The first list to merge.
	 * @param l2 The second list to merge.
	 * @return A concatenated list.
	 */
	public static <T> List<T> concatenate(List<T> l1, List<T> l2){
		List<T> result = new ArrayList<T>(l1.size() + l2.size());
		result.addAll(l1);
		result.addAll(l2);
		return result;
	}
	
	/**
	 * Returns a de-duplicated list containing all the items in l1 that aren't in l2.
	 * The list will be in the order of l1.
	 * @param l1 The base list.
	 * @param l2 The list of things to ignore.
	 * @return A list containing all items in l1 that aren't in l2.
	 */
	public static <T> List<T> except(List<T> l1, List<T> l2){
		List<T> result = new ArrayList<T>();
		HashSet<T> exceptElements = new HashSet<T>(l2);
		HashSet<T> addedElements = new HashSet<T>();
		for(T item: l1){
			if (!exceptElements.contains(item) && !addedElements.contains(item)) {
				result.add(item);
				addedElements.add(item);
			}
		}
		return result;
	}
	
	/**
	 * Returns the list with the elements in reverse order.
	 * @param l1 The list to reverse
	 * @return The list in reverse order
	 */
	public static <T> List<T> reverse(List<T> l1){
		List<T> result = new ArrayList<T>();
		for(int i = l1.size() - 1; i >= 0; i--){
			result.add(l1.get(i));
		}
		return result;
	}
}
