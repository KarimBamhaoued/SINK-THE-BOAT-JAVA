import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

public class logout extends HttpServlet {
    private static final long serialVersionUID = 1L;
  
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
      HttpSession session = request.getSession();
      //Esto se supone que invalida las variables de session
      session.invalidate();
      response.sendRedirect("/trabajo-stgi/home.html");
    }
  }