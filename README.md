# BorrowCircle

**Share more. Own less. Build trust locally.**

BorrowCircle is a Kotlin Multiplatform application for borrowing and lending useful items inside trusted communities. The current prototype is designed around the NYU Abu Dhabi community and runs on Android and the web.

## Background

People frequently buy items they use only once or twice: projectors, tripods, calculators, tools, camping equipment, event supplies, and small appliances. At the same time, identical items often sit unused in nearby rooms.

General rental marketplaces can connect strangers, but they do not always provide enough community context, accountability, condition evidence, or a fair process for resolving damage. Informal group chats have the opposite problem: they may feel familiar, but requests become buried and there is no reliable loan history.

## The problem

Communities need a simple way to:

- discover items already available nearby;
- ask for something even when no listing exists;
- know who currently holds an item and when it is due;
- evaluate borrowers and lenders using transparent evidence;
- document an item's condition before and after a loan;
- handle damage, missing items, and disagreements fairly;
- reduce unnecessary purchases, cost, clutter, and waste.

## Our solution

BorrowCircle creates a verified sharing circle rather than an anonymous public marketplace. Every member can both borrow and lend.

Users can browse available items, publish listings, post community requests, respond to requests, propose prices, and follow each transaction through Activity. Condition checks and two separate reliability indicators create accountability for both sides of a loan.

## Core functions

- **Discover listings:** Search and filter items available within the community.
- **Item details:** View photos, condition, accessories, availability, pricing, maximum loan period, and lender information.
- **Create a listing:** Add item information, photographs, availability, and free, daily, weekly, or fixed-period pricing.
- **Community requests:** Post an unmet need when the required item is not currently listed.
- **Request-first matching:** Lenders can answer a community request using an existing item or a newly created public listing.
- **Borrow checkout:** Review dates, duration, pricing, and the calculated prototype total before sending a request.
- **Activity tracking:** Follow borrowing and lending transactions through pending, approved, active, due, overdue, and completed states.
- **Condition records:** Compare pickup and return photos, functional checks, notes, and included accessories.
- **Issue resolution:** Record cleaning, missing-part, repair, replacement, loss, or moderator-review outcomes. Repairs require lender approval.
- **Member profiles:** View public community participation and lender statistics without exposing private account details.
- **Borrower Reliability:** Measures timely returns, item care, completed handoffs, communication, and resolution follow-through.
- **Lender Integrity:** Measures listing accuracy, item readiness, reliable handoffs, responsiveness, fair resolution, and borrower feedback.
- **Responsive interface:** Shared Compose experience optimized for Android and web, including light and dark themes.

## Trust through NYU authentication

BorrowCircle is intended for bounded communities where membership can be verified. In the NYUAD deployment, each account would be connected to a unique NYU identity rather than an easily discarded anonymous account.

This improves trust in several ways:

- one verified person is associated with each account;
- transaction history and reliability indicators remain connected to that identity;
- repeated misuse affects the member's ability to borrow in the future;
- serious unresolved loss or suspected theft can be escalated to an authorized community moderator;
- the possibility of institutional follow-up creates a deterrent against intentional non-return.

Verification does not guarantee honesty or eliminate theft. BorrowCircle therefore also uses condition evidence, structured handoffs, dispute review, and transparent score calculations. A lender cannot publicly expose a borrower's email or unilaterally declare them responsible.

Ordinary users see a display name, verified-community badge, and relevant reliability information. Full email addresses and NetIDs remain private. Identity access for a serious incident would require an authorized institutional process.

> **Prototype note:** The hackathon version may simulate NYU email verification using an `@nyu.edu` address. It does not claim to be integrated with NYU's production identity or disciplinary systems.

## Damage and loss approach

BorrowCircle records a condition snapshot at pickup and return. If a problem occurs:

1. The item can be temporarily removed from availability.
2. Both parties can compare before-and-after evidence.
3. The borrower can respond or add evidence.
4. The parties can agree on cleaning, replacement of a missing part, an approved repair, or a prototype payment agreement.
5. Unresolved cases can be sent for moderator review.

Pending accusations do not automatically affect reliability scores. Repairs may proceed only after the lender approves the proposal, and no real payments are processed in the prototype.

## Technology

- Kotlin Multiplatform
- Compose Multiplatform
- Android target
- Web target using Kotlin/Wasm or the project's configured browser target
- Shared Kotlin domain models, validation, pricing, transaction workflows, and reliability calculations
- Gradle Kotlin DSL

The project demonstrates why Kotlin Multiplatform is valuable: the transaction rules and much of the interface can be implemented once while still adapting interactions to each platform. Android uses phone-appropriate screens and sheets, while wide web layouts can use contextual right-side panels.

## Project structure

```text
androidApp/     Android application entry point
webApp/         Browser application entry point
shared/         Shared UI, domain logic, repositories, and tests
desktopApp/     Optional future desktop target
gradle/         Gradle wrapper configuration
```

The exact source-set organization can be inspected under the individual modules.

## Running the prototype

### Requirements

- IntelliJ IDEA with Kotlin Multiplatform support
- Compatible JDK configured as the Gradle JVM
- Android SDK and an Android emulator or physical Android device
- A modern browser for the web target

### Android

1. Open the project in IntelliJ IDEA.
2. Allow Gradle synchronization to finish.
3. Start an Android emulator or connect a device.
4. Select the `androidApp` run configuration.
5. Click **Run**.

### Web

1. Select the configured web run configuration, typically `webApp[wasmJs]` or `webApp[js]`.
2. Click **Run**.
3. IntelliJ will open the local development address in a browser.

If run-configuration names differ, inspect the Gradle tool window under the `webApp` browser tasks.

## Prototype limitations

- No real payment is collected or stored.
- NYU authentication may be simulated rather than connected to production SSO.
- Demo data and selected images may persist only for the current session.
- Insurance, legal enforcement, and institutional moderation require production partnerships and policy review.
- Reliability indicators are community-transaction measures, not financial credit scores.
- Desktop and iOS are not required targets for the current hackathon prototype.

## Hackathon context

BorrowCircle was created for the JetBrains Kotlin Multiplatform Challenge: build a cross-platform application that supports communities, raises awareness, or helps solve a real-world problem.

The prototype focuses on a complete, understandable community-sharing workflow across Android and web while demonstrating shared Kotlin logic and platform-responsive design.

## Team

Built at the JetBrains NYUAD Hackathon.

