import java.io.FileNotFoundException;

public class heapsort {

	public static void main(String[] args) {
		Sorter sorter = null;
		try {
			sorter = new Sorter(args[0], args[1]);
		} catch (FileNotFoundException e) {
			System.out.println(e.toString());
		}
		sorter.replacementSelection();
	}

}
