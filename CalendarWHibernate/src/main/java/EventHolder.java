import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

@SuppressWarnings("Duplicates") //Duplicates when closing the connections to the db
class EventHolder {

    private static ArrayList<Event> eventList = new ArrayList<Event>();
    private final static String dbUrl = "jdbc:mysql://localhost:3306/event_list?useSSL=false";
    private final static String dbUser = "root";
    private final static String dbPass = "admin15";

    static ArrayList<Event> getEventList() {
        return eventList;
    } /*Only needed for testing */

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
                Date startDate = rs.getTimestamp("start_date");
                Date endDate = rs.getTimestamp("end_date");
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
                if (stmt != null) {
                    stmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    static void addEventToDb(Event event) {
        Connection connection = null;
        Statement stmt = null;
        try {
            Driver dbDriver = new com.mysql.cj.jdbc.Driver();
            DriverManager.registerDriver(dbDriver);
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startDate = sdf.format(event.getStartDate());
            String endDate = sdf.format(event.getEndDate());
            String query = "INSERT INTO event(`start_date`,`end_date`, `name`, `location_id`) VALUES ('"
                    + startDate +"','" + endDate + "','" + event.getName() + "','"
                    + event.getLocationId() + "')";
            stmt = connection.createStatement();
            stmt.executeUpdate(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startDate = sdf.format(event.getStartDate());
            String endDate = sdf.format(event.getEndDate());
            String query = "UPDATE `event` SET `start_date`='" + startDate + "',`end_date`='"
                    + endDate + "',`name`='"+ event.getName() +"', `location_id`='"
                    + event.getLocationId() + "' WHERE `event_id`=" + eventID;
            stmt.executeUpdate(query);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    static void removeEvent(int eventID) {
        Connection connection = null;
        Statement stmt = null;
        try {
            Driver dbDriver = new com.mysql.cj.jdbc.Driver();
            DriverManager.registerDriver(dbDriver);
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            String query = "DELETE FROM `event` WHERE event_id=" + eventID;
            stmt= connection.createStatement();
            stmt.executeUpdate(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
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
                if (stmt != null) {
                    stmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    static void generateQueryToPrintAllEvents() {
        String query = "SELECT `event`.`event_id`,`event`.`start_date`,`event`.`end_date`,`event`.`name`,`location_list`.`location` " +
                "FROM `event` LEFT JOIN `location_list` ON `event`.`location_id`=`location_list`.`location_id` " +
                "ORDER BY `event`.`start_date`";
        retrieveEvents(query);
    }
    /* Move these to servlets? */
    static void generateQueryToPrintEventsForASpecificMonth(String date) {
        String selectedPeriodStart = "'" + date + "-01'";
        String selectedPeriodEnd = "'" + handleDateIncrementation(date) + "-01'";
        String query = "SELECT `event`.`event_id`,`event`.`start_date`,`event`.`end_date`,`event`.`name`,`location_list`.`location`" +
                " FROM `event` LEFT JOIN `location_list` ON `event`.`location_id`=`location_list`.`location_id` " +
                "WHERE `event`.`start_date` BETWEEN " + selectedPeriodStart + "AND " +selectedPeriodEnd;
        retrieveEvents(query);
    }
    /* Move these to servlets? */
    static void generateQueryToPrintEventsForASpecificDay(String date) {
        String query = "SELECT `event`.`event_id`,`event`.`start_date`,`event`.`end_date`,`event`.`name`,`location_list`.`location`" +
                " FROM `event` LEFT JOIN `location_list` ON `event`.`location_id`=`location_list`.`location_id` " +
                "WHERE `event`.`start_date` BETWEEN '" + date + " 00:00:00' AND '" + date + " 23:59:59'";
        retrieveEvents(query);
    }

    static String handleDateIncrementation(String date) {
        String[] slice = date.split("-");
        int year = Integer.valueOf(slice[0]);
        int month = Integer.valueOf(slice[1]);
        if(month == 12) {
            year += 1;
        }
        if(month == 12) {
            month = 1;
        }
        else {
            month += 1;
        }
        return (year + "-" + month);
    }

    static void printEventsFromList(PrintWriter out) {
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
                                + "<td>" + event.getLocation() + "</td>"
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
