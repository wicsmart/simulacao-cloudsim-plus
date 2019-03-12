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
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
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
public class SimulacaoLive implements Runnable {

    private static final String WORKLOAD_FORMAT = ".swf";
    private int maximumNumberOfCloudletsToCreateFromTheWorkloadFile = -1;

    private CloudSim simulation;
    private List<DatacenterBroker> brokers;
    private List<Cloudlet> cloudletList;
    protected List<Vm> vmColetores;
    protected List<Vm> vmCoreback;
    private String nome;
    private int countTime = 1;
    private int index = 0;

    private int LENGTH1 = 10;
    private int LENGTH2 = 70;
    private final int TOTAL = 330;
    private int tempo;
    private int coletores;
    private int coreback;
    private List<Map<Integer, Integer>> workloadList = new ArrayList<>();

    private Resultado resultado;

    public SimulacaoLive(int coletores, int coreback, String nome, double lenght1, double lenght2, int tempo) {
        this.coletores = coletores;
        this.coreback = coreback;
        this.nome = nome;
        this.tempo = tempo;
    }

    public void inicia() throws IOException {
      //    Log.setLevel(ch.qos.logback.classic.Level.INFO);
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //          Log.setLevel(Level.INFO);
        //      Log.setLevel(Level.WARN);

        final long start = System.currentTimeMillis();

        simulation = new CloudSim();
        simulation.terminateAt(tempo);
        resultado = new Resultado(coletores, coreback, nome, LENGTH1, LENGTH2);

        System.out.println("Starting " + getClass().getSimpleName() + nome);
        createDatacenter();
        brokers = createBrokers();

        vmColetores = new ArrayList<>(coletores);
        vmCoreback = new ArrayList<>(coreback);
        this.cloudletList = new ArrayList<>();

        createAndSubmitVmsAndCloudlets(coletores, coreback);
        System.out.printf("# Created %d Cloudlets for sim %s \n", this.cloudletList.size(), nome);
        addSegundaCarga();
        simulation.addOnClockTickListener(this::createDynamicCloudlet);
        simulation.addOnClockTickListener(this::onClockTickListener);
        simulation.start();
/*
        new CloudletsTableBuilder(brokers.get(0).getCloudletFinishedList())
                .setTitle(brokers.get(0).getName())
                .build();
        new CloudletsTableBuilder(brokers.get(1).getCloudletFinishedList())
                .setTitle(brokers.get(1).getName())
                .build();
        //      resultado.createFile(brokers);
*/
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

    private void geraCargaListadeCloudLets() throws IOException {

        final String fileName = "workload/swf/" + nome + WORKLOAD_FORMAT;
        WorkloadFileReader reader = WorkloadFileReader.getInstance(fileName, LENGTH1);
        reader.setMaxLinesToRead(maximumNumberOfCloudletsToCreateFromTheWorkloadFile);
        this.workloadList = reader.generateListWorkLoad();
        resultado.setTempoInicial(reader.getInicio());
    }

    private void createAndSubmitVmsAndCloudlets(int coletor, int coreback) throws IOException {
        List<Vm> newColetorVms = new ArrayList<>(coletor);
        List<Vm> newCorebackVms = new ArrayList<>(coreback);

        CreateVm col = new CreateVm(8);
        newColetorVms = col.listVm(coletor);

        CreateVm core = new CreateVm(8);
        newCorebackVms = core.listVm(coreback);

        this.vmCoreback.addAll(newCorebackVms);
        this.brokers.get(1).submitVmList(newCorebackVms);

        this.vmColetores.addAll(newColetorVms);

        geraCargaListadeCloudLets();

        this.cloudletList.addAll(cargaInicial(workloadList.get(index)));
        index += 1;

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

    private void createDynamicCloudlet(EventInfo evt) {
     
        if (mudouSegundo((int) evt.getTime())) {
            countTime = (int) evt.getTime();
            CreateCloudlet cl = new CreateCloudlet(300, 300);
            if (index < workloadList.size()) {
                int quantidade = workloadList.get(index).get(index);
                System.out.printf("\n# Dynamically creating %d Cloudlets at time %.2f \n", quantidade, evt.getTime());
                List<Cloudlet> cloudlets = cl.geraCargaLive(LENGTH1, quantidade);
                this.brokers.get(0).submitCloudletList(cloudlets);
                for (Cloudlet cloud : cloudlets) {
                    cloud.addOnFinishListener(this::submitNewVmsAndCloudletsToBroker);
                }
                index++;
            }
            System.out.println("time: "+evt.getTime());
        }
    }

    @Override
    public void run() {
        try {
            inicia();
        } catch (IOException ex) {
            System.out.println("error");
        }
    }

    private List<Cloudlet> cargaInicial(Map<Integer, Integer> get) {
        CreateCloudlet cloud = new CreateCloudlet(300, 300);
        List<Cloudlet> lista = cloud.geraCargaLive(LENGTH1, get.get(0));
        return lista;
    }

    private Boolean mudouSegundo(int eventoTime) {
        if(countTime == eventoTime)
            return false;
        else
            return true;
    }

    private boolean maior10(int eventoTime) {
        if ((eventoTime - countTime) < 10) {
            return false;
        } else {
            return true;
        }
    }

    private boolean maior1(int eventoTime) {
          if ((eventoTime - countTime) < 1) {
            return false;
        } else {
            return true;
        }
    }

}
