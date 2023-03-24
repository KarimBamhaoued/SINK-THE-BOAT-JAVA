import java.io.IOException;
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

public class cambiar_turno extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private Connection connection = null;

    public cambiar_turno() {
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

        try {

            //obtener idusuario
            String sql = "SELECT idusuario FROM trabajo_stgi.detalles_partida WHERE idpartida = ? AND idusuario != ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, idPartida);
            statement.setInt(2, idUsuario);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int idOtroJugador = resultSet.getInt("idusuario");      

            //obtener usr
            sql = "SELECT usr FROM trabajo_stgi.usuarios_login WHERE idusuarios = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, idOtroJugador);
            resultSet = statement.executeQuery();
            resultSet.next();
            String nombreUsuario = resultSet.getString("usr");

            //update turno
            sql = "UPDATE trabajo_stgi.partida SET turno = ? WHERE idpartida = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, nombreUsuario);
            statement.setInt(2, idPartida);
            statement.executeUpdate();
            
            response.sendRedirect("seleccion_partidas");      
        }catch (SQLException e) {
            e.printStackTrace();
        }
}  

} 