package net.tullco.tullutils.test_suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.tullco.tullutils.test_classes.FileUtilsTest;
import net.tullco.tullutils.test_classes.GraphTest;
import net.tullco.tullutils.test_classes.MergeUtilsTest;
import net.tullco.tullutils.test_classes.NullUtilsTest;
import net.tullco.tullutils.test_classes.PairTest;

@RunWith(Suite.class)
@SuiteClasses({ FileUtilsTest.class, GraphTest.class, MergeUtilsTest.class, NullUtilsTest.class, PairTest.class })
public class AllTests {

}
