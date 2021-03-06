/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.examples.bigdata;

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
import org.cloudsimplus.listeners.CloudletVmEventInfo;
import org.cloudsimplus.listeners.EventInfo;

/**
 *
 * @author wictor
 */
public class SimulacaoTeste implements Runnable {

    private static final String WORKLOAD_FORMAT = ".swf";
    private int maximumNumberOfCloudletsToCreateFromTheWorkloadFile = -1;

    private CloudSim simulation;
    private List<DatacenterBroker> brokers;
    private List<Cloudlet> cloudletList;
    protected List<Vm> vmColetores;
    protected List<Vm> vmCoreback;
    private String nome;
    private String file;
    
 //   for 0.1 para cada segundo
    private int LENGTH1 = 250;
    private int LENGTH2 = 700;

    private int coletores;
    private int coreback;
    private AWSVM configColetor;
    private AWSVM configCore;


    private Resultado resultado;

    public SimulacaoTeste(AWSVM configColertor, AWSVM configCore, int coletores, int coreback,
            String nome, String file) {
        this.coletores = coletores;
        this.coreback = coreback;
        this.nome = nome;
        this.file = file;
        this.configColetor = configColertor;
        this.configCore = configCore;
    }

    public void inicia() throws IOException {
//        Log.setLevel(ch.qos.logback.classic.Level.);
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //    Log.setLevel(ch.qos.logback.classic.Level.INFO);.
        //    Log.setLevel(Level.INFO);

        final long start = System.currentTimeMillis();

        simulation = new CloudSim();
        resultado = new Resultado(coletores, coreback, nome);

        System.out.println("Starting " + getClass().getSimpleName() + nome);
        createDatacenter();
        brokers = createBrokers();

        vmColetores = new ArrayList<>(coletores);
        vmCoreback = new ArrayList<>(coreback);
        this.cloudletList = new ArrayList<>();

        createAndSubmitVmsAndCloudlets(coletores, coreback);
        System.out.printf("# Created %d Cloudlets for sim %s \n", this.cloudletList.size(), nome);
        addSegundaCarga();

        simulation.addOnClockTickListener(this::onClockTickListener);
        simulation.start();

//        new CloudletsTableBuilder(brokers.get(0).getCloudletFinishedList())
//                    .setTitle(brokers.get(0).getName())
//                    .build();
//        new CloudletsTableBuilder(brokers.get(1).getCloudletFinishedList())
//                    .setTitle(brokers.get(1).getName())
//                    .build();        
        //        resultado.createFile(brokers);
        resultado.saveElastic(brokers);

        final long finish = System.currentTimeMillis() - start;

        System.out.println(
                ConsoleColors.GREEN
                + "Simulacao " + nome + " done in " + miliTotime(finish)
                + ConsoleColors.RESET);

    }

    public void onClockTickListener(EventInfo event) {
        resultado.cpuRamSalva(this.vmColetores, this.vmCoreback, event);
    }

    private Datacenter createDatacenter() {
        CreateHost hosts = new CreateHost(10, 36);
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

    private List<Cloudlet> geraCarga() throws IOException {

        final String fileName = "workload/swf/" + file + WORKLOAD_FORMAT;
        WorkloadFileReader reader = WorkloadFileReader.getInstance(fileName, LENGTH1);
        reader.setMaxLinesToRead(maximumNumberOfCloudletsToCreateFromTheWorkloadFile);
        List<Cloudlet> cl = reader.generateWorkload();
        resultado.setTempoInicial(reader.getInicio());
        return reader.generateWorkload();
    }

    private void createAndSubmitVmsAndCloudlets(int coletor, int coreback) throws IOException {
        List<Vm> newColetorVms = new ArrayList<>(coletor);
        List<Vm> newCorebackVms = new ArrayList<>(coreback);

        CreateVm col = new CreateVm(configColetor);
        newColetorVms = col.listVm(coletor);
 
 //************ codigo para adicionar vms diferentes no coletor***************
/*
        AWSVM c42xlarge = new AWSVM("c42xlarge", 8, (int) (15 * 1024));
        AWSVM c4xlarge = new AWSVM("c4xlarge", 4, (int) (7.75 * 1024));
        
        newColetorVms = col.listaMista(c4xlarge, c4xlarge, coletor);
*/
//******************************************************************************

        CreateVm core = new CreateVm(configCore);
        newCorebackVms = core.listVm(coreback);
 
//*************** codigo para adicionar vms diferentes no core***************
/*
        AWSVM c44xlarge = new AWSVM("c44xlarge", 16, (int) (30 * 1024));
    
        newCorebackVms = core.listaMista(c42xlarge, c44xlarge, coreback);       
//******************************************************************************
*/

        this.vmCoreback.addAll(newCorebackVms);
        this.brokers.get(1).submitVmList(newCorebackVms);
        
        this.vmColetores.addAll(newColetorVms);

        this.cloudletList.addAll(geraCarga());

        this.brokers.get(0).submitVmList(newColetorVms);
        this.brokers.get(0).submitCloudletList(this.cloudletList);

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
        //  this.cloudletList.add(cloudlet);
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
