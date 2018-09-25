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
package org.cloudsimplus.builders.tables;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.Identifiable;

import java.util.List;

import java.io.FileWriter;
import java.io.IOException;
 import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.reflect.TypeToken;

/**
 * Builds a table for printing simulation results from a list of Cloudlets.
 * It defines a set of default columns but new ones can be added
 * dynamically using the {@code addColumn()} methods.
 *
 * <p>The basic usage of the class is by calling its constructor,
 * giving a list of Cloudlets to be printed, and then
 * calling the {@link #build()} method.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class CloudletsTableBuilderJson extends TableBuilderAbstract<Cloudlet> {
    private static final String TIME_FORMAT = "%d";
    private static final String SECONDS = "Seconds";
    private static final String CPU_CORES = "CPU cores";

    /**
     * Instantiates a builder to print the list of Cloudlets using the a
     * default {@link TextTable}.
     * To use a different {@link Table}, check the alternative constructors.
     *
     * @param list the list of Cloudlets to print
     */
    public CloudletsTableBuilderJson(final List<? extends Cloudlet> list) {
        super(list);
    }

    /**
     * Instantiates a builder to print the list of Cloudlets using the a
     * given {@link Table}.
     *
     * @param list the list of Cloudlets to print
     * @param table the {@link Table} used to build the table with the Cloudlets data
     */
    public CloudletsTableBuilderJson(final List<? extends Cloudlet> list, final Table table) {
        super(list, table);
    }

    @Override
    protected void createTableColumns() {
        FileWriter file;
        double x = this.cloudlets.get(0).getFinishTime();
        JsonObject tempo = new JsonObject();
        JsonObject startTime = new JsonObject();
        JsonObject finishTime = new JsonObject();
        JsonObject execTime = new JsonObject();
        JsonObject broker = new JsonObject();
        
        JsonObject cloud = new JsonObject();
        JsonArray array = new JsonArray();
        
        for(Cloudlet cdl : this.cloudlets){
            broker.addProperty("broker", cdl.getBroker().getName());
            tempo.addProperty("id", cdl.getUid());
            tempo.addProperty("startTime", cdl.getExecStartTime());
            tempo.addProperty("finishTime", cdl.getFinishTime());
            tempo.addProperty("execTime", cdl.getActualCpuTime());
            array.add(tempo);
        }
        cloud.add("cloudlet", array);
        try {
            file = new FileWriter("/home/wictor/output.json");
            file.write(cloud.toString());
            file.flush();
            file.close();
        } catch (IOException ex) {
            Logger.getLogger(CloudletsTableBuilderJson.class.getName()).log(Level.SEVERE, null, ex);
     
    }
          final String ID = "ID";
        addColumnDataFunction(getTable().addColumn("Cloudlet", ID), Identifiable::getId);
        addColumnDataFunction(getTable().addColumn("Status "), c -> c.getStatus().name());
        addColumnDataFunction(getTable().addColumn("VM", ID), c -> c.getVm().getId());
        addColumnDataFunction(getTable().addColumn("CloudletLen", "MI"), Cloudlet::getLength);
        addColumnDataFunction(getTable().addColumn("CloudletPEs", CPU_CORES), Cloudlet::getNumberOfPes);
        addColumnDataFunction(getTable().addColumn("StartTime", "Seconds"), c -> String.format ("%.3f",c.getExecStartTime()));
        addColumnDataFunction(getTable().addColumn("FinishTime", "Seconds"), c -> String.format ("%.3f",c.getFinishTime()));
        addColumnDataFunction(getTable().addColumn("Exectime", "Seconds"), c -> String.format ("%.3f", c.getActualCpuTime()));
     
    }
}
