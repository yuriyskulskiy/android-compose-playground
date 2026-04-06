# README Format

This file defines how catalog items and media should be prepared for the repository `README.md`.

## Purpose

Use this spec to avoid re-explaining:

- where raw videos go
- what media is committed
- how preview assets should be exported
- how each catalog item in `README.md` should be structured

## README Structure

`README.md` contains a feature catalog.

The catalog is a sequence of catalog items separated by:

```md
---
```

Each catalog item describes one feature or one publication-related group of features.

Default ordering rule:

- new catalog items should be inserted at the top of the catalog
- the newest items should appear first
- exception: when backfilling older features or older publications, insert the item where it fits historically, including at the end if needed

## Media Folders

### `docs/gifs/`

Tracked final preview assets used directly by `README.md`.

Put here:

- final GIF previews
- final PNG/JPG images
- final small visual assets referenced from README

Rules:

- only final exported assets
- filenames should be stable
- filenames should be descriptive and feature-related
- `README.md` should reference media from this folder

### `docs/video/`

Local working media folder for source and intermediate files.

Put here:

- raw screen recordings
- uncropped videos
- temporary exports
- editing drafts

Rules:

- this folder is gitignored
- files here are not final documentation assets
- do not reference this folder from committed markdown
- use it as a staging area before exporting the final preview to `docs/gifs/`

## Media Export Rules

Default GIF spec:

- duration: about 3 seconds
- framerate: 30 fps
- width: 300 px
- keep good visual quality after compression

If a static image communicates the result better than a GIF, a regular image is allowed.

## Catalog Item Format

Each catalog item should follow this order:

1. Title
2. Short description
3. Preview media
4. Publication status or publication links
5. Internal code link or links
6. Divider `---`

Base template:

```md
### Feature Title

Short description explaining the idea, experiment, or problem being solved.

<img src="docs/gifs/feature_preview.gif" alt="Feature preview" width="300" />

Published: not published yet

Code: [feature/packageName](app/src/main/java/com/skul/yuriy/composeplayground/feature/packageName)

---
```

## Title Rules

- title should be short and searchable
- description should be short and practical
- description should explain the experiment, not marketing language
- if the item is still being developed, append `(WIP)` to the title

Example:

```md
### Sensor Rotation (WIP)
```

## Preview Layout Rules

Single preview:

```md
<img src="docs/gifs/feature_preview.gif" alt="Feature preview" width="300" />
```

Two previews in one row:

```md
<p align="left">
  <img src="docs/gifs/first.gif" alt="First preview" width="300" />
  <img src="docs/gifs/second.gif" alt="Second preview" width="300" />
</p>
```

If there are more than two previews:

- place only 2 previews per row
- move the next previews to the next row

Example:

```md
<p align="left">
  <img src="docs/gifs/1.gif" alt="Preview 1" width="300" />
  <img src="docs/gifs/2.gif" alt="Preview 2" width="300" />
</p>

<p align="left">
  <img src="docs/gifs/3.gif" alt="Preview 3" width="300" />
</p>
```

## Code Link Rules

Each catalog item should contain an internal code link related to that item.

Preferred format:

```md
Code: [feature/packageName](app/src/main/java/com/skul/yuriy/composeplayground/feature/packageName)
```

This display label should usually be the package path under:

`com.skul.yuriy.composeplayground.feature.*`

If one catalog item maps to multiple feature packages, include multiple links on the same `Code:` line or in the same code block area.

Example:

```md
Code: [feature/firstPackage](app/src/main/java/com/skul/yuriy/composeplayground/feature/firstPackage), [feature/secondPackage](app/src/main/java/com/skul/yuriy/composeplayground/feature/secondPackage)
```

If the code is not yet available in `main`, use:

```md
Code: in progress
```

## Publication Rules

If the item has a publication, specify where it was published and include the link.

Allowed common labels:

- `Published in: [Medium](...)`
- `Published in: <img ... /> [ProAndroidDev](...)`

If the item is not published yet, use:

```md
Published: not published yet
```

If one catalog item combines multiple feature packages but they are covered by one publication, keep one publication line and add multiple code links.

If the item is still in development and publication is not ready yet, publication status may remain:

```md
Published: in progress
```

## WIP Rules

If a catalog item is still under development:

- append `(WIP)` to the title
- use `Code: in progress` if the code is not yet in `main`
- use `Published: in progress` or `Published: not published yet` depending on the situation

WIP example:

```md
### New Feature Name (WIP)

Short description.

<img src="docs/gifs/new_feature.gif" alt="New feature preview" width="300" />

Published: in progress

Code: in progress

---
```

## Workflow

When adding a new catalog item:

1. Record the source video into `docs/video/`.
2. Crop, trim, and prepare the preview locally.
3. Export the final README-ready GIF or image.
4. Place the final asset into `docs/gifs/`.
5. Add the catalog item to `README.md`.
6. By default, insert the new item at the top of the catalog unless this is a backfill of an older feature.
7. Reference only committed assets from `docs/gifs/`.

## Consistency Notes

- `README.md` is a showcase, not setup documentation
- catalog items should stay visually consistent
- final assets belong in `docs/gifs/`
- raw and intermediate media belong in `docs/video/`
- avoid referencing gitignored media from committed markdown
