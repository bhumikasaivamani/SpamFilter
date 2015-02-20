
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

public class LogisticRegressionWithStopWordsCriteria
{
    public String spamFolderPath;
    public String hamFolderPath;
    public String spamTestFolderPath;
    public String hamTestFolderPath;
    public String stopWordTextPath;
    
    Map<String,Integer> Vocabulary;
    Map<String,Double> VocabularyWeights;
    ArrayList<DocumentVocabulary> spamDocs;
    ArrayList<DocumentVocabulary> hamDocs;
   
    public double w0;
    double learningRate;
    double lambda;
    double randomWeight;
    int hardLimit;
    
    StopWord s;
    ArrayList<String> stopwrd;
    int sCount, hCount ;
    
    public LogisticRegressionWithStopWordsCriteria()
    {
       Vocabulary=new HashMap<>();
       VocabularyWeights=new HashMap<>();
       
       spamDocs=new ArrayList<>();
       hamDocs=new ArrayList<>();
       
       s=new StopWord();
       stopwrd=s.ConstructStopWordsArray(stopWordTextPath);
       
       w0=1.0;
       //learningRate=0.001;
       //lambda=0.5;
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
                       if(stopwrd.contains(word))
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
                       if(stopwrd.contains(word))
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
                   if(stopwrd.contains(word))
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
    
    public double CalculateDocumentProbability(DocumentVocabulary d,int classNumber)
    {
        double summation=0.0;
        for(String s:Vocabulary.keySet())
        {
            double w=0.0;double t1=0.0;double t2=0.0;
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
            }
            
            summation=summation+t1+t2;
        }
        summation=summation+w0;
        double probability=0.0;
        if(classNumber==0)
        {
            double temp=Math.exp(summation);
            probability=1.0/(1+Math.exp(summation));
        }
        else
        {
            double temp=(1.0/(1+Math.exp(summation)));
            probability=1.0-temp;
        }
        return probability;
    }
    public void CalculateDocumentPriorProbability()
    {
        for(int i=0;i<spamDocs.size();i++)
        {
            spamDocs.get(i).priorProb=CalculateDocumentProbability(spamDocs.get(i),0);
        }
        for(int i=0;i<hamDocs.size();i++)
        {
           hamDocs.get(i).priorProb=CalculateDocumentProbability(hamDocs.get(i),1);
        }
    }
    
    public void TrainLR()
    {
        for(int i=0;i<hardLimit;i++) //hard limit
        {
            CalculateDocumentPriorProbability();
            
            for(String s:Vocabulary.keySet())
            {
                double sump=0.0;   
                //spam
                for(int j=0;j<spamDocs.size();j++)
                {
                   if(spamDocs.get(j).v.containsKey(s))
                    sump=sump+(spamDocs.get(j).v.get(s)*(0-spamDocs.get(j).priorProb));
                }
                //ham
                for(int j=0;j<hamDocs.size();j++)
                {
                   if(hamDocs.get(j).v.containsKey(s))
                    sump=sump+(hamDocs.get(j).v.get(s)*(1-hamDocs.get(j).priorProb));
                }
                double weight=(double)(VocabularyWeights.get(s)+(learningRate*sump)-(learningRate*lambda*VocabularyWeights.get(s)));
                VocabularyWeights.put(s, weight);
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
                double t = temp * docToken.get(s);
                sum=sum+t;
                if(Double.isNaN(sum))
                    sum = 0;
            }
        }
        sum=sum+w0;
        double spamProbability=1.0/(1+Math.exp(sum));
        double hamProbability=Math.exp(sum)/(1+Math.exp(sum));
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
        LogisticRegressionWithStopWordsCriteria lr=new LogisticRegressionWithStopWordsCriteria();
        /*lr.spamFolderPath="/Users/bhumikasaivamani/spam";
        lr.hamFolderPath="/Users/bhumikasaivamani/ham";
        lr.spamTestFolderPath="/Users/bhumikasaivamani/test/spam";
        lr.hamTestFolderPath="/Users/bhumikasaivamani/test/ham";
        lr.stopWordTextPath="/Users/bhumikasaivamani/stopWords.txt";*/
        
        lr.spamFolderPath=args[0];
        lr.hamFolderPath=args[1];
        lr.spamTestFolderPath=args[2];
        lr.hamTestFolderPath=args[3];
        lr.stopWordTextPath=args[4];
        lr.lambda=Double.parseDouble(args[5]);
        lr.learningRate=Double.parseDouble(args[6]);
        lr.hardLimit=Integer.parseInt(args[7]);
        
        lr.ConstructVocabulary();
        
        //Training
        lr.InitializeVocabularyWeights();
        lr.TrainLR();
        
        //Testing
        lr.CalculateAccuracy(lr.spamTestFolderPath);
        double acc = Math.round(((double)lr.sCount/(lr.sCount+lr.hCount)*100));
        System.out.println("Spam accuracy : "+(int)acc+"%");
        
        lr.CalculateAccuracy(lr.hamTestFolderPath);
        acc = Math.round(((double)lr.hCount/(lr.sCount+lr.hCount)*100));
        System.out.println("Ham accuracy : "+(int)acc+"%");
    }
    
}
