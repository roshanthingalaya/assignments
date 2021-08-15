package com.company.conference.controller;

import com.company.conference.enums.SessionType;
import com.company.conference.models.SessionDetails;
import com.company.conference.models.Talks;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.*;

/**
 * Class which has the core functionalities to schedule and cater to the conference scheduling
 */
public class ConferenceScheduleController {


    /**
     * @param dataMap param passed from the input res file
     *                Primary function is to align the data and arrange into tracks
     */
    public Map<String, SortedSet<Talks>> schedule(List<SessionDetails> dataMap){
        Map<String, SortedSet<Talks>> tracksMap = new HashMap<>();

        SortedSet<Talks> sessionsSet = new TreeSet<>();
        AtomicInteger id= new AtomicInteger();
        AtomicInteger counter= new AtomicInteger();
        counter.set(0);
        dataMap.forEach(sessionDetails -> {
            id.getAndIncrement();

            LocalTime startTime;
            LocalTime endTime;

            if(!(sessionsSet.isEmpty())) {

                Talks talks = new Talks();
                talks.setId(id.get());
                talks.setSession(sessionDetails.getEvent());
                talks.setDuration(sessionDetails.getDuration());
                endTime = sessionsSet.last().getEndTime().plusMinutes(sessionDetails.getDuration());
                talks.setStartTime(sessionsSet.last().getEndTime());
                talks.setEndTime(endTime);

                if (talks.getStartTime().isBefore(LocalTime.of(12, 00))) {
                    talks.setType(SessionType.MORNING);
                } else if (talks.getStartTime().isAfter(LocalTime.of(12, 00)) &&
                        talks.getStartTime().isBefore(LocalTime.of(17, 00))) {
                    talks.setType(SessionType.AFTERNOON);
                }

                if (talks.getEndTime().isAfter(LocalTime.of(12,0))&&
                        talks.getStartTime().isBefore(LocalTime.of(13,0))){

                        Talks lunchBreak = new Talks();
                        lunchBreak.setId(id.get());
                        lunchBreak.setSession("LUNCH");
                        lunchBreak.setType(SessionType.LUNCH);
                        lunchBreak.setDuration(60);
                        lunchBreak.setStartTime(LocalTime.of(12, 0));
                        lunchBreak.setEndTime(LocalTime.of(13, 0));

                        sessionsSet.add(lunchBreak);

                    talks = new Talks();
                    talks.setId(id.get());
                    talks.setSession(sessionDetails.getEvent());
                    talks.setType(SessionType.AFTERNOON);
                    talks.setDuration(sessionDetails.getDuration());
                    endTime = LocalTime.of(13, 00).plusMinutes(sessionDetails.getDuration());
                    talks.setStartTime(LocalTime.of(13, 00));
                    talks.setEndTime(endTime);

                }else if(talks.getEndTime().isAfter(LocalTime.now().withHour(16).withMinute(59))){

                    Talks network = new Talks();
                    network.setId(id.get());
                    network.setSession("NETWORKING");
                    network.setType(SessionType.NETWORK);
                    network.setDuration(120);
                    network.setStartTime(sessionsSet.last().getEndTime());
                    network.setEndTime(LocalTime.of(19,0));

                    sessionsSet.add(network);

                    SortedSet<Talks> sessionsSetClone = new TreeSet<>();
                    sessionsSetClone.addAll(sessionsSet);
                    tracksMap.put("Track"+counter.incrementAndGet(),sessionsSetClone);
                    sessionsSet.clear();

                    Talks newtalks = new Talks();
                    newtalks.setId(id.get());
                    newtalks.setSession(sessionDetails.getEvent());
                    newtalks.setType(SessionType.MORNING);
                    newtalks.setDuration(sessionDetails.getDuration());
                    newtalks.setStartTime(LocalTime.of(9,0));
                    newtalks.setEndTime(LocalTime.of(9,0).plusMinutes(sessionDetails.getDuration()));

                    sessionsSet.add(newtalks);
                    talks=null;

                }

                if(talks!=null) {
                    sessionsSet.add(talks);
                }
            }else{

                Talks talks = new Talks();
                talks.setId(id.get());
                talks.setSession(sessionDetails.getEvent());
                talks.setType(SessionType.MORNING);
                talks.setDuration(sessionDetails.getDuration());
                talks.setStartTime(LocalTime.of(9,0));
                talks.setEndTime(LocalTime.of(9,0).plusMinutes(sessionDetails.getDuration()));


                if(talks!=null) {
                    sessionsSet.add(talks);
                }
            }

        });

        if(!sessionsSet.isEmpty()) {
            tracksMap.put("Track" + counter.incrementAndGet(), sessionsSet);
            if(sessionsSet.last().getEndTime().isAfter(LocalTime.of(15,59))){
                Talks network = new Talks();
                network.setId(id.get());
                network.setSession("NETWORKING");
                network.setType(SessionType.NETWORK);
                network.setDuration(120);
                network.setStartTime(sessionsSet.last().getEndTime());
                network.setEndTime(LocalTime.of(19,0));
                sessionsSet.add(network);
            }
        }


        validateTimeSlots(tracksMap);

        return tracksMap;
    }

    /**
     * @param tracksMap Validate and arrange the sessions by
     *                  swapping the sessions within the same track
     */
    private void validateTimeSlots(Map<String, SortedSet<Talks>> tracksMap) {
        Map<String,Map<SessionType,List<Talks>>> tracker = new HashMap<>();
        Map<String,Map<SessionType,List<Talks>>> trackerClone = new HashMap<>();
        final Map<SessionType, List<Talks>>[] talksMapper = new Map[]{null};

        tracksMap.forEach((sessions, talks) -> {
            talksMapper[0] = talks.stream().collect(groupingBy(Talks::getType));
            tracker.putIfAbsent(sessions,talksMapper[0]);
        });

        tracker.forEach((s, sessionTypeListMap) -> {
            AtomicInteger morningTalkDuration = new AtomicInteger();
            AtomicInteger afternoonTalkDuration = new AtomicInteger();
            List<Talks> morningTalks = sessionTypeListMap.get(SessionType.MORNING);
            List<Talks> afternoonTalks = sessionTypeListMap.get(SessionType.AFTERNOON);

            SortedSet<Talks> talksSortedSet = tracksMap.get(s);

            if(morningTalks != null) {
                morningTalks.iterator().forEachRemaining(talks -> {
                    morningTalkDuration.set(morningTalkDuration.get() + talks.getDuration());
                });
            }

            if(afternoonTalks != null) {
                afternoonTalks.iterator().forEachRemaining(talks -> {
                    afternoonTalkDuration.set(afternoonTalkDuration.get() + talks.getDuration());
                });
            }
            int morningTalkDifference = 0, afternoonTalkDifference = 0;
            if (morningTalkDuration.get() != 180) { //3 hours span will give us 180 mins of lecture
                morningTalkDifference = 180 - morningTalkDuration.get();

            }

            if (afternoonTalkDuration.get() < 180 || afternoonTalkDuration.get() > 240) { //4 hours span will give us 240 mins of lecture
                if (afternoonTalkDuration.get() < 180)
                    afternoonTalkDifference = 180 - afternoonTalkDuration.get();
                else
                    afternoonTalkDifference = afternoonTalkDuration.get() - 240;
            }

            int finalMorningTalkDifference = morningTalkDifference;
            talksSortedSet.stream().iterator().forEachRemaining(talks -> {

                morningTalks.forEach(morngTalks -> {
                    if (talks.getDuration() > morngTalks.getDuration() &&
                            (talks.getDuration() - morngTalks.getDuration() == finalMorningTalkDifference)
                            && talks.getType().equals(SessionType.AFTERNOON)) {


                        Talks temp = new Talks();
                        temp.setType(morngTalks.getType());
                        temp.setEndTime(morngTalks.getEndTime());
                        temp.setDuration(morngTalks.getDuration());
                        temp.setStartTime(morngTalks.getStartTime());
                        temp.setId(morngTalks.getId());
                        temp.setSession(morngTalks.getSession());

                        morngTalks.setType(morngTalks.getType());
                        morngTalks.setEndTime(morngTalks.getStartTime().plusMinutes(talks.getDuration()));
                        morngTalks.setDuration(talks.getDuration());
                        morngTalks.setStartTime(morngTalks.getStartTime());
                        morngTalks.setId(talks.getId());
                        morngTalks.setSession(talks.getSession());

                        talks.setType(talks.getType());
                        talks.setEndTime(talks.getStartTime().plusMinutes(temp.getDuration()));
                        talks.setDuration(temp.getDuration());
                        talks.setStartTime(talks.getStartTime());
                        talks.setId(temp.getId());
                        talks.setSession(temp.getSession());

                        //talks = morngTalks;
                    }else{

                    }
                });



            });

            sortTime(morningTalks);
            sortTime(afternoonTalks);

        });
    }

    private void sortTime(List<Talks> afternoonTalks) {
        if (afternoonTalks != null && !afternoonTalks.isEmpty()) {
            for (int i = 1; i < afternoonTalks.size(); i++) {
                Talks talks = afternoonTalks.get(i);
                talks.setStartTime(afternoonTalks.get(i - 1).getEndTime());
                talks.setEndTime(afternoonTalks.get(i - 1).getEndTime().plusMinutes(talks.getDuration()));
            }
        }
    }

    /**
     * @param filepath
     */
    public List<SessionDetails> readDataFromFile(String filepath) throws FileNotFoundException {
        Scanner scanner = new Scanner(ConferenceScheduleController.class.getResourceAsStream(filepath));
        String input;
        List<SessionDetails> sessionDetailsList = new ArrayList<>();
        //Map<String,Integer> dataMap = new HashMap<>();
        while(scanner.hasNextLine()){
            input = scanner.nextLine();
            String[] sessionDetails = input.split(" - ");
            //dataMap.putIfAbsent(sessionDetails[0].trim(),Integer.valueOf(sessionDetails[1].replace("min","").trim()));
            SessionDetails details = new SessionDetails();
            details.setEvent(sessionDetails[0]);
            details.setDuration(Integer.valueOf(sessionDetails[1].replace("min","")));
            sessionDetailsList.add(details);
        }

       return sessionDetailsList;
    }

}
