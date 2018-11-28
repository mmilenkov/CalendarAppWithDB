import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/EditEvent")
public class EditEventServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        String date = req.getParameter("eventDate");
        String[] sDate = date.split("-");
        Event event = new Event(Integer.valueOf(sDate[0]),Integer.valueOf(sDate[1]),Integer.valueOf(sDate[2]),req.getParameter("eventName"),req.getParameter("eventLocation"),req.getParameter("eventStart"),req.getParameter("eventEnd"));
        EventHolder.editEventInDb(event, Integer.valueOf(req.getParameter("eventID")));
        resp.sendRedirect("http://localhost:8080/");
    }
}
