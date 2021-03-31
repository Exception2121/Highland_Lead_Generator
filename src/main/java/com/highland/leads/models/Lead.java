package com.highland.leads.models;

import java.io.File;

public class Lead {
    private String date;
    private String docType;
    private String grantor; // buyer(s)
    private String grantee; // Lender(s)
    private String subdivision;
    private String address;
    private String lot;
    private String section;
    private String legalDesc;
    private String mortgage;
    private String transfer;
    private File pdf;
    private String builder;
    private String firstDate;
    private String lastDate;
    private String imageLink;

    public Lead(String date, String docType, String grantor, String grantee, String subdivision, String address, String lot, String section, String legalDesc, String mortgage, String transfer, File pdf, String builder, String firstDate, String lastDate, String imageLink) {
        this.date = date;
        this.docType = docType;
        this.grantor = grantor;
        this.grantee = grantee;
        this.subdivision = subdivision;
        this.address = address;
        this.lot = lot;
        this.section = section;
        this.legalDesc = legalDesc;
        this.mortgage = mortgage;
        this.transfer = transfer;
        this.pdf = pdf;
        this.builder = builder;
        this.firstDate = firstDate;
        this.lastDate = lastDate;
        this.imageLink = imageLink;
    }

    @Override
    public String toString()
    {
        return
                "-------- com.higland.leads.Lead Start --------" + "\n" +
                "Date: " + date + "\n" +
                "Document Type: " + docType + "\n" +
                "Buyer(s): " + grantor + "\n" +
                "Lessor(s): " + grantee + "\n" +
                "Address: " + address + "\n" +
                "Subdivision: " + subdivision + "\n" +
                "Lot: " + lot + "\n" +
                "Section: " + section + "\n" +
                "Legal Desc: " + legalDesc + "\n" +
                "Mortgage Amt: " + mortgage + "\n" +
                "Transfer Amt: " + transfer + "\n" +
                "Builder: " + builder + "\n" +
                "First Date: " + firstDate + "\n" +
                "Last Date: " + lastDate + "\n" +
                "-------- com.higland.leads.Lead End --------" + "\n";

    }

    public String getFirstDate() {
        return firstDate;
    }

    public void setFirstDate(String firstDate) {
        this.firstDate = firstDate;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String pdfLink) {
        this.imageLink = pdfLink;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBuilder() {
        return builder;
    }

    public void setBuilder(String builder) {
        this.builder = builder;
    }

    public String getAddress() {
        return address;
    }

    public String getDate() {
        return date;
    }

    public String getDocType() {
        return docType;
    }

    public String getGrantor() {
        return grantor;
    }

    public String getGrantee() {
        return grantee;
    }

    public String getSubdivision() {
        return subdivision;
    }

    public String getLot() {
        return lot;
    }

    public String getSection() {
        return section;
    }

    public String getLegalDesc() {
        return legalDesc;
    }

    public String getMortgage() {
        return mortgage;
    }

    public String getTransfer() {
        return transfer;
    }

    public File getPdf() {
        return pdf;
    }
}
