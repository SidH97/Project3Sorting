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
	public void setUp()
	{
		//purposly lefty blsnk
	}
	
	public void testReplacementSelection() throws IOException
	{
		Sorter sorty = new Sorter("8block", "statistics.txt");
		assertNotNull(sorty);
		assertEquals(1, sorty.replacementSelection());
	}
	
	public void testNewReplacementSelection1() throws IOException
	{
		Sorter sorty = new Sorter("8block", "statistics.txt");
		sorty.newReplacementSelection();
		assertNotNull(sorty);
	}


}
