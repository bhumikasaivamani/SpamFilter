
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 *
 * @author bhumikasaivamani
 */
public class ExtractData 
{
    public Data BuildVocabulary(String folderPath)
    {
        File folder=new File(folderPath);
        File [] files=folder.listFiles();
        Map<String,String> vocabulary = new HashMap<String,String>();
        Data data =new Data();
        data.NumberOfFiles=files.length-1;
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
                       String word=token.nextToken().toLowerCase().trim();
                       if(word.length()==1)
                        word=word.replaceAll("[^a-zA-Z]+","");
                       if(word.length()==0)
                        continue;
                       if(vocabulary.containsKey(word))
                       {
                           String value=vocabulary.get(word);
                           String newValue=Integer.toString(Integer.parseInt(value)+1);
                           vocabulary.replace(word, newValue);
                       }
                       else
                          vocabulary.put(word,"1");
                    } 
                    line=br.readLine();
                }
            }
            catch(Exception e)
            {}
          }
          data.vocabulary=vocabulary;
          return data;
    }
}
