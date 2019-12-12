package net.tullco.tullutils.test_classes;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import net.tullco.tullutils.ListUtils;

public class ListUtilsTest {
	public List<String> list1;
	public List<String> list2;

	@Before
	public void setUp() throws Exception {
		list1 = new ArrayList<String>();
		list2 = new ArrayList<String>();
		list1.add("oops");
		list1.add("lol");
		list2.add("oops");
		list2.add("fun");
	}
	
	@Test
	public void listMergeUniqueTest(){
		assertEquals(2,list1.size());
		assertEquals(2,list2.size());
		ListUtils.mergeListsUnique(list1, list2);
		assertEquals(3,list1.size());
		assertEquals("oops",list1.get(0));
		assertEquals("lol",list1.get(1));
		assertEquals("fun",list1.get(2));
	}

	@Test
	public void migrateListTest(){
		assertEquals(2,list1.size());
		assertEquals(2,list2.size());
		ListUtils.migrateList(list1,list2);
		assertEquals(2,list1.size());
		assertEquals("oops",list1.get(0));
		assertEquals("fun",list1.get(1));
	}
	
	
	@Test
	public void testIntersect(){
		List<String> numListOutput = ListUtils.intersect(list1, list2);
		assertEquals(1, numListOutput.size());
		assertEquals("oops", numListOutput.get(0));
		

		numListOutput = ListUtils.intersect(list2, list1);
		assertEquals(1, numListOutput.size());
		assertEquals("oops", numListOutput.get(0));
	}
	
	@Test
	public void testExcept(){
		List<String> numListOutput = ListUtils.except(list1, list2);
		assertEquals(1, numListOutput.size());
		assertEquals("lol", numListOutput.get(0));
		

		numListOutput = ListUtils.except(list2, list1);
		assertEquals(1, numListOutput.size());
		assertEquals("fun", numListOutput.get(0));
	}
	
	@Test
	public void testUnion(){
		List<String> numListOutput = ListUtils.union(list1, list2);
		assertEquals(3, numListOutput.size());
		assertEquals("oops", numListOutput.get(0));
		assertEquals("lol", numListOutput.get(1));
		assertEquals("fun", numListOutput.get(2));
		

		numListOutput = ListUtils.union(list2, list1);
		assertEquals(3, numListOutput.size());
		assertEquals("oops", numListOutput.get(0));
		assertEquals("fun", numListOutput.get(1));
		assertEquals("lol", numListOutput.get(2));
	}
	
	@Test
	public void testConcat(){
		List<String> numListOutput = ListUtils.concatenate(list1, list2);
		assertEquals(4, numListOutput.size());
		assertEquals("oops", numListOutput.get(0));
		assertEquals("lol", numListOutput.get(1));
		assertEquals("oops", numListOutput.get(2));
		assertEquals("fun", numListOutput.get(3));
		

		numListOutput = ListUtils.concatenate(list2, list1);
		assertEquals(4, numListOutput.size());
		assertEquals("oops", numListOutput.get(0));
		assertEquals("fun", numListOutput.get(1));
		assertEquals("oops", numListOutput.get(2));
		assertEquals("lol", numListOutput.get(3));
	}
}
