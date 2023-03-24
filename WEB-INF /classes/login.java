import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class login extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        
        PrintWriter pw = res.getWriter();
        res.setContentType("text/html");

        String usr = req.getParameter("usr");
        String password = req.getParameter("password");
        String accion = req.getParameter("accion");

        //Codificar la contraseña utilizando SHA-1
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                sb.append(Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1));
            }
            password = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try( Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/trabajo_stgi","root","");

            PreparedStatement ps = con.prepareStatement("SELECT usr, password FROM trabajo_stgi.usuarios_login WHERE usr = ? and password = ?")){
            ps.setString(1, usr);
            ps.setString(2, password);


            ResultSet rs = ps.executeQuery();
            
            if (accion.equals("register")) {
                //Registro
                if (rs.next()) {
                    pw.println("<h2>El usuario ya existe</h2>");
                } else {
                    try (PreparedStatement ps2 = con.prepareStatement("INSERT INTO trabajo_stgi.usuarios_login(usr,password) VALUES(?,?)")) {
                        ps2.setString(1, usr);
                        ps2.setString(2, password);
                        int i = ps2.executeUpdate();
                        if (i > 0) {
                            pw.println("<h2>Registrado correctamente</h2>");
                            pw.println("<a href='home.html'>Volver a la pagina de inicio de sesion</a>");
                        }
                    }
                }
            } else if (accion.equals("login")) {
                //Login
                if (rs.next()) {
                    //pw.println("<h2>Bienvenido " + usr + "</h2>"); //Falta añadir a que pagina html redirigira, en concreto a la del juego
                    HttpSession session = req.getSession();
                    int idUsuario = 0;
                    try (PreparedStatement ps3 = con.prepareStatement("SELECT idusuarios FROM trabajo_stgi.usuarios_login WHERE usr = ?")) {
                        ps3.setString(1, usr);
                        ResultSet rs2 = ps3.executeQuery();
                        if (rs2.next()) {
                            idUsuario = rs2.getInt("idusuarios");
                        }
                        session.setAttribute("idusuario", idUsuario);
                        session.setAttribute("nombreusuario", usr);
                    }
                    res.sendRedirect("/trabajo-stgi/seleccion_partidas");
                    
                } else {
                    pw.println("<h2>Usuario o contrasenya incorrecta</h2>");
                    pw.println("<a href='home.html'>Volver</a>");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}