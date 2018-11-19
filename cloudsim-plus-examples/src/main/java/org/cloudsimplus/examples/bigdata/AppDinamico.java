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
public class AppDinamico{

    public static void main(String[] args) throws IOException {
       
//        int vm1 = Integer.parseInt(args[0]);
//        int vm2 = Integer.parseInt(args[1]);
//        int minutos = Integer.parseInt(args[2]);
//        int c1 = Integer.parseInt(args[3]);
//        int c2 = Integer.parseInt(args[4]);
//        
//        int[] carga = {c1,c2};
//        
        Log.setLevel(Level.INFO);
        
        List<SimulacaoDinamica> listasim = new ArrayList<>(1);
        final int[] CARGA = {65, 80, 110, 85, 130, 80};
        final int[] CARGA1 = {100, 130,140, 120, 160, 110, 90};
        final int[] CARGA2 = {4500};
        
        /*
          Parametros:
        
          1 - Quant vms do coletor
          2 - Quant vms do coreback 
          3 - Tempo de simulação
          4 - Carga de arquivos por segundo
          5 - Nome da simulação
         */
        int minutos = 1;
        int vm1 = 2; int vm2 = 2;
        double lenght2 = 1;
        String name =  Integer.toString(minutos)+"min-"+
                Integer.toString(vm1)+"x"+Integer.toString(vm2)+"-teste4";
        SimulacaoDinamica sim1 = new SimulacaoDinamica(vm1,vm2, 60*minutos, CARGA2, name, lenght2);
        sim1.run();
//        
//        name =  Integer.toString(minutos)+"min-"+
//                Integer.toString(vm1)+"x"+Integer.toString(vm2)+"carga";
//        SimulacaoDinamica sim2 = new SimulacaoDinamica(vm1,vm2, 60*minutos, CARGA, name, lenght2);
//        
//        listasim.add(sim1);
//        listasim.add(sim2);
//        listasim.add(sim3);
//        listasim.add(sim4);
//
//        listasim.parallelStream().forEach(SimulacaoDinamica::run);
//        
//        lenght2 = 1.5;
//        name = "vms335%";
//        SimulacaoDinamica sim2 = new SimulacaoDinamica(3, 4,60*minutos , CARGA, name, lenght2);
//        sim2.run();
//        
//        name = "vms3320%";
//        lenght2 = 1.2;
//        SimulacaoDinamica sim3 = new SimulacaoDinamica(3, 3,60*minutos , CARGA1, name, lenght2);
//        sim3.run();
//        
//        String name = "vms3420%";
//        SimulacaoDinamica sim4 = new SimulacaoDinamica(3, 4,60*minutos , CARGA1, name, lenght2);
//        sim4.run();
        
       }

}
