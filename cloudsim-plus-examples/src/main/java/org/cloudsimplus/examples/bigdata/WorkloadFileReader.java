/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.examples.bigdata;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

            if (filePath.endsWith(".gz")) {
                readGZIPFile(reader);
            } else if (filePath.endsWith(".zip")) {
                readZipFile(reader);
            } else {
                readTextFile(reader);
            }
        }

        return cloudlets;
    }

    @Override
    public WorkloadReader setPredicate(Predicate<Cloudlet> predicate) {
        this.predicate = predicate;
        return this;
    }

    /**
     * Sets the string that identifies the start of a comment line.
     *
     * @param comment a character that denotes the start of a comment, e.g. ";" or "#"
     * @return <code>true</code> if it is successful, <code>false</code> otherwise
     */
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

    /**
     * Reads traces from a InputStream to a workload reader
     * in any supported format.
     *
     * @param inputStream the stream that is able to read data from a workload reader
     * @return <code>true</code> if successful, <code>false</code> otherwise.
     * @throws IOException if the there was any error reading the reader
     */
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

    /**
     * Reads traces from a text reader, usually with the swf extension, one line at a time.
     *
     * @param inputStream a reader name
     * @return <code>true</code> if successful, <code>false</code> otherwise.
     * @throws IOException if the there was any error reading the reader
     */
    protected void readTextFile(final InputStream inputStream) throws IOException {
        readFile(inputStream);
    }

    /**
     * Reads traces from a gzip reader, one line at a time.
     *
     * @param inputStream a {@link InputStream} to read the file
     * @return <code>true</code> if successful; <code>false</code> otherwise.
     * @throws IOException if the there was any error reading the reader
     */
    protected void readGZIPFile(final InputStream inputStream) throws IOException {
        readFile(new GZIPInputStream(inputStream));
    }

    /**
     * Reads a set of trace files inside a Zip reader.
     *
     * @param inputStream a {@link InputStream} to read the file
     * @return <code>true</code> if reading a reader is successful;
     * <code>false</code> otherwise.
     * @throws IOException if the there was any error reading the reader
     */
    protected boolean readZipFile(final InputStream inputStream) throws IOException {
        try (ZipInputStream zipFile = new ZipInputStream(inputStream)) {
            while (zipFile.getNextEntry() != null) {
                readFile(zipFile);
            }
            return true;
        }
    }

    /**
     * Reads the next line of the workload reader.
     *
     * @param reader     the object that is reading the workload reader
     * @param lineNumber the number of the line that that will be read from the workload reader
     * @return the line read; or null if there isn't any more lines to read or if
     * the number of lines read reached the {@link #getMaxLinesToRead()}
     */
    private String readNextLine(BufferedReader reader, int lineNumber) throws IOException {
        if (reader.ready() && (maxLinesToRead == -1 || lineNumber <= maxLinesToRead)) {
            return reader.readLine();
        }

        return null;
    }

    /**
     * Gets the maximum number of lines of the workload reader that will be read.
     * The value -1 indicates that all lines will be read, creating
     * a cloudlet from every one.
     *
     * @return
     */
    public int getMaxLinesToRead() {
        return maxLinesToRead;
    }

    /**
     * Sets the maximum number of lines of the workload reader that will be read.
     * The value -1 indicates that all lines will be read, creating
     * a cloudlet from every one.
     *
     * @param maxLinesToRead the maximum number of lines to set
     */
    public void setMaxLinesToRead(int maxLinesToRead) {
        this.maxLinesToRead = maxLinesToRead;
    }

    /**
     * Gets the MIPS capacity of the PEs from the VM where each created Cloudlet is supposed to run.
     * Considering the workload reader provides the run time for each
     * application registered inside the reader, the MIPS value will be used
     * to compute the {@link Cloudlet#getLength() length of the Cloudlet (in MI)}
     * so that it's expected to execute, inside the VM with the given MIPS capacity,
     * for the same time as specified into the workload reader.
     */
    public int getMips() {
        return mips;
    }

    /**
     * Sets the MIPS capacity of the PEs from the VM where each created Cloudlet is supposed to run.
     * Considering the workload reader provides the run time for each
     * application registered inside the reader, the MIPS value will be used
     * to compute the {@link Cloudlet#getLength() length of the Cloudlet (in MI)}
     * so that it's expected to execute, inside the VM with the given MIPS capacity,
     * for the same time as specified into the workload reader.
     *
     * @param mips the MIPS value to set
     */
    public final WorkloadFileReader setMips(final int mips) {
        if (mips <= 0) {
            throw new IllegalArgumentException("MIPS must be greater than 0.");
        }
        this.mips = mips;
        return this;
    }

}
