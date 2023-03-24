import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class tablero extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final int[][] BARCOS = {
        {2, 2},
        {3, 3},
        {3, 3},
        {4, 4},
        {5, 5}
    };
    //private static final int[][] DIRECCIONES = {
     //   {0, 1}, // Derecha
     //   {1, 0}  // Abajo
    //};

    private Connection connection = null;

    public tablero() {
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

        ArrayList<ArrayList<Integer>> tablero = cargarTablero(idPartida, idUsuario);   
        PrintWriter out = response.getWriter();
 

        response.setContentType("text/html");
        
        out.println("<html><head><title>Hundir la Flota</title></head><body>");
        out.println("<div style='text-align:center'>");
        out.println("<h1>Hundir la Flota</h1>");
        out.println("</div>");

        if (tablero == null) {
            //Nuevo tablero aleatorio
            tablero = generarTableroAleatorio();
            guardarTablero(idPartida, idUsuario, tablero);
        }else {
            idPartida = Integer.parseInt(request.getParameter("idpartida"));
            out.println("<div style='text-align:center; margin-top: 50px;'>");
            out.println("<form action='combate' method='post'>");
            out.println("<input type='hidden' name='idpartida' value='" + idPartida + "'>");
            out.println("<input type='submit' value='Iniciar combate' style='font-size: 20px; padding: 10px;'>");
            out.println("</form>");
            out.println("</div>");
        }
        out.println("<p>El estado de tus barcos</p>");
        out.println("<table style='border-collapse: collapse;'>");
        for (int i = 0; i < tablero.size(); i++) {
            out.println("<tr>");
            for (int j = 0; j < tablero.get(i).size(); j++) {
                if (tablero.get(i).get(j) == 0) {
                    out.println("<td style='border: 1px solid black; width: 50px; height: 50px;'></td>");
                } 
                else if (tablero.get(i).get(j) == 9) {
                    out.println("<td style='border: 1px solid black; width: 50px; height: 50px; background-color: #ADD8E6;'></td>");
                }else if (tablero.get(i).get(j) == 8) {
                    out.println("<td style='border: 1px solid black; width: 50px; height: 50px; background-color: red;'></td>");
                }else {
                    out.println("<td style='border: 1px solid black; width: 50px; height: 50px; background-color: green;'></td>");
                }
            }
            out.println("</tr>");
        }
        out.println("</table>");

        out.println("<form action='cambiar_turno' method='post'>");
        out.println("<input type='hidden' name='idpartida' value='" + idPartida + "'>");
        out.println("<input type='submit' value='Cambiar de turno'>");
        out.println("</form>");
        out.println("</body></html>");

    }

    private ArrayList<ArrayList<Integer>> generarTableroAleatorio() {
        ArrayList<ArrayList<Integer>> tablero = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            ArrayList<Integer> fila = new ArrayList<>();
            for (int j = 0; j < 8; j++) {
                fila.add(0);
            }
            tablero.add(fila);
        }

        // Colocar los barcos aleatoriamente en el tablero
        for (int[] barco : BARCOS) {
            int barcoId = barco[0];
            int barcoTam = barco[1];
            boolean colocado = false;
            while (!colocado) {
            // Elegir una posición aleatoria para el barco
            int fila = (int) (Math.random() * 8);
            int columna = (int) (Math.random() * 8);
            int direccion = (int) (Math.random() * 2);
                // Comprobar si el barco cabe en esa posición
                if (direccion == 0) { // Horizontal
                    if (columna + barcoTam > 8) {
                        continue;
                    }
                    boolean libre = true;
                    for (int i = columna; i < columna + barcoTam; i++) {
                        if (tablero.get(fila).get(i) != 0) {
                            libre = false;
                            break;
                        }
                    }
                    if (libre) {
                        for (int i = columna; i < columna + barcoTam; i++) {
                            tablero.get(fila).set(i, barcoId);
                        }
                        colocado = true;
                    }
                } else { // Vertical
                    if (fila + barcoTam > 8) {
                        continue;
                    }
                    boolean libre = true;
                    for (int i = fila; i < fila + barcoTam; i++) {
                        if (tablero.get(i).get(columna) != 0) {
                            libre = false;
                            break;
                        }
                    }
                    if (libre) {
                        for (int i = fila; i < fila + barcoTam; i++) {
                            tablero.get(i).set(columna, barcoId);
                        }
                        colocado = true;
                    }
                }
            }
        }
        return tablero;
    }
    
    
    private void guardarTablero(int idPartida, int idUsuario, ArrayList<ArrayList<Integer>> tablero) {
        try {
            //Eliminar cualquier partida anterior de ese usuario e idpartida
            String sql = "DELETE FROM trabajo_stgi.detalles_partida WHERE idpartida = ? AND idusuario = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, idPartida);
            statement.setInt(2, idUsuario);
            statement.executeUpdate();
    
            //Guardar la configuración actual
            sql = "INSERT INTO trabajo_stgi.detalles_partida (idpartida, idusuario, tablero) VALUES (?, ?, ?)";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, idPartida);
            statement.setInt(2, idUsuario);
            StringBuilder tableroStr = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    tableroStr.append(tablero.get(i).get(j));
                }
            }
            statement.setString(3, tableroStr.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<ArrayList<Integer>> cargarTablero(int idPartida, int idUsuario) {
        ArrayList<ArrayList<Integer>> tablero = null;
        try {
            String sql = "SELECT tablero FROM trabajo_stgi.detalles_partida WHERE idpartida = ? AND idusuario = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, idPartida);
            statement.setInt(2, idUsuario);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String tableroStr = resultSet.getString("tablero");
                if (tableroStr != null) {
                    tablero = new ArrayList<>();
                    for (int i = 0; i < 8; i++) {
                        ArrayList<Integer> fila = new ArrayList<>();
                        for (int j = 0; j < 8; j++) {
                            fila.add(Character.getNumericValue(tableroStr.charAt(i * 8 + j)));
                        }
                        tablero.add(fila);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tablero;
    }
}