# Testprotokoll – Modul 450 (LB2)

**Klasse:** INA23B  
**Namen:** Timeon Haas, Yanis Stebler  
**Datum:** 17.09.2025  
**Projekt:** M450_lib  
**SUT (Base URL):** http://localhost:8080  
**Profile:** `test` (PostgreSQL, `ddl-auto=create-drop`, Seed)

---

## 1. Testumgebung
- **OS:** Linux Arch x64
- **Java:** 21.0.8
- **Spring Boot:** 3.5.4
- **DB:** PostgreSQL 16 (Docker `postgres:16-alpine`)
- **Docker:** 28.4.0
- **Docker Compose:** v2.39.2
- **Start Server:** `./gradlew :server:bootRun --args="--spring.profiles.active=test"`
- **Start DB:** `docker compose up -d`
- **Seed:** Wird beim Start des Test-Profils automatisch ausgeführt.

---

## 2. Gewählte Schwerpunkte
1) **Datenbankverbindung**
2) **Transaktionsprüfung/CRUD**
3) **Datenintegrität**
4) **Fehlerbehandlung**

---

## 3. Endpunkte (implementiert)
- **Books**: `/api/book/getBooks`, `/api/book/getBookById/{bookId}`, `/api/book/rentBook`, `/api/book/returnBook`
- **Categories**: `/api/category/getCategories`, `/api/category/getBooksByCategory/{categoryId}`
- **Users**: `/api/user/getUsers`, `/api/user/getUserById/{userId}`, `/api/user/getUserRentedBooks/{userId}`

---

## 4. Testfälle inkl. Requests, Commands & Antworten

> Hinweis: Die unten dokumentierten Antworten sind die **tatsächlich beobachteten** Resultate der Ausführung.

### 4.1 Datenbankverbindung

**T-DB-01 – Verbindung OK**  
**Request:** `GET /api/book/getBooks`  
**Command:**
curl -i http://localhost:8080/api/book/getBooks
**Antwort (HTTP 200):**
{
"books": [
{"id":"b1","name":"1984","author":"Orwell","category":{"id":"cat-1","name":"Novel"},"available":true,"rentedBy":null,"rentedDate":null},
{"id":"b2","name":"Dune","author":"Herbert","category":{"id":"cat-1","name":"Novel"},"available":false,"rentedBy":null,"rentedDate":null},
{"id":"b3","name":"Watchmen","author":"Moore","category":{"id":"cat-2","name":"Comics"},"available":true,"rentedBy":null,"rentedDate":null}
]
}
**Erwartet:** 200  
**Mängelklasse:** 0

**T-DB-02 – Seed-Konsistenz (b2)**  
**Request:** `GET /api/book/getBookById/b2`  
**Command:**
curl -i http://localhost:8080/api/book/getBookById/b2
**Antwort (HTTP 200):**
{"id":"b2","name":"Dune","author":"Herbert","category":{"id":"cat-1","name":"Novel"},"available":false,"rentedBy":null,"rentedDate":null}
**Erwartet:** 200 (fachlich: wenn `available=false` dann `rentedBy != null`)  
**Mängelklasse:** 2 (fachliche Inkonsistenz sichtbar: vermietet, aber `rentedBy=null`)

---

### 4.2 Transaktionsprüfung / CRUD

**T-TRX-01 – Rent b1 durch u1 (Happy Path)**  
**Request:** `POST /api/book/rentBook`  
**Command:**
curl -i -X POST http://localhost:8080/api/book/rentBook \
-H "Content-Type: application/json" \
-d '{"bookId":"b1","userId":"u1"}'
**Antwort (HTTP 400):**
{"success":false,"message":"Could not commit JPA transaction","book":null,"rentedDate":null}
**Erwartet:** 200 und `available=false`, `rentedBy="u1"`  
**Mängelklasse:** 3 (Transaktion scheitert)

**State-Check b1 nach T-TRX-01**  
**Request:** `GET /api/book/getBookById/b1`  
**Command:**
curl -i http://localhost:8080/api/book/getBookById/b1
**Antwort (HTTP 200):**
{"id":"b1","name":"1984","author":"Orwell","category":{"id":"cat-1","name":"Novel"},"available":true,"rentedBy":null,"rentedDate":null}
**Bemerkung:** Zustand unverändert (Fehlschlag bestätigt).

**T-TRX-02 – Re-rent b2 durch u2 (Konflikt erwartet)**  
**Request:** `POST /api/book/rentBook`  
**Command:**
curl -i -X POST http://localhost:8080/api/book/rentBook \
-H "Content-Type: application/json" \
-d '{"bookId":"b2","userId":"u2"}'
**Antwort (HTTP 400):**
{"success":false,"message":"Book is not available for rent","book":null,"rentedDate":null}
**Erwartet:** 400/409, Zustand unverändert  
**Mängelklasse:** 0 (fachlich korrekt abgelehnt)

**State-Check b2 nach T-TRX-02**  
**Request:** `GET /api/book/getBookById/b2`  
**Command:**
curl -i http://localhost:8080/api/book/getBookById/b2
**Antwort (HTTP 200):**
{"id":"b2","name":"Dune","author":"Herbert","category":{"id":"cat-1","name":"Novel"},"available":false,"rentedBy":null,"rentedDate":null}
**Bemerkung:** Zustand unverändert (ok).

**T-TRX-03 – Return b2 durch u2 (falscher Nutzer)**  
**Request:** `POST /api/book/returnBook`  
**Command:**
curl -i -X POST http://localhost:8080/api/book/returnBook \
-H "Content-Type: application/json" \
-d '{"bookId":"b2","userId":"u2"}'
**Antwort (HTTP 400):**
{"success":false,"message":"Book is rented by a different user","book":null,"returnedDate":null}
**Erwartet:** 400/401/403  
**Mängelklasse:** 0 (fachlich korrekt abgelehnt)

**T-TRX-04 – Return b2 durch u1 (Happy Path)**  
**Request:** `POST /api/book/returnBook`  
**Command:**
curl -i -X POST http://localhost:8080/api/book/returnBook \
-H "Content-Type: application/json" \
-d '{"bookId":"b2","userId":"u1"}'
**Antwort (HTTP 400):**
{"success":false,"message":"Could not commit JPA transaction","book":null,"returnedDate":null}
**Erwartet:** 200 und danach `available=true`, `rentedBy=null`  
**Mängelklasse:** 3 (Transaktion scheitert)

**State-Check b2 nach T-TRX-04**  
**Request:** `GET /api/book/getBookById/b2`  
**Command:**
curl -i http://localhost:8080/api/book/getBookById/b2
**Antwort (HTTP 200):**
{"id":"b2","name":"Dune","author":"Herbert","category":{"id":"cat-1","name":"Novel"},"available":false,"rentedBy":null,"rentedDate":null}
**Bemerkung:** Keine Änderung (Fehlschlag bestätigt).

---

### 4.3 Fehlerbehandlung

**T-FE-01 – Not Found (unbekannte Book-ID)**  
**Request:** `GET /api/book/getBookById/does-not-exist`  
**Command:**
curl -i http://localhost:8080/api/book/getBookById/does-not-exist
**Antwort (HTTP 404):**
{"message":"Book not found with id: does-not-exist","error":"NOT_FOUND"}
**Erwartet:** 404 + Fehlerkörper  
**Mängelklasse:** 0

**T-FE-02 – Validation (fehlendes `userId` beim Rent)**  
**Request:** `POST /api/book/rentBook`  
**Command:**
curl -i -X POST http://localhost:8080/api/book/rentBook \
-H "Content-Type: application/json" \
-d '{"bookId":"b1"}'
**Antwort (HTTP 400):**
{"success":false,"message":"The given id must not be null","book":null,"rentedDate":null}
**Erwartet:** 400  
**Mängelklasse:** 0

---

### 4.4 Kategorien & User (Happy Paths)

**T-CAT-01 – Kategorien**  
**Request:** `GET /api/category/getCategories`  
**Command:**
curl -i http://localhost:8080/api/category/getCategories
**Antwort (HTTP 200):**
{"categories":[{"id":"cat-1","name":"Novel"},{"id":"cat-2","name":"Comics"}]}
**Mängelklasse:** 0

**T-CAT-02 – Bücher je Kategorie**  
**Request:** `GET /api/category/getBooksByCategory/cat-1`  
**Command:**
curl -i http://localhost:8080/api/category/getBooksByCategory/cat-1
**Antwort (HTTP 200):**
{"books":[
{"id":"b1","name":"1984","author":"Orwell","category":{"id":"cat-1","name":"Novel"},"available":true,"rentedBy":null,"rentedDate":null},
{"id":"b2","name":"Dune","author":"Herbert","category":{"id":"cat-1","name":"Novel"},"available":false,"rentedBy":null,"rentedDate":null}
]}
**Request:** `GET /api/category/getBooksByCategory/cat-2`  
**Command:**
curl -i http://localhost:8080/api/category/getBooksByCategory/cat-2
**Antwort (HTTP 200):**
{"books":[{"id":"b3","name":"Watchmen","author":"Moore","category":{"id":"cat-2","name":"Comics"},"available":true,"rentedBy":null,"rentedDate":null}]}
**Mängelklasse:** 0

**T-USER-01 – Users**  
**Request:** `GET /api/user/getUsers`  
**Command:**
curl -i http://localhost:8080/api/user/getUsers
**Antwort (HTTP 200):**
{"users":[
{"id":"u1","name":"User One","email":"u1@example.com","authenticated":true},
{"id":"u2","name":"User Two","email":"u2@example.com","authenticated":true}
]}

**T-USER-02 – User by ID**  
**Request:** `GET /api/user/getUserById/u1`  
**Command:**
curl -i http://localhost:8080/api/user/getUserById/u1
**Antwort (HTTP 200):**
{"id":"u1","name":"User One","email":"u1@example.com","authenticated":true}

**T-USER-03 – Rented Books by User**  
**Request:** `GET /api/user/getUserRentedBooks/u1`  
**Command:**
curl -i http://localhost:8080/api/user/getUserRentedBooks/u1
**Antwort (HTTP 200):**
{"books":[{"id":"b2","name":"Dune","author":"Herbert","category":{"id":"cat-1","name":"Novel"},"available":false,"rentedBy":null,"rentedDate":null}]}
**Mängelklasse (User-Teil):** 0

---

## 5. Zusammenfassung
| Schwerpunkt | Ergebnis | Bemerkung |
|---|---|---|
| Datenbankverbindung | **OK** | Verbindung stabil |
| Transaktionsprüfung/CRUD | **Nicht bestanden** | `rent`/`return` liefern 400 „Could not commit JPA transaction“ |
| Datenintegrität | **Teilweise nicht bestanden** | b2: `available=false` **aber** `rentedBy=null` |
| Fehlerbehandlung | **OK** | 404 und Validierung (400) verhalten sich erwartungsgemäß |

---

## 6. Next Steps
1) **Transaktionsfehler analysieren:** Ursache für „Could not commit JPA transaction“ beim Rent/Return identifizieren (z. B. DB-Constraint-Konflikt, Service-Logik, Null-Werte, Mapping/Conversion). Stacktrace im Server-Log prüfen.
2) **Integrität erzwingen:** Seed/Service so anpassen, dass bei `available=false` **immer** `rentedBy != null`. Optional: DB-`CHECK`-Constraint (XOR).
3) **Retest nach Fix:** T-TRX-01/04 sollten 200 liefern; T-TRX-02/03 weiterhin 400/409.
