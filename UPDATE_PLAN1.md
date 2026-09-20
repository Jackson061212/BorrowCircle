# BorrowCircle — Update Plan 1

## Copy-paste prompt for the new Codex chat

> Read `IMPLEMENTATION_PLAN.md` and `UPDATE_PLAN1.md` completely before editing. `IMPLEMENTATION_PLAN.md` remains the product specification; `UPDATE_PLAN1.md` is the immediate repair-and-polish priority and takes precedence where it is more specific. Start by inspecting the current repository, `git status`, current diff, navigation structure, app-level state, theme implementation, and the existing Android and web run configurations. Reproduce every reported issue before changing code. Then give me a concise implementation plan and complete the update stages in order. Do not start a later stage until the current stage builds, passes relevant tests, runs on both Android and web, and has been visually inspected using screenshots. Preserve working functionality and do not perform unrelated dependency, Gradle, Kotlin, Compose, AGP, or JDK upgrades.

---

## 1. Purpose

This update addresses the first usability and visual problems found in the BorrowCircle prototype:

1. There is no intentional post-login loading/brand experience.
2. The full `BorrowCircle` text in every top-left header consumes too much horizontal space.
3. Switching bottom-navigation destinations has no smooth selected-tab movement.
4. Pages do not scroll, making content inaccessible.
5. The center Create button has no observable effect.
6. Dark mode is missing, hidden, incomplete, or not discoverable.
7. Borrower and lender reliability information is missing, hidden, or not discoverable.

The update must repair these problems without rewriting the application or breaking existing Android/web behavior.

## 2. Mandatory inspection before editing

The implementation agent must first:

1. Read both plan files completely.
2. Inspect `git status` and the current diff. Do not discard or overwrite valid previous work.
3. Identify the shared Compose entry point, navigation state owner, authentication state owner, theme owner, and repository/state holder.
4. Run the existing Android and web configurations before editing.
5. Reproduce and record:
   - which pages cannot scroll;
   - what happens when Create is clicked;
   - whether a dark theme already exists but has no visible control;
   - whether Borrower Reliability and Lender Integrity already exist in code but are not reachable;
   - whether the header is shared or duplicated;
   - whether navigation state is shared or separately implemented per platform.
6. Report the exact run/build commands and proposed files to change.

Do not assume a feature is absent only because it is not visible. Reuse working theme, score, and navigation code when it exists.

## 3. Brand icon and one-time post-login loading screen

### 3.1 BorrowCircle icon

Create a compact, original, code-native BorrowCircle mark that works at small sizes on Android and web.

Recommended visual concept:

- Two clean, opposing circular arcs suggesting exchange and circulation.
- A small simplified object/card shape in the center, suggesting a shared item.
- The silhouette must remain recognizable at approximately 24–32 dp.
- Use the app accent color in light mode and an accessible lighter accent in dark mode.
- Avoid detailed illustrations, gradients that reduce legibility, tiny text, or a generic shopping-cart symbol.
- Do not put letters or the entire product name inside the icon.

Prefer a shared Compose `ImageVector`, vector resource, or a small reusable `Canvas` composable. Do not add a large raster asset or an external icon dependency solely for this mark.

Create one reusable component, for example `BorrowCircleMark`, with:

- configurable size;
- configurable tint or theme-aware default;
- semantic description where informative;
- ability to mark it decorative when adjacent text already identifies the app.

### 3.2 Loading/brand screen behavior

After a user successfully signs in, show a short branded loading/entry screen containing:

- centered BorrowCircle icon;
- `BorrowCircle` product name;
- a short optional line such as `Share more. Own less.`;
- a restrained loading animation;
- background and content colors from the active theme.

The animation should feel calm and intentional. Suitable options include:

- a subtle rotating or drawing motion around the two circulation arcs;
- a gentle scale/fade pulse of the mark;
- three low-key progress dots below the name.

Avoid an aggressive spinner, long artificial wait, bouncing text, flashing, or continuous animation after navigation begins.

### 3.3 Show it only once per authenticated app session

This is critical: the loading screen must **not** be implemented as part of the Discover/Home destination.

Model the root app state explicitly, for example:

```text
SignedOut -> Authenticating -> PostLoginIntro -> SignedInReady
```

Requirements:

- Show the branded loading screen only after successful authentication and before the first signed-in destination.
- Returning to Discover/Home through bottom navigation or Back must not show it again.
- Recomposition, tab switching, screen rotation, or window resizing must not show it again.
- It may show again after sign-out followed by a new sign-in, or after a genuinely new app/browser session.
- Own the `hasShownPostLoginIntro` state at the root authenticated-session level, not inside the Home composable.
- Do not use a destination-local `remember` flag as the sole protection.
- If real initialization work exists, keep the loading screen visible until it completes, with a reasonable timeout/error path.
- If no work exists, keep the animation brief—approximately 700–1400 ms—and do not pretend a long network operation is occurring.
- If the platform exposes reduced-motion preferences conveniently, replace the movement with a short fade.

### 3.4 Header replacement

On every signed-in primary page:

- Replace the full `BorrowCircle` text at the top-left with the compact icon.
- Keep the current page title separate and clear, such as `Discover`, `Requests`, `Activity`, or `Profile`.
- The mark may act as a non-interactive brand element. Do not make it secretly navigate unless that behavior is consistently communicated.
- Give the header enough height and padding for a 24–32 dp icon without wasting space.
- Ensure actions such as notifications and theme controls still fit at narrow Android widths.
- Do not replace meaningful page titles with only the icon.

If authentication/onboarding has a marketing-style layout, it may continue to show both the mark and the full name.

## 4. Smooth bottom-navigation transition

### 4.1 Moving selected indicator

When the selected primary destination changes, animate the shaded selected section from the old tab to the new tab instead of instantly removing and recreating separate backgrounds.

Desired behavior:

- A single selected indicator smoothly changes horizontal position.
- The indicator may also animate width if navigation items are not equal-width.
- Duration should generally be 180–260 ms.
- Use a smooth easing curve; avoid spring overshoot or cartoonish bouncing.
- Animate selected/unselected icon and label color in coordination with the indicator.
- Clicking the already-selected tab should not replay the full transition or recreate the destination.
- The indicator must not obstruct text, clip labels, or extend outside navigation bounds.
- In dark mode, use a subtle charcoal/accent-tinted surface rather than a bright block.

Implementation may use `animateDpAsState`, `updateTransition`, `animateFloatAsState`, an animated `Modifier.offset`, or an equivalent shared Compose approach. Prefer one coherent indicator rather than five independently animated pill backgrounds.

### 4.2 Destination content transition

Add a restrained content transition when changing primary destinations:

- short fade or fade-through;
- optionally a very small directional slide;
- approximately 150–220 ms;
- do not animate the entire screen from far off-screen;
- preserve destination state where reasonable, including search text and scroll position.

Avoid stacking several competing animations. Navigation responsiveness is more important than visual novelty.

### 4.3 Accessibility and input

- Selected state must be available semantically, not indicated by color alone.
- Maintain Android touch/ripple feedback.
- Maintain web hover, pressed, and keyboard-focus states.
- Keyboard activation on web must select the tab.
- Motion must not delay navigation logic.

## 5. Scrolling repair

Every page containing content taller than the viewport must scroll with Android touch gestures and the web mouse wheel/trackpad.

### 5.1 Choose the correct container

Use:

- `LazyColumn` for feeds and potentially long/repeating collections such as Discover listings, Requests, Activity, notifications, and transaction history.
- A `Column` with `verticalScroll(rememberScrollState())` for finite forms or short detail pages.
- `LazyRow` for horizontal chips/categories where appropriate.

Do not nest vertical `LazyColumn` and `verticalScroll` containers without a demonstrated need. Prefer one vertical scroll owner per screen.

### 5.2 Insets and bottom content

Each scroll container must:

- respect the parent `Scaffold` content/inner padding;
- include enough bottom content padding that the last item is not hidden beneath bottom navigation;
- handle status-bar and safe-area insets;
- use `imePadding` or equivalent where forms can be obscured by the Android keyboard;
- allow the last form field and submit button to be reached;
- avoid double-applying insets.

### 5.3 Preserve useful state

- Use remembered lazy-list/scroll state per destination where practical.
- Switching from Discover to Profile and back should not unexpectedly jump Discover to the top unless the app intentionally implements reselect-to-top.
- Back navigation from an item detail should return the user near the previous list position.

### 5.4 Pages to audit explicitly

At minimum test scrolling on:

- Discover
- Item detail
- Requests
- Request detail
- Create listing form
- Create request form
- Activity
- Transaction detail
- Condition check
- Resolution flow
- Profile
- Borrower Reliability details
- Lender Integrity details
- Balance/history
- Notifications
- Settings/Appearance

If some screens do not exist yet, do not create empty routes solely for this audit. Apply the rule to all implemented screens and note missing planned screens.

## 6. Repair the Create action

### 6.1 Expected interaction

The center Create destination/action must always give immediate visible feedback when clicked.

On activation, open a responsive action chooser containing exactly two primary choices:

1. **List an item**
   - Supporting text: `Offer something your circle can borrow.`
   - Navigate to the listing form.

2. **Post a request**
   - Supporting text: `Ask your circle for something you need.`
   - Navigate to the request form.

Recommended presentation:

- Android: modal bottom sheet if stable in the current Compose version.
- Web/wide layout: centered modal/dialog or compact responsive sheet.
- A shared implementation is preferred if it behaves correctly on both.

The chooser must support:

- touch/click;
- keyboard focus and activation on web;
- dismiss via explicit close/cancel;
- dismiss by Back on Android;
- escape-key dismissal on web where supported;
- clear focus behavior;
- no accidental duplicate sheets after repeated clicks.

### 6.2 Navigation and form completion

- Selecting `List an item` must open the existing listing form, or implement it according to `IMPLEMENTATION_PLAN.md` if missing.
- Selecting `Post a request` must open the existing request form, or implement it according to the original plan if missing.
- On successful submission, dismiss the creation flow and navigate to the new item/request detail or relevant feed.
- The new content must become visible immediately in the session repository.
- Invalid forms must show inline errors and retain entered values.
- Cancel must return safely without creating partial content.

### 6.3 Selected-state behavior

Create is an action entry point, not a persistent feed. Therefore:

- Do not leave Create selected after its chooser is dismissed.
- Keep the previously active destination selected behind the chooser.
- Do not create a blank Create page.
- If the current architecture already treats it as a destination, refactor minimally so the observable behavior matches this rule.

## 7. Dark mode audit and implementation

The dark theme must be discoverable and global.

### 7.1 User-facing location

Add an **Appearance** section accessible from Profile. Provide three choices:

- System
- Light
- Dark

The current choice must be visually selected and semantically announced.

Optionally show a compact theme action in a web header only if it does not clutter the interface, but Profile → Appearance remains the canonical location.

### 7.2 Behavior

- Theme choice applies to the entire app, including post-login loading, dialogs, forms, navigation, score details, and error/empty states.
- Switching theme must update immediately without restarting.
- It must not reset navigation or user-entered form data.
- Preserve the choice across destination changes.
- Persist the choice across app/browser restarts if a simple cross-platform settings mechanism already exists. Otherwise implement session persistence and document durable persistence as a follow-up rather than introducing a risky dependency.
- `System` follows platform/browser preference when supported.

### 7.3 Dark visual specification

- True or near-black app background.
- Slightly lighter charcoal surfaces.
- Off-white primary text and readable muted secondary text.
- Accessible accent color derived from the light-theme accent.
- Visible borders/dividers without excessive contrast.
- No white flashes during navigation or startup.
- Avoid pure-gray text on nearly identical gray surfaces.
- Keep modest 8–12 dp corner radii; dark mode must not introduce extra pill shapes.

If dark mode already exists internally, expose and repair it instead of duplicating the theme system.

## 8. Borrower and lender score visibility

The product should use the professional names from the main specification:

- **Borrower Reliability**
- **Lender Integrity**

Do not label these as financial credit scores in the UI.

### 8.1 Profile summary

Place two distinct score cards near the top of the signed-in Profile screen, after the identity/verification summary and before lower-priority settings.

Each card shows:

- role title;
- score from 0–100, or `New member` when no finalized history exists;
- confidence/history label;
- completed role-specific transaction count;
- one short explanation;
- clear affordance such as `View breakdown`.

The cards must remain distinct because a user may be an excellent lender but a new borrower, or vice versa.

### 8.2 Detail screens

Clicking a card opens its detailed score page. It must show:

- current score or New member state;
- last updated time;
- history/confidence level;
- six weighted components from `IMPLEMENTATION_PLAN.md`;
- earned points and maximum points for each component;
- plain-language definitions;
- expandable `How this is calculated` section;
- finalized events that affected the calculation;
- `Report an error / request review` action;
- note that pending accusations do not affect scores;
- note that this is not a financial credit score.

Use the existing shared score calculators if implemented. Never hard-code UI scores that disagree with domain calculations. If calculators are absent, implement and test the formulas from the main plan before displaying numbers.

### 8.3 Seed/demo states

Seed at least:

- one established borrower score;
- one established lender score;
- one limited-history state;
- one New member state;
- a score breakdown with non-identical component values.

Do not use all-perfect demo data. Judges should be able to see why the transparent breakdown matters.

## 9. Design polish shared across these changes

- Maintain the light, minimal BorrowCircle aesthetic.
- Use 8–12 dp corner radii for most surfaces.
- Reserve pill shapes for short chips/status badges.
- Ensure text never touches or clips against rounded corners.
- Use one consistent spacing scale.
- Keep touch targets approximately 48 dp.
- Provide hover, focus, pressed, disabled, and loading states where relevant.
- Every click must produce immediate visual feedback.
- Avoid duplicated page headings, excessive cards, and repeated explanations.
- Use concise labels and keep important actions visible without scrolling when practical.
- Verify that the compact logo does not reduce clarity of the current page.

## 10. Implementation stages and quality gates

Complete these stages in order.

### Stage 0 — Baseline audit

Tasks:

- Reproduce all reported issues on Android and web.
- Locate existing theme and score code.
- Record baseline screenshots.
- Run relevant existing tests/builds.

Gate:

- Exact Android and web run commands are known.
- Current behavior of Create, scrolling, theme, and scores is documented.
- No code has been broadly rewritten.

### Stage 1 — Scrolling and Create repair

These are blocking functionality defects and take priority over animation.

Tasks:

- Repair scrolling on all implemented screens.
- Repair the Create action chooser and both navigation paths.
- Make successful creation update the visible session data.

Gate:

- Android swipe and browser wheel/trackpad scrolling work.
- Last content/form controls are not hidden by navigation or keyboard.
- Both Create choices work on Android and web.
- Cancel, Back, and repeated clicks behave safely.
- A created request and listing become visible.

### Stage 2 — Theme and score discoverability

Tasks:

- Expose or implement Profile → Appearance.
- Verify global light/dark/system behavior.
- Expose or implement both score cards and detail breakdowns.

Gate:

- Dark mode is reachable without developer knowledge.
- Theme change does not reset current work.
- Both score cards are visible in Profile and open correct details.
- Displayed numbers match shared calculations/tests.
- New member state behaves correctly.

### Stage 3 — Brand loading and compact headers

Tasks:

- Create the reusable BorrowCircle mark.
- Implement the post-login loading/entry state.
- Replace repeated header wordmark text with the compact mark while preserving page titles.

Gate:

- Loading appears once after sign-in.
- It does not appear when returning to Discover.
- It does not replay on recomposition, rotation, resizing, or tab changes.
- Every signed-in header remains readable at narrow Android width.
- Icon works in both themes.

### Stage 4 — Navigation animation and final polish

Tasks:

- Add the moving selected-tab indicator.
- Add restrained content transitions.
- Verify hover/focus/pressed behavior.
- Correct visual regressions found through screenshots.

Gate:

- Indicator visibly moves between old and new destinations.
- Rapid tab switching does not crash, overlap, or leave the wrong tab selected.
- Existing destination state is reasonably preserved.
- Animations feel responsive on Android and web.

## 11. Required tests

Add or update tests where feasible for:

- Post-login intro state occurs once per authenticated session.
- Returning to Discover does not re-enter loading.
- Sign-out and a new sign-in can start a new intro session.
- Create chooser emits the correct `ListItem` and `PostRequest` navigation actions.
- Successful creation inserts data into the current repository state.
- Theme state changes without resetting navigation state.
- Score cards use domain-calculated values rather than independent constants.
- New member score display.

UI automation is welcome if already configured, but do not add a large testing framework solely for this update. Shared state/domain tests plus manual screenshot verification are acceptable for the prototype.

## 12. Required visual verification

After every stage, run both platforms and inspect—not merely capture—screenshots.

Minimum screenshots:

1. Android post-login loading screen.
2. Android Discover after loading has completed.
3. Android page scrolled to its bottom.
4. Android Create chooser.
5. Android Profile showing both score cards in dark mode.
6. Android score breakdown.
7. Web Discover at wide width.
8. Web narrow/responsive layout.
9. Web Create chooser with visible keyboard focus.
10. Web Profile → Appearance in light mode.
11. Web score breakdown in dark mode.

For animations, capture screenshots before/after and interactively observe the transition. A still screenshot alone cannot verify motion.

Check specifically for:

- clipped text;
- content hidden behind bottom navigation;
- non-scrolling forms;
- nested-scroll jank;
- header overcrowding;
- logo distortion;
- white flashes in dark mode;
- selected indicator misalignment;
- unreadable hover/focus states;
- Create chooser covering important controls;
- wrong score or duplicate score cards.

## 13. Completion report required from Codex

At the end, report:

- files changed;
- stages completed;
- Android build/run command and result;
- web build/run command and result;
- tests run and result;
- screenshots inspected;
- any behavior not verified;
- whether theme preference persists across restart or only the session;
- whether demo data persists across restart or only the session;
- known limitations;
- recommended next stage from `IMPLEMENTATION_PLAN.md`.

Do not begin unrelated product features as part of this update.

## 14. Definition of done

This update is complete only when:

- Android and web build and run.
- All implemented pages with overflowing content scroll correctly.
- Create visibly opens a chooser and both paths function.
- The BorrowCircle loading/brand screen appears once per authenticated session, not on every Home visit.
- Signed-in headers use the compact mark without sacrificing page titles.
- Bottom-navigation selection moves smoothly between tabs.
- Dark mode is discoverable through Profile → Appearance and applies globally.
- Borrower Reliability and Lender Integrity are visible in Profile and have understandable detail screens.
- Scores come from tested shared logic.
- Light and dark modes have been visually inspected on Android and web.
- No unrelated working feature has regressed.

