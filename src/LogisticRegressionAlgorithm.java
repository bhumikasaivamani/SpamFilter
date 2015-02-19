
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
public class LogisticRegressionAlgorithm 
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
    double lambda;
    double randomWeight;
    
    public LogisticRegressionAlgorithm()
    {
       Vocabulary=new HashMap<>();
       VocabularyWeights=new HashMap<>();
       
       spamDocs=new ArrayList<>();
       hamDocs=new ArrayList<>();
       w0=1.0;
       learningRate=0.001;
       lambda=0.2;
    }
    
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
            {}
        }
    }
    
    public void InitializeVocabularyWeights()
    {
        Random rand = new Random();
        for(String s:Vocabulary.keySet())
        {
            double r = rand.nextDouble();
            int sign = rand.nextInt(2);
             
            if(sign == 0)
                randomWeight = -1.0*r;
            else
                randomWeight = r;
            VocabularyWeights.put(s, randomWeight);
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
    
    public double CalculateDocumentProbability(DocumentVocabulary d,int classNumber)
    {
        double summation=0.0;
        for(String s:Vocabulary.keySet())
        {
            
            double w=0.0;double t1=0.0;double t2=0.0;
            //summation=summation+(Math.log(VocabularyWeights.get(s))/Math.log(2))+(Math.log(d.v.get(s))/Math.log(2));
            //summation=summation+(VocabularyWeights.get(s)*d.v.get(s));
            
            if(VocabularyWeights.get(s)<0)
            {
                w=Math.abs(VocabularyWeights.get(s));
            }
            else
                w=VocabularyWeights.get(s);
            
            t1=(double)Math.log(w);
            
            
            if(d.v.get(s)!=null)
            {
                t2=(double)Math.log(d.v.get(s));
                //t2=d.v.get(s);
            }
            
            summation=summation+t1+t2;
            
            //summation += VocabularyWeights.get(s) * d.v.get(s);
        }
        summation=summation+w0;
        double probability=0.0;
        if(classNumber==0)
        {
            double temp=Math.exp(summation);
            probability=1.0/(1+Math.exp(summation));
            //System.out.println(summation + "******"+probability);
            //probability=1.0/(1+Math.exp(summation));
        }
        else
        {
            //probability=Math.exp(summation)/(1+Math.exp(summation));
            double temp=(1.0/(1+Math.exp(summation)));
            probability=1.0-temp;
            //System.out.println(summation + " "+probability);
            //probability=1.0/(1+Math.exp(summation));
        }
        //System.out.println(probability);
        return probability;
    }
    public void CalculateDocumentPriorProbability()
    {
        for(int i=0;i<spamDocs.size();i++)
        {
            //Map<String,Integer> dv=new HashMap<>();
            //dv=ExtractTokensFromDocument(spamDocs.get(i).path);
            //spamDocs.get(i).v=dv;
            spamDocs.get(i).priorProb=CalculateDocumentProbability(spamDocs.get(i),0);
        }
        for(int i=0;i<hamDocs.size();i++)
        {
            //Map<String,Integer> dv=new HashMap<>();
            //dv=ExtractTokensFromDocument(hamDocs.get(i).path);
            //hamDocs.get(i).v=dv;
            hamDocs.get(i).priorProb=CalculateDocumentProbability(hamDocs.get(i),1);
        }
    }
    
    public void TrainLR()
    {
        for(int i=0;i<80;i++) //hard limit
        {
            CalculateDocumentPriorProbability();
            
            //spam
            for(String s:Vocabulary.keySet())
            {
                double sump=0.0;   
                for(int j=0;j<spamDocs.size();j++)
                {
                   if(spamDocs.get(j).v.containsKey(s))
                   {
                       sump=sump+(spamDocs.get(j).v.get(s)*(0-spamDocs.get(j).priorProb));
                       
                   }
                }
                //ham

                //double sump=0.0;
                for(int j=0;j<hamDocs.size();j++)
                {
                   if(hamDocs.get(j).v.containsKey(s))
                   {
                       sump=sump+(hamDocs.get(j).v.get(s)*(1-hamDocs.get(j).priorProb));
                       
                   }
                    //System.out.println("sum "+sump);
                }
                double weight=(double)(VocabularyWeights.get(s)+(learningRate*sump)-(learningRate*lambda*VocabularyWeights.get(s)));
                //System.out.println(weight);
                double origWt = VocabularyWeights.get(s);
                //if(weight < origWt-1 || weight > origWt+1)
                //    weight = origWt;
                VocabularyWeights.replace(s, weight);
                        //.put(s, weight);
            }
            
        }
    }
    
    public String TestLR(String path)
    {
        Map<String,Integer> docToken=new HashMap<>();
        docToken=ExtractTokensFromDocument(path);
        
        double sum=0.0;
        for(String s:docToken.keySet())
        {
            if(VocabularyWeights.containsKey(s))
            {
                double temp = VocabularyWeights.get(s);
                //double t=(double)Math.log(VocabularyWeights.get(s))+Math.log(docToken.get(s));
                double t = temp * docToken.get(s);
                sum=sum+t;
                if(Double.isNaN(sum))
                    sum = 0;
            }
        }
        sum=sum+w0;
        //System.out.println("");
        double spamProbability=1.0/(1+Math.exp(sum));
        double hamProbability=Math.exp(sum)/(1+Math.exp(sum));
        //double hamProbability=1.0-(1.0/(1+Math.exp(sum)));
        double cl=Math.log(1);
        double cr=spamProbability/hamProbability;
        if(cl<cr)
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
                String s=TestLR(files[i].getAbsolutePath());
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
            {}
        }
        System.out.println("Spam Count ="+spamCount);
        System.out.println("Ham Count ="+hamCount);
    }
    
    public static void main(String args[])
    {
        LogisticRegressionAlgorithm lr=new LogisticRegressionAlgorithm();
        lr.spamFolderPath="/Users/bhumikasaivamani/spam";
        lr.hamFolderPath="/Users/bhumikasaivamani/ham";
        lr.spamTestFolderPath="/Users/bhumikasaivamani/test/spam";
        lr.hamTestFolderPath="/Users/bhumikasaivamani/test/ham";
        lr.ConstructVocabulary();
        lr.InitializeVocabularyWeights();
        //lr.CalculateDocumentPriorProbability();
        lr.TrainLR();
        lr.CalculateAccuracy(lr.spamTestFolderPath);
        lr.CalculateAccuracy(lr.hamTestFolderPath);
       
        System.out.println("");
    }
    
}
