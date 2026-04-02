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

## Fake Status Bar

- Android does not give normal app UI full visual control over the real system status bar contents.
- For a strong custom-screen effect, the real system bar can be partially hidden and replaced visually by app-owned UI.
- A common window flag for that approach is:
  - `WindowCompat.setDecorFitsSystemWindows(window, false)`
- After that, the app can draw its own top chrome and fake status bar inside edge-to-edge content.
- In this project the fake status bar is used mostly for fun and for the visual completeness of the rotation host.
- It currently shows real runtime information:
  - current time
  - battery percentage
  - charging state
  - Wi‑Fi state
- It is still only a fake status bar:
  - the real system status bar remains a system-controlled surface
  - the app only imitates its appearance inside app content

## Startup Angle

- There is a startup problem on this screen:
  - when the screen opens while the phone lies flat, the raw accelerometer source may emit nothing
  - waiting for the first real sensor angle can leave the screen blank
- The current practical solution is:
  - keep `SensorRotationActivity` portrait-locked in the manifest
  - read the opening display rotation before the new screen starts rotating itself
  - pass that raw display rotation into `SensorRotationActivity` through an `Intent` extra
  - convert it into a simple startup fallback angle:
    - portrait family -> `0°`
    - landscape family -> `±90°`
  - use that fallback immediately on first composition
  - when the first real sensor angle arrives, the smoother snaps to it immediately
  - after that, normal `smoothAlpha` / `animateTo` behavior continues
- This avoids:
  - black screen on startup
  - waiting for the first sensor event while the device is flat
  - startup blink caused by reading `display.rotation` too late from inside the already portrait-locked activity
- The important detail is:
  - the opening rotation is captured in the previous screen before `SensorRotationActivity` starts
  - if it is read only inside the new activity, Android may already have forced the new window into portrait semantics
  - that leads to wrong startup fallback and visible blink

### More Serious Alternative

- A more advanced approach would be:
  - do not hardcode portrait in the manifest
  - open the activity in the same orientation in which it was launched
  - determine that opening orientation in `onCreate`
  - then lock the activity dynamically to that exact quarter-turn orientation
- That would reduce some system-level startup rotation issues, but it is much more complex.
- With that approach, the whole feature would need to support multiple base window orientations:
  - portrait
  - reverse portrait
  - landscape
  - reverse landscape
- In practice that means many geometry paths would need to become orientation-aware:
  - rotation host measurements
  - top bar and fake status bar insets
  - bottom action panel placement
  - rhombus text line shifting
  - all rotation pattern calculators
- So the current project uses the simpler pragmatic approach:
  - keep the activity portrait-based
  - rotate content inside that portrait host
  - use startup fallback angle only to avoid bad first-frame behavior

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

## Rotated Scroll And Fling

- A separate bug appeared after moving scroll state into the child text composables.
- Manual drag scrolling still felt correct, but fling became inverted in these angle ranges:
  - `90..180`
  - `-180..-90`
- The reason is:
  - the scrollable content is rotated visually with the host
  - drag still feels mostly correct because pointer movement and content movement stay visually coupled
  - but fling uses inertial velocity in the scrollable content's local coordinate system
  - in the "upside-down" quadrants that local vertical direction no longer matches the vertical direction the user sees on screen
- So the problem was not regular scroll itself, but fling velocity interpretation after rotation.
- The fix is:
  - keep normal drag scroll behavior unchanged
  - detect whether the current host angle belongs to an inverted quadrant
  - only for fling, invert the initial velocity sign before delegating to the default Compose fling behavior
- This logic is now isolated in:
  - [RotationScrollDirection.kt](/Users/yuriyskulskiy/playground/app/src/main/java/com/skul/yuriy/composeplayground/feature/sensorRotation/scroll/RotationScrollDirection.kt)
  - [RotationAwareFlingBehavior.kt](/Users/yuriyskulskiy/playground/app/src/main/java/com/skul/yuriy/composeplayground/feature/sensorRotation/scroll/RotationAwareFlingBehavior.kt)
- The custom fling behavior is applied to both text paths:
  - rectangular text
  - rhombus text
- The inclusive boundaries were chosen intentionally:
  - `90`, `180`, `-90`, `-180`
  - because those edge angles already belong to the same flipped local-axis case

## Angle Anchoring

- Anchoring logic is currently shared inside the `smoothing` package.
- The current shared helper is:
  - [SnapAnchorSupport.kt](/Users/yuriyskulskiy/playground/app/src/main/java/com/skul/yuriy/composeplayground/feature/sensorRotation/smoothing/SnapAnchorSupport.kt)
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
