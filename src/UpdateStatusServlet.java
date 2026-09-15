import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class UpdateStatusServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String trackingId = request.getParameter("tracking_id");
        String status = request.getParameter("status");
        String location = request.getParameter("location");
        String dotPercent = request.getParameter("dot_percent");

        // Simple validation: Ensure values exist
        if (trackingId == null || status == null || location == null || dotPercent == null) {
            response.sendRedirect("admin_payments.html?msg=missing_fields");
            return;
        }

        // Professional logic: When set to 'Delivered', use the provided destination as the current location
        if (status.equals("Delivered") && location.trim().equals("")) {
             response.sendRedirect("admin_payments.html?msg=missing_location_for_delivered");
             return;
        }

        try {
            Connection con = DatabaseConfig.getConnection();
            
            // SQL query matching the updated database schema (Step 1)
            String sql = "INSERT INTO couriers (tracking_id, status, current_location, tracking_dot_percent) " +
                         "VALUES (?, ?, ?, ?) " +
                         "ON DUPLICATE KEY UPDATE " +
                         "status = VALUES(status), " +
                         "current_location = VALUES(current_location), " +
                         "tracking_dot_percent = VALUES(tracking_dot_percent)";
            
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, trackingId);
            ps.setString(2, status);
            ps.setString(3, location.trim().isEmpty() ? "Verified" : location.trim());
            // Save the new numerical percentage to the DB for the user's view
            ps.setInt(4, Integer.parseInt(dotPercent)); 
            
            int check = ps.executeUpdate();
            con.close();

            if(check > 0) {
                response.sendRedirect("admin_payments.html?msg=parcel_updated_successfully");
            } else {
                response.sendRedirect("admin_payments.html?msg=no_update_made");
            }
        } catch (SQLException se) {
            response.getWriter().println("Database Update Error: " + se.getMessage());
            se.printStackTrace();
        } catch (NumberFormatException nfe) {
             response.getWriter().println("Invalid Dot Percentage format. Must be an integer.");
        } catch (Exception e) {
            response.getWriter().println("General Error during status update: " + e.getMessage());
            e.printStackTrace();
        }
    }
}