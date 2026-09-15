import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class GetAdminPaymentsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONArray jsonArray = new JSONArray();

        try {
            Connection con = DatabaseConfig.getConnection();
            // This query joins your payments and couriers tables to get full info
            String sql = "SELECT p.*, IFNULL(c.status, 'Pending Verification') as live_status, " +
                         "IFNULL(c.current_location, 'N/A') as current_loc, " +
                         "IFNULL(c.tracking_dot_percent, 0) as dot_p " +
                         "FROM payments p LEFT JOIN couriers c ON p.tracking_id = c.tracking_id";
            
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("tracking_id", rs.getString("tracking_id"));
                obj.put("transaction_no", rs.getString("transaction_no"));
                obj.put("sender_name", rs.getString("sender_name"));
                obj.put("sender_phone", rs.getString("sender_phone"));
                obj.put("sender_address", rs.getString("sender_address"));
                obj.put("receiver_name", rs.getString("receiver_name"));
                obj.put("receiver_phone", rs.getString("receiver_phone"));
                obj.put("receiver_address", rs.getString("address"));
                obj.put("product_type", rs.getString("product_type"));
                obj.put("product_weight", rs.getString("product_weight"));
                obj.put("amount", rs.getString("amount"));
                obj.put("status", rs.getString("live_status"));
                obj.put("location", rs.getString("current_loc"));
                obj.put("dot_percent", rs.getInt("dot_p"));
                jsonArray.put(obj);
            }
            out.print(jsonArray.toString());
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
        }
    }
}