# Hand-author Kivo's brand scheme, expressive type, and motion on stable Material 3

Stable `material3` keeps the expressive theming surface `internal`: `expressiveLightColorScheme`,
`expressiveDarkColorScheme`, `MaterialExpressiveTheme` and `MotionScheme` cannot be called from app
code, and neither can the emphasized type getters or the 30-argument `Typography` constructor
(ADR-0005, ADR-0006). Rather than move to the `1.5.0-alpha` line, Kivo writes its own brand palette
onto the public `ColorScheme` roles and hand-authors the emphasized type styles and motion springs
beside `MaterialTheme`. The palette is a design decision rather than Material's tonal algorithm, so
every role is set explicitly — light on tone 40/90/98, dark on tone 80/30/10, with a static `error`.
The emphasized set keeps each baseline style's size and line height and changes **only** weight and
tracking: display/headline/title-large/body go Regular → Medium, title-medium/title-small and every
label go Medium → Bold. The motion springs reuse Material's published Expressive values —
`0.8 / 380` for spatial default, `0.6 / 800` for spatial fast, `1.0 / 1600` for effects — so the
geometry springs may bounce while colour and opacity never overshoot. Dynamic colour is off by
default and stays an opt-in that Settings will expose.

## Considered options

- Move to `material3:1.5.0-alpha` for the expressive theme, colours and motion — rejected for now:
  it is the same API churn ADR-0005 avoided, and the values are copyable onto the stable API.
- Keep Material's stock colours and type — rejected: Kivo would ship baseline purple and unmodified
  Roboto, which the redesign exists to replace.

## Consequences

- Every colour role is set in `ui/theme/Color.kt`; there are no generated tones to regenerate when
  Material's algorithm changes, and an edit that breaks a listed contrast pair fails `ColorSchemeTest`.
- Emphasized styles are reached through a `CompositionLocal` (`KivoType.emphasized`) instead of living
  on `Typography`, because the emphasized getters and the wide constructor are `internal`.
- Motion is applied per animation through `KivoMotion`; Material's components keep their built-in
  motion until the seam flips to a real expressive theme.
- Money can opt into tabular figures through `TextStyle.withTabularFigures()`, so amounts do not
  shift as their digits change.
