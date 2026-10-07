# Start on stable Material 3; adopt Material 3 Expressive through a theme seam

Kivo's intended look is Material 3 Expressive, but as of this decision the Expressive
APIs ship only in `androidx.compose.material3:1.5.0-alpha` — stable `material3` is
`1.4.0`, and Google has not yet stabilised Expressive. We build on stable Material 3
and route all theming through a single `KivoTheme` composable, so that switching to
`MaterialExpressiveTheme` and expressive motion later is a contained change rather than
a sweeping one.

## Considered options

- Depend on `1.5.0-alpha` now — rejected: the alpha has already renamed and removed
  public APIs, which is churn we do not want under a foundational build.
- Hybrid — deferred: worth revisiting per-component once the seam exists.

## Consequences

- Expressive components (button groups, FAB menus, wavy progress, expressive motion)
  are not available until we flip the seam.
- The seam is the single place that knows which theming API is in use.
