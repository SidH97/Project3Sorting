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

    private byte removeInputBuffer(int index)
    {
        inputIndex++;
        return in[index];
    }

    private void insertInputBuffer(byte b)
    {
        inBuffer.put(frontIndex, b);
        frontIndex++;
    }

    private void cleanInputBuffer()
    {
        for (int i = frontIndex; i >= 0; i--)
        {
        	heapBuffer.put(511 - i, removeInputBuffer(i));
        }
        heapify();
        frontIndex = 0;
    }

    private boolean isInputEmpty()
    {
        return inputIndex == 512;
    }

    private void insertOutputBuffer(byte b)
    {
        outBuffer.put(outputIndex, b);
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

    private void minHeapify(int index)
    {
        byte hold = heapBuffer.get(index);
        while ((index > 0) && (hold < heapBuffer.get(parentIndex(index))))
        {
            heapBuffer.put(index, heapBuffer.get(parentIndex(index)));
            index = parentIndex(index);
        }
        heapBuffer.put(index, hold);
    }

    private int parentIndex(int indexChild)
    {
        return ((indexChild - 1) / 2);
    }

    private void heapify()
    {
        minHeapify(0);
    }

    public void replacementSelection()
    {
        while (fileInCheck != -1)
        {
            while (!isInputEmpty())
            {
                heapify();
                insertOutputBuffer(heap[0]);
                byte temp = removeInputBuffer(inputIndex);
                if (temp < outBuffer.get(outputIndex - 1))
                {
                    insertInputBuffer(temp);
                }
                else
                {
                    heapBuffer.put(0, temp);
                }
            }
            getNewInput();
        }
        heapify();
        int i = 0;
        while (i < 4092)
        {
            insertOutputBuffer(heapBuffer.get(i));
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
