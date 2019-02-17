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
import org.cloudsimplus.examples.ParallelSimulationsExample;
import org.cloudsimplus.util.Log;

/**
 *
 * @author wictor
 */
public class AppTeste {

    public static void main(String[] args) throws IOException, InterruptedException {

       Log.setLevel(Level.OFF);
        
        double lenght1 = 1;
        double lenght2 = 1;
        int vm1 = 2;
        int vm2 = 2;
        
        SimulacaoLive sim = new SimulacaoLive(vm2, vm2, "live3", lenght1, lenght2);
        sim.run();
/*
        List<SimulacaoTeste> list = new ArrayList<>(4);
        
        list.add(new SimulacaoTeste(vm1, vm2, "8h-1s", lenght1, lenght2));
        list.add(new SimulacaoTeste(vm1, vm2, "9h-1s", lenght1, lenght2));
        list.add(new SimulacaoTeste(vm1, vm2, "10h-1s", lenght1, lenght2));
        list.add(new SimulacaoTeste(vm1, vm2, "11h-1s", lenght1, lenght2));
        list.add(new SimulacaoTeste(vm1, vm2, "12h-1s", lenght1, lenght2));
        list.add(new SimulacaoTeste(vm1, vm2, "13h-1s", lenght1, lenght2));
        list.add(new SimulacaoTeste(vm1, vm2, "14h-1s", lenght1, lenght2));
        list.add(new SimulacaoTeste(vm1, vm2, "15h-1s", lenght1, lenght2));
        list.add(new SimulacaoTeste(vm1, vm2, "16h-1s", lenght1, lenght2));
        list.add(new SimulacaoTeste(vm1, vm2, "17h-1s", lenght1, lenght2));
        list.add(new SimulacaoTeste(vm1, vm2, "18h-1s", lenght1, lenght2));
        list.add(new SimulacaoTeste(vm1, vm2, "19h-1s", lenght1, lenght2));
        list.add(new SimulacaoTeste(vm1, vm2, "20h-1s", lenght1, lenght2));
        list.add(new SimulacaoTeste(vm1, vm2, "21h-1s", lenght1, lenght2));
   ;
        list.parallelStream().forEach(SimulacaoTeste::run);
*/
    }

}
