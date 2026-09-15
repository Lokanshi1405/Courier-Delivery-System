import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class GetPendingPayments extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONArray jsonArray = new JSONArray();

        try {
            Connection con = DatabaseConfig.getConnection();
            // Fetch only those that are NOT yet verified
            String sql = "SELECT tracking_id, transaction_no, sender_name, amount FROM payments WHERE status = 'Pending Verification'";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("id", rs.getString("tracking_id"));
                obj.put("utr", rs.getString("transaction_no"));
                obj.put("sender", rs.getString("sender_name"));
                obj.put("amount", rs.getString("amount"));
                jsonArray.put(obj);
            }
            out.print(jsonArray.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}