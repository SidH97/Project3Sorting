/**
 * 
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * This class will sort the parsed data given.
 *
 * @author Siddharth Hingorani (sid97)
 * @author Matthew Evans (matce93)
 * 
 * @version 10.28.2017
 *
 */
public class Sorter
{
    private static final int BUFFBYTES = 4096;
    private byte[] heap;
    private byte[] in;
    private byte[] out;
    private int inputIndex;
    private int outputIndex;
    private int frontIndex;
    private int fileInCheck;
    ByteBuffer inBuffer;
    ByteBuffer outBuffer;
    ByteBuffer heapBuffer;
    RandomAccessFile file;
    FileChannel wChannel;

    Sorter(String records, String stats) throws FileNotFoundException
    {
    	File runfile = new File("run.txt");
        wChannel = new FileOutputStream(runfile, true).getChannel();
        file = new RandomAccessFile(records, "r");
        in = new byte[BUFFBYTES];
        out = new byte[BUFFBYTES];
        heap = new byte[8 * BUFFBYTES];
        try
        {
            fileInCheck = file.read(heap);
        }
        catch (IOException e)
        {
            System.out.println(e.toString());
        }
        inBuffer = ByteBuffer.wrap(in);
        outBuffer = ByteBuffer.wrap(out);
        heapBuffer = ByteBuffer.wrap(heap);
        inputIndex = 0;
        outputIndex = 0;
        frontIndex = 0;
    }

    //this needs to change
    private long removeInputBuffer(int index)
    {
        inputIndex++;
        return inBuffer.getLong((index * 8));
    }

    //this has been changed
    private void insertInputBuffer(long l)
    {
        inBuffer.putLong((frontIndex * 8), l);
        frontIndex++;
    }

    //this needs to change
    private void cleanInputBuffer()
    {
        for (int i = frontIndex; i >= 0; i--)
        {
        	heapBuffer.putLong(((511 - i) * 8), removeInputBuffer(i));
        }
        heapify();
        frontIndex = 0;
    }

    //this does not need to be changed
    private boolean isInputEmpty()
    {
        return inputIndex == 512;
    }

    //this has been changed
    private void insertOutputBuffer(long l)
    {
        outBuffer.putLong((outputIndex * 8), l);
        outputIndex++;
        if (outputIndex == 512)
        {
            sendOutputBuffer();
        }
    }

    private void sendOutputBuffer()
    {
        outputIndex = 0;
        cleanInputBuffer();
        try {
			wChannel.write(outBuffer);
		} catch (IOException e) {
			System.out.println(e.toString());
		}
        outBuffer.clear();
    }

    //this should be changed to work
    private void minHeapify(int index)
    {
        long hold = heapBuffer.getLong(index);
        float key = getKey(index);
        //parent is multiplied by 8 to get actual index
        while ((index > 0) && (key < getKey(parentIndex(index) * 8)))
        {
        	heapBuffer.putLong((index * 8), heapBuffer.getLong(parentIndex(index) * 8));
        	index = parentIndex(index);
        }
        heapBuffer.putLong((index*8), hold);
    }
    
    //gets the 4 bytes of the key
    private float getKey(int index)
    {
    	//adds four to get the second half of record
    	return heapBuffer.getFloat(index + 4);
    }

    //This is based on the 512 index so this will work
    private int parentIndex(int indexChild)
    {
        return ((indexChild - 1) / 2);
    }

    private void heapify()
    {
        minHeapify(0);
    }

    private float getInputKey(int index)
    {
    	return inBuffer.getFloat(index + 4);
    }
    
    private float getOutputKey(int index)
    {
    	return outBuffer.getFloat(index+ 4);
    }
    
    public void replacementSelection()
    {
        while (fileInCheck != -1)
        {
            while (!isInputEmpty())
            {
                heapify();
                insertOutputBuffer(heapBuffer.getLong(0));
                float key = getInputKey(inputIndex);
                long hold = removeInputBuffer(inputIndex);
                if (key < getOutputKey(outputIndex - 1))
                {
                    insertInputBuffer(hold);
                }
                else
                {
                    heapBuffer.putLong(0, hold);
                }
            }
            getNewInput();
        }
        heapify();
        int i = 0;
        while (i < 4092)
        {
            insertOutputBuffer(heapBuffer.getLong(i * 8));
            i++;
        }
    }

    public void getNewInput()
    {
    	try 
    	{
			fileInCheck = file.read(in);
			inBuffer = ByteBuffer.wrap(in);
		} 
    	catch (IOException e) 
    	{
			System.out.println(e.toString());
		}
    }
}
