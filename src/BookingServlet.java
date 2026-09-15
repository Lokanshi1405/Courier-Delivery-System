import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.Random;

public class BookingServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Generating a unique Tracking ID
        String trackingId = "CR-" + (1000 + new Random().nextInt(9000));
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        try {
            Connection con = DatabaseConfig.getConnection();
            
            // SQL query matching your schema
            String sql = "INSERT INTO payments (tracking_id, transaction_no, status, sender_name, sender_phone, " +
                         "sender_address, receiver_name, receiver_phone, address, product_type, " +
                         "product_weight, distance, amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, trackingId);
            ps.setString(2, request.getParameter("utr"));
            ps.setString(3, "Pending Verification"); 
            ps.setString(4, request.getParameter("sender_name"));
            ps.setString(5, request.getParameter("sender_phone"));
            ps.setString(6, request.getParameter("sender_address"));
            ps.setString(7, request.getParameter("receiver_name"));
            ps.setString(8, request.getParameter("receiver_phone"));
            ps.setString(9, request.getParameter("receiver_address"));
            ps.setString(10, request.getParameter("product_type"));
            ps.setString(11, request.getParameter("product_weight"));
            ps.setString(12, request.getParameter("distance"));
            ps.setString(13, request.getParameter("amount"));

            int check = ps.executeUpdate();
            
            if(check > 0) {
                out.println("<html><head>");
                // UPDATED: Changed from 5 to 8 seconds and ensuring it points to index.jsp
                out.println("<meta http-equiv='refresh' content='8;url=index.jsp'>"); 
                out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css' rel='stylesheet'>");
                out.println("<link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css'>");
                out.println("<title>Booking Confirmed | CourierPro</title>");
                out.println("<style>");
                // Custom CSS to make the progress bar look like it's actually timing 8 seconds
                out.println(".progress-bar-load { animation: loadBar 8s linear forwards; }");
                out.println("@keyframes loadBar { from { width: 0%; } to { width: 100%; } }");
                out.println("</style>");
                out.println("</head>");
                out.println("<body class='bg-light d-flex align-items-center' style='height:100vh;'>");
                out.println("<div class='container text-center'>");
                out.println("<div class='card shadow-lg border-0 p-5 mx-auto' style='max-width:550px; border-radius: 20px; background: white;'>");
                
                out.println("<div class='mb-4'><i class='bi bi-check-circle-fill text-success' style='font-size: 3.5rem;'></i></div>");
                out.println("<h2 class='text-success fw-bold'>Payment Submitted!</h2>");
                
                out.println("<div class='alert alert-secondary my-4'>");
                out.println("  <p class='mb-1 text-uppercase small fw-bold text-muted'>Your Tracking ID</p>");
                out.println("  <h3 class='mb-0 fw-bold text-dark'>" + trackingId + "</h3>");
                out.println("</div>");
                
                out.println("<p class='text-muted'>Verifying UTR: <b>" + request.getParameter("utr") + "</b></p>");
                out.println("<p class='text-muted small'>The admin will verify your payment shortly. Use this ID to track your parcel on the home page.</p>");
                
                // Progress bar with 8-second visual animation
                out.println("<div class='progress mt-4' style='height: 8px;'>");
                out.println("  <div class='progress-bar progress-bar-striped progress-bar-animated bg-success progress-bar-load'></div>");
                out.println("</div>");
                
                out.println("<p class='mt-3 text-secondary small'>Redirecting to home page in 8 seconds...</p>");
                
                out.println("<a href='index.jsp' class='btn btn-link text-decoration-none mt-2'>Skip wait and go home</a>");
                
                out.println("</div></div></body></html>");
            }
            con.close();
        } catch (SQLException se) {
            out.println("<div class='container py-5 text-center'>");
            out.println("<div class='alert alert-danger shadow-sm p-4'>");
            out.println("<h4><i class='bi bi-exclamation-triangle'></i> Database Error</h4>");
            out.println("<p>Code: " + se.getMessage() + "</p>");
            out.println("<a href='booking.html' class='btn btn-danger mt-3'>Try Booking Again</a>");
            out.println("</div></div>");
            se.printStackTrace();
        } catch (Exception e) {
            out.println("<div class='alert alert-warning m-5'>General Error: " + e.getMessage() + "</div>");
            e.printStackTrace();
        }
    }
}