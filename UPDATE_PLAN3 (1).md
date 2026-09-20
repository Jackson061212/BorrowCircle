# BorrowCircle — Update Plan 3

## Short launcher prompt

> Read `IMPLEMENTATION_PLAN.md` and `UPDATE_PLAN1.md` through `UPDATE_PLAN3.md`. Update Plan 3 is the immediate priority. This project does not use Git: inspect and run the current Android and web versions before editing, preserve working code, and reproduce each issue first. Implement the stages in order, verifying each on Android and web with tests and screenshots. Do not make unrelated dependency or build-tool upgrades.

---

## 1. Goals

This update completes the core marketplace transaction loop and improves requests, listing prices, member profiles, activities, and wide-web panels.

Priorities:

1. Remove duplicate request-card actions.
2. Make wide-web panels behave like real independently scrollable side panels.
3. Improve listing price hierarchy and make lender names open member profiles.
4. Make `Request to borrow` create a real pending activity.
5. Add a receipt-style borrow checkout with duration-based pricing.
6. Add request search/filter/sort and a functional `I can help` offer flow.
7. Seed coherent users, ratings, statistics, and activity states.

## 2. Inspect before editing

Because the project has no Git history, first inspect the current files and run both platforms. Identify:

- request and listing card components;
- detail-panel implementation and its scroll container;
- current user seed data and avatar logic;
- member/profile routes;
- item/request pricing types;
- `Request to borrow` and `I can help` callbacks;
- Activity data model, tabs, and status rendering;
- notifications and due-date rendering;
- existing tests and exact Android/web run commands.

Reproduce every reported issue and state which files will be changed. Reuse current navigation, repositories, score calculators, and panels rather than creating competing implementations.

## 3. Request cards: remove duplicate Open action

Requests currently show `Open` twice: a status indication at the top-right and a button at the bottom-right.

- Keep the top-right **Open** status badge. It indicates request state and is not a button.
- Delete the bottom-right `Open` button.
- Make the entire card clickable/tappable to open request details.
- Provide hover, focus, pressed/ripple, pointer cursor, Enter/Space activation, and a semantic label.
- Retain top-right alternatives such as `Matched`, `Fulfilled`, or `Closed` for other states.
- Clicking status text must not create a second navigation event.

## 4. Wide-web right-side panels

On wide web, item details, request details, Create flows, checkout, and `I can help` review must use a panel that slides in from the right—not a centered popup.

Panel requirements:

- Use a responsive width breakpoint based on available size, approximately 840–960 dp initially.
- Width approximately 400–520 dp or 34–44% of the viewport, with a readable maximum.
- Full available height beneath/within the app shell.
- Animate from the right in roughly 180–260 ms.
- Give it a light-theme white/neutral surface, dark-theme charcoal surface, left border, and soft shadow.
- The panel body scrolls independently using mouse wheel/trackpad and keeps its header/close action reachable.
- Long receipts, profiles, forms, and details must scroll to the final action.
- A clear close icon closes it; Escape and browser/in-app Back should also work when supported.
- Closing restores focus to the card/button that opened it.
- Do not stack panels. Selecting another item replaces panel content.
- The underlying feed remains visible so users retain context.
- Read-only detail panels may be non-modal. Checkout/forms must prevent accidental background actions but should still appear as side panels, using at most a subtle scrim.
- On Android and compact web, retain full-screen detail routes or mobile-friendly sheets/dialogs.

Represent panel content with one exclusive state rather than several booleans, for example:

```text
Closed
ItemDetails(itemId)
RequestDetails(requestId)
MemberProfile(userId)
BorrowCheckout(itemId)
HelpOffer(requestId)
CreateListing(linkedRequestId?)
```

## 5. Listing-card price and lender presentation

The supplied screenshot shows price, availability, and lender compressed into one sentence. Separate them.

Recommended card hierarchy:

1. Title
2. Category and condition
3. Short description
4. A bottom metadata row containing:
   - compact green price block;
   - availability text;
   - clickable lender name.

### Green price block

- Display examples such as `8 AED/day`, `25 AED/week`, `40 AED total`, or `Free`.
- Use a small muted-green rectangular/chip surface beneath or adjacent to the price, with accessible dark-green text.
- Keep corner radius modest; do not use a large pill.
- Dark mode uses a deeper green surface and readable light-green text.
- Do not color the entire metadata line green.

### Clickable lender

- Render `by Maya`, `by Omar`, etc. with the name visually identifiable as a link.
- Clicking the lender name opens that member’s profile, not the item again.
- Prevent the parent card click from firing simultaneously.
- Do not expose email, NetID, private dispute history, or exact address.

## 6. Member profiles and seeded users

### Current user

Change the signed-in demo user from Maya to **Jackson**:

- display name: Jackson;
- avatar fallback: `J` rather than `M`;
- verified NYU community badge;
- Profile tab must consistently show Jackson across Android and web.

Jackson’s Profile tab should show:

- Borrower Reliability score and breakdown link;
- Lender Integrity score and breakdown link;
- completed borrows;
- successful/on-time returns;
- completed loans as lender;
- listing accuracy rate;
- active listings;
- open borrowing/lending activities;
- prototype balance and impact data if already present.

Use coherent non-perfect dummy statistics so they match the existing score formulas. Do not hard-code a visible score inconsistent with its evidence.

### Other dummy members

Create several coherent members, for example Maya, Omar, Priya, and Lina. Each should have:

- avatar initials;
- verified-circle badge;
- short member-since or participation summary;
- Lender Integrity score/confidence;
- successful completed loans;
- listing accuracy percentage;
- handoff reliability/on-time handoffs;
- borrower experience rating and review count;
- active public listings;
- a few structured public reviews without private dispute allegations.

Member profile pages/panels must distinguish `Lender Integrity` from a general star rating and provide a `View breakdown` action when the score system is implemented.

## 7. Borrow checkout and pricing logic

The `Request to borrow` button must open a review/checkout flow and then create a transaction.

### Receipt-style checkout

Show:

- item thumbnail and title;
- lender summary;
- pricing policy;
- pickup/start date;
- selected return date or fixed return date;
- duration;
- included accessories/condition reminder;
- prototype payment disclaimer;
- receipt-style line items;
- prominent total aligned at the right of the summary row;
- final `Send borrow request` action.

On wide web this is a right panel. On Android/compact layouts use a full-screen or scrollable mobile sheet.

### Listing pricing types

Use explicit sealed/domain types rather than parsing display strings:

```text
DailyRate(amount, minDays, maxDays)
WeeklyRate(amount, minWeeks, maxWeeks) // preserve only if already supported
FixedPeriod(totalAmount, startDate, returnDate)
FreeLoan(minDays, maxDays)
```

- For `DailyRate`, user selects valid start/return dates up to the maximum period; total updates immediately as `daily rate × billable days`.
- Define and test one consistent billable-day rule. A same-day/under-24-hour loan counts as at least one day.
- For `WeeklyRate`, use the existing documented rule and show it clearly; do not silently mix daily and weekly math.
- For `FixedPeriod`, dates and duration are locked; the user cannot choose a different duration.
- For `FreeLoan`, duration rules still apply but total remains `Free`.
- Use integer minor currency units or another exact money representation; do not use imprecise floating-point arithmetic.
- Prevent dates in the past, return before pickup, duration beyond maximum, or unavailable periods.

### Confirmation behavior

The prototype processes no real payment. The button must say `Send borrow request`, not imply a real charge. Display:

> Prototype checkout — no real payment will be processed.

After confirmation:

- create one loan transaction with a stable ID;
- status becomes `Pending approval`;
- add it to Jackson’s Activity → Borrowing immediately;
- add the corresponding incoming request to the lender’s Activity → Lending in shared demo state;
- add a notification for the lender;
- close/navigate away from checkout and show a success acknowledgement;
- repeated clicking must not create duplicates.

## 8. Activity model, seeded activity, and deadlines

Activity must contain separate `Borrowing` and `Lending` views.

Recommended transaction statuses:

```text
PendingApproval
ApprovedAwaitingPickup
Active
ReturnDue
Overdue
ReturnReview
Completed
Declined
Cancelled
```

Show a clear next action on every non-terminal activity.

Seed examples for Jackson:

- Jackson requested an item: pending approval.
- Someone requested Jackson’s item: incoming/pending.
- One Jackson lending transaction: approved and awaiting pickup.
- One active borrow with a return deadline.
- One finished borrow or loan.

### Return deadline indication

For approved/active unreturned loans, show a deadline badge with text such as `Return by 18 Oct`.

- Use red with an icon/text for a due-soon or overdue deadline.
- Overdue state must explicitly say `Overdue`.
- If the deadline is far away, use a less alarming warning/neutral treatment; never rely only on color.
- Pending approval has no active return countdown yet.
- Completed/declined/cancelled activities show no return warning.

## 9. Community-request discovery

Add to the Requests page:

- search by title, description, category, and requester display name;
- category filter with `All` plus available categories;
- sort options:
  - `Most recent` (default);
  - `Oldest`;
  - optionally `Budget high to low` when comparable;
- result count;
- clear-filters action;
- useful no-results state.

Search/filter/sort must combine predictably and operate on the same source list. Preserve query/filter state while opening and closing request details. Use local filtering for demo data; do not add a backend solely for search.

## 10. Request pricing options

When creating a community request, require exactly one pricing constraint:

```text
ExactPrice(amount, basis)
PriceRange(minAmount, maxAmount, basis)
MaximumBudget(maxAmount, basis)
```

Where `basis` is explicitly one of:

```text
PerDay
FixedPeriod(startDate, returnDate)
```

Rules:

- Exact price: requester sets the amount; lender cannot change it.
- Range: requester sets inclusive minimum and maximum; lender selects an offer inside that range.
- Maximum budget: lender selects an amount from zero/free through the maximum.
- Minimum cannot exceed maximum.
- Amounts cannot be negative.
- Fixed-period requests lock the requested dates/duration.
- Per-day requests show the rate basis clearly and compute an estimated total from requested days.
- Store price constraint as a domain type, not display text.

The request card/detail should show a concise representation such as `Exact: 10 AED/day`, `5–12 AED/day`, or `Budget up to 60 AED total`.

## 11. Functional I can help offer flow

Clicking `I can help` opens a receipt-style offer panel/sheet.

Show:

- request and requester summary;
- dates/duration;
- description and category;
- pricing constraint;
- option to link an existing eligible item or create a new listing;
- proposed price when adjustable;
- calculated total for per-day requests;
- handoff summary;
- final `Send offer` action.

Price control:

- `ExactPrice`: read-only locked price.
- `PriceRange`: selectable only within min/max.
- `MaximumBudget`: selectable only at or below maximum.
- Display inline errors and disable Send when invalid.

After Send:

- create one offer with a stable ID;
- offer status becomes `Waiting for requester`;
- show it in Jackson’s Activity → Lending as awaiting the requester’s acceptance;
- show the corresponding incoming offer in the requester’s activity/notification data;
- do not mark the request matched until the requester accepts;
- requester can accept or decline in their seeded/demo view;
- acceptance links the listing/item, advances the transaction consistently, and updates both sides;
- prevent duplicate submissions.

Recommended offer states:

```text
WaitingForRequester
Accepted
Declined
Withdrawn
```

## 12. State consistency

All flows must update one shared repository/state source so cards, details, Activity, profiles, and notifications remain consistent.

- Do not separately hard-code the same transaction in multiple screens.
- Derive profile statistics and visible scores from seed/evidence where practical.
- Item, request, loan, and offer IDs must be stable.
- Use centralized status transitions and reject invalid transitions.
- Demo persona switching, if available, must reveal the corresponding other side of the same transaction rather than a disconnected duplicate.

## 13. Implementation stages

### Stage 0 — Audit

- Reproduce issues, inspect current architecture, run both targets, and list files to change.

Gate: exact root causes and run commands are known.

### Stage 1 — Request-card cleanup, panels, price block, and member profiles

- Remove the duplicate Open button.
- Repair the wide-web right panel and independent scrolling.
- Add the green price block and clickable lender links.
- Make Jackson the current user and add coherent member profiles/statistics.

Gate: Android and web pass interaction and screenshot checks; no panel content is unreachable.

### Stage 2 — Borrow checkout and Activity lifecycle

- Implement receipt checkout, duration pricing, request creation, Activity updates, seeded activities, and deadline badges.

Gate: sending one request creates exactly one pending activity on both relevant sides; pricing tests pass.

### Stage 3 — Request discovery and I can help

- Implement search/category/sort, request price constraints, receipt-style offer flow, and shared offer activities.

Gate: valid offers update both sides; exact/range/maximum rules are enforced; request is not matched before acceptance.

### Stage 4 — Regression and polish

- Verify all earlier Update Plans, both themes, responsive layouts, accessibility, and screenshots.

Gate: Android and web builds/tests succeed with no regression to item/request opening, Create, scrolling, images, or profiles.

## 14. Required tests

At minimum test:

- request card has one Open status and no Open button;
- whole request card opens correct ID;
- panel exclusivity, scroll, close, and focus restoration;
- lender-name click does not also open item details;
- current user is Jackson with `J` fallback;
- daily/fixed/free pricing and boundary dates;
- fixed-period dates cannot be edited;
- duplicate borrow submission is prevented;
- borrow request creates correct two-sided activity state;
- deadline badges appear only for eligible states;
- search plus category plus sort composition;
- exact request price locked;
- range and maximum validation;
- offer creates correct two-sided waiting state;
- acceptance advances both sides consistently.

## 15. Required visual verification

Inspect screenshots and interactions for:

- request cards without duplicate buttons;
- screenshot-inspired listing card with separate green price block;
- lender link and another member’s profile;
- Jackson’s Profile with `J` avatar and statistics;
- wide-web independently scrolling side panel;
- Android detail/sheet alternative;
- borrow checkout with one-day, multi-day, and fixed-period cases;
- pending, approved, active/due, overdue, and completed activities;
- Requests search/category/sort;
- exact-price, range, and maximum-budget offer receipts;
- light/dark and wide/narrow layouts.

Check for clipped totals, inaccessible final buttons, stacked panels, dead clicks, duplicate activities, inconsistent users, incorrect prices, and hidden deadlines.

## 16. Completion report

Codex must report:

- files inspected and modified;
- stages completed;
- exact Android/web commands and results;
- tests and results;
- screenshots inspected;
- pricing rules implemented;
- seeded users, activities, and statistics;
- whether shared state is session-only;
- unverified behavior and known limitations.

Do not add real payments, expose private identity, or begin unrelated backend, desktop, iOS, or AI work.

