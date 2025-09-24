@echo off
powershell -ExecutionPolicy Bypass -File "%~dp0create-pr.ps1" %*
