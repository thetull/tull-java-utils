package net.tullco.tullutils;

import java.util.List;

public class ListUtils {
	/**
	 * Adds the items from list 2 to the end of list 1.
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
	 * @param baseList The list to change.
	 * @param goalList The list containing the goal elements.
	 */
	public static <T> void migrateList(List<T> baseList, List<T> goalList){
		baseList.retainAll(goalList);
		mergeListsUnique(baseList,goalList);
	}
}
