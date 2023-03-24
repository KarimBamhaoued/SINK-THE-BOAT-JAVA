import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ganador extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {

        String idpartida = request.getParameter("idpartida");
        int idusuarios = Integer.parseInt(request.getParameter("idusuario"));

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        String url = "jdbc:mysql://localhost:3306/trabajo_stgi";
        String user = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement selectStatement = connection.prepareStatement("SELECT usr FROM trabajo_stgi.usuarios_login WHERE idusuarios = ?");
            PreparedStatement statement = connection.prepareStatement("UPDATE trabajo_stgi.partida SET estado = 'finalizado' WHERE idpartida = ?")) {

            statement.setString(1, idpartida);
            statement.executeUpdate();

            selectStatement.setInt(1, idusuarios);
            ResultSet rs = selectStatement.executeQuery();


            String username = "";
            if (rs.next()) {
                username = rs.getString("usr");
            }


            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h1>El ganador es: " + username + "</h1>");
            out.println("<form action=\"seleccion_partidas\" method=\"GET\">");
            out.println("<input type=\"submit\" value=\"Volver a seleccionar partida\">");
            out.println("</form>");
            out.println("</body></html>");

        } catch (SQLException e) {

            e.printStackTrace();
            throw new ServletException("Unable to access database.", e);
        }
    }
}
