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
}
