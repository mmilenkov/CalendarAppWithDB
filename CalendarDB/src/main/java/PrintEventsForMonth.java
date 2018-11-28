import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/PrintMonth")
public class PrintEventsForMonth extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String date = req.getParameter("date");
        PrintWriter out = resp.getWriter();
        out.print("<h3>List of Events for this month:</h3>");
       EventHolder.generateQueryToPrintEventsForASpecificMonth(out,date);
    }

}
