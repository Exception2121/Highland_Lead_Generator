package com.highland.leads.models;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.highland.leads.models.Lead;
import com.highland.leads.services.WebScanner;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class Excel {
    HSSFWorkbook workbook;
    HSSFSheet sheet;
    Map<String, Object[]> data;
    Set<String> keyset;
    public static int currentRow = 1;
    String fileTime;
    public static String filepath;

    public Excel()
    {
        fileTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-hh-mm"));
        filepath =  "results" + fileTime + ".xls";
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet("Results sheet");

        data = new HashMap<String, Object[]>();
        for(int i = 0; i< WebScanner.limit; i++)
        {
            data.put("" + i, new Object[] {"null", "null", "null", "null", "null", "null", "null", "null", "null"});
        }

        keyset = data.keySet();
        int rownum = 0;
        for (String key : keyset) {
            Row row = sheet.createRow(rownum++);
            Object [] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                if(obj instanceof Date)
                    cell.setCellValue((Date)obj);
                else if(obj instanceof Boolean)
                    cell.setCellValue((Boolean)obj);
                else if(obj instanceof String)
                    cell.setCellValue((String)obj);
                else if(obj instanceof Double)
                    cell.setCellValue((Double)obj);
            }
        }

        try {
            FileOutputStream out = new FileOutputStream(filepath);
            workbook.write(out);
            out.close();
            System.out.println("com.higland.leads.Excel written successfully..");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateImageLink(int rowNum, String imageLink) {
        try {
            FileInputStream file = new FileInputStream(filepath);

            HSSFWorkbook workbook = new HSSFWorkbook(file);
            HSSFSheet sheet = workbook.getSheetAt(0);

            Cell cell = null;
            cell = sheet.getRow(0).getCell(8);
                cell.setCellValue("Image Link");
            cell = sheet.getRow(rowNum).getCell(8);
                cell.setCellValue(imageLink);

            file.close();

            FileOutputStream outFile =new FileOutputStream(filepath);
            workbook.write(outFile);
            outFile.close();
        }catch (Exception ignored){}
    }

    public void updateData(Lead lead)
    {
        String date = lead.getDate();
        String buyerName = lead.getGrantor();
        String price = lead.getMortgage();
        String address = lead.getAddress();
        String subdivision = lead.getSubdivision();
        String builder = lead.getBuilder();
        String firstDate = lead.getFirstDate();
        String lastDate = lead.getLastDate();
        String imageLink = lead.getImageLink();

        try {
            FileInputStream file = new FileInputStream(filepath);

            HSSFWorkbook workbook = new HSSFWorkbook(file);
            HSSFSheet sheet = workbook.getSheetAt(0);
            Cell cell = null;

            cell = sheet.getRow(0).getCell(0);
            cell.setCellValue("Date");
            cell = sheet.getRow(0).getCell(1);
            cell.setCellValue("Buyer Name(s)");
            cell = sheet.getRow(0).getCell(2);
            cell.setCellValue("Price");
            cell = sheet.getRow(0).getCell(3);
            cell.setCellValue("Address");
            cell = sheet.getRow(0).getCell(4);
            cell.setCellValue("Subdivision");
            cell = sheet.getRow(0).getCell(5);
            cell.setCellValue("Builder");
            cell = sheet.getRow(0).getCell(6);
            cell.setCellValue("First Transfer Date");
            cell = sheet.getRow(0).getCell(7);
            cell.setCellValue("Latest Transfer Date");


            //Update the value of cell
            cell = sheet.getRow(currentRow).getCell(0);
            cell.setCellValue(date);
            cell = sheet.getRow(currentRow).getCell(1);
            cell.setCellValue(buyerName);
            cell = sheet.getRow(currentRow).getCell(2);
            cell.setCellValue(price);
            cell = sheet.getRow(currentRow).getCell(3);
            cell.setCellValue(address);
            cell = sheet.getRow(currentRow).getCell(4);
            cell.setCellValue(subdivision);
            cell = sheet.getRow(currentRow).getCell(5);
            cell.setCellValue(builder);
            cell = sheet.getRow(currentRow).getCell(6);
            cell.setCellValue(firstDate);
            cell = sheet.getRow(currentRow).getCell(7);
            cell.setCellValue(lastDate);


            file.close();

            FileOutputStream outFile =new FileOutputStream(filepath);
            workbook.write(outFile);
            outFile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
