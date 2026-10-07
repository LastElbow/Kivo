# Releasing

Kivo ships as a signed APK attached to a GitHub Release. There is no Play Store step, and nothing about the process needs a Google account.

The mechanics live in two places: signing is configured in `app/build.gradle.kts`, and `.github/workflows/release.yml` builds, verifies, and publishes on a `v*` tag.

## How signing is wired

`app/build.gradle.kts` reads four environment variables:

| Variable | Contents |
| --- | --- |
| `RELEASE_KEYSTORE_PATH` | Path to the `.jks` file |
| `RELEASE_STORE_PASSWORD` | Keystore password |
| `RELEASE_KEY_ALIAS` | Alias of the key inside the keystore |
| `RELEASE_KEY_PASSWORD` | Password of that key |

With all four set, `:app:assembleRelease` writes a signed `app-release.apk`. With any of them missing it writes `app-release-unsigned.apk`, which no device will install — deliberate, so pull-request and local builds keep working without secrets. The release workflow fails before it builds if a secret is absent, so an unsigned APK can never reach a release.

Nothing in this scheme reads a keystore from the repository; the file is decoded from a secret into the runner's temporary directory for the length of one job.

## One-time setup

### 1. Create the keystore

Run once and keep the result outside the repository (`*.jks` is gitignored, but keeping it out of the tree entirely is better):

```powershell
New-Item -ItemType Directory -Force "$env:USERPROFILE\.kivo" | Out-Null
keytool -genkeypair -v `
  -keystore "$env:USERPROFILE\.kivo\kivo-release.jks" `
  -alias kivo -keyalg RSA -keysize 4096 -validity 10000
```

- `-validity 10000` is about 27 years, comfortably past the **22 October 2033** expiry Google Play requires of an app signing key.
- Enter the **same password** at both prompts: press Enter at the key password prompt to reuse the store password. Play App Signing rejects an upload keystore whose two passwords differ, so matching them now keeps a future Play release possible.
- JDK 9 and later write a PKCS12 keystore by default, which is what the Android plugin expects. No `-storetype` needed.

### 2. Back it up

The keystore is the app's identity: only an APK signed with the same key can update an installed copy. Store the `.jks` and both passwords somewhere offline. Losing them means publishing under a new application id, or telling every user to uninstall and reinstall.

### 3. Add repository secrets

From the repository root (`gh` infers `LastElbow/Kivo` from `origin`):

```powershell
$keystore = "$env:USERPROFILE\.kivo\kivo-release.jks"
$b64 = [Convert]::ToBase64String([IO.File]::ReadAllBytes($keystore))
gh secret set RELEASE_KEYSTORE_BASE64 --body $b64
gh secret set RELEASE_KEY_ALIAS --body kivo
foreach ($name in 'RELEASE_STORE_PASSWORD', 'RELEASE_KEY_PASSWORD') {
    $value = Read-Host "Value for $name"
    if ([string]::IsNullOrWhiteSpace($value)) { throw "refusing to set an empty $name" }
    gh secret set $name --body $value
}
gh secret list
```

Base64 is only there to move a binary file through a text field; it is not protection. The passwords are read by `Read-Host` and passed with `--body`, so they never reach the command line or the shell history.

**Never set a password with a bare `gh secret set NAME`.** With no value argument `gh` reads stdin, and when stdin is not a terminal — a pipe, a redirect, a linter or script runner, some integrated terminals — it stores an **empty** secret and still exits `0`. `gh secret list` shows the name and nothing else, so the mistake only surfaces on the next release, as `Repository secrets not set: RELEASE_STORE_PASSWORD RELEASE_KEY_PASSWORD` from the guard step. If the guard names a secret you believe you set, re-set it with `--body` and dispatch the workflow again.

## Cutting a release

1. Raise `versionCode` in `app/build.gradle.kts` and set `versionName` to the version you are tagging. `versionCode` must increase for every release or devices refuse the update; `versionName` is what users see.
2. Produce a local release build with the keystore variables from "Signing locally" below, install that APK and open the app. Release builds are minified by R8, and a keep-rule mistake — a stripped class that reflection still asks for — only shows up at runtime, so `:app:check` cannot catch it. (Skipping this is why the first release was verified by hand: the optimized APK was 3.1 MB against 25.5 MB unminified, and Home, Accounts and Settings all ran.)
3. Commit and push to `main`, and let CI go green.
4. Tag and push:

   ```powershell
   git tag -a v1.0.0 -m "Kivo 1.0.0"
   git push origin v1.0.0
   ```

The tag starts `.github/workflows/release.yml`. Its contract is: nothing unsigned or untested gets published. It fails fast when a secret is missing, builds only if lint, ktlint and the tests pass, then verifies the APK two ways — a signature check with `apksigner` and a check that the APK's `versionName` matches the tag — before attaching the APK to the GitHub Release (re-uploading with `--clobber` on a retry) and keeping the R8 `mapping.txt` as a workflow artifact. The step-by-step lives in the workflow file itself; this paragraph states the guarantee, so the two cannot drift apart on details.

The `r8-mapping-<tag>` artifact is downloadable by anyone who can read the repository, and this repository is public — treat the mapping as published alongside the APK. That is the point: an obfuscated stack trace from a user needs that exact file with `retrace` before it reads as anything.

To release an existing tag, or to retry after a failure, run the workflow by hand:

```powershell
gh workflow run Release -f tag=v1.0.0
```

The job checks out the tag, so everything it needs has to exist at that commit. Retrying a tag cut before a change to the release job fails — the error names the missing file, commonly a local action added later (`Can't find 'action.yml' ... Did you forget to run actions/checkout?`). Cut a new tag instead of re-releasing one that predates the job's own tooling.

The job needs no manual approval. To inspect an APK before it goes public, add `--draft` to the `gh release create` line in the workflow.

## Signing locally

The same four variables make a local release build, which is useful for installing a release build on a device without going through a tag:

```powershell
$env:RELEASE_KEYSTORE_PATH = "$env:USERPROFILE\.kivo\kivo-release.jks"
$env:RELEASE_KEY_ALIAS = 'kivo'
$env:RELEASE_STORE_PASSWORD = Read-Host 'Keystore password'
$env:RELEASE_KEY_PASSWORD = Read-Host 'Key password'
.\gradlew.bat :app:assembleRelease
```

Drop the variables and the same command produces an APK you cannot install — that is the intended signal that signing was not configured, not a broken build.

## If you publish to Play later

Enrol in Play App Signing and choose **use the existing key** with this same keystore. Google then re-signs with it, so a Play install and a GitHub install share one identity and update each other. If Google generates a fresh key instead, users must uninstall the GitHub build before they can install from Play.
