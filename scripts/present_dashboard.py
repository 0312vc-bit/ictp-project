from __future__ import annotations

import json
import os
from datetime import datetime
from http.server import ThreadingHTTPServer, SimpleHTTPRequestHandler
from pathlib import Path


ROOT = Path(__file__).resolve().parent.parent
PORT = 5500


def reset_dashboard() -> None:
    payload = {
        "generatedAt": datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
        "pipelineStatus": "READY",
        "buildNumber": "ICTP-DEMO-RESET",
        "browserSuite": "HtmlUnit Selenium",
        "apiCoverage": "GET and POST validation",
        "securityTool": "OWASP ZAP Baseline",
        "totalTests": 0,
        "passedTests": 0,
        "failedTests": 0,
        "securityIssues": 0,
        "stages": [
            {"name": "Build", "status": "READY"},
            {"name": "UI Tests", "status": "READY"},
            {"name": "API Tests", "status": "READY"},
            {"name": "Security Scan", "status": "READY"},
            {"name": "Dashboard Publish", "status": "READY"},
        ],
        "alerts": [
            {
                "level": "WARN",
                "message": "Dashboard reset for live presentation. Trigger Jenkins to generate fresh results.",
            }
        ],
    }

    for relative in ("reports/latest-run.json", "dashboard/data/latest-run.json"):
        target = ROOT / relative
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_text(json.dumps(payload, indent=2), encoding="utf-8")


def main() -> None:
    reset_dashboard()
    os.chdir(ROOT)
    server = ThreadingHTTPServer(("127.0.0.1", PORT), SimpleHTTPRequestHandler)
    print(f"Dashboard reset to zero state.")
    print(f"Serving ICTP dashboard at http://localhost:{PORT}/dashboard/index.html")
    server.serve_forever()


if __name__ == "__main__":
    main()
