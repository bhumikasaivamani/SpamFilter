
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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
public class PerceptronClassification 
{
    Map<String,Integer> Vocabulary;
    Map<String,Double> VocabularyWeights;
    public String spamFolderPath;
    public String hamFolderPath;
    public String spamTestFolderPath;
    public String hamTestFolderPath;
    ArrayList<DocumentVocabulary> spamDocs;
    ArrayList<DocumentVocabulary> hamDocs;
    public double w0;
    double learningRate;
    int hardLimit;
    int sCount, hCount ;
    
    public PerceptronClassification()
    {
        Vocabulary=new HashMap<>();
        VocabularyWeights=new HashMap<>();

        spamDocs=new ArrayList<>();
        hamDocs=new ArrayList<>();
        w0=0.0;
        hardLimit=100;
        learningRate=0.001;
    }
    
    /**
     * Function Extract Words given a Document along with its frequency in map
     */
    public Map<String,Integer> ExtractTokensFromDocument(String path)
    {
        Map<String,Integer> extractedTokens = new HashMap<>();
        try
        {
            FileReader fileReader=new FileReader(path);
            BufferedReader br=new BufferedReader(fileReader);
            String line=br.readLine();
            while(line!=null)
            {
                StringTokenizer token=new StringTokenizer(line," ");
                while(token.hasMoreTokens())
                {
                   String word=token.nextToken().toLowerCase().trim();
                   word=word.replaceAll("[^a-zA-Z]+","");
                   if(word.length()==0)
                       continue;
                   if(extractedTokens.containsKey(word))
                   {
                       int value=extractedTokens.get(word);
                       extractedTokens.put(word, value+1);
                   }
                   else
                   {
                      extractedTokens.put(word,1);
                   }
                } 
                line=br.readLine();
            }
        }
        catch(Exception e)
        {}
        return extractedTokens;
    }
   
    
    /**
     * Function to COnstruct the Vocabulary
     * @param args 
     */
    public void ConstructVocabulary()
    {
        File spamfolder=new File(spamFolderPath);
        File [] spamFiles=spamfolder.listFiles();
        
        for(int i=0;i<spamFiles.length;i++)
        {
            if(spamFiles[i].getName().equals(".DS_Store"))
                continue;
            try
            {
                FileReader fileReader=new FileReader(spamFiles[i].getAbsolutePath());
                DocumentVocabulary d=new DocumentVocabulary(spamFiles[i].getName(),spamFiles[i].getAbsolutePath());
                Map<String,Integer> dv=new HashMap<>();
                dv=ExtractTokensFromDocument(spamFiles[i].getAbsolutePath());
                d.v=dv;
                spamDocs.add(d);
                BufferedReader br=new BufferedReader(fileReader);
                String line=br.readLine();
                while(line!=null)
                {
                    StringTokenizer token=new StringTokenizer(line," ");
                    while(token.hasMoreTokens())
                    {
                       String word=token.nextToken().trim().toLowerCase();
                       word=word.replaceAll("[^a-zA-Z]+","");
                       if(word.length()==0)
                           continue;
                       if(Vocabulary.containsKey(word))
                       {
                           int value=Vocabulary.get(word);
                           int newValue=value+1;
                           Vocabulary.put(word, newValue);
                       }
                       else
                       {
                          Vocabulary.put(word,1);
                       }
                    } 
                    line=br.readLine();
                }
            }
            catch(Exception e)
            {}
          }
        
        //Ham Folder
        File hamfolder=new File(hamFolderPath);
        File [] hamFiles=hamfolder.listFiles();
       
        for(int i=0;i<hamFiles.length;i++)
        {
            if(hamFiles[i].getName().equals(".DS_Store"))
                continue;
            try
            {
                FileReader fileReader=new FileReader(hamFiles[i].getAbsolutePath());
                DocumentVocabulary d=new DocumentVocabulary(hamFiles[i].getName(),hamFiles[i].getAbsolutePath());
                Map<String,Integer> dv=new HashMap<>();
                dv=ExtractTokensFromDocument(hamFiles[i].getAbsolutePath());
                d.v=dv;
                hamDocs.add(d);
                BufferedReader br=new BufferedReader(fileReader);
                String line=br.readLine();
                while(line!=null)
                {
                    StringTokenizer token=new StringTokenizer(line," ");
                    while(token.hasMoreTokens())
                    {
                       String word=token.nextToken().trim().toLowerCase();
                       word=word.replaceAll("[^a-zA-Z]+","");
                       if(word.length()==0)
                           continue;
                       if(Vocabulary.containsKey(word))
                       {
                           int value=Vocabulary.get(word);
                           int newValue=value+1;
                           Vocabulary.put(word, newValue);
                       }
                       else
                       {
                          Vocabulary.put(word,1);
                       }
                    } 
                    line=br.readLine();
                }
            }
            catch(Exception e)
            {}
        }
        System.out.println("");
    }
    
    /**
     * Function to initialize weights
     * @param args 
     */
   public void InitializeVocabularyWeights()
    {
        Random rand = new Random();
        for(String s:Vocabulary.keySet())
        {
            /*double r = rand.nextDouble();
            int sign = rand.nextInt(2);
             
            if(sign == 0)
                randomWeight = -1.0*r;
            else
                randomWeight = r;
            VocabularyWeights.put(s, randomWeight);*/
            VocabularyWeights.put(s, 0.3);
        }
    }
   
   /**
    * Function to Calculate Perceptron output o
    * @return 
    */
   public int CalculatePerceptronOutput(String docId)
   {
       double o=0.0;
       for(int i=0;i<spamDocs.size();i++)
       {
           if(spamDocs.get(i).documentId.equals(docId))
           {
               double weight;
               for(String s:Vocabulary.keySet())
               {
                   int freq;
                   if(spamDocs.get(i).v.get(s)==null)
                   {
                       freq=0;
                   }
                   else
                       freq=spamDocs.get(i).v.get(s);
                    
                    weight=VocabularyWeights.get(s);
                    o=(double)(o+(weight*freq));
                    
               }
               break;
           }
       }
       for(int i=0;i<hamDocs.size();i++)
       {
           if(hamDocs.get(i).documentId.equals(docId))
           {
               double weight;
               for(String s:Vocabulary.keySet())
               {
                   int freq;
                   if(hamDocs.get(i).v.get(s)==null)
                   {
                       freq=0;
                   }
                   else
                       freq=hamDocs.get(i).v.get(s);
                    
                    weight=VocabularyWeights.get(s);
                    o=(double)(o+(weight*freq));
                }
               break;
           }
       }
       o=o+w0;
       if(o>0)
           return 1;
       else
           return -1;
   }
   
   /**
    * Function to implement Training Rule and Update Weights.
    * @param learningRate
    * @param documentId
    * @param target
    * @param o 
    */
   public void TraingRuleToUpdateWeights(double learnRate,String docId,int target,int o)
   {
       double weight;
       //check for the doc in spam docs
       for(int i=0;i<spamDocs.size();i++)
       {
           if(spamDocs.get(i).documentId.equals(docId))
           {
               for(String s:Vocabulary.keySet())
               {
                   int freq;
                   if(spamDocs.get(i).v.get(s)==null)
                   {
                       freq=0;
                   }
                   else
                       freq=spamDocs.get(i).v.get(s);
                    
                    weight=VocabularyWeights.get(s);
                    double deltaWi=(double)((learnRate)*(target-o)*(freq));
                    weight=weight+deltaWi;
                    VocabularyWeights.put(s,weight);
                }
                break;
           }
       }
       
       //check for the document in ham docs
       for(int i=0;i<hamDocs.size();i++)
       {
           if(hamDocs.get(i).documentId.equals(docId))
           {
               for(String s:Vocabulary.keySet())
               {
                   int freq;
                   if(hamDocs.get(i).v.get(s)==null)
                   {
                       freq=0;
                   }
                   else
                       freq=hamDocs.get(i).v.get(s);
                    
                    weight=VocabularyWeights.get(s);
                    double deltaWi=(double)((learnRate)*(target-o)*(freq));
                    weight=weight+deltaWi;
                    VocabularyWeights.put(s,weight);
               }
               break;
           }
       }
      
   }
   
   /**
    * Function to Train Using Perceptron Classification
    * @param args 
    */
   public void TrainPerceptron()
   {
      for(int i=0;i<1;i++) // For different Learning rates
      {
          int c=0;
          for(int j=0;j<hardLimit;j++) //hardlimit
          {
              
              for(int k=0;k<spamDocs.size();k++) //Processing spam documents
              {
                  int o=CalculatePerceptronOutput(spamDocs.get(k).documentId);
                  if(o==-1)//it is a spam
                      c++;
                  else
                      TraingRuleToUpdateWeights(learningRate, spamDocs.get(k).documentId, -1, o);
              }
              for(int k=0;k<hamDocs.size();k++) //Processing spam documents
              {
                  int o=CalculatePerceptronOutput(hamDocs.get(k).documentId);
                  if(o==1)//it is a spam
                      c++;
                  else
                      TraingRuleToUpdateWeights(learningRate, hamDocs.get(k).documentId, 1, o);
              }
              //System.out.println("Correct"+(c/(hamDocs.size()+spamDocs.size())));
          }
          //System.out.println("Correct"+c);
      }
       
   }
   public String TestPerceptron(String path)
    {
        Map<String,Integer> docToken=new HashMap<>();
        docToken=ExtractTokensFromDocument(path);
        
        double sum=0.0;
        for(String s:docToken.keySet())
        {
            if(VocabularyWeights.containsKey(s))
            {
                double temp = VocabularyWeights.get(s);
                double t = temp * docToken.get(s);
                sum=sum+t;
            }
        }
        sum=sum+w0;
        
        if(sum<0)
            return "spam";
        else 
            return "ham";
        
    }
   public void CalculateAccuracy(String path)
    {
        //spam folder
        int spamCount=0;
        int hamCount=0;
        File folder=new File(path);
        File [] files=folder.listFiles();
        for(int i=0;i<files.length;i++)
        {
            if(files[i].getName().equals(".DS_Store"))
                continue;
            try
            {
                String s=TestPerceptron(files[i].getAbsolutePath());
                if(s.equals("spam"))
                    spamCount++;
                else if(s.equals("ham"))
                    hamCount++;
            }
            catch(Exception e)
            {}
        }
        sCount = spamCount;
        hCount = hamCount;
    }
   public static void main(String args[])
   {
        PerceptronClassification p=new PerceptronClassification();
        
        p.spamFolderPath="/Users/bhumikasaivamani/spam";
        p.hamFolderPath="/Users/bhumikasaivamani/ham";
        p.spamTestFolderPath="/Users/bhumikasaivamani/test/spam";
        p.hamTestFolderPath="/Users/bhumikasaivamani/test/ham";
        
        p.ConstructVocabulary();
        p.InitializeVocabularyWeights();
        
        p.TrainPerceptron();
        
        p.CalculateAccuracy(p.spamTestFolderPath);
        double acc = Math.round(((double)p.sCount/(p.sCount+p.hCount)*100));
        System.out.println("Spam accuracy : "+(int)acc+"%");
        
        p.CalculateAccuracy(p.hamTestFolderPath);
        acc = Math.round(((double)p.hCount/(p.sCount+p.hCount)*100));
        System.out.println("Ham accuracy : "+(int)acc+"%");
   }
    
}
