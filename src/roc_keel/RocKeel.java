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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import org.core.Files;

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
        
        String configuration="";
      
        if (args.length != 1)
        {
            System.err.println("Error. A parameter is only needed.");	
        }
        else
        {
            configuration=args[0];
        }
        
        Files file = new Files();
        file.readConfiguracion(configuration);
        int nclass=0;
        String [] curvesTra;
        String [] curvesTest;
        String salida;
        
        double [] aucTra;
        double [] aucTest;
        boolean twoClass=true;
        FileParser test = new FileParser(file.getTestFile());  
        FileParser tra = new FileParser(file.getTrainFile());
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
            fichero = new FileWriter(file.getOutFile());
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
