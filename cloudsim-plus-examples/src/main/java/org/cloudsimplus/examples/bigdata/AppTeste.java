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

   //     Log.setLevel(Level.OFF);
        final int giga = 1024;

        AWSVM c4large = new AWSVM("c4large", 2, (int) (3.75 * giga));
        AWSVM c4xlarge = new AWSVM("c4xlarge", 4, (int) (7.75 * giga));
        AWSVM c42xlarge = new AWSVM("c42xlarge", 8, (int) (15 * giga));
        AWSVM c44xlarge = new AWSVM("c44xlarge", 16, (int) (30 * giga));
        AWSVM c48xlarge = new AWSVM("c48xlarge", 36, (int) (60 * giga));

   //    String[] args;
 //    args = new String[]{"2", "2", "sim", "um10"};
        if (args.length < 4) {
            System.err.println("Inserir 'vm1' 'vm2' 'nomeIndex' 'file1' 'file2' 'filen'");
            System.exit(-1);
        }

        int vm1 = Integer.parseInt(args[0]);
        int vm2 = Integer.parseInt(args[1]);
        String name = args[2];

        List<SimulacaoTeste> list = new ArrayList<>(2);
        int max = args.length;
        for (int i = 3; i < max; i++) {
            list.add(new SimulacaoTeste(c42xlarge, c42xlarge, vm1, vm2, "c42x-c42xc44x" + name, args[i]));
         //   list.add(new SimulacaoTeste(c4xlarge, c48xlarge, vm1, vm2, "c4xc48x" + name, args[i]));
          //  list.add(new SimulacaoTeste(c4xlarge, c42xlarge, vm1, vm2, "c4c42x" + name, args[i]));

        }

        list.parallelStream().forEach(SimulacaoTeste::run);

    }

}
