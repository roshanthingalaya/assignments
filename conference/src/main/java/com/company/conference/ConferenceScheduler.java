package com.company.conference;

import com.company.conference.controller.ConferenceScheduleController;
import com.company.conference.models.SessionDetails;
import com.company.conference.models.Talks;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * Main class
 */
public class ConferenceScheduler extends ConferenceScheduleController {

    public static void main(String[] args) throws FileNotFoundException {

	    ConferenceScheduler conferenceScheduler = new ConferenceScheduler();

	    List<SessionDetails> dataMap = conferenceScheduler.readDataFromFile("/input");
        Map<String, SortedSet<Talks>> result = conferenceScheduler.schedule(dataMap);
        result.forEach((s, talks) -> {
            System.out.println("++++++++Next Track+++++++++++");
            talks.forEach(talks1 ->{
                System.out.println(s + " " + talks1.getStartTime() + " " +talks1.getSession() + " " + talks1.getDuration()+"mins");
            } );

        });
        //conferenceScheduler.scheduleNew(dataMap);


    }
}
