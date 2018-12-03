import org.junit.*;

import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UnitTestSuite {

    private static ArrayList<Event> testList;
    private Event validEvent = new Event(10,10,2229, "TestEvent", 4, "09:00", "11:50");
    private Event editedEvent = new Event (10,10,2229,"EditedTestEvent", 2,"09:00", "11:50");
    private final String validDateByMonth = "2229-10";
    private final String validDateByYear ="2229-12";

    private void retrieveEventList() {
        EventHolder.generateQueryToPrintEventsForASpecificDay("2229-10-10");
        testList = EventHolder.getEventList();
    }

    @Test
    public void canAddEvent() {
        EventHolder.addEventToDb(validEvent);
        retrieveEventList();
        testList = EventHolder.getEventList();
        for (Event event: testList) {
            assertEquals("TestEvent",event.getName());
            assertEquals("Skopje",event.getLocation());
        }
    }

    @Test
    public void canEditEvent() {
        retrieveEventList();
        boolean edited = false;
        for (Event event: testList) {
            EventHolder.editEventInDb(editedEvent,event.getEventID());
            edited = true;
        }
        retrieveEventList();
        for (Event event : testList) {
            assertEquals("EditedTestEvent", event.getName());
            assertEquals("Burgas", event.getLocation());
        }
        assertTrue(edited);
    }

    @Test
    public void canPrintLocationOptionsFromDB() throws IOException {
        StringWriter testWriter = new StringWriter();
        PrintWriter testPrintWriter = new PrintWriter(testWriter);

        EventHolder.printLocationOptionsFromDb(testPrintWriter);
        /* If new options are added to the location list they need to be added here ! */
        String testedOutput = testWriter.toString();
        assertEquals("\"<option value=\"" +1 +"\">Sofia</option>\"" +
                "\"<option value=\"" + 2 + "\">Burgas</option>\"" +
                "\"<option value=\"" + 3 +"\">Ruse</option>\"" +
                "\"<option value=\"" + 4 + "\">Skopje</option>\"",testedOutput);
        testPrintWriter.close();
        testWriter.close();
    }

    @Test
    public void canPrintEventListFromDB() throws IOException {
        StringWriter testStringWriter = new StringWriter();
        PrintWriter testPrintWriter = new PrintWriter(testStringWriter);
        retrieveEventList();
        EventHolder.printEventsFromList(testPrintWriter);
        String testedOutput = testStringWriter.toString();
        for(Event event : testList) {
            assertEquals("<table align=\"center\"><thead><tr><th> Select </th><th> Date </th><th> Name </th>" +
                    "<th> Start time </th><th> End time </th><th> Location </th></tr></thead><tbody id=\"eventTableBody\"><tr>" +
                    "<td><input type=\"checkbox\" name=\"eventId\" value=\"" + event.getEventID() + "\"></td>" +
                    "<td>10 OCTOBER 2229</td><td>EditedTestEvent</td><td>9:0</td><td>11:50</td><td>Burgas</td></tr></tbody>" +
                    "</table><button class=\"buttonStyle\" onclick=\"printAddEventDiv()\">Add an Event</button>" +
                    "<button class=\"buttonStyle\" onclick=\"printEditEventDiv()\">Edit an Event</button>" +
                    "<button class=\"buttonStyle\" onclick=\"removeEvent()\">Delete an Event</button>", testedOutput);
        }
        testPrintWriter.close();
        testStringWriter.close();
    }

    @Test
    public void ableToPrintEmptyEventListFromDB() throws IOException {
        StringWriter testStringWriter = new StringWriter();
        PrintWriter testPrintWriter = new PrintWriter(testStringWriter);
        EventHolder.printEventsFromList(testPrintWriter);
        String testedOutput = testStringWriter.toString();
        assertEquals("<table align=\"center\"><thead><tr><th> Select </th><th> Date </th><th> Name </th>" +
            "<th> Start time </th><th> End time </th><th> Location </th></tr></thead>" +
            "<tbody id=\"eventTableBody\"><tr><td colspan=\"6\"> There are no events for this period.</td></tr></tbody></table>" +
            "<button class=\"buttonStyle\" onclick=\"printAddEventDiv()\">Add an Event</button>" +
            "<button class=\"buttonStyle\" onclick=\"printEditEventDiv()\">Edit an Event</button>" +
            "<button class=\"buttonStyle\" onclick=\"removeEvent()\">Delete an Event</button>", testedOutput);
        testPrintWriter.close();
        testStringWriter.close();
    }

    @Test
    public void canRemoveEvent() {
        boolean removed = false;
        for (Event event: testList) {
            EventHolder.removeEvent(event.getEventID());
            removed = true;
        }
        retrieveEventList();
        assertTrue((testList.isEmpty() && removed));
    }

    @Test
    public void canIncrementDateByMonth() {
        String incrementedDate = EventHolder.handleDateIncrementation(validDateByMonth);
        assertEquals("2229-11",incrementedDate);
    }

    @Test
    public void canIncrementDateByMonthAndYear() {
        String incrementedDate = EventHolder.handleDateIncrementation(validDateByYear);
        assertEquals("2230-1",incrementedDate);
    }

    @Test
    public void canPrintAddEventWindow() throws Exception {
        StringWriter testStringWriter = new StringWriter();
        PrintWriter testPrintWriter = new PrintWriter(testStringWriter);
        AddEventServlet.printAddEventDiv(testPrintWriter);
        String testedOutput = testStringWriter.toString();
        /*This test needs to chance if more options are added. */
        assertEquals("<div class=\"eventContainer\"><div class=\"header\"><h3>Add an Event:</h3></div>" +
                "<form id=\"addEvent\" method=\"POST\" action=\"/AddEvent\">Date:<input type=\"text\" name=\"eventDate\" " +
                "placeholder=\"dd-mm-yyyy\" pattern=\"\\d{1,2}-\\d{1,2}-\\d{4}\" required><br>Name for event:<input type=\"text\" " +
                "name=\"eventName\" placeholder=\"Event Name\" required><br>Location: &nbsp;<select name=\"locationList\" " +
                "form=\"addEvent\"><br>\"<option value=\"1\">Sofia</option>\"\"<option value=\"2\">Burgas</option>\"\"" +
                "<option value=\"3\">Ruse</option>\"\"<option value=\"4\">Skopje</option>\"</select>" +
                "<br>Starting time:<input type=\"text\" name=\"eventStart\" placeholder=\"00:00\" " +
                "pattern=\"((0\\d)|(1\\d)|(2[0-3])):((0\\d)|([1-6]\\d))\" required><br>End time:<input type=\"text\" name=\"eventEnd\" " +
                "placeholder=\"23:59\" pattern=\"((0\\d)|(1\\d)|(2[0-3])):((0\\d)|([1-6]\\d))\" required><br><input class=\"buttonStyle\" " +
                "type=\"submit\" name=\"submitForm\" onclick=\"submitNewEvent(ev)\"><input class=\"buttonStyle\" type=\"reset\">" +
                "</form></div>", testedOutput);
        testPrintWriter.close();
        testStringWriter.close();
    }

    @Test
    public void canPrintEditEventWindow() throws Exception {
        StringWriter testStringWriter = new StringWriter();
        PrintWriter testPrintWriter = new PrintWriter(testStringWriter);
        EditEventServlet.printEditEventDiv(testPrintWriter);
        String testedOutput = testStringWriter.toString();
        /*This test needs to chance if more options are added. */
        assertEquals("<div class=\"eventContainer\"><div class=\"header\">" +
                "<h3>Edit Selected Event:</h3></div><form id=\"editEvent\" method=\"POST\" action=\"/EditEvent\">" +
                "<input type=\"hidden\" name=\"eventID\">New Date:" +
                "<input type=\"text\" name=\"eventDate\" placeholder=\"dd-mm-yyyy\" pattern=\"\\d{1,2}-\\d{1,2}-\\d{4}\" required>" +
                "<br>New Name for event:<input type=\"text\" name=\"eventName\" placeholder=\"Event Name\" required>" +
                "<br>New Location: &nbsp;<select name=\"locationList\" form=\"editEvent\" required>\"" +
                "<option value=\"1\">Sofia</option>\"\"<option value=\"2\">Burgas</option>\"\"<option value=\"3\">Ruse</option>\"\"" +
                "<option value=\"4\">Skopje</option>\"</select><br>New Starting time:" +
                "<input type=\"text\" name=\"eventStart\" placeholder=\"00:00\" pattern=\"((0\\d)|(1\\d)|(2[0-3])):((0\\d)|([1-6]\\d))\" required>" +
                "<br>New End time:<input type=\"text\" name=\"eventEnd\" placeholder=\"23:59\" pattern=\"((0\\d)|(1\\d)|(2[0-3])):((0\\d)|([1-6]\\d))\" required>" +
                "<br><input class=\"buttonStyle\" type=\"submit\" name=\"submitForm\"><input class=\"buttonStyle\" type=\"reset\"></form></div>", testedOutput);
        testPrintWriter.close();
        testStringWriter.close();
    }

}

