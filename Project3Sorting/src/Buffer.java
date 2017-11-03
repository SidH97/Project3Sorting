
/**
 * @author Sid Hingorani (sid97)
 * @author Matthew Evans (matce93)
 * 
 * @version 11.01.2017
 */
public class Buffer
{

    public static final int BUFFER_SIZE = 4096;

    private long position;
    private byte[] data;
    private boolean dirty;

    /**
     * This is the default constructor.
     */
    public Buffer()
    {
        data = new byte[BUFFER_SIZE];
        dirty = false;
        position = 0;
    }

    /**
     * Sets the data of the buffer.
     * 
     * @param input
     *            the data to be stored.
     */
    public void setData(byte[] input)
    {
        System.arraycopy(input, 0, data, 0, BUFFER_SIZE);
    }

    /**
     * returns the byte array holding all the data.
     * 
     * @return The array holding the bytes.
     */
    public byte[] getData()
    {
        return data;
    }

    /**
     * Set's the buffers position
     * 
     * @param pos
     *            the value of the position
     */
    public void setPosition(long pos)
    {
        position = pos;
    }

    /**
     * returns the position the buffer is holding.
     * 
     * @return the long value of the position.
     */
    public long getPosition()
    {
        return position;
    }

    /**
     * Returns the status of the dirty field.
     * 
     * @return true if dirtybit is on.
     */
    public boolean isDirty()
    {
        return dirty;
    }

    /**
     * Sets the dirty field to true.
     */
    public void setToDirty()
    {
        dirty = true;
    }

}
