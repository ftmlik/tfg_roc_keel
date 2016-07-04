/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roc_keel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author joseadiazg
 */
public class RocKeel
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException 
    { 
      /*
        // The text file location 
        String filename = "/Users/joseadiazg/Desktop/TFG/roc_keel/input/sencillo.tst";
        //String filename = "/Users/joseadiazg/Desktop/TFG/roc_keel/input/prob.tst";
        //String filename = "/Users/joseadiazg/Desktop/TFG/roc_keel/input/iout.tra";
        //String filename2 = "/Users/joseadiazg/Desktop/TFG/probabilistic_keel/output/KNN/Prob-resultadoKNN.tra";
        //String filename2 = "/Users/joseadiazg/Desktop/TFG/probabilistic_keel/output/SMO/Prob-salidaSMO.tst";
        String filename2 = "/Users/joseadiazg/Desktop/TFG/probabilistic_keel/output/NB/Prob-salidaNB.tra";
        
      
        FileParser rf = new FileParser(filename);  
        
        
        FileParser rf2 = new FileParser(filename2);
        
        
        String matrix [][] = new String [rf.rows][rf.columns];
        
        matrix=rf.buildMatrix();
        
        String matrix2 [][] = new String[rf2.rows][rf2.columns];
        
        matrix2=rf2.buildMatrix();
        
        RocCoord roc = new RocCoord();
        
        
        
        roc.buildTwoClassRoc(matrix);
        
        //roc.buildClassClassRoc(matrix,1,2);
        
        //roc.buildTwoClassRoc(matrix2);
        
        
        //roc.buildClassVsAllClassRoc(matrix2, 2);
       
        String [] curves = new String[1];
     
        
        */
        String testFile = "/Users/joseadiazg/Desktop/TFG/probabilistic_keel/output/NB/Prob-salidaNB.tst";
        String traFile  = "/Users/joseadiazg/Desktop/TFG/probabilistic_keel/output/NB/Prob-salidaNB.tra";
        int nclass=0;
        String [] curvesTra;
        String [] curvesTest;
        String salida;
        
        double [] aucTra;
        double [] aucTest;
        boolean twoClass=true;
        FileParser test = new FileParser(testFile);  
        FileParser tra = new FileParser(traFile);
        RocCoord rocTest = new RocCoord();
        RocCoord rocTra = new RocCoord();
        
        String matrixTest [][] = new String [test.rows][test.columns];
        
        matrixTest=test.buildMatrix();
        
        String matrixTra [][] = new String[tra.rows][tra.columns];
        
        matrixTra=tra.buildMatrix();
        
        //get the num of classes
               
        nclass=tra.columns-1;
        curvesTra= new String[nclass];
        curvesTest= new String[nclass];
        aucTra = new double[nclass];
        aucTest = new double[nclass];
        
        
        if(nclass==2)
        {
            rocTest.buildTwoClassRoc(matrixTest);
            rocTra.buildTwoClassRoc(matrixTra);
        }
        else
        {
            twoClass=false;
            for(int i=0; i<nclass; i++)
            {
                rocTra.buildClassVsAllClassRoc(matrixTra, i+1);
                curvesTra[i]=rocTra.getCoord();
                aucTra[i]=rocTra.auc;
                
                rocTest.buildClassVsAllClassRoc(matrixTest, i+1);
                curvesTest[i]=rocTest.getCoord();
                aucTest[i]=rocTest.auc;
            }
        }
        
        if(twoClass)
        {
            salida=test.printLatexHeader("TEST");
            salida+=test.printROC(rocTest.getCoord());  
            salida+=test.printAUC(rocTest.auc);
            
            salida+=test.printLatexBody("TRAINING");
            salida+=tra.printROC(rocTra.getCoord());
            salida+=tra.printAUC(rocTra.auc);
            salida+=test.printLatexFooter();
        }
        else
        {
            salida=test.printLatexHeader("TEST");
            for(int i=0; i<nclass; i++)
            {
                salida+=test.printROC(curvesTest[i]);  
                salida+=test.printAUC(aucTest[i]); 
            }
            salida+=test.printROCS(curvesTest, nclass);
            salida+=test.printLatexBody("TRAINING");
            
            for(int i=0; i<nclass; i++)
            {
                salida+=tra.printROC(curvesTra[i]);  
                salida+=tra.printAUC(aucTra[i]); 
            }
            salida+=tra.printROCS(curvesTra, nclass);
            salida+=test.printLatexFooter();
            
        }
        

        /*curves[0]=roc.getCoord();
        
        String salida=rf.printLatexHeader("TRA");
        salida+=rf.printROCS(curves, curves.length);
        salida+=rf.printAUC(roc.auc);
        salida+=rf.printLatexBody("TRA");
        salida+=rf.printLatexFooter();*/
        
        try
        {
            FileWriter fichero = null;
            fichero = new FileWriter("/Users/joseadiazg/Desktop/TFG/roc_keel/output/salida3.tex");
            BufferedWriter out = new BufferedWriter(fichero);
            out.write(salida);
            out.close();
            fichero.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }  
        
}
