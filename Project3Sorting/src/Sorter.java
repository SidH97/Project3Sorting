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
        inBuffer = ByteBuffer.wrap(in);
        outBuffer = ByteBuffer.wrap(out);
        heapBuffer = ByteBuffer.wrap(heap);
        inputIndex = 0;
        outputIndex = 0;
        frontIndex = 0;
        newReplacementSelection();
    }

    //this is changed
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
    public float getKey(int index)
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

    public float getInputKey(int index)
    {
    	return inBuffer.getFloat(index + 4);
    }
    
    public float getOutputKey(int index)
    {
    	return outBuffer.getFloat(index+ 4);
    }
    
    private boolean getNewHeap()
    {
    	try {
			fileInCheck = file.read(heap);
		} catch (IOException e) {
			System.out.println(e.toString());
		}
    	if (fileInCheck != -1)
    	{
    		heapBuffer = ByteBuffer.wrap(heap);
    		return true;
    	}
    	else
    	{
    		return false;
    	}
		
    }
    
    private void sendHeap()
    {
        try {
			wChannel.write(heapBuffer);
		} catch (IOException e) {
			System.out.println(e.toString());
		}
        heapBuffer.clear();
    }
    
    public void newReplacementSelection()
    {
    	while (getNewHeap())
    	{
    		heapify();
    		sendHeap();
    	}
    }
    
    public void replacementSelection()
    {
    	boolean check = true;
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
            if (frontIndex == 0)
            {
            	getNewInput();
            } else {
            	clearHeap();
            	cleanInputBuffer();
            	try {
					file.read(heap);
				} catch (IOException e) {
					System.out.println(e.toString());
				}
            }
            
        }
        
    }

    private void clearHeap()
    {
    	heapify();
        int i = 0;
        while (i < 4092)
        {
            insertOutputBuffer(heapBuffer.getLong(i * 8));
            i++;
        }
    }
    
    private void getNewInput()
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
    
    private void mergeSort()
    {
    	
    }
}
