/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roc_keel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joseadiazg
 */
public class RocCoord 
{
   protected int tp;
   protected int fp;
   protected int tn;
   protected int fn;
   protected double auc;
   
   protected String coord;
   
   
   RocCoord()
   {
       tp=0;
       fp=0;
       tn=0;
       fn=0;
       coord = null;
   }
   
   public String getCoord()
   {
       return this.coord;
   }
   
   public void buildTwoClassRoc(String[][] matrix)
   {
       
       boolean[] success= new boolean[matrix.length];
       char [] trueClass= new char [matrix.length];   
       
       for(int i=1; i<matrix.length-1; i++)
       {
           
          if(matrix[i][0].equals(matrix[0][1]) && Double.parseDouble(matrix[i][1])>Double.parseDouble(matrix[i][2]))
          {
             this.tp++;
             success[i]=true;
             trueClass[i]='P'; 
          }
          else if(matrix[i][0].equals(matrix[0][1]) && Double.parseDouble(matrix[i][1])<Double.parseDouble(matrix[i][2]))
          {
              this.fp++;
              //this.fn++;
              success[i]=false;
              trueClass[i]='P'; 
          } 
          else if(matrix[i][0].equals(matrix[0][2]) && Double.parseDouble(matrix[i][2])>Double.parseDouble(matrix[i][1]))
          {
              this.tn++;
              success[i]=true;
              trueClass[i]='N';
          }
          else if(matrix[i][0].equals(matrix[0][2]) && Double.parseDouble(matrix[i][2])<Double.parseDouble(matrix[i][1]))
          {
              //this.fp++;
              this.fn++;
              success[i]=false;
              trueClass[i]='N';
          }
       }
       
       computeCoordsRoc(success,trueClass);
   }
   
   public void buildClassVsAllClassRoc(String[][] matrix, int classA)
   {
       String [][] result = new String[matrix.length][3];
       double max=0;
       double [] norm = new double[2];
       
       result[0][0]="True-Class";
       result[0][1]="positive";
       result[0][2]="negative";
       
       for(int i=1; i<matrix.length;i++)
       {
            result[i][1]=matrix[i][classA];
             
            if(matrix[i][0].equals(matrix[0][classA]))
            {
                result[i][0]="positive";
            }
            else
            {
                result[i][0]="negative";
            }
            
            for(int j=1; j<matrix[i].length;j++)
            {
                if(j!=classA)
                {
                   if(Double.parseDouble(matrix[i][j])>max)
                   {
                       max=Double.parseDouble(matrix[i][j]);
                   } 
                }            
            }
            norm[0]=Double.parseDouble(matrix[i][classA]);
            norm[1]=max;           
       
            norm=normalize(norm);
            
            result[i][1]=Double.toString(norm[0]);
            result[i][2]=Double.toString(norm[1]);
            max=0;
       }
       
       for(int i=0; i<result.length; i++)
       {
          for(int j=0; j<result[i].length; j++)
            {
                    System.out.print(result[i][j]+" ");
            } 
          System.out.println("\n");
       } 
       
       this.buildTwoClassRoc(result);
   }
   
   public void computeCoordsRoc(boolean [] success, char [] trueClass )
   {
       double moveX=1.0/(this.fp+this.tn);
       double moveY=1.0/(this.tp+this.fn);
       double x=0;
       double y=0;
       double auc=0;
       double width=0;
       double widthAcumulate=0;
       double heightTriangle=0;
       double heightRectangle=0;
       double htOld=0;
       double hrOld=0;
       int firstTriangle=0;
       
       this.coord="coordinates { (0,0)";
       
       
       for(int i=0; i<success.length;i++)
       {
           //true positive
           if(success[i]==true && trueClass[i]=='P')
           {
               y+=moveY;
               
           }
           // false positive
           else if(success[i]==true && trueClass[i]=='N')
           {
               x+=moveX;
           }
           //false negative
           if(success[i]==false && trueClass[i]=='P')
           {
               this.coord+="("+(x+=moveX)+","+y+")";
               htOld=heightTriangle;
               hrOld=heightRectangle;
               width=(x-widthAcumulate);
               widthAcumulate+=width;
               heightTriangle=y-htOld;
               heightRectangle=(y-(hrOld+htOld))*firstTriangle;
               auc=auc+(double)(((width*heightTriangle)/2)+(width*heightRectangle));
               firstTriangle=1;
           }
           //false negative
           else if(success[i]==false && trueClass[i]=='N')
           {          
               this.coord+="("+x+","+(y+=moveY)+")";
               htOld=heightTriangle;
               hrOld=heightRectangle;
               width=(x-widthAcumulate);  
               widthAcumulate+=width;
               heightTriangle=(y)-(hrOld+htOld);
               heightRectangle=(hrOld+htOld)*firstTriangle; 
               auc=auc+(double)(((width*heightTriangle)/2)+(width*heightRectangle)); 
               firstTriangle=1;
           }
       }   
       
      this.coord+=" };";
      
      //auc=auc+(double)((((1-widthAcumulate)*(1-(hrOld+htOld)))/2)+((1-widthAcumulate)*((hrOld+htOld)*firstTriangle)));
      this.auc=auc;
      isPerfect(this.coord);
      
   }  
   
   public void isPerfect(String coord)
   {
       if(coord.equals("coordinates { (0,0) };"))
       {
           this.coord="coordinates { (0,0)(0,1)(1,1)};";
           this.auc=1;
       }
   }
   
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
   * Normalizes the doubles in the array using the given value.
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
   * Find the max value in a doubles array.
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
}
