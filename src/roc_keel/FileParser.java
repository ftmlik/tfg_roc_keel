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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p> Implements methods to manage the imput files and the output files in latex. 
 * @author joseangeldiazg (University of Granada) 
 * @version 1.0
 * </p>
 */
public class FileParser 
{
    
    protected List<String> lines;
    protected int columns;
    protected int rows; 
    protected double probabilities [][];
    protected String realClasses[];
    protected String differentClasses[];
    
    public FileParser()
    {
        this.lines = new ArrayList<>();
        this.columns=0;
        this.rows=0;
        this.probabilities=null;
        this.realClasses=null;
        this.differentClasses=null;
    }
    
    /**
     * <p>
     * Get the probabilities.
     * </p>
     * @return A matrix of strings with the probabilities
     */
    public double [][] getProbabilities()
    {
        return this.probabilities;
    }
    /**
     * <p>
     * Get the real Classes.
     * </p>
     * @return Array of strings with the real class for each  row of probabilities
     */
    public String [] getRealClasses()
    {
        return this.realClasses;
    }
    
    /**
     * <p>
     * Get the different classes.
     * </p>
     * @return Array of strings with nominal values of the different classes 
     */
    public String [] getDifferentClasses()
    {
        return this.differentClasses;
    }
    
    /**
     * <p>
     * Builder of the class. Initialize the structures. 
     * </p>
     * @param filename String with the name of the file to build the matrix of probabilities
     */
    
    public FileParser(String filename) throws IOException
    {
        FileReader fileReader = new FileReader(filename);
        this.lines = new ArrayList<>();
        int rows = 0;
        try (BufferedReader bufferedReader = new BufferedReader(fileReader)) 
        {
            
            String line = null;
            
            while ((line = bufferedReader.readLine()) != null )
            {
                
                if(!line.isEmpty())
                {
                    rows++;
                    this.lines.add(line);
                }
            }
        }     
        this.rows=rows-1;
        this.columns=columnCount(lines.get(0));
        this.probabilities= new double[rows-1][columnCount(lines.get(0))-1];
        this.realClasses = new String[rows-1];
        this.differentClasses = new String [columnCount(lines.get(0))-1];
    }
    
    /**
     * <p>
     * Count the number of columns in a String.
     * </p>
     * @param s String to obtain the number of columns
     */
    public static int columnCount(String s)
    {
        if (s == null)
            return 0;
        return s.trim().split("\\s+").length;
    }   
    
    /**
     * <p>
     * Build the matrix with the probabilities.
     * </p>
     */
    
    public void buildMatrix()
    {
        String[] cols = lines.get(0).trim().split("\\s+");
        
        for(int i=1; i<cols.length; i++)
        {
            differentClasses[i-1]=cols[i];
        }
        
        for(int i=1; i<this.lines.size(); i++)
        {
            cols = lines.get(i).trim().split("\\s+");
            realClasses[i-1]=cols[0];
            
            for(int j=1; j<this.columns;j++)
            {
                probabilities[i-1][j-1]=Double.parseDouble(cols[j]);
            }
        }
    }
}
