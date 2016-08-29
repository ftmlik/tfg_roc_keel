/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/
package roc_keel;

import java.util.ArrayList;

/**
 * <p> Implements methods to manage the ROC curves. 
 * @author joseangeldiazg (University of Granada) 
 * @version 1.0
 * </p>
 */
public class RocCoord 
{
   protected double auc;
   protected String coord;
   
   RocCoord()
   {
       coord = null;
       auc=0;
   }
   
   /**
    * <p>
    * Get the coords of the roc curve.
    * </p>
    * @return String with coords of the ROC curve. 
    */
   public String getCoord()
   {
       return this.coord;
   }
   
   /**
    * <p>
    * Get the coords of the roc curve.
    * </p>
    * @return Double with AUC value of the ROC curve. 
    */
   
   public double getAuc()
   {
       return this.auc;
   }
   
   /**
    * <p>
    * Compute the number of elements for each class.
    * </p>
    * @param probabilities The matrix with the probabilities. 
    * @param realClasses Array of Strings with the real class for each pair of probabilities. 
    */
   
   public void buildTwoClassRoc(double[][] probabilities, String realClasses[], boolean allPoints)
   {
       this.sort(probabilities, realClasses, 0);
       ArrayList<String> output = new ArrayList<>();
       output=this.distributeExamples(probabilities, realClasses);
       
       char [] trueClass= new char [probabilities.length];
       
       int p=0;
       int n=0;
       
       for(int i=0; i<probabilities.length; i++)
       {
           
          if(output.get(i).equals("positive"))
          {
             trueClass[i]='P'; 
             p++;
          }     
          else if(output.get(i).equals("negative"))
          {
              trueClass[i]='N';
              n++;
          }
       }
       if(allPoints)
       {
           computeCoordsRocAllPoints(trueClass, p, n);
       }
       else
       {
           computeCoordsRoc(trueClass,p,n);
       }
       
   }
   
   /**
    * <p>
    * Creates a two-class model based on one of several classes.
    * </p>
    * @param probabilities The matrix with the probabilities. 
    * @param realClasses Array of Strings with the real class for each pair of probabilities.
    * @param differentClasses  Array with the different values of the classes in the real problem.
    * @param classA Index of the class that must be confronted with others. 
    */
   
   
   public void buildClassVsAllClassRoc(double[][] probabilities, String[] realClasses, 
           String[] differentClasses, int classA, boolean allPoints)
   {
       double[][] resultProbabilities = new double [probabilities.length][2];
       String []  resultRealClasses = new String [probabilities.length];

       double max=0;
       double [] norm = new double[2];
       
       
       
       for(int i=0; i<probabilities.length;i++)
       {
            resultProbabilities[i][0]=probabilities[i][classA];
             
            if(realClasses[i].equals(differentClasses[classA]))
            {
                resultRealClasses[i]="positive";
                
                
            }
            else
            {
               resultRealClasses[i]="negative";
            }
            
            for(int j=0; j<probabilities[i].length;j++)
            {
                if(j!=classA)
                {
                   if(probabilities[i][j]>max)
                   {
                       max=probabilities[i][j];
                   } 
                }            
            }
            norm[0]=probabilities[i][classA];
            norm[1]=max;           
       
            norm=normalize(norm);
            
            resultProbabilities[i][0]=norm[0];
            resultProbabilities[i][1]=norm[1];
            max=0;
       }
       this.buildTwoClassRoc(resultProbabilities, resultRealClasses, allPoints);
   }
   /**
    * <p>
    * Obtain the value of the AUC and the coordinates of the ROC using the different trend points.
    * </p>
    * @param trueClass Array with the value of the real class for each row. 
    * @param p Number of positive examples.
    * @param n  Number of negative examples.
    */
   
   public void computeCoordsRoc(char [] trueClass, int p, int n )
   {
       
       double moveX=1.0/n;
       double moveY=1.0/p;
       double x=0;
       double y=0;
       double auc=0;
       double width=0;
       double widthAcumulate=0;
       int control=0;
       
       if(p!=0 && n!=0)
       {
            this.coord="coordinates { (0,0)";

            for(int i=0; i<trueClass.length;i++)
            {

                if(trueClass[i]=='N')
                {
                    x+=moveX;

                    width=(x-widthAcumulate);
                    widthAcumulate+=width;
                    auc=auc+(double)((width*y));
                    control++;
                }
                //false negative
                else if(trueClass[i]=='P')
                {      
                    control=0;
                    y+=moveY;  
                }
                 if(1==control)
                 {
                 this.coord+="("+x+","+y+")";
                 }
             }   

             this.coord+=" (1,1) };";
             this.auc=auc;
             isPerfect(this.coord);
       }
       else
       {
           makeRandom();
       }  
    }
   /**
    * <p>
    * Obtain the value of the AUC and the coordinates of the ROC with all points.
    * </p>
    * @param trueClass Array with the value of the real class for each row. 
    * @param p Number of positive examples.
    * @param n  Number of negative examples.
    */
   
   public void computeCoordsRocAllPoints(char [] trueClass, int p, int n )
   {
       
       double moveX=1.0/n;
       double moveY=1.0/p;
       double x=0;
       double y=0;
       double auc=0;
       double width=0;
       double widthAcumulate=0;
       
       if(p!=0 && n!=0)
       {
       
            this.coord="coordinates { (0,0)";

            for(int i=0; i<trueClass.length;i++)
            {

                if(trueClass[i]=='N')
                {
                    x+=moveX;

                    width=(x-widthAcumulate);
                    widthAcumulate+=width;
                    auc=auc+(double)((width*y));
                }
                //false negative
                else if(trueClass[i]=='P')
                {      
                    y+=moveY;  
                }
                this.coord+="("+x+","+y+")";
             }  
             this.coord+="};";
             this.auc=auc;
             isPerfect(this.coord);
       }
       else
       {
           makeRandom();
       }  
    }
   
   /**
    * <p>
    * Obtain the coordinates when the problem is perfect. 
    * </p>
    * @param coord Coords to check. 
    */
   
   public void isPerfect(String coord)
   {
       if(coord.contains("(0.0,1.0)")||coord.contains("(0.0,1.00"))
       {
           this.coord="coordinates { (0,0)(0,1)(1,1)};";
           this.auc=1;
       }
   }
   
   /**
    * <p>
    * Generates ramdom coordinates. 
    * </p>
    */
   
   public void makeRandom()
   {
           this.coord="coordinates { (0,0)(1,1)};";
           this.auc=0.5;
   }
   
  /**
   *<p>
   * Normalizes the doubles in the array using the given value.
   *</p>
   * 
   * @param doubles the array of double
   * @return the doubles normalize to the rank 0-1 with sum(rank)>1
   */
   
   public double[] normalize(double[] doubles)
   {
      
        double normalize[];
        normalize = new double[doubles.length];


        double max= max(doubles);
        double min= min(doubles);


        for(int i=0; i<doubles.length; i++)
        {
            normalize[i]=((doubles[i])-(min))/((max)-(min));  
        }
  
    return normalize2(normalize);
  }
  
   /**
   *<p>
   * Normalizes the doubles in the array using the given value.
   *</p>
   * 
   * @param doubles the array of double
   * @return the doubles normalize to the rank 0-1 with sum(rank)=1
   */
  
  public double[] normalize2(double[] doubles) {
      
    double normalize[];
    normalize = new double[doubles.length];
    
    double total=0;
    
    for(int i=0; i<doubles.length; i++)
    {
        total+=doubles[i];  
    }
    
    for(int i=0; i<doubles.length; i++)
    {
        normalize[i]=doubles[i]/total;  
    }

    return normalize;
  }
  
   /**
   *<p>
   * Find the min value in a doubles array.
   *</p>
   *
   * @param doubles the array of double
   * @return min value of the array
   */
  
    public double min(double[] doubles) 
    { 
        double resultado = 0; 
        for(int i=0; i<doubles.length; i++) 
        { 
            if(doubles[i] < resultado) 
            { 
                resultado = doubles[i]; 
            } 
        } 
        
        return resultado; 
    } 
  /**
   *<p>
   * Find the max value in a doubles array.
   *</p>
   *
   * @param doubles the array of double
   * @return max value of the array
   */
    public double max(double[] doubles) 
    { 
        double resultado =0; 
        for(int i=0; i<doubles.length; i++) 
        { 
            if(doubles[i] > resultado) 
            { 
                resultado = doubles[i]; 
            } 
        } 
        return resultado; 
    }
    
    
    /**
   *<p>
   * Redistribute the examples with the same probabilitie. 
   *</p>
   * @param realClasses The array with the strings (real classes) to sort. 
   * @param probabilities The matrix with the probabilities to sort. 
   */
    
    
    public ArrayList<String> distributeExamples(double [][] probabilities, String[] realClasses)
    {
        int indexA=0;
        int indexB=0;
        int control=0;
        boolean out=false;
        int p=0;
        int n=0;
        
        ArrayList<String> output = new ArrayList<String>();
        
        double controlProb=probabilities[0][0];
        
        for(int i=0;i<probabilities.length && out==false;i++)
        {
            indexA=i;
            
            if(probabilities[i][0]<controlProb || i==probabilities.length-1 || probabilities[i][0]!=0)
            {
                controlProb=probabilities[i][0];
                for(int j=indexB; j<indexA;j++)
                {
                    if(realClasses[j].equals("positive"))
                    {
                        p++;
                    }
                    else if(realClasses[j].equals("negative"))
                    {
                        n++;
                    } 
                    
                    indexB=j+1;
                }
                
                if(p>n && 0!=n)
                {
                    
                    control=p/n;
                    for(int k=0; k<p+n;)
                    {
                        for(int l=0;l<control;l++)
                        {
                            output.add("positive");
                        }
                        k=k+control+1;
                        output.add("negative");
                        for(int l=0;l<control;l++)
                        {
                            output.add("positive");
                        }
                        k+=control; 
                    }
                }
                else if(n>p && 0!=p)
                {
                    control=n/p;
                    for(int k=0; k<p+n;)
                    {
                        for(int l=0;l<control;l++)
                        {
                            output.add("negative");
                        }
                        k=k+control+1;
                        output.add("positive");
                        for(int l=0;l<control;l++)
                        {
                            output.add("negative");
                        }
                        k+=control;
                    }
                }
                else if(p==0)
                {
                    for(int k=0; k<n;k++)
                       output.add("negative");
                }
                else if(n==0)
                {
                    for(int k=0; k<p;k++)
                       output.add("positive");
                }
                p=0;
                n=0;
            }
            else if(probabilities[i][0]==0.0)
            {
                for(int j=indexB; j<probabilities.length;j++)
                {
                    if(realClasses[j].equals("positive"))
                    {
                        p++;
                    }
                    else if(realClasses[j].equals("negative"))
                    {
                        n++;
                    }    
                    indexB=j+1;
                }
                if(p>n && 0!=n)
                {
                    control=p/n;
                    for(int k=0; k<p+n;)
                    {
                        for(int l=0;l<control;l++)
                        {
                            output.add("positive");
                        }
                       
                        k+=+control+1;
                        output.add("negative");
                        for(int l=0;l<control;l++)
                        {
                            output.add("positive");
                        }
                       
                        k+=control;
                    }
                }
                else if(n>p && 0!=p)
                {
                    control=n/p;
                    for(int k=0; k<p+n;)
                    {
                        for(int l=0;l<control;l++)
                        {
                            output.add("negative");
                        }
                        
                        k+=+control+1;
                        output.add("positive");
                        for(int l=0;l<control;l++)
                        {
                            output.add("negative");
                        }
                        
                        k+=control;
                    }
                }
                else if(p==0)
                {
                    for(int k=0; k<n;k++)
                       output.add("negative");
                }
                else if(n==0)
                {
                    for(int k=0; k<n;k++)
                       output.add("positive");
                }
                p=0;
                n=0;
                out=true;
            }
        }
        output.add(realClasses[probabilities.length-1]);
        return output;
    } 
  /**
   *<p>
   * Sort the probabilities and the realclasses acording to the max value of the probabilities.
   *</p>
   * @param realClasses The array with the strings (real classes) to sort. 
   * @param probabilities The matrix with the probabilities to sort. 
   * @param col Column on which sort 
   */
    
    public void sort(double[][] probabilities, String[] realClasses, int col) 
    {
        if (col < 0 || col > probabilities[0].length)
        {
            return;
        }

        double auxP;
        String auxC;

        for (int i = 0; i < probabilities.length; i++) 
        {
            for (int j = i + 1; j < probabilities.length; j++) 
            {  
                if (probabilities[i][col]<probabilities[j][col]) 
                {
                    auxC = realClasses[i];
                    realClasses[i] = realClasses[j];
                    realClasses[j] = auxC;
                    
                    auxP = probabilities[i][col];
                    probabilities[i][col] = probabilities[j][col];
                    probabilities[j][col]=auxP;
                }
            }
        }
    }
}
