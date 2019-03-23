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
public class AppTeste {

    public static void main(String[] args) throws IOException, InterruptedException {

       Log.setLevel(Level.OFF);
       
       if (args.length < 3) {
            // adicionar log
            System.err.println("Inserir vm1 vm2 nome1 ...");
            System.exit(-1);
        }
        
        double lenght1 = 1;
        double lenght2 = 1;
        int vm1 = Integer.parseInt(args[0]);
        int vm2 = Integer.parseInt(args[1]);
                
        List<SimulacaoTeste> list = new ArrayList<>(args.length-2);
         
        for(int i= 2; i < args.length; i++){
           list.add(new SimulacaoTeste(vm1, vm2, args[i], lenght1, lenght2));
        }    
      
        list.parallelStream().forEach(SimulacaoTeste::run);

    }

}
