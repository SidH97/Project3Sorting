import java.io.FileNotFoundException;
import java.io.IOException;

public class heapsort {

	public static void main(String[] args) {
		Sorter sorter = null;
		try {
			sorter = new Sorter("test.bin", "okay");
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		sorter.replacementSelection();
	}

}
