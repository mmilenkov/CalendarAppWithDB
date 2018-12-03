import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/DeleteEvent")
public class DeleteEventServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        String eventID = req.getParameter("eventID");
        String [] eventIDArray = eventID.split("-");
        for(String id : eventIDArray) {
            EventHolder.removeEvent(Integer.valueOf(id));
        }
    }
}
