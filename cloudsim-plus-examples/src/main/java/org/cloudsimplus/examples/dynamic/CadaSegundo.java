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
package org.cloudsimplus.examples.dynamic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.listeners.CloudletVmEventInfo;
import org.cloudsimplus.listeners.EventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudsimplus.builders.tables.CloudletsTableBuilderJson;
import org.cloudsimplus.examples.bigdata.CreateCloudlet;
import org.cloudsimplus.examples.bigdata.CreateHost;
import org.cloudsimplus.examples.bigdata.CreateVm;
import org.cloudsimplus.util.Log;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.common.xcontent.XContentType;

public class CadaSegundo {
    private final CloudSim simulation;
    private final List<DatacenterBroker> brokers;
    private List<Cloudlet> cloudletList;
    private List<Vm> vmList, vmList2;
    private List<Integer> workload;
    private int numberOfCreatedCloudlets = 0;
    private int numberOfCreatedVms = 0;
    
    private final int LENGTH1 = 1000;
    private final int LENGTH2 = 1650;

    private final int vms = 3; // 3 coletores
    private final int vms2 = 2; // 2 coreback
    
    private final int CARGA = 100;
    private final double DELAY = 0.01;
    private final int cls = CARGA*30;
    
    public static void main(String[] args) throws IOException {
       
        //String simId = "filesize1024"+String.valueOf(vms)+String.valueOf(vms2)+String.valueOf(cls);
        String simId = "Teste";
        
        String aux;
        
        new CadaSegundo(simId);
    }
    /**
     * Default constructor that builds the simulation.
     */
    public CadaSegundo(String simId) throws IOException {

        Log.setLevel(ch.qos.logback.classic.Level.ERROR);

        System.out.println("Starting " + getClass().getSimpleName());
        this.simulation = new CloudSim();

        createDatacenter();
        brokers = createBrokers();
     
        final int vmsToCreate = vms;
        final int vmsToCreate2 = vms2;
        final int cloudletsToCreate = cls;
        this.vmList = new ArrayList<>(vmsToCreate);
        this.vmList2 = new ArrayList<>(vmsToCreate2);
        this.cloudletList = new ArrayList<>(cloudletsToCreate);
        createAndSubmitVmsAndCloudlets(vmsToCreate, cloudletsToCreate, vmsToCreate2);
        
        for(Cloudlet cloud : this.cloudletList){
            cloud.addOnFinishListener(this::submitNewVmsAndCloudletsToBroker);
        }
       
        simulation.start();
        printResults(cls, vms, vms2, simId);
    }
    
    private void printResults( int cls, int vms, int vms2, String simId) throws IOException {
        List<? extends CloudletSimple> cloudlist1;     
        List<? extends CloudletSimple> cloudlist2;
        double tempoColetor = 0.0;
        double tempoCore = 0.0;
        cloudlist1 = brokers.get(0).getCloudletFinishedList();
        cloudlist2 = brokers.get(1).getCloudletFinishedList();
        
        for(int i=0; i < cloudlist1.size(); i++){
            tempoColetor += cloudlist1.get(i).getActualCpuTime();
            tempoCore += cloudlist2.get(i).getActualCpuTime();
        }
        double mediaColetor = tempoColetor/cloudlist1.size();
        double mediaCore = tempoCore/cloudlist2.size();
        System.out.println("Media Coletor = "+ mediaColetor+ "seg" +" Quant " + cloudlist1.size());
        System.out.println("Media Core = "+ mediaCore+ "seg" +" Quant " + cloudlist2.size());
       
            new CloudletsTableBuilder(brokers.get(0).getCloudletFinishedList())
                    .setTitle(brokers.get(0).getName())
                    .build();
            new CloudletsTableBuilder(brokers.get(1).getCloudletFinishedList())
                    .setTitle(brokers.get(1).getName())
                    .build();
         

    System.out.println();
        for (Vm vm : vmList2) {
            System.out.printf("%s -> Start Time: %.3f Stop Time: %.3f Total Execution Time: %.3f\n",
                    vm, vm.getStartTime(), vm.getStopTime(), vm.getTotalExecutionTime());
        }
        System.out.println();
       
         System.out.println("Media Coletor = "+ mediaColetor+ "seg" +" Quant " + cloudlist1.size());
        System.out.println("Media Core = "+ mediaCore+ "seg" +" Quant " + cloudlist2.size());
    }
    private void createAndSubmitVmsAndCloudlets(int vmsToCreate, int cloudletsToCreate, int vmsToCreate2) {
        List<Vm> newVmList = new ArrayList<>(vmsToCreate);
        List<Vm> newVmList2 = new ArrayList<>(vmsToCreate2);

        List<Cloudlet> newCloudletList = new ArrayList<>(cloudletsToCreate);
        
        CreateVm  vm = new CreateVm(8);
        newVmList = vm.listVm(vmsToCreate);
        
        CreateVm  vm2 = new CreateVm(8);
        newVmList2 = vm.listVm(vmsToCreate2);
        
        CreateCloudlet cloudlets = new CreateCloudlet(1536, 512);
        newCloudletList = cloudlets.criaLista(LENGTH1, cloudletsToCreate, DELAY);
        
        this.vmList2.addAll(newVmList2);
        brokers.get(1).submitVmList(newVmList2);
        
        this.vmList.addAll(vm.listVm(vmsToCreate));
        this.cloudletList.addAll(newCloudletList);
       
        brokers.get(0).submitVmList(newVmList);
        brokers.get(0).submitCloudletList(newCloudletList);
    }
    
    private void createAndSubmitVmsAndCloudlets2(int id) {
        CreateCloudlet cloud = new CreateCloudlet(512,512);
        Cloudlet cloudlet = cloud.cria(LENGTH2, id);
        this.cloudletList.add(cloudlet);
        brokers.get(1).submitCloudlet(cloudlet);
    }
    /**
     * Dynamically creates and submits a set of VMs to the broker when
     * the first cloudlet finishes.
     * @param eventInfo information about the fired event
     */
    private void submitNewVmsAndCloudletsToBroker(CloudletVmEventInfo eventInfo) {
        
        createAndSubmitVmsAndCloudlets2(eventInfo.getCloudlet().getId());
    }

     private List<DatacenterBroker> createBrokers() {
        final List<DatacenterBroker> list = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            list.add(new DatacenterBrokerSimple(simulation));
        }
        return list;
    }
     
    private DatacenterSimple createDatacenter() {
        CreateHost hosts = new CreateHost(10, 8);
        return new DatacenterSimple(simulation, hosts.listHosts(), new VmAllocationPolicySimple());
    }

}
