2# BorrowCircle — Kotlin Multiplatform Hackathon Implementation Plan

## Copy-paste launcher prompt for the coding agent

> Read `IMPLEMENTATION_PLAN.md` completely before editing any code. Begin by inspecting the existing repository, Gradle configuration, source sets, run configurations, and current Android build. Then give me a concise implementation brainstorm covering architecture, navigation, state flow, risks, and the files you expect to change. Implement the plan milestone by milestone. Do not begin a later milestone until the current milestone builds, its acceptance checks pass, and—where UI is involved—you have run it and visually inspected screenshots on both web and Android using the screenshot/browser/emulator access available to you. Fix visible defects before proceeding. Preserve working project versions and do not perform speculative Kotlin, Compose, Gradle, AGP, or JDK upgrades. If a requested feature is too risky for the hackathon prototype, implement the documented prototype fallback and say so explicitly.

---

## 1. Mission and event context

This project is for a one-day JetBrains Kotlin Multiplatform hackathon. The challenge is to create a cross-platform application that supports communities, raises awareness, or helps solve a real-world problem. The product must run on at least two platforms. Judging is weighted as follows:

- Working Product — 35%
- Problem & Insight — 25%
- Technical Execution, including effective use of tooling and AI — 25%
- Pitch & Clarity — 15%

The primary targets are:

1. Android
2. Web

Desktop is a possible third target only after the Android and web versions are stable. iOS is explicitly out of scope for this hackathon build.

The app is named **BorrowCircle**. It is a trusted borrowing network for bounded communities such as universities, residence halls, clubs, apartment buildings, and neighborhood groups. The initial demo community is **NYU Abu Dhabi**.

The product is not an anonymous rental marketplace and should not be presented as “Airbnb for tools.” Its distinctive idea is:

> BorrowCircle helps verified communities discover what they collectively own, request what they need, circulate items responsibly, document every handoff, and resolve damage fairly.

The prototype must emphasize four differentiators:

1. **Verified circles:** membership is tied to a trusted community, demonstrated with NYU email verification.
2. **Request-first matching:** users can ask for an item even when no listing exists; owners can answer the need by creating a public listing linked to the request.
3. **Documented care:** timestamped condition checks, photos, accessory lists, and an item history reduce ambiguity.
4. **Responsible circulation:** every loan has clear custody, return expectations, issue-resolution steps, and transparent reliability indicators.

## 2. Product principles

Use these principles to resolve implementation decisions:

- **Working beats broad.** A smaller end-to-end flow is better than many dead buttons.
- **Every screen has one primary purpose.** Do not create tabs that duplicate one another.
- **Shared code must be visible to judges.** Put domain models, validation, trust-score calculations, state transitions, sample repositories, and as much UI as practical in shared Kotlin code.
- **Privacy is part of trust.** Verify identity without exposing email addresses or creating a public blacklist.
- **Evidence beats accusation.** Condition records and handoff confirmations should drive disputes.
- **Repair before replacement.** A borrower may arrange a repair only after the lender approves the repair proposal.
- **No fake production claims.** Payments, deposits, insurance, institutional verification, and administrator escalation may be simulated, but the UI must label them as prototype behavior.
- **Accessible by default.** Use readable contrast, semantic labels, sensible touch targets, keyboard focus on web, and do not communicate status using color alone.

## 3. First action: inspect and brainstorm before coding

Before making edits, the agent must:

1. Inspect the repository structure and identify all modules and source sets.
2. Read the current Gradle version catalog, Kotlin version, Compose Multiplatform version, Android Gradle Plugin version, and JDK/Gradle JVM requirements.
3. Run the currently working Android target once and record the exact command or run configuration that succeeds.
4. Determine whether a web target already exists. Do not assume module names from a newer template.
5. Check Git status and preserve unrelated existing work.
6. Identify the smallest safe change required to add web support if it is absent.
7. Present a concise brainstorm to the user before implementation containing:
   - proposed module/source-set structure;
   - shared versus platform-specific responsibilities;
   - navigation model;
   - state-management approach;
   - image-picking prototype approach;
   - local demo data versus optional backend approach;
   - likely technical risks and fallbacks;
   - milestone-by-milestone file changes.

Do not rewrite the project from scratch merely to match a template. Adapt the plan to the existing project. Do not upgrade Kotlin, Compose, Gradle, AGP, or the JDK unless the current versions cannot support the required web target and the incompatibility is demonstrated. If an upgrade is unavoidable, make the smallest compatible version change and build immediately afterward.

## 4. Technical architecture

### 4.1 Preferred platform approach

Use **Compose Multiplatform** to share UI and domain logic across Android and web. Prefer a Kotlin/Wasm browser target (`wasmJs`) when supported by the project’s installed versions. If the existing project cannot safely support Kotlin/Wasm within the available time, use the compatible Kotlin/JS web target while keeping the domain layer shared.

Do not remove a currently working desktop or iOS module just because it is not a primary target. It may remain unmodified. Do not allow those optional targets to block Android and web builds; use target-specific build/run tasks.

### 4.2 Layering

Keep dependencies flowing inward:

```text
Presentation/UI -> Use cases/ViewModels -> Domain models and rules -> Repository interfaces
                                                     ^
                                                     |
                                      Demo/local repository implementation
```

Recommended packages, adapted to the actual repository namespace:

```text
commonMain/
  app/
    App.kt
    navigation/
    theme/
  domain/
    model/
    scoring/
    validation/
    workflow/
  data/
    repository/
    demo/
  feature/
    auth/
    discover/
    requests/
    create/
    activity/
    condition/
    resolution/
    profile/
    notifications/
  ui/
    components/
    state/

commonTest/
  scoring/
  workflow/
  validation/

androidMain/
  Android entry point and genuinely Android-specific integrations

wasmJsMain/ or jsMain/
  Browser entry point and genuinely browser-specific integrations
```

The exact directory names may differ. Follow existing conventions where reasonable.

### 4.3 State management

Use an explicit unidirectional state flow:

```text
User action -> Event/intent -> State update/use case -> Repository -> New immutable UI state
```

Prefer simple Kotlin `StateFlow`/coroutines if they are already supported. Do not add a heavy architecture framework for the prototype. Keep composables mostly stateless and pass state plus event callbacks into them.

### 4.4 Repository strategy

Create repository interfaces in shared code, for example:

- `AuthRepository`
- `CatalogRepository`
- `RequestRepository`
- `LoanRepository`
- `UserRepository`
- `NotificationRepository`
- `BalanceRepository`

The mandatory prototype implementation is a deterministic `DemoRepository` or cohesive set of in-memory repositories populated with strong seed data. Mutations during a session must work: creating listings and requests, answering a request, starting a loan, completing condition checks, changing a loan’s status, and resolving an incident must visibly update the UI.

Local persistence is desirable only if it is straightforward with the existing stack. A real remote backend is optional and must not be attempted until both targets work end to end with demo data. Do not let authentication or backend configuration jeopardize the demonstration.

If a remote backend is later added, keep it behind the same interfaces so demo mode remains available as an offline fallback.

### 4.5 Prototype authentication

Implement a clearly labeled simulated sign-in flow:

- Accept only addresses ending in `@nyu.edu` for the demo.
- Validate the domain in shared Kotlin code.
- Simulate sending/accepting a verification code, or provide a “Continue with verified demo account” action.
- Never claim that the prototype has contacted NYU identity systems.
- Store the full email privately in the user/account model but never display it to other ordinary users.
- Other members see a display name, optional avatar, circle affiliation, “Verified NYU member” badge, and trust indicators.
- Administrators are represented only as a simulated authorized role. Identity disclosure for theft/safety escalation must be described as a controlled institutional process, not as a button that reveals private data to a lender.

## 5. Identity, conversation, and privacy decision

Loans and conversations should **not be fully anonymous**. Complete anonymity weakens accountability and makes handoff coordination impractical. However, public exposure of NetIDs or email addresses creates privacy and harassment risks.

Implement this balanced model:

- Before a loan is accepted, show the member’s display name, verified-community badge, participation history, and transparent trust summaries.
- After acceptance, the two parties can use an in-app conversation tied to that loan or request.
- Do not display either party’s full NYU email or NetID in chat, listings, requests, profiles, reviews, or scores.
- The platform retains the verified account identity privately.
- In a serious unresolved loss, theft, or safety incident, the UI offers “Request moderator review.” It explains that authorized community administrators may access account identity according to institutional policy.
- Do not implement public accusations, “thief” labels, doxxing, global blocklists, or public dispute narratives.
- Trust scores are visible within the verified circle, but raw private incident details are visible only to the parties and authorized moderators.
- Include an appeal/correction concept in the score details screen.

## 6. Information architecture and navigation

Use exactly five primary bottom-navigation destinations on Android. On wide web layouts, render the same destinations in a compact left rail or top navigation while preserving the same route structure.

### 6.1 Primary destinations

1. **Discover**
   - Browse/search/filter available item listings.
   - Show relevant open community requests as a secondary section, but do not reproduce the full Requests feed.
   - Item cards lead to item details.

2. **Requests**
   - Dedicated feed of unmet community needs.
   - Search/filter by category, date needed, circle, and open/matched status.
   - A request detail page lets an owner choose “I can help,” then create or select a listing and link it to the request.

3. **Create**
   - A visually distinct center action.
   - Opens a choice sheet/page with exactly two choices: “List an item” and “Post a request.”
   - It is an action entry point, not a content feed.

4. **Activity**
   - All borrowing/lending transactions and their status.
   - Segmented control: `Borrowing` and `Lending`.
   - Optional filters: Active, Awaiting action, Completed.
   - Conversations exist inside a specific transaction detail page so messages retain context. Do not create a duplicate global chat tab for the prototype.

5. **Profile**
   - Must be the bottom-right navigation item.
   - Shows both role scores, balance, verification status, circle membership, impact summary, settings, theme switch, and sign out.

### 6.2 Secondary destinations

These are reached contextually and must not be bottom tabs:

- Item detail
- Request detail
- Create/edit listing
- Create/edit request
- Checkout/loan proposal
- Transaction detail
- Condition check
- Issue/resolution center
- Loan conversation
- Notifications
- Borrower Reliability details
- Lender Integrity details
- Balance and transaction history
- Settings

The back action must behave consistently on Android and web. If deep-link routing is too risky for the prototype, implement stable in-app navigation and do not claim deep-link support.

## 7. Core user flows

### 7.1 Discover and borrow an existing item

1. User opens Discover.
2. User searches or filters by category, price, availability, and circle.
3. User opens an item detail page.
4. Page shows photos, description, owner summary, Lender Integrity score, availability, pricing unit, latest permitted return date, deposit/protection placeholder, included accessories, condition summary, and item passport highlights.
5. User selects start and return dates.
6. Validation prevents dates in the past, a return earlier than pickup, a return after the listing’s maximum/latest return date, or an unavailable interval.
7. User reviews a clear cost estimate. Payment is marked “Prototype—no charge will be made.”
8. User sends a loan request.
9. The transaction appears in Activity with `Requested` status and the lender receives a notification.

### 7.2 Post an unmet need

1. User opens Create and selects “Post a request.”
2. Required fields: title, category, description, needed-from date, needed-until date, circle, optional maximum budget, optional reference image, and pickup flexibility.
3. The request appears in Requests and can also appear in the Discover secondary section.
4. An owner opens the request and chooses “I can help.”
5. The owner selects an existing eligible listing or creates a new public listing.
6. The new listing links to the request and tags/notifies its creator, but remains discoverable by other eligible members.
7. The request becomes `Matched` only when the requester accepts an offer, not merely when a listing is tagged.

### 7.3 List an item

Required fields:

- Title
- Category
- At least one product photo; allow multiple photos
- Description
- Condition
- Included accessories checklist
- Circle visibility
- Pricing model: free, per day, per week, or flat period
- Price when not free
- Minimum and maximum loan duration
- Availability range
- Latest return date
- Approximate replacement value, kept private except where needed for an agreed-resolution preview
- Handoff location description; avoid exposing a home address publicly
- Usage/safety notes

For the prototype, image selection must at least show selected previews and attach them to the session’s listing. If durable cross-platform file storage is not configured, state that limitation in code comments and demo notes; do not silently fake an upload to a server.

### 7.4 Accept and hand off

1. Lender approves or declines the request.
2. On approval, both users see a transaction detail page and contextual conversation.
3. At pickup, the lender starts a “Condition handoff.”
4. Record required photos, functional status, condition notes, and accessory checklist.
5. Both parties confirm the same snapshot.
6. The loan becomes `Active`; custody is now recorded against the borrower.

### 7.5 Return normally

1. Borrower selects “Start return.”
2. Capture after-use photos and complete the same accessory/function checklist.
3. Lender reviews the return.
4. Lender chooses `Returned as expected`, `Normal wear`, or `Report an issue`.
5. A normal return completes the transaction and updates both users’ score evidence.
6. Each party may leave short structured feedback. Avoid an unmoderated public free-text attack surface; free text can be private transaction feedback in the prototype.

### 7.6 Relay handoff — stretch feature

If the same item has a subsequent approved borrower, the owner may approve a direct handoff from the current borrower to the next borrower. Require a fresh condition snapshot and confirmations from current borrower, next borrower, and owner. Do not implement this until the ordinary return workflow is fully stable.

## 8. Loan state machine

Represent loan status with a sealed type or enum and centralize permitted transitions. UI code must not set arbitrary strings.

Suggested states:

```text
Requested
Approved
PickupPending
Active
ReturnPending
ConditionReview
ResolutionOpen
Completed
Declined
Cancelled
LostOrStolenReported
```

Required transition rules:

- `Requested -> Approved | Declined | Cancelled`
- `Approved -> PickupPending | Cancelled`
- `PickupPending -> Active | Cancelled`
- `Active -> ReturnPending | LostOrStolenReported`
- `ReturnPending -> ConditionReview`
- `ConditionReview -> Completed | ResolutionOpen`
- `ResolutionOpen -> Completed` only after a resolution is recorded
- `LostOrStolenReported -> ResolutionOpen`

Invalid transitions must return a domain error and leave state unchanged. Add unit tests for valid and invalid transitions.

## 9. Damage, loss, theft, and dispute resolution

### 9.1 Evidence model

Create a `ConditionSnapshot` for pickup and return containing:

- transaction ID and item ID;
- timestamp;
- creator user ID;
- photo references;
- functional check result;
- accessory checklist results;
- existing-defect notes;
- confirmation status for both parties.

Use a versioned item condition history. Never overwrite the original pickup snapshot.

### 9.2 Issue types

- Normal wear
- Cleaning required
- Missing accessory
- Cosmetic damage
- Functional damage
- Complete loss
- Suspected theft/non-return
- Safety concern
- Condition already present at pickup

### 9.3 Resolution workflow

When an issue is reported:

1. Freeze the item from new bookings if the issue affects completeness or safety.
2. Show the before/after evidence side by side.
3. Let the borrower acknowledge, disagree, or add evidence.
4. Offer resolution options:
   - lender accepts as normal wear;
   - borrower cleans and returns again;
   - borrower replaces a missing accessory;
   - borrower proposes a repair provider and estimate;
   - borrower and lender record an agreed payment amount;
   - request moderator review.
5. A repair can proceed **only after the lender approves the repair proposal**. Store the proposal and approval as separate events.
6. Payment is display-only in the prototype. The UI records an agreement but must say “No payment is processed by this prototype.”
7. When resolved, record outcome, responsibility, and item availability. Update scores only from a finalized outcome, never from an unverified accusation.

### 9.4 Lost or stolen item flow

- Borrower or lender can report a non-return/loss from the transaction.
- Ask whether it appears lost, stolen from the borrower, or intentionally not returned; do not publicly label a person as a thief.
- Record a deadline for response and recovery attempts.
- Temporarily restrict new high-value borrowing while a serious case is unresolved. Show this restriction privately to the affected user and authorized moderators, not as a public badge.
- Offer return/recovery, agreed replacement, agreed payment, or moderator review.
- Explain that an authorized institution may use the privately retained verified identity under its own policy. The app must not automatically reveal email/NetID to the lender.
- Score changes occur only after acknowledgement or moderator-confirmed resolution.

### 9.5 Fairness and safety rules

- Normal wear is not treated as damage.
- The lender cannot unilaterally charge an amount.
- A repair cannot be unauthorized.
- A borrower can dispute a claim and submit evidence.
- A lender’s repeated inaccurate or unfair claims affect Lender Integrity evidence.
- Scores must show the evidence period, sample size, and appeal entry point.
- Do not implement vigilantism, public shaming, permanent bans based solely on one report, or exposure of private identity.

## 10. Two-role trust system

Each person can lend and borrow, so maintain two independent indicators.

### 10.1 Names

- **Borrower Reliability** — how dependably a member returns and cares for borrowed items.
- **Lender Integrity** — how accurately and fairly a member presents items and fulfills lending commitments.

Avoid the phrase “credit score” in the product UI because no lending of money or financial creditworthiness is being assessed. The profile may explain that these are community transaction reliability indicators, not financial credit scores.

### 10.2 Common scoring method

Both indicators range from 0 to 100, but new members display **New member** rather than a misleading perfect or poor number until they complete a transaction.

For rate-based components, use Bayesian smoothing to prevent one transaction from producing an extreme score:

```kotlin
fun smoothedRate(
    successes: Double,
    opportunities: Double,
    priorMean: Double = 0.80,
    priorWeight: Double = 5.0
): Double = (successes + priorMean * priorWeight) / (opportunities + priorWeight)
```

For 1–5 ratings, normalize the smoothed average to 0–1. Use a prior rating of 4.0 with weight 5 unless tests justify another transparent constant.

```text
normalizedRating = (smoothedAverageRating - 1) / 4
```

Clamp every component to 0–1. The final score is the rounded sum of component value multiplied by its weight. Keep the weights and evidence counts visible in the details screen.

Confidence label based on finalized role-specific transactions:

- 0: New member
- 1–2: Limited history
- 3–7: Developing history
- 8 or more: Established history

For the prototype, use all seeded history. In production notes, recommend a rolling recent-history window so a very old mistake does not define someone forever.

### 10.3 Borrower Reliability formula

| Component | Weight | Evidence |
|---|---:|---|
| Timely return rate | 25 | Returned by agreed deadline or an extension approved before the deadline |
| Care and condition rate | 25 | Finalized returns classified as expected/normal wear rather than borrower-responsible damage |
| Completion rate | 15 | Approved loans completed rather than borrower-caused cancellation or unresolved non-return |
| Handoff compliance | 15 | Required pickup/return snapshots and accessory checks completed accurately |
| Communication quality | 10 | Lender’s structured 1–5 communication rating |
| Resolution responsibility | 10 | Borrower followed through on agreed cleaning, repair, replacement, or payment resolutions |

```text
Borrower Reliability =
  25 * timelyReturnRate +
  25 * careRate +
  15 * completionRate +
  15 * handoffComplianceRate +
  10 * normalizedCommunicationRating +
  10 * resolutionFollowThroughRate
```

Only include resolution responsibility when the borrower has eligible finalized resolution events; otherwise use the documented prior, not an automatic zero.

### 10.4 Lender Integrity formula

| Component | Weight | Evidence |
|---|---:|---|
| Listing accuracy | 25 | Borrower confirms that photos, description, included parts, and actual condition matched |
| Item readiness | 20 | Item was functional, complete, clean, and available as promised at handoff |
| Handoff reliability | 15 | Lender completed approved handoffs without lender-caused cancellation or material delay |
| Responsiveness | 10 | Structured response-timeliness outcome, not invasive message surveillance |
| Fair resolution conduct | 15 | Finalized issue records show evidence-based, timely, policy-consistent behavior |
| Borrower experience rating | 15 | Borrower’s structured 1–5 overall lending experience rating |

```text
Lender Integrity =
  25 * listingAccuracyRate +
  20 * itemReadinessRate +
  15 * handoffReliabilityRate +
  10 * responsivenessRate +
  15 * fairResolutionRate +
  15 * normalizedBorrowerRating
```

### 10.5 Score details UI

Both indicators appear on Profile as separate cards. Opening one must show:

- Current score or New member status
- Confidence/history label
- Completed role-specific transaction count
- Last updated time
- Six component rows with earned points, maximum points, weight, and plain-language explanation
- Expandable “How this is calculated” section containing the formula
- Recent finalized events that changed the indicator
- “Report an error / request review” action
- Privacy note: this is a community reliability indicator, not a financial credit score

Do not hide the algorithm, add arbitrary secret penalties, or change a score from a merely pending allegation.

### 10.6 Required score tests

Add deterministic tests for:

- New member state
- One successful transaction does not produce an unjustified 100
- Several successful transactions raise confidence and score
- A late but otherwise safe return affects only appropriate components
- Normal wear does not count as borrower damage
- Unresolved accusation does not change score
- Finalized borrower-responsible damage changes relevant evidence
- Accurate lender with few loans remains smoothed toward the prior
- Score remains between 0 and 100
- Same evidence always produces the same result on Android and web

## 11. Balance and prototype payments

Profile must contain a **Balance** card and a balance detail screen. This is not a real wallet.

Show:

- Available demo balance
- Pending demo charges
- Pending demo earnings
- Transaction history rows
- Labels such as `Prototype balance` and `No real money is stored or processed`

Loan estimates should support:

- Free
- Price per day
- Price per week
- Flat price for the selected period

Put cost calculation in shared domain code and unit-test date boundaries and pricing types. Do not collect card details or use a real payment SDK during the mandatory milestones.

## 12. Data model

Use stable IDs rather than list indices. Models can be data classes plus sealed types/enums. At minimum represent:

```text
User
Circle
Membership
Item
ItemPhoto
AvailabilityWindow
PricingPolicy
BorrowRequest
RequestOfferLink
LoanTransaction
ConditionSnapshot
AccessoryCheck
IssueReport
RepairProposal
ResolutionAgreement
Message
Notification
StructuredFeedback
TrustEvidence
RoleScoreBreakdown
BalanceAccount
BalanceEntry
```

Important relationships:

- A user may be both borrower and lender.
- An item has one owner and visibility in one or more circles.
- A community request can receive multiple linked offers/listings.
- A listing created for a request remains a normal discoverable listing.
- A loan has one immutable pickup snapshot and zero or more later snapshots.
- An issue belongs to one loan and may contain multiple proposed resolutions.
- Scores are computed from finalized evidence rather than manually stored numbers whenever practical.

Seed enough coherent data to make every major screen look intentional:

- 8–12 listings across useful categories
- 5–7 open/matched requests
- 4–6 users with varied but plausible histories
- active borrowing and lending transactions
- one clean completed return
- one damage-resolution example
- one missing-accessory example
- notifications and balance entries

Use diverse, respectful names and realistic NYUAD-oriented scenarios. Do not use real people’s private details.

## 13. Visual and interaction design

### 13.1 Design direction

Create a clean, calm, minimal interface that feels credible rather than playful or overdecorated.

Light theme:

- Warm white or very light neutral background
- Near-black primary text
- Muted gray secondary text
- One restrained accent color, preferably deep teal, indigo, or blue-green
- Soft neutral surfaces and subtle borders

Dark theme:

- True or near-black background
- Slightly elevated charcoal surfaces
- Off-white text rather than glaring pure white everywhere
- Same accent family adjusted for sufficient contrast
- Avoid gray-on-gray low-contrast text

### 13.2 Shape and spacing

- Use modest corner radii, roughly 8–12 dp for most cards and inputs.
- Do not use pill-shaped containers for long text.
- Pills/chips are acceptable only for compact status labels and filters.
- Ensure text has internal padding and never clips against rounded corners.
- Prefer whitespace, dividers, and hierarchy over putting every section in a floating card.
- Minimum touch target approximately 48 dp.
- Use a coherent spacing scale such as 4, 8, 12, 16, 24, and 32.

### 13.3 Responsive behavior

- Compact Android: bottom navigation, single-column feeds, full-width forms.
- Wide web: constrained centered content, optional two-column discovery/detail layouts, and navigation rail/top bar.
- Do not stretch cards edge to edge across a large desktop browser.
- Avoid fixed pixel widths that break smaller browser windows.
- Test at minimum one phone viewport and one desktop-browser viewport.

### 13.4 Interaction feedback

- Buttons need clear default, hover (web), keyboard focus (web), pressed, disabled, and loading states.
- Use ripple/pressed feedback on Android.
- Use subtle scale, tonal, border, or elevation changes for web hover; avoid excessive bouncing.
- Add smooth but short transitions between primary destinations and detail content, generally around 150–250 ms.
- Respect reduced-motion preferences where the platform API makes this practical.
- Animate content/state changes intentionally; do not animate everything.
- All destructive or consequential actions require confirmation.
- Forms display inline validation and preserve entered values when validation fails.

### 13.5 Reusable components

Create a small component system instead of duplicating styling:

- `BorrowCircleScaffold`
- `PrimaryButton`, `SecondaryButton`, `TextButton`
- `SearchField`
- `FilterChip`
- `ListingCard`
- `RequestCard`
- `MemberSummary`
- `TrustIndicatorCard`
- `StatusBadge`
- `EmptyState`
- `ErrorState`
- `PhotoPickerField`
- `PriceLabel`
- `ConditionChecklist`
- `TimelineStep`
- `ConfirmationDialog`

Names may change to match code style, but responsibilities should remain distinct.

## 14. Required screens and content

### Authentication/onboarding

- BorrowCircle value proposition
- NYU email field
- Domain validation
- Simulated verification state
- Community agreement emphasizing respectful borrowing and privacy

### Discover

- Greeting and current circle
- Search
- Categories/filters
- Featured or nearby-style listings without exposing precise private locations
- Small “Community needs” section linking to Requests
- Useful empty/no-results state

### Item detail

- Photo gallery
- Item facts, pricing, availability, accessories, safety notes
- Lender summary and Lender Integrity indicator
- Borrow CTA
- Clear maximum/latest return constraint

### Requests

- Dedicated unmet-needs feed
- Open/matched status
- “I can help” flow
- Request creator notification when tagged

### Create

- Choice between listing and request
- Validated forms
- Photo preview
- Save draft optional only if simple
- Success confirmation routing to the newly created content

### Activity

- Borrowing/Lending segments
- Status and next action visible on every row
- Transaction detail timeline
- Contextual conversation
- Start pickup/return condition check
- Report issue and resolution flow

### Profile

- Avatar and verified-circle identity
- Borrower Reliability card
- Lender Integrity card
- Prototype balance card
- Sustainability/community impact summary, using clearly defined demo metrics
- Theme control: System, Light, Dark
- Settings and sign out

### Notifications

- Request matched/tagged
- Loan approved/declined
- Pickup/return action required
- New contextual message
- Issue/resolution update
- Mark read behavior

## 15. Milestones and hard quality gates

The agent must work in this order. At the end of each milestone, report what changed, the build/test commands run, and remaining limitations. Do not move on after a failing gate.

### Milestone 0 — Baseline and architecture decision

Tasks:

- Complete the inspection and brainstorm in Section 3.
- Establish the exact Android and web build/run commands.
- Add or configure the web target with the smallest safe change if needed.
- Preserve the Android sample’s ability to run.

Gate:

- Android target builds and launches.
- Web target builds and opens in a browser.
- Screenshot each target showing a minimal shared BorrowCircle shell.
- No speculative version upgrades or unrelated refactors.

### Milestone 1 — Domain foundation and deterministic demo data

Tasks:

- Add models, repository interfaces, demo repositories, validation, pricing rules, loan state machine, and score calculators.
- Add coherent seed data.
- Add shared unit tests for scoring, pricing, validation, and state transitions.

Gate:

- All shared tests pass.
- Both platform targets still compile.
- Invalid transitions and invalid date/email inputs are demonstrably rejected.

### Milestone 2 — Theme, responsive shell, and navigation

Tasks:

- Implement light/dark themes and design tokens.
- Implement responsive Android/web scaffolds.
- Implement the five non-overlapping primary destinations.
- Add hover, focus, pressed, and selected states.
- Add restrained navigation transitions.

Gate:

- Every tab is reachable and has one clear purpose.
- Back navigation works from one detail page.
- Light and dark screenshots on web and Android have no clipped text, overlapping controls, or excessive rounding.
- Keyboard focus is visible on web.

### Milestone 3 — Discovery, listings, and requests

Tasks:

- Build Discover, Item Detail, Requests, Request Detail, Create Listing, and Create Request.
- Implement search and meaningful filters.
- Implement session-functional photo selection/preview or the documented fallback.
- Implement “I can help,” linking a listing to a request and notifying/tagging the requester.

Gate:

- User can create a listing and immediately find/open it.
- User can create a request and see it in Requests.
- Owner can link a listing to a request; linked listing remains public in the eligible circle.
- Search and at least two filters change visible results.
- Validate the complete flow with screenshots on both targets.

### Milestone 4 — Loan transaction and conversation

Tasks:

- Implement date selection, pricing estimate, send request, lender decision, Activity, transaction detail, and contextual conversation.
- Implement pickup condition snapshot and transition to Active.
- Implement normal return and feedback.

Gate:

- One seeded or newly created item completes the happy path from request through completed return.
- Both Borrowing and Lending views update correctly.
- Messages are scoped to the correct transaction.
- Dates and prices are calculated by shared code.
- Screenshots show the important states on Android and web.

### Milestone 5 — Damage/loss resolution

Tasks:

- Implement before/after comparison, issue categories, item freeze, borrower response, repair proposal, lender approval, agreed payment placeholder, moderator-review placeholder, and resolution completion.
- Implement lost/stolen report behavior and private temporary restriction messaging.

Gate:

- Damage cannot be finalized from accusation alone.
- Repair cannot proceed without recorded lender approval.
- Payment UI explicitly says no real payment is processed.
- A safety-related issue makes the item unavailable.
- Finalized resolution creates score evidence; unresolved issue does not.

### Milestone 6 — Profile, trust transparency, balance, and notifications

Tasks:

- Implement Profile and both detailed score screens.
- Display component weights, earned points, explanations, history confidence, and appeal action.
- Implement prototype balance/history and notifications.
- Implement theme switching from Profile/Settings.

Gate:

- Scores shown in UI exactly match tested shared calculations.
- New-member state is clear.
- Emails/NetIDs are absent from public/member-facing screens.
- Balance is unmistakably a prototype.
- Dark theme is visually checked on both platforms.

### Milestone 7 — Polish, accessibility, and demo hardening

Tasks:

- Add loading, empty, error, confirmation, and success states.
- Review wording and remove placeholder developer text.
- Verify contrast, semantic labels, focus order, touch sizes, scroll behavior, and clipping.
- Ensure seed data supports a deterministic 2–3 minute pitch.
- Add a concise README section with run commands, architecture, shared-code highlights, prototype limitations, and demo script.
- Only if everything above is stable, consider relay handoff or desktop as a stretch feature.

Gate:

- Clean build and tests from the documented commands.
- Android and web both complete the pitch flow.
- Visual screenshot review at phone and desktop-browser sizes.
- No visible dead buttons unless marked “Coming later”; preferably remove them.
- No runtime crash during the rehearsed demo.

## 16. Screenshot-driven visual verification

After every UI milestone:

1. Run the web app and Android app.
2. Capture screenshots using the available browser and emulator/IDE screenshot tools.
3. Inspect, do not merely capture, the images.
4. Check:
   - text clipping and truncation;
   - navigation selection;
   - content obscured by bottom navigation;
   - form keyboard/scroll issues;
   - narrow and wide layouts;
   - light and dark contrast;
   - modal/dialog sizing;
   - image aspect ratios;
   - excessively rounded containers;
   - hover/focus/pressed visibility;
   - empty and error states.
5. Fix issues and recapture the affected screen.

If screenshot or browser automation access is unavailable, say exactly what could not be verified and ask the user for a screenshot. Never claim visual verification that was not performed.

Minimum final screenshot set:

- Android Discover
- Android request or listing creation
- Android active transaction/condition check
- Android Profile score details in dark mode
- Web Discover at desktop width
- Web Requests
- Web damage-resolution view
- Web Profile score details in light mode

## 17. Demo narrative

Optimize seed data and navigation for this pitch:

1. Sign in as a verified NYU member.
2. Search for a projector and find that no suitable listing is available for the required time.
3. Post “Need a projector for Film Club tonight.”
4. Switch to a lender demo user or use a clear role-switch developer control.
5. Answer the request by creating/tagging a public projector listing.
6. Request the item and show the prototype price calculation.
7. Approve it and complete the photographed/checklist handoff.
8. During return, report a damaged or missing HDMI cable.
9. Compare pickup and return evidence.
10. Borrower proposes replacement or repair; lender approves; record resolution.
11. Show the transparent score evidence and the item becoming available again.
12. Briefly show the same shared experience on Android and web and explain which logic is shared.

Pitch line:

> Most rental apps help strangers monetize objects. BorrowCircle helps trusted communities respond to needs, circulate what they already own, and protect trust through documented care.

## 18. Explicit non-goals for the mandatory prototype

Do not spend mandatory milestone time on:

- Real money movement, card collection, payouts, or refunds
- Real insurance claims
- Actual integration with NYU SSO or directory services
- Automatic disclosure of university identity
- Production-grade identity verification
- GPS tracking or precise home-address maps
- AI-based guilt, fraud, damage, or trust-score decisions
- Public accusation feeds
- A complex administrator console
- Push notifications requiring store credentials
- iOS
- Desktop before Android and web are solid
- A backend migration before the local/demo flow works

## 19. Optional AI use, only after the core product works

AI is not necessary. If added, use it for a narrow assistive task, such as:

- suggesting a category and accessory checklist from a listing description;
- summarizing a long private dispute timeline for moderator review;
- proposing neutral wording for an issue report.

AI must not determine whether someone is guilty, assign scores, estimate damage charges, or expose private identity. Always let the user review generated content.

## 20. Definition of done

The prototype is satisfactory when:

- Android and web both build and run reliably.
- The interface is clean, responsive, lightly rounded rather than pill-heavy, and usable in light and dark modes.
- Users can both lend and borrow.
- Users can browse listings, post needs, answer needs with linked public listings, and receive visible notifications.
- Listing creation requires photos/previews and complete loan constraints.
- The happy-path loan workflow functions end to end.
- Condition evidence and the damage/loss workflow function coherently.
- Borrower Reliability and Lender Integrity are calculated by shared, tested Kotlin code and explained transparently in the UI.
- Profile is the bottom-right destination and includes both indicators and prototype balance.
- Verified identity is retained privately without exposing email/NetID to ordinary users.
- Screenshot inspection has been performed and visible problems corrected.
- README documents exact run commands, architecture, limitations, and the pitch demo.

## 21. Reference notes for the agent

- Current Kotlin documentation recommends Kotlin/Wasm with Compose Multiplatform when sharing both UI and logic with web, while Kotlin/JS is suitable when sharing logic with a web-native UI.
- Official Compose Multiplatform project guidance supports selecting Android and Web targets and placing reusable implementation in common source sets.
- Adapt these recommendations to the project’s currently compatible versions; do not blindly upgrade a working hackathon environment.

Useful official references:

- Kotlin web overview: <https://kotlinlang.org/docs/web-overview.html>
- Compose Multiplatform first app: <https://kotlinlang.org/docs/multiplatform/compose-multiplatform-create-first-app.html>

