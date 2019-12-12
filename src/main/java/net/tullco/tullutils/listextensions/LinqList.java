package net.tullco.tullutils.listextensions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import net.tullco.tullutils.ListUtils;

public class LinqList<E> extends ArrayList<E>{
	
	private static final long serialVersionUID = -4390397743566086419L;
	
	/**
	 * Creates a LinqList backed by an ArrayList
	 */
	public LinqList(){
		super();
	}
	
	public LinqList(Collection<? extends E> c){
		super(c);
	}
	
	public LinqList(int initialCapacity){
		super(initialCapacity);
	}
	
	/**
	 * Returns a new LinqList containing only the elements that match the filter statement.
	 * @param filter The predicate to be used in the filtering.
	 * @return A new list containing the matching elements.
	 */
	public LinqList<E> where(Predicate<E> filter){
		LinqList<E> result = new LinqList<E>();
		for(E item: this){
			if (filter.test(item))
				result.add(item);
		}
		return result;
	}
	
	/**
	 * Counts the elements that match the filter statement.
	 * @param filter The predicate to be used in the filtering.
	 * @return The number of matching items in the list.
	 */
	public int count(Predicate<E> filter){
		int i = 0;
		for(E item: this){
			if (filter.test(item))
				i++;
		}
		return i;
	}
	
	/**
	 * The first item in the list. Fails if the list is empty.
	 * @return The last item in the list.
	 */
	public E first(){
		return this.get(0);
	}
	
	/**
	 * The last item in the list. Fails if the list is empty.
	 * @return The last item in the list.
	 */
	public E last(){
		return this.get(this.size() - 1);
	}
	
	/**
	 * Returns a new list that has the result of the given function executed on each element of the list.
	 * @param function The function to execute on the elements in the list.
	 * @return The new list containing the results.
	 */
	public <T> LinqList<T> select(Function<E, T> function){
		LinqList<T> result = new LinqList<T>();
		for(E item: this){
			result.add(function.apply(item));
		}
		return result;
	}
	
	/**
	 * Gets the maximum value of the given function when it is evaluated on all the items in the list 
	 * @param function The function to get the maximum of
	 * @return The maxmimum value of the function results on the inputs from the list
	 */
	public double max(Function<E, Double> function){
		double currentMax = Double.MIN_VALUE;
		for(E item: this){
			double value = function.apply(item);
			if (value > currentMax)
				currentMax = value;
		}
		return currentMax;
	}
	
	/**
	 * Gets the sum of the given function when it is evaluated on all the items in the list 
	 * @param function The function to sum
	 * @return The sum of the function results on the inputs from the list
	 */
	public double sum(Function<E, Double> function){
		double currentSum = 0;
		for(E item: this){
			currentSum += function.apply(item);
		}
		return currentSum;
	}

	/**
	 * Gets the average of the given function when it is evaluated on all the items in the list 
	 * @param function The function to average
	 * @return The average of the function results on the inputs from the list
	 */
	public double average(Function<E, Double> function){
		int currentSum = 0;
		int currentCount = 0;
		for(E item: this){
			currentSum += function.apply(item);
			currentCount++;
		}
		return currentSum / currentCount;
	}

	/**
	 * Gets the min of the given function when it is evaluated on all the items in the list 
	 * @param function The function to min
	 * @return The min of the function results on the inputs from the list
	 */
	public double min(Function<E, Double> function){
		double currentMin = Double.MAX_VALUE;
		for(E item: this){
			double value = function.apply(item);
			if (value < currentMin)
				currentMin = value;
		}
		return currentMin;
	}
	
	/**
	 * Returns the list after skipping over the given number of elements
	 * @param elementsToSkip The number of elements to skip
	 * @return The skipped list.
	 */
	public LinqList<E> skip(int elementsToSkip) {
		LinqList<E> result = new LinqList<>();
		for (int i = elementsToSkip; i < this.size(); i++)
			result.add(this.get(i));
		return result;
	}
	
	/**
	 * Returns a new list with the same elements in the reverse order.
	 * @return The new list with the elements in reverse.
	 */
	public LinqList<E> reverse() {
		return ConvertToLinqList(ListUtils.reverse(this));
	}
	
	/**
	 * Takes a given list and converts it into a LinqList
	 * @param listToConvert The list to convert
	 * @return A LinqList with the same elements as the input list.
	 */
	public static <T> LinqList<T> ConvertToLinqList(List<T> listToConvert){
		return new LinqList<>(listToConvert);
	}
}
