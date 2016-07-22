/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roc_keel;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;
import org.core.Files;

/**
 *
 * @author joseadiazg
 */
public class Roc {
    
    
    
    protected String trainFile;
    protected String testFile;
    protected String outFile;


    public String getTrainFile()
    {
        return trainFile;
    }
    
    public String getTestFile()
    {
        return testFile;
    }
    public String getOutFile()
    {
        return outFile;
    }
    
    Roc(String configuration) throws IOException
    {
        
        this.readConfiguracion(configuration);
        int nclass=0;
        String [] curvesTra;
        String [] curvesTest;
        String salida;
        
        double [] aucTra;
        double [] aucTest;
        boolean twoClass=true;
        FileParser test = new FileParser(this.testFile);  
        FileParser tra = new FileParser(this.trainFile);
        RocCoord rocTest = new RocCoord();
        RocCoord rocTra = new RocCoord();

        
        test.buildMatrix();
        
        tra.buildMatrix();
               
        nclass=tra.columns-1;
        curvesTra= new String[nclass];
        curvesTest= new String[nclass];
        aucTra = new double[nclass];
        aucTest = new double[nclass];
        
        
        if(nclass==2)
        {
            rocTest.buildTwoClassRoc(test.getProbabilities(),test.getRealClasses());
            rocTra.buildTwoClassRoc(tra.getProbabilities(),tra.getRealClasses());
        }
        else
        {
            twoClass=false;
            for(int i=0; i<nclass; i++)
            {
                rocTra.buildClassVsAllClassRoc(tra.getProbabilities(),tra.getRealClasses(),tra.getDifferentClasses(), i);
                curvesTra[i]=rocTra.getCoord();
                aucTra[i]=rocTra.auc;
                
                rocTest.buildClassVsAllClassRoc(test.getProbabilities(),test.getRealClasses(), test.getDifferentClasses(), i);
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
       
        try
        {
            FileWriter fichero = null;
            fichero = new FileWriter(this.outFile);
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
    
    
    /** 
     * Reads configuration script, and extracts its contents.
     * 
     * @param script Name of the configuration script  
     * 
     */	
    public void readConfiguracion (String script) 
    {

        String fichero, linea, token;
        StringTokenizer lineasFichero, tokens;
        byte line[];
        int i, j;

        fichero = Files.readFile(script);
        lineasFichero = new StringTokenizer (fichero,"\n");

        lineasFichero.nextToken();
        linea = lineasFichero.nextToken();

        tokens = new StringTokenizer (linea, "=");
        tokens.nextToken();
        token = tokens.nextToken();

        //Getting the names of training and test files
        //reference file will be used as comparision

        line = token.getBytes();
        for (i=0; line[i]!='\"'; i++);
        i++;
        for (j=i; line[j]!='\"'; j++);
        testFile = new String (line,i,j-i);
        for (i=j+1; line[i]!='\"'; i++);
        i++;
        for (j=i; line[j]!='\"'; j++);
        trainFile = new String (line,i,j-i);

        //Getting the path and base name of the results files

        linea = lineasFichero.nextToken();
        tokens = new StringTokenizer (linea, "=");
        tokens.nextToken();
        token = tokens.nextToken();

        //Getting the names of output files

        line = token.getBytes();
        for (i=0; line[i]!='\"'; i++);
        i++;
        for (j=i; line[j]!='\"'; j++);
        outFile = new String (line,i,j-i);

    } //end-method
}


