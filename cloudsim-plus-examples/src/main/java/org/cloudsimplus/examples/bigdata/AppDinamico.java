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
        final int[] CARGA1 = {65, 80, 100 ,110, 90, 70};
        /*
          Parametros:
          1 - Quant vms do coletor
          2 - Quant vms do coreback 
          3 - Tempo de simulação
          4 - Carga de arquivos por segundo
          5 - Nome da simulação
         */
        int minutos = 1;
        String name = "cargaD1";
        SimulacaoDinamica sim = new SimulacaoDinamica(3, 2,60*minutos , CARGA1, name);
        sim.run();
        
    }

}
