package com.highland.leads.services;

import com.highland.leads.models.Excel;
import com.highland.leads.models.JobRequest;
import com.highland.leads.models.Lead;
import com.highland.leads.cosmos.Database;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.*;
import java.time.LocalTime;
import java.util.*;

public class PDFReader {
    private File document;
    private Tesseract tesseract;
    private static boolean runOnce = false;
    static int processNum = 0;
    public static LinkedList<Lead> processed;
    public static int currentlyProcessing = 0;
    public static LinkedList<File> queue;
    public static Excel excel;
    public static boolean finished = false;

    public PDFReader(){
        System.out.println("PDFReader loaded");
    }
    PDFReader(File document) {
        try {
            if(!runOnce) {
                queue = new LinkedList<>();
                processed = new LinkedList<>();
            }
            tesseract = new Tesseract();
            tesseract.setDatapath(System.getProperty("user.dir") + "//tessData");
            this.document = document;
            queue.add(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void start(){
        if(!runOnce) {
            excel = new Excel();
            CheckQueue cq = new CheckQueue();
            cq.start();
            runOnce = true;
        }
    }

    private class CheckQueue extends Thread
    {
        public void run()
        {
            while(currentlyProcessing != 0 || queue.size() != 0)
            {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(currentlyProcessing < 1 & queue.size()>0)
                {
                    currentlyProcessing++;
                    document = queue.poll();
                    processThread t = new processThread();
                    t.start();
                }
            }
            //long endTime = System.nanoTime();
            //long seconds = (endTime - GUI.startTime)/1000000000;
            //long minutes = seconds / 60;
            //long hours = minutes / 60;
            //minutes = minutes - (hours*60);
            //System.out.println("Runtime: " + hours + " hours " + minutes + " minutes");
            WebScanner.jobRequest.setEndTime(LocalTime.now());
            String fileName = System.getProperty("user.dir") + "\\results.xls";
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource(fileName).getFile());
            WebScanner.jobRequest.setStatus(JobRequest.Status.SUCCEEDED);
            WebScanner.jobRequest.setFile(file);
            Database.updateJobRequest(WebScanner.jobRequest);
            finished = true;
        }
    }

    private class processThread extends Thread
    {
        public void run()
        {
            String results = "Couldn't process pdf!";
            try {
                results = tesseract.doOCR(document);
            } catch (TesseractException e) {
                e.printStackTrace();
            }

            try {
                WebScanner.leads[processNum].setAddress(findAddress(results));
                WebScanner.leads[processNum].setBuilder(findBuilder(results));
                WebScanner.leads[processNum].setFirstDate(findFirstDate(results));
                WebScanner.leads[processNum].setLastDate(findLastDate(results));

                System.out.println("Number Skipped: " + WebScanner.numSkipped);
                System.out.println("File: " + (processNum + 1) + " / " + (WebScanner.limit - WebScanner.numSkipped));
                System.out.println(WebScanner.leads[processNum].toString());
                //System.out.println("\n\n" + results + "\n\n");
            } catch (Exception e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
            excel.updateData(WebScanner.leads[processNum]);
            processed.add(WebScanner.leads[processNum]);
            Excel.currentRow++;
            processNum++;
            currentlyProcessing--;
        }
    }

    private String findAddress(String field)
    {
        try {
            String addressString = field;
            ArrayList<String> replacementString = new ArrayList<>();

            replacementString.add("[Zip Code");
            replacementString.add("{Zip Code");
            replacementString.add("(Zip Code");
            replacementString.add("(City");
            replacementString.add("{City");
            replacementString.add("[City");
            replacementString.add("{Street");
            replacementString.add("[Street");
            replacementString.add("(Street");
            replacementString.add("[Stread");
            replacementString.add("[Stees");
            replacementString.add("[Sweet");
            replacementString.add("[Straet");
            replacementString.add("/");
            replacementString.add("\\");
            replacementString.add("|");
            replacementString.add(":");
            replacementString.add("-");
            replacementString.add("+");
            replacementString.add("[");
            replacementString.add("]");
            replacementString.add("{");
            replacementString.add("}");
            replacementString.add(";");
            replacementString.add("\"");
            replacementString.add("!");
            replacementString.add("(");
            replacementString.add(")");
            replacementString.add("©");
            replacementString.add("»");

            if(field.contains("currently has the address of"))
            {
                addressString = addressString.substring(addressString.indexOf("currently has the address of") + 29, addressString.indexOf("currently has the address of")+200);
                addressString = addressString.substring(0, addressString.lastIndexOf("37") + 5);
                for(int i=0;i<replacementString.size();i++)
                {
                    addressString = addressString.replace(replacementString.get(i),"");
                }
            }else if(field.contains("commonly known as"))
            {
                addressString = addressString.substring(addressString.indexOf("commonly known as") + 18, addressString.indexOf("commonly known as")+200);
                addressString = addressString.substring(0, addressString.lastIndexOf("37") + 5);
                for(int i=0;i<replacementString.size();i++)
                {
                    addressString = addressString.replace(replacementString.get(i),"");
                }
            }else if(field.contains("KNOWN BY THE STREET ADDRESS OF"))
            {
                addressString = addressString.substring(addressString.indexOf("KNOWN BY THE STREET ADDRESS OF") + 33, addressString.indexOf("KNOWN BY THE STREET ADDRESS OF")+200);
                addressString = addressString.substring(0, addressString.lastIndexOf("37") + 5);
                for(int i=0;i<replacementString.size();i++)
                {
                    addressString = addressString.replace(replacementString.get(i),"");
                }
            }else if(field.contains("which currently has the"))
            {
                addressString = addressString.substring(addressString.indexOf("which currently has the") + 36, addressString.indexOf("which currently has the")+200);
                addressString = addressString.substring(0, addressString.lastIndexOf("37") + 5);
                for(int i=0;i<replacementString.size();i++)
                {
                    addressString = addressString.replace(replacementString.get(i),"");
                }
            }else if(field.contains("located at"))
            {
                addressString = addressString.substring(addressString.indexOf("located at") + 11, addressString.indexOf("located at")+200);
                addressString = addressString.substring(0, addressString.lastIndexOf("37") + 5);
                for(int i=0;i<replacementString.size();i++)
                {
                    addressString = addressString.replace(replacementString.get(i),"");
                }
            }else if(field.contains("at the following address"))
            {
                addressString = addressString.substring(addressString.indexOf("at the following address") + 25, addressString.indexOf("at the following address")+200);
                addressString = addressString.substring(0, addressString.lastIndexOf("37") + 5);
                for(int i=0;i<replacementString.size();i++)
                {
                    addressString = addressString.replace(replacementString.get(i),"");
                }
            }else if(field.contains("also known as"))
            {
                addressString = addressString.substring(addressString.indexOf("also known as") + 14, addressString.indexOf("also known as")+200);
                addressString = addressString.substring(0, addressString.lastIndexOf("37") + 5);
                for(int i=0;i<replacementString.size();i++)
                {
                    addressString = addressString.replace(replacementString.get(i),"");
                }
            }else if(field.contains("Address:"))
            {
                addressString = addressString.substring(addressString.indexOf("Address:") + 8, addressString.indexOf("Address:")+200);
                addressString = addressString.substring(0, addressString.lastIndexOf("37") + 5);
                for(int i=0;i<replacementString.size();i++)
                {
                    addressString = addressString.replace(replacementString.get(i),"");
                }
            }else if(field.contains("in Williamson County at"))
            {
                addressString = addressString.substring(addressString.indexOf("in Williamson County at") + 24, addressString.indexOf("in Williamson County at")+200);
                addressString = addressString.substring(0, addressString.lastIndexOf("37") + 5);
                for(int i=0;i<replacementString.size();i++)
                {
                    addressString = addressString.replace(replacementString.get(i),"");
                }
            }
            if(addressString.length() < 300)
                return addressString.replace("\n", "");
        }catch (Exception e) { }
        return "n/a";
    }

    private String findBuilder(String field)
    {
        try {
            ArrayList<String> builders = new ArrayList<>();
            builders.add("Tennessee Valley Homes");
            builders.add("Botsko Builders");
            builders.add("Forte Building Group");
            builders.add("DeFatta Custom Homes");
            builders.add("Superior Custom Homes & Remodeling");
            builders.add("Hallmark Building Group");
            builders.add("Hughes Edwards Builders");
            builders.add("The Jones Company of TN");
            builders.add("Majestic Building");
            builders.add("New Castle Properties");
            builders.add("Valley View Builders");
            builders.add("Rosanbalm Homes");
            builders.add("Huseby Homes");
            builders.add("Focus Builders");
            builders.add("The Tant Companies");
            builders.add("MDC General Contractor");
            builders.add("Barlow Builders");
            builders.add("Ford Custom Builders");
            builders.add("Rains Building Group");
            builders.add("Celebration Homes");
            builders.add("John Wieland Homes and Neighborhoods");
            builders.add("Home and Neighborhoods");
            builders.add("Mike Ford Custom Builders");
            builders.add("Turnberry Homes");
            builders.add("NSH Nashville");
            builders.add("JW Homes");
            builders.add("Santoro Custom Builders");
            builders.add("Building of Te");
            builders.add("Cadence Construction");
            builders.add("Drees Premier");
            builders.add("SLC Homebuilding");
            builders.add("Horton Inc");
            builders.add("Meadowbrook Companies");

            //Generic
            builders.add("Homes, LLC");
            builders.add("Homes, L.L.C");
            builders.add("Building and Development");
            builders.add("Building & Development");
            builders.add("Construction Company");
            builders.add("Construction LLC");
            builders.add("Construction, LLC");
            builders.add("Construction INC");
            builders.add("Construction, INC");
            builders.add("Construction CO");
            builders.add("Premier Homes");
            builders.add("Custom Homes");
            builders.add("Custom Builders");
            builders.add("Homebuild");
            builders.add("Builders");
            builders.add("Contractor");
            builders.add("Building Group");
            builders.add("Homes ");



            field = field.replace("\n", "");
            //field = field.toLowerCase();
            for(int i=0;i<builders.size();i++)
            {
                if(field.toLowerCase().contains(builders.get(i).toLowerCase())) {
                    field = field.substring(field.toLowerCase().indexOf(builders.get(i).toLowerCase()) - 15, field.toLowerCase().indexOf(builders.get(i).toLowerCase()) + builders.get(i).length() + 15);
                    if(field.toLowerCase().contains("llc"))
                        return field.substring(field.toLowerCase().indexOf(builders.get(i).toLowerCase()) - 15, field.toLowerCase().indexOf("llc")+3);
                    else if(field.toLowerCase().contains("inc"))
                        return field.substring(field.toLowerCase().indexOf(builders.get(i).toLowerCase()) - 15, field.toLowerCase().indexOf("inc")+3);
                    else if(field.toLowerCase().contains("l.l.c."))
                        return field.substring(field.toLowerCase().indexOf(builders.get(i).toLowerCase()) - 15, field.toLowerCase().indexOf("l.l.c")+6);
                    else
                        return field;
                }
            }
        }catch (Exception e)
        { }
        return "n/a";
    }

    private String findFirstDate(String field)
    {
        String[] lastDates = {""};
        field = field.toLowerCase();
        field = field.replace("\n","");
        field = field.replace("\"", "");
        field = field.replace("'", "");
        field = field.replace("“", "");
        field = field.replace("”", "");

        if(field.contains("exhibit a"))
        {
            try
            {
                field = field.substring(field.lastIndexOf("exhibit a"),field.lastIndexOf("exhibit a") + 1000);
                field = field.substring(0, field.lastIndexOf("book"));
            }catch (IndexOutOfBoundsException e)
            {
                field = field.substring(field.lastIndexOf("exhibit a"));
            }
        }


        lastDates = field.split("[,/ ]");

        ArrayList<Integer> numbers = new ArrayList();
        for (String s:lastDates
        ) {
            try{
                numbers.add(Integer.parseInt(s));
            }catch (Exception e){}
        }
        Integer lowest = 999999;
        for (Integer i:numbers
        ) {
            if(i < lowest && i < 2021 && i > 1980)
                lowest = i;
        }
        if(lowest.toString().length() < 5 && lowest != 999999)
            return lowest.toString();

        return "n/a";
    }

    private String findLastDate(String field)
    {
        String[] lastDates = {""};
        field = field.toLowerCase();
        field = field.replace("\n","");
        field = field.replace("\"", "");
        field = field.replace("'", "");
        field = field.replace("“", "");
        field = field.replace("”", "");

        if(field.contains("exhibit a"))
        {
            try
            {
                field = field.substring(field.lastIndexOf("exhibit a"),field.lastIndexOf("exhibit a") + 1000);
                field = field.substring(0, field.lastIndexOf("book"));
            }catch (IndexOutOfBoundsException e)
            {
                field = field.substring(field.lastIndexOf("exhibit a"));
            }
        }


        lastDates = field.split("[,/ ]");

        ArrayList<Integer> numbers = new ArrayList();
        for (String s:lastDates
        ) {
            try{
                numbers.add(Integer.parseInt(s));
            }catch (Exception e){}
        }
        Integer highest = 0;
        for (Integer i:numbers
        ) {
            if(i > highest && i < 2021 && i > 1980)
                highest = i;
        }
        if(highest.toString().length() < 5 && highest != 0)
            return highest.toString();

        return "n/a";
    }

}

