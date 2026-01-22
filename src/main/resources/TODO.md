# 📋 Project TODO List: Wigell_Camping_DB

## 🔴 1. Critical Backend Fixes (Must do first)
These are logical gaps preventing the application from functioning correctly based on the code analysis.

- [X] **Fix Entity IDs:** Ensure `Vehicle.java` and `Tent.java` have public `getId()` methods.
    - *Why:* `RentalService` cannot link an ID to a Rental without these.
- [ ] **Implement `returnItem` in `RentalService.java`:**
    - [ ] Create a method `returnItem(Long rentalId)`.
    - [ ] Fetch the Rental.
    - [ ] Calculate the cost: `(ReturnDate - StartDate) * ItemPrice`.
    - [ ] Update the `Rental` entity (set `returnDate` and `totalCost`).
    - [ ] **Switch/Case Logic:** Based on `rental.getRentalType()`, find the specific item (Vehicle/Tent/Gear) in its repository and set `isRented = false`.
    - [ ] Save changes to both the Rental and the Item.
- [ ] **Add Business Logic Validation:**
    - [ ] Ensure `startDate` is not in the past (in `rentItem`).
    - [ ] Ensure `returnDate` is not before `startDate` (in `returnItem`).

## 🟢 2. Assignment Compliance (The "Rules")
Checklist to ensure you don't lose points on technicalities.

- [ ] **Verify "Pure" Hibernate:**
    - [X] Check all Repositories (`*Impl.java`). Ensure **NO** `EntityManager` is used. Only `SessionFactory`, `Session`, and `Transaction`.
    - [X] Ensure **NO** Spring annotations (`@Autowired`, `@Service`, `@Repository`) are present anywhere.
- [ ] **Dependency Injection:**
    - [X] Verify `RentalService` receives its repositories via the **Constructor** (Done, but double-check if you add new services).
- [ ] **No Base Class:**
    - [X] Verify `Vehicle`, `Tent`, and `Gear` do **not** extend a common parent class (like `BaseItem`). They must be separate entities.

## 🧪 3. Testing Requirements (Crucial for Grade)
The assignment has very specific testing requirements.

### For Grade G (Unit Tests)
*Target: `src/test/java/com/nilsson/service/`*
- [ ] **Setup JUnit 5 & Mockito:** (Already in POM, needs implementation).
- [ ] **Create `RentalServiceTest.java`:**
    - [ ] Mock all repositories (`RentalRepository`, `VehicleRepository`, etc.).
    - [ ] **Test 1:** Rent a Vehicle successfully (Verify `save` is called).
    - [ ] **Test 2:** Try to rent an already rented Vehicle (Expect `ItemAlreadyRentedException`).
    - [ ] **Test 3:** Try to rent a non-existent Item (Expect `ResourceNotFoundException`).
    - [ ] **Test 4:** Return an item successfully (Verify cost calculation).
    - [ ] **Test 5:** Test logic for Tent rental.
    - [ ] **Test 6:** Test logic for Gear rental.

### For Grade VG (Integration Tests)
*Target: `src/test/java/com/nilsson/repo/`*
- [ ] **Create `RentalRepositoryImplTest.java`:**
    - [ ] Use H2 Database (in-memory) or a test MySQL config.
    - [ ] **Test 1:** `save()` a Rental and then `getRentalsByMemberId()` to verify it persists.
    - [ ] **Test 2:** Create a Rental, update it (return it), and verify the update in the DB.

## 🖥️ 4. JavaFX UI Implementation
Connecting the backend to your frontend.

### Rental View (`RentalView.java`)
- [ ] **Populate Active Rentals:**
    - [ ] Call `rentalRepository.getRentalsByMemberId(...)` or `getAllRentals()` depending on view mode.
- [ ] **Implement "Return" Action:**
    - [ ] Add a button "Återlämna" (Return).
    - [ ] On click: Call `rentalService.returnItem(selectedRental.getId())`.
    - [ ] Show an Alert with the calculated price: "Total kostnad: X kr".
    - [ ] Refresh the table.

### Booking/New Rental (`NewRentalDialog.java`)
- [ ] **Dynamic Item Loading:**
    - [ ] When user selects "Vehicle", populate the dropdown only with *Available* (`!isRented`) vehicles.
- [ ] **Submit Action:**
    - [ ] Call `rentalService.rentItem(...)`.
    - [ ] Handle Exceptions: If `ItemAlreadyRentedException` is thrown, show an Error Alert.

### Member View (`MemberView.java`)
- [ ] **CRUD Actions:**
    - [ ] Ensure "Add Member" saves to DB.
    - [ ] Ensure "Delete Member" removes from DB.
    - [ ] *Note:* Handle deletion carefully. If a member has active rentals, DB might throw a Foreign Key constraint error. Catch this and tell user: "Cannot delete member with active rentals."

## 📝 5. Documentation & Submission
- [ ] **Project Diary:** Write the required reflection/log.
- [ ] **Clean Code:** Remove unused imports and commented-out code.
- [ ] **JavaDoc:** Add basic comments to complex methods (like `rentItem` and `returnItem`).