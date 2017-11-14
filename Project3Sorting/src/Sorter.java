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
    private int run;
    private int test = 0;
    private int ct = 0;
    ByteBuffer inBuffer;
    ByteBuffer outBuffer;
    ByteBuffer heapBuffer;
    RandomAccessFile file;
    FileChannel wChannel;

    @SuppressWarnings("resource")
	Sorter(String records, String stats) throws IOException
    {
    	File runFile = new File("run.txt");
        wChannel = new FileOutputStream(runFile, true).getChannel();
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
        runFile = new File(records);
        runFile.delete();
        runFile = new File(records);
        wChannel = new FileOutputStream(runFile, true).getChannel();
        mergeSort();
        sysOut(records);
			wChannel.close();
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
    
    private void sysOut(String file) throws IOException
    {
		RandomAccessFile tempFile = new RandomAccessFile(file, "r");
    	for (int i = 0; i < run; i++)
    	{
    		heapBuffer.clear();
    		tempFile.read(heap);
    		heapBuffer = ByteBuffer.wrap(heap);
    		sendToSysOut(heapBuffer.getInt(0), getKey(0));
    		sendToSysOut(heapBuffer.getInt(4096), getKey(4096));
    		sendToSysOut(heapBuffer.getInt(8192), getKey(8192));
    		sendToSysOut(heapBuffer.getInt(12288), getKey(12288));
    		sendToSysOut(heapBuffer.getInt(16384), getKey(16384));
    		sendToSysOut(heapBuffer.getInt(20480), getKey(20480));
    		sendToSysOut(heapBuffer.getInt(24576), getKey(24576));
    		sendToSysOut(heapBuffer.getInt(28672), getKey(28672));
    	}
    	tempFile.close();
    	
    }
    
    private void sendToSysOut(int recordId, float key)
    {
    	if ((ct != 0) && (ct % 5 == 0))
    	{
    		System.out.println();
    	}
    	System.out.print(recordId + " " + key);
    	ct++;
    }
    
    public void newReplacementSelection()
    {
    	run = 0;
    	while (getNewHeap())
    	{
    		heapify();
    		sendHeap();
    		run++;
    	}
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
    
    private boolean nextInput(int index, int size, int runNum, int depth)
    {
    	try 
    	{
        	RandomAccessFile newFile = new RandomAccessFile("run.txt", "r");
    		index = index + (runNum * 4096);
    		long spot = (((runNum * 32768) - 32768) + ((depth * 512) - 512));
    		newFile.seek(spot);
			test = newFile.read(heap, index, size);
			inBuffer = ByteBuffer.wrap(in);
			if (test == -1)
			{
				return false;
			}
			else
			{
				return true;
			}
		} 
    	catch (IOException e) 
    	{
			System.out.println(e.toString());
		}
    	return false;
    	
    }
    
    public void mergeSort()
    {
    	if (run == 1) {
    		return;
    	}
    	else if (run == 2) {
    		//load into merge2
    		merge2(run * 4096);
    	}
    	else if (run == 3) {
    		merge3(run * 4096);
    	}
    	else if (run == 4) {
    		//load into merge4
    		merge4(run * 4096);
    	}
    	else if (run == 5) {
    		//load into heapBuffer
    		merge5(run * 4096);
    	}
    	else if (run == 6) {
    		//load into heapBuffer
    		merge6(run * 4096);
    	}
    	else if (run == 7) {
    		//load into heapBuffer
    		merge7(run * 4096);
    	}
    	else if (run == 8) {
    		//load into merge8
    		merge8(run * 4096);
    	}
    }
    
    private void merge2(int numRec) 
    {
    	int x1 = 0;
    	int hold2 = 2048;
    	int x2 = 2048;
    	for(int i = 0; i < numRec; i++)
    	{
    		if (x1 != -1)
    		{
    			if (getKey(x1) <= getKey(x2))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x1));
        			x1++;
        			if (x1 == hold2) {
        				//needs to get the next input and put it at the beginning (0)
        				if (nextInput(0, 2048, 1))
        				{
        					x1 = 0;
        				}
        				else
        				{
        					x1 = -1;
        				}
        			}
        		}
    		}
    		else if (x2 != -1)
    		{
    			if (getKey(x2) <= getKey(x1))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x2));
        			x2++;
        			if (x2 == 4096) {
        				//needs to get the next input and put it at the beginning (hold2)
        				if(nextInput(hold2, 2048, 2))
        				{
        					x2 = hold2;
        				}
        				else
        				{
        					x2 = -1;
        				}
        			}
        		}
    		}
    	}
    }
    
    private void merge3(int numRec)
    {
    	int x1 = 0;
    	int hold2 = 1024;
    	int x2 = 1024;
    	int hold3 = 2048;
        int x3 = 2048;
        for(int i = 0; i < numRec; i++)
    	{
    		if (x1 != -1)
    		{
    			if ((getKey(x1) <= getKey(x2))&&(getKey(x1) <= getKey(x3)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x1));
        			x1++;
        			if (x1 == hold2) {
        				//needs to get the next input and put it at the beginning (0)
        				if (nextInput(0, 1024, 1))
        				{
        					x1 = 0;
        				}
        				else
        				{
        					x1 = -1;
        				}
        			}
        		}
    		}
    		else if (x2 != -1)
    		{
    			if ((getKey(x2) <= getKey(x1))&&(getKey(x2) <= getKey(x3)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x2));
        			x2++;
        			if (x2 == hold3) {
        				//needs to get the next input and put it at the beginning (hold2)
        				if(nextInput(hold2, 1024, 2))
        				{
        					x2 = hold2;
        				}
        				else
        				{
        					x2 = -1;
        				}
        			}
        		}
    		}
    		else if (x3 != -1) 
    		{
 
        		insertOutputBuffer(heapBuffer.getLong(x3));
        		x3++;
        		if (x3 == 4096) {
        			//needs to get the next input and put it at the beginning (hold3)
        			if(nextInput(hold3, 2048, 3))
        			{
        				x3 = hold3;
        			}
        			else
        			{
        				x3 = -1;
        			}
        		}
    		}
    	}
    }

    
    private void merge4(int numRec)
    {
    	int x1 = 0;
    	int hold2 = 1024;
    	int x2 = 1024;
    	int hold3 = 2048;
        int x3 = 2048;
    	int hold4 = 3072;
        int x4 = 3072;
        for(int i = 0; i < numRec; i++)
    	{
    		if (x1 != -1)
    		{
    			if ((getKey(x1) <= getKey(x2))&&(getKey(x1) <= getKey(x3))&&
        				(getKey(x1) <= getKey(x4)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x1));
        			x1++;
        			if (x1 == hold2) {
        				//needs to get the next input and put it at the beginning (0)
        				if (nextInput(0, 1024, 1))
        				{
        					x1 = 0;
        				}
        				else
        				{
        					x1 = -1;
        				}
        			}
        		}
    		}
    		else if (x2 != -1)
    		{
    			if ((getKey(x2) <= getKey(x1))&&(getKey(x2) <= getKey(x3))&&
        				(getKey(x2) <= getKey(x4)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x2));
        			x2++;
        			if (x2 == hold3) {
        				//needs to get the next input and put it at the beginning (hold2)
        				if(nextInput(hold2, 1024, 2))
        				{
        					x2 = hold2;
        				}
        				else
        				{
        					x2 = -1;
        				}
        			}
        		}
    		}
    		else if (x3 != -1) 
    		{
    			if ((getKey(x3) <= getKey(x1))&&(getKey(x3) <= getKey(x2))&&
        				(getKey(x3) <= getKey(x4)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x3));
        			x3++;
        			if (x3 == hold4) {
        				//needs to get the next input and put it at the beginning (hold3)
        				if(nextInput(hold3, 1024, 3))
        				{
        					x3 = hold3;
        				}
        				else
        				{
        					x3 = -1;
        				}
        			}
        		}
    		}
    		else if (x4 != -1)
    		{
    			insertOutputBuffer(heapBuffer.getLong(x4));
        		x4++;
        		if (x4 == 4096) {
        			//needs to get the next input and put it at the beginning (hold4)
        			if(nextInput(hold4, 512, 4))
        		    {
        				x4 = hold4;
        			}
        			else
        			{
        				x4 = -1;
        			}
        		}
    		}
    	}
    }
    
    private void merge5(int numRec)
    {
    	int x1 = 0;
    	int hold2 = 1024;
    	int x2 = 1024;
    	int hold3 = 2048;
    	int x3 = 2048;
    	int hold4 = 3072;
    	int x4 = 3072;
    	int hold5 = 3584;
    	int x5 = 3584;
    	for(int i = 0; i < numRec; i++)
    	{
    		if (x1 != -1)
    		{
    			if ((getKey(x1) <= getKey(x2))&&(getKey(x1) <= getKey(x3))&&
        				(getKey(x1) <= getKey(x4))&&(getKey(x1) <= getKey(x5)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x1));
        			x1++;
        			if (x1 == hold2) {
        				//needs to get the next input and put it at the beginning (0)
        				if (nextInput(0, 1024, 1))
        				{
        					x1 = 0;
        				}
        				else
        				{
        					x1 = -1;
        				}
        			}
        		}
    		}
    		else if (x2 != -1)
    		{
    			if ((getKey(x2) <= getKey(x1))&&(getKey(x2) <= getKey(x3))&&
        				(getKey(x2) <= getKey(x4))&&(getKey(x2) <= getKey(x5)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x2));
        			x2++;
        			if (x2 == hold3) {
        				//needs to get the next input and put it at the beginning (hold2)
        				if(nextInput(hold2, 1024, 2))
        				{
        					x2 = hold2;
        				}
        				else
        				{
        					x2 = -1;
        				}
        			}
        		}
    		}
    		else if (x3 != -1) 
    		{
    			if ((getKey(x3) <= getKey(x1))&&(getKey(x3) <= getKey(x2))&&
        				(getKey(x3) <= getKey(x4))&&(getKey(x3) <= getKey(x5)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x3));
        			x3++;
        			if (x3 == hold4) {
        				//needs to get the next input and put it at the beginning (hold3)
        				if(nextInput(hold3, 1024, 3))
        				{
        					x3 = hold3;
        				}
        				else
        				{
        					x3 = -1;
        				}
        			}
        		}
    		}
    		else if (x4 != -1)
    		{
    			if ((getKey(x4) <= getKey(x1))&&(getKey(x4) <= getKey(x2))&&
        				(getKey(x4) <= getKey(x3))&&(getKey(x4) <= getKey(x5)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x4));
        			x4++;
        			if (x4 == hold5) {
        				//needs to get the next input and put it at the beginning (hold4)
        				if(nextInput(hold4, 512, 4))
        				{
        					x4 = hold4;
        				}
        				else
        				{
        					x4 = -1;
        				}
        			}
        		}
    		}
    		else if (x5 != -1) 
    		{
    			insertOutputBuffer(heapBuffer.getLong(x5));
    			x5++;
        		if (x5 == 4096) {
        		//needs to get the next input and put it at the beginning (hold5)
        			if(nextInput(hold5, 512, 5))
        			{
        				x5 = hold5;
        			}
        			else
        			{
        				x5 = -1;
        			}
        		}
    		}
    	}

    }
    
    private void merge6(int numRec)
    {
    	int x1 = 0;
    	int hold2 = 1024;
    	int x2 = 1024;
    	int hold3 = 2048;
    	int x3 = 2048;
    	int hold4 = 2560;
    	int x4 = 2560;
    	int hold5 = 3072;
    	int x5 = 3072;
    	int hold6 = 3584;
    	int x6 = 3584;
    	for(int i = 0; i < numRec; i++)
    	{
    		if (x1 != -1)
    		{
    			if ((getKey(x1) <= getKey(x2))&&(getKey(x1) <= getKey(x3))&&
        				(getKey(x1) <= getKey(x4))&&(getKey(x1) <= getKey(x5))&&
        				(getKey(x1) <= getKey(x6)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x1));
        			x1++;
        			if (x1 == hold2) {
        				//needs to get the next input and put it at the beginning (0)
        				if (nextInput(0, 1024, 1))
        				{
        					x1 = 0;
        				}
        				else
        				{
        					x1 = -1;
        				}
        			}
        		}
    		}
    		else if (x2 != -1)
    		{
    			if ((getKey(x2) <= getKey(x1))&&(getKey(x2) <= getKey(x3))&&
        				(getKey(x2) <= getKey(x4))&&(getKey(x2) <= getKey(x5))&&
        				(getKey(x2) <= getKey(x6)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x2));
        			x2++;
        			if (x2 == hold3) {
        				//needs to get the next input and put it at the beginning (hold2)
        				if(nextInput(hold2, 1024, 2))
        				{
        					x2 = hold2;
        				}
        				else
        				{
        					x2 = -1;
        				}
        			}
        		}
    		}
    		else if (x3 != -1) 
    		{
    			if ((getKey(x3) <= getKey(x1))&&(getKey(x3) <= getKey(x2))&&
        				(getKey(x3) <= getKey(x4))&&(getKey(x3) <= getKey(x5))&&
        				(getKey(x3) <= getKey(x6)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x3));
        			x3++;
        			if (x3 == hold4) {
        				//needs to get the next input and put it at the beginning (hold3)
        				if(nextInput(hold3, 512, 3))
        				{
        					x3 = hold3;
        				}
        				else
        				{
        					x3 = -1;
        				}
        			}
        		}
    		}
    		else if (x4 != -1)
    		{
    			if ((getKey(x4) <= getKey(x1))&&(getKey(x4) <= getKey(x2))&&
        				(getKey(x4) <= getKey(x3))&&(getKey(x4) <= getKey(x5))&&
        				(getKey(x4) <= getKey(x6)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x4));
        			x4++;
        			if (x4 == hold5) {
        				//needs to get the next input and put it at the beginning (hold4)
        				if(nextInput(hold4, 512, 4))
        				{
        					x4 = hold4;
        				}
        				else
        				{
        					x4 = -1;
        				}
        			}
        		}
    		}
    		else if (x5 != -1) 
    		{
    			if ((getKey(x5) <= getKey(x1))&&(getKey(x5) <= getKey(x2))&&
        				(getKey(x5) <= getKey(x3))&&(getKey(x5) <= getKey(x4))&&
        				(getKey(x5) <= getKey(x6)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x5));
        			x5++;
        			if (x5 == hold6) {
        				//needs to get the next input and put it at the beginning (hold5)
        				if(nextInput(hold5, 512, 5))
        				{
        					x5 = hold5;
        				}
        				else
        				{
        					x5 = -1;
        				}
        			}
        		}
    		}
    		else if (x6 != -1) 
    		{
    			insertOutputBuffer(heapBuffer.getLong(x6));
        		x6++;
        		if (x6 == 4096) {
        				//needs to get the next input and put it at the beginning (hold6)
        			if(nextInput(hold6, 512, 6))
        			{
        				x6 = hold6;
        			}
        			else
        			{
        				x6 = -1;
        			}
        		}
    		}
      	}

    }
    
    private void merge7(int numRec)
    {
    	int x1 = 0;
    	int hold2 = 512;
    	int x2 = 512;
    	int hold3 = 1024;
    	int x3 = 1024;
    	int hold4 = 1536;
    	int x4 = 1536;
    	int hold5 = 2048;
    	int x5 = 2048;
    	int hold6 = 2560;
    	int x6 = 2560;
    	int hold7 = 3072;
    	int x7 = 3072;
    	for(int i = 0; i < numRec; i++)
    	{
    		if (x1 != -1)
    		{
    			if ((getKey(x1) <= getKey(x2))&&(getKey(x1) <= getKey(x3))&&
        				(getKey(x1) <= getKey(x4))&&(getKey(x1) <= getKey(x5))&&
        				(getKey(x1) <= getKey(x6))&&(getKey(x1) <= getKey(x7)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x1));
        			x1++;
        			if (x1 == hold2) {
        				//needs to get the next input and put it at the beginning (0)
        				if (nextInput(0, 512, 1))
        				{
        					x1 = 0;
        				}
        				else
        				{
        					x1 = -1;
        				}
        			}
        		}
    		}
    		else if (x2 != -1)
    		{
    			if ((getKey(x2) <= getKey(x1))&&(getKey(x2) <= getKey(x3))&&
        				(getKey(x2) <= getKey(x4))&&(getKey(x2) <= getKey(x5))&&
        				(getKey(x2) <= getKey(x6))&&(getKey(x2) <= getKey(x7)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x2));
        			x2++;
        			if (x2 == hold3) {
        				//needs to get the next input and put it at the beginning (hold2)
        				if(nextInput(hold2, 512, 2))
        				{
        					x2 = hold2;
        				}
        				else
        				{
        					x2 = -1;
        				}
        			}
        		}
    		}
    		else if (x3 != -1) 
    		{
    			if ((getKey(x3) <= getKey(x1))&&(getKey(x3) <= getKey(x2))&&
        				(getKey(x3) <= getKey(x4))&&(getKey(x3) <= getKey(x5))&&
        				(getKey(x3) <= getKey(x6))&&(getKey(x3) <= getKey(x7)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x3));
        			x3++;
        			if (x3 == hold4) {
        				//needs to get the next input and put it at the beginning (hold3)
        				if(nextInput(hold3, 512, 3))
        				{
        					x3 = hold3;
        				}
        				else
        				{
        					x3 = -1;
        				}
        			}
        		}
    		}
    		else if (x4 != -1)
    		{
    			if ((getKey(x4) <= getKey(x1))&&(getKey(x4) <= getKey(x2))&&
        				(getKey(x4) <= getKey(x3))&&(getKey(x4) <= getKey(x5))&&
        				(getKey(x4) <= getKey(x6))&&(getKey(x4) <= getKey(x7)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x4));
        			x4++;
        			if (x4 == hold5) {
        				//needs to get the next input and put it at the beginning (hold4)
        				if(nextInput(hold4, 512, 4))
        				{
        					x4 = hold4;
        				}
        				else
        				{
        					x4 = -1;
        				}
        			}
        		}
    		}
    		else if (x5 != -1) 
    		{
    			if ((getKey(x5) <= getKey(x1))&&(getKey(x5) <= getKey(x2))&&
        				(getKey(x5) <= getKey(x3))&&(getKey(x5) <= getKey(x4))&&
        				(getKey(x5) <= getKey(x6))&&(getKey(x5) <= getKey(x7)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x5));
        			x5++;
        			if (x5 == hold6) {
        				//needs to get the next input and put it at the beginning (hold5)
        				if(nextInput(hold5, 512, 5))
        				{
        					x5 = hold5;
        				}
        				else
        				{
        					x5 = -1;
        				}
        			}
        		}
    		}
    		else if (x6 != -1) 
    		{
    			if ((getKey(x6) <= getKey(x1))&&(getKey(x6) <= getKey(x2))&&
        				(getKey(x6) <= getKey(x3))&&(getKey(x6) <= getKey(x4))&&
        				(getKey(x6) <= getKey(x5))&&(getKey(x6) <= getKey(x7)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x6));
        			x6++;
        			if (x6 == hold7) {
        				//needs to get the next input and put it at the beginning (hold6)
        				if(nextInput(hold6, 512, 6))
        				{
        					x6 = hold6;
        				}
        				else
        				{
        					x6 = -1;
        				}
        			}
        		}
    		}
    		else if (x7 != -1)
    		{
    			if ((getKey(x7) <= getKey(x1))&&(getKey(x7) <= getKey(x2))&&
        				(getKey(x7) <= getKey(x3))&&(getKey(x7) <= getKey(x4))&&
        				(getKey(x7) <= getKey(x5))&&(getKey(x7) <= getKey(x6)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x7));
        			x7++;
        			if (x7 == 4096) {
        				//needs to get the next input and put it at the beginning (hold7)
        				if(nextInput(hold7, 1024, 7))
        				{
        					x7 = hold7;
        				}
        				else 
        				{
        					x7 = -1;
        				}
        			}
        		}
    		}
    	}

    }

    
    private void merge8(int numRec)
    {
    	int x1 = 0;
    	int hold2 = 512;
    	int x2 = 512;
    	int hold3 = 1024;
    	int x3 = 1024;
    	int hold4 = 1536;
    	int x4 = 1536;
    	int hold5 = 2048;
    	int x5 = 2048;
    	int hold6 = 2560;
    	int x6 = 2560;
    	int hold7 = 3072;
    	int x7 = 3072;
    	int hold8 = 3584;
    	int x8 = 3584;
    	for(int i = 0; i < numRec; i++)
    	{
    		if (x1 != -1)
    		{
    			if ((getKey(x1) <= getKey(x2))&&(getKey(x1) <= getKey(x3))&&
        				(getKey(x1) <= getKey(x4))&&(getKey(x1) <= getKey(x5))&&
        				(getKey(x1) <= getKey(x6))&&(getKey(x1) <= getKey(x7))&&
        				(getKey(x1) <= getKey(x8)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x1));
        			x1++;
        			if (x1 == hold2) {
        				//needs to get the next input and put it at the beginning (0)
        				if (nextInput(0, 512, 1))
        				{
        					x1 = 0;
        				}
        				else
        				{
        					x1 = -1;
        				}
        			}
        		}
    		}
    		else if (x2 != -1)
    		{
    			if ((getKey(x2) <= getKey(x1))&&(getKey(x2) <= getKey(x3))&&
        				(getKey(x2) <= getKey(x4))&&(getKey(x2) <= getKey(x5))&&
        				(getKey(x2) <= getKey(x6))&&(getKey(x2) <= getKey(x7))&&
        				(getKey(x2) <= getKey(x8)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x2));
        			x2++;
        			if (x2 == hold3) {
        				//needs to get the next input and put it at the beginning (hold2)
        				if(nextInput(hold2, 512, 2))
        				{
        					x2 = hold2;
        				}
        				else
        				{
        					x2 = -1;
        				}
        			}
        		}
    		}
    		else if (x3 != -1) 
    		{
    			if ((getKey(x3) <= getKey(x1))&&(getKey(x3) <= getKey(x2))&&
        				(getKey(x3) <= getKey(x4))&&(getKey(x3) <= getKey(x5))&&
        				(getKey(x3) <= getKey(x6))&&(getKey(x3) <= getKey(x7))&&
        				(getKey(x3) <= getKey(x8)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x3));
        			x3++;
        			if (x3 == hold4) {
        				//needs to get the next input and put it at the beginning (hold3)
        				if(nextInput(hold3, 512, 3))
        				{
        					x3 = hold3;
        				}
        				else
        				{
        					x3 = -1;
        				}
        			}
        		}
    		}
    		else if (x4 != -1)
    		{
    			if ((getKey(x4) <= getKey(x1))&&(getKey(x4) <= getKey(x2))&&
        				(getKey(x4) <= getKey(x3))&&(getKey(x4) <= getKey(x5))&&
        				(getKey(x4) <= getKey(x6))&&(getKey(x4) <= getKey(x7))&&
        				(getKey(x4) <= getKey(x8)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x4));
        			x4++;
        			if (x4 == hold5) {
        				//needs to get the next input and put it at the beginning (hold4)
        				if(nextInput(hold4, 512, 4))
        				{
        					x4 = hold4;
        				}
        				else
        				{
        					x4 = -1;
        				}
        			}
        		}
    		}
    		else if (x5 != -1) 
    		{
    			if ((getKey(x5) <= getKey(x1))&&(getKey(x5) <= getKey(x2))&&
        				(getKey(x5) <= getKey(x3))&&(getKey(x5) <= getKey(x4))&&
        				(getKey(x5) <= getKey(x6))&&(getKey(x5) <= getKey(x7))&&
        				(getKey(x5) <= getKey(x8)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x5));
        			x5++;
        			if (x5 == hold6) {
        				//needs to get the next input and put it at the beginning (hold5)
        				if(nextInput(hold5, 512, 5))
        				{
        					x5 = hold5;
        				}
        				else
        				{
        					x5 = -1;
        				}
        			}
        		}
    		}
    		else if (x6 != -1) 
    		{
    			if ((getKey(x6) <= getKey(x1))&&(getKey(x6) <= getKey(x2))&&
        				(getKey(x6) <= getKey(x3))&&(getKey(x6) <= getKey(x4))&&
        				(getKey(x6) <= getKey(x5))&&(getKey(x6) <= getKey(x7))&&
        				(getKey(x6) <= getKey(x8)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x6));
        			x6++;
        			if (x6 == hold7) {
        				//needs to get the next input and put it at the beginning (hold6)
        				if(nextInput(hold6, 512, 6))
        				{
        					x6 = hold6;
        				}
        				else
        				{
        					x6 = -1;
        				}
        			}
        		}
    		}
    		else if (x7 != -1)
    		{
    			if ((getKey(x7) <= getKey(x1))&&(getKey(x7) <= getKey(x2))&&
        				(getKey(x7) <= getKey(x3))&&(getKey(x7) <= getKey(x4))&&
        				(getKey(x7) <= getKey(x5))&&(getKey(x7) <= getKey(x6))&&
        				(getKey(x7) <= getKey(x8)))
        		{
        			insertOutputBuffer(heapBuffer.getLong(x7));
        			x7++;
        			if (x7 == hold8) {
        				//needs to get the next input and put it at the beginning (hold7)
        				if(nextInput(hold7, 512, 7))
        				{
        					x7 = hold7;
        				}
        				else 
        				{
        					x7 = -1;
        				}
        			}
        		}
    		}
    		else if (x8 != -1) 
    		{
    			insertOutputBuffer(heapBuffer.getLong(x8));
    			x8++;
    			if (x8 == hold8 + hold2 - 1) { //not 100% about this
    				//needs to get the next input and put it at the beginning (hold8)
    				if(nextInput(hold8, 512, 8))
    				{
    					x8 = hold8;
    				}
    				else
    				{
    					x8 = -1;
    				}
    			}
    		}
    	}

    }
}
