# Metaball Bridge Solution (Bezier Conjugation)

Status: current accepted baseline, February 28, 2026.
Previous variants are deprecated.

## Idea
- Two static circles (left/right), no circle motion.
- Bridge is a Bezier-conjugated shape between circles (no sharp center kink).
- Curvature animation imitates moving virtual equal-distance center `OX` closer:
  - `OX` far -> weak concavity
  - `OX` near -> stronger concavity and near-1px center neck
- Final phase includes explicit bridge break (alpha to zero near the end of cycle).

## Files
- Vector geometry: `app/src/main/res/drawable/ic_playground_vector.xml`
- AVD mapping: `app/src/main/res/drawable/ic_playground_avd.xml`
- Curvature animation: `app/src/main/res/animator/mb_bridge_curvature.xml`
- Break timing (alpha): `app/src/main/res/animator/mb_bridge_break_alpha.xml`
- Demo usage in activity: `app/src/main/java/com/skul/yuriy/composeplayground/MainActivity.kt`

## Notes
- Bridge is animated via `pathData` morph (`valueFrom`/`valueTo`) with `repeatMode="reverse"`.
- Keep identical path command structure between `valueFrom` and `valueTo`.
