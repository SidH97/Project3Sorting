import java.io.IOException;

import junit.framework.TestCase;

/**
 * 
 */

/**
 * @author sidhingorani
 *
 */
public class SorterTest extends TestCase{
	Sorter sorter;
	public void setUp()
	{
		try {
			sorter = new Sorter("8block.bin", "stats.txt");
		} catch (IOException e) {
			System.out.println("nice try");
		}
	}
	
	public void testReplacementSelection()
	{
		assertTrue(sorter.replacementSelection() == 1);
	}

}
