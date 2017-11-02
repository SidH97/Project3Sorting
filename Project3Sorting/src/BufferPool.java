import java.util.LinkedList;
import java.util.List;
import java.io.RandomAccessFile;
import java.io.File;

/**
 * 
 * @author sidhingorani
 *
 */
public class BufferPool {
   
   private List<Buffer> pool = new LinkedList<Buffer>();
   
   public BufferPool() {
    
   }
   
   // Copy "sz" bytes from "space" to position "pos" in the buffered storage
   public void insert(byte[] space, int sz, int pos)
   {
	   
   }

   // Copy "sz" bytes from position "pos" of the buffered storage to "space"
   public void getbytes(byte[] space, int sz, int pos)
   {
	   
   }
}
