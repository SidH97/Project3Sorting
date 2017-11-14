/**
 * 
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
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

    /**
     * Constructor
     * @param records file written to with run(s)
     * @param stats file about timing
     * @throws IOException
     */
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
        long timer1 = System.currentTimeMillis();
        newReplacementSelection();
        long timer2 = System.currentTimeMillis();
        long runTime = timer2 - timer1;
        
      //Here true is to append the content to file
    	FileWriter fw = new FileWriter(new File(stats),true);
    	//BufferedWriter writer give better performance
    	BufferedWriter bw = new BufferedWriter(fw);
    	bw.write(records + " " + Long.toString(runTime));
    	//Closing BufferedWriter Stream
    	bw.close();
       
        
        runFile = new File(records);
        runFile.delete();
        runFile = new File(records);
        wChannel = new FileOutputStream(runFile, true).getChannel();
        mergeSort();
        sysOut(records);
			wChannel.close();
		}

    /**
     * removes a record from the index
     * @param index of record to be removed
     * @return record
     */
    private long removeInputBuffer(int index)
    {
        inputIndex++;
        return inBuffer.getLong((index * 8));
    }

    /**
     * adds record top front of input buffer
     * @param l record to be re-added to input buffer
     */
    private void insertInputBuffer(long l)
    {
        inBuffer.putLong((frontIndex * 8), l);
        frontIndex++;
    }

    /**
     * clears out the input buffer of all re-added records
     * adds them to heap
     */
    private void cleanInputBuffer()
    {
        for (int i = frontIndex; i >= 0; i--)
        {
        	heapBuffer.putLong(((511 - i) * 8), removeInputBuffer(i));
        }
        heapify();
        frontIndex = 0;
    }

    /**
     * checks input index, can still have records in the buffer
     * @return true if outside of index
     */
    private boolean isInputEmpty()
    {
        return inputIndex == 512;
    }

    /**
     * inserts record to the output buffer 
     * if the output buffer it calls sendOutputBuffer()
     * @param l record to add
     */
    private void insertOutputBuffer(long l)
    {
        outBuffer.putLong((outputIndex * 8), l);
        outputIndex++;
        if (outputIndex == 512)
        {
            sendOutputBuffer();
        }
    }

    /**
     * sends output buffer to file
     */
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

    /**
     * creates a minheap out of the heap
     * @param index to start the minheap
     */
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
    
    /**
     * returns the key of the record in the heap buffer at the index
     * @param index in heapBuffer
     * @return key
     */
    private float getKey(int index)
    {
    	//adds four to get the second half of record
    	return heapBuffer.getFloat(index + 4);
    }

    /**
     * wrks on a 512 indexing
     * @param indexChild of the parent you're trying to find
     * @return index of the parent
     */
    private int parentIndex(int indexChild)
    {
        return ((indexChild - 1) / 2);
    }

    /**
     * calls minHeap(0)
     */
    private void heapify()
    {
        minHeapify(0);
    }

    /**
     * returns the key of the record in the input buffer at the index
     * @param index in inputBuffer
     * @return key
     */
    private float getInputKey(int index)
    {
    	return inBuffer.getFloat(index + 4);
    }
    
    /**
     * returns the key of the record in the output buffer at the index
     * @param index in outputBuffer
     * @return key
     */
    private float getOutputKey(int index)
    {
    	return outBuffer.getFloat(index+ 4);
    }
    
    /**
     * fills in the heap
     * @return true if works
     */
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
    
    /**
     * clears the heap
     */
    private void sendHeap()
    {
        try {
			wChannel.write(heapBuffer);
		} catch (IOException e) {
			System.out.println(e.toString());
		}
        heapBuffer.clear();
    }
    
    /**
     * outputs the first record of each block to system out
     * @param file to output to
     * @throws IOException well it does
     */
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
    
    /**
     * calls sysOut()
     * @param recordId half of the record
     * @param key half of the record
     */
    private void sendToSysOut(int recordId, float key)
    {
    	if ((ct != 0) && (ct % 5 == 0))
    	{
    		System.out.println();
    	}
    	System.out.print(recordId + " " + key + " ");
    	ct++;
    }
    
    /**
     * helper method for replacementSelection()
     */
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
    
    /**
     * out implementation of replacement sort selection
     * @return 1
     */
    public int replacementSelection()
    {
    	int i = 0;
        while (i != 0)
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
        return 1;
        
    }

    /**
     * clears the heap
     */
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
    
    /**
     * gets new full input buffer
     */
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
    
    /**
     * gets the next block(s) for merge sort
     * @param index in heap
     * @param size number of records pulled each time
     * @param runNum to keep track of runs
     * @param depth number calls
     * @return true if able to fill request
     */
    private boolean nextInput(int index, int size, int runNum, int depth)
    {
    	try 
    	{
        	RandomAccessFile newFile = new RandomAccessFile("run.txt", "r");
    		long spot = size * depth;
    		newFile.seek(spot);
			test = newFile.read(heap, index, size);
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
    
    /**
     * @return 1
     */
    public int return1()
    {
    	return 1;
    }
    
    /**
     * our implementation of merge sort
     */
    public void mergeSort()
    {
    	if (run == 1) {
    		return;
    	}
    	else if (run == 2) {
    		//load into merge2
    		nextInput(0, 2048, 1, 0);
    		nextInput(2048, 2048, 2, 0);
    		merge2(run * 4096);
    	}
    	else if (run == 3) {
    		nextInput(0, 1024, 1, 0);
    		nextInput(1024, 1024, 2, 0);
    		nextInput(2048, 2048, 3, 0);
    		merge3(run * 4096);
    	}
    	else if (run == 4) {
    		//load into merge4
    		nextInput(0, 1024, 1, 0);
    		nextInput(1024, 1024, 2, 0);
    		nextInput(2048, 1024, 3, 0);
    		nextInput(3072, 1024, 4, 0);    		
    		merge4(run * 4096);
    	}
    	else if (run == 5) {
    		//load into heapBuffer
        	nextInput(0, 1024, 1, 0);
        	nextInput(1024, 1024, 2, 0);
        	nextInput(2048, 1024, 3, 0);
        	nextInput(3072, 512, 4, 0);
        	nextInput(3584, 512, 5, 0);
    		merge5(run * 4096);
    	}
    	else if (run == 6) {
    		//load into heapBuffer
        	nextInput(0,1024, 1, 0);
        	nextInput(1024, 1024, 2, 0);
        	nextInput(2048, 512, 3, 0);
        	nextInput(2560, 512, 4, 0);
        	nextInput(3072, 512, 5, 0);
        	nextInput(3584, 512, 6, 0);
    		merge6(run * 4096);
    	}
    	else if (run == 7) {
    		//load into heapBuffer
        	nextInput(0, 512, 1, 0);
    		nextInput(512, 512, 2, 0);
    		nextInput(1024, 512, 3, 0);
    		nextInput(1536, 512, 4, 0);
    		nextInput(2048, 512, 5, 0);
    		nextInput(2560, 512, 6, 0);
    		nextInput(3072, 1024, 7, 0);
    		merge7(run * 4096);
    	}
    	else if (run == 8) {
    		//load into merge8
    		nextInput(0, 512, 1, 0);
    		nextInput(512, 512, 2, 0);
    		nextInput(1024, 512, 3, 0);
    		nextInput(1536, 512, 4, 0);
    		nextInput(2048, 512, 5, 0);
    		nextInput(2560, 512, 6, 0);
    		nextInput(3072, 512, 7, 0);
    		nextInput(3584, 512, 8, 0);
    		merge8(run * 4096);
    	}
    }
    
    /**
     * helper method
     * @param numRec total numbewr of records in all runs
     */
    private void merge2(int numRec) 
    {
    	int depth1 = 1;
    	int x1 = 0;
    	int depth2 = 1;
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
        				if (nextInput(0, 2048, 1, depth1))
        				{
        					x1 = 0;
        					
        					depth1++;
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
        				if(nextInput(hold2, 2048, 2, depth2))
        				{
        					x2 = hold2;
        					depth2++;
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
    
    /**
     * helper method
     * @param numRec total numbewr of records in all runs
     */
    private void merge3(int numRec)
    {
    	int depth1 = 1;
    	int x1 = 0;
    	int depth2 = 1;
    	int hold2 = 1024;
    	int x2 = 1024;
    	int depth3 = 1;
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
        				if (nextInput(0, 1024, 1, depth1))
        				{
        					x1 = 0;
        					depth1++;
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
        				if(nextInput(hold2, 1024, 2, depth2))
        				{
        					x2 = hold2;
        					depth2++;
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
        			if(nextInput(hold3, 2048, 3, depth3))
        			{
        				x3 = hold3;
        				depth3++;
        			}
        			else
        			{
        				x3 = -1;
        			}
        		}
    		}
    	}
    }

    /**
     * helper method
     * @param numRec total numbewr of records in all runs
     */
    private void merge4(int numRec)
    {
    	int depth1 = 1;
    	int x1 = 0;
    	int depth2 = 1;
    	int hold2 = 1024;
    	int x2 = 1024;
    	int depth3 = 1;
    	int hold3 = 2048;
        int x3 = 2048;
        int depth4 = 1;
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
        				if (nextInput(0, 1024, 1, depth1))
        				{
        					x1 = 0;
        					depth1++;
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
        				if(nextInput(hold2, 1024, 2, depth2))
        				{
        					x2 = hold2;
        					depth2++;
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
        				if(nextInput(hold3, 1024, 3, depth3))
        				{
        					x3 = hold3;
        					depth3++;
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
        			if(nextInput(hold4, 512, 4, depth4))
        		    {
        				x4 = hold4;
        				depth4++;
        			}
        			else
        			{
        				x4 = -1;
        			}
        		}
    		}
    	}
    }
    
    /**
     * helper method
     * @param numRec total numbewr of records in all runs
     */
    private void merge5(int numRec)
    {
    	int depth1 = 1;
    	int x1 = 0;
    	int depth2 = 1;
    	int hold2 = 1024;
    	int x2 = 1024;
    	int depth3 = 1;
    	int hold3 = 2048;
    	int x3 = 2048;
    	int depth4 = 1;
    	int hold4 = 3072;
    	int x4 = 3072;
    	int depth5 = 1;
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
        				if (nextInput(0, 1024, 1, depth1))
        				{
        					x1 = 0;
        					depth1++;
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
        				if(nextInput(hold2, 1024, 2, depth2))
        				{
        					x2 = hold2;
        					depth2++;
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
        				if(nextInput(hold3, 1024, 3, depth3))
        				{
        					x3 = hold3;
        					depth3++;
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
        				if(nextInput(hold4, 512, 4, depth4))
        				{
        					x4 = hold4;
        					depth4++;
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
        			if(nextInput(hold5, 512, 5, depth5))
        			{
        				x5 = hold5;
        				depth5++;
        			}
        			else
        			{
        				x5 = -1;
        			}
        		}
    		}
    	}

    }
    
    /**
     * helper method
     * @param numRec total numbewr of records in all runs
     */
    private void merge6(int numRec)
    {
    	int depth1 = 1;
    	int x1 = 0;
    	int depth2 = 1;
    	int hold2 = 1024;
    	int x2 = 1024;
    	int depth3 = 1;
    	int hold3 = 2048;
    	int x3 = 2048;
    	int depth4 = 1;
    	int hold4 = 2560;
    	int x4 = 2560;
    	int depth5 = 1;
    	int hold5 = 3072;
    	int x5 = 3072;
    	int depth6 = 1;
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
        				if (nextInput(0, 1024, 1, depth1))
        				{
        					x1 = 0;
        					depth1++;
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
        				if(nextInput(hold2, 1024, 2, depth2))
        				{
        					x2 = hold2;
        					depth2++;
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
        				if(nextInput(hold3, 512, 3, depth3))
        				{
        					x3 = hold3;
        					depth3++;
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
        				if(nextInput(hold4, 512, 4, depth4))
        				{
        					x4 = hold4;
        					depth4++;
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
        				if(nextInput(hold5, 512, 5, depth5))
        				{
        					x5 = hold5;
        					depth5++;
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
        			if(nextInput(hold6, 512, 6, depth6))
        			{
        				x6 = hold6;
        				depth6++;
        			}
        			else
        			{
        				x6 = -1;
        			}
        		}
    		}
      	}

    }
    
    /**
     * helper method
     * @param numRec total numbewr of records in all runs
     */
    private void merge7(int numRec)
    {
    	int depth1 = 1;
    	int x1 = 0;
    	int depth2 = 1;
    	int hold2 = 512;
    	int x2 = 512;
    	int depth3 = 1;
    	int hold3 = 1024;
    	int x3 = 1024;
    	int depth4 = 1;
    	int hold4 = 1536;
    	int x4 = 1536;
    	int depth5 = 1;
    	int hold5 = 2048;
    	int x5 = 2048;
    	int depth6 = 1;
    	int hold6 = 2560;
    	int x6 = 2560;
    	int depth7 = 1;
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
        				if (nextInput(0, 512, 1, depth1))
        				{
        					x1 = 0;
        					depth1++;
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
        				if(nextInput(hold2, 512, 2, depth2))
        				{
        					x2 = hold2;
        					depth2++;
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
        				if(nextInput(hold3, 512, 3, depth3))
        				{
        					x3 = hold3;
        					depth3++;
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
        				if(nextInput(hold4, 512, 4, depth4))
        				{
        					x4 = hold4;
        					depth4++;
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
        				if(nextInput(hold5, 512, 5, depth5))
        				{
        					x5 = hold5;
        					depth5++;
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
        				if(nextInput(hold6, 512, 6, depth6))
        				{
        					x6 = hold6;
        					depth6++;
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
        				if(nextInput(hold7, 1024, 7, depth7))
        				{
        					x7 = hold7;
        					depth7++;
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

    /**
     * helper method
     * @param numRec total numbewr of records in all runs
     */
    private void merge8(int numRec)
    {
    	int depth1 = 1;
    	int x1 = 0;
    	int depth2 = 1;
    	int hold2 = 512;
    	int x2 = 512;
    	int depth3 = 1;
    	int hold3 = 1024;
    	int x3 = 1024;
    	int depth4 = 1;
    	int hold4 = 1536;
    	int x4 = 1536;
    	int depth5 = 1;
    	int hold5 = 2048;
    	int x5 = 2048;
    	int depth6 = 1;
    	int hold6 = 2560;
    	int x6 = 2560;
    	int depth7 = 1;
    	int hold7 = 3072;
    	int x7 = 3072;
    	int depth8 = 1;
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
        				if (nextInput(0, 512, 1, depth1))
        				{
        					x1 = 0;
        					depth1++;
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
        				if(nextInput(hold2, 512, 2, depth2))
        				{
        					depth2++;
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
        				if(nextInput(hold3, 512, 3, depth3))
        				{
        					x3 = hold3;
        					depth3++;
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
        				if(nextInput(hold4, 512, 4, depth4))
        				{
        					x4 = hold4;
        					depth4++;
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
        				if(nextInput(hold5, 512, 5, depth5))
        				{
        					x5 = hold5;
        					depth5++;
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
        				if(nextInput(hold6, 512, 6, depth6))
        				{
        					x6 = hold6;
        					depth6++;
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
        				if(nextInput(hold7, 512, 7, depth7))
        				{
        					x7 = hold7;
        					depth7++;
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
    				if(nextInput(hold8, 512, 8, depth8))
    				{
    					x8 = hold8;
    					depth8++;
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
