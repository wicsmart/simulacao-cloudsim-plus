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
        
        final int[] CARGA = {130, 140, 120, 160};
        final int[] CARGA2 = {2900};
        
        int minutos = 2;
        int vm1 = 2; int vm2 = 2;
        double lenght2 = 1;
        String name =  Integer.toString(minutos)+"min-"+
                Integer.toString(vm1)+"x"+Integer.toString(vm2)+"-teste";
        
        name = "timeshared0";
        SimulacaoTeste sim1 = new SimulacaoTeste(vm1,vm2, 60*minutos, CARGA2, name, lenght2);
        sim1.run();
        
       }
    
}
