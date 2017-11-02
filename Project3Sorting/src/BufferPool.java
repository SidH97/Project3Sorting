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
   
   private List<Buffer> freeBuffers = new LinkedList<Buffer>();
   
   public BufferPool(File ) {
      for (int i=0;i<BUFFER_POOL_SIZE; i++) {
         Buffer buf = new Buffer();
         freeBuffers.add(buf);
      }
   }
   
   public synchronized int getNumFreeBuffers() {
      return freeBuffers.size();
   }
   
   public synchronized Buffer getFreeBuffer() {
      while (freeBuffers.isEmpty()) {
         try {
            wait();
         }
         catch (InterruptedException iex) {} // ignored
      }
   
      assert (!freeBuffers.isEmpty());
      return freeBuffers.remove(0);
   }
   
   public synchronized void returnFreeBuffer(Buffer buf) {
      freeBuffers.add(buf);      
      notify(); 
   }
}
