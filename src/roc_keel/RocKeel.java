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
        // The text file location 
        String filename = "/Users/joseadiazg/Desktop/TFG/probabilistic_keel/output/MLP/prob.tst";
        String filename2 = "/Users/joseadiazg/Desktop/TFG/probabilistic_keel/output/C45/Prob-resultC45.tst";
        FileParser rf = new FileParser(filename);  
        
        FileParser rf2 = new FileParser(filename2);
        
        
        String matrix [][] = new String [rf.rows][rf.columns];
        
        matrix=rf.buildMatrix();
        
        String matrix2 [][] = new String[rf2.rows][rf2.columns];
        
        matrix2=rf2.buildMatrix();
        
        RocCoord roc = new RocCoord();
        
        
        
        //roc.buildTwoClassRoc(matrix);
        
        //roc.buildClassClassRoc(matrix,1,2);
        
        roc.buildTwoClassRoc(matrix);
        double auc= roc.rocArea();
        
        String [] curves = new String[1];
        
        curves[0]=roc.getCoord();
        
        String salida=rf.printLatexHeader("TRAINING", "TST");
        salida+=rf.printROC(curves, curves.length);
        salida+=rf.printLatexBody("Prob-salidaNB.tra", "TRA");
        salida+=rf.printLatexFooter();
        
        try
        {
            FileWriter fichero = null;
            fichero = new FileWriter("/Users/joseadiazg/Desktop/salida.tex");
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
