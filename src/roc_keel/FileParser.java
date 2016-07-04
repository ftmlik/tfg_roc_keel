/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roc_keel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joseadiazg
 */
public class FileParser 
{
    
    protected List<String> lines;
    protected int columns;
    protected int rows;
    
    public FileParser()
    {
        lines = new ArrayList<>();
        this.columns=0;
        this.rows=0;
    }
    
    public FileParser(String filename) throws IOException
    {
        FileReader fileReader = new FileReader(filename);
        this.lines = new ArrayList<>();
        int rows = 0;
        try (BufferedReader bufferedReader = new BufferedReader(fileReader)) 
        {
            
            String line = null;
            
            while ((line = bufferedReader.readLine()) != null)
            {
                rows++;
                this.lines.add(line);
                
            }
        }     
        this.rows=rows;
        this.columns=columnCount(lines.get(0));
    }
    
    
    public static int columnCount(String s)
    {
        if (s == null)
            return 0;
        return s.trim().split("\\s+").length;
    }   
    
    public String[][] buildMatrix()
    {
        String matrix [][];
        matrix= new String [this.rows][this.columns];
        
        for(int i=0; i<this.lines.size(); i++)
        {
            String[] cols = lines.get(i).trim().split("\\s+");
     
            for(int j=0; j<this.columns;j++)
            {
                matrix[i][j]=cols[j];
            }
        }
        return matrix;
    }
    
    public String printLatexHeader(String file)
    {
        return  "\\documentclass{article}\n" +
                "\\usepackage{pgfplots}\n" +
                "\\title{KEEL: ROC output}\n" +
                "\\begin{document}\n" +
                "\\maketitle\n" +
                "\\hfill \\break\n" +
                "File: "+file+"\n"+
                "\\hfill \\break\n"+
                "\\hfill \\break\n";
    }
    
    public String printLatexBody(String file)
    {
        return  "\\hfill \\break\n" +
                "File: "+file+"\n"+
                "\\hfill \\break\n";
    }
    
    public String printAUC(Double auc)
    {
        return  "\\hfill \\break\n" +
                " AUC:"+auc+"\n" +
                "\\hfill \\break\n";
    }
    
    
    public String printROC(String coords)
    {
        
        return  "\\begin{tikzpicture}\n" +
                "\\begin{axis} [xlabel=False positive rate,\n" +
                "ylabel=True positive rate,"+ 
                "axis x line=bottom,\n" +
                "axis y line=left]\n"+
                "\\addplot "+coords+
                "\\end{axis}\n" +
                "\\end{tikzpicture}";
    }
    
    public String printROCS(String [] rocCoords, int numROC)
    {
        String rocs ="";
        
        for(int i=0; i<numROC; i++)
        {
            rocs+="\\addplot "+rocCoords[i]+"\n";
        }
        
        return  "\\begin{tikzpicture}\n" +
                "\\begin{axis} [xlabel=False positive rate,\n" +
                "ylabel=True positive rate,"+ 
                "axis x line=bottom,\n" +
                "axis y line=left]\n"+
                rocs+
                "\\end{axis}\n" +
                "\\end{tikzpicture}";
    }
    
    public String printLatexFooter()
    {
        return  "\\end{document}";
    }
}
