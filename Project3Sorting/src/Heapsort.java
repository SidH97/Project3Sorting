import java.io.IOException;
/**
 * this is our main class
 * @author Matthew Evans
 * @author Sid97
 * @version 11/13
 */
public class Heapsort
{
     /**
     * main class
     * @param args should be two
     */
    public static void main(String[] args)
    {
        try
        {
            new Sorter(args[0], args[1]);
        }
        catch (IOException e)
        {
            System.out.println(e.toString());
        }
    }
}
