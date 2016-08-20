/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roc_keel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;
import org.core.Files;

/**
 *
 * @author joseadiazg
 */
public class Roc {
    
    
    
    protected String[] testFiles;
    protected String outFile;
    protected boolean allPoints;
    protected double[][] probabilities;
    protected String[] realClasses;
    protected String[] differentClasses;
    protected boolean markers;
    
    
    /**
     * <p>
     * Get the testFile.
     * </p>
     * @return A array of strings with the names of test files
     */
    public String[] getTestFile()
    {
        return testFiles;
    }
    /**
     * <p>
     * Get the outFile.
     * </p>
     * @return Strings with the names of utput file. 
     */
    public String getOutFile()
    {
        return outFile;
    }
    /**
     * <p>
     * Get value of the param All Points
     * </p>
     * @return Boolean. 
     */
    public boolean getAllPoints()
    {
        return allPoints;
    }
    
    Roc(String configuration) throws IOException
    {
        
        //this.readConfiguracion(configuration);
        this.config_read(configuration);
        int nclass=0;
        String [] curvesTest;
        String salida;
        
        double [] aucTest;
        boolean twoClass=true;
         
        RocCoord rocTest = new RocCoord();

        //
        
        FileParser [] test = new FileParser[this.getTestFile().length];
        FileParser [] testReplicate = new FileParser[2];
        for(int i=0; i<test.length;i++)
        {
            test[i]=new FileParser(this.testFiles[i]);
            test[i].buildMatrix();
        }
        
        this.unify(test);
        
        
        FileParser example1 = new FileParser();
        FileParser example2 = new FileParser();
        
        example1.setProbabilities(this.probabilities);
        example2.setProbabilities(this.probabilities);
        example1.setDifferentClasses(this.differentClasses);
        example2.setDifferentClasses(this.differentClasses);
        example1.setRealClasses(this.realClasses);
        example2.setRealClasses(this.realClasses);
        
        testReplicate[0]=example1;
        testReplicate[1]=example2;
        
        
        this.unify(testReplicate);
        //replicate the examples
              
        nclass=test[0].columns-1;
        curvesTest= new String[nclass];
        aucTest = new double[nclass];
        
        
        if(nclass==2)
        {
            rocTest.buildTwoClassRoc(this.probabilities,this.realClasses, this.allPoints);
        }
        else
        {
            twoClass=false;
            for(int i=0; i<nclass; i++)
            {
                rocTest.buildClassVsAllClassRoc(this.probabilities,this.realClasses,this.differentClasses, i, this.allPoints);
                curvesTest[i]=rocTest.getCoord();
                aucTest[i]=rocTest.auc;
            }
        }
        if(twoClass)
        {
            salida=this.printLatexHeader();
            salida+=this.printROC(rocTest.getCoord(),0);  
            salida+=this.printAUC(rocTest.auc);
            salida+=this.printLatexFooter();
        }
        else
        {
            salida=this.printLatexHeader();
            for(int i=0; i<nclass; i++)
            {
                salida+=this.printROC(curvesTest[i],i);  
                salida+=this.printAUC(aucTest[i]); 
            }
            salida+=this.printROCS(curvesTest, nclass);
            salida+=this.printLatexFooter();
            
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
     * @param fileParam Name of the configuration script  
     * 
     */	

    private void config_read(String fileParam) 
    {
        File inputFile = new File(fileParam);

	if (inputFile == null || !inputFile.exists()) 
        {
            System.out.println("parameter " + fileParam + " file doesn't exists!");
            System.exit(-1);
	}
	// begin the configuration read from file
	try 
        {
            FileReader file_reader = new FileReader(inputFile);
            BufferedReader buf_reader = new BufferedReader(file_reader);
            String line;

            do 
            {
		line = buf_reader.readLine();
            }
            while (line.length() == 0); // avoid empty lines for processing
			// ->
			// produce exec failure
            String out[] = line.split("name = ");

            //imput data
            
            do 
            {
		line = buf_reader.readLine();
            } while (line.length() == 0);
	
            out = line.split("inputData = ");
            out = out[1].split("\\s\"");
            
            this.testFiles = new String[out.length];
            this.testFiles[0]=new String(out[0].substring(1,out[0].length() - 1));
            
            for(int i=1; i<out.length;i++)
            {
                this.testFiles[i]=new String(out[i].substring(0,out[i].length() - 1));
            }
            
            //output data
            
            
            do 
            {
		line = buf_reader.readLine();
            } while (line.length() == 0);
            
            out = line.split("outputData = ");
            out = out[1].split("\\s\'");
            this.outFile = new String(out[0].substring(1,out[0].length()-3));

            // parameters
            
            do 
            {
		line = buf_reader.readLine();
            } while (line.length() == 0);
            
            out = line.split("all-points = ");
            this.allPoints = (Boolean.valueOf(out[1])); 
            
            do 
            {
		line = buf_reader.readLine();
            } while (line.length() == 0);
            
            out = line.split("display-markers = ");
            this.markers = (Boolean.valueOf(out[1])); 
			
	} 
        catch (IOException e) 
        {
            System.out.println("IO exception = " + e);
            e.printStackTrace();
            System.exit(-1);
	}
    }
    
    private void unify(FileParser[] files)
    {
        
        double m[][] = files[0].getProbabilities();
        double m2[][]=null;
        double m3[][]=null;
        
        String s[] = files[0].getRealClasses();
        String s2[]=null;
        String s3[]=null;
        
        for(int i=1; i<files.length;i++)
        {
            m2=files[i].getProbabilities();
            m3=new double[m2.length+m.length][];
            System.arraycopy(m, 0, m3, 0, m.length);
            System.arraycopy(m2, 0, m3, m.length, m2.length);
            m=m3;
            
            s2=files[i].getRealClasses();
            s3=combine(s,s2);
            s=s3;
        }
        
        String c[]=files[0].getDifferentClasses();
        
        this.probabilities=m3;
        this.realClasses=s3;
        this.differentClasses=c;
    }
    
    public static String[] combine(String[] a, String[] b)
    {
        int length = a.length + b.length;
        String[] result = new String[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result ;
    }
    
     /**
     * <p>
     * Print the header in a latex file.
     * </p>
     * @param file The name of the file.
     */
    
    public String printLatexHeader()
    {
        return  "\\documentclass{article}\n" +
                "\\usepackage{pgfplots}\n" +
                "\\title{KEEL: ROC output}\n" +
                "\\begin{document}\n" +
                "\\maketitle\n" +
                "\\pagebreak[4]\n"+
                "\\hfill \\break\n" +
                "Section one: TEST FILE\n"+
                "\\hfill \\break\n"+
                "\\hfill \\break\n";
    }
    
    /**
     * <p>
     * Print the name of a new plot in the middle of the tex file.
     * </p>
     * @param file The name of the file.
     * @return String with the necessary latex commands. 
     */
    
    public String printLatexBody(String file)
    {
        return  "\\hfill \\break\n" +
                "File: "+file+"\n"+
                "\\hfill \\break\n";
    }
    
    /**
     * <p>
     * Print the auc value in the tex file.
     * </p>
     * @param auc auc´s value.
     * @return String with the necessary latex commands.  
     */
    
    public String printAUC(Double auc)
    {
        return  "\\hfill \\break\n" +
                "\\hfill \\break\n" +
                " Area Under Curve (AUC):"+auc+"\n" +
                "\\hfill \\break\n" +
                "\\hfill \\break\n";
    }
    
    /**
     * <p>
     * Print the ROC curve in latex.
     * </p>
     * @param coords Coordinates of ROC points.
     * @return String with the necessary latex commands. 
     */
    
    public String printROC(String coords, int c)
    {
        String a = "\\begin{tikzpicture}\n" +
                "\\begin{axis} [xlabel=False positive rate,\n" +
                "ylabel=True positive rate,"+ 
                "axis x line=bottom,\n" +
                "axis y line=left, legend pos=south east]\n"+
                "\\addplot ";
        if(!markers)
            a+= " [mark=none]";
        
        return  a+coords+
                "\\addlegendentry{"+this.differentClasses[c]+" vs All}\n"+
                "\\end{axis}\n" +
                "\\end{tikzpicture}";
    }
    
    /**
     * <p>
     * Print more than one ROC curve.
     * </p>
     * @param rocCoords String with the coordinates for each ROC curve.
     * @param numROC Number of ROC curves to plot. 
     * @return String with the necessary latex commands. 
     */
    public String printROCS(String [] rocCoords, int numROC)
    {
        String rocs ="";
        String a;
        
        for(int i=0; i<numROC; i++)
        {
            rocs+="\\addplot ";
            a ="";
                    
            rocs+=a+rocCoords[i]+"\n";
            rocs+="\\addlegendentry{"+this.differentClasses[i]+" vs All}\n";
        }
        
        return  "\\begin{tikzpicture}\n" +
                "\\begin{axis} [xlabel=False positive rate,\n" +
                "ylabel=True positive rate,"+ 
                "axis x line=bottom,\n" +
                "axis y line=left  , cycle list name=color list, legend style={at={(0.5,-0.17)},anchor=north,legend cell align=left}]\n"+
                rocs+
                "\\end{axis}\n" +
                "\\end{tikzpicture}";
    }
    
    /**
     * <p>
     * Print the necessary commands to close a tex file.
     * </p>
     * @return String with the necessary latex commands. 
     */
    
    public String printLatexFooter()
    {
        return  "\\end{document}";
    }
}


