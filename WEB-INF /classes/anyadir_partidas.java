import java.io.IOException;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class anyadir_partidas extends HttpServlet {
  private static final long serialVersionUID = 1L;

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    HttpSession session = request.getSession();
    Integer idUsuario = (Integer) session.getAttribute("idusuario");
    String estado = "Pendiente";

    Connection con = null;
    PreparedStatement ps = null;
    String usr = null;


    try {
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/trabajo_stgi", "root", "");
  
        //Obtenemos usr
        String sql = "SELECT usr FROM trabajo_stgi.usuarios_login WHERE idusuarios = ?";
        ps = con.prepareStatement(sql);
        ps.setInt(1, idUsuario);
        ResultSet rs = ps.executeQuery();
  
        if (rs.next()) {
          usr = rs.getString("usr");
        }
  
        //Insertar en trabajo_stgi.partida
        sql = "INSERT INTO trabajo_stgi.partida (idusuario, estado, turno) VALUES (?, ?, ?)";
        ps = con.prepareStatement(sql);
        ps.setInt(1, idUsuario);
        ps.setString(2, estado);
        ps.setString(3, usr);
        ps.executeUpdate();

        //Obtenemos el id
        sql = "SELECT MAX(idpartida) AS last_id FROM trabajo_stgi.partida WHERE idusuario = ?";
        ps = con.prepareStatement(sql);
        ps.setInt(1, idUsuario);
        rs = ps.executeQuery();
        int UltId = 0;
        if (rs.next()) {
          UltId = rs.getInt("last_id");
        }

        //insertamos en trabajo_stgi.detalles_partida
        sql = "INSERT INTO trabajo_stgi.detalles_partida (idpartida, idusuario) VALUES (?, ?)";
        ps = con.prepareStatement(sql);
        ps.setInt(1, UltId);
        ps.setInt(2, idUsuario);
        ps.executeUpdate();
  
        response.sendRedirect("seleccion_partidas");

      } catch (SQLException e) {
        e.printStackTrace();
  }
}
}