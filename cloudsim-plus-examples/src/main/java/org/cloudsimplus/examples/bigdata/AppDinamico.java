/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.examples.bigdata;

import java.io.IOException;


/**
 *
 * @author wictor
 */
public class AppDinamico {

    public static void main(String[] args) throws IOException {
   
        final int[] CARGA = {65, 110, 85, 120};
        final int[] CARGA1 = {90, 100, 130, 100};
        
        /*
          Parametros:
          1 - Quant vms do coletor
          2 - Quant vms do coreback 
          3 - Tempo de simulação
          4 - Carga de arquivos por segundo
          5 - Nome da simulação
         */
        int minutos = 2;
        double lenght2 = 1.1;
//        String name = "vms3310%";
//        SimulacaoDinamica sim1 = new SimulacaoDinamica(3, 3, 60*minutos , CARGA1, name, lenght2);
//        sim1.run();
//        
//        name = "vms3410%";
//        SimulacaoDinamica sim2 = new SimulacaoDinamica(3, 4,60*minutos , CARGA1, name, lenght2);
//        sim2.run();
//        
//        name = "vms3320%";
//        lenght2 = 1.2;
//        SimulacaoDinamica sim3 = new SimulacaoDinamica(3, 3,60*minutos , CARGA1, name, lenght2);
//        sim3.run();
//        
        String name = "vms3420%";
        SimulacaoDinamica sim4 = new SimulacaoDinamica(3, 4,60*minutos , CARGA1, name, lenght2);
        sim4.run();
        
       }

}
