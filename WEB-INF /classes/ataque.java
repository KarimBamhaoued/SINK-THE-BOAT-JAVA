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

public class ataque extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public ataque() {
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
        String posicionString = request.getParameter("posicion");
        String[] posicionArray = posicionString.split(",");
        int fila = Integer.parseInt(posicionArray[0]);
        int columna = Integer.parseInt(posicionArray[1]);

        //Tablero oponente
        String tableroOponente = "";
        try {
            tableroOponente = obtenerTableroOponente(idPartida, idUsuario);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Actualizar el tablero del oponente
        int index = fila * 8 + columna;
        char c = tableroOponente.charAt(index);
        if (c == '0') {
            //falla = agua
            tableroOponente = tableroOponente.substring(0, index) + "9" + tableroOponente.substring(index + 1);
        } else if (c == '1' || c == '2' || c == '3' || c == '4' || c == '5') {
            //acierta = rojo
            tableroOponente = tableroOponente.substring(0, index) + "8" + tableroOponente.substring(index + 1);
        
            //Ha ganado?
            if (!tableroOponente.contains("1") && !tableroOponente.contains("2") && !tableroOponente.contains("3")
                    && !tableroOponente.contains("4") && !tableroOponente.contains("5")) {
                int idusuario = (Integer) session.getAttribute("idusuario");
                int idpartida = Integer.parseInt(request.getParameter("idpartida"));
                response.sendRedirect("ganador?idusuario=" + idusuario + "&idpartida=" + idpartida);
                return;
            }
        }

        //Actualizar el la bbdd con el tablero del oponente
        try {
            actualizarTableroOponente(idPartida, idUsuario, tableroOponente);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PrintWriter out = response.getWriter();
        out.println("<html><head><title>Hundir la Flota</title>");
        out.println("<style>");
        out.println("table { margin: auto; }");
        out.println("td { border: 1px solid black; width: 50px; height: 50px; text-align: center; }");
        out.println("</style>");
        out.println("</head><body>");
        out.println("<div style='text-align:center'>");
        out.println("<h1>Hundir la Flota</h1>");
        out.println("<p class='center'>Tablero del oponente actualizado:</p>");
        out.println("</div>");
        out.println("<table style='border-collapse: collapse;'>");

        //obtener el nuevo tablero del oponente
        try {
            tableroOponente = obtenerTableroOponente(idPartida, idUsuario);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 8; i++) {
            out.println("<tr>");
            for (int j = 0; j < 8; j++) {
                index = i * 8 + j;
                c = tableroOponente.charAt(index);
                    if (c == '0') {
                        out.println("<td style='background-color: white;'></td>");
                    } else if (c == '9') {
                        out.println("<td style='background-color: #ADD8E6;'></td>");
                    } else if (c == '8') {
                        out.println("<td style='background-color: red;'></td>");
                    } else {
                        out.println("<td style='background-color: white;'></td>");
                    }
            }
            out.println("</tr>");
        }

        out.println("</table>");
        out.println("<div style='text-align:center'>");
        out.println("<form action='cambiar_turno' method='post'>");
        out.println("<input type='hidden' name='idpartida' value='" + idPartida + "'>");
        out.println("<input type='submit' value='Cambiar de turno'>");
        out.println("</form>");
        out.println("</div>");
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

    private void actualizarTableroOponente(int idPartida, int idUsuario, String tableroOponente) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE detalles_partida SET tablero = ? WHERE idpartida = ? AND idusuario != ?");
        ps.setString(1, tableroOponente);
        ps.setInt(2, idPartida);
        ps.setInt(3, idUsuario);
        ps.executeUpdate();
    }
}