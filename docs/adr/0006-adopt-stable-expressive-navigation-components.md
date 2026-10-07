# Adopt the stable Material 3 Expressive navigation components

ADR-0005 deferred all of Material 3 Expressive behind the `KivoTheme` seam, on the premise
that Expressive ships only in `material3` `1.5.0-alpha`. That premise holds for Expressive
*theming*: `MaterialExpressiveTheme` and `MotionScheme` are `internal` in the stable
`material3` `1.4.0` we build against, so app code cannot call them. It does not hold for
Expressive *navigation*: `ShortNavigationBar`, `WideNavigationRail`, `AppBarRow` and
`AppBarColumn` are public in `1.4.0` and need no opt-in. We adopt the expressive
navigation components now, keep `MaterialTheme`, and stop short of the alpha.

## Considered options

- Move to `material3:1.5.0-alpha` for `MaterialExpressiveTheme` and expressive motion —
  rejected for now: the same churn ADR-0005 avoided, and the components we need are
  already stable.
- Keep the stock `NavigationBar`/`NavigationRail` — rejected: the expressive components
  are the intended look and cost nothing extra.

## Consequences

- The shell shows a `ShortNavigationBar` on compact windows and a `WideNavigationRail` at
  600dp and wider. The breakpoint is read with `BoxWithConstraints`, not the
  experimental, `Activity`-bound `material3-window-size-class` API.
- `KivoTheme` still wraps `MaterialTheme` with Kivo's shapes and typography; the seam is
  untouched and remains where a future Expressive theming switch would happen.
- Expressive theming and motion, and the alpha-only components (`ButtonGroup`,
  `SplitButton`, `LoadingIndicator`, FAB menu, `MaterialShapes`,
  `expressiveLightColorScheme`), stay out of scope.
- `AppBarRow`/`AppBarColumn` are available but unused until each screen gains its own app
  bar.
