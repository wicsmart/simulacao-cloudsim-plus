/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.examples.bigdata;

import com.google.gson.JsonObject;
import java.io.FileWriter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpHost;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.listeners.EventInfo;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author wictor
 */
public class Resultado {

    private int coletores;
    private int coreback;
    private String nome;
    private long length1;
    private long length2;
    private String bigdata = "bigdata";
    private String type = "_doc";
    private String source = "_source";
    private List<JsonObject> lista = new ArrayList<>();

    public long getTempoInicial() {
        return tempoInicial;
    }

    public void setTempoInicial(long tempoInicial) {
        this.tempoInicial = tempoInicial;
    }
    private long tempoInicial;

    public Resultado(int coletores, int coreback,
            String nome, long length1, long length2) {
        this.coletores = coletores;
        this.coreback = coreback;
        this.nome = nome;
        this.length1 = length1;
        this.length2 = length2;
    }

    public void createFile(List<DatacenterBroker> brokers) throws IOException {
        String timestamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());

        List<? extends CloudletSimple> cloudlist1;
        List<? extends CloudletSimple> cloudlist2;
        cloudlist1 = brokers.get(0).getCloudletFinishedList();
        cloudlist2 = brokers.get(1).getCloudletFinishedList();
        System.out.println("broker 1 : " + cloudlist1.size() + "  broker 2 : " + cloudlist2.size());
        JSONObject obj = new JSONObject();
        JSONArray docs = new JSONArray();

        for (CloudletSimple cdl : cloudlist1) {
            JsonObject doc = new JsonObject();
            JsonObject campo = new JsonObject();
            doc.addProperty("_index", "bigdata");
            doc.addProperty("_type", "_doc");
            campo.addProperty("created", timestamp);
            campo.addProperty("startTimeColetor", secondToDate((long) cdl.getExecStartTime()));
            campo.addProperty("execTimeColetor", cdl.getActualCpuTime());
            for (CloudletSimple cdl2 : cloudlist2) {
                if (cdl2.getId() == (cdl.getId())) {
                    campo.addProperty("startTimeCore", secondToDate((long) cdl2.getExecStartTime()));
                    campo.addProperty("execTimeCore", cdl2.getActualCpuTime());
                }
            }
            campo.addProperty("coletor", coletores);
            campo.addProperty("core", coreback);
            campo.addProperty("nome", nome);
            campo.addProperty("lengthColetor", length1);
            campo.addProperty("lengthCore", length2);
            doc.add("_source", campo);
            docs.add(doc);
        }
        for (JsonObject js : lista) {
            JsonObject doc = new JsonObject();
            doc.addProperty("_index", "bigdata");
            doc.addProperty("_type", "_doc");
            js.addProperty("created", timestamp);
            js.addProperty("nome", nome);
            doc.add("_source", js);
            docs.add(doc);
        }
        
        try (FileWriter file = new FileWriter("/home/wictor/resultado/" + nome + ".json")) {
            file.write(docs.toJSONString());
            System.out.println("Successfully Copied JSON Object to File...");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void saveElastic(List<DatacenterBroker> brokers) throws IOException {
        String timestamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
     
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));

        BulkProcessor.Listener listener = new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                int numberOfActions = request.numberOfActions();
//                System.out.println("Executing bulk " + executionId
//                        + " with " + numberOfActions + " requests");
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request,
                    BulkResponse response) {
                if (response.hasFailures()) {
                    System.out.println("Bulk " + executionId + " executed with failures");

                } else {
                    System.out.println("Bulk " + executionId + " completed in " + response.getTook().getMillis()
                            + " milliseconds");
                }

            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                System.out.println("Falhou " + failure);
            }
        };
        BiConsumer<BulkRequest, ActionListener<BulkResponse>> bulkConsumer
                = (request, bulkListener) -> client.bulkAsync(request, RequestOptions.DEFAULT, bulkListener);

        BulkProcessor bulkProcessor = BulkProcessor.builder(bulkConsumer, listener).build();
        BulkProcessor.Builder builder = BulkProcessor.builder(bulkConsumer, listener);
        builder.setBulkActions(500);
        builder.setBulkSize(new ByteSizeValue(1L, ByteSizeUnit.MB));
        builder.setConcurrentRequests(0);
        builder.setFlushInterval(TimeValue.timeValueSeconds(10L));
        builder.setBackoffPolicy(BackoffPolicy
                .constantBackoff(TimeValue.timeValueSeconds(1L), 3));

        List<? extends CloudletSimple> cloudlist1;
        List<? extends CloudletSimple> cloudlist2;
        cloudlist1 = brokers.get(0).getCloudletFinishedList();
        cloudlist2 = brokers.get(1).getCloudletFinishedList();
      
        for (CloudletSimple cdl : cloudlist1) {

            JsonObject campo = new JsonObject();
            campo.addProperty("created", timestamp);
            campo.addProperty("startTimeColetor", secondToDate((long) cdl.getExecStartTime()));
            campo.addProperty("execTimeColetor", cdl.getActualCpuTime());
            for (CloudletSimple cdl2 : cloudlist2) {
                if (cdl2.getId() == (cdl.getId())) {
                    campo.addProperty("startTimeCore", secondToDate((long) cdl2.getExecStartTime()));
                    campo.addProperty("execTimeCore", cdl2.getActualCpuTime());
                }
            }
            campo.addProperty("coletor", coletores);
            campo.addProperty("core", coreback);
            campo.addProperty("nome", nome);
            campo.addProperty("lengthColetor", length1);
            campo.addProperty("lengthCore", length2);

            bulkProcessor.add(new IndexRequest("bigdata", "_doc").source(campo.toString(), XContentType.JSON));

        }
        for (JsonObject js : this.lista) {
            js.addProperty("created", timestamp);
            bulkProcessor.add(new IndexRequest("bigdata", "_doc").source(js.toString(), XContentType.JSON));
        }

        try {
            boolean terminated = bulkProcessor.awaitClose(60L, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Logger.getLogger(Resultado.class.getName()).log(Level.SEVERE, null, ex);
        }
        client.close();
    }

    public String segundoTotime(double seg) {
        double milli = seg * 1000;
        int mili = (int) milli;
        String tempo = String.format("%02d:%02d:%02d.%03d",
                TimeUnit.MILLISECONDS.toHours(mili),
                TimeUnit.MILLISECONDS.toMinutes(mili) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mili)),
                TimeUnit.MILLISECONDS.toSeconds(mili) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mili)),
                mili - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(mili)));
        return tempo;
    }

    public String secondToDate(long seg) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        long mili = this.tempoInicial + (seg * 1000);
        Date data = new Date(mili + TimeUnit.HOURS.toMillis(2));
        return df.format(data);

    }

    public void estatiticas(List<Vm> coletor, List<Vm> coreback, List<DatacenterBroker> brokers) {

        List<? extends CloudletSimple> coletorFinished;
        List<? extends CloudletSimple> corebackFinished;

        double tempoColetor = 0.0;
        double tempoCore = 0.0;
        coletorFinished = brokers.get(0).getCloudletFinishedList();
        corebackFinished = brokers.get(1).getCloudletFinishedList();

        for (int i = 0; i < coletorFinished.size(); i++) {
            tempoColetor += coletorFinished.get(i).getActualCpuTime();
            tempoCore += corebackFinished.get(i).getActualCpuTime();
        }

        double mediaColetor = tempoColetor / coletorFinished.size();
        double mediaCore = tempoCore / corebackFinished.size();

        System.out.println();
        System.out.println("Coletor");
        for (Vm vm : coletor) {
            System.out.printf("%s -> Start Time: %.3f Stop Time: %.3f "
                    + "Total Execution Time: %.3f\n",
                    vm, vm.getStartTime(), vm.getStopTime(), vm.getTotalExecutionTime());
        }
        System.out.println("Coreback");
        for (Vm vm : coreback) {
            System.out.printf("%s -> Start Time: %.3f Stop Time: %.3f "
                    + "Total Execution Time: %.3f\n",
                    vm, vm.getStartTime(), vm.getStopTime(), vm.getTotalExecutionTime());
        }
        System.out.println("");
        System.out.println("Media Coletor = " + secondToDate((long) mediaColetor)
                + " seg. " + " Quant: " + coletorFinished.size());
        System.out.println("Media Core = " + secondToDate((long) mediaCore)
                + " seg. " + " Quant: " + corebackFinished.size());

    }

    public void cpuRamSalva(List<Vm> coletor, List<Vm> coreback, EventInfo event) {

        JsonObject campo = new JsonObject();
        campo.addProperty("timeUsage", secondToDate((long) event.getTime()));
        double cpuCol = 0;
        double ramCol = 0;
        double cpuCore = 0;
        double ramCore = 0;

        for (Vm vm : coletor) {
            cpuCol += vm.getCpuPercentUsage() * 100.0;
            ramCol += vm.getRam().getPercentUtilization() * 100;
            campo.addProperty("coletorCpuVM" + vm.getId(), vm.getCpuPercentUsage() * 100.0);
            campo.addProperty("coletorRamVM" + vm.getId(), vm.getRam().getPercentUtilization() * 100);
        }
        double mediaCpuColetor = cpuCol / coletor.size();
        double mediaRamColetor = ramCol / coletor.size();
        campo.addProperty("mediaCpuColetor", mediaCpuColetor);
        campo.addProperty("mediaRamColetor", mediaRamColetor);
        for (Vm vm : coreback) {
            cpuCore += vm.getCpuPercentUsage() * 100.0;
            ramCore += vm.getRam().getPercentUtilization() * 100;
            campo.addProperty("coreCpuVM" + vm.getId(), vm.getCpuPercentUsage() * 100.0);
            campo.addProperty("coreRamVM" + vm.getId(), vm.getRam().getPercentUtilization() * 100);
        }
        double mediaCpuCore = cpuCore / coreback.size();
        double mediaRamCore = ramCore / coreback.size();
        campo.addProperty("mediaCpuCore", mediaCpuCore);
        campo.addProperty("mediaRamCore", mediaRamCore);
        campo.addProperty("nome", this.nome);

        this.lista.add(campo);
    }

    public void cpuRamPrint(List<Vm> coletor, List<Vm> coreback, EventInfo event) {
        for (Vm vm : coletor) {
            System.out.printf(
                    "Time %6.1f: Vm %d CPU Usage: %6.2f%%. RAM usage: %.2f%% (%d MB)\n",
                    event.getTime(), vm.getId(), vm.getCpuPercentUsage() * 100.0,
                    vm.getRam().getPercentUtilization() * 100, vm.getRam().getAllocatedResource());
        }
        for (Vm vm : coreback) {
            System.out.printf(
                    "Time %6.1f: Vm %d CPU Usage: %6.2f%%. RAM usage: %.2f%% (%d MB)\n",
                    event.getTime(), vm.getId(), vm.getCpuPercentUsage() * 100.0,
                    vm.getRam().getPercentUtilization() * 100, vm.getRam().getAllocatedResource());
        }
    }

}
