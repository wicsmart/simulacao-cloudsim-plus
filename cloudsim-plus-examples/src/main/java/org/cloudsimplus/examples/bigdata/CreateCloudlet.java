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
    
     
     public List<Cloudlet> geraCargaDinamica3(int[] cargas, int tempo){
        
        List<Cloudlet> lista = new ArrayList<>();
        double delay = 0.0;
        double tempoInicial = 0.0;
        int id = 0;
        Random r = new Random();
	double div;
        int max;
        int min;
        while(tempoInicial <= tempo){
            div = tempoInicial/120;
            if(div <= 1){
                min = 0; max = 1;
            }else if(div>1 && div<=2){
                min = 1; max = min +1;
            }else if(div>2 && div<=3){
                min = 2; max = min +1;
            }else if(div>3 && div<=4){
                min = 3; max = min +1;
            }else if(div>4 && div<=5){
                min = 4; max = min +1;
            }else {
                min = 5; max = min +1;
            }
            int carga = cargas[r.nextInt((max - min) + 1) + min];
            delay = (double) 1/carga;
            Cloudlet cl = cria(fileSize, id);
            cl.setSubmissionDelay(tempoInicial+delay);
            lista.add(cl);
            id++;
            tempoInicial += delay;
        }
        System.out.println("Quantidade de cloudlets: "+id);        
       return lista;
    }
   
}
