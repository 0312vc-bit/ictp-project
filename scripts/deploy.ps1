# deploy.ps1
# This script automatically deploys the generated DevSecOps Dashboard to our AWS EC2 instance.

$serverIp = "16.171.23.248"
$keyPath = "$PSScriptRoot\..\ictp-key.pem"
$username = "ubuntu"

Write-Host "Fixing SSH Key Permissions..." -ForegroundColor Cyan
icacls $keyPath /inheritance:r | Out-Null
icacls $keyPath /grant:r "$($env:USERNAME):(R)" | Out-Null

Write-Host "Connecting to AWS EC2 Instance ($serverIp)..." -ForegroundColor Cyan
Write-Host "Installing Nginx Web Server on Cloud..." -ForegroundColor Cyan
ssh -i $keyPath -o StrictHostKeyChecking=no $username@$serverIp "sudo apt-get update && sudo apt-get install -y nginx"

Write-Host "Uploading Premium Dashboard to Cloud..." -ForegroundColor Cyan
# Upload the entire dashboard folder to the ubuntu user's home directory
scp -i $keyPath -o StrictHostKeyChecking=no -r "$PSScriptRoot\..\dashboard" $username@${serverIp}:/home/ubuntu/

Write-Host "Publishing Website to Public IP..." -ForegroundColor Cyan
# Move the contents into the public Nginx folder
ssh -i $keyPath -o StrictHostKeyChecking=no $username@$serverIp "sudo cp -r /home/ubuntu/dashboard/* /var/www/html/ && sudo systemctl restart nginx"

Write-Host "==================================================" -ForegroundColor Green
Write-Host "AWS DEPLOYMENT SUCCESSFUL!" -ForegroundColor Green
Write-Host "Your dashboard is officially live on the internet at:" -ForegroundColor Yellow
Write-Host "http://$serverIp/" -ForegroundColor Yellow
Write-Host "==================================================" -ForegroundColor Green
Exit 0
