package com.highland.leads;

import com.highland.leads.services.PDFReader;
import com.highland.leads.services.WebScanner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author William Phillips
 */
public class Main{
    public static long startTime;
    public static long endTime;

    public static void main(String[] args) {
        try{
            WebScanner.startDate = args[0];
            WebScanner.endDate = args[1];
            WebScanner.username = args[2];
            WebScanner.password = args[3];

            WebScanner w = new WebScanner();
            Thread scanThread = new Thread(w);
            scanThread.start();
        }catch (IndexOutOfBoundsException e){
            System.out.println("Must specify arguments! \nstartDate:\nendDate\nusername\npasssword\n");
        }
    }
    public static ClassLoader getClassLoeader(){
        return Main.class.getClassLoader();
    }
}
