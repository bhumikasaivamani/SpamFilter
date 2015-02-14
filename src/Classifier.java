
import java.util.HashMap;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bhumikasaivamani
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
