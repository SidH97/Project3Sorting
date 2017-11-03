/**
 * 
 */

/**
 * This class will test our buffer class.
 *
 * @author Siddharth Hingorani (sid97)
 * @author Matthew Evans (matce93)
 * @version 11.01.2017
 *
 */
public class BufferTest extends student.TestCase
{
    private Buffer buff;

    /**
     * This method will setUp the testing Environment.
     */
    public void setUp()
    {
        buff = new Buffer();
    }

    /**
     * This method will test the constructor.
     */
    public void testConstruct()
    {
        assertFalse(buff.isDirty());
        assertEquals(0, buff.getPosition());
    }

    /**
     * This method will test our getter and setters
     */
    public void testGetsets()
    {
        buff.setToDirty();
        assertTrue(buff.isDirty());
        buff.setPosition(100);
        assertEquals(100, buff.getPosition());

        byte[] test = new byte[409];

        Exception ex = null;
        try
        {
            buff.setData(test);
        }
        catch (Exception e)
        {
            ex = e;
        }

        assertNotNull(ex);
    }

}
