package com.ictp.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public final class DemoAppServer {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static HttpServer server;

    private DemoAppServer() {
    }

    public static synchronized void start(int port) {
        if (server != null) {
            return;
        }

        try {
            server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
            server.createContext("/", new HtmlHandler());
            server.createContext("/api/health", exchange -> json(exchange, 200, Map.of(
                    "service", "ICTP Demo App",
                    "status", "UP",
                    "pipeline", "ready"
            )));
            server.createContext("/api/build", new BuildHandler());
            server.createContext("/api/deploy", new DeployHandler());
            server.setExecutor(Executors.newFixedThreadPool(4));
            server.start();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to start demo application server", exception);
        }
    }

    public static synchronized void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
    }

    private static final class HtmlHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = """
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                        <meta charset="UTF-8">
                        <title>ICTP Demo App</title>
                        <style>
                            body { font-family: Arial, sans-serif; padding: 40px; background: #eef4ff; color: #10203f; }
                            .wrap { max-width: 760px; margin: 0 auto; background: white; padding: 28px; border-radius: 18px; box-shadow: 0 16px 32px rgba(16, 32, 63, 0.12); }
                            label, input, button { display: block; width: 100%; }
                            input { margin: 8px 0 16px; padding: 12px; border: 1px solid #cbd6ea; border-radius: 10px; }
                            button { background: #173f8a; color: white; border: 0; padding: 12px; border-radius: 10px; font-weight: 700; cursor: pointer; }
                            .stats { display: flex; gap: 14px; margin-top: 24px; }
                            .tile { flex: 1; background: #f5f8ff; padding: 16px; border-radius: 14px; }
                            #message { margin-top: 18px; font-weight: 700; color: #157347; }
                        </style>
                        <script>
                            function simulateLogin() {
                                const user = document.getElementById('username').value;
                                const pass = document.getElementById('password').value;
                                const message = document.getElementById('message');
                                if (user === 'admin' && pass === 'admin123') {
                                    message.textContent = 'Login successful. Continuous testing dashboard synced.';
                                } else {
                                    message.textContent = 'Invalid credentials.';
                                    message.style.color = '#b42318';
                                }
                            }
                        </script>
                    </head>
                    <body>
                    <div class="wrap">
                        <h1>ICTP Demo Application</h1>
                        <p>This lightweight app is the application under test for UI automation, API checks, and security scan simulation.</p>
                        <label for="username">Username</label>
                        <input id="username" type="text" value="admin">
                        <label for="password">Password</label>
                        <input id="password" type="password" value="admin123">
                        <button id="loginButton" onclick="simulateLogin()">Login</button>
                        <div id="message">Ready for validation.</div>
                        <div class="stats">
                            <div class="tile"><strong id="buildStatus">SUCCESS</strong><p>Latest build status</p></div>
                            <div class="tile"><strong id="uiSuite">4 / 4</strong><p>Automation tests passed</p></div>
                            <div class="tile"><strong id="securityScore">0 High</strong><p>Security issues found</p></div>
                        </div>
                    </div>
                    </body>
                    </html>
                    """;
            send(exchange, 200, "text/html", html);
        }
    }

    private static final class BuildHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("buildId", "ICTP-DEMO-001");
            response.put("branch", "main");
            response.put("status", "SUCCESS");
            response.put("uiTestsPassed", 2);
            response.put("apiTestsPassed", 2);
            response.put("securityIssues", 0);
            json(exchange, 200, response);
        }
    }

    private static final class DeployHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                json(exchange, 405, Map.of("message", "Method not allowed"));
                return;
            }

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("deploymentStatus", "QUEUED");
            response.put("environment", "staging");
            response.put("approver", "automation-bot");
            json(exchange, 201, response);
        }
    }

    private static void json(HttpExchange exchange, int status, Object payload) throws IOException {
        send(exchange, status, "application/json", OBJECT_MAPPER.writeValueAsString(payload));
    }

    private static void send(HttpExchange exchange, int status, String contentType, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(bytes);
        }
    }
}
