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
	private int[] heap;
	
	private void minHeapify(int index)
	{
		int hold = heap[index];
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
	
}
