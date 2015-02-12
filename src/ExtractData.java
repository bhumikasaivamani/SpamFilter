
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bhumikasaivamani
 */
public class ExtractData 
{
    public Map<String,String> BuildVocabulary(String folderPath)
    {
        File folder=new File(folderPath);
        File [] files=folder.listFiles();
        Map<String,String> vocabulary = new HashMap<String,String>();
        
        
        for(int i=0;i<files.length;i++)
        {
            if(files[i].getName().equals(".DS_Store"))
                continue;
            try
            {
                FileReader fileReader=new FileReader(files[i].getAbsolutePath());
                BufferedReader br=new BufferedReader(fileReader);
                String line=br.readLine();
                while(line!=null)
                {
                    StringTokenizer token=new StringTokenizer(line," ");
                    while(token.hasMoreTokens())
                    {
                       String word=token.nextToken();
                       if(vocabulary.containsKey(word))
                       {
                           String value=vocabulary.get(word);
                           String newValue=Integer.toString(Integer.parseInt(value)+1);
                           vocabulary.replace(word, newValue);
                       }
                       else
                       {
                          vocabulary.put(word,"1");
                       }
                    } 
                    line=br.readLine();
                }
            }
            catch(Exception e)
            {
                
            }
          }
          /*Set setOfWords=vocabulary.entrySet();
          Iterator iterator=setOfWords.iterator();
          while(iterator.hasNext())
          {
            Map.Entry entry=(Map.Entry) iterator.next();
                System.out.println(entry.getKey()+"\t"+entry.getValue());
          }*/
          return vocabulary;
    }
    
    public static void main(String args[])
    {
        ExtractData data=new ExtractData();
        String spamFolderPath="/Users/bhumikasaivamani/spam";
        String hamFolderPath="/Users/bhumikasaivamani/ham";
        Map<String,String> spamVocabulary = new HashMap<String,String>();
        Map<String,String> hamVocabulary = new HashMap<String,String>();
        spamVocabulary=data.BuildVocabulary(spamFolderPath);
        hamVocabulary=data.BuildVocabulary(hamFolderPath);
        System.out.println("");
    }
    
}
