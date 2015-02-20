
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author bhumikasaivamani
 * Stores Data of a class(Ham and Spam)
 */
public class Data 
{
    Map<String,String> vocabulary;
    int NumberOfFiles;
    public Data()
    {
        vocabulary = new HashMap<String,String>();
    }
}
