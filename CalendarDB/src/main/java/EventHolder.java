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
            String query = "INSERT INTO event(`start_date`,`end_date`, `name`, `location_id`) VALUES ('" +event.getStartDate() +"','" + event.getEndDate() + "','" + event.getName() + "','" + event.getLocationId() + "')";
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
            String query = "UPDATE `event` SET `start_date`='" + event.getStartDate() + "',`end_date`='" + event.getEndDate() + "',`name`='"+ event.getName() +"', `location_id`='" + event.getLocationId() + "' WHERE `event_id`="+eventID;
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
        String query = "SELECT `event`.`event_id`,`event`.`start_date`,`event`.`end_date`,`event`.`name`,`location_list`.`location` " +
                "FROM `event` LEFT JOIN `location_list` ON `event`.`location_id`=`location_list`.`location_id` " +
                "ORDER BY `event`.`start_date`";
        retrieveEvents(query);
        printEventsFromList(out);
    }

    static void generateQueryToPrintEventsForASpecificMonth(PrintWriter out, String date) {
        String endDate = handleDateIncrementation(date);
        String selectedPeriodStart = "'" + date + "-01'";
        String selectedPeriodEnd = "'" + endDate + "-01'";
        String query = "SELECT `event`.`event_id`,`event`.`start_date`,`event`.`end_date`,`event`.`name`,`location_list`.`location`" +
                " FROM `event` LEFT JOIN `location_list` ON `event`.`location_id`=`location_list`.`location_id` " +
                "WHERE `event`.`start_date` BETWEEN " + selectedPeriodStart + "AND " +selectedPeriodEnd;
        retrieveEvents(query);
        printEventsFromList(out);
    }

    static void generateQueryToPrintEventsForASpecificDay(PrintWriter out, String date) {
        String query = "SELECT `event`.`event_id`,`event`.`start_date`,`event`.`end_date`,`event`.`name`,`location_list`.`location`" +
                " FROM `event` LEFT JOIN `location_list` ON `event`.`location_id`=`location_list`.`location_id` " +
                "WHERE `event`.`start_date` BETWEEN '" + date + " 00:00:00' AND '" + date + " 23:59:59'";
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
        out.print("<table align=\"center\">");
        out.print("<thead>");
        out.print("<tr>");
        out.print("<th> Select </th>");
        out.print("<th> Date </th>");
        out.print("<th> Name </th>");
        out.print("<th> Start time </th>");
        out.print("<th> End time </th>");
        out.print("<th> Location </th>");
        out.print("</tr>");
        out.print("</thead>");
        out.print("<tbody id=\"eventTableBody\">");
        if(eventList.size() == 0) {
            out.print("<tr>");
            out.print("<td colspan=\"6\"> There are no events for this period.</td>");
            out.print("</tr>");
        }
        if (eventList.size() > 0) {
            for (Event event : eventList) {
                out.print("<tr>");
                out.print("<td><input type=\"checkbox\" name=\"eventId\"value=\"" + event.getEventID() + "\"></td>");
                out.print("<td>" + event.getStartDate().getDayOfMonth() + " " + event.getStartDate().getMonth() + " " + event.getStartDate().getYear() + "</td>");
                out.print("<td>" + event.getName() + "</td>");
                out.print("<td>" + event.getStartDate().getHour() + ":" + event.getStartDate().getMinute() + "</td>");
                out.print("<td>" + event.getEndDate().getHour() + ":" + event.getEndDate().getMinute() + "</td>");
                out.print("<td>" + event.getLocation() + "</td>");
                out.print("</tr>");
            }
        }
        out.print("</tbody>");
        out.print("</table>");
        out.print("<button class=\"buttonStyle\" onclick=\"printAddEventDiv()\">Add an Event</button>");
        out.print("<button class=\"buttonStyle\" onclick=\"printEditEventDiv()\">Edit an Event</button>");
        out.print("<button class=\"buttonStyle\" onclick=\"deleteEvent()\">Delete an Event</button>");
    }

    static void printLocationOptionsFromDb (PrintWriter out) {
        Connection connection = null;
        Statement stmt = null;
        try {
            Driver dbDriver = new com.mysql.cj.jdbc.Driver();
            DriverManager.registerDriver(dbDriver);
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            String query ="SELECT * FROM `location_list`";
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int locId = rs.getInt("location_id");
                String location = rs.getString("location");
                out.print("\"<option value=\"" + locId + "\">" + location + "</option>\"");
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

}
