<div align="center">
  <img src="logo.png" width="800" alt="Logo">
</div>

# Uppgift: Uthyrningssystem med Hibernate (Wigellkoncernen)

## 1. Översikt
Du ska bygga ett uthyrningssystem för en medlemsklubb hos "Wigellkoncernen". Projektet fokuserar på **persistens, ORM och Hibernate** och ska följa samma struktur och arbetssätt som projektet `testDemo`.

* **Mål:** Skapa en robust backend med ett kopplat frontend (Konsol eller JavaFX).
* **Mall:** Använd `testDemo` som mall och inspiration för projektets struktur.

---

## 2. Teknisk Stack & Begränsningar
* **Språk:** Java
* **Byggverktyg:** Maven
* **Persistens:** Hibernate ORM
    * **Måste använda:** `SessionFactory`, `Session`, `Transaction`.
    * **Förbjudet:** Spring Framework ("Ingen Spring").
    * **Förbjudet:** `EntityManager` (Du ska använda ren Hibernate, inte JPA-standardinterfacet).
* **Databas:** MySQL.
* **Konfiguration:** `hibernate.properties`.

---

## 3. Projektstruktur (Obligatorisk)
Projektet måste följa exakt denna paketstruktur (under ditt grupp-ID, t.ex. `com.wigell` eller `com.nilsson`):

* `src/main/java`
    * `entity` (Domänmodeller)
    * `repo` (Dataåtkomst-interface och implementationer)
    * `service` (Affärslogik)
    * `exception` (Egna undantag/exceptions)
    * `util` (Hjälpklasser för konfiguration)
    * `Main` (Startpunkt)
* `src/test/java`
    * `service` (Enhetstester)
    * `repo` (Integrationstester)

---

## 4. Domänmodell (Entities)
Alla entiteter ska annoteras för Hibernate.

### Krav på Entiteter
1.  **Member** (Medlem)
2.  **Rental** (Uthyrning)
3.  **Uthyrningsobjekt** (Minst 3 olika typer). Exempel:
    * `Vehicle`
    * `Tent`
    * `Gear`

### Specifika Modelleringsregler [VIKTIGT]
* **Ingen gemensam basklass:** Det får **inte** finnas en gemensam entitets-basklass (arv) för uthyrningsobjekten.
* **Uthyrningsrelation:** `Rental` ska kopplas löst till objektet som hyrs ut via:
    1.  `rentalType` (Enum)
    2.  `rentalObjectId` (ID-referens)
* **Medlemsrelation:** `Rental` har en **ManyToOne**-relation till `Member`.

---

## 5. Arkitekturlager

### A. Repository-lager (`repo`)
* **Struktur:** Skapa ett **Interface** och en **Implementation** per entitet (t.ex. `MemberRepository` och `MemberRepositoryImpl`).
* **Ansvar:**
    * Spara, hämta och söka data.
    * **Ingen affärslogik** får finnas här.
* **Teknik:** Måste använda Hibernate `Session` och `Transaction` direkt.

### B. Service-lager (`service`)
* **Struktur:** Vanliga Java-klasser.
* **Ansvar:**
    * Affärslogik och validering.
    * **Ingen Hibernate-kod** får finnas här (strikt separation).
* **Dependency Injection:** Repositories ska skickas in via **konstruktorn**.
* **Exempel på regler:**
    1.  Ett objekt får **inte** bokas om det redan är uthyrt.
    2.  Ogiltiga datum ska kasta exception.
    3.  Pris ska räknas ut vid avslutad uthyrning.

### C. Exception-lager (`exception`)
* Skapa **egna exceptions** för affärsregler.
* Exempel: Dubbelbokning, ogiltiga datum, saknad medlem.

### D. Verktyg (`util`)
* Skapa en hjälpklass (t.ex. `HibernateUtil`).
* Ansvar: Skapa och hålla i `SessionFactory` (Singleton).

---

## 6. Krav på Användargränssnitt

### För Betyg G (Godkänt)
* **Typ:** Konsolapplikation.
* **Funktionalitet:**
    * Ska köras tills användaren väljer att avsluta.
    * Demonstrera systemets funktionalitet:
        * Skapa data.
        * Boka.
        * Avsluta bokning.
        * Övrig funktionalitet i systemet.

### För Betyg VG (Väl Godkänt)
* **Typ:** Grafiskt gränssnitt i JavaFX.
* **Funktionalitet:**
    * Hantera medlemmar.
    * Hantera uthyrningsobjekt.
    * Boka och avsluta uthyrningar.
    * Se aktiva uthyrningar.
    * Utföra övriga funktioner i systemet.

---

## 7. Testkrav

### För Betyg G (Godkänt)
* **Omfattning:** Enhetstester.
* **Mål:** Service-lagret.
* **Antal:** Minst **6 tester**.
* **Verktyg:** JUnit + Mockito.
* **Begränsning:** Repositories ska **mockas**. Ingen Hibernate/DB-kod i dessa tester.

### För Betyg VG (Väl Godkänt)
* **Omfattning:** Integrationstester.
* **Mål:** Repository-lagret (implementationer).
* **Antal:** Minst **2 tester**.
* **Syfte:** Testa repository-implementationer mot en databas.

---

## 8. Bedömningskriterier

### Betyg G
* [ ] Projektet följer samma struktur som `testDemot`.
* [ ] Hibernate används korrekt.
* [ ] Affärslogik ligger i service-lagret.
* [ ] Konsolapplikationen fungerar.
* [ ] Minst 6 enhetstester (Service-lager, mockade repon).

### Betyg VG
* [ ] Alla G-krav är uppfyllda.
* [ ] Samtliga VG-krav för tester är uppfyllda (Integrationstester).
* [ ] JavaFX-gränssnitt implementerat.
* [ ] Robust felhantering.
* [ ] Helheten känns som ett litet men seriöst system.
* [ ] Djupare och korrekt analys samt reflektion i projektdagboken.

---

## 9. Inlämning
* **Deadline:** Måndag 9/2 kl 23:59.
* **Format:** GitHub-länk eller zip-fil på It's Learning.
* **Bifogas:** En dagbok över projektet med analys och reflektion över det egna arbetet.