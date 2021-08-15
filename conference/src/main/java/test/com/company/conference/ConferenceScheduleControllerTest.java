package test.com.company.conference;

import com.company.conference.controller.ConferenceScheduleController;
import com.company.conference.enums.SessionType;
import com.company.conference.models.SessionDetails;
import com.company.conference.models.Talks;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import static org.junit.Assert.*;

import java.io.File;
import java.time.LocalTime;
import java.util.*;

/** 
* ConferenceScheduleController Tester. 
* 
* @version 1.0
*/ 
public class ConferenceScheduleControllerTest {

    List<SessionDetails> sessionsList;

    @Before
public void before() throws Exception {
    System.out.println("Before called");
    testReadDataFromFile();
}

@After
public void after() throws Exception {
    System.out.println("After called");
} 

/** 
* 
* Method: schedule(List<SessionDetails> dataMap) 
* 
*/ 
@Test
public void testSchedule() throws Exception { 
//TODO: Test goes here...
    ConferenceScheduleController conferenceScheduleController = new ConferenceScheduleController();
    Map<String, SortedSet<Talks>> mapped = conferenceScheduleController.schedule(sessionsList);
    mapped.forEach((s, talks) -> {
       talks.forEach(talks1 -> {
           if (talks1.getStartTime().isAfter(LocalTime.of(12,0)) &&
           talks1.getStartTime().isBefore(LocalTime.of(13,0))) {
               assertEquals(SessionType.LUNCH,talks1.getType());
           }
           if (talks1.getStartTime().isAfter(LocalTime.of(17,0))) {
               assertEquals(SessionType.NETWORK,talks1.getType());
           }
       });
    });
    assertNotNull(mapped);
} 

/** 
* 
* Method: readDataFromFile(String filepath) 
* 
*/ 
@Test
public void testReadDataFromFile() throws Exception { 
//TODO: Test goes here...
    File file = new File("resources/input");
    Scanner scanner = new Scanner(file);
    String input;
    StringBuilder builder = new StringBuilder();
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

    sessionsList = sessionDetailsList;
    assertNotNull(sessionDetailsList.size());
} 



} 
