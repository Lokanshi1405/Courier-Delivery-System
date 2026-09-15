import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class AdminServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String trackingId = request.getParameter("trackingId");
        String status = request.getParameter("status");
        String currentLocation = request.getParameter("currentLocation");
        int dotPercent = Integer.parseInt(request.getParameter("dotPercent"));

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/courier_db", "root", "password");

            // UPSERT statement to insert new status or update existing tracking details
            String sql = "INSERT INTO couriers (tracking_id, status, current_location, tracking_dot_percent) " +
                         "VALUES (?, ?, ?, ?) " +
                         "ON DUPLICATE KEY UPDATE status = ?, current_location = ?, tracking_dot_percent = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, trackingId);
            ps.setString(2, status);
            ps.setString(3, currentLocation);
            ps.setInt(4, dotPercent);
            
            ps.setString(5, status);
            ps.setString(6, currentLocation);
            ps.setInt(7, dotPercent);

            ps.executeUpdate();
            con.close();

            // Redirect back to admin dashboard upon successful update
            response.sendRedirect("admin.jsp");

        } catch (Exception e) {
            response.setContentType("text/html");
            response.getWriter().println("<h3 style='color:red;'>Error updating delivery status: " + e.getMessage() + "</h3>");
        }
    }
}