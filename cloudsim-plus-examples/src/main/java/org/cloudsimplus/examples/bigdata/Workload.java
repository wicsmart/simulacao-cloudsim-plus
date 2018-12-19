/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.examples.bigdata;

/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.io.IOException;
import java.util.*;

/**
 * An example showing how to dynamically create cloudlets from a workload trace
 * file in the Standard Workload Format (.swf file) defined by the
 * <a href="http://www.cs.huji.ac.il/labs/parallel/workload/">Hebrew University
 * of Jerusalem</a>. This example uses the workload file
 * "<i>NASA-iPSC-1993-3.1-cln.swf.gz</i>", which was downloaded from the given
 * web page and is located at the resources folder of this project.
 * The workload file has 18239 jobs that will be created as Cloudlets.
 *
 * <p>Considering the large number of cloudlets that can have a workload file,
 * that can cause the simulation to consume a lot of resources
 * at the developer machine and can spend a long time to finish,
 * the example allow to limit the maximum number of cloudlets to be submitted
 * to the DatacenterBroker.
 * See the {@link #maximumNumberOfCloudletsToCreateFromTheWorkloadFile} attribute for more details.
 * </p>
 *
 * <p>
 * The workload file is compressed in <i>gz</i> format and the
 * <i>swf</i> file inside it is just a text file that can be opened in any text
 * editor. For more information about the workload format, check
 * <a href="http://www.cs.huji.ac.il/labs/parallel/workload/swf.html">this
 * page</a>.
 * </p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0.0
 */
public class Workload {
    /**
     * The workload file to be read.
     */
    private static final String WORKLOAD_FILENAME = "workload.swf";
    
    private int maximumNumberOfCloudletsToCreateFromTheWorkloadFile = -1;

    private static final int CLOUDLETS_MIPS = 1700;

    private List<Cloudlet> cloudletList;
    private List<Vm> vmlist;
    private Datacenter datacenter0;
    private DatacenterBroker broker;
    private CloudSim simulation;
    /**
     * Creates main() to run this example.
     *
     * @param args the args
     */
    public static void main(String[] args) {
        new Workload();
    }

    private Workload() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        System.out.println("Starting " + getClass().getSimpleName());

        this.simulation = new CloudSim();
        vmlist = new ArrayList<>(2);
        
        
        try {
            datacenter0 = createDatacenter();
            broker = createBrokers();
            createCloudletsFromWorkloadFile();
            createVmsandSubmitToBroker();

            broker.submitVmList(vmlist);
            broker.submitCloudletList(cloudletList);

            simulation.start();

            List<Cloudlet> newList = broker.getCloudletFinishedList();
            new CloudletsTableBuilder(newList).build();

            System.out.println(getClass().getSimpleName() + " finished!");
        } catch (IOException e) {
            System.out.printf("Erro during simulation execution: %s\n", e.getMessage());
        }
    }

    private void createVmsandSubmitToBroker() {
        CreateVm col = new CreateVm(8);
        this.vmlist.addAll(col.listVm(2));
        System.out.printf("# Created %d VMs\n", vmlist.size());
    }

    private void createCloudletsFromWorkloadFile() throws IOException {
        final String fileName = "workload/swf/"+WORKLOAD_FILENAME;
        WorkloadFileReader reader = WorkloadFileReader.getInstance(fileName, CLOUDLETS_MIPS);
        reader.setMaxLinesToRead(maximumNumberOfCloudletsToCreateFromTheWorkloadFile);
        this.cloudletList = reader.generateWorkload();
        System.out.printf("# Created %d Cloudlets for %s\n", this.cloudletList.size(), broker);
  
    }

    private Datacenter createDatacenter() {
        CreateHost hosts = new CreateHost(10, 8);
        Datacenter dc0 = new DatacenterSimple(simulation, hosts.listHosts(), new VmAllocationPolicySimple());
        dc0.setSchedulingInterval(1);
        return dc0;
    }
      private DatacenterBroker createBrokers() {
         return new DatacenterBrokerSimple(simulation);
    }
    
}
