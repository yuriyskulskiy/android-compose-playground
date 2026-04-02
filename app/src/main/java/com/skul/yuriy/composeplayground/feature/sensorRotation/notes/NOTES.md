# Sensor Rotation Notes

- For the rectangular text-host experiment, `requiredSize(...)` helped.
- `size(...)` was still being constrained by the parent layout bounds, so the text kept behaving like it was measured in portrait-width constraints.
- Switching the inner content host to `requiredSize(width, height)` allowed the text container to use the intended debug size.

## Angle Sources

- The feature can track device angle in two different ways:
  - `AccelerometerRotationAngleSource`
  - `OrientationEventRotationAngleSource`
- The compact `raw / lis` switch in the top bar changes not only the source, but the whole paired motion pipeline:
  - `raw` switches to accelerometer source + `smoothAlpha`
  - `lis` switches to orientation-listener source + `animateTo`
- The accelerometer-based source behaves like a raw sensor stream:
  - it is continuous
  - it is noisy
  - it produces float values with very small deltas
- The orientation-listener source behaves like a quantized stream:
  - Android delivers `orientation` as `Int`
  - the source is effectively quantized by whole degrees
  - decimal values visible in the UI appear only after smoothing

## Smoothing Strategies

- Noise suppression / smoothing is also split into two strategies:
  - `AlphaRotationAngleSmoother`
  - `AnimatedRotationAngleSmoother`
  - `SpringLockRotationAngleSmoother` (experimental)

### Alpha Smoothing

- Alpha smoothing uses the classic low-pass formula:
  - `new = previous + (target - previous) * alpha`
- In other words, raw sensor mode does not jump directly to the new angle.
- It moves toward the latest value smoothly using:
  - `smoothed = previous + (raw - previous) * alpha`
- `alpha` controls how strongly the new raw angle affects the output:
  - `alpha = 1` means no smoothing, the output becomes the raw angle immediately
  - `alpha = 0.1` means very strong smoothing and much slower motion
- Smaller `alpha`:
  - smoother output
  - larger lag
- Larger `alpha`:
  - faster response
  - more visible noise
- This strategy works best with many small updates.
- In practice it matches the accelerometer-based raw sensor stream better:
  - the stream is continuous
  - neighboring values are very close to each other
  - alpha smoothing can compress that fine-grained noise very naturally

### Animated Smoothing

- The alternative strategy uses `Animatable` and animates the current angle to the latest target angle.
- In listener mode this is the default paired behavior:
  - source = `OrientationEventRotationAngleSource`
  - smoothing = `AnimatedRotationAngleSmoother`
- A broken version lagged heavily when every new sensor event:
  - started a new outer `Job`
  - canceled the previous job manually
- That produced very poor behavior for dense angle streams.
- The better version keeps a single `Animatable` alive and feeds new targets directly into it.
- This approach works noticeably better.
- In practice it also behaves best with the listener-based angle source:
  - `angleListener + animateTo`
  - because that source is calmer and less noisy than the accelerometer stream
- This strategy works better with larger discrete steps than alpha smoothing.
- In practice that means it pairs well with the listener source:
  - fewer updates
  - larger quantized angle steps
  - `Animatable` can interpolate those steps into smooth visual motion
- In this mode angle anchoring is built as:
  - hysteresis around canonical angles
  - then smooth convergence to the chosen anchor via `animateTo(anchor)`

## Event Stream Filtering

- Angle events should be filtered before entering the smoother.
- Two important operators:
  - `distinctUntilChanged()`
  - `conflate()`
- `distinctUntilChanged()` removes duplicate consecutive values.
- `conflate()` keeps only the latest angle when events arrive faster than the consumer can process them.
- In practice this means:
  - the buffer effectively stores only the freshest value
  - outdated intermediate values are dropped
  - the smoother always works with the latest relevant angle

## Rhombus Text

- A custom text path was added for rhombus / parallelogram rotation shapes.
- The regular Compose `Text` works for rectangular hosts, but it cannot correctly lay out text inside rhombus-like shapes.
- For rhombus cases the feature now uses a custom text implementation that:
  - recalculates line placement for the current shape geometry
  - shifts each next line left or right depending on the current slant of the shape
  - keeps line width constant while changing the start position of each line
- This text is not a single paragraph.
- Internally it uses many paragraph fragments:
  - one paragraph per visual line
  - effectively `paragraph count = rendered line count`
- This makes it possible to keep the text visually inside rhombus-like shapes while the shape changes during rotation.

## Angle Anchoring

- Anchoring logic is now separated from generic smoothing under the `anchoring` package.
- The current shared helper is:
  - [SnapAnchorSupport.kt](/Users/yuriyskulskiy/playground/app/src/main/java/com/skul/yuriy/composeplayground/feature/sensorRotation/anchoring/SnapAnchorSupport.kt)
- It contains:
  - canonical anchor lookup
  - angle normalization for anchoring math
  - hysteresis thresholds for enter / exit / settle
- The current anchoring model uses hysteresis:
  - enter the anchor zone with a smaller threshold
  - leave it only through a wider threshold
- This avoids rapid toggling near the boundary of a canonical angle zone.
- The current implementation also avoids an instant visual jump:
  - entering the anchor zone does not immediately replace `9.9°` with `0°`
  - instead, the smoother switches its target to the anchor
  - the angle approaches that anchor smoothly
  - only after settling does it become a fully snapped/anchored value

### Current Practical Options

- `smoothAlpha`
  - best with dense, noisy raw sensor streams
  - uses alpha smoothing in free motion
  - uses the same alpha approach to converge toward anchors
- `animateTo`
  - best with quantized listener angles
  - uses `Animatable` in free motion
  - uses the same animation mechanism to converge toward anchors
- `springLock`
  - experimental physical-style alternative
  - adds a spring phase before hard lock

### More Advanced Options

- The current anchoring is based on hysteresis + target switching.
- More advanced variants are still possible:
  - nonlinear angle distortion like `easeDegrees`
  - hybrid soft-attraction plus tiny hard snap
  - spring + hysteresis + hard lock around canonical angles
  - full value remapping / distortion around anchors
