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

**Feature Catalog**

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

<img src="docs/gifs/rect_glowing_snake_btn.gif" alt="Rect Snake Border demo" width="300" />

Published in: <img src="docs/gifs/logo/proAndroidDevLogo.png" alt="ProAndroidDev" width="28" /> [ProAndroidDev](https://proandroiddev.com/jetpack-compose-animated-snake-border-for-rectangle-shapes-31de5e9ef713)

Code: [feature/animatedRectButton](app/src/main/java/com/skul/yuriy/composeplayground/feature/animatedRectButton)

---

### Animated Glowing Border

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

Published in: <img src="docs/gifs/logo/proAndroidDevLogo.png" alt="ProAndroidDev" width="28" /> [ProAndroidDev](https://proandroiddev.com/how-many-ways-do-you-know-to-draw-a-glowing-border-in-jetpack-compose-57980d049562)

Code: [feature/animatedBorderRect](app/src/main/java/com/skul/yuriy/composeplayground/feature/animatedBorderRect)

---

### PDE-Based Wave Simulation in Jetpack Compose: Canvas vs AGSL

A Jetpack Compose experiment that compares three ways to build a PDE-based wave effect and highlights where classic Canvas rendering can outperform per-pixel AGSL for simple 1D simulations.

- AGSL `RenderEffect`
- AGSL shader via `Paint`
- A classic Canvas approach

For simple 1D simulations, the classic Android `Path` API with cubic interpolation can outperform per-pixel AGSL rendering.

<img src="docs/gifs/liquid_bar.gif" alt="PDE Wave Simulation demo" width="300" />

Published in: <img src="docs/gifs/logo/proAndroidDevLogo.png" alt="ProAndroidDev" width="28" /> [ProAndroidDev](https://proandroiddev.com/pde-based-wave-simulation-in-jetpack-compose-canvas-vs-agsl-58d52a88f22f)

Code: [feature/liquidBar](app/src/main/java/com/skul/yuriy/composeplayground/feature/liquidBar)

---

### Jetpack Compose Metaball Edge Effect - Final Part

A controlled way to build metaball interaction between scrolling content and screen edges. The core idea is a localized custom blur applied only near the edges, with a dynamically increasing radius as elements approach the boundary. This avoids the typical issue where everything turns into a blob and keeps the effect visually clean and controlled.

<p align="left">
  <img src="docs/gifs/metaball_scroll_vertical_edge.gif" alt="Metaball scroll edge demo" width="300" />
  <img src="docs/gifs/metaball_scroll_vertical_melt.gif" alt="Metaball melt demo" width="300" />
</p>

Gooey edge effect + Melt effect

Vertical scroll code: [feature/metaballEdgesAdvanced](app/src/main/java/com/skul/yuriy/composeplayground/feature/metaballEdgesAdvanced)

<img src="docs/gifs/horizontal_metaball_edges.gif" alt="Horizontal metaball edges demo" width="300" />

Horizontal metaball edges

Horizontal scroll code: [feature/metaballEdgeHorizontalScroll](app/src/main/java/com/skul/yuriy/composeplayground/feature/metaballEdgeHorizontalScroll)

Published in: <img src="docs/gifs/logo/proAndroidDevLogo.png" alt="ProAndroidDev" width="28" /> [ProAndroidDev](https://proandroiddev.com/jetpack-compose-metaball-edge-effect-final-part-ac8a4cc7a425)

---

### AGSL Alpha Blur with Local Regions and Dynamic Radius (Linear and Gaussian, 17/61/101 taps)

A Jetpack Compose experiment focused on dynamic and local alpha blur on Android. Native blur is static and cannot be applied selectively to a local region inside a composable, so this AGSL-based Gaussian alpha blur explores 17-, 61-, and 101-tap kernel quality, selective blur applied only to chosen composable areas, and blur strength that changes with distance.

<p align="left">
  <img src="docs/gifs/custom_blur_liniar.gif" alt="Custom blur linear demo" width="300" />
  <img src="docs/gifs/custom_blur_circular.gif" alt="Custom blur circular demo" width="300" />
</p>

The goal here is applying blur locally, where blur strength gradually changes based on the distance from the center.

Published in: [Medium](https://medium.com/@yuriyskul/agsl-alpha-blur-with-local-regions-and-dynamic-radius-linear-and-gaussian-17-61-101-taps-3a36198c9567)

Code: [feature/customAlphaBlur](app/src/main/java/com/skul/yuriy/composeplayground/feature/customAlphaBlur), [feature/customAlphaBlurRadial](app/src/main/java/com/skul/yuriy/composeplayground/feature/customAlphaBlurRadial)

---

### Text Metaball Scrolling Edges: local overlay with regular Compose `Modifier.blur()`

This approach uses the standard `blur()` API. To avoid blurring the whole screen, the text is additionally covered with top and bottom overlay bands that duplicate the text and its position in blurred form. The scroll offset is synchronized through the scroll state of the main full-screen text. This solution is more of a foreground workaround that demonstrates why a local dynamic blur implementation was needed.

<img src="docs/gifs/text_metaball_scrolling_edges.gif" alt="Text metaball scrolling edges demo" width="300" />

Published in: [Medium](https://medium.com/@yuriyskul/text-metaball-scrolling-edges-local-overlay-with-regular-compose-modifier-blur-23b835242815)

Code: [feature/metaballEdgesRegular](app/src/main/java/com/skul/yuriy/composeplayground/feature/metaballEdgesRegular)

---

### AGSL Text Metaball Scrolling Edges And Text

This feature covers two related ideas at once: the basic metaball concept between the screen edge and a dragging object (circle shape), and the core text metaball behavior, where blur causes the text to merge and interact with itself.

<p align="left">
  <img src="docs/gifs/metaball_circle_to_edge.gif" alt="Metaball circle to edge demo" width="300" />
  <img src="docs/gifs/blur_alpha_filter.gif" alt="Metaball blur alpha filter demo" width="300" />
</p>

<p align="left">
  <img src="docs/gifs/text_metaball_swap.gif" alt="Text metaball swap demo" width="300" />
</p>

Published in: [Medium](https://medium.com/@yuriyskul/agsl-text-metaball-scrolling-edges-getting-started-696d59418127)

Code: [feature/metaballEdgesAndText](app/src/main/java/com/skul/yuriy/composeplayground/feature/metaballEdgesAndText)

---

### Compose AGSL Shader: Gooey Outline Metaball Effect with Transparent Background

This feature explores a transparent-background gooey outline metaball effect in Compose using AGSL and per-element blur. Marker color detection, alpha filtering, and brightness-based transparency are used to preserve the outline and support smooth icon disappearance and reappearance during metaball interaction.

<img src="docs/gifs/supra_metaball.gif" alt="Gooey outline metaball demo" width="300" />

Published in: [Medium](https://medium.com/@yuriyskul/compose-agsl-shader-gooey-outline-metaball-effect-with-transparent-background-cb8b0e72286c)

Code: [ExampleRuntimeRenderEffectOutline.kt](app/src/main/java/com/skul/yuriy/composeplayground/feature/gooey/blurConcept/ExampleRuntimeRenderEffectOutline.kt), [OutlineAgslShader.kt](app/src/main/java/com/skul/yuriy/composeplayground/feature/gooey/blurConcept/util/shader/OutlineAgslShader.kt)
