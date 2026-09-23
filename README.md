# BorrowCircle

**Share more. Own less. Build trust locally.**

BorrowCircle is a Kotlin Multiplatform app for borrowing and lending items inside a trusted community. The prototype is built for the NYU Abu Dhabi community and runs on **Android and the web from one shared codebase**.

Built for the JetBrains Kotlin Multiplatform Challenge at the NYUAD Hackathon.

## Demo

[![Web Preview](https://img.youtube.com/vi/J3WsJGGK4m0/maxresdefault.jpg)](https://youtu.be/J3WsJGGK4m0)
[Android Preview](https://youtube.com/shorts/CGJbnlmmAXo?feature=share)

## The problem

People buy things they use once or twice — projectors, tripods, tools, camping gear, event supplies — while identical items sit unused a few rooms away.

Public rental marketplaces connect strangers but lack community context, accountability, and a fair way to handle damage. Group chats have the opposite problem: they feel familiar, but requests get buried and there is no loan history.

A community needs one place to see what is available nearby, ask for what is not listed, know who holds what and when it is due, document condition before and after, and resolve damage fairly.

## What makes BorrowCircle different

- **A verified circle, not an anonymous marketplace.** Membership is tied to a real community identity, so history and reputation follow the person.
- **Request-first matching.** If nothing is listed, post a need — lenders can answer with an existing item or a brand-new listing.
- **Evidence, not arguments.** Photos, functional checks, notes, and accessory lists are captured at pickup and at return, so disputes start from a record instead of memory.
- **Two-sided reputation.** *Borrower Reliability* and *Lender Integrity* are separate scores, so lending carelessly costs you just as much as borrowing carelessly.
- **A real dispute path.** Cleaning, missing parts, approved repairs, replacement, loss, or moderator review — with repairs gated behind lender approval and pending accusations kept out of scores.
- **One codebase, two platforms.** Transaction rules, pricing, validation, and most of the UI are shared Kotlin; Android gets phone-shaped screens and sheets, while wide web layouts use contextual side panels.

## Features

| Area | What it does |
| --- | --- |
| **Discover** | Search and filter items available in your community |
| **Item details** | Photos, condition, accessories, availability, pricing, max loan period, lender info |
| **Create a listing** | Item info, photos, availability, and free / daily / weekly / fixed-period pricing |
| **Community requests** | Post an unmet need and let lenders respond |
| **Borrow checkout** | Review dates, duration, pricing, and calculated total before requesting |
| **Activity** | Track loans through pending → approved → active → due → overdue → completed |
| **Condition records** | Compare pickup and return evidence side by side |
| **Issue resolution** | Cleaning, missing parts, repair, replacement, loss, or moderator review |
| **Profiles & scores** | Public participation and lender stats, without exposing private account details |
| **Responsive UI** | Shared Compose experience tuned for Android and web, light and dark themes |

## Trust model

BorrowCircle is designed for **bounded communities where membership can be verified**. Tying each account to one verified NYU identity means transaction history and reliability scores stay attached to a real person, repeated misuse limits future borrowing, and serious unresolved loss can be escalated through an authorized process.

Verification alone does not guarantee honesty, so the app also relies on condition evidence, structured handoffs, dispute review, and transparent score calculations. Ordinary users only ever see a display name, a verified-community badge, and reliability information — **full emails and NetIDs stay private**, and a lender cannot publicly expose a borrower or unilaterally declare them at fault.

> **Prototype note:** This version simulates NYU email verification with an `@nyu.edu` address. It is not connected to NYU's production identity or disciplinary systems, and no real payments are processed.

## Tech stack

- Kotlin Multiplatform + Compose Multiplatform
- Android target, and a web target via Kotlin/Wasm or Kotlin/JS
- Shared Kotlin domain models, validation, pricing, transaction workflows, and reliability calculations
- Gradle Kotlin DSL

```text
androidApp/   Android entry point
webApp/       Browser entry point (js + wasmJs)
shared/       Shared UI, domain logic, repositories, tests
desktopApp/   Optional future desktop target
gradle/       Wrapper and version catalog
```

## Running the prototype

**Requirements:** IntelliJ IDEA with Kotlin Multiplatform support, a compatible JDK as the Gradle JVM, the Android SDK with an emulator or device, and a modern browser.

**Android**

1. Open the project and let Gradle sync finish.
2. Start an emulator or connect a device.
3. Run the `androidApp` configuration.

**Web**

1. Run the `webApp[wasmJs]` or `webApp[js]` configuration.
2. IntelliJ opens the local dev address in your browser.

From the command line:

```bash
./gradlew :androidApp:assembleDebug        # build the Android APK
./gradlew :webApp:jsBrowserDevelopmentRun  # serve the web app on localhost:8080
```

If run-configuration names differ, check the Gradle tool window under the `webApp` browser tasks. Android builds need `local.properties` to point at your SDK (`sdk.dir=...`).

## Future improvements

- **Campus currency support.** Integrate campus dirhams so NYUAD students can pay with the currency they already hold instead of real money only. Most students carry a campus balance rather than spare cash, so accepting it removes the main payment barrier and makes paid loans genuinely accessible across the community.
- **Official NYU login.** Replace simulated `@nyu.edu` verification with real NYU SSO, so only actual members of the community can register and operate in the app. Real institutional authentication is what turns the trust model from a convention into a guarantee — it makes the circle verifiably closed, transparent, and accountable.
- **A better reliability score.** Evolve Borrower Reliability and Lender Integrity into a more reflective, transparent, and objective system: clearer weighting of each signal, a visible breakdown of how a score was reached, protection against a single bad dispute distorting a long good history, and measures that are harder to game than raw counts.

## Prototype limitations

- No real payment is collected or stored.
- NYU authentication is simulated, not connected to production SSO.
- Demo data and selected images may persist only for the current session.
- Insurance, legal enforcement, and institutional moderation require production partnerships and policy review.
- Reliability indicators are community-transaction measures, not financial credit scores.
- Desktop and iOS are not required targets for this prototype.

## Team

Built at the JetBrains NYUAD Hackathon by
[Tiffy Yu](https://www.linkedin.com/in/hsin-lun-yu-a49b763b1/),
Jackson Wei,
[Daria Lobanova](https://www.linkedin.com/in/dlobanova/), 
[Anastasia Gaynullina](https://www.linkedin.com/in/anastasia-gaynullina/)
