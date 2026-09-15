import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class ProcessPaymentServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String trackingId = request.getParameter("trackingId");
        String txnId = request.getParameter("txnId");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Connection con = DatabaseConfig.getConnection();
            
            // 1. Insert the proof into the payments table
            PreparedStatement ps1 = con.prepareStatement(
                "INSERT INTO payments (tracking_id, transaction_no, status) VALUES (?, ?, 'Pending Verification')"
            );
            ps1.setString(1, trackingId);
            ps1.setString(2, txnId);
            ps1.executeUpdate();

            // 2. Update the courier table so the user sees 'Under Review' when they track it
            PreparedStatement ps2 = con.prepareStatement(
                "UPDATE couriers SET payment_status = 'Under Review' WHERE tracking_id = ?"
            );
            ps2.setString(1, trackingId);
            ps2.executeUpdate();

            // Success Message and Redirect
            out.println("<html><body style='font-family:sans-serif; text-align:center; padding-top:50px;'>");
            out.println("<h2>✅ Payment Proof Submitted!</h2>");
            out.println("<p>Transaction ID: " + txnId + " is under review.</p>");
            out.println("<p>Redirecting to home page...</p>");
            out.println("<script>setTimeout(function(){ window.location='index.html'; }, 3000);</script>");
            out.println("</body></html>");

        } catch (Exception e) {
            out.println("<h3 style='color:red;'>Error: " + e.getMessage() + "</h3>");
            e.printStackTrace(out);
        }
    }
}