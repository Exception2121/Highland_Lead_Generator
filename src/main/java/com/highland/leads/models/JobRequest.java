package com.highland.leads.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.google.gson.Gson;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;

public class JobRequest {

    private String id;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate selectedStartDate;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate selectedEndDate;
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonSerialize(using = LocalTimeSerializer.class)
    private LocalTime startTime;
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonSerialize(using = LocalTimeSerializer.class)
    private LocalTime endTime;
    private Integer mortgageMinimum;
    private Integer mortgageMaximum;
    private Status status;
    private File file;

    public enum Status {
        NOT_STARTED("Not Started"), RUNNING("Running"), FAILED("Failed"), SUCCEEDED("Succeeded");
        public final String label;
        Status(String label) { this.label = label; }
        public static Status valueOfLabel(String label) {
            for (Status e : values()) {
                if (e.label.equals(label)) {
                    return e;
                }
            }
            return null;
        }
        @Override
        public String toString() {
            return label;
        }
    }

    public JobRequest(
            @JsonProperty("id")
                    String id,
            @JsonProperty("status")
                    Status status,
            @JsonProperty("selectedStartDate")
                    LocalDate selectedStartDate,
            @JsonProperty("selectedEndDate")
                    LocalDate selectedEndDate,
            @JsonProperty("startTime")
                    LocalTime startTime,
            @JsonProperty("endTime")
                    LocalTime endTime,
            @JsonProperty("mortgageMinimum")
                    Integer mortgageMinimum,
            @JsonProperty("mortgageMaximum")
                    Integer mortgageMaximum,
            @JsonProperty("file")
                    File file){
        this.id = id;
        this.status = status;
        this.selectedStartDate = selectedStartDate;
        this.selectedEndDate = selectedEndDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.mortgageMinimum = mortgageMinimum;
        this.mortgageMaximum = mortgageMaximum;
        this.file = file;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSelectedStartDate(LocalDate selectedStartDate) {
        this.selectedStartDate = selectedStartDate;
    }

    public void setSelectedEndDate(LocalDate selectedEndDate) {
        this.selectedEndDate = selectedEndDate;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setMortgageMinimum(Integer mortgageMinimum) {
        this.mortgageMinimum = mortgageMinimum;
    }

    public void setMortgageMaximum(Integer mortgageMaximum) {
        this.mortgageMaximum = mortgageMaximum;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public String getId() {
        return id;
    }

    public Integer getMortgageMinimum() {
        return mortgageMinimum;
    }

    public Integer getMortgageMaximum() {
        return mortgageMaximum;
    }

    public LocalDate getSelectedStartDate() {
        return selectedStartDate;
    }

    public LocalDate getSelectedEndDate() {
        return selectedEndDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString(){
        return new Gson().toJson(this);
    }
}
