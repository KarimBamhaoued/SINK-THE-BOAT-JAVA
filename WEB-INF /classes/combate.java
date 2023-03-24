import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class combate extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public combate() {
        super();
        try {
            
            Class.forName("com.mysql.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/trabajo_stgi";
            String user = "root";
            String password = "";
            connection = DriverManager.getConnection(url, user, password);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer idUsuario = (Integer) session.getAttribute("idusuario");
        Integer idPartida = Integer.parseInt(request.getParameter("idpartida"));

        
        String tableroOponente = "";
        try {
            tableroOponente = obtenerTableroOponente(idPartida, idUsuario);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        //Tablero oponente HTML
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><head><title>Hundir la Flota</title>");
        out.println("<style>");
        out.println("table { margin: auto; }");
        out.println("td { border: 1px solid black; width: 50px; height: 50px; text-align: center; }");
        out.println("</style>");
        out.println("</head><body>");
        out.println("<div style='text-align:center'>");
        out.println("<h1>Hundir la Flota</h1>");
        out.println("<p class='center'>Tablero del oponente:</p>");
        out.println("</div>");
        out.println("<form action='/trabajo-stgi/ataque' method='post'>");
        out.println("<input type='hidden' name='idpartida' value='" + idPartida + "'>");
        out.println("<table style='border-collapse: collapse;'>");
        for (int i = 0; i < 8; i++) {
            out.println("<tr>");
            for (int j = 0; j < 8; j++) {
                int index = i * 8 + j;
                char c = tableroOponente.charAt(index);
                if (c == '0') {
                    out.println("<td style='background-color: white;'><button type='submit' name='posicion' value='" + i + "," + j + "'>X</button></td>");
                } else if (c == '9') {
                    out.println("<td style='background-color: #ADD8E6;'></td>");
                } else if (c == '8') {
                    out.println("<td style='background-color: red;'></td>");
                } else {
                    out.println("<td style='background-color: white;'><button type='submit' name='posicion' value='" + i + "," + j + "'>X</button></td>");
                }
            }
            out.println("</tr>");
        }
        
        out.println("</table>");
        out.println("</form>");
        out.println("</body></html>");
    }
    private String obtenerTableroOponente(int idPartida, int idUsuario) throws SQLException {
    String tablero = "";
    PreparedStatement ps = connection.prepareStatement("SELECT tablero FROM detalles_partida WHERE idpartida = ? AND idusuario != ?");
    ps.setInt(1, idPartida);
    ps.setInt(2, idUsuario);
    ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            tablero = rs.getString("tablero");
        }
            return tablero;
    }
}