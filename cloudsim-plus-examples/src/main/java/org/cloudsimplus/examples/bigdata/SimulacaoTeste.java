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
import java.util.concurrent.TimeUnit;
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
public class SimulacaoTeste implements Runnable{
    private static final String WORKLOAD_FILENAME = "workload.swf";
    private int maximumNumberOfCloudletsToCreateFromTheWorkloadFile = -1;

    private CloudSim simulation;
    private List<DatacenterBroker> brokers;
    private List<Cloudlet> cloudletList;
    protected List<Vm> vmColetores;
    protected List<Vm> vmCoreback;
    private String nome;

    private int LENGTH1 = 100;
    private int LENGTH2 = 150;

    private int coletores;
    private int coreback;

    private int[] cargas;
    private int tempo;
    private Resultado resultado;

    public SimulacaoTeste(int coletores, int coreback, int tempo, int[] cargas, String nome, double lenght2) {
        this.coletores = coletores;
        this.coreback = coreback;
        this.tempo = tempo;
        this.cargas = cargas;
        this.nome = nome;
    }

    public void inicia() throws IOException {
//        Log.setLevel(ch.qos.logback.classic.Level.);
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
    //    Log.setLevel(ch.qos.logback.classic.Level.INFO);.
     //    Log.setLevel(Level.INFO);
        System.out.println("Simulação: "+nome);
        simulation = new CloudSim();
        resultado = new Resultado(coletores, coreback, tempo, nome, LENGTH1, LENGTH2);
        
        System.out.println("Starting " + getClass().getSimpleName());
        createDatacenter();
        brokers = createBrokers();
        
        vmColetores = new ArrayList<>(coletores);
        vmCoreback = new ArrayList<>(coreback);
        cloudletList = new ArrayList<>();
        cloudletList = geraCarga(cargas, tempo);
        
        System.out.printf("# Created %d Cloudlets \n", this.cloudletList.size());
        
        createAndSubmitVmsAndCloudlets(coletores, coreback, cloudletList);
        addSegundaCarga();
     
//        simulation.addOnClockTickListener(this::onClockTickListener);
//        final long startTimeMilliSec = System.currentTimeMillis();
        simulation.start();
//        final long finishTimeMilliSec = System.currentTimeMillis() - startTimeMilliSec;
//        System.out.println("Tempo de simulacao: "+miliTotime(finishTimeMilliSec));
        
        
//        new CloudletsTableBuilder(brokers.get(0).getCloudletFinishedList())
//                    .setTitle(brokers.get(0).getName())
//                    .build();
//        new CloudletsTableBuilder(brokers.get(1).getCloudletFinishedList())
//                    .setTitle(brokers.get(1).getName())
//                    .build();
         
        final long sartELK = System.currentTimeMillis();
//        resultado.createFile(brokers);
        resultado.saveElastic(brokers);
        final long finishELK = System.currentTimeMillis() - sartELK;
        System.out.println("Tempo de ELK: "+miliTotime(finishELK));

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

    private List<Cloudlet> geraCarga(int[] cargas, int tempo) throws IOException{
//       CreateCloudlet cloud = new CreateCloudlet(300, 300);
//       return  cloud.geraCargaDinamica4(cargas, tempo, LENGTH1);
        final String fileName = "workload/swf/"+WORKLOAD_FILENAME;
        WorkloadFileReader reader = WorkloadFileReader.getInstance(fileName, LENGTH1);
        reader.setMaxLinesToRead(maximumNumberOfCloudletsToCreateFromTheWorkloadFile);
        List<Cloudlet> cl = reader.generateWorkload();
        resultado.setTempoInicial(reader.getInicio());
        return reader.generateWorkload();
    }

    private void createAndSubmitVmsAndCloudlets(int coletor, int coreback, List<Cloudlet> cloudletList) {
        List<Vm> newColetorVms = new ArrayList<>(coletor);
        List<Vm> newCorebackVms = new ArrayList<>(coreback);
       
        CreateVm col = new CreateVm(8);
        newColetorVms = col.listVm(coletor);

        CreateVm core = new CreateVm(8);
        newCorebackVms = core.listVm(coreback);
       
        this.vmCoreback.addAll(newCorebackVms);
        this.brokers.get(1).submitVmList(newCorebackVms);

        this.vmColetores.addAll(newColetorVms);
        this.cloudletList.addAll(cloudletList);

        this.brokers.get(0).submitVmList(newColetorVms);
        this.brokers.get(0).submitCloudletList(cloudletList);
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
        
        Cloudlet cloudlet = cloud.cria(LENGTH2);
        this.cloudletList.add(cloudlet);
        this.brokers.get(1).submitCloudlet(cloudlet);
    }
    
     public static String miliTotime(long mili) {
        String tempo = String.format("%02d:%02d:%02d.%03d",
                TimeUnit.MILLISECONDS.toHours(mili),
                TimeUnit.MILLISECONDS.toMinutes(mili) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mili)),
                TimeUnit.MILLISECONDS.toSeconds(mili) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mili)),
                mili - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(mili)));
        return tempo;
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