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
	
	Sorter(byte[] inputBuffer, byte[] heapBuffer, byte[] outputBuffer)
	{
		input = inputBuffer;
		heap = heapBuffer;
		output = outputBuffer;
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
	}
	
	public byte[] getOutputBuffer()
	{
		return output;
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
	
	public void heapify()
	{
		minHeapify(0);
	}
}
