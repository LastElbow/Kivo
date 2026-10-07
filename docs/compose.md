# Compose and Material3 at the pinned versions

Kivo resolves Compose through the BOM in `gradle/libs.versions.toml` (Material3 1.4.0 today). Part of the Material 3 expressive surface is `internal` at these versions, so an API from the online docs can be unreachable from app code, and a component default can surprise. Check the resolved source before designing around a component.

## Check an API offline

The sources Gradle resolved are already on disk:

```powershell
$m3 = "$env:USERPROFILE\.gradle\caches\modules-2\files-2.1\androidx.compose.material3\material3-android"
$jar = Get-ChildItem $m3 -Recurse -Filter "material3-android-*-sources.jar" | Select-Object -First 1
$src = Join-Path $env:TEMP "m3src"
Remove-Item -Recurse -Force $src -ErrorAction SilentlyContinue
Copy-Item $jar "$src.zip"
Expand-Archive "$src.zip" $src
Get-ChildItem $src -Recurse -Filter "AppBar.kt" | Select-String -Pattern "fun LargeTopAppBar"
```

Swap the module path for `androidx.compose.foundation`, `androidx.compose.ui` and the rest: each published module keeps a `*-sources.jar` beside its AAR.

## Verified against 1.4.0

- **App bars.** `TopAppBar`, `MediumTopAppBar`, `LargeTopAppBar`, `AppBarRow` and `AppBarColumn` are public. `MediumFlexibleTopAppBar` and `LargeFlexibleTopAppBar` are `internal`, so the public large app bar is `LargeTopAppBar` — what Home and Settings use.
- **List rows.** `ListItemDefaults.colors()` resolves its container colour from `ListTokens.ListItemContainerColor`, the opaque `surface` role, which paints over any container wrapped around the row. Give the row `ListItemDefaults.colors(containerColor = Color.Transparent)` and the container shows through, as Accounts and Home both do.
- **Theme and motion.** `MaterialExpressiveTheme`, `MotionScheme`, the expressive colour schemes, the emphasized type getters and `MaterialShapes` are `internal`; `KivoTheme`, `KivoMotion` and `KivoType` hand-author them instead (ADR-0005, ADR-0007).
