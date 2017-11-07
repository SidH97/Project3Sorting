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
		RandomAccessFile file = new RandomAccessFile(records, "R");
		byte[] in = new byte[BUFFBYTES];
		byte[] out = new byte[BUFFBYTES];
		byte[] heap = new byte[8*BUFFBYTES];
		ByteBuffer inBuffer =  ByteBuffer.wrap(in);
		ByteBuffer outBuffer =  ByteBuffer.wrap(out);
		ByteBuffer heapBuffer =  ByteBuffer.wrap(heap);
	} 

}
