# Sensor Rotation Notes

- For the rectangular text-host experiment, `requiredSize(...)` helped.
- `size(...)` was still being constrained by the parent layout bounds, so the text kept behaving like it was measured in portrait-width constraints.
- Switching the inner content host to `requiredSize(width, height)` allowed the text container to use the intended debug size.

## Angle Sources

- The feature can track device angle in two different ways:
  - `AccelerometerRotationAngleSource`
  - `OrientationEventRotationAngleSource`
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

### Alpha Smoothing

- Alpha smoothing uses the classic low-pass formula:
  - `new = previous + (target - previous) * alpha`
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
