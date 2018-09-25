/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.examples.bigdata;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;

/**
 *
 * @author wictor
 */
public class CreateCloudlet {
    private long fileSize; //Size (in bytes) before execution
    private long outputSize; //Size (in bytes) after execution
    private final int numberOfCpuCores = 1;

    public CreateCloudlet(long fileSize, long outputSize) {
        this.fileSize = fileSize;
        this.outputSize = outputSize;
    }
    
    public Cloudlet cria(long length, int id) {
        UtilizationModel UtilizationModelFull = new UtilizationModelFull();
        UtilizationModel utilizationModelDynamic = new UtilizationModelDynamic(1.0/20);
        UtilizationModelDynamic ramModel = new UtilizationModelDynamic(UtilizationModel.Unit.ABSOLUTE, 10);

        return new CloudletSimple(
                id, length, numberOfCpuCores)
                .setFileSize(fileSize)
                .setOutputSize(outputSize)
                .setUtilizationModelBw(UtilizationModelFull)
                .setUtilizationModelRam(utilizationModelDynamic)
                .setUtilizationModelCpu(UtilizationModelFull);
    }
    
    public List<Cloudlet> criaLista(long length, int quantidade, double delay){
        List<Cloudlet> lista = new ArrayList<>();
        for(int j = 0; j< quantidade; j++){
            Cloudlet cloudlet = cria(length, j);
            cloudlet.setSubmissionDelay(0+j*delay);
            lista.add(cloudlet);
        }
        return lista;
    }
    
    
    public List<Cloudlet> geraCargaDinamica(int[] cargas, int tempo){
        List<Cloudlet> lista = new ArrayList<>();
        double delay = 0.0;
        double tempoInicial = 0.0;
        int quant = 0;
        int id = 0;
        for( int carga: cargas){
            delay = (double) 1/carga;
            quant = tempo*carga;
            for(int i = 0; i<quant; i++){
                Cloudlet cl = cria(fileSize, id);
                cl.setSubmissionDelay(tempoInicial+i*delay);
                lista.add(cl);
                id++;
            }
            tempoInicial += tempo;
        }
       return lista;
    }
   
}
