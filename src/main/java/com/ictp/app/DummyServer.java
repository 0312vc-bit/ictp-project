package com.ictp.app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class DummyServer {

    private static HttpServer server;
    public static final int PORT = 8085;

    public static void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // UI Route: Login Page
        server.createContext("/login", new LoginHandler());
        
        // API Route: Health Check
        server.createContext("/api/health", new HealthApiHandler());
        
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Dummy Server started on port " + PORT);
    }

    public static void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Dummy Server stopped.");
        }
    }

    public static void main(String[] args) throws IOException {
        start();
    }

    // --- Handlers ---

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = """
                    <!DOCTYPE html>
                    <html>
                    <head><title>ICTP Demo Login</title></head>
                    <body>
                        <h2>Login to Demo App</h2>
                        <form id="loginForm">
                            <label for="username">Username:</label>
                            <input type="text" id="username" name="username"><br><br>
                            <label for="password">Password:</label>
                            <input type="password" id="password" name="password"><br><br>
                            <input type="button" id="loginBtn" value="Login" onclick="login()">
                        </form>
                        <p id="message"></p>
                        <script>
                            function login() {
                                var user = document.getElementById('username').value;
                                var pass = document.getElementById('password').value;
                                if(user === 'admin' && pass === 'password123') {
                                    document.getElementById('message').innerText = 'Login Successful!';
                                    document.getElementById('message').style.color = 'green';
                                } else {
                                    document.getElementById('message').innerText = 'Invalid Credentials';
                                    document.getElementById('message').style.color = 'red';
                                }
                            }
                        </script>
                    </body>
                    </html>
                    """;
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class HealthApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "{\"status\": \"OK\", \"version\": \"1.0.0\"}";
            t.getResponseHeaders().set("Content-Type", "application/json");
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
