import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class AdminLoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Ensure these match the 'name' attributes in your admin_login.html
        String user = request.getParameter("username");
        String pass = request.getParameter("password");

        try {
            Connection con = DatabaseConfig.getConnection();
            // Using a query that specifically checks the admins table
            String sql = "SELECT * FROM admins WHERE username=? AND password=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // 1. Create a session to "remember" the admin
                HttpSession session = request.getSession(true);
                session.setAttribute("adminUser", user);
                
                // 2. Redirect to the dashboard through the site structure
                response.sendRedirect("admin_dashboard.html");
            } else {
                // 3. Failed login: send back to login page with error param
                response.sendRedirect("admin_login.html?error=invalid");
            }
            
            con.close(); // Clean up the connection
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Database Connection Error: " + e.getMessage());
        }
    }
}