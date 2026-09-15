<%@ page import="java.sql.*, java.util.*" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CourierPro | Admin Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f8f9fa; }
        .sidebar { background-color: #212529; min-height: 100vh; color: white; }
        .table-card { border-radius: 12px; border: none; box-shadow: 0 5px 15px rgba(0,0,0,0.05); }
        .expand-row { background-color: #f1f3f5; display: none; }
        .badge-status { font-size: 0.85rem; padding: 0.4em 0.8em; }
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-md-2 sidebar p-3">
                <h4 class="fw-bold mb-4 text-primary">🚚 CourierPro</h4>
                <p class="text-uppercase small fw-bold text-muted">Admin Panel</p>
                <hr>
                <ul class="nav flex-column gap-2">
                    <li class="nav-item"><a class="nav-link text-white active bg-primary rounded" href="#"><i class="bi bi-box-seam me-2"></i> Deliveries</a></li>
                    <li class="nav-item"><a class="nav-link text-muted" href="index.jsp"><i class="bi bi-house me-2"></i> Home Page</a></li>
                </ul>
            </div>

            <!-- Main Content -->
            <div class="col-md-10 p-4">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h3 class="fw-bold">Management Dashboard</h3>
                    <span class="badge bg-dark px-3 py-2">Live Database Sync</span>
                </div>

                <div class="card table-card p-3">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle">
                            <thead class="table-dark">
                                <tr>
                                    <th>Tracking ID</th>
                                    <th>UTR Number</th>
                                    <th>Status</th>
                                    <th>Live Location</th>
                                    <th>Dot Position (%)</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    try {
                                        // Fetching records by joining payments and couriers
                                        Class.forName("com.mysql.cj.jdbc.Driver");
                                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/courier_db", "root", "password");
                                        String sql = "SELECT p.tracking_id, p.utr, p.sender_name, p.receiver_name, p.address, p.product_weight, p.distance, p.amount, " +
                                                     "COALESCE(c.status, p.status) as live_status, " +
                                                     "COALESCE(c.current_location, 'Awaiting Pickup') as live_location, " +
                                                     "IFNULL(c.tracking_dot_percent, 0) as dot_percent " +
                                                     "FROM payments p LEFT JOIN couriers c ON p.tracking_id = c.tracking_id " +
                                                     "ORDER BY p.id DESC";
                                        Statement stmt = con.createStatement();
                                        ResultSet rs = stmt.executeQuery(sql);

                                        while(rs.next()) {
                                            String trackId = rs.getString("tracking_id");
                                            String utr = rs.getString("utr");
                                            String status = rs.getString("live_status");
                                            String location = rs.getString("live_location");
                                            int dotPercent = rs.getInt("dot_percent");
                                %>
                                <!-- Main Row -->
                                <tr>
                                    <td><strong><%= trackId %></strong></td>
                                    <td><code class="text-primary"><%= utr %></code></td>
                                    <td><span class="badge bg-info badge-status"><%= status %></span></td>
                                    <td><%= location %></td>
                                    <td><strong><%= dotPercent %>%</strong></td>
                                    <td>
                                        <button class="btn btn-sm btn-outline-secondary me-1" onclick="toggleDetails('<%= trackId %>')">
                                            <i class="bi bi-eye"></i> Expand
                                        </button>
                                        <button class="btn btn-sm btn-primary" onclick="openUpdateModal('<%= trackId %>', '<%= status %>', '<%= location %>', '<%= dotPercent %>')">
                                            <i class="bi bi-pencil"></i> Override
                                        </button>
                                    </td>
                                </tr>

                                <!-- Expandable Package Details Row -->
                                <tr id="details-<%= trackId %>" class="expand-row">
                                    <td colspan="6">
                                        <div class="p-3 border rounded bg-white">
                                            <h6 class="fw-bold text-dark border-bottom pb-2">Package Details for <%= trackId %></h6>
                                            <div class="row">
                                                <div class="col-md-3"><strong>Sender:</strong> <%= rs.getString("sender_name") %></div>
                                                <div class="col-md-3"><strong>Receiver:</strong> <%= rs.getString("receiver_name") %></div>
                                                <div class="col-md-2"><strong>Weight:</strong> <%= rs.getString("product_weight") %> kg</div>
                                                <div class="col-md-2"><strong>Distance:</strong> <%= rs.getString("distance") %> km</div>
                                                <div class="col-md-2"><strong>Paid Amount:</strong> ₹<%= rs.getString("amount") %></div>
                                            </div>
                                            <div class="mt-2"><strong>Delivery Address:</strong> <%= rs.getString("address") %></div>
                                        </div>
                                    </td>
                                </tr>
                                <%
                                        }
                                        con.close();
                                    } catch(Exception e) {
                                        out.println("<tr><td colspan='6' class='text-danger'>Error loading data: " + e.getMessage() + "</td></tr>");
                                    }
                                %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Live Delivery Status Override Modal -->
    <div class="modal fade" id="overrideModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <form action="AdminServlet" method="POST">
                    <div class="modal-header bg-dark text-white">
                        <h5 class="modal-title">Override Status & Dot Position</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-content-body p-4">
                        <input type="hidden" name="trackingId" id="modalTrackingId">
                        
                        <div class="mb-3">
                            <label class="form-label fw-bold">Live Status Override</label>
                            <select name="status" id="modalStatus" class="form-select">
                                <option value="In Transit">In Transit</option>
                                <option value="Out for Delivery">Out for Delivery</option>
                                <option value="Delivered">Delivered</option>
                                <option value="Delayed">Delayed</option>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Current Stop Location</label>
                            <input type="text" name="currentLocation" id="modalLocation" class="form-control" required>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Move Live Dot Position (0% to 100%)</label>
                            <input type="number" name="dotPercent" id="modalDotPercent" class="form-control" min="0" max="100" required>
                            <div class="form-text">Sets the vertical line percentage for tracking user view.</div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-success fw-bold">Save Changes</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Toggle package details expansion
        function toggleDetails(id) {
            const row = document.getElementById('details-' + id);
            row.style.display = (row.style.display === 'none' || row.style.display === '') ? 'table-row' : 'none';
        }

        // Open status override modal with current row values
        function openUpdateModal(id, status, location, dotPercent) {
            document.getElementById('modalTrackingId').value = id;
            document.getElementById('modalStatus').value = status;
            document.getElementById('modalLocation').value = location;
            document.getElementById('modalDotPercent').value = dotPercent;
            
            var myModal = new bootstrap.Modal(document.getElementById('overrideModal'));
            myModal.show();
        }
    </script>
</body>
</html>