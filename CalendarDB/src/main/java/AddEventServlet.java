import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/AddEvent")
public class AddEventServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        String date = req.getParameter("eventDate");
        String[] sDate = date.split("-");
        Event event = new Event(Integer.valueOf(sDate[0]),Integer.valueOf(sDate[1]),Integer.valueOf(sDate[2]),
                req.getParameter("eventName"),Integer.valueOf(req.getParameter("locationList")),
                req.getParameter("eventStart"),req.getParameter("eventEnd"));
        EventHolder.addEventToDb(event);
        resp.sendRedirect("http://localhost:8080/");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        printAddEventDiv(out);
        out.close();
    }

    static void printAddEventDiv(PrintWriter out) {
        out.print(
                "<div class=\"eventContainer\">"
                + "<div class=\"header\">"
                + "<h3>Add an Event:</h3>"
                + "</div>"
                + "<form id=\"addEvent\" method=\"POST\" action=\"/AddEvent\">"
                + "Date:"
                + "<input type=\"text\" name=\"eventDate\" placeholder=\"dd-mm-yyyy\" "
                + "pattern=\"\\d{1,2}-\\d{1,2}-\\d{4}\" required><br>"
                + "Name for event:"
                + "<input type=\"text\" name=\"eventName\" placeholder=\"Event Name\" required><br>"
                + "Location: &nbsp;"
                + "<select name=\"locationList\" form=\"addEvent\"><br>"
        );
        EventHolder.printLocationOptionsFromDb(out);
        out.print(
                "</select><br>"
                + "Starting time:"
                + "<input type=\"text\" name=\"eventStart\" placeholder=\"00:00\" "
                + "pattern=\"((0\\d)|(1\\d)|(2[0-3])):((0\\d)|([1-6]\\d))\" required><br>"
                + "End time:"
                + "<input type=\"text\" name=\"eventEnd\" placeholder=\"23:59\" "
                + "pattern=\"((0\\d)|(1\\d)|(2[0-3])):((0\\d)|([1-6]\\d))\" required><br>"
                + "<input class=\"buttonStyle\" type=\"submit\" name=\"submitForm\" onclick=\"submitNewEvent(ev)\">"
                + "<input class=\"buttonStyle\" type=\"reset\">"
                + "</form>"
                + "</div>"
        );
    }

}
