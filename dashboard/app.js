let currentState = "SYNCING";

function loadDataScript() {
    const oldScript = document.getElementById('pipelineDataScript');
    if (oldScript) oldScript.remove();

    const script = document.createElement('script');
    script.id = 'pipelineDataScript';
    // Append timestamp to prevent browser caching
    script.src = 'report-data.js?t=' + new Date().getTime();
    script.onload = () => {
        checkStateChange();
    };
    script.onerror = () => {
        console.log("Waiting for pipeline to generate data...");
    }
    document.body.appendChild(script);
}

function checkStateChange() {
    const isDataEmpty = !window.testData || Object.keys(window.testData).length === 0;
    
    if (isDataEmpty && currentState !== "SYNCING") {
        // Pipeline just started, reset UI to 0
        currentState = "SYNCING";
        resetDashboard();
    } else if (!isDataEmpty && currentState === "SYNCING") {
        // Pipeline finished, animate new results
        currentState = "FINISHED";
        fetchTestResults();
        fetchZapResults();
    }
}

function resetDashboard() {
    document.getElementById('totalTests').innerText = 0;
    document.getElementById('passedTests').innerText = 0;
    document.getElementById('failedTests').innerText = 0;
    document.getElementById('securityAlerts').innerText = 0;
    document.getElementById('testProgressBar').style.width = '0%';
    document.getElementById('testProgressBar').style.background = 'var(--success)';
    document.getElementById('testResultsBody').innerHTML = '';
    document.getElementById('securityStatus').innerHTML = 'Scanning...';
    document.getElementById('securityStatus').style.color = 'var(--text-secondary)';
}

document.addEventListener("DOMContentLoaded", () => {
    // Initial load
    loadDataScript();
    
    // Poll every 3 seconds for updates
    setInterval(loadDataScript, 3000);
});

async function fetchTestResults() {
    try {
        const data = window.testData;
        
        // Update Stats
        document.getElementById('totalTests').innerText = data.total || 0;
        
        const passedEl = document.getElementById('passedTests');
        passedEl.innerText = 0;
        animateValue(passedEl, 0, data.passed || 0, 1500);

        const failedEl = document.getElementById('failedTests');
        failedEl.innerText = 0;
        animateValue(failedEl, 0, data.failed || 0, 1500);

        // Progress bar
        const total = data.total || 1; 
        const passPercentage = ((data.passed || 0) / total) * 100;
        const bar = document.getElementById('testProgressBar');
        setTimeout(() => {
            bar.style.width = passPercentage + '%';
            if(passPercentage < 100) {
                bar.style.background = 'linear-gradient(90deg, #ef4444, #f87171)'; 
            }
        }, 800);

        // Update Table
        const tbody = document.getElementById('testResultsBody');
        tbody.innerHTML = '';
        if (data.results) {
            data.results.forEach(test => {
                const tr = document.createElement('tr');
                tr.style.animation = `fadeUp 0.5s ease forwards`;
                
                let statusBadge = '';
                if (test.status === 'SUCCESS') {
                    statusBadge = `<span class="status-badge PASSED">PASSED</span>`;
                } else {
                    statusBadge = `<span class="status-badge FAILED">FAILED</span>`;
                }

                tr.innerHTML = `
                    <td style="font-family: monospace; color: #e2e8f0;">${test.name}</td>
                    <td style="color: var(--text-secondary);">${test.description}</td>
                    <td style="color: var(--text-secondary);">${test.duration}ms</td>
                    <td>${statusBadge}</td>
                `;
                tbody.appendChild(tr);
            });
        }
    } catch (error) {
        console.error('Error rendering test results:', error);
    }
}

async function fetchZapResults() {
    try {
        const data = window.zapData;
        
        let alertCount = 0;
        if(data.site && data.site[0] && data.site[0].alerts) {
            alertCount = data.site[0].alerts.length;
        }

        const alertsEl = document.getElementById('securityAlerts');
        alertsEl.innerText = 0;
        animateValue(alertsEl, 0, alertCount, 1500);

        const statusEl = document.getElementById('securityStatus');
        setTimeout(() => {
            if(alertCount > 0) {
                statusEl.innerHTML = `<i class="fa-solid fa-triangle-exclamation" style="color: var(--warning)"></i> ${alertCount} vulnerabilities found.`;
                statusEl.style.color = 'var(--warning)';
            } else {
                statusEl.innerHTML = `<i class="fa-solid fa-shield-check" style="color: var(--success)"></i> Code is secure.`;
                statusEl.style.color = 'var(--success)';
            }
        }, 1500);

        // Populate Modal Data
        const tbody = document.getElementById('securityDetailsBody');
        tbody.innerHTML = '';
        if (alertCount > 0) {
            data.site[0].alerts.forEach(alert => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td style="color: #e2e8f0; font-weight: 600;">${alert.alert}</td>
                    <td><span class="status-badge" style="color: var(--warning); border: 1px solid var(--warning);">${alert.riskdesc.split(' ')[0]}</span></td>
                    <td style="font-size: 0.85rem; color: var(--text-secondary);">${alert.desc}</td>
                `;
                tbody.appendChild(tr);
            });
        }

    } catch (error) {
        console.error('Error fetching ZAP results:', error);
    }
}

function animateValue(obj, start, end, duration) {
    let startTimestamp = null;
    const step = (timestamp) => {
        if (!startTimestamp) startTimestamp = timestamp;
        const progress = Math.min((timestamp - startTimestamp) / duration, 1);
        obj.innerHTML = Math.floor(progress * (end - start) + start);
        if (progress < 1) {
            window.requestAnimationFrame(step);
        } else {
            obj.innerHTML = end;
        }
    };
    window.requestAnimationFrame(step);
}

const style = document.createElement('style');
style.innerHTML = `
@keyframes fadeUp {
    from { opacity: 0; transform: translateY(10px); }
    to { opacity: 1; transform: translateY(0); }
}
`;
document.head.appendChild(style);

// Modal Logic
function openSecurityModal() {
    const modal = document.getElementById('securityModal');
    modal.classList.add('show');
}

function closeSecurityModal() {
    const modal = document.getElementById('securityModal');
    modal.classList.remove('show');
}

// Close modal when clicking outside of it
window.onclick = function(event) {
    const modal = document.getElementById('securityModal');
    if (event.target == modal) {
        closeSecurityModal();
    }
}
