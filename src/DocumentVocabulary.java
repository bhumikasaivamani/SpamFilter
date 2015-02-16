
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
public class DocumentVocabulary 
{
    Map<String,Integer> v;
    Map<String,Double> weight;
    Map<String,Double> probabilities;
    double priorProb;
   
    String documentId;
    public DocumentVocabulary(String s)
    {
        v=new HashMap<>();
        priorProb=0.0;
        probabilities=new HashMap<>();
        weight=new HashMap<>();
        documentId=s;
    }
    
}
