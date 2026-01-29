# Wigell Camping DB

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-Programmatic-4285F4?style=for-the-badge&logo=java&logoColor=white)
![Hibernate](https://img.shields.io/badge/ORM-Hibernate_6-59666C?style=for-the-badge&logo=hibernate&logoColor=white)
![MySQL](https://img.shields.io/badge/Database-MySQL_8-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

A comprehensive enterprise rental management system designed to modernize the camping equipment leasing workflow. It transitions from legacy flat-file storage to a robust **Relational Database** architecture, offering **dynamic pricing strategies**, **member history tracking**, automated profit calculation, and a responsive, localized user interface.

---

## 📸 Interface

### Login Screen
|                                      Login Screen                                       |
|:---------------------------------------------------------------------------------------:
| <img src="src/main/resources/screenshots/dark_mode.png" width="400" alt="Login Screen"> |
|                          *Prepared for Secure Authentication*                           |

### Dashboard & Rentals
| Home Dashboard | New Rental Wizard |
|:---:|:---:|
| <img src="src/main/resources/screenshots/home_view.png" width="400" alt="Home Dashboard"> | <img src="src/main/resources/screenshots/rental_dialog.png" width="400" alt="Rental Wizard"> |
| *Real-time availability & Status Overview* | *Streamlined Checkout with Dynamic Pricing* |

### Inventory & Management
| Inventory Grid | Member Administration |
|:---:|:---:|
| <img src="src/main/resources/screenshots/inventory_view.png" width="400" alt="Inventory Grid"> | <img src="src/main/resources/screenshots/member_view.png" width="400" alt="Member Admin"> |
| *Filterable Equipment & Vehicle Tracking* | *Member Tiers & Rental History* |

<details>
<summary><b>View Advanced Features</b></summary>
<br>

| Financial Reporting |                                     Light Mode UI                                      |
|:---:|:--------------------------------------------------------------------------------------:|
| <img src="src/main/resources/screenshots/profits_view.png" width="400" alt="Profits View"> | <img src="src/main/resources/screenshots/light_mode.png" width="400" alt="Light Mode"> |
| *Visualized Profit Analysis over Time* |                       *Themed CSS for Bright-Light Environments*                       |

</details>

---

## ✨ Key Features

* **Authentication & Security:** Secure entry via `LoginView` with role-based access potential.
* **Custom Window Management:**
    * **Undecorated Stage:** Custom-built title bar replacing standard OS chrome.
    * **Window Snapping:** Drag-to-edge logic for auto-resizing (Quarter/Half screen layouts).
    * **Session Timer:** Integrated usage timer in the title bar for admin session tracking.
* **Enterprise Persistence:** A complete migration to **MySQL** via **Hibernate ORM**, replacing legacy JSON handling for ACID-compliant data integrity.
* **Dynamic Pricing Engine:** Implements the **Strategy Pattern** to calculate costs based on membership tiers:
    * **Premium:** 24/7 support and priority booking.
    * **Student:** Adjusted rates for budget-friendly rentals.
    * **Standard:** Base market rates.
* **Smart History Logging:** Automatically tracks rental events in the `member_history` table, utilizing cascading integrity for robust audit trails.
* **Internationalization (i18n):** Native support for English and Swedish (`sv_SE`), instantly switchable within the application.
* **Polymorphic Inventory:** Handles diverse item types (Vehicles, Gear, Tents) using a "Table-per-Concrete-Class" database strategy.

---

## 🛠️ Technical Architecture

The application implements a layered **Service-Repository** architecture to ensure separation of concerns.

* **Dependency Injection:** A custom `ServiceContainer` manages the lifecycle of services and repositories, removing hard dependencies.
* **Hibernate ORM:** Annotated Entities (`@Entity`, `@Table`) map Java objects directly to SQL, handling complex relationships and lazy loading.
* **JavaFX 21:** Built strictly with **Programmatic JavaFX** (No FXML) for maximum performance, type safety, and custom component logic (`CustomTitleBar`).
* **Testing Strategy:**
    * **Unit Tests:** Mockito-based testing for Service logic.
    * **Integration Tests:** **H2 In-Memory Database** used for safe Repository testing without polluting the production MySQL instance.
* **Database Seeding:** Automatic `schema.sql` and `data.sql` execution ensures the environment is production-ready on the first launch.

---

## 🚀 Getting Started

### Prerequisites
1.  Ensure **MySQL Server** is running locally on port `3306`.
2.  Create a database named `wigell_camping_members_club`.

### Configuration
Modify `src/main/resources/hibernate.properties` if your database credentials differ:

```properties
hibernate.connection.username=root
hibernate.connection.password=YOUR_PASSWORD
```

---

## 📜 License

Distributed under the **MIT License**. Free for personal and commercial use.

---

<p align="center">
  <b>Developed by</b><br>
  <img src="src/main/resources/corner_logo.png" width="120" alt="Alexander Nilsson Logo"><br>
  Copyright (c) 2026 Alexander Nilsson
</p>
