import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class ApprovePaymentServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String trackingId = request.getParameter("id");

        try {
            Connection con = DatabaseConfig.getConnection();
            
            // 1. Update the 'payments' table to mark as Verified
            PreparedStatement ps1 = con.prepareStatement(
                "UPDATE payments SET status = 'Verified' WHERE tracking_id = ?"
            );
            ps1.setString(1, trackingId);
            int updated = ps1.executeUpdate();

            if (updated > 0) {
                // 2. Fix the Database Error: Use UPSERT logic
                // This ensures a record exists in the 'couriers' table so the user can track it
                PreparedStatement ps2 = con.prepareStatement(
                    "INSERT INTO couriers (tracking_id, status, current_location) " +
                    "VALUES (?, 'Verified', 'Bhopal Hub') " +
                    "ON DUPLICATE KEY UPDATE status='Verified'"
                );
                ps2.setString(1, trackingId);
                ps2.executeUpdate();

                // 3. Fix the 404 Redirect Logic
                // We use Referer to keep the Admin on their dashboard, 
                // but we ensure all user-side redirects go to .jsp
                String referer = request.getHeader("Referer");
                if (referer != null && referer.contains("admin")) {
                    response.sendRedirect("admin_payments.html?msg=approved");
                } else {
                    // This is the key fix for the user-side 404
                 // WRONG: response.sendRedirect("index.html");
// RIGHT:
response.sendRedirect("index.jsp");
                }
            } else {
                response.sendRedirect("admin_payments.html?error=IdNotFound");
            }
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            // This will help you see exactly what's wrong if it fails during the demo
            response.getWriter().println("Admin Approval Error: " + e.getMessage());
        }
    }
}