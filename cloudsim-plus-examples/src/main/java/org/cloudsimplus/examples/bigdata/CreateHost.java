/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.examples.bigdata;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;

/**
 *
 * @author wictor
 */
public class CreateHost {

    private long mips = 10000; // capacity of each CPU core (in Million Instructions per Second)
    private long ram = 17000; // host memory (MEGABYTE) 17 Gb
    private long storage = 400000; // host storage (MEGABYTE) 40 Gb
    private long bw = 1000; //in Megabits/s
    private int numberOfHosts;
    private int numberOfPes;

    public CreateHost(int numberOfHosts, int numberOfPes) {
        this.numberOfHosts = numberOfHosts;
        this.numberOfPes = numberOfPes;
    }

    public Host cria() {
        
        List<Pe> peList = new ArrayList<>(numberOfPes); //List of CPU cores
        for (int i = 0; i < numberOfPes; i++) {
            peList.add(new PeSimple(mips, new PeProvisionerSimple()));
        }

        return new HostSimple(ram, bw, storage, peList)
                .setRamProvisioner(new ResourceProvisionerSimple())
                .setBwProvisioner(new ResourceProvisionerSimple())
                .setVmScheduler(new VmSchedulerTimeShared());
    }
    
    public List<Host> listHosts(){
        List<Host> hostList = new ArrayList<>(numberOfHosts);
        for (int i = 0; i < numberOfHosts; i++) {
            Host host = cria();
            hostList.add(host);
        }
        return hostList;
    }
}
