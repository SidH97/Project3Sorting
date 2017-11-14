import java.io.IOException;

public class heapsort
{

    public static void main(String[] args)
    {
        try
        {
            Sorter sorter = new Sorter(args[0], args[1]);
        }
        catch (IOException e)
        {
            System.out.println(e.toString());
        }
    }

}
