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
| `2- Code/NonProfitSystem/` | Java 17 Maven project: 94 classes, MVC, console UI |
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

## Run

Requires JDK 17+ and Maven.

```bash
cd "2- Code/NonProfitSystem"
mvn compile
java -cp target/classes com.nonprofit.Main
```

Or open `2- Code/NonProfitSystem` as a Maven project in NetBeans / IntelliJ and run `com.nonprofit.Main`.
Sample accounts use the password `pass123`.

## Regenerating the diagrams

All diagrams come from one shared model, `Class Diagrams/source/model.iuml`:

```bash
cd "Class Diagrams"
docker run --rm -v "$PWD":/data -w /data plantuml/plantuml:1.2024.7 -tpng -o /data "source/0*.puml"
```
