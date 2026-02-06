package coordinator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ScheduleGenerator {

    //get session
    public List<SessionManager.Session> getOrderedSchedule() {
        List<SessionManager.Session> allSessions = new ArrayList<>(SessionManager.getInstance().getAllSessions());
        if (allSessions.isEmpty()) {
            return allSessions;
        }
        //sort
        Collections.sort(allSessions, new Comparator<SessionManager.Session>() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            @Override
            public int compare(SessionManager.Session s1, SessionManager.Session s2) {
                try {
                    //get start time
                    String t1 = s1.time.split(" - ")[0];
                    String t2 = s2.time.split(" - ")[0];
                    Date d1 = sdf.parse(s1.date + " " + t1);
                    Date d2 = sdf.parse(s2.date + " " + t2);
                    return d1.compareTo(d2); //asc order
                } catch (Exception e) {
                    return 0;
                }
            }
        });

        return allSessions;
    }
}