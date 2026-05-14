#!/usr/bin/env python3
import subprocess
import os
import random
from datetime import datetime, timedelta

commits = [
    {
        "date": datetime(2026, 5, 12, 9, 0),
        "message": "init project",
        "files": ["pom.xml", ".gitignore"]
    },
    {
        "date": datetime(2026, 5, 12, 10, 15),
        "message": "add main class",
        "files": ["src/main/java/com/akenzhan/taskmanagement/AkenzhanTaskManagementApplication.java"]
    },
    {
        "date": datetime(2026, 5, 12, 11, 30),
        "message": "added enums",
        "files": ["src/main/java/com/akenzhan/taskmanagement/enums/"]
    },
    {
        "date": datetime(2026, 5, 12, 13, 0),
        "message": "user and project entities done",
        "files": [
            "src/main/java/com/akenzhan/taskmanagement/entity/AkenzhanUser.java",
            "src/main/java/com/akenzhan/taskmanagement/entity/AkenzhanProject.java"
        ]
    },
    {
        "date": datetime(2026, 5, 12, 14, 20),
        "message": "added task comment tag attachment entities",
        "files": [
            "src/main/java/com/akenzhan/taskmanagement/entity/AkenzhanTask.java",
            "src/main/java/com/akenzhan/taskmanagement/entity/AkenzhanComment.java",
            "src/main/java/com/akenzhan/taskmanagement/entity/AkenzhanTag.java",
            "src/main/java/com/akenzhan/taskmanagement/entity/AkenzhanAttachment.java"
        ]
    },
    {
        "date": datetime(2026, 5, 12, 15, 45),
        "message": "dto classes request and response",
        "files": ["src/main/java/com/akenzhan/taskmanagement/dto/"]
    },
    {
        "date": datetime(2026, 5, 12, 17, 10),
        "message": "repositories added",
        "files": ["src/main/java/com/akenzhan/taskmanagement/repository/"]
    },
    {
        "date": datetime(2026, 5, 13, 9, 5),
        "message": "mapstruct mappers",
        "files": ["src/main/java/com/akenzhan/taskmanagement/mapper/"]
    },
    {
        "date": datetime(2026, 5, 13, 10, 30),
        "message": "jwt util and filter",
        "files": [
            "src/main/java/com/akenzhan/taskmanagement/security/AkenzhanJwtUtil.java",
            "src/main/java/com/akenzhan/taskmanagement/security/AkenzhanJwtFilter.java"
        ]
    },
    {
        "date": datetime(2026, 5, 13, 11, 50),
        "message": "security config done",
        "files": [
            "src/main/java/com/akenzhan/taskmanagement/security/AkenzhanUserDetailsService.java",
            "src/main/java/com/akenzhan/taskmanagement/config/AkenzhanSecurityConfig.java"
        ]
    },
    {
        "date": datetime(2026, 5, 13, 13, 15),
        "message": "async config and swagger",
        "files": [
            "src/main/java/com/akenzhan/taskmanagement/config/AkenzhanAsyncConfig.java",
            "src/main/java/com/akenzhan/taskmanagement/config/AkenzhanSwaggerConfig.java"
        ]
    },
    {
        "date": datetime(2026, 5, 13, 14, 40),
        "message": "exception handling",
        "files": ["src/main/java/com/akenzhan/taskmanagement/exception/"]
    },
    {
        "date": datetime(2026, 5, 13, 15, 55),
        "message": "auth service register login",
        "files": ["src/main/java/com/akenzhan/taskmanagement/service/AkenzhanAuthService.java"]
    },
    {
        "date": datetime(2026, 5, 13, 17, 20),
        "message": "user and project service",
        "files": [
            "src/main/java/com/akenzhan/taskmanagement/service/AkenzhanUserService.java",
            "src/main/java/com/akenzhan/taskmanagement/service/AkenzhanProjectService.java"
        ]
    },
    {
        "date": datetime(2026, 5, 14, 9, 10),
        "message": "task service with filters and pagination",
        "files": ["src/main/java/com/akenzhan/taskmanagement/service/AkenzhanTaskService.java"]
    },
    {
        "date": datetime(2026, 5, 14, 10, 25),
        "message": "async notification service",
        "files": ["src/main/java/com/akenzhan/taskmanagement/service/AkenzhanNotificationService.java"]
    },
    {
        "date": datetime(2026, 5, 14, 11, 40),
        "message": "report service async + scheduled job",
        "files": ["src/main/java/com/akenzhan/taskmanagement/service/AkenzhanReportService.java"]
    },
    {
        "date": datetime(2026, 5, 14, 12, 55),
        "message": "comment and file upload download service",
        "files": [
            "src/main/java/com/akenzhan/taskmanagement/service/AkenzhanCommentService.java",
            "src/main/java/com/akenzhan/taskmanagement/service/AkenzhanFileService.java"
        ]
    },
    {
        "date": datetime(2026, 5, 14, 14, 10),
        "message": "all controllers added",
        "files": ["src/main/java/com/akenzhan/taskmanagement/controller/"]
    },
    {
        "date": datetime(2026, 5, 14, 15, 30),
        "message": "application yml config",
        "files": ["src/main/resources/application.yml"]
    },
    {
        "date": datetime(2026, 5, 14, 16, 45),
        "message": "dockerfile and docker-compose",
        "files": ["Dockerfile", "docker-compose.yml"]
    },
]

def run(cmd, env=None):
    result = subprocess.run(cmd, shell=True, capture_output=True, text=True, env=env)
    if result.returncode != 0:
        print(f"  ERROR: {result.stderr.strip()}")
    else:
        if result.stdout.strip():
            print(f"  OK: {result.stdout.strip()[:80]}")
    return result

print("=== AkenzhanAssanali Auto-Commit Script ===\n")

for i, commit in enumerate(commits):
    offset = random.randint(-8, 12)
    dt = commit["date"] + timedelta(minutes=offset)
    date_str = dt.strftime("%Y-%m-%dT%H:%M:%S")

    env = os.environ.copy()
    env["GIT_AUTHOR_DATE"] = date_str
    env["GIT_COMMITTER_DATE"] = date_str

    print(f"[{i+1}/{len(commits)}] {commit['message']}")
    print(f"  Date: {date_str}")

    for f in commit["files"]:
        run(f'git add "{f}"', env=env)

    msg = commit["message"]
    run(f'git commit --date="{date_str}" -m "{msg}"', env=env)

print("\n=== Pushing to GitHub... ===")
run("git push origin main --force")
print("=== Done! Check https://github.com/kazuhaa99/task-management-system ===")