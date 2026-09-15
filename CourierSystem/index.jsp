<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Check if a user session exists
    String user = (String) session.getAttribute("user");
    boolean isLoggedIn = (user != null);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CourierPro | Fast & Smart Delivery</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://unpkg.com/aos@2.3.4/dist/aos.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

    <style>
        body {
            font-family: 'Inter', sans-serif;
            overflow-x: hidden;
        }

        /* Navbar */
        .navbar-brand {
            font-weight: 700;
            font-size: 1.5rem;
        }

        /* Hero Section */
        .hero {
            position: relative;
            height: 80vh;
            min-height: 500px;
            background: linear-gradient(rgba(13, 110, 253, 0.8), rgba(102, 16, 242, 0.8)), 
                        url('https://images.unsplash.com/photo-1586769852044-692d6e39241f?q=80&w=2070&auto=format&fit=crop') center/cover;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            text-align: center;
        }

        /* Booking Box Overlap */
        .booking-box {
            position: relative;
            z-index: 10;
            margin-top: -80px;
        }

        .card {
            border: none;
            border-radius: 15px;
            transition: all 0.3s ease;
        }

        .feature-card:hover {
            transform: translateY(-10px);
            box-shadow: 0 15px 30px rgba(0,0,0,0.1) !important;
        }

        /* Reviews */
        .review-card {
            background: #ffffff;
            border-left: 5px solid #0d6efd;
        }

        /* Footer */
        footer {
            background: #111;
            color: #ccc;
            padding: 60px 0 20px;
        }

        .btn-success {
            background-color: #198754;
            border: none;
        }
        
        .btn-warning {
            color: #212529;
            font-weight: 600;
        }
    </style>
</head>

<body>

<nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top">
    <div class="container">
        <a class="navbar-brand" href="#">🚚 CourierPro</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto align-items-center">
                <li class="nav-item"><a class="nav-link mx-2" href="#">Home</a></li>
                <li class="nav-item"><a class="nav-link mx-2" href="#">Services</a></li>
                <li class="nav-item"><a class="nav-link mx-2" href="#">Reviews</a></li>
                <li class="nav-item"><a href="#tracking-section" class="btn btn-outline-warning btn-sm mx-2">Track Order</a></li>
                <li class="nav-item"><a href="booking.html" class="btn btn-success btn-sm mx-2">Book Now</a></li>
                
                <% if (isLoggedIn) { %>
                    <li class="nav-item dropdown ms-2">
                        <a class="nav-link dropdown-toggle btn btn-outline-light btn-sm px-3" href="#" id="profileDropdown" role="button" data-bs-toggle="dropdown">
                            <i class="bi bi-person-circle"></i> <%= user %>
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end shadow border-0 mt-2">
                            <li><a class="dropdown-item py-2" href="#"><i class="bi bi-person"></i> My Profile</a></li>
                            <li><a class="dropdown-item py-2" href="#"><i class="bi bi-box-seam"></i> My Orders</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item py-2 text-danger" href="LogoutServlet"><i class="bi bi-box-arrow-right"></i> Logout</a></li>
                        </ul>
                    </li>
                <% } %>
            </ul>
        </div>
    </div>
</nav>

<section class="hero">
    <div class="container" data-aos="zoom-out">
        <h1 class="display-3 fw-bold mb-3">Fast & Smart Courier Delivery</h1>
        <p class="lead mb-4">Reliable logistics solutions for businesses and individuals worldwide.</p>
        
        <% if (!isLoggedIn) { %>
            <a href="login.html" class="btn btn-light btn-lg px-5 fw-bold shadow">User Login</a>
        <% } else { %>
            <a href="#" class="btn btn-light btn-lg px-5 fw-bold shadow">Go to Profile</a>
        <% } %>
    </div>
</section>

<section id="tracking-section" class="container booking-box">
    <div class="card p-4 shadow-lg" data-aos="fade-up">
        <h4 class="text-center mb-4 fw-bold">Track Your Shipment</h4>
        
        <form action="track" method="get" class="row g-3 justify-content-center border-bottom pb-4 mb-4">
            <div class="col-md-6">
                <label class="form-label small fw-bold">Enter Tracking ID</label>
                <input type="text" name="trackingId" class="form-control" placeholder="e.g. CR-101" required>
            </div>
            <div class="col-md-3 d-flex align-items-end">
                <button type="submit" class="btn btn-warning w-100 py-2 fw-bold">Track Now</button>
            </div>
        </form>

        <h4 class="text-center mb-4 fw-bold">Calculate Estimate</h4>
        <div class="row g-3">
            <div class="col-md-3">
                <label class="form-label small fw-bold">Sender City</label>
                <input class="form-control" placeholder="e.g. New York">
            </div>
            <div class="col-md-3">
                <label class="form-label small fw-bold">Receiver City</label>
                <input class="form-control" placeholder="e.g. London">
            </div>
            <div class="col-md-3">
                <label class="form-label small fw-bold">Weight (kg)</label>
                <input type="number" class="form-control" placeholder="0.00">
            </div>
            <div class="col-md-3 d-flex align-items-end">
                <button class="btn btn-primary w-100 py-2 fw-bold">Get Estimate</button>
            </div>
        </div>
    </div>
</section>

<section class="container py-5 mt-5">
    <div class="text-center mb-5">
        <h2 class="fw-bold">Why Choose CourierPro?</h2>
        <div class="mx-auto bg-primary" style="height: 3px; width: 60px;"></div>
    </div>

    <div class="row g-4 text-center">
        <div class="col-md-4" data-aos="fade-up" data-aos-delay="100">
            <div class="card p-4 shadow-sm h-100 feature-card">
                <div class="mb-3 text-primary"><i class="bi bi-geo-alt-fill fs-1"></i></div>
                <h5>Real-Time Tracking</h5>
                <p class="text-muted">Know exactly where your parcel is at every moment with GPS accuracy.</p>
            </div>
        </div>
        <div class="col-md-4" data-aos="fade-up" data-aos-delay="200">
            <div class="card p-4 shadow-sm h-100 feature-card">
                <div class="mb-3 text-primary"><i class="bi bi-shield-lock-fill fs-1"></i></div>
                <h5>Secure Delivery</h5>
                <p class="text-muted">Insured and handled with care. Your items are safe in our hands.</p>
            </div>
        </div>
        <div class="col-md-4" data-aos="fade-up" data-aos-delay="300">
            <div class="card p-4 shadow-sm h-100 feature-card">
                <div class="mb-3 text-primary"><i class="bi bi-lightning-charge-fill fs-1"></i></div>
                <h5>Fast Shipping</h5>
                <p class="text-muted">Express options available for same-day and next-day deliveries.</p>
            </div>
        </div>
    </div>
</section>

<section class="bg-light py-5">
    <div class="container">
        <h2 class="text-center fw-bold mb-5">Customer Voice</h2>
        
        <div id="reviewList" class="row mb-4">
            <div class="col-md-4 mb-3">
                <div class="card p-3 shadow-sm review-card">
                    <p class="fst-italic text-muted">"Amazing service! My package arrived 2 days early. Highly recommended."</p>
                    <div class="fw-bold">- John Doe</div>
                </div>
            </div>
        </div>

        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card p-4 shadow">
                    <h5 class="mb-3">Leave a Review</h5>
                    <input id="reviewName" class="form-control mb-2" placeholder="Your Name">
                    <textarea id="reviewText" class="form-control mb-3" rows="3" placeholder="Share your experience..."></textarea>
                    <button class="btn btn-dark w-100" onclick="addReview()">Post Review</button>
                </div>
            </div>
        </div>
    </div>
</section>

<footer class="text-center">
    <div class="container">
        <div class="row">
            <div class="col-md-12 mb-3">
                <h4 class="text-white">🚚 CourierPro</h4>
                <p>Moving things forward since 2020.</p>
            </div>
        </div>
        <hr class="bg-secondary">
        <p class="mb-3">&copy; 2026 CourierPro Logistics | All Rights Reserved</p>
        <a href="admin_login.html" class="btn btn-primary btn-sm mt-2">Admin Dashboard</a>
    </div>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://unpkg.com/aos@2.3.4/dist/aos.js"></script>

<script>
    AOS.init({ duration: 800, once: true });

    function addReview() {
        let name = document.getElementById("reviewName").value;
        let text = document.getElementById("reviewText").value;

        if(name.trim() === "" || text.trim() === "") {
            alert("Please fill all fields");
            return;
        }

        let reviewHTML = `
        <div class="col-md-4 mb-3">
            <div class="card p-3 shadow-sm review-card" data-aos="zoom-in">
                <p class="fst-italic text-muted">"${text}"</p>
                <div class="fw-bold">- ${name}</div>
            </div>
        </div>`;

        document.getElementById("reviewList").insertAdjacentHTML('beforeend', reviewHTML);

        document.getElementById("reviewName").value = "";
        document.getElementById("reviewText").value = "";
    }
</script>

</body>
</html>