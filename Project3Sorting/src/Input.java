import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * 
 */

/**
 * @author sidhingorani
 *
 */
public class Input {
	
	private static final int BUFFBYTES = 4096;
	
	public Input(String records, String stats) throws FileNotFoundException
	{
		RandomAccessFile file = new RandomAccessFile(records, "r");
		byte[] in = new byte[BUFFBYTES];
		byte[] out = new byte[BUFFBYTES];
		byte[] heap = new byte[8*BUFFBYTES];
		ByteBuffer inBuffer =  ByteBuffer.wrap(in);
		ByteBuffer outBuffer =  ByteBuffer.wrap(out);
		ByteBuffer heapBuffer =  ByteBuffer.wrap(heap);
	} 
	
	public void read()
	{
		try
        {
			RandomAccessFile file = new RandomAccessFile
	                ("test.txt", "r");
	        FileChannel inChannel = aFile.getChannel();
	        ByteBuffer buffer = ByteBuffer.allocate(1024);
	        while(inChannel.read(buffer) > 0)
	        {
	            buffer.flip();
	            for (int i = 0; i < buffer.limit(); i++)
	            {
	                System.out.print((char) buffer.get());
	            }
	            buffer.clear(); // do something with the data and clear/compact it.
	        }
	        inChannel.close();
	        aFile.close();
        }
        catch (Exception exc)
        {
            System.out.println(exc);
            System.exit(1);
        }
	}
	
	public void replacementSort()
	{
		heap = byte[] data;
		inputbuffer = byte[] data;
		Looptill input.isempty();
		{
			heap.heapify;
			heap[0].pushtoout;
			input[0].pushtoheap;
		}
		heap.heapify;
		Looptll heap.isempty()
		{
			heap[0].pushtoout;
		}
		
		
		
	}

}
