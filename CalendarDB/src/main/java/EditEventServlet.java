import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/EditEvent")
public class EditEventServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        String date = req.getParameter("eventDate");
        String[] sDate = date.split("-");
        String locationID = req.getParameter("locationList") ;
        Event event = new Event(Integer.valueOf(sDate[0]),Integer.valueOf(sDate[1]),Integer.valueOf(sDate[2]),req.getParameter("eventName"),Integer.valueOf(locationID),req.getParameter("eventStart"),req.getParameter("eventEnd"));
        EventHolder.editEventInDb(event, Integer.valueOf(req.getParameter("eventID")));
        resp.sendRedirect("http://localhost:8080/");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.print("<div class=\"eventContainer\">");
        out.print("<h3>Edit Selected Event:</h3>");
        out.print("<form id=\"editEvent\" method=\"POST\" action=\"/EditEvent\">");
        out.print("<input type=\"hidden\" name=\"eventID\">");
        out.print("New Date:");
        out.print("<input type=\"text\" name=\"eventDate\" placeholder=\"dd-mm-yyyy\" pattern=\"\\d{1,2}-\\d{1,2}-\\d{4}\" required><br>");
        out.print("New Name for event:");
        out.print("<input type=\"text\" name=\"eventName\" placeholder=\"Event Name\" required><br>");
        out.print("New Location: &nbsp;");
        out.print("<select name=\"locationList\" form=\"editEvent\" required>");
        EventHolder.printLocationOptionsFromDb(out);
        out.print("</select><br>");
        out.print("New Starting time:");
        out.print("<input type=\"text\" name=\"eventStart\" placeholder=\"00:00\" pattern=\"((0\\d)|(1\\d)|(2[0-3])):((0\\d)|([1-6]\\d))\" required><br>");
        out.print("New End time:");
        out.print("<input type=\"text\" name=\"eventEnd\" placeholder=\"23:59\" pattern=\"((0\\d)|(1\\d)|(2[0-3])):((0\\d)|([1-6]\\d))\" required><br>");
        out.print("<input class=\"buttonStyle\" type=\"submit\" name=\"submitForm\">");
        out.print("<input class=\"buttonStyle\" type=\"reset\">");
        out.print("</form>");
        out.print("</div>");
        out.close();
    }
}
