import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

public class perfil extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        int idusuario = (int) session.getAttribute("idusuario");

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/trabajo_stgi", "root", "");

            //Obtenemos usr de trabajo_stgi.usuarios_login
            preparedStatement = connection.prepareStatement("SELECT usr FROM trabajo_stgi.usuarios_login WHERE idusuarios = ?");
            preparedStatement.setInt(1, idusuario);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String usr = resultSet.getString("usr");

                out.println("<h2>Mi perfil</h2>");
                out.println("<p>Usuario: " + usr + "</p>");

                //formulario para cambiar pass
                out.println("<h3>Cambiar la contrase&ntilde;a</h3>");
                out.println("<form method='post'>");
                out.println("<label for='password'>Nueva contrase&ntilde;a:</label>");
                out.println("<input type='password' id='password' name='password' required>");
                out.println("<input type='submit' value='Guardar'>");
                out.println("</form>");

                //Boton de volver
                out.println("<form action='/trabajo-stgi/seleccion_partidas' method='get'>");
                out.println("<input type='submit' value='Volver'>");
                out.println("</form>");

            } else {
                out.println("<p>Usuario no encontrado</p>");
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
    
        HttpSession session = request.getSession();
        int idusuario = (int) session.getAttribute("idusuario");
    
        Connection connection = null;
        PreparedStatement preparedStatement = null;
    
        String password = request.getParameter("password");
    
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/trabajo_stgi", "root", "");
    
            // Hash the new password using SHA-1
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                sb.append(Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1));
            }
            String hashedPassword = sb.toString();
    
            //Updatear contrasenya
            preparedStatement = connection.prepareStatement("UPDATE trabajo_stgi.usuarios_login SET password = ? WHERE idusuarios = ?");
            preparedStatement.setString(1, hashedPassword);
            preparedStatement.setInt(2, idusuario);
    
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                out.println("<p>Contrase&ntilde;a cambiada correctamente</p>");
                out.println("<a href='home.html'>Volver a la pagina de inicio</a>");
            } else {
                out.println("<p>Error al cambiar la contrase&ntilde;a</p>");
            }
    
        } catch (ClassNotFoundException | SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}