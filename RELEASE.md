# Release Process

## Automated Release Build

Every push to `main` triggers an automated release APK build.

### Download Release APK

**Option 1: GitHub Actions Artifacts** (Available for 30 days)
1. Go to [Actions](https://github.com/VictorHerreraO/Score-count/actions)
2. Click latest "Build Release APK" workflow run
3. Download `release-apk-X.X.X` artifact
4. Unzip and install APK

**Option 2: GitHub Releases** (Permanent)
1. Go to [Releases](https://github.com/VictorHerreraO/Score-count/releases)
2. Download the latest `.apk` file
3. Install on your device

### Creating a Formal Release

```bash
# 1. Ensure main is up to date
git checkout main
git pull origin main

# 2. Create and push version tag
git tag -a v1.2.0 -m "Release v1.2.0: Description of changes"
git push origin v1.2.0

# 3. GitHub Actions will automatically create a release with APK attached
```

### Version Management

- **versionCode**: Auto-incremented based on Git commit count
- **versionName**: Derived from latest Git tag (format: `v1.2.3`)

To bump version: Create a new Git tag with desired version number.

### Signing Configuration

Release APKs are signed with a keystore stored securely in GitHub Secrets.

**Secrets Required**:
- `KEYSTORE_BASE64`: Base64-encoded release keystore
- `KEYSTORE_PASSWORD`: Keystore password
- `KEY_ALIAS`: Key alias (currently: `scorecount`)
- `KEY_PASSWORD`: Key password

### Troubleshooting

**Build fails with signing error**:
- Verify all 4 secrets are set in GitHub repository settings
- Check keystore hasn't expired: `keytool -list -v -keystore scorecount-release.keystore`

**Version not updating**:
- Ensure you've created and pushed a Git tag: `git tag v1.x.x && git push origin v1.x.x`
- Workflow fetches full Git history (fetch-depth: 0)

**APK not appearing in Releases**:
- GitHub Release only created when Git tag starting with `v` is pushed
- Use manual workflow dispatch for ad-hoc builds (artifact only)
