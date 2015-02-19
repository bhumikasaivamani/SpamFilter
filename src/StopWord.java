
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bhumikasaivamani
 */
public class StopWord 
{
    ArrayList<String> stopWords;
    public static void main(String args [])
    {
        StopWord s=new StopWord();
        s.ConstructStopWordsArray();
    }
    public ArrayList<String> ConstructStopWordsArray()
    {
        stopWords=new ArrayList<>();
        try
        {
            FileReader reader=new FileReader("/Users/bhumikasaivamani/stopWords.txt");
            BufferedReader br=new BufferedReader(reader);
            String line=br.readLine();
            while(line!=null)
            {
               stopWords.add(line.trim().toLowerCase()); 
               line=br.readLine();
            }
            
        }
        catch(Exception e)
        {
            
        }
        return stopWords;
    }
    
}
