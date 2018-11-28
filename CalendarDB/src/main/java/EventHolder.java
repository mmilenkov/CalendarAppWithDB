import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

class EventHolder {

    private static ArrayList<Event> eventList = new ArrayList<Event>();
    private final static String dbUrl = "jdbc:mysql://localhost:3306/event_list";
    private final static String dbUser = "root";
    private final static String dbPass = "admin15";

    private static void retrieveEvents(String query) {
        if(eventList.size() > 0) {
            eventList.clear();
        }
        Connection connection = null;
        Statement stmt = null;
        Event newEvent;
        try {
            Driver dbDriver = new com.mysql.cj.jdbc.Driver();
            DriverManager.registerDriver(dbDriver);
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt("event_id");
                LocalDateTime startDate = rs.getTimestamp("start_date").toLocalDateTime();
                LocalDateTime endDate = rs.getTimestamp("end_date").toLocalDateTime();
                String name = rs.getString("name");
                String location = rs.getString("location");
                newEvent = new Event(id, name, startDate,endDate,location);
                eventList.add(newEvent);
            }
            rs.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(stmt != null) {
                    stmt.close();
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    static void addEventToDB (Event event) {
        Connection connection = null;
        Statement stmt = null;
        try {
            Driver dbDriver = new com.mysql.cj.jdbc.Driver();
            DriverManager.registerDriver(dbDriver);
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            String query = "INSERT INTO event(`start_date`,`end_date`, `name`, `location`) VALUES ('" +event.getStartDate() +"','" + event.getEndDate() + "','" + event.getName() + "','" + event.getLocation() + "')";
            stmt = connection.createStatement();
            stmt.executeUpdate(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(stmt != null) {
                    stmt.close();
                }
                /* SSL ERROR - Why?
                if(connection != null) {
                    connection.close();
                }*/
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    static void editEventInDb (Event event, int eventID) {
        Connection connection = null;
        Statement stmt = null;
        try {
            Driver dbDriver = new com.mysql.cj.jdbc.Driver();
            DriverManager.registerDriver(dbDriver);
            connection = DriverManager.getConnection(dbUrl,dbUser,dbPass);
            stmt = connection.createStatement();
            String query = "UPDATE `event` SET `start_date`='" + event.getStartDate() + "',`end_date`='" + event.getEndDate() + "',`name`='"+ event.getName() +"', `location`='" + event.getLocation() + "' WHERE `event_id`="+eventID;
            stmt.executeUpdate(query);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(stmt != null) {
                    stmt.close();
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    static void deleteEvent (int eventID) {
        Connection connection = null;
        Statement stmt = null;
        try {
            Driver dbDriver = new com.mysql.cj.jdbc.Driver();
            DriverManager.registerDriver(dbDriver);
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            String query = "DELETE FROM `event` WHERE event_id=" +eventID;
            stmt= connection.createStatement();
            stmt.executeUpdate(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(stmt != null) {
                    stmt.close();
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    static void generateQueryToPrintAllEvents(PrintWriter out) {
        String query = "SELECT * FROM `event` ORDER BY `start_date`";
        retrieveEvents(query);
        printEventsFromList(out);
    }

    static void generateQueryToPrintEventsForASpecificMonth(PrintWriter out, String date) {
        String endDate = handleDateIncrementation(date);
        String selectedPeriodStart = "'" + date + "-01'";
        String selectedPeriodEnd = "'" + endDate + "-01'";
        String query = "SELECT * FROM `event` WHERE `start_date` BETWEEN " + selectedPeriodStart + "AND " +selectedPeriodEnd;
        retrieveEvents(query);
        printEventsFromList(out);
    }

    private static String handleDateIncrementation(String date) {
        String[] slice = date.split("-");
        int year = Integer.valueOf(slice[0]);
        int month = Integer.valueOf(slice[1]);
        if(month == 12) {
            year+=1;
        }
        if(month == 12) {
            month = 1;
        }
        else {
            month += 1;
        }
        return (year + "-"+ month);
    }

    private static void printEventsFromList(PrintWriter out) {
        for (Event event : eventList) {
            if(event != null) {
                out.print("<p>Event ID: " + event.getId() + "</p>");
                out.print("<p>" + event.getStartDate().getDayOfMonth() + " " + event.getStartDate().getMonth() + " " + event.getStartDate().getYear() + "</p>");
                out.print("<p>Event name: " + event.getName() + "</p>");
                out.print("<p>Start time: " + event.getStartDate().getHour() + ":" + event.getStartDate().getMinute() + "</p>");
                out.print("<p>End time: " + event.getEndDate().getHour() + ":" + event.getEndDate().getMinute() + "</p>");
                out.print("<p>Location: " + event.getLocation() + "</p>");
                out.print("<br>");
            }
        }
    }

}
