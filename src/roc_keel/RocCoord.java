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
   
   public void buildClassClassRoc(String[][] matrix, int classA, int classB)
   {
       boolean[] success= new boolean[matrix.length];
       char [] trueClass= new char [matrix.length];   
       
       for(int i=1; i<matrix.length-1; i++)
       {
           
          if(matrix[i][0].equals(matrix[0][classA]) && Double.parseDouble(matrix[i][classA])>Double.parseDouble(matrix[i][classB]))
          {
             this.tp++;
             success[i]=true;
             trueClass[i]='P';
          }
          else if(matrix[i][0].equals(matrix[0][classA]) && Double.parseDouble(matrix[i][classA])<Double.parseDouble(matrix[i][classB]))
          {
              this.fp++;
              success[i]=false;
              trueClass[i]='P';  
          } 
          else if(matrix[i][0].equals(matrix[0][classB]) && Double.parseDouble(matrix[i][classB])>Double.parseDouble(matrix[i][classA]))
          {
              this.tn++;
              success[i]=true;
              trueClass[i]='N';
          }
          else if(matrix[i][0].equals(matrix[0][classB]) && Double.parseDouble(matrix[i][classB])<Double.parseDouble(matrix[i][classA]))
          {
              this.fn++;
              success[i]=false;
              trueClass[i]='N';
          }
          else
          {
              //case for a different class than classA and classB
              trueClass[i]='X';
          }
       }
       
       computeCoordsRoc(success,trueClass);
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
              success[i]=false;
              trueClass[i]='P'; 
          } 
          else if(matrix[i][0].equals(matrix[0][2]) && Double.parseDouble(matrix[i][2])>Double.parseDouble(matrix[i][1]))
          {
              this.tn++;
              success[i]=true;
              trueClass[i]='N';
          }
          else
          {
              this.fn++;
              success[i]=false;
              trueClass[i]='N';
          }
       }
       
       computeCoordsRoc(success,trueClass);
   }
   
   public void buildClassVsAllRoc(String[][] matrix, int classA)
   {
       int numOfClass = matrix[0].length-1;
       boolean[] success= new boolean[matrix.length];
       char [] trueClass= new char [matrix.length];   
       
        for(int classB=1;classB<numOfClass;classB++)
        {
           for(int i=1; i<matrix.length-1; i++)
           {
                
                if(classB!=classA){ 
                    if(matrix[i][0].equals(matrix[0][classA]) && Double.parseDouble(matrix[i][classA])>Double.parseDouble(matrix[i][classB]))
                    {
                       this.tp++;
                       success[i]=true;
                       trueClass[i]='P'; 
                    }
                    else if(matrix[i][0].equals(matrix[0][classA]) && Double.parseDouble(matrix[i][classA])<Double.parseDouble(matrix[i][classB]))
                    {
                        success[i]=false;
                        this.fp++;
                        trueClass[i]='P'; 
                    } 
                    else if(matrix[i][0].equals(matrix[0][classB]) && Double.parseDouble(matrix[i][classB])>Double.parseDouble(matrix[i][classA]))
                    {
                        this.tn++;
                        success[i]=true;
                        trueClass[i]='N';
                    }
                    else if(matrix[i][0].equals(matrix[0][classB]) && Double.parseDouble(matrix[i][classB])<Double.parseDouble(matrix[i][classA]))
                    {
                        this.fn++;
                        success[i]=false;
                        trueClass[i]='N';
                    }
                    else
                    {
                        //case for a different class than classA and classB
                        trueClass[i]='X';
                    }
                }
           }
        }    
       computeCoordsRoc(success,trueClass);
   }
   
   public void computeCoordsRoc(boolean [] success, char [] trueClass )
   {
       double moveX=1.0/(this.fp+this.tn);
       double moveY=1.0/(this.tp+this.fn);
       double x=0;
       double y=0;
       
       this.coord="coordinates { (0,0)";
       
       
       for(int i=0; i<success.length;i++)
       {
           //true positive
           if(success[i]==true && trueClass[i]=='P')
           {
               //this.coord+="("+x+","+(y+=moveY)+")";
               y+=moveY;
           }
           // false positive
           else if(success[i]==true && trueClass[i]=='N')
           {
               //this.coord+="("+(x+=moveX)+","+y+")";
               x+=moveX;
           }
           //false negative
           if(success[i]==false && trueClass[i]=='P')
           {
               this.coord+="("+(x+=moveX)+","+y+")";
           }
           //false negative
           else if(success[i]==false && trueClass[i]=='N')
           {
               this.coord+="("+x+","+(y+=moveY)+")";
           }
       }       
      this.coord+=" };";
      
      isPerfect(this.coord);
   }
   
   public double rocArea() 
   {
       double recall=0;
       double specificity=0;
       
       specificity=((double)this.tn/((double)this.fp+(double)this.tn));
       recall=((double)this.tp/((double)this.tp+(double)this.fn));
       
       return (double)(recall+specificity)/2;  
   }
   
   
   
   public void isPerfect(String coord)
   {
       if(coord.equals("coordinates { (0,0) };"))
       {
           this.coord="coordinates { (0,0)(0,1)(1,1)};";
       }
   }
}
