/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.examples.bigdata;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;
import static org.cloudbus.cloudsim.cloudlets.Cloudlet.NULL;
import org.cloudbus.cloudsim.util.*;

public class WorkloadFileReader implements WorkloadReader {
    private final String filePath;
    private final InputStream reader;

    private int mips;
    private int id;
    private long delay;
    private int quantidade;
    private long inicio;

    private final List<Cloudlet> cloudlets;
    private int jobNum = 0;

    private int submitTime = 1;

    private int amount = 2;

    private int maxField = 3;

    private String comment = ";";

    private final int IRRELEVANT = -1;

    private String[] fieldArray;

    private int maxLinesToRead;

    private Predicate<Cloudlet> predicate;

    public WorkloadFileReader(final String filePath, final int mips) throws FileNotFoundException {
        this(filePath, new FileInputStream(filePath), mips);
    }

    private WorkloadFileReader(final String filePath, final InputStream reader, final int mips) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("Invalid trace reader name.");
        }

        this.setMips(mips);
        this.reader = reader;
        this.filePath = filePath;

        /* A default predicate which indicates that a Cloudlet will be
           created for any job read from the workload reader.
           That is, there isn't an actual condition to create a Cloudlet. */
        this.predicate = c -> true;

        this.cloudlets = new ArrayList<>();
        this.maxLinesToRead = -1;
    }
    
    public static WorkloadFileReader getInstance(final String fileName, final int mips) {
        final InputStream reader = ResourceLoader.getInputStream(WorkloadFileReader.class, fileName);
        return new WorkloadFileReader(fileName, reader, mips);
    }

    @Override
    public List<Cloudlet> generateWorkload() throws IOException {
        if (cloudlets.isEmpty()) {
            // create a temp array
            fieldArray = new String[maxField];
            readTextFile(reader);
        }
        return cloudlets;
    }
    
    @Override
    public WorkloadReader setPredicate(Predicate<Cloudlet> predicate) {
        this.predicate = predicate;
        return this;
    }

    public boolean setComment(final String comment) {
        if (comment != null && !comment.trim().isEmpty()) {
            this.comment = comment;
            return true;
        }

        return false;
    }

    private List<Cloudlet> createCloudletFromTraceLine(final String[] array) {
       
        id = Integer.valueOf(array[jobNum].trim());
        
        if(id == IRRELEVANT){
            id = cloudlets.size() + 1;
        }
        long milisec = Long.valueOf(array[submitTime].trim());
        delay = convertMiliToSecond(milisec);
        
        quantidade = (int) Double.parseDouble(array[amount].trim());

        CreateCloudlet cloud = new CreateCloudlet(300, 300);
        return cloud.geraCargaReal(mips, delay, quantidade);
    }
    
     public long convertMiliToSecond(long atual){
        if (id == 0){
            this.inicio = atual;
            return 0; 
        }
        
        return (atual-inicio)/1000;
    }
     
    public String secondToDate(long seg) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        long milli = inicio + (60 *1000);
        Date data = new Date(milli);
        return df.format(data);
        
    }
   
    private List<Cloudlet> parseTraceLineAndCreateCloudlet(final String line) {
        // skip a comment line
        if (line.startsWith(comment)) {
            return (List<Cloudlet>) NULL;
        }

        final String[] sp = line.split("\\s+"); // split the fields based on a space
        int index = 0; // the index of an array

        // check for each field in the array
        for (final String elem : sp) {
            if (!elem.trim().isEmpty()) {
                fieldArray[index] = elem;
                index++;
            }
        }

        //If all the fields could not be read, don't create the Cloudlet.
        if (index < maxField) {
            return (List<Cloudlet>) Cloudlet.NULL;
        }

        return createCloudletFromTraceLine(fieldArray);
        
    }
    
      
    private void readFile(final InputStream inputStream) throws IOException {
        //the reader is safely closed by the caller
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        int line = 1;
        String readLine;
        while ((readLine = readNextLine(reader, line)) != null) {
       final List<Cloudlet> c = parseTraceLineAndCreateCloudlet(readLine);
            if (c != Cloudlet.NULL) {
                cloudlets.addAll(c);
                line++;
            }
        }
    }
       
    public long getInicio() {
        return inicio;
    }
  
    protected void readTextFile(final InputStream inputStream) throws IOException {
        readFile(inputStream);
    }
  
    private String readNextLine(BufferedReader reader, int lineNumber) throws IOException {
        if (reader.ready() && (maxLinesToRead == -1 || lineNumber <= maxLinesToRead)) {
            return reader.readLine();
        }

        return null;
    }

    public int getMaxLinesToRead() {
        return maxLinesToRead;
    }

    
    public void setMaxLinesToRead(int maxLinesToRead) {
        this.maxLinesToRead = maxLinesToRead;
    }

    public int getMips() {
        return mips;
    }

    public final WorkloadFileReader setMips(final int mips) {
        if (mips <= 0) {
            throw new IllegalArgumentException("MIPS must be greater than 0.");
        }
        this.mips = mips;
        return this;
    }

}
