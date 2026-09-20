# BorrowCircle — Update Plan 2

## Copy-paste prompt for the new Codex chat

> Read `IMPLEMENTATION_PLAN.md`, `UPDATE_PLAN1.md`, and `UPDATE_PLAN2.md` completely before editing. The original file defines the product, Update Plan 1 defines earlier repairs, and Update Plan 2 is the immediate priority and takes precedence where it is more specific. This project does not use Git. Before changing anything, inspect the existing project structure, current source files, navigation, responsive layout, item/request models, image handling, and Android/web run configurations. Build and run the current version, reproduce every reported problem, and preserve valid existing work. Then give me a concise plan naming the files you expect to modify. Implement Update Plan 2 stage by stage. Do not start a later stage until the current stage builds, runs on both Android and web, passes relevant tests, and has been visually inspected using screenshots. Do not recreate the project or make unrelated Kotlin, Compose, Gradle, AGP, JDK, or dependency upgrades.

---

## 1. Purpose and current problems

This update makes BorrowCircle’s marketplace content functional and visually coherent across Android and web.

Reported problems:

1. Item cards such as `Portable Projector` cannot be opened, so users cannot see details or photos.
2. The `Open` button on community request cards does not open request details.
3. The Create chooser uses a purple-tinted popup that conflicts with the primarily white BorrowCircle theme.
4. Wide web layouts should use an elegant right-side panel instead of phone-style popups.
5. Listing and request cards look too plain and need stronger hierarchy, clearer boundaries, and restrained color.
6. The current listing-photo field is unclear and appears to be only a placeholder.
7. Creating a listing must support selecting new photos and, when supported, taking a new photo.
8. The app icon and title treatment remains inconsistent or incomplete.
9. Large white areas make the product feel unfinished and do not clearly separate sections.

Functional navigation and real image selection are higher priority than decorative polish.

## 2. Mandatory audit before editing

Because this project does not use Git, begin with read-only inspection and do not overwrite files speculatively.

The agent must:

1. Read all three plan files completely.
2. Inspect current modules, source sets, recently modified files, and application entry points.
3. Identify:
   - the item card composable and its click callback;
   - the request card composable and its `Open` callback;
   - the navigation/state mechanism used for primary and detail screens;
   - the existing item-detail and request-detail UI, if any;
   - the current Create chooser implementation;
   - the responsive breakpoint logic, if any;
   - the current photo placeholder implementation;
   - existing platform-specific image picker/camera code;
   - current BorrowCircle mark, header, theme colors, and card components.
4. Build and run Android and web before editing.
5. Reproduce each broken click and capture baseline screenshots.
6. Report what already exists, what is disconnected, what is absent, and the files expected to change.

Reuse existing working detail screens, image infrastructure, and responsive components when possible. Do not duplicate routes or create a second competing navigation system.

## 3. Make item cards open functional item details

### 3.1 Click behavior

Every visible item/listing card must be actionable.

- Clicking/tapping the card opens the corresponding item by stable item ID.
- Clicking the item image or title performs the same action.
- If the card includes an explicit `View details` action, it must call the same handler.
- Do not create separate behaviors for the image, title, and card surface.
- Buttons nested in the card must not accidentally trigger the card twice.
- Provide Android pressed/ripple feedback.
- Provide web pointer cursor, hover treatment, visible keyboard focus, Enter activation, and Space activation where semantically appropriate.
- Give the card a meaningful accessibility label such as `Open Portable Projector details`.
- Rapid clicks must not create multiple stacked copies of the same destination/panel.

Use stable IDs and retrieve the current item from repository/state. Do not pass a fragile list index or reconstruct a disconnected item copy.

### 3.2 Item detail content

The detail experience must show enough information to decide whether to borrow:

- image gallery with the primary image first;
- title and category;
- green price label, including pricing period such as `$8/day`, `$25/week`, `AED 20/day`, or `Free`;
- concise availability status;
- description;
- owner summary and verified-circle badge;
- Lender Integrity summary when implemented;
- condition;
- included accessories;
- price policy and estimated cost explanation;
- minimum/maximum loan duration;
- latest permitted return date;
- handoff area without exposing a private home address;
- usage/safety notes;
- primary `Request to borrow` action;
- close/back behavior appropriate to the platform.

If data for a field is unavailable, omit that row or show an honest neutral state. Do not invent production guarantees, insurance, or exact locations.

### 3.3 Image gallery behavior

- Show a coherent aspect ratio without stretching images.
- Allow users to move between multiple photos through thumbnails, pager, or previous/next controls.
- Provide a clear selected-photo state.
- Supply content descriptions or mark decorative imagery appropriately.
- Show a polished empty-image fallback using the BorrowCircle visual language, not a confusing `placeholder photo` label.
- Do not use the app logo as the item image fallback.

## 4. Make request cards and Open buttons functional

### 4.1 Click behavior

- The whole request card may be clickable.
- The `Open` button must open the same request detail by stable request ID.
- The event must not be swallowed by an overlay, disabled parent, transparent box, or missing callback.
- Provide the same Android/web interaction and accessibility behaviors described for item cards.
- Clicking a matched or closed request still opens its details; availability of actions changes according to status.

### 4.2 Request detail content

Show:

- request title;
- open, matched, fulfilled, or closed status;
- requester display name and verified-circle badge without revealing email/NetID;
- category;
- complete description;
- needed-from and needed-until dates;
- optional maximum budget;
- pickup flexibility/area;
- reference photo if one exists;
- linked offers/listings;
- time posted or freshness;
- primary `I can help` action when eligible.

`I can help` must open a clear choice:

1. Link one of the current user’s eligible existing listings.
2. Create a new public listing linked to this request.

The linked listing remains discoverable to other eligible members. The request becomes matched only when the requester accepts an offer, in accordance with the original plan.

## 5. Responsive detail and creation presentation

### 5.1 Android and compact layouts

Retain phone-appropriate presentation:

- The Create choice can remain a modal bottom sheet or compact dialog.
- Item and request details should normally be full-screen destinations on a phone so content and forms have enough space.
- Secondary confirmations may use dialogs.
- Sheets must use BorrowCircle theme colors rather than a default purple container.

Do not force a narrow right-side strip onto Android phones.

### 5.2 Wide web right-side panel

On sufficiently wide web layouts, replace phone-style Create/detail popups with a full-height panel that enters from the right.

Use a responsive breakpoint derived from available window width rather than a platform-name check alone. A reasonable initial breakpoint is approximately 840–960 dp/css-equivalent, adjusted after screenshot inspection.

The panel should:

- occupy the right side from beneath or alongside the app header to the bottom;
- use approximately 380–520 dp width or 34–44% of the available width, whichever produces a readable layout;
- have a sensible maximum width;
- animate in from the right over roughly 180–260 ms;
- use a subtle left border and/or soft shadow;
- use the normal white/light surface in light mode and a charcoal surface in dark mode;
- scroll independently when its content is long;
- preserve the Discover/Requests context behind it;
- have a clear close button;
- close with Escape on web when appropriate;
- restore focus to the element that opened it;
- avoid shifting content so violently that the user loses context.

For an item or request selected from a feed, prefer a master-detail treatment:

- feed remains visible on the left;
- selected card receives a subtle selected state;
- details appear in the right panel;
- selecting another card updates the panel rather than stacking another panel.

For Create:

- clicking Create opens a right-side creation panel on wide web;
- first show the two choices, `List an item` and `Post a request`;
- after choosing, replace the panel content with the corresponding form;
- provide an internal Back action to return to the two choices;
- closing the panel returns to the previously selected main destination;
- do not mark Create as a permanent selected destination.

On narrow browser windows, use the compact/mobile sheet or full-screen destination instead. Test both wide and narrow web widths.

### 5.3 Navigation state

Model the open panel explicitly, for example:

```text
Closed
ItemDetails(itemId)
RequestDetails(requestId)
CreateChoice
CreateListing(linkedRequestId?)
CreateRequest
```

Use the project’s existing architecture and equivalent naming. Avoid multiple independent booleans such as `showDialog`, `showItem`, and `showRequest` that can become true simultaneously.

If browser-history/deep-link support already exists, preserve it. If not, implement consistent in-app Back/Escape behavior and document deep links as a later enhancement.

## 6. Correct the mismatched purple Create popup

The Create surface currently appears slightly purple. This likely comes from a default Material container or theme token.

Requirements:

- Find the actual color source rather than covering it with arbitrary white values in several components.
- Define/use semantic theme tokens such as:
  - `appBackground`;
  - `surface`;
  - `surfaceSubtle`;
  - `surfaceElevated`;
  - `borderSubtle`;
  - `accent`;
  - `pricePositive`.
- In light mode, Create sheets/panels should use white or a deliberate warm neutral surface consistent with the rest of the app.
- In dark mode, use the established dark surface.
- Remove unintended Material purple from dialog/sheet container, drag handle, buttons, focus states, and selection states.
- Preserve the intentional BorrowCircle accent for primary actions and selected navigation.
- Check both Android and web; do not fix only one target.

The result should not be an entirely flat white rectangle. Use subtle section backgrounds, borders, spacing, and elevation to establish hierarchy.

## 7. Improve item and request card aesthetics

### 7.1 Shared visual language

Listings and requests should share a design system but remain distinguishable.

Common rules:

- modest 8–12 dp corner radius;
- thin neutral border;
- very soft elevation/shadow on light backgrounds;
- clear hover and selected states on web;
- 12–16 dp internal padding;
- consistent spacing between image, title, metadata, and actions;
- no text clipping;
- no excessive pill-shaped containers;
- clear separation between cards and the page background.

### 7.2 Listing cards

Recommended hierarchy:

1. Image/thumbnail with a consistent aspect ratio.
2. Title on a lightly tinted or subtly shaded title surface, or using a clear typographic block with sufficient contrast.
3. Category and availability metadata.
4. Green price tag.
5. Owner/circle information.
6. Clear affordance that the card opens details.

Green price tag:

- Use a restrained green background with dark accessible green text in light mode.
- Use a darker/desaturated green surface with light green text in dark mode.
- Include both value and unit, such as `AED 20/day`.
- Use `Free` when applicable.
- Do not communicate availability or safety solely through this green color.
- Keep it compact; it may be a chip because the content is short.

Title emphasis:

- Prefer a subtle accent-neutral shade rather than saturated color.
- Ensure two-line titles do not clip.
- Avoid putting long titles inside highly rounded pills.

### 7.3 Request cards

Recommended hierarchy:

1. Small `Community request` or status eyebrow.
2. Prominent title.
3. Needed dates and category.
4. Optional budget chip distinct from listing price.
5. Requester/circle summary.
6. `Open` action with a clear clickable card surface.

Use a different but related subtle accent from listing cards—such as a pale blue/teal section marker—without making the app visually noisy.

### 7.4 Section boundaries and populated feel

Improve the blank-canvas appearance by using:

- a lightly tinted page background rather than pure white everywhere;
- white/elevated content surfaces;
- subtle section headers or bands;
- dividers where grouping is clearer than another card;
- varied but restrained neutral/accent surfaces;
- consistent maximum content width on web;
- purposeful whitespace rather than empty unbounded areas.

Do not fill every area with color. The goal is visual structure, not decoration.

## 8. Real photo selection and camera capture

### 8.1 Replace the confusing placeholder control

The listing form must have a clearly labeled **Photos** section.

When empty, show:

- a neutral dashed or bordered upload area;
- short text: `Add clear photos of the actual item`;
- two explicit actions where supported:
  - `Choose from photos` / `Upload photos`;
  - `Take photo`;
- supported-count guidance such as `1–5 photos`;
- no unexplained placeholder image.

At least one real selected/captured photo is required to publish a listing. A request’s reference photo remains optional.

### 8.2 Shared abstraction

Keep form state and image metadata in shared code, but use platform-specific acquisition behind a small interface or expect/actual abstraction.

Conceptual interface:

```kotlin
interface ImageAcquirer {
    suspend fun chooseImages(maxCount: Int): ImageAcquireResult
    suspend fun captureImage(): ImageAcquireResult
    val canCaptureImage: Boolean
}
```

Adapt to current architecture and Compose version. Do not introduce a large media library if platform APIs or an existing dependency are sufficient.

Represent selected images with enough information for previews and form submission without exposing raw platform types throughout common code. Manage temporary resources/object URLs correctly.

### 8.3 Android behavior

- `Choose from photos` opens the system photo picker or suitable Activity Result API.
- Allow multiple selection up to the defined limit when supported.
- `Take photo` launches the system camera flow.
- Request camera permission only when the user chooses camera capture and only if required by the selected API.
- Handle permission denial, cancellation, unavailable camera, and capture failure without crashing or losing the form.
- Use a safe temporary URI/file mechanism; do not rely on unrestricted file paths.
- Do not request broad storage permissions when the system photo picker can avoid them.

### 8.4 Web behavior

- `Upload photos` opens a browser file chooser accepting images.
- Support multiple files up to the defined maximum.
- Validate MIME type and size before previewing.
- If the browser/device provides a supported camera-capture input, expose `Take photo`; otherwise hide or disable it with a clear explanation.
- Do not promise live camera capture on every desktop browser.
- Revoke object URLs when no longer needed to prevent memory leaks.
- File selection must work with keyboard input.

### 8.5 Validation and preview

- Accept common image formats supported by both targets.
- Limit to 5 images for the prototype.
- Apply a reasonable per-image size cap, initially 8 MB, unless existing architecture requires a smaller limit.
- Reject invalid files with a clear inline message.
- Display thumbnail previews in selection order.
- Allow setting/reordering the primary image if straightforward; at minimum, the first image is clearly labeled primary.
- Allow removing an image before submission.
- Maintain aspect ratio and avoid decoding enormous images at full resolution merely for thumbnails.
- Show upload/capture progress only if real asynchronous work exists.

### 8.6 Prototype storage truthfulness

If no remote image storage exists:

- keep selected images usable for preview and the active demo session;
- attach them to the new session listing;
- clearly document that images may not survive an app/browser restart;
- do not pretend the image was uploaded to a production server.

Do not block this update on adding a backend. Keep image storage behind the repository boundary so durable storage can be added later.

## 9. Resolve icon and title treatment

This update intentionally supersedes any earlier instruction to show only the icon in signed-in headers.

Use a compact **BorrowCircle brand lockup** at the top-left:

- BorrowCircle icon at approximately 24–28 dp;
- `BorrowCircle` wordmark immediately beside it;
- modest gap, approximately 8 dp;
- one line only;
- no oversized hero typography inside the app shell;
- accessible contrast in both themes.

Layout rules:

- The global top bar contains the icon + `BorrowCircle` at left and global actions such as notifications at right.
- The current page title remains visible as a content heading below the global bar or in a clearly separate region.
- Do not repeat `BorrowCircle` as both the top-bar brand and page title.
- At narrow Android width, keep the lockup compact; truncate lower-priority action labels before hiding the brand.
- On authentication/loading screens, a larger centered icon and name remain appropriate.
- Use the same reusable mark created or planned in Update Plan 1; do not create a second unrelated logo.
- If no mark was implemented, create the two-arc shared-item mark described in Update Plan 1.

The lockup itself should generally be non-interactive unless the app consistently defines it as a Home action and exposes that semantics.

## 10. States and error handling

Item/request details and photo acquisition require intentional states:

- loading/resolving by ID;
- loaded;
- not found/deleted;
- error with retry;
- no images;
- image selection cancelled;
- invalid image;
- image acquisition unavailable;
- panel opening/closing;
- form dirty/unsaved.

If a selected item/request no longer exists, show a friendly state and close/back action rather than a blank panel or crash.

When closing a creation panel with unsaved changes, ask for confirmation. Closing a read-only detail panel should not require confirmation.

## 11. Implementation stages and hard gates

Complete stages in order. Report commands, results, and screenshots after each stage.

### Stage 0 — Audit and baseline

Tasks:

- Perform Section 2 inspection.
- Reproduce broken item/request interactions.
- Record current Create surface, image placeholder, cards, and header.
- Establish exact Android and web run commands.

Gate:

- Root cause of each broken click is known.
- Existing detail/image/branding code has been identified.
- Baseline screenshots exist.
- Both targets are in a known build state.

### Stage 1 — Functional item and request details

Tasks:

- Wire item cards to item details by stable ID.
- Wire request cards and Open buttons to request details by stable ID.
- Implement missing detail content and actions.
- Add appropriate Back/close and not-found behavior.

Gate:

- `Portable Projector` and every other seeded listing open correctly.
- Item photos are visible and multiple photos can be navigated where seeded.
- Every request Open button works.
- `I can help` begins the correct link/create flow.
- Keyboard, pointer, and touch interactions work.
- Android and web screenshots have been inspected.

### Stage 2 — Responsive web panel and Create surface color

Tasks:

- Add one explicit responsive panel state.
- Use right-side panels for item details, request details, and Create on wide web.
- Retain mobile-appropriate Android/compact behavior.
- Correct unintended purple surface colors using theme tokens.

Gate:

- Wide web shows a right panel, not a centered phone-style popup.
- Narrow web and Android remain usable.
- Panel is independently scrollable, closable, keyboard accessible, and non-stacking.
- Create surfaces match BorrowCircle light/dark themes.
- No unintended purple remains.

### Stage 3 — Real image acquisition

Tasks:

- Implement shared image-acquisition abstraction and platform adapters.
- Implement Android gallery/photo picker and camera action.
- Implement web file selection and capability-aware camera option.
- Add previews, validation, removal, limits, and session listing integration.

Gate:

- Android can select an existing image and take a new photo on a supported emulator/device.
- Web can select one or more image files.
- Unsupported web camera is handled honestly.
- Selected images appear in the form and the resulting item detail.
- Cancellation, invalid files, and permission denial do not crash or erase unrelated form fields.

### Stage 4 — Card, section, and brand polish

Tasks:

- Implement improved listing/request card hierarchy.
- Add accessible green price tags.
- Improve page/section boundaries.
- Apply the compact icon + BorrowCircle lockup.
- Fix remaining visual defects found in screenshots.

Gate:

- Cards are clearly separated from the page without excessive rounding.
- Price labels are readable in both themes.
- Titles are prominent and not clipped.
- The interface feels populated but remains minimal.
- Icon and title are consistent across Android/web and light/dark modes.

## 12. Required tests

Add or update tests where practical for:

- item-card click emits the correct item ID;
- request Open emits the correct request ID;
- resolving a valid and invalid item ID;
- resolving a valid and invalid request ID;
- responsive presentation decision at compact and wide widths;
- only one panel state can be active;
- closing Create restores the previous primary destination;
- image count/type/size validation;
- listing submission requires at least one photo;
- new listing retains selected session image references;
- linked `I can help` flow retains the request ID.

Do not add a large testing framework solely for this update. Preserve existing test conventions.

## 13. Required visual verification

Run both targets and visually inspect at least:

1. Android Discover with improved listing cards.
2. Android Portable Projector detail with photos.
3. Android Requests and an opened request detail.
4. Android Create sheet in light mode with corrected color.
5. Android listing form with gallery image preview.
6. Android listing form with a captured image on a supported device/emulator.
7. Wide web Discover with item right panel open.
8. Wide web Requests with request right panel open.
9. Wide web Create right panel.
10. Narrow web fallback behavior.
11. Web listing form with selected-file preview.
12. Header brand lockup and cards in both light and dark themes.

Check for:

- dead click targets;
- double navigation;
- panel stacking;
- panel content that cannot scroll;
- incorrect selected-card state;
- stretched or cropped images;
- lost image previews;
- accidental purple surfaces;
- insufficient price-tag contrast;
- overuse of colored blocks;
- clipped titles;
- large empty unbounded white areas;
- header crowding on Android;
- focus loss after closing a web panel;
- bottom navigation obscuring Android details/forms.

## 14. Completion report required from Codex

At completion, report:

- files inspected and modified;
- root cause of each broken click;
- stages completed;
- exact Android build/run commands and results;
- exact web build/run commands and results;
- tests run and results;
- screenshots inspected;
- Android photo picker/camera result;
- web file picker/camera-capability result;
- whether selected images persist only for the session or across restart;
- responsive breakpoint used and why;
- any unverified behavior or known limitation.

Do not begin unrelated backend, payment, desktop, iOS, or AI work during this update.

## 15. Definition of done

Update Plan 2 is complete only when:

- Every listing card opens the correct detail experience.
- Portable Projector details and images are visible.
- Every request Open action opens the correct request.
- Wide web uses a polished right-side detail/Create panel.
- Android retains phone-appropriate sheets and full-screen details.
- Create surfaces no longer contain mismatched default purple.
- Listing and request cards have clear hierarchy and section boundaries.
- Price appears in a small accessible green tag.
- Listing creation can select real photos and capture a photo where supported.
- Selected photos appear in the created item’s detail during the demo session.
- The header consistently shows the BorrowCircle icon and name together.
- Light and dark themes both remain readable.
- Android and web builds, tests, and screenshot verification pass.

