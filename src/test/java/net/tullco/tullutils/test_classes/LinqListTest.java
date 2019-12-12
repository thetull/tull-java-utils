package net.tullco.tullutils.test_classes;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import net.tullco.tullutils.StringUtils;
import net.tullco.tullutils.listextensions.LinqList;

public class LinqListTest {

	LinqList<String> list;
	LinqList<Integer> numList;
	
	@Before
	public void setUp() throws Exception {
		list = new LinqList<String>();
		list.add("BQ");
		list.add("RU");
		list.add("OI");
		list.add("WC");
		list.add("NK");

		numList = new LinqList<Integer>();
		numList.add(1);
		numList.add(2);
		numList.add(3);
		numList.add(4);
		numList.add(5);
	}

	@Test
	public void testSelect() {
		LinqList<String> selectedString = list.<String>select((String s) -> {return StringUtils.left(s, 1);});
		assertEquals("B", selectedString.get(0));
		assertEquals("R", selectedString.get(1));
		assertEquals("O", selectedString.get(2));
		assertEquals("W", selectedString.get(3));
		assertEquals("N", selectedString.get(4));
		
		selectedString = list.<String>select((String s) -> {return StringUtils.right(s, 1);});
		assertEquals("Q", selectedString.get(0));
		assertEquals("U", selectedString.get(1));
		assertEquals("I", selectedString.get(2));
		assertEquals("C", selectedString.get(3));
		assertEquals("K", selectedString.get(4));
	}

	@Test
	public void testWhere() {
		LinqList<String> selectedString = list.where((String s) -> {return s.startsWith("B") || s.endsWith("K");});
		assertEquals(2, selectedString.size());
		assertEquals("BQ", selectedString.get(0));
		assertEquals("NK", selectedString.get(1));
	}
	
	@Test
	public void testCount() {
		assertEquals(5, list.count((String s) ->{return true;}));
		assertEquals(2, list.count((String s) ->{return s.startsWith("B") || s.endsWith("K");}));
	}
	
	@Test
	public void testSum() {
		assertEquals(5, Math.round(numList.sum((Integer s) ->{return 1d;})));
		assertEquals(15, Math.round(numList.sum((Integer s) ->{return (double)s;})));
	}
	
	@Test
	public void testAverage() {
		assertEquals(1, Math.round(numList.average((Integer s) ->{return 1d;})));
		assertEquals(3, Math.round(numList.average((Integer s) ->{return (double)s;})));
	}
	
	@Test
	public void testMax() {
		assertEquals(1, Math.round(numList.max((Integer s) ->{return 1d;})));
		assertEquals(5, Math.round(numList.max((Integer s) ->{return (double)s;})));
	}
	
	@Test
	public void testMin() {
		assertEquals(1, Math.round(numList.min((Integer s) ->{return 1d;})));
		assertEquals(1, Math.round(numList.min((Integer s) ->{return (double)s;})));
	}

	@Test
	public void testReverse() {
		LinqList<String> reversedString = list.reverse();
		assertEquals("BQ", reversedString.get(4));
		assertEquals("RU", reversedString.get(3));
		assertEquals("OI", reversedString.get(2));
		assertEquals("WC", reversedString.get(1));
		assertEquals("NK", reversedString.get(0));
	}
	
	@Test
	public void testFirst(){
		assertEquals("BQ", list.first());
	}
	
	@Test
	public void testLast(){
		assertEquals("NK", list.last());
	}
	
	@Test
	public void testConvertToLinqList(){
		List<Integer> genericList = numList;
		assertEquals(numList, genericList);
	}

}
