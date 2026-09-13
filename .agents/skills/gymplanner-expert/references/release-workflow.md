# Release Workflow & Asset Deployment Reference

## 1. Attaching APK Binaries to GitHub Releases
Due to upload payload sizes (~19 MB), upload using `curl.exe` with timeout handling to prevent socket write timeouts:

```python
import subprocess
cmd = [
    "curl.exe", "-sS", "-X", "POST",
    "-H", f"Authorization: Bearer {token}",
    "-H", "User-Agent: Antigravity",
    "-H", "Content-Type: application/vnd.android.package-archive",
    "--data-binary", f"@{apk_path}",
    "--connect-timeout", "60",
    "--max-time", "600",
    f"https://uploads.github.com/repos/{repo}/releases/{release_id}/assets?name={apk_name}"
]
subprocess.run(cmd, check=True)
```

## 2. Checksum Verification
Always compute and publish SHA-256 and MD5 hashes:
```python
import hashlib
data = open("GymPlanner-v1.0.0.apk", "rb").read()
print("SHA256:", hashlib.sha256(data).hexdigest())
print("MD5:", hashlib.md5(data).hexdigest())
```

## 3. Secure Private Repository Sharing
To share a private repository without exposing tokens or making it public:
1. GitHub Repo -> **Settings** -> **Collaborators** -> **Add people**.
2. Invite via username or email.
3. Select **Read** permission for view/test access, or **Write** for contributions.
