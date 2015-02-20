import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author bhumikasaivamani
 * Class that stores data about different class of data (Ham and Spam)
 * Each class has Data(vocabulary) and conditional probabilities associated with that class
 */
public class Classifier 
{
    String Name;
    double prior;
    Map<String,String> condProb;
    String folderPath;
    Data classData;
    double score;
    
    public Classifier()
    {
        classData=new Data();
        condProb=new HashMap<String,String>(); 
    }
}
