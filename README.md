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

---

### Jetpack Compose: Gooey (Metaball) Interaction Using AGSL Shader and Blur - Fixing Coloring Issues

A small follow-up to the previous gooey AGSL work focused on fixing coloring issues. The key takeaways are that AGSL should always be clipped to the intended bounds so it does not affect content outside the target area, and the shader should explicitly return the target color for the semi-transparent metaball region instead of relying on the original blurred input color.

<img src="docs/gifs/color_issue.gif" alt="Gooey metaball coloring issue fix demo" width="300" />

Published in: [Medium](https://medium.com/@yuriyskul/jetpack-compose-gooey-metaball-interaction-using-agsl-shader-and-blur-fixing-coloring-issues-c70affdcde0f)

Code: [ExampleRuntimeRenderEffectColorFix.kt](app/src/main/java/com/skul/yuriy/composeplayground/feature/gooey/blurConcept/ExampleRuntimeRenderEffectColorFix.kt)

---

### Compose Gooey (Metaball) Button Effect: Fixing Blur Mask Issues on Pre-Android 10 Devices

A legacy-device workaround for gooey metaball buttons on pre-Android 10. `BlurMaskFilter` is replaced with a circular gradient, while the metaball filtering still relies on the legacy Android color-filter pipeline.

<img src="docs/gifs/legacy_metaball.gif" alt="Legacy metaball demo" width="300" />

Published in: [Medium](https://medium.com/@yuriyskul/compose-gooey-metaball-button-effect-fixing-blur-mask-issues-on-pre-android-10-devices-with-d8f3fadc2680)

Code: [ExampleLegacySolution.kt](app/src/main/java/com/skul/yuriy/composeplayground/feature/gooey/blurConcept/ExampleLegacySolution.kt)

---

### Gooey (Metaball) Effects on Android with Jetpack Compose: Blurring and Alpha Filtering Concepts Across All API Levels

A cross-API overview of gooey (metaball) effects in Jetpack Compose. All versions are based on the same idea: blur first, then filter by alpha. The API 33+ version uses AGSL Runtime Shader, the API 31+ version uses RenderEffect for blur and color filtering, and the pre-31 version falls back to Paint with BlurMaskFilter.

<img src="docs/gifs/metaball_across_api.gif" alt="Metaball across API levels demo" width="300" />

Published in: [Medium](https://medium.com/@yuriyskul/gooey-metaball-effects-on-android-with-jetpack-compose-blurring-and-alpha-filtering-concepts-63f1cf879257)

Code: [feature/gooey/blurConcept](app/src/main/java/com/skul/yuriy/composeplayground/feature/gooey/blurConcept)

---

### Animated Glowing Circular Button

A complete Jetpack Compose implementation that combines glow, blur, and gradient-shadow techniques into an animated circular button. The rotating circular border is driven by an animated angle, the press state is rendered with a clipped halo gradient, and the inner drop shadow is implemented as a second blurred icon layer with a dynamic offset derived from the current rotation angle.

<img src="docs/gifs/circular_snake_border.gif" alt="Animated glowing circular button demo" width="300" />

Published in: [Medium](https://medium.com/@yuriyskul/making-an-animated-glowing-circular-button-with-blur-and-gradient-shadows-in-jetpack-compose-72ffe9a3169d)

Code: [feature/animatedCircularButton](app/src/main/java/com/skul/yuriy/composeplayground/feature/animatedCircularButton)

---

### Jetpack Compose: Creating Direct and Spread Light Shadows for Vector Drawables (API 12+)

A Jetpack Compose implementation for direct and spread vector shadows built from a second duplicated icon layer. The shadow is created by blurring that extra layer, then applying an offset for direct drop shadow, or adding scale for spread shadow around the original icon.

<p align="left">
  <img src="docs/gifs/direct_drop_shadow.gif" alt="Direct drop shadow demo" width="300" />
  <img src="docs/gifs/spread_shadow.gif" alt="Spread shadow demo" width="300" />
</p>

Left: Direct drop shadow. Right: Spread shadow.


Published in: [Medium](https://medium.com/@yuriyskul/jetpack-compose-creating-direct-and-spread-light-shadows-for-vector-drawables-api-12-230362982a0f)

Code: [DropShadowSection.kt](app/src/main/java/com/skul/yuriy/composeplayground/feature/vectorIconShadow/DropShadowSection.kt), [SpreadHaloShadowSection.kt](app/src/main/java/com/skul/yuriy/composeplayground/feature/vectorIconShadow/SpreadHaloShadowSection.kt)

---

### Animating Circular Shadow Borders in Jetpack Compose: Rotating Arcs with Blur and Gradient Effects

A fully customized Jetpack Compose rotating circular border built from a sweep-gradient arc body and a blurred glowing arc layer. The border rotation is animated independently from the inner content, while arc width, blur radius, padding behavior, and glow styling remain fully configurable.

<img src="docs/gifs/animated_circular_border.gif" alt="Animated circular border demo" width="270" />

Published in: [Medium](https://medium.com/@yuriyskul/animating-circular-shadow-borders-in-jetpack-compose-rotating-arcs-with-blur-and-gradient-effects-4e4288daf7bf)

Code: [feature/rotationArk](app/src/main/java/com/skul/yuriy/composeplayground/feature/rotationArk)

---

### Metaballs in Jetpack Compose: Classic Math Approach Using Field Function 1/distance²

This article demonstrates the classic metaball approach in Jetpack Compose using the field function `1 / distance²` to calculate the metaball shape. The implementation relies on Compose `Canvas` for precise rendering and dynamic animation.

<img src="docs/gifs/classic_metaball_formula.gif" alt="Classic metaball formula demo" width="300" />

Published in: [Medium](https://medium.com/@yuriyskul/metaballs-in-jetpack-compose-classic-math-approach-using-field-function-1-distance%C2%B2-37f86738db30)

Code: [feature/metaballClassic](app/src/main/java/com/skul/yuriy/composeplayground/feature/metaballClassic)

---

### Metaball Blur RenderEffects: Solid Gooey Background and Transparent Outline Upgrade

Two related Jetpack Compose solutions built on the same `feature/metaballBlur` foundation:

- Solid metaball background: a basic gooey effect built with a chained blur and color-filter `RenderEffect`.
- Transparent outline metaball: an upgraded version that replaces the linear color filter with a chained blur + AGSL runtime `RenderEffect` to isolate only the outline border on a transparent background.

<p align="left">
  <img src="docs/gifs/meteball_blur_basic.gif" alt="Solid metaball blur demo" width="356" />
  <img src="docs/gifs/metaball_blur_outline.gif" alt="Transparent outline metaball demo" width="356" />
</p>

Left: solid metaball background. Right: transparent outline metaball.

Published with 2 parts:
- [Building a Metaballs (Gooey) Animation in Android Compose Using RenderEffect API](https://medium.com/@yuriyskul/building-a-metaballs-gooey-animation-in-android-compose-using-rendereffect-api-120b8f8465da)
- [Outline Metaball Implementation with Blur and AGSL Runtime RenderEffects](https://medium.com/@yuriyskul/chaining-blur-and-agsl-runtime-rendereffects-to-create-transparent-metaball-backgrounds-with-15680ad1e1a9)

Code: [feature/metaballBlur](app/src/main/java/com/skul/yuriy/composeplayground/feature/metaballBlur)

---

### How to Track/Detect Sticky Header Stuck State

How to detect when a sticky header is already at the top and is truly in the stuck state, so you can apply shadow, elevation, and a lifted-on-top visual state above the content. The solution has two criteria:

- The tracked header must be the first visible item and already aligned to the top: `header.key == key && header.offset == 0`
- The next visible element must still be positioned under that header: `header.size > nextElement.offset`

<img src="docs/gifs/sticky_header.gif" alt="Sticky header stuck state demo" width="300" />

Published in: [Medium](https://medium.com/@yuriyskul/how-to-track-and-detect-sticky-headers-stuck-states-in-jetpack-compose-f4f2499f2ae8)

Code: [feature/stickyHeader](app/src/main/java/com/skul/yuriy/composeplayground/feature/stickyHeader)

---

### Drawing Bottom-Edge Shadow only in Jetpack Compose

A Jetpack Compose solution for drawing shadow only on the bottom edge of a composable instead of all sides. It uses the regular Android shadow API together with Compose `clipPath` intersection to keep the built-in shadow behavior while restricting the visible shadow area to the bottom edge.

<img src="docs/gifs/bottom_edge_shadow.webp" alt="Bottom edge shadow demo" width="300" />

Published in: [Medium](https://medium.com/@yuriyskul/drawing-bottom-edge-shadow-only-in-jetpack-compose-1503d05bc9f6)

Code: [feature/bottomEdge](app/src/main/java/com/skul/yuriy/composeplayground/feature/bottomEdge)

---

### Different Approaches to Create Android-Style Shadows with Transparent Containers in Jetpack Compose

The problem is drawing a transparent container with the native `.shadow()` modifier. As soon as the container becomes transparent, the default Android shadow is drawn not only around the shape but also produces visible artifacts under the container. In the demo, tapping the default shadow example toggles the container between opaque and transparent states, making that glitch clearly visible.

<p align="left">
  <img src="docs/gifs/custom_shadow_like_nativ.gif" alt="Transparent container shadow comparison demo" width="300" />
  <img src="docs/gifs/transparent_conteiner_glitch.webp" alt="Transparent container shadow glitch still frame" width="300" />
</p>

Published in: [Medium](https://medium.com/@yuriyskul/different-approaches-to-create-android-style-shadows-with-transparent-containers-in-jetpack-compose-e299a215557e)

Code: [feature/shadowBox](app/src/main/java/com/skul/yuriy/composeplayground/feature/shadowBox)

---

### Applying Smooth Gradient Edges for Scrollable Content in Jetpack Compose

A Jetpack Compose solution for elegant fading edges on scrollable content with a single modifier, even when the screen has an image, shader, or other complex background. Instead of drawing gradient overlays above the whole screen, this approach uses `graphicsLayer` with `BlendMode.DstIn` so the fade is applied only to the drawn content and does not darken the background itself.

<img src="docs/gifs/fading_edges_screen.gif" alt="Fading edges scroll demo" width="300" />

How to use `fadingTopBottomEdgesDp`:

```kotlin
LazyColumn(
    modifier = Modifier
        .fillMaxSize()
        .fadingTopBottomEdgesDp(
            topFadeHeight = 16.dp,
            bottomFadeHeight = 16.dp
        )
        .clipToBounds()
) {
    // content
}
```

Published in: [Medium](https://medium.com/@yuriyskul/applying-smooth-gradient-edges-for-scrollable-content-in-jetpack-compose-814a10fec8ca)

Code: [feature/scrollEdge](app/src/main/java/com/skul/yuriy/composeplayground/feature/scrollEdge), [FadeTopBottomEdge.kt](app/src/main/java/com/skul/yuriy/composeplayground/util/FadeTopBottomEdge.kt)

---

### Shadow Halo Borders for Circular Shapes in Jetpack Compose

A Jetpack Compose implementation of a shadow halo border for circular shapes with a transparent inner area left open for content. The feature includes two different solutions that produce a very similar visual result: `BlurMaskFilter` for a blurred glow and `RadialGradient` for a smooth gradient-based halo.

<img src="docs/gifs/radial_halo_border.gif" alt="Radial halo border demo" width="300" />

Published in: [Medium](https://medium.com/@yuriyskul/shadow-halo-effect-with-transparent-outline-borders-for-circular-shapes-in-jetpack-compose-using-c2d03562e17b)

Code: [feature/animatedBorder](app/src/main/java/com/skul/yuriy/composeplayground/feature/animatedBorder), [drawOutlineHaloShadowBlur](app/src/main/java/com/skul/yuriy/composeplayground/util/shadowborder/CircleHaloShadow.kt), [drawOutlineCircularShadowGradient](app/src/main/java/com/skul/yuriy/composeplayground/util/shadowborder/CircleHaloShadow.kt)

---

### Android Jetpack Compose: How to Add Parallax Scrolling to Lazy Lists

A Jetpack Compose parallax implementation for `LazyColumn`, where each item tracks its position inside the viewport through `LazyListState`. Based on that normalized scroll progress, the inner content is shifted vertically up or down, creating the parallax effect while the card itself stays in place.

<img src="docs/gifs/paralax_scroll_list.gif" alt="Parallax scroll list demo" width="300" />

Published in: [Medium](https://medium.com/@yuriyskul/android-jetpack-compose-how-to-add-parallax-scrolling-to-lazy-lists-e617ed7f4d00)

Code: [feature/parallax](app/src/main/java/com/skul/yuriy/composeplayground/feature/parallax)
