import java.io.*;

/**
 * 
 * @author Siddharth Hingorani (sid97)
 * @author Matthew Evans (matce93)
 */
public class heap 
{
	public Byte [] heap;
	public int n;

	public heap(Byte B[]) throws IOException
	{
		heap = new Byte[B.length];
		System.arraycopy(B,0,heap,0,B.length); 
		n = heap.length;
		for(int i=n/2-1; i>=0; i--)
		{
			heapify(i);
		}
	}

	//------------------------------------------------------------------------
	   public void heapify(int i)  // utility routine to percolate down from index i
	   {
		int left, r, min, tmp;			// declare variables

		left = 2 * i + 1;  			// left child
		r = 2 * i + 2;       			// right child

	  	if(left < n && heap[left] < heap[i])		// find smallest child
	     		min = left;             	// save index of smaller child
	  	else
	  	   	min = i;

	  	if(r < n && heap[r] < heap[min])
	     		min = r;           		// save index of smaller child

	  	if(min != i)	 			// swap and percolate, if necessary
	  	{
	      		tmp = heap[i];      		// exchange values at two indices
	      		heap[i] = heap[min];
	      		heap[min] = tmp;
	      		heapify(min); 			// call Heapify

	     	}// end if

	   }// end method Heapify
}
