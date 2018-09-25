/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.examples.bigdata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.cloudsimplus.util.Log;

/**
 *
 * @author wictor
 */
public class App {

    public static void main(String[] args) throws IOException {
        List<Simulacao> listasim = new ArrayList<>(2);
        final int CARGA = 66;
        /*
          Parametros:
          1 - Quant vms do coletor
          2 - Quant vms do coreback 
          3 - Tempo de simulação
          4 - Carga de arquivos por segundo
          5 - Nome da simulação
         */

        int minutos = 1;

        String name = "22" + Integer.toString(CARGA);
        Simulacao sim2 = new Simulacao(2, 2, 60 * minutos, CARGA, name);
        name = "23" + Integer.toString(CARGA);
        Simulacao sim3 = new Simulacao(2, 3, 60 * minutos, CARGA, name);

        listasim.add(sim2);
        listasim.add(sim3);


        listasim.parallelStream().forEach(Simulacao::run);

  

   
    }

    public static String miliTotime(long mili) {
        String tempo = String.format("%02d:%02d:%02d.%03d",
                TimeUnit.MILLISECONDS.toHours(mili),
                TimeUnit.MILLISECONDS.toMinutes(mili) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mili)),
                TimeUnit.MILLISECONDS.toSeconds(mili) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mili)),
                mili - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(mili)));
        return tempo;
    }
}
