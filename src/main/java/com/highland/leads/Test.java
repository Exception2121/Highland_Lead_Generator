package com.highland.leads;

import com.highland.leads.cosmos.Database;
import com.highland.leads.models.JobRequest;

import java.time.LocalDate;
import java.time.LocalTime;

public class Test {
    public static void run() {
        JobRequest jobRequest = new JobRequest("3", JobRequest.Status.NOT_STARTED, LocalDate.now(), LocalDate.now(),LocalTime.now(), LocalTime.now(),10,20,null);
        System.out.println(jobRequest.toString());
        //Database.createJobRequest(jobRequest);
        //System.out.println(Database.getAllJobRequests().get(0).toString());
    }
}
