import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.http.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class seleccion_partidas extends HttpServlet {
  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();

    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            

    try {
      connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/trabajo_stgi", "root", "");
      statement = connection.createStatement();
      HttpSession session = request.getSession();
      int idusuario = (int) session.getAttribute("idusuario");
      String nombreusuario = (String) session.getAttribute("nombreusuario");
      session.setAttribute("idusuario", idusuario);
     
      resultSet = statement.executeQuery("SELECT p.idpartida, p.turno, p.estado, " +
      "(SELECT COUNT(*) FROM trabajo_stgi.detalles_partida dp " +
      "WHERE dp.idpartida = p.idpartida AND dp.idusuario = " + idusuario + ") AS Partida " +
      "FROM trabajo_stgi.partida p");

      out.println("<table border='1'>");
      out.println("<tr>");
      out.println("<th>N&ordm; Partida</th>");
      out.println("<th>Turno</th>");
      out.println("<th>Estado</th>");
      out.println("<th>Accion</th>");
      out.println("<th>Partida</th>");
      out.println("</tr>");
      
      while (resultSet.next()) {
        int idPartida = resultSet.getInt("idpartida");
        String turno = resultSet.getString("turno");
        String estado = resultSet.getString("estado");
        int columnaPartida = resultSet.getInt("Partida");
        String accionJoin = "";
        String accionPartida = "";
        
        // Comprobar si el usuario actual puede unirse a la partida
        if (!turno.equals(nombreusuario) && estado.equals("Pendiente")) {
            accionJoin = "<form action='/trabajo-stgi/unirse_partida' method='post'>" +
                   "<input type='hidden' name='idpartida' value='" + idPartida + "'>" +
                   "<input type='submit' value='Join'>" +
                   "</form>";
        }
        
        if (columnaPartida == 1 && turno.equals(nombreusuario) && estado.equals("En curso")) {
            accionPartida = "<form action='/trabajo-stgi/tablero' method='post'>" +
                     "<input type='hidden' name='idpartida' value='" + idPartida + "'>" +
                     "<input type='submit' value='Join'>" +
                     "</form>";
          }
        
        out.println("<tr>");
        out.println("<td>" + idPartida + "</td>");
        out.println("<td>" + turno + "</td>");
        out.println("<td>" + estado + "</td>");
        out.println("<td>" + accionJoin + "</td>");
        out.println("<td>" + accionPartida  + "</td>");
        out.println("</tr>");
      }
    out.println("</table>");
    
    out.println("<form action='/trabajo-stgi/anyadir_partidas' method='post'>");
    out.println("<input type='submit' value='Nueva Partida'>");
    out.println("</form>");

    out.println("<form action='/trabajo-stgi/logout' method='get'>");
    out.println("<input type='submit' value='Logout'>");
    out.println("</form>"); 

    out.println("<form action='/trabajo-stgi/perfil' method='get'>");
    out.println("<input type='submit' value='Mi perfil'>");
    out.println("</form>");
    
} catch (SQLException e) {
    e.printStackTrace();
}
}
}
