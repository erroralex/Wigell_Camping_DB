# Projektarbete – Wigellkoncernen
## Uthyrningssystem med Hibernate

---

## Bakgrund

Du ska bygga ett uthyrningssystem för en medlemsklubb hos **Wigellkoncernen**.

Projektet ska byggas på samma struktur och arbetssätt som projektet  
**enhetstestDemo**, men nu med fokus på:

- persistens
- ORM
- Hibernate

Tanken är att du ska kunna använda **testDemot** som mall och inspiration för hur projektet ska byggas.

---

## Tekniska krav

Projektet ska uppfylla följande tekniska krav:

- Java + Maven
- Hibernate
    - SessionFactory
    - Session
    - Transaction
- Ingen Spring
- Ingen EntityManager
- Databas: **MySQL**

---

## Projektstruktur (obligatorisk)

Projektet ska ha **samma paketstruktur som testDemot**.

### Produktionskod
```
com.nilsson
├── entity
├── exception
├── repo
├── service
├── util
└── Main
```

```test
src/test/java/com.nilsson
├── service
└── repo
```


---

## Domänmodell (entity)

Du ska ha minst följande entities:

- **Member** (medlem)
- **Rental** (uthyrning)
- **Minst tre uthyrningsbara objekt**, till exempel:
    - Vehicle
    - Tool
    - MovieBox

❗ Det finns **ingen gemensam basklass** för uthyrningsobjekt.

### Koppling mellan Rental och uthyrningsobjekt

`Rental` ska kopplas till det som hyrs ut via:

- `rentalType` (enum)
- `rentalObjectId`

### Relationer

- **Rental → Member**
    - ManyToOne

---

## Repository-lager (repo)

För varje entity ska det finnas:

- Ett repository-interface
- En implementation (Impl-klass)

### Regler för repositories

- Hibernate `Session` och `Transaction` används här
- Repositories ska:
    - spara data
    - hämta data
    - söka data
- Repositories ska **inte innehålla affärslogik**

---

## Service-lager (service)

Services ska fungera på samma sätt som i **testDemot**.

### Krav på services

- Repositories skickas in via konstruktor
- Services innehåller:
    - affärslogik
    - validering
- Services ska **inte innehålla Hibernate-kod**

### Exempel på affärsregler

- Ett objekt får **inte bokas** om det redan är uthyrt
- Ogiltiga datum ska ge **exception**
- Pris ska räknas ut vid **avslutad uthyrning**

---

## Exceptions (exception)

- Egna exceptions ska användas för affärsregler

### Exempel

- Dubbelbokning
- Ogiltiga datum
- Saknad medlem

---

## Hibernate-konfiguration (util)

- En util-klass (t.ex. `HibernateUtil`) som:
    - skapar
    - håller i `SessionFactory`
- Konfiguration ska ske via:
    - `hibernate.properties`

---

## Användargränssnitt

### För betyget G

- En **konsolapplikation** räcker
- Applikationen ska:
    - köras tills användaren väljer att avsluta
- `Main` ska visa att systemet fungerar genom att:
    - skapa data
    - boka uthyrningar
    - avsluta uthyrningar
    - visa övrig funktionalitet i systemet

---

### För betyget VG

- Ett **grafiskt gränssnitt i JavaFX**
- Användaren ska kunna:
    - hantera medlemmar
    - hantera uthyrningsobjekt
    - boka och avsluta uthyrningar
    - se aktiva uthyrningar
    - utföra övriga funktioner i systemet

---

## Tester

### För betyget G

- Minst **6 enhetstester**
- Testerna ska:
    - testa service-lagret
    - använda Mockito för att mocka repositories
    - **inte innehålla Hibernate-kod**

---

### För betyget VG

- Alla krav för G är uppfyllda
- Dessutom:
    - Minst **2 integrationstester**
    - Tester repository-implementationer mot databas

---

## Bedömning

### Betyget G

- Projektet följer samma struktur som testDemot
- Hibernate används korrekt
- Affärslogik finns i service-lagret
- Konsolapplikationen fungerar
- Minst 6 enhetstester finns

---

### Betyget VG

- Alla G-krav är uppfyllda
- Samtliga VG-krav för tester är uppfyllda
- JavaFX-gränssnitt finns
- Integrationstester finns
- Robust felhantering
- Helheten känns som ett litet men seriöst system
- Uppgiften lämnas in i tid enligt kravspecifikationen

---

## Inlämning

- Projektet ska lämnas in **senast måndag 9/2 kl 23:59**
- Inlämning sker via **its learning**
    - GitHub-länk **eller**
    - zip-fil
- En **dagbok** över projektet ska bifogas:
    - analys
    - reflektion över det egna arbetet
- För VG krävs:
    - djupare
    - korrekt analys och reflektion

---

**Projektarbete – Wigellkoncernen**


