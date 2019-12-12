package net.tullco.tullutils.test_suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.tullco.tullutils.test_classes.AESEncryptionTest;
import net.tullco.tullutils.test_classes.FileUtilsTest;
import net.tullco.tullutils.test_classes.GraphTest;
import net.tullco.tullutils.test_classes.LinqListTest;
import net.tullco.tullutils.test_classes.ListUtilsTest;
import net.tullco.tullutils.test_classes.MergeUtilsTest;
import net.tullco.tullutils.test_classes.NullUtilsTest;
import net.tullco.tullutils.test_classes.PairTest;
import net.tullco.tullutils.test_classes.StopwatchTest;
import net.tullco.tullutils.test_classes.StringUtilsTest;

@RunWith(Suite.class)
@SuiteClasses({ 
	FileUtilsTest.class
	,GraphTest.class
	,MergeUtilsTest.class
	,NullUtilsTest.class
	,PairTest.class
	,StringUtilsTest.class
	,ListUtilsTest.class
	,StopwatchTest.class
	,AESEncryptionTest.class
	,LinqListTest.class
	})
public class AllTests {

}
