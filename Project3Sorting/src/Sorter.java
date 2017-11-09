/**
 * 
 */
import java.util.*;

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
	private byte[] heap;
	private byte[] input;
	private byte[] output;
	private int inputIndex;
	private int outputIndex;
	
	Sorter(byte[] inputBuffer, byte[] heapBuffer, byte[] outputBuffer)
	{
		input = inputBuffer;
		heap = heapBuffer;
		output = outputBuffer;
		inputIndex = 0;
		outputIndex = 0;
	}
	
	public byte[] getHeapBuffer()
	{
		return heap;
	}
	
	public byte[] getInputBuffer()
	{
		return input;
	}
	
	public void setInputBuffer(byte[] inputBuffer)
	{
		input = inputBuffer;
		inputIndex = 0;
	}
	
	private byte removeInputBuffer(int index)
	{
		inputIndex++;
		return input[index];
	}
	
	private boolean isInputEmpty()
	{
		return inputIndex == 512;
	}
	
	public byte[] getOutputBuffer()
	{
		return output;
	}
	
	private void insertOutputBuffer(byte b)
	{
		output[outputIndex] = b;
		outputIndex++;
		if(outputIndex == 512)
		{
			sendOutputBuffer();
		}
	}
	
	private void sendOutputBuffer()
	{
		outputIndex = 0;
		//to do
	}
	
	private void minHeapify(int index)
	{
		byte hold = heap[index];
		while ((index > 0)&&(hold < heap[parentIndex(index)]))
		{
			heap[index] = heap[parentIndex(index)];
			index = parentIndex(index);
		}
		heap[index] = hold;
	}
	
	private int parentIndex(int indexChild)
	{
		return ((indexChild-1)/2);
	}
	
	private void heapify()
	{
		minHeapify(0);
	}
	
	public void replacementSelection()
	{
		while(getNewInput() == false)
		{
			while(!isInputEmpty()) 
			{
				heapify();
				insertOutputBuffer(heap[0]);
				byte temp = removeInputBuffer(inputIndex);
				if (temp < output[outputIndex - 1])
				{
					
				}
				heap[0] = 
			}
			getNewInput();
		}
		heapify();
		
		int i = 0;
		while(i < 4092)
		{
			insertOutputBuffer(heap[i]);
			i++;
		}
	}
	
	public boolean getNewInput()
	{
		//to do
	}
}
