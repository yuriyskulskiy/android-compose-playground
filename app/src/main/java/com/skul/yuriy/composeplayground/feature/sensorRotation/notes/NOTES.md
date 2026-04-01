# Sensor Rotation Notes

- For the rectangular text-host experiment, `requiredSize(...)` helped.
- `size(...)` was still being constrained by the parent layout bounds, so the text kept behaving like it was measured in portrait-width constraints.
- Switching the inner content host to `requiredSize(width, height)` allowed the text container to use the intended debug size.
