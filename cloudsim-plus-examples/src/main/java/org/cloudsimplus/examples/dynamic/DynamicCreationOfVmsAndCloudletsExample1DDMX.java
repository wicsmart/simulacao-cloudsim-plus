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
import org.cloudsimplus.util.Log;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.common.xcontent.XContentType;


/**
 * An example showing how to dynamically create VMs and Cloudlets during simulation
 * executing using the exclusive CloudSim Plus {@link DatacenterBroker} implementations
 * and Listener features. Using such features, <b>it is not required to create DatacenterBrokers in runtime
 * in order to allow dynamic submission of VMs and Cloudlets.</b>
 *
 * <p>This example uses CloudSim Plus Listener features to intercept when
 * the first Cloudlet finishes its execution to then request
 * the creation of new VMs and Cloudlets. It uses the Java 8 Lambda Functions features
 * to pass a listener to the mentioned Cloudlet, by means of the
 * {@link Cloudlet#addOnFinishListener(EventListener)} method.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 *
 * @see Cloudlet#addOnFinishListener(EventListener)
 * @see EventListener
 */
public class DynamicCreationOfVmsAndCloudletsExample1DDMX {
    private final CloudSim simulation;
    private final List<DatacenterBroker> brokers;
    private List<Cloudlet> cloudletList;
    private List<Vm> vmList, vmList2;
    private List<Integer> workload;
    private int numberOfCreatedCloudlets = 0;
    private int numberOfCreatedVms = 0;
    private final int VMS = 1;
    private final int VMS2 = 1;
    private final int CLS = 5;
    private final double DELAY = 0.01; // 100/s - MAX 
//    private final double DELAY = 0.028; // 35/s - MIN
    private final int SIGMA = 22;
    private final int MU = 52;
    private final int LENGTH1 = 100;
    private final int LENGTH2 = 4300;
  
    public static void main(String[] args) throws IOException {
        int cls = 5*100;//
        int vms = 2; // 3 coletores
        int vms2 = 2; // 2 coreback
        //String simId = "filesize1024"+String.valueOf(vms)+String.valueOf(vms2)+String.valueOf(cls);
        String simId = "Teste";
        int id = vms;
        String aux;
        
        new DynamicCreationOfVmsAndCloudletsExample1DDMX(cls, vms,vms2, simId);
//       
//        for(int i  = vms; i <= 10; i++){
//            for(int j = vms2; j<= 10; j++){
//                aux = simId+String.valueOf(id);
//                new DynamicCreationOfVmsAndCloudletsExample1DDMX(cls, i, j, aux);
//                id++;
//            }
//        }
    }
    /**
     * Default constructor that builds the simulation.
     */
    public DynamicCreationOfVmsAndCloudletsExample1DDMX(int cls, int vms, int vms2, String simId) throws IOException {

        Log.setLevel(ch.qos.logback.classic.Level.ERROR);

        System.out.println("Starting " + getClass().getSimpleName());
        this.simulation = new CloudSim();

        Datacenter datacenter0 = createDatacenter();
        brokers = createBrokers();
        workload = Gaussiana(CLS, MU, SIGMA);
      
        final int vmsToCreate = vms;
        final int cloudletsToCreate = cls;
        this.vmList = new ArrayList<>(vmsToCreate);
        this.vmList2 = new ArrayList<>(vmsToCreate);
        this.cloudletList = new ArrayList<>(cloudletsToCreate);
        createAndSubmitVmsAndCloudlets(vmsToCreate, cloudletsToCreate, vms2);

        for(int i=0;i<this.cloudletList.size(); i++){
            Cloudlet cloudlet0 = this.cloudletList.get(i);
            cloudlet0.addOnFinishListener(this::submitNewVmsAndCloudletsToBroker);
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
        /*    RestClient restClient = RestClient.builder(
        new HttpHost("localhost", 9200, "http")).build();
            List<? extends CloudletSimple> cloudlist1;     
             List<? extends CloudletSimple> cloudlist2;  
            cloudlist1 = brokers.get(0).getCloudletFinishedList();
            cloudlist2 = brokers.get(1).getCloudletFinishedList();
            FileWriter file;
          
            JsonObject cloud = new JsonObject();
            JsonArray array1 = new JsonArray();
            
            for(CloudletSimple cdl : cloudlist1){
                String timestamp = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date());
                
                JsonObject campo = new JsonObject();
                campo.addProperty("data", timestamp);
                campo.addProperty("cloudletId", cdl.getId());
                campo.addProperty("startTime1", cdl.getExecStartTime());
                campo.addProperty("finishTime1", cdl.getFinishTime());
                campo.addProperty("execTime1", cdl.getActualCpuTime());
                for(CloudletSimple cdl2 : cloudlist2){
                    if (cdl2.getId() == (cdl.getId())){
                        campo.addProperty("startTime2", cdl2.getExecStartTime());
                        campo.addProperty("finishTime2", cdl2.getFinishTime());
                        campo.addProperty("execTime2", cdl2.getActualCpuTime());
                    }
                }
                campo.addProperty("injestao", vms);
                campo.addProperty("processamento", vms2);
                campo.addProperty("cloudlets", cls);
                campo.addProperty("simId", simId);
             
                HttpEntity entity = new NStringEntity(campo.toString(), ContentType.APPLICATION_JSON);
                Response response;
                Map<String, String> params = Collections.emptyMap();
                response = restClient.performRequest("POST", "/testes/_doc", params, entity);
             
            }
             restClient.close();
  //          String brokerName =Integer.toString(vms)+"--"+Integer.toString(vms2);
//            cloud.add(brokerName, array1);
//            try {
//                file = new FileWriter("/home/wictor/resultados/"+brokerName+".json");
//                file.write(cloud.toString());
//                file.flush();
//                file.close();
//            } catch (IOException ex) {
//                Logger.getLogger(CloudletsTableBuilderJson.class.getName()).log(Level.SEVERE, null, ex);
//            }
            */
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
    private void createAndSubmitVmsAndCloudlets(int vmsToCreate, int cloudletsToCreate, int vms) {
        List<Vm> newVmList = new ArrayList<>(vmsToCreate);
        List<Vm> newVmList2 = new ArrayList<>(vmsToCreate);

        List<Cloudlet> newCloudletList = new ArrayList<>(cloudletsToCreate);
        for (int i = 0; i < vmsToCreate; i++) {
            Vm vm = createVm();
            newVmList.add(vm);
        }
        for (int i = 0; i < vms; i++) {
            Vm vm = createVm();
            newVmList2.add(vm);
         }
        for(int j = 0; j< cloudletsToCreate; j++){
            Cloudlet cloudlet = createCloudlet();
            cloudlet.setSubmissionDelay(0+j*DELAY);
            newCloudletList.add(cloudlet);
        }
        
        this.vmList2.addAll(newVmList2);
        brokers.get(1).submitVmList(newVmList2);
        
        this.vmList.addAll(newVmList);
        this.cloudletList.addAll(newCloudletList);
       
        brokers.get(0).submitVmList(newVmList);
        brokers.get(0).submitCloudletList(newCloudletList);
    }
    
  
    public static  List<Integer> Gaussiana(double x, double mu, double sigma) {
        Random randomno = new Random();
        List<Integer> gauss = new ArrayList<Integer>();
        for (int i = 0; i<x; i++){
            gauss.add((int) (randomno.nextGaussian()* sigma + mu ));
        }
        return gauss;
    }
    
    private void createAndSubmitVmsAndCloudlets2(int cloudletsToCreate, int id) {
        List<Cloudlet> newCloudletList = new ArrayList<>(1);
        for(int j = 0; j< cloudletsToCreate; j++){
            Cloudlet cloudlet = createCloudlet2();
            cloudlet.setId(id);
            newCloudletList.add(cloudlet);
        }
        this.cloudletList.addAll(newCloudletList);
        brokers.get(1).submitCloudletList(newCloudletList);
    }
    /**
     * Dynamically creates and submits a set of VMs to the broker when
     * the first cloudlet finishes.
     * @param eventInfo information about the fired event
     */
    private void submitNewVmsAndCloudletsToBroker(CloudletVmEventInfo eventInfo) {
    //    System.out.printf(
     //       "\n\t# Cloudlet %d finished. Submitting %d new VMs to the broker\n",
      //      eventInfo.getCloudlet().getId(), VMS);
        
        createAndSubmitVmsAndCloudlets2(1, eventInfo.getCloudlet().getId());
    }

     private List<DatacenterBroker> createBrokers() {
        final List<DatacenterBroker> list = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            list.add(new DatacenterBrokerSimple(simulation));
        }

        return list;
    }
     
    private DatacenterSimple createDatacenter() {
        final int numberOfHosts = 10;
        List<Host> hostList = new ArrayList<>(numberOfHosts);
        for (int i = 0; i < numberOfHosts; i++) {
            Host host = createHost();
            hostList.add(host);
        }

        return new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
    }

    private Host createHost() {
       long  mips = 10000; // capacity of each CPU core (in Million Instructions per Second)
       long  ram = 17000; // host memory (MEGABYTE) 17 Gb
       long storage = 40000; // host storage (MEGABYTE) 40 Gb
       long bw = 1000; //in Megabits/s


        final int numberOfPes = 8;
        List<Pe> peList = new ArrayList<>(numberOfPes); //List of CPU cores
        for (int i = 0; i < numberOfPes; i++) {
            peList.add(new PeSimple(mips, new PeProvisionerSimple()));
        }

        return new HostSimple(ram, bw, storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
                .setVmScheduler(new VmSchedulerTimeShared());
    }

    private Vm createVm() {
        long   mips = 10000;
        long   storage = 20000; // vm image size (MEGABYTE) 20 Gb
        int    ram = 16000; // vm memory (MEGABYTE)
        long   bw = 1000; // vm bandwidth (Megabits/s)
        int    pesNumber = 8; // number of CPU cores

        return new VmSimple(numberOfCreatedVms++, mips, pesNumber)
                .setRam(ram)
                .setBw(bw)
                .setSize(storage)
                .setCloudletScheduler(new CloudletSchedulerSpaceShared());
    }

    private Cloudlet createCloudlet() {
        long length = LENGTH1; //in Million Structions (MI)
        long fileSize = 1024; //Size (in bytes) before execution
        long outputSize = 1024; //Size (in bytes) after execution
        long numberOfCpuCores = 1;//vm.getNumberOfPes(); //cloudlet will use all the VM's CPU cores

        //Defines how CPU, RAM and Bandwidth resources are used
        //Sets the same utilization model for all these resources.
        UtilizationModel utilization = new UtilizationModelFull();

        return new CloudletSimple(
                numberOfCreatedCloudlets++, length, numberOfCpuCores)
                .setFileSize(fileSize)
                .setOutputSize(outputSize)
                .setUtilizationModel(utilization);
    }
    private Cloudlet createCloudlet2() {
        long length = LENGTH2; //in Million Structions (MI)
        long fileSize = 512; //Size (in bytes) before execution
        long outputSize = 512; //Size (in bytes) after execution
        long numberOfCpuCores = 1;//vm.getNumberOfPes(); //cloudlet will use all the VM's CPU cores

        //Defines how CPU, RAM and Bandwidth resources are used
        //Sets the same utilization model for all these resources.
        UtilizationModel utilization = new UtilizationModelFull();

        return new CloudletSimple(
                numberOfCreatedCloudlets++, length, numberOfCpuCores)
                .setFileSize(fileSize)
                .setOutputSize(outputSize)
                .setUtilizationModel(utilization);
    }
}
