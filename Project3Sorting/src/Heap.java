import java.util.Arrays;
import java.util.NoSuchElementException;

class Heap    
{
    private int size;
    private int[] heap;
 
    /** 
     * Constructor 
     **/    
    public Heap(int capacity)
    {
        heap = new int[capacity + 1];
        Arrays.fill(heap, -1);
        size = 0;
    }
 
    /** 
     * @return if the heap is empty
     **/
    public boolean isEmpty( )
    {
    	if (size == 0) {
    		return true;
    	} else {
    		return false;
    	}
    }
 
    /** 
     *  @return if heap is full
     **/
    public boolean isFull( )
    {
    	if (size == heap.length) {
    		return true;
    	} else {
    		return false;
    	}
    }
 
    /** 
     * sets size equal to zero 
     **/
    public void makeEmpty( )
    {
        size = 0;
    }
 
    /** Function to  get index parent of i **/
    private int getParent(int i) 
    {
        return (i - 1)/2;
    }
 
    /** Function to get index of k th child of i **/
    private int kthChild(int i, int k) 
    {
        return 2 * i + k;
    }
 
    /** 
     * @param x to be inserted
     * @return if was inserted
     **/
    public boolean insert(int x)
    {
        if (!isFull( ) ) //not full
        {
        	heap[size++] = x;
            heapUp(size - 1);
            return true;
        } else {  //full
        	return false;
        }
        
    }
 
    /** 
     * @return minimum element
     **/
    public int findMin( )
    {
        if (!isEmpty() ) {
        	return heap[0];
        } else {
        	throw new NoSuchElementException("heap empty"); 
        }   
    }
 
    /**
     * deletes the smallest element of heap
     * @return minimum element
     **/
    public int deleteMin()
    {
    	if(!isEmpty()) {
    		int keyItem = heap[0];
    		heap[0] = heap[size - 1];
            size--;
            heapDown(0);
            return keyItem;
    	} else {
        	throw new NoSuchElementException("heap empty"); 
        }  
        
    }
 
    /** 
     * @param the index of the child
     **/
    private void heapUp(int childIndex)
    {
        int hold = heap[childIndex];    
        while ((childIndex > 0) && (hold < heap[getParent(childIndex)]))
        {
            heap[childIndex] = heap[getParent(childIndex)];
            childIndex = getParent(childIndex);
        }                   
        heap[childIndex] = hold;
    }
 
    /** Function heapifyDown **/
    private void heapDown(int ind)
    {
        int child;
        int tmp = heap[ ind ];
        while (kthChild(ind, 1) < size)
        {
            child = minChild(ind);
            if (heap[child] < tmp)
                heap[ind] = heap[child];
            else
                break;
            ind = child;
        }
        heap[ind] = tmp;
    }
 
    /** Function to get smallest child **/
    private int minChild(int ind) 
    {
        int bestChild = kthChild(ind, 1);
        int k = 2;
        int pos = kthChild(ind, k);
        while ((k <= 2) && (pos < size)) 
        {
            if (heap[pos] < heap[bestChild]) 
                bestChild = pos;
            pos = kthChild(ind, k++);
        }    
        return bestChild;
    }     
}