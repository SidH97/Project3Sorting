import java.io.IOException;

import junit.framework.TestCase;

/**Test class for sorter
 * 
 * @author sid97
 * @author matce93
 * @version 2017.11.13
 *
 */
public class SorterTest extends TestCase
{
    public void setUp()
    {
        // Purposely left blank
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
        sorty.mergeSort();
        assertEquals(3, sorty.setRun(3));
        sorty.mergeSort();

        assertEquals(4, sorty.setRun(4));
        sorty.mergeSort();

        assertEquals(5, sorty.setRun(5));
        sorty.mergeSort();

        assertEquals(6, sorty.setRun(6));
        sorty.mergeSort();

        assertEquals(7, sorty.setRun(7));
        sorty.mergeSort();

        assertEquals(8, sorty.setRun(8));
        sorty.mergeSort();
        assertNotNull(sorty);
    }
    
    public void testParentInde() throws IOException
    {
    	Sorter sorty = new Sorter("8block", "statistics.txt");
    	assertEquals(1,sorty.parentIndex(3));
    }

    public void testGetKey() throws IOException
    {
    	Sorter sorty = new Sorter("8block", "statistics.txt");
    	assertNotNull(sorty.getKey(0));
    }
    
    public void testGetInputKey() throws IOException
    {
    	Sorter sorty = new Sorter("8block", "statistics.txt");
    	assertNotNull(sorty.getInputKey(0));
    }
    
    public void testGetOutputKey() throws IOException
    {
    	Sorter sorty = new Sorter("8block", "statistics.txt");
    	assertNotNull(sorty.getOutputKey(0));
    }
}
