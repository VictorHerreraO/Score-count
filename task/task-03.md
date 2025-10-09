# Task 03: Configure Application Manifest for Production

## Description

The `AndroidManifest.xml` is using default configurations that are not ideal for a production application. It should be hardened to improve security and define behavior more explicitly.

### Current Behavior

-   The `android:usesCleartextTraffic` attribute is not set, defaulting to `true` on older APIs, which is a security risk.
-   The app uses default, non-explicit backup rules, which could lead to backing up private or unnecessary files.

### Expected Behavior

-   The `AndroidManifest.xml` explicitly disables cleartext traffic by setting `android:usesCleartextTraffic="false"` in the `<application>` tag.
-   The backup rules (`backup_rules.xml` and `data_extraction_rules.xml`) are reviewed and modified to explicitly include only the necessary files for backup (e.g., Room database files and DataStore preferences) and exclude everything else.

## Acceptance criteria

- The `android:usesCleartextTraffic` attribute is present and set to `false`.
- The backup configuration files explicitly define what is included and excluded from backups.
