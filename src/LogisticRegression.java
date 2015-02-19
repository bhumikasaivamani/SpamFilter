
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
public class LogisticRegression 
{
   /* Map<String,Integer> Vocabulary;
    Map<String,Double> VocabularySpamWeights;
    Map<String,Double> VocabularyHamWeights;
    Map<String,Double> VocabularySpamProbabilities;
    Map<String,Double> VocabularyHamProbabilities;
    public String spamFolderPath;
    public String hamFolderPath;
    public String spamTestFolderPath;
    public String hamTestFolderPath;
    ArrayList<DocumentVocabulary> spamDocuments;
    ArrayList<DocumentVocabulary> hamDocuments;
    int w0=1;
    double learningRate;
    double lambda;
    
    public LogisticRegression()
    {
        Vocabulary=new HashMap<>();
        VocabularySpamWeights=new HashMap<>();
        VocabularyHamWeights=new HashMap<>();
        VocabularySpamProbabilities=new HashMap<>();
        VocabularyHamProbabilities=new HashMap<>();
        spamDocuments=new ArrayList<>();
        hamDocuments=new ArrayList<>();
        learningRate=0.01;
        lambda=0.7;
    }
    
    public ArrayList<DocumentVocabulary> ConstructDocumentVocabulary(String path)
    {
        File folder=new File(path);
        File [] files=folder.listFiles();
        ArrayList<DocumentVocabulary> classifiedV=new ArrayList<>();
        for(int i=0;i<files.length;i++)
        {
            Map<String,Integer> docV=new HashMap<>();
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
                       String word=token.nextToken().trim().toLowerCase();
                       word=word.replaceAll("[^a-zA-Z]+","");
                       if(word.length()==0)
                           continue;
                       if(docV.containsKey(word))
                       {
                           int value=docV.get(word);
                           int newValue=value+1;
                           docV.replace(word, newValue);
                       }
                       else
                       {
                          docV.put(word,1);
                       }
                    } 
                    line=br.readLine();
                }
                
            }
            catch(Exception e)
            {
                
            }
            DocumentVocabulary d=new DocumentVocabulary(files[i].getName());
            d.v=docV;
            classifiedV.add(d);
        }
        return classifiedV;
    }
    public void ConstructVocabulary()
    {
        File spamfolder=new File(spamFolderPath);
        File [] spamFiles=spamfolder.listFiles();
        
        Data spamdata =new Data();
        spamdata.NumberOfFiles=spamFiles.length-1;
        for(int i=0;i<spamFiles.length;i++)
        {
            if(spamFiles[i].getName().equals(".DS_Store"))
                continue;
            try
            {
                FileReader fileReader=new FileReader(spamFiles[i].getAbsolutePath());
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
                           Vocabulary.replace(word, newValue);
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
            {
                
            }
          }
        
        //Ham Folder
        File hamfolder=new File(hamFolderPath);
        File [] hamFiles=hamfolder.listFiles();
        
        Data hamdata =new Data();
        hamdata.NumberOfFiles=hamFiles.length-1;
        for(int i=0;i<hamFiles.length;i++)
        {
            if(hamFiles[i].getName().equals(".DS_Store"))
                continue;
            try
            {
                FileReader fileReader=new FileReader(hamFiles[i].getAbsolutePath());
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
                           Vocabulary.replace(word, newValue);
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
            {
                
            }
        }
    }
    
    public void InitializeWeights()
    {
        Map<String,Double> spamW=new HashMap<>();
        Map<String,Double> hamW=new HashMap<>();
        
        for(int i=0;i<spamDocuments.size();i++)
        {
            for(String s:spamDocuments.get(i).v.keySet())
            {
                spamW.put(s,0.3);
            }
            spamDocuments.get(i).weight=spamW;
        }
        
        for(int i=0;i<hamDocuments.size();i++)
        {
            for(String s:hamDocuments.get(i).v.keySet())
            {
                hamW.put(s,0.3);
            }
            hamDocuments.get(i).weight=hamW;
        }
    }

    
    public void CalculatePriorProbability()
    {
        for(int i=0;i<spamDocuments.size();i++)
        {
            spamDocuments.get(i).priorProb=0.0;
            for(String s:spamDocuments.get(i).v.keySet())
            {
                spamDocuments.get(i).priorProb+=spamDocuments.get(i).weight.get(s)*spamDocuments.get(i).v.get(s);
            }
        }
        
        for(int i=0;i<hamDocuments.size();i++)
        {
            hamDocuments.get(i).priorProb=0.0;
            for(String s:hamDocuments.get(i).v.keySet())
            {
                hamDocuments.get(i).priorProb+=hamDocuments.get(i).weight.get(s)*hamDocuments.get(i).v.get(s);
            }
        }
    }
    
    public double CalculateSpamDocumentProbabilityGivenword(String wrd)
    {
        double totalProb=0.0;
        for(int i=0;i<spamDocuments.size();i++)
        {
            if(spamDocuments.get(i).v.get(wrd)!=null)
            {
                totalProb=totalProb+spamDocuments.get(i).weight.get(wrd)*spamDocuments.get(i).v.get(wrd);
            }
        }
        return totalProb;
    }
    
    public double CalculateHamDocumentProbabilityGivenword(String wrd)
    {
        double totalProb=0.0;
        for(int i=0;i<hamDocuments.size();i++)
        {
            if(hamDocuments.get(i).v.get(wrd)!=null)
            {
                totalProb=totalProb+hamDocuments.get(i).weight.get(wrd)*hamDocuments.get(i).v.get(wrd);
            }
        }
        return totalProb;
    }
    public void UpdateWeights()
    {
        double tempW;
        for(int i=0;i<100;i++)
        {
            //CalculatePriorProbability();
            //CalculateProbabilities();
            for(String s: Vocabulary.keySet())
            {
                if(VocabularySpamWeights.get(s)==null)
                    tempW=0.3;
                else
                    tempW=VocabularySpamWeights.get(s);
                double w=tempW+(learningRate*Vocabulary.get(s)*CalculateSpamDocumentProbabilityGivenword(s))-(lambda*tempW*learningRate);
                VocabularySpamWeights.put(s, w);
            }
            for(String s: Vocabulary.keySet())
            {
                if(VocabularyHamWeights.get(s)==null)
                    tempW=0.3;
                else
                    tempW=VocabularyHamWeights.get(s);
                double w=tempW+(learningRate*Vocabulary.get(s)*CalculateHamDocumentProbabilityGivenword(s))-(lambda*tempW*learningRate);
                VocabularyHamWeights.put(s, w);
            } 
        }
        
    }
    
    public void CalculateProbabilities()
    {
        for(String s:VocabularySpamWeights.keySet())
        {
            double p=1.0/(1+Math.exp(-1*VocabularySpamWeights.get(s)));
            VocabularySpamProbabilities.put(s, p);
        }
        for(String s:VocabularyHamWeights.keySet())
        {
            double p=(Math.exp(-1*VocabularyHamWeights.get(s)))/(1+Math.exp(-1*VocabularyHamWeights.get(s)));
            VocabularyHamProbabilities.put(s, p);
        }
        
    }

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
                       extractedTokens.replace(word, value+1);
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
        {

        }
        return extractedTokens;
    }
    public String ApplyLR(String path)
    {
       
       Map<String,Integer> extractedTokensfromDoc = new HashMap<>();
       extractedTokensfromDoc=ExtractTokensFromDocument(path);
       double spamScore=0.0;
       double hamScore=0.0;
       //spam probability
       for(String s:extractedTokensfromDoc.keySet())
       {
           if(VocabularySpamProbabilities.get(s)!=null)
           {
               double exp=(double)Math.exp(w0+VocabularySpamProbabilities.get(s)*extractedTokensfromDoc.get(s));
               spamScore=spamScore+(1.0/(1+exp));
              // spamScore=spamScore*extractedTokensfromDoc.get(s)*VocabularySpamProbabilities.get(s);
           }
       }
       //ham probability
       for(String s:extractedTokensfromDoc.keySet())
       {
           if(VocabularyHamProbabilities.get(s)!=null)
           {
               double exp=(double)Math.exp(w0+VocabularyHamProbabilities.get(s)*extractedTokensfromDoc.get(s));
               hamScore=hamScore+(exp/(1+exp));
               //hamScore=hamScore*extractedTokensfromDoc.get(s)*VocabularyHamProbabilities.get(s);
           }
       }
       
       if(spamScore>hamScore)
           return "spam";
       else
           return "ham";
    }
    
    public void CalculateAccuracy(String Path)
    {
        //spam folder
        int spamCount=0;
        int hamCount=0;
        File folder=new File(Path);
        File [] files=folder.listFiles();
       
        for(int i=0;i<files.length;i++)
        {
            if(files[i].getName().equals(".DS_Store"))
                continue;
            try
            {
                String s=ApplyLR(files[i].getAbsolutePath());
                if(s.equals("spam"))
                {
                    spamCount++;
                }
                else if(s.equals("ham"))
                {
                    hamCount++;
                }
            }
            catch(Exception e)
            {
                
            }
        }
        System.out.println("Spam Count ="+spamCount);
        System.out.println("Ham Count ="+hamCount);
     }
    public static void main(String args[])
    {
        LogisticRegression lr=new LogisticRegression();
        lr.spamFolderPath="/Users/bhumikasaivamani/spam";
        lr.hamFolderPath="/Users/bhumikasaivamani/ham";
        lr.spamTestFolderPath="/Users/bhumikasaivamani/test/spam";
        lr.hamTestFolderPath="/Users/bhumikasaivamani/test/ham";
        
        lr.ConstructVocabulary();
        
        lr.spamDocuments=lr.ConstructDocumentVocabulary(lr.spamFolderPath);
        lr.hamDocuments=lr.ConstructDocumentVocabulary(lr.hamFolderPath);
        lr.InitializeWeights();
        
        lr.CalculatePriorProbability();
        lr.UpdateWeights();
        //lr.CalculateProbabilities();
        lr.CalculateAccuracy(lr.spamTestFolderPath);
        lr.CalculateAccuracy(lr.hamTestFolderPath);
        System.out.println(""); 
    }*/
    
}
