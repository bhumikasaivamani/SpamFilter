
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
    
    public ArrayList<String> ConstructStopWordsArray(String path)
    {
        stopWords=new ArrayList<>();
        try
        {
            FileReader reader=new FileReader(path);
            BufferedReader br=new BufferedReader(reader);
            String line=br.readLine();
            while(line!=null)
            {
               stopWords.add(line.trim().toLowerCase()); 
               line=br.readLine();
            }
            
        }
        catch(Exception e)
        {}
        return stopWords;
    }
    
}
