# Compose Playground

![Android](https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=flat&logoColor=white)
![AGSL](https://img.shields.io/badge/AGSL-Shaders-222222?style=flat)

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

Published in: <img src="docs/gifs/logo/proAndroidDevLogo.png" alt="ProAndroidDev" width="14" /> [ProAndroidDev](https://proandroiddev.com/jetpack-compose-animated-snake-border-for-rectangle-shapes-31de5e9ef713)

Code: [feature/animatedRectButton](app/src/main/java/com/skul/yuriy/composeplayground/feature/animatedRectButton)

---

### Glowing Border Rect

A Jetpack Compose experiment that explores multiple ways to draw glowing rectangle borders and compares them with HWUI profiling.

0. PNG borders with crossfade on tap
1. Multi-layering in Canvas with different sizes
2. Paint with `BlurMaskFilter`
3. Blur using `RenderEffect`
4. Applying `Paint.setShadowLayer()` multiple times
5. Circular gradient for rounded corners + linear gradients for the four sides
6. Custom AGSL applied via `RuntimeEffect` or as a Canvas shader
7. Advanced AGSL originally prototyped by me in GLSL on [ShaderToy](https://www.shadertoy.com/view/fcfGzH)

<p align="left">
  <img src="docs/gifs/animated_border_rect/0.gif" alt="Glowing Border Rect demo 0" width="150" />
  <img src="docs/gifs/animated_border_rect/1.gif" alt="Glowing Border Rect demo 1" width="150" />
  <img src="docs/gifs/animated_border_rect/2.gif" alt="Glowing Border Rect demo 2" width="150" />
  <img src="docs/gifs/animated_border_rect/3.gif" alt="Glowing Border Rect demo 3" width="150" />
</p>

<p align="left">
  <img src="docs/gifs/animated_border_rect/4.gif" alt="Glowing Border Rect demo 4" width="150" />
  <img src="docs/gifs/animated_border_rect/5.gif" alt="Glowing Border Rect demo 5" width="150" />
  <img src="docs/gifs/animated_border_rect/6.gif" alt="Glowing Border Rect demo 6" width="150" />
  <img src="docs/gifs/animated_border_rect/7.gif" alt="Glowing Border Rect demo 7" width="150" />
</p>

Published in: <img src="docs/gifs/logo/proAndroidDevLogo.png" alt="ProAndroidDev" width="14" /> [ProAndroidDev](https://proandroiddev.com/how-many-ways-do-you-know-to-draw-a-glowing-border-in-jetpack-compose-57980d049562)

Code: [feature/animatedBorderRect](app/src/main/java/com/skul/yuriy/composeplayground/feature/animatedBorderRect)
