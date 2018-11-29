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
        System.out.println(req.getParameter("eventDate"));
        String[] sDate = date.split("-");
        Event event = new Event(Integer.valueOf(sDate[0]),Integer.valueOf(sDate[1]),Integer.valueOf(sDate[2]),req.getParameter("eventName"),Integer.valueOf(req.getParameter("locationList")),req.getParameter("eventStart"),req.getParameter("eventEnd"));
        EventHolder.addEventToDB(event);
        resp.sendRedirect("http://localhost:8080/");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.print("<div class=\"eventContainer\">");
        out.print("<div class=\"header\">");
        out.print("<h3>Add an Event:</h3>");
        out.print("</div>");
        out.print("<form id=\"addEvent\" method=\"POST\" action=\"/AddEvent\">");
        out.print("Date:");
        out.print("<input type=\"text\" name=\"eventDate\" placeholder=\"dd-mm-yyyy\" pattern=\"\\d{1,2}-\\d{1,2}-\\d{4}\" required><br>");
        out.print("Name for event:");
        out.print("<input type=\"text\" name=\"eventName\" placeholder=\"Event Name\" required><br>");
        out.print("Location: &nbsp;");
        out.print("<select name=\"locationList\" form=\"addEvent\"><br>");
        EventHolder.printLocationOptionsFromDb(out);
        out.print("</select><br>");
        out.print("Starting time:");
        out.print("<input type=\"text\" name=\"eventStart\" placeholder=\"00:00\" pattern=\"((0\\d)|(1\\d)|(2[0-3])):((0\\d)|([1-6]\\d))\" required><br>");
        out.print("End time:");
        out.print("<input type=\"text\" name=\"eventEnd\" placeholder=\"23:59\" pattern=\"((0\\d)|(1\\d)|(2[0-3])):((0\\d)|([1-6]\\d))\" required><br>");
        out.print("<input class=\"buttonStyle\" type=\"submit\" name=\"submitForm\" onclick=\"submitNewEvent(ev)\">");
        out.print("<input class=\"buttonStyle\" type=\"reset\">");
        out.print("</form>");
        out.print("</div>");
        out.close();

    }
}
