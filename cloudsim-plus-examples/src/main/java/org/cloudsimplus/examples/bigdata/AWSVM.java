/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.examples.bigdata;

/**
 *
 * @author wictor
 */
public class AWSVM {
    
    private String nome;
    private int pes;
    private int memoria;

    public AWSVM(String nome, int pes, int memoria) {
        this.nome = nome;
        this.pes = pes;
        this.memoria = memoria;
    }
     
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getPes() {
        return pes;
    }

    public void setPes(int pes) {
        this.pes = pes;
    }

    public int getMemoria() {
        return memoria;
    }

    public void setMemoria(int memoria) {
        this.memoria = memoria;
    }
    
    
}
