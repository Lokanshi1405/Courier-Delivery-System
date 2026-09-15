import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class TrackServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String trackingId = request.getParameter("trackingId");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        String user = (session != null) ? (String) session.getAttribute("user") : null;

        try {
            Connection con = DatabaseConfig.getConnection();
            
            // UPDATED SQL: Now specifically fetching the tracking_dot_percent we added to the couriers table
            String sql = "SELECT p.sender_name, p.receiver_name, p.address, " +
                         "COALESCE(c.status, p.status) as live_status, " +
                         "COALESCE(c.current_location, 'Awaiting Pickup') as live_location, " +
                         "IFNULL(c.tracking_dot_percent, 0) as dot_position " +
                         "FROM payments p " +
                         "LEFT JOIN couriers c ON p.tracking_id = c.tracking_id " +
                         "WHERE p.tracking_id = ?";
            
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, trackingId);
            ResultSet rs = ps.executeQuery();

            out.println("<!DOCTYPE html><html><head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>CourierPro | Live Tracking</title>");
            out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css' rel='stylesheet'>");
            out.println("<link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css'>");
            out.println("<style>");
            out.println("body { font-family: 'Inter', sans-serif; background-color: #f4f7f6; display: flex; align-items: center; min-height: 100vh; }");
            out.println(".tracking-card { border: none; border-radius: 15px; box-shadow: 0 10px 30px rgba(0,0,0,0.1); max-width: 500px; width: 100%; margin: auto; background: white; overflow: hidden; }");
            out.println(".status-header { background: #1a1d20; color: white; padding: 25px; }");
            out.println(".progress-track { padding: 20px; border-left: 2px solid #e9ecef; margin-left: 20px; position: relative; height: 120px; }");
            
            // DYNAMIC CSS: The 'top' value is now a variable that changes based on the percentage
            out.println(".track-dot { position: absolute; left: -9px; width: 16px; height: 16px; background: #0d6efd; border-radius: 50%; border: 3px solid white; transition: top 1s ease-in-out; }");
            out.println(".navbar-custom { position: fixed; top: 0; width: 100%; background: #212529; padding: 10px; z-index: 1000; }");
            out.println("</style></head><body>");

            out.println("<div class='navbar-custom d-flex justify-content-between px-4'>");
            out.println("  <span class='text-white fw-bold'>🚚 CourierPro</span>");
            if (user != null) {
                out.println("  <span class='text-white small'>Welcome, " + user + "</span>");
            }
            out.println("</div>");

            out.println("<div class='container mt-5'>");
            out.println("<div class='card tracking-card mt-4'>");

            if (rs.next()) {
                String currentStatus = rs.getString("live_status");
                String currentLocation = rs.getString("live_location");
                int dotPosition = rs.getInt("dot_position"); // Get the 0-100 value from DB
                
                out.println("<div class='status-header text-center'>");
                out.println("  <h4 class='mb-0 text-white'>Live Status Updates</h4>");
                out.println("  <code class='text-info'>Track ID: " + trackingId + "</code>");
                out.println("</div>");

                out.println("<div class='card-body p-4'>");
                
                out.println("<div class='text-center mb-4'>");
                out.println("  <h2 class='fw-bold text-primary'>" + currentStatus + "</h2>");
                out.println("  <p class='text-muted small'><i class='bi bi-geo-alt-fill'></i> Current Stop: " + currentLocation + "</p>");
                out.println("</div>");

                // APPLYING THE DOT POSITION: We use the dotPosition variable directly in the style
                out.println("<div class='progress-track'>");
                out.println("  <div class='track-dot' style='top: " + dotPosition + "%;'></div>");
                
                out.println("  <div class='mb-4'>");
                out.println("    <h6 class='fw-bold mb-0 text-dark'>Shipping Information</h6>");
                out.println("    <p class='text-muted small mb-0'>To: " + rs.getString("receiver_name") + "</p>");
                out.println("  </div>");
                
                out.println("  <div style='margin-top: 35px;'>");
                out.println("    <h6 class='fw-bold mb-0 text-dark'>Delivery Destination</h6>");
                out.println("    <p class='text-muted small mb-0'>" + rs.getString("address") + "</p>");
                out.println("  </div>");
                out.println("</div>");
                
            } else {
                out.println("<div class='p-5 text-center'>");
                out.println("  <i class='bi bi-exclamation-circle text-warning display-4'></i>");
                out.println("  <div class='alert alert-warning mt-3'><b>" + trackingId + "</b> not found.</div>");
                out.println("  <a href='index.jsp' class='btn btn-outline-dark btn-sm'>Try Again</a>");
                out.println("</div>");
            }

            out.println("<div class='p-4 pt-0 text-center'>");
            out.println("  <hr>");
            out.println("  <a href='index.jsp' class='btn btn-dark w-100 py-2 fw-bold'>Return to Home Page</a>");
            out.println("</div>");
            
            out.println("</div></div></div>");
            con.close();

        } catch (Exception e) {
            out.println("<div class='container mt-5'><div class='alert alert-danger'>Database Error: " + e.getMessage() + "</div></div>");
        }

        out.println("</body></html>");
    }
}