/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.examples.bigdata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.cloudsimplus.util.Log;

/**
 *
 * @author wictor
 */
public class AppDinamico {

    public static void main(String[] args) throws IOException {
   
        final int[] CARGA = {65, 110, 85, 120};
        /*
          Parametros:
          1 - Quant vms do coletor
          2 - Quant vms do coreback 
          3 - Tempo de simulação
          4 - Carga de arquivos por segundo
          5 - Nome da simulação
         */
        int minutos = 2;
        String name = "testeDinamico";
        SimulacaoDinamica sim = new SimulacaoDinamica(2, 2, 60*minutos, CARGA, name);
        sim.run();
        
    }

}
