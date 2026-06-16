@echo off
REM This batch file bypasses the mvnw.cmd wrapper to avoid PowerShell memory issues (Error 800705af).
REM It runs the Maven executable directly from the locally downloaded distribution.

"C:\Users\User\.m2\wrapper\dists\apache-maven-3.9.16\0daed3be3ebd1c706f0e69e8b07c6b73f5cc4ea3dfce72a8d0ec2e849ca2ddb0\bin\mvn.cmd" spring-boot:run
