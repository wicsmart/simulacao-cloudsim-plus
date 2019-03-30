/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.examples.bigdata;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

    public Cloudlet criaColetor(long length) {
        UtilizationModel UtilizationModelFull = new UtilizationModelFull();
        UtilizationModel utilizationModelDynamic = new UtilizationModelDynamic(1.0 / 60);
        UtilizationModelDynamic ramModel = new UtilizationModelDynamic(UtilizationModel.Unit.ABSOLUTE, 250);
              
        return new CloudletSimple(
                 length, numberOfCpuCores)
                .setFileSize(fileSize)
                .setOutputSize(outputSize)
                .setUtilizationModelBw(UtilizationModelFull)
                .setUtilizationModelRam(ramModel)
                .setUtilizationModelCpu(UtilizationModelFull);
    }
    
     public Cloudlet criaCore(long length) {
        UtilizationModel UtilizationModelFull = new UtilizationModelFull();
        UtilizationModel utilizationModelDynamic = new UtilizationModelDynamic(1.0 / 60);
        UtilizationModelDynamic ramModel = new UtilizationModelDynamic(UtilizationModel.Unit.ABSOLUTE, 300);
        
        return new CloudletSimple(
                 length, numberOfCpuCores)
                .setFileSize(fileSize)
                .setOutputSize(outputSize)
                .setUtilizationModelBw(UtilizationModelFull)
                .setUtilizationModelRam(ramModel)
                .setUtilizationModelCpu(UtilizationModelFull);
    }
    
     public List<Cloudlet> geraCargaReal(int length, double submitTime, int quantidade) {
        List<Cloudlet> lista = new ArrayList<>();
        for(int i = 0; i< quantidade; i++){
            Cloudlet cl = criaColetor(length);
            cl.setSubmissionDelay(submitTime);
            lista.add(cl);
        }
        return lista;
    }
     
      public List<Cloudlet> geraCargaLive(int length, int quantidade) {
        List<Cloudlet> lista = new ArrayList<>();
        for(int i = 0; i< quantidade; i++){
            Cloudlet cl = criaColetor(length);
            lista.add(cl);
        }
        return lista;
    }

}
