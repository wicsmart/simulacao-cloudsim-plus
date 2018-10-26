/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.examples.bigdata;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;

/**
 *
 * @author wictor
 */
public class CreateVm {

    private final long mips = 10000;
    private final long storage = 200000; // vm image size (MEGABYTE) 20 Gb
    private final int ram = 16000; // vm memory (MEGABYTE)
    private final long bw = 1000; // vm bandwidth (Megabits/s)
    private int pesNumber; // number of CPU cores

    public CreateVm(int pes) {
        this.pesNumber = pes;
    }

    public List<Vm> listVm(int quantidade) {
        List<Vm> lista = new ArrayList<>();
        for (int i = 0; i < quantidade; i++) {
            Vm vm = criaVm(i);
            vm.getUtilizationHistory().enable();
            lista.add(vm);
        }
        return lista;
    }

    public Vm criaVm(int id) {
        return new VmSimple(id, mips, pesNumber)
                .setRam(ram)
                .setBw(bw)
                .setSize(storage)
                .setCloudletScheduler(new CloudletSchedulerTimeShared());
    }
}
