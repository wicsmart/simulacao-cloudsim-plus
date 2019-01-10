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
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.listeners.CloudletVmEventInfo;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.util.Log;

/**
 *
 * @author wictor
 */
public class Simulacao implements Runnable{

    private CloudSim simulation;
    private List<DatacenterBroker> brokers;
    private List<Cloudlet> cloudletList;
    protected List<Vm> vmColetores;
    protected List<Vm> vmCoreback;
    private String nome;

    private final long LENGTH1 = 1000;
    private final long LENGTH2 = 1650;

    private int coletores;
    private int coreback;

    private int carga;
    private int tempo;
    private Resultado resultado;

    public Simulacao(int coletores, int coreback, int tempo, int carga, String nome) {
        this.coletores = coletores;
        this.coreback = coreback;
        this.tempo = tempo;
        this.carga = carga;
        this.nome = nome;
    }

    public void inicia() throws IOException {
    
        simulation = new CloudSim();
        resultado = new Resultado(coletores, coreback, tempo, nome, LENGTH1, LENGTH2);
        
        System.out.println("Starting " + getClass().getSimpleName());
        createDatacenter();
        brokers = createBrokers();

        int cloudlets = cloudletsToCreate();
        double delay = delay();
        
        vmColetores = new ArrayList<>(coletores);
        vmCoreback = new ArrayList<>(coreback);
        cloudletList = new ArrayList<>();

        createAndSubmitVmsAndCloudlets(coletores, coreback, cloudlets, delay);
        addSegundaCarga();
     
        simulation.addOnClockTickListener(this::onClockTickListener);
        simulation.start();
        resultado.estatiticas(vmColetores, vmCoreback, brokers);
        
//        new CloudletsTableBuilder(brokers.get(0).getCloudletFinishedList())
//                    .setTitle(brokers.get(0).getName())
//                    .build();
//        new CloudletsTableBuilder(brokers.get(1).getCloudletFinishedList())
//                    .setTitle(brokers.get(1).getName())
//                    .build();
//         
   //     resultado.saveElastic(brokers);
       
    }

    public void onClockTickListener(EventInfo event) {
        resultado.cpuRamSalva(this.vmColetores, this.vmCoreback, event);
    }

    private Datacenter createDatacenter() {
        CreateHost hosts = new CreateHost(10, 8);
        Datacenter dc0 = new DatacenterSimple(simulation, hosts.listHosts(), new VmAllocationPolicySimple());
        dc0.setSchedulingInterval(1);
        return dc0;
    }

    private List<DatacenterBroker> createBrokers() {
        final List<DatacenterBroker> list = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            list.add(new DatacenterBrokerSimple(simulation));
        }
        return list;
    }

    private int cloudletsToCreate() {
        return carga * tempo;
    }
  
    private double delay() {
       double delay = (double) 1 / carga;
       return delay;
    }
   
    private void createAndSubmitVmsAndCloudlets(int coletor, int coreback, int cloudlets, double delay) {
        List<Vm> newColetorVms = new ArrayList<>(coletor);
        List<Vm> newCorebackVms = new ArrayList<>(coreback);
        List<Cloudlet> newCloudletList = new ArrayList<>();

        CreateVm col = new CreateVm(8);
        newColetorVms = col.listVm(coletor);

        CreateVm core = new CreateVm(8);
        newCorebackVms = core.listVm(coreback);

        CreateCloudlet cloud = new CreateCloudlet(1536, 512);
        
        newCloudletList = (List<Cloudlet>) cloud.criaColetor(LENGTH1);

        this.vmCoreback.addAll(newCorebackVms);
        this.brokers.get(1).submitVmList(newCorebackVms);

        this.vmColetores.addAll(newColetorVms);
        this.cloudletList.addAll(newCloudletList);

        this.brokers.get(0).submitVmList(newColetorVms);
        this.brokers.get(0).submitCloudletList(newCloudletList);
    }

    public void addSegundaCarga() {
        for (Cloudlet cloud : this.cloudletList) {
            cloud.addOnFinishListener(this::submitNewVmsAndCloudletsToBroker);
        }
    }

    private void submitNewVmsAndCloudletsToBroker(CloudletVmEventInfo eventInfo) {
        criaSegundaCarga(eventInfo.getCloudlet().getId());
    }

    private void criaSegundaCarga(int id) {
        CreateCloudlet cloud = new CreateCloudlet(512, 512);
        Cloudlet cloudlet = cloud.criaCore(LENGTH2);
        this.cloudletList.add(cloudlet);
        this.brokers.get(1).submitCloudlet(cloudlet);
    }

    @Override
    public void run() {
        try {
            inicia();
        } catch (IOException ex) {
            System.out.println("error");
        }
    }
    
}
