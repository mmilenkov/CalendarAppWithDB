import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/PrintAll")
public class PrintAllEvents extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        out.print("<h3 class=\"header\">List of all events:</h3>");
        if(AbstractionChoice.DB_SAVE_METHOD.equals("jdbc")) {
            EventHolder.generateQueryToPrintAllEvents();
            EventHolder.printEventsFromList(out);
        }
        else {
            EventHolderJPA.generateSearchPrintAllEvents();
            EventHolderJPA.printEventList(out);
        }
        out.close();
    }
}
