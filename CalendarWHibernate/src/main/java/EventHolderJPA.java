import javax.persistence.*;
import javax.transaction.Transactional;
import java.io.PrintWriter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Transactional
class EventHolderJPA {

    private static List<Event> eventList = new LinkedList<Event>();

    static List<Event> getEventList() {
        return eventList;
    }

    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");
    private static EntityManager entityManager = emf.createEntityManager();
    private static EntityTransaction tx = entityManager.getTransaction();

    static void addEvent(Event event) {
        tx.begin();
        entityManager.persist(event);
        tx.commit();
    }

    static void removeEvent(int eventId) {
        Event toDelete = entityManager.find(Event.class,eventId);
        if(toDelete != null) {
            tx.begin();
            entityManager.remove(toDelete);
            tx.commit();
        }
    }

    static void editEvent(Event event, int eventId) {
        Event toEdit = entityManager.find(Event.class,eventId);
        Location newLocation = entityManager.find(Location.class,event.getLocationId());
        tx.begin();
        toEdit.setName(event.getName());
        toEdit.setStartDate(event.getStartDate());
        toEdit.setEndDate(event.getEndDate());
        toEdit.setLocationId(event.getLocationId());
        toEdit.setLocationO(newLocation);
        tx.commit();
    }

    static private void retrieveEvents(String searchTerm) {
        TypedQuery<Event> query = entityManager.createQuery(searchTerm,Event.class);
        eventList = query.getResultList();
    }

    static void generateSearchPrintAllEvents() {
        String searchTerm = "SELECT e FROM Event e";
        retrieveEvents(searchTerm);
    }

    static void generateSearchPrintMonth(String date) {
        String[] slice = date.split("-");
        int year = Integer.valueOf(slice[0]);
        int month = Integer.valueOf(slice[1]);
        Date initialDate = new Date(year-1900,month-1,1,0,0,0);
        Date secondaryDate = generateSecondaryDate(year, month);

        TypedQuery<Event> query = entityManager.createQuery("Select e FROM Event e Where e.startDate BETWEEN ?1 AND ?2",Event.class);
        query.setParameter(1,initialDate);
        query.setParameter(2,secondaryDate);
        eventList = query.getResultList();
    }

    static void generateSearchPrintDay(String date) {
        String[] slice = date.split("-");
        int year = Integer.valueOf(slice[0]);
        int month = Integer.valueOf(slice[1]);
        int day = Integer.valueOf(slice[2]);
        Date initialDate = new Date(year-1900,month-1,day,0,0,0);
        Date secondaryDate = new Date(year-1900,month-1,day,23,59,59);


        TypedQuery<Event> query = entityManager.createQuery("Select e FROM Event e Where e.startDate BETWEEN ?1 AND ?2",Event.class);
        query.setParameter(1,initialDate);
        query.setParameter(2,secondaryDate);
        eventList = query.getResultList();
    }

    static Date generateSecondaryDate(int year, int month) {
        if(month == 12) {
            year += 1;
        }
        if(month == 12) {
            month = 1;
        }
        else {
            month += 1;
        }
        return new Date(year-1900,month-1,1,23,59,0);
    }

    static void printEventList(PrintWriter out) {
        out.print(
                "<table align=\"center\">"
                        + "<thead>"
                        + "<tr>"
                        + "<th> Select </th>"
                        + "<th> Date </th>"
                        + "<th> Name </th>"
                        + "<th> Start time </th>"
                        + "<th> End time </th>"
                        + "<th> Location </th>"
                        + "</tr>"
                        + "</thead>"
                        + "<tbody id=\"eventTableBody\">"
        );
        if(eventList.size() == 0) {
            out.print(
                    "<tr>"
                            + "<td colspan=\"6\"> There are no events for this period.</td>"
                            + "</tr>"
            );
        }
        if (eventList.size() > 0) {
            for (Event event : eventList) {
                out.print(
                        "<tr>"
                                + "<td><input type=\"checkbox\" name=\"eventId\" value=\"" + event.getEventID() + "\"></td>"
                                + "<td>" + event.getStartDate().getDate() + " " + (event.getStartDate().getMonth()+1) + " " + (event.getStartDate().getYear()+1900) + "</td>"
                                + "<td>" + event.getName() + "</td>"
                                + "<td>" + event.getStartDate().getHours() + ":" + event.getStartDate().getMinutes() + "</td>"
                                + "<td>" + event.getEndDate().getHours() + ":" + event.getEndDate().getMinutes() + "</td>"
                                + "<td>" + event.getLocationO().getLocation() + "</td>"
                                + "</tr>"
                );
            }
        }
        out.print(
                "</tbody>"
                        + "</table>"
                        + "<button class=\"buttonStyle\" onclick=\"printAddEventDiv()\">Add an Event</button>"
                        + "<button class=\"buttonStyle\" onclick=\"printEditEventDiv()\">Edit an Event</button>"
                        + "<button class=\"buttonStyle\" onclick=\"removeEvent()\">Delete an Event</button>"
        );
    }

}
