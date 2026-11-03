# Hope Bridge - Non-Profit Organization Management System

CSE336 Software Design Patterns - Major Task, **Phase 1 submission** (Fall 2026),
Faculty of Engineering, Ain Shams University.

## Team

| Name | Student ID | Area |
|---|---|---|
| Saifeldin Tamer ElSayes | 22P0233 | Foundation, people, lookup tables (Singleton, CRUD, MVC entry) |
| Ahmed Khaled Hamdy | 22P0024 | Observer and notification core |
| Mohamed Emad Eldeen | 22P0065 | Donations, payment and notification channels |
| Seif Eldeen Ibrahim | 22P0023 | Events and volunteer logic |
| Yousef Sameh Fathi | 22P0010 | Decorators (documents, tickets) and volunteer output |
| Mohamed Khaled Mohamed Anwar | 22P0041 | Beneficiaries and impact evaluation |

The class-by-class split is in section 7 of the SRS.

## Contents

| Folder / file | What it is |
|---|---|
| `1- SRS Document/` | Software Requirements Specification (Word and PDF) |
| `2- Code/NonProfitSystem/` | Java 17 Maven project: shared model, console UI, Spring Boot website |
| `3- GitHub Repo.txt` | Link to this repository |
| `4- Grading Criteria/` | Phase 1 grading sheet with team names and IDs |
| `Class Diagrams/` | 7 class diagrams (PNG + SVG) and their PlantUML source |

## Phase 1 grading items

| Item | Where |
|---|---|
| Class diagram | `Class Diagrams/01-full-class-diagram` (94 classes, 167 relationships) |
| Strategy | `PaymentStrategy` (MonetaryDonation), `NotificationStrategy` (NotificationService), `ImpactEvaluationStrategy` (ImpactEvaluator) |
| Observer | `Subject` / `Observer` / `ChangeEvent`; subjects `DonationService`, `EventService`, `DistributionService`; 6 observers |
| Decorator | `Document` + `DocumentDecorator` (receipts, certificates); `Ticket` + `TicketDecorator` (VIP, meal) |
| Factory (Factory Method) | `DonationHandler` and `EventCreator` hierarchies |
| Singleton | `DatabaseManager` (with `GenericDAO<T>`) |
| MVC | `model`, `view`, `controller` packages |
| Client requirements | SRS section 4 |
| CRUD lookup tables | `Skill`, `ItemCategory`, `ResourceCategory` in `GenericDAO`, via `LookupController` / `LookupView` |

## Run the website

Requires JDK 17 or newer. Maven is included through the Maven Wrapper; the first
build needs internet access to download Maven and project dependencies.

**On macOS:** double-click `start-website.command` in the repository root. It
starts Spring Boot and opens your browser when the website is ready. Keep its
terminal window open; press **Control+C** to stop the server.

**From a terminal:**

```bash
cd "2- Code/NonProfitSystem"
./mvnw spring-boot:run
```

Then open **http://localhost:8080**. On Windows, use `mvnw.cmd spring-boot:run`.
In IntelliJ or NetBeans, import this folder as a Maven project and run
`com.nonprofit.web.NonProfitWebApplication`.

The browser interface includes:

- Dashboard with live campaign progress, stock, people, and upcoming events.
- People registration, search, contact updates, and guarded deletion.
- Monetary and in-kind donations, payment strategies, decorated receipts, and annual statements.
- Event creation, registrations, attendance, schedule/venue updates, reminders, tickets, and completion.
- Volunteer matching, assignments, hours, and signed certificates.
- Beneficiary needs, stock replenishment, distributions, and impact evaluation.
- Lookup-table CRUD, activity history, and notification inboxes.

This is a **local demonstration workspace** with no access-control/login gate.
It listens on `127.0.0.1` by default. All records are in memory and reset when
the server stops. Payment and notification channels remain Phase 1 simulations;
use demo payment references. Sample accounts retain the password `pass123` for
the original console authentication feature.

`ApplicationServices` wires the original services and observers once, and both
interfaces reuse that composition. Spring MVC controllers adapt browser forms
to these services; Thymeleaf renders the pages. Forms use per-session CSRF tokens,
and browser requests are serialized around the mutable Phase 1 data store.

### Build and test

```bash
cd "2- Code/NonProfitSystem"
./mvnw test
./mvnw package
java -jar target/NonProfitSystem-1.0-SNAPSHOT.jar
```

The integration suite checks every page and the main workflows, including
receipt/notification observers, stock deduction, decorated tickets and
certificates, input validation, and protected lookup deletion.

### Run the original console

```bash
cd "2- Code/NonProfitSystem"
./mvnw compile
java -cp target/classes com.nonprofit.Main
```

Or run `com.nonprofit.Main` from your IDE. The original Phase 1 diagrams describe
the domain and console classes; the new web adapter is outside those diagrams.

## Regenerating the diagrams

All diagrams come from one shared model, `Class Diagrams/source/model.iuml`:

```bash
cd "Class Diagrams"
docker run --rm -v "$PWD":/data -w /data plantuml/plantuml:1.2024.7 -tpng -o /data "source/0*.puml"
```
