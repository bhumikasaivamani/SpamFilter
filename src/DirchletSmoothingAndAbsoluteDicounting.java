
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 *
 * @author bhumikasaivamani
 */
public class DirchletSmoothingAndAbsoluteDicounting 
{
    ExtractDataWithStopCriteria dataExtraction;
    String spamFolderPath;
    String hamFolderPath;
    String spamTestFolderPath;
    String hamTestFolderPath;
    int sCount, hCount ;
    StopWord s;
    ArrayList<String> stopwrd;
    double mu;
    String stopWordTextPath;
    
    Data spamData;
    Data hamData;
    Map<String,String> totalV;
    public DirchletSmoothingAndAbsoluteDicounting()
    {
        s=new StopWord();
        stopwrd=s.ConstructStopWordsArray(stopWordTextPath);
        totalV = new HashMap<String,String>();
        dataExtraction=new ExtractDataWithStopCriteria();
        spamData=new Data();
        hamData=new Data();
        mu=0.95;
    }
    
    public Map<String,String> ConstructVocabulary()
    {
        Map<String,String> vocabulary = new HashMap<String,String>();
        
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
                       if(word.length()==1)
                        word=word.replaceAll("[^a-zA-Z]+","");
                       if(word.length()==0)
                           continue;
                       if(stopwrd.contains(word))
                           continue;
                       if(vocabulary.containsKey(word))
                       {
                           String value=vocabulary.get(word);
                           int newValue=Integer.parseInt(value)+1;
                           vocabulary.put(word, Integer.toString(newValue));
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
            {}
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
                       if(word.length()==1)
                        word=word.replaceAll("[^a-zA-Z]+","");
                       if(word.length()==0)
                           continue;
                       if(stopwrd.contains(word))
                           continue;
                       if(vocabulary.containsKey(word))
                       {
                           String value=vocabulary.get(word);
                           int newValue=Integer.parseInt(value)+1;
                           vocabulary.put(word, Integer.toString(newValue));
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
            {}
        }
        return vocabulary;
    }

    public int CountTotalDocs()
    {
        int N=spamData.NumberOfFiles+hamData.NumberOfFiles;
        return N;
    }
    public int FindTotalWords(Map<String,String> m)
    {
        int count=0;
        for(String key : m.keySet())
        {
            count += Integer.parseInt(m.get(key));
        }
        return count;
    }
    public ArrayList<Classifier> TrainMultinomialNBWithDirichletSmoothing(ArrayList<Classifier> C)
    {
        ArrayList<Classifier> trainedClassifier=new ArrayList<>();
        totalV=ConstructVocabulary();
        int N=CountTotalDocs();
        for(int i=0;i<C.size();i++)
        {
            Classifier c=new Classifier();
            Map<String,String> condProb = new HashMap<String,String>();
            c.Name=C.get(i).Name;
            c.classData=C.get(i).classData;
            c.folderPath=C.get(i).folderPath;
            
            int Nc=C.get(i).classData.NumberOfFiles;
            double Priorc=(double)Nc/(double)N;
            c.prior=Priorc;
            
            Map<String,String> textc = new HashMap<String,String>();
            textc=C.get(i).classData.vocabulary;
            double Tct = 0.0;
            for(String t : totalV.keySet()) {
                String val =  c.classData.vocabulary.get(t);
                if(val!=null)
                    Tct = Double.parseDouble(val);
                else
                    Tct = 0.0;
            
                double newval=(double)(mu*(Double.parseDouble(totalV.get(t))/totalV.size())); //dirichlet Smoothing in Naive Bayes
                double cp=(Tct+newval)/(FindTotalWords(textc)+mu);
                
                condProb.put(t, Double.toString(cp));
            }
            c.condProb = condProb;
            trainedClassifier.add(c);
            
        }
        return trainedClassifier;   
     }

    public ArrayList<Classifier> TrainMultinomialNBWithAbsoluteDiscountingSmoothing(ArrayList<Classifier> C)
    {
        ArrayList<Classifier> trainedClassifier=new ArrayList<>();
        totalV=ConstructVocabulary();
        int N=CountTotalDocs();
        for(int i=0;i<C.size();i++)
        {
            Classifier c=new Classifier();
            Map<String,String> condProb = new HashMap<String,String>();
            c.Name=C.get(i).Name;
            c.classData=C.get(i).classData;
            c.folderPath=C.get(i).folderPath;
            
            int Nc=C.get(i).classData.NumberOfFiles;
            double Priorc=(double)Nc/(double)N;
            c.prior=Priorc;
            
            Map<String,String> textc = new HashMap<String,String>();
            textc=C.get(i).classData.vocabulary;
            double Tct = 0.0;
            for(String t : totalV.keySet()) {
                String val =  c.classData.vocabulary.get(t);
                if(val!=null)
                    Tct = Double.parseDouble(val);
                else
                    Tct = 0.0;
            
                double max;
                if(Tct-0.6>0.0)
                    max=Tct;
                else max=0.0;
                double temp=(0.6)*(c.classData.vocabulary.size())*(Double.parseDouble(totalV.get(t))/totalV.size()); 
                double cp=(double)((max+temp)/(totalV.size())); //Absolute Discounting - with delta value 0.6
                condProb.put(t, Double.toString(cp));
            }
            c.condProb = condProb;
            trainedClassifier.add(c);
            
        }
        return trainedClassifier;   
     }

    public Map<String,String> ExtractTokensFromDocument(String path)
    {
        Map<String,String> extractedTokens = new HashMap<String,String>();
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
                   if(word.length()==1)
                        word=word.replaceAll("[^a-zA-Z]+","");
                   if(word.length()==0)
                       continue;
                   if(stopwrd.contains(word))
                       continue;
                   if(extractedTokens.containsKey(word))
                   {
                       String value=extractedTokens.get(word);
                       String newValue=Integer.toString(Integer.parseInt(value)+1);
                       extractedTokens.put(word, newValue);
                   }
                   else
                   {
                      extractedTokens.put(word,"1");
                   }
                } 
                line=br.readLine();
            }
        }
        catch(Exception e)
        {}
        return extractedTokens;
     }
    
    public void CalculateAccuracy(ArrayList<Classifier> C,String Path)
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
                String s=ApplyMultiNomialNB(C,files[i].getAbsolutePath());
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
    
    public String ApplyMultiNomialNB(ArrayList<Classifier> C,String d)
    {
       String maxClass=""; 
       Map<String,String> extractedTokensfromDoc = new HashMap<String,String>();
       extractedTokensfromDoc=ExtractTokensFromDocument(d);
       double maxScore=Double.NEGATIVE_INFINITY;
       for(int i=0;i<C.size();i++)
       {
           double classScore=(double)Math.log(C.get(i).prior)/Math.log(2);
           for(String key : extractedTokensfromDoc.keySet()) {
               String val = C.get(i).condProb.get(key);
               double cp=0.0;
               if(val!=null)
               {
                   cp = Double.parseDouble(val);
               }
               else
               {
                   cp=(double)1/((FindTotalWords(C.get(i).classData.vocabulary)));
               }
               classScore += (double)(Math.log(cp)/Math.log(2));
           }
           if(classScore>maxScore)
           {
               maxScore=classScore;
               maxClass=C.get(i).Name;
           }
       }
       return maxClass;
    }
    
    public static void main(String args[])
    {
        DirchletSmoothingAndAbsoluteDicounting n=new DirchletSmoothingAndAbsoluteDicounting();
        ArrayList<Classifier> C=new ArrayList<>();
        /*n.spamFolderPath="/Users/bhumikasaivamani/spam";
        n.hamFolderPath="/Users/bhumikasaivamani/ham";
        n.spamTestFolderPath="/Users/bhumikasaivamani/test/spam";
        n.hamTestFolderPath="/Users/bhumikasaivamani/test/ham";
        n.stopWordTextPath="/Users/bhumikasaivamani/stopWords.txt";*/
        
        n.spamFolderPath=args[0];
        n.hamFolderPath=args[1];
        n.spamTestFolderPath=args[2];
        n.hamTestFolderPath=args[3];
        n.stopWordTextPath=args[4];
        
        n.spamData=n.dataExtraction.BuildVocabulary(n.spamFolderPath,n.stopWordTextPath);
        n.hamData=n.dataExtraction.BuildVocabulary(n.hamFolderPath,n.stopWordTextPath);
        
        Classifier spamClass=new Classifier();
        spamClass.classData=n.spamData;
        spamClass.Name="spam";
        C.add(spamClass);
        
        Classifier hamClass=new Classifier();
        hamClass.classData=n.hamData;
        hamClass.Name="ham";
        C.add(hamClass);
        
        System.out.println("\n\tDirichlet Smoothing");
        //Training
        ArrayList<Classifier> trainedC=new ArrayList<>();
        trainedC=n.TrainMultinomialNBWithDirichletSmoothing(C);
        n.CalculateAccuracy(trainedC,n.spamTestFolderPath);
        double acc = Math.round(((double)n.sCount/(n.sCount+n.hCount)*100));
        System.out.println("Spam accuracy : "+(int)acc+"%");
        
        n.CalculateAccuracy(trainedC,n.hamTestFolderPath);
        acc = Math.round(((double)n.hCount/(n.sCount+n.hCount)*100));
        System.out.println("Ham accuracy : "+(int)acc+"%");
        
        System.out.println("\n\tAbsolute Discounting");
        
        ArrayList<Classifier> trainedC1=new ArrayList<>();
        trainedC1=n.TrainMultinomialNBWithAbsoluteDiscountingSmoothing(C);
        n.CalculateAccuracy(trainedC1,n.spamTestFolderPath);
        acc = Math.round(((double)n.sCount/(n.sCount+n.hCount)*100));
        System.out.println("Spam accuracy : "+(int)acc+"%");
        
        n.CalculateAccuracy(trainedC1,n.hamTestFolderPath);
        acc = Math.round(((double)n.hCount/(n.sCount+n.hCount)*100));
        System.out.println("Ham accuracy : "+(int)acc+"%");
        
        
    }
    
}
