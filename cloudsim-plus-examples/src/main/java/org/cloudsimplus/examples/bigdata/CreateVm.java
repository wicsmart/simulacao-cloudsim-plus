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

    private final long mips = 1000;
    private final long storage = 200000; // vm image size (MEGABYTE) 20 Gb
    private final long bw = 1000; // vm bandwidth (Megabits/s)
    private int ram; // vm memory (MEGABYTE)
    private int pesNumber; // number of CPU cores

    public CreateVm(AWSVM config) {
        this.pesNumber = config.getPes();
        this.ram = config.getMemoria();
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

    public List<Vm> listaMista(AWSVM v0, AWSVM v1) {
        List<Vm> lista = new ArrayList<>();

        this.pesNumber = v0.getPes();
        this.ram = v0.getMemoria();

        Vm vm0 = criaVm(1);
        vm0.getUtilizationHistory().enable();
        lista.add(vm0);

        this.pesNumber = v1.getPes();
        this.ram = v1.getMemoria();

        Vm vm1 = criaVm(1);
        vm1.getUtilizationHistory().enable();
        lista.add(vm1);
        return lista;
    }

}
