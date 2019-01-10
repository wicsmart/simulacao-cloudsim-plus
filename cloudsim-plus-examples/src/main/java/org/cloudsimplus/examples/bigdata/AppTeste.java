/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.examples.bigdata;

import ch.qos.logback.classic.Level;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.cloudsimplus.util.Log;


/**
 *
 * @author wictor
 */
public class AppTeste{

    public static void main(String[] args) throws IOException {

        Log.setLevel(Level.INFO);
        
        String simu = "timeshared0";
        
        double lenght1 = 1;
        double lenght2 = 1;
        int vm1 = 2;
        int vm2 = 2;
                
        String name = simu;
                
        SimulacaoTeste sim1 = new SimulacaoTeste(vm1,vm2, name,lenght1, lenght2);
        sim1.run();
        
       }
    
}
