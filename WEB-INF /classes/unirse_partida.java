import java.io.*;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;




public class unirse_partida extends HttpServlet {
    private static final long serialVersionUID = 1L;
 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        
 
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/trabajo_stgi", "root", "");
            statement = connection.createStatement();
            HttpSession session = request.getSession();
            int idPartida = Integer.parseInt(request.getParameter("idpartida"));
            int idUsuario = (int) session.getAttribute("idusuario");  

            resultSet = statement.executeQuery("SELECT idusuario, estado FROM trabajo_stgi.partida WHERE idpartida = " + idPartida);
            resultSet.next();
            int idCreador = resultSet.getInt("idusuario");
            String estadoPartida = resultSet.getString("estado");
            if (idUsuario != idCreador && estadoPartida.equals("Pendiente")) {
                
            // Actualizar el estado de la partida y registrar la nueva entrada en la tabla "unirse_partida"
            statement.executeUpdate("UPDATE trabajo_stgi.partida SET estado = 'En curso' WHERE idpartida = " + idPartida);
            statement.executeUpdate("INSERT INTO trabajo_stgi.detalles_partida (idpartida, idusuario) VALUES (" + idPartida + ", " + idUsuario + ")");
            response.sendRedirect("/trabajo-stgi/seleccion_partidas");
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
}  

} 