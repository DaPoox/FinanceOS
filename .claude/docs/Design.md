# Emerald Ledger Design System

### 1. Overview & Creative North Star
**Creative North Star: "The Financial Atelier"**
Emerald Ledger moves away from the sterile, spreadsheet-like nature of traditional fintech. It is a bespoke, editorial experience that treats financial data as a curated exhibition. By utilizing high-contrast typography scales, deep monochromatic layering, and vibrant "jewel-tone" data points, the system transforms management into a moment of intentional reflection. The layout avoids rigid grids in favor of fluid, nested containers that prioritize visual rhythm over density.

### 2. Colors

> **Color gap rule:** If a Figma value is not in `Color.kt`, flag it, state the closest existing token, and wait for confirmation before adding or substituting.
The palette is rooted in a deep "Obsidian" base, allowing the vibrant "Emerald" primary and "Sapphire" secondary accents to radiate.

- **The "No-Line" Rule:** Sectioning is achieved exclusively through background shifts. For example, moving from `background` (#0f1417) to `surface-container-low` (#171c1f) defines a card boundary without a single 1px line.
- **Surface Hierarchy & Nesting:** Hierarchy is built through "Tonal Stacking." A `surface-container-low` card may host a `surface-container-high` icon button, creating a clear interactive depth.
- **The "Glass & Gradient" Rule:** Use `backdrop-blur-xl` with 60-80% opacity for fixed elements (like Top App Bars) to maintain a sense of environmental continuity. Gradients should be used sparingly, primarily on progress bars and primary action FABs to draw the eye.
- **Signature Textures:** Interactive states and primary elements use a subtle `primary-container` to `primary` gradient to simulate depth and light.

### 3. Typography
The system uses **Inter** across all roles to maintain a modern, Swiss-inspired precision, but varies weight and tracking dramatically to create an editorial feel.

- **Display & Headline (1.875rem - 1.5rem):** Extra-bold and tight-tracked. Used for hero numbers and brand titles.
- **Title & Body (1.25rem - 1.125rem):** Used for primary list items and section headers.
- **Label & Utility (10px - 11px):** High-contrast uppercase labels with wide tracking (tracking-widest). This is critical for the "Atelier" look, treating small text as metadata.
- **Real-World Rhythm:** The screen utilizes a non-standard 10px uppercase label for category headers, which contrasts sharply against the 30px (1.875rem) main balance, creating a "big and small" dynamic that feels intentional and high-end.

### 4. Elevation & Depth
Depth is conveyed through light and layering, not shadows.

- **The Layering Principle:** Instead of lifting objects with shadows, we "sink" them or "stack" them using the `surface-container` tiers.
- **Ambient Shadows:** Only used for the FAB and Bottom Nav to indicate they float above the scrollable content. Shadows are highly diffused: `rgba(110,229,145,0.3)` with a 40px blur for the primary FAB.
- **Glassmorphism & Depth:** The Bottom Navigation uses a `backdrop-blur-2xl` with a `surface` tint at 80% opacity, ensuring the content beneath is suggested but not distracting.

### 5. Components
- **Progress Bars:** Dual-layered with a container background of `surface-container-low` and a rounded-full pill interior using a linear gradient.
- **Cards:** Defined by `surface-container-low` with a 1rem (DEFAULT) or 2rem (lg) corner radius. No borders.
- **Pips & Chips:** Categorical markers are small 10px circles paired with bold 11px text, nested inside `surface-container-highest/30` pills.
- **Buttons:**
    - **FAB:** Circular, large (w-16), using the primary gradient.
    - **Icon Buttons:** Circular `surface-container` or `surface-container-high` backgrounds with centered icons.
- **Bottom Navigation:** High-radius (3rem) top corners, using high-contrast active states (Primary background with dark On-Primary text).

### 6. Do's and Don'ts
- **Do:** Use uppercase 10px labels for all non-interactive metadata.
- **Do:** Use `bg-surface-container-low` for primary grouping containers.
- **Do:** Apply `transition-all duration-300` and `active:scale-95` to all clickable containers to provide tactile feedback.
- **Don't:** Use solid borders for card outlines.
- **Don't:** Use pure white text; use `on-surface` (#dfe3e7) to reduce eye strain against the dark background.
- **Don't:** Crowd elements; maintain `spacing: 3` (Spacious) to preserve the editorial feel.