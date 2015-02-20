
import java.util.HashMap;
import java.util.Map;

public class DocumentVocabulary 
{
    Map<String,Integer> v;
    double priorProb;
    String path;
   
    String documentId;
    public DocumentVocabulary(String s,String p)
    {
        v=new HashMap<>();
        priorProb=0.0;
        path=p;
        documentId=s;
    }
    
}
