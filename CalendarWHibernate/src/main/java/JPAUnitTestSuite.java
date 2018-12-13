import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JPAUnitTestSuite {

    private List<Event> testList;
    private int month = 10;
    private int day = 10;
    private int year = 2229;
    private Event validEvent = new Event(day,month-1,year, "TestEvent", 4, "09:00", "11:50");
    private Event editedEvent = new Event (day,month-1,year,"EditedTestEvent", 2,"09:00", "11:50");

    private void retrieveEventList() {
        EventHolderJPA.generateSearchPrintDay("2229-10-10");
        testList = EventHolderJPA.getEventList();
    }

    @Test
    public void canAddEvent() {
        EventHolderJPA.addEvent(validEvent);
        retrieveEventList();
        testList = EventHolderJPA.getEventList();
        for (Event event: testList) {
            assertEquals("TestEvent",event.getName());
            assertEquals(4,event.getLocationId());
        }
    }

    @Test
    public void canEditEvent() {
        retrieveEventList();
        boolean edited = false;
        for (Event event: testList) {
            EventHolderJPA.editEvent(editedEvent,event.getEventID());
            edited = true;
        }
        retrieveEventList();
        for (Event event : testList) {
            assertEquals("EditedTestEvent", event.getName());
            assertEquals(2, event.getLocationId());
        }
        assertTrue(edited);
    }

    @Test
    public void canRemoveEvent() {
        boolean removed = false;
        retrieveEventList();
        for (Event event: testList) {
            EventHolderJPA.removeEvent(event.getEventID());
            removed = true;
        }
        retrieveEventList();
        assertTrue((testList.isEmpty() && removed));
    }

    @Test
    public void canPrintEventListFromDB() throws IOException {
        StringWriter testStringWriter = new StringWriter();
        PrintWriter testPrintWriter = new PrintWriter(testStringWriter);
        retrieveEventList();
        EventHolderJPA.printEventList(testPrintWriter);
        String testedOutput = testStringWriter.toString();
        for(Event event : testList) {
            assertEquals("<table align=\"center\"><thead><tr><th> Select </th><th> Date </th><th> Name </th>" +
                    "<th> Start time </th><th> End time </th><th> Location </th></tr></thead><tbody id=\"eventTableBody\"><tr>" +
                    "<td><input type=\"checkbox\" name=\"eventId\" value=\"" + event.getEventID() + "\"></td>" +
                    "<td>10 10 2229</td><td>EditedTestEvent</td><td>9:0</td><td>11:50</td><td>Burgas</td></tr></tbody>" +
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
        EventHolderJPA.printEventList(testPrintWriter);
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
    public void canIncrementDateByMonth() {
        Date incrementedDate = EventHolderJPA.generateSecondaryDate(year,month);
        Date expected = new Date(year-1900,10,1,23,59);
        assertEquals(expected,incrementedDate);
    }

    @Test
    public void canIncrementDateByMonthAndYear() {
        Date incrementedDate = EventHolderJPA.generateSecondaryDate(2229,12);
        Date expected = new Date(year-1899,0,1,23,59);
        assertEquals(expected,incrementedDate);
    }

    @Test
    public void canPrintLocationOptionsFromDB() throws IOException {
        StringWriter testWriter = new StringWriter();
        PrintWriter testPrintWriter = new PrintWriter(testWriter);

        EventHolder.printLocationOptionsFromDb(testPrintWriter);
        /* If new options are added to the location list they need to be added here ! */
        String testedOutput = testWriter.toString();
        assertEquals("\"<option value=\"1\">Sofia</option>\"" +
                "\"<option value=\"2\">Burgas</option>\"" +
                "\"<option value=\"3\">Ruse</option>\"" +
                "\"<option value=\"4\">Skopje</option>\"",testedOutput);
        testPrintWriter.close();
        testWriter.close();
    }

    @Test
    public void canPrintAddEventWindow() throws Exception {
        StringWriter testStringWriter = new StringWriter();
        PrintWriter testPrintWriter = new PrintWriter(testStringWriter);
        AddEventServlet.printAddEventDiv(testPrintWriter);
        String testedOutput = testStringWriter.toString();
        /*This test needs to change if more options are added. */
        assertEquals("<div class=\"eventContainer\"><div class=\"header\"><h3>Add an Event:</h3></div>" +
                "<form id=\"addEvent\" method=\"POST\" action=\"/AddEvent\">Date:<input type=\"text\" name=\"eventDate\" " +
                "placeholder=\"dd-mm-yyyy\" pattern=\"\\d{1,2}-\\d{1,2}-\\d{4}\" required><br>Name for event:<input type=\"text\" " +
                "name=\"eventName\" placeholder=\"Event Name\" required><br>Location: &nbsp;<select name=\"locationList\" " +
                "form=\"addEvent\"><br>\"" +

                "<option value=\"1\">Sofia</option>\"\"<option value=\"2\">Burgas</option>\"\"" +
                "<option value=\"3\">Ruse</option>\"\"<option value=\"4\">Skopje</option>\"" +

                "</select><br>Starting time:<input type=\"text\" name=\"eventStart\" placeholder=\"00:00\" " +
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
        /*This test needs to change if more options are added. */
        assertEquals("<div class=\"eventContainer\"><div class=\"header\">" +
                "<h3>Edit Selected Event:</h3></div><form id=\"editEvent\" method=\"POST\" action=\"/EditEvent\">" +
                "<input type=\"hidden\" name=\"eventID\">New Date:" +
                "<input type=\"text\" name=\"eventDate\" placeholder=\"dd-mm-yyyy\" pattern=\"\\d{1,2}-\\d{1,2}-\\d{4}\" required>" +
                "<br>New Name for event:<input type=\"text\" name=\"eventName\" placeholder=\"Event Name\" required>" +
                "<br>New Location: &nbsp;<select name=\"locationList\" form=\"editEvent\" required>\"" +

                "<option value=\"1\">Sofia</option>\"\"<option value=\"2\">Burgas</option>\"\"<option value=\"3\">Ruse</option>\"\"" +
                "<option value=\"4\">Skopje</option>\"" +

                "</select><br>New Starting time:" +
                "<input type=\"text\" name=\"eventStart\" placeholder=\"00:00\" pattern=\"((0\\d)|(1\\d)|(2[0-3])):((0\\d)|([1-6]\\d))\" required>" +
                "<br>New End time:<input type=\"text\" name=\"eventEnd\" placeholder=\"23:59\" pattern=\"((0\\d)|(1\\d)|(2[0-3])):((0\\d)|([1-6]\\d))\" required>" +
                "<br><input class=\"buttonStyle\" type=\"submit\" name=\"submitForm\"><input class=\"buttonStyle\" type=\"reset\"></form></div>", testedOutput);
        testPrintWriter.close();
        testStringWriter.close();
    }
}
