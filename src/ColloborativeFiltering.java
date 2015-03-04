
import java.io.BufferedReader;
import java.io.FileReader;
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
public class ColloborativeFiltering 
{
    public Map<String,Map<String,String>> data;
    public Map<String,Map<String,String>> movieIdMap;
    public Map<Map<String,String>,Double> predictiveRatings;
    String trainingDataPath;
    double k;
    
    public ColloborativeFiltering()
    {
        data=new HashMap<>();
        movieIdMap=new HashMap<>();
        predictiveRatings=new HashMap<>();
        k=1.0;
    }
    
    public void ExtractData()
    {
        try
        {
           FileReader reader=new FileReader(trainingDataPath);
           BufferedReader br=new BufferedReader(reader);
           String line=br.readLine();
           while(line!=null)
           {
               String [] s=line.split(",");
               String movieId=s[0];
               String userId=s[1];
               String rating=s[2];
               
               if(data.get(userId)==null)
               {
                   Map<String,String> m=new HashMap<>();
                   m.put(movieId,rating);
                   data.put(userId,m);
               }
               else
               {
                   data.get(userId).put(movieId,rating);
               }
               
               if(movieIdMap.get(movieId)==null)
               {
                   Map<String,String> m=new HashMap<>();
                   m.put(userId, rating);
                   movieIdMap.put(movieId, m);
               }
               else
               {
                   movieIdMap.get(movieId).put(userId, rating);
               }
               line=br.readLine();
           }
        }
        catch(Exception e)
        {}
        System.out.println("");  
    }
    
    public double calculateMean(String user)
    {
	int I=data.get(user).size();
	double totalRating=0.0;
	for(String s:data.get(user).keySet())
	{
		Double r=Double.parseDouble(data.get(user).get(s));
		totalRating=totalRating+r;
	}

	double meanRating=(double)(totalRating/I);
	return meanRating;
    }
    
    public double CalculateCorrelationWeight(String usera,String useri)
    {
        double totalNumerator=0.0;
        for(String s: movieIdMap.keySet())
        {
            if((movieIdMap.get(s).get(usera)!=null))
            {
                if(movieIdMap.get(s).get(useri)!=null)
                {
                    double vaj=Double.parseDouble(data.get(usera).get(s));
                    double meanva=calculateMean(usera);
                    double vij=Double.parseDouble(data.get(useri).get(s));
                    double meanvi=calculateMean(useri);
                    totalNumerator=(double)totalNumerator+((vaj-meanva)*(vij-meanvi));
                }
            }
        }
        
        double vajsquared=0.0;
        for(String s: movieIdMap.keySet())
        {
            if((movieIdMap.get(s).get(usera)!=null))
            {
                if(movieIdMap.get(s).get(useri)!=null)
                {
                    double vaj=Double.parseDouble(data.get(usera).get(s));
                    double meanva=calculateMean(usera);
                    vajsquared=vajsquared+((vaj*meanva)*(vaj*meanva));
                }
            }
        }
        
        double vijsquared=0.0;
        for(String s: movieIdMap.keySet())
        {
            if((movieIdMap.get(s).get(usera)!=null))
            {
                if(movieIdMap.get(s).get(useri)!=null)
                {
                    double vij=Double.parseDouble(data.get(useri).get(s));
                    double meanvi=calculateMean(useri);
                    vijsquared=vijsquared+((vij*meanvi)*(vij*meanvi));
                }
            }
        }
        
        double denominator=(double)vajsquared*vijsquared;
        double totalDenominator=(double)Math.sqrt(denominator);
        
        double w=(double)(totalNumerator/totalDenominator);
        return w;
    }
    public double CalculatePredictiveWeight(String user,String movie)
    {
        double m=0.0;
        for(String s:data.keySet()) //for each user
        {
            double w=CalculateCorrelationWeight(user,s);
            
            if(data.get(s).get(movie)!=null)
            {
                double vij=Double.parseDouble(data.get(s).get(movie));
                double meanvi=calculateMean(s);
                m=m+(w*(vij-meanvi));
            }
           
        }
        double meanva=calculateMean(user);
        double predictiveValue=(double)meanva+(k*m);
        return predictiveValue;
    }
    
    public void TrainData()
    {
        for(String a:data.keySet())
        {
            for(String j:movieIdMap.keySet())
            {
                double p=CalculatePredictiveWeight(a,j);
                Map<String,String> m=new HashMap<>();
                m.put(a, j);
                predictiveRatings.put(m, p);
            }
        }
        System.out.println("");
    }
    public static void main(String args [])
    {
        ColloborativeFiltering c=new ColloborativeFiltering();
        c.trainingDataPath="/Users/bhumikasaivamani/TrainingRatings.txt";
        c.ExtractData();
        c.TrainData();
    }
    
}
