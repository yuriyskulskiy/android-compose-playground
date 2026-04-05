# Compose Playground

<p align="left">
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/android/android-original.svg" alt="Android" width="28" height="28" />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/kotlin/kotlin-original.svg" alt="Kotlin" width="28" height="28" />
  <img src="https://upload.wikimedia.org/wikipedia/commons/4/4c/Jetpack_Compose_logo.png" alt="Jetpack Compose" width="28" height="28" />
</p>

<details>
<summary><strong>About</strong></summary>

Medium profile: https://medium.com/@yuriyskul

This repository is a collection of non-trivial Jetpack Compose UI/graphics experiments.

Each feature is centered around a specific Compose problem and an implementation built to solve or explore it.

</details>

<details>
<summary><strong>Scope</strong></summary>

This project is intentionally focused on the UI layer only.

It does not try to demonstrate production architecture, modularization, domain/data layers, or a complete app structure. The main focus is stateless or near-stateless UI implementation and rendering techniques.

It also does not follow one custom design system or one visual specification. That is intentional: this repository collects very different Jetpack Compose problems, and each feature uses the visual approach that best exposes the idea behind the implementation.

</details>

<details>
<summary><strong>Topics</strong></summary>

The project currently explores topics such as:

- AGSL shaders and `RenderEffect`
- gooey / metaball interactions
- transparent metaball outlines
- custom blur and radial blur
- vector drawable shadows
- bottom-edge-only shadows
- sticky header state detection
- parallax and animated controls
- custom Canvas-based rendering

</details>

<details>
<summary><strong>Articles</strong></summary>

All features are described in my Medium articles:

- Medium profile: https://medium.com/@yuriyskul

</details>

<details>
<summary><strong>Tech</strong></summary>

- Kotlin
- Jetpack Compose
- Compose customization
- Animation
- AGSL
- Canvas drawing
- Android graphics / blur / shader experiments

</details>

## Feature Catalog

### Sensor Rotation

A Jetpack Compose experiment that explores sensor-driven rotation, custom shape morphing, and text layout inside rotating non-rectangular containers.

<img src="docs/gifs/sensor_rotation_git.gif" alt="Sensor Rotation demo" width="300" />

Published: not published

Code: [feature/sensorRotation](app/src/main/java/com/skul/yuriy/composeplayground/feature/sensorRotation)

---

### Flow Text

A text-flow experiment focused on overflow behavior and non-standard text layout presentation in Compose.

<img src="docs/gifs/text_overflow_gif.gif" alt="Flow Text demo" width="300" />

Published: not published

---

### Rect Snake Border

A Jetpack Compose animated rectangle border with a snake-style highlight and support for different corner radii.

<img src="docs/gifs/featured_cropped_github.gif" alt="Rect Snake Border demo" width="300" />

Published in: [ProAndroidDev](https://proandroiddev.com/jetpack-compose-animated-snake-border-for-rectangle-shapes-31de5e9ef713)

Code: [feature/animatedRectButton](app/src/main/java/com/skul/yuriy/composeplayground/feature/animatedRectButton)
