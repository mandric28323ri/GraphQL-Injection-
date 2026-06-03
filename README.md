# GraphQL Injection - Projekat iz bezbednost web servisa

## O projektu

Ovaj projekat demonstrira ranjivosti GraphQL API-ja kroz namerno ranjivo web
aplikaciju. Projekat se sastoji od Java Spring Boot backenda i HTML/JS frontenda. 
Implementirane su ranjivosti koje se mogu eksploatisati kroz browser i Burp Suite alat.

Projekat ima dve grane:
- **main** - ranjiva verzija aplikacije
- **GraphQL-fix** - zaštićena verzija sa implementiranim mehanizmima odbrane

---

## Tehnologije

- **Backend:** Java 17, Spring Boot 3.2, Spring for GraphQL
- **Baza podataka:** H2 in-memory
- **Frontend:** HTML, CSS, JavaScript
- **Alati za demonstraciju:** Burp Suite

---

## Pokretanje aplikacije

### Preduslovi
- Java 17+
- Maven
- Node.js za npx live-server

### Backend
```bash
.\mvnw spring-boot:run
```

Backend se pokreće na `http://localhost:8080`


H2 konzola dostupna na: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:demodb`
- Username: `sa`
- Password: (prazno)

### Frontend
Otvoriti `frontend/login.html` kroz Live Server (VS Code ekstenzija) ili:
```bash
cd frontend
npx live-server --port=5500
```

### Dostupni nalozi
| Username | Password | Role  |
|----------|----------|-------|
| admin | admin123 | ADMIN |
| marko | marko123 | USER  |
| nikola | nikola123 | USER  |
| sminka | sminka123 | USER  |

---

## Ranjivosti

### 1. JPQL Injection - searchUsers

**Opis:** Parametar `searchTerm` se direktno ubacuje u JPQL upit bez ikakve sanitizacije ili validacije. Napadač može da izmeni logiku upita i dobije podatke koji mu nisu namenjeni.

**Ranjiv kod:**
```java
String jpql = "SELECT u FROM User u WHERE u.username LIKE '%" + searchTerm + "%'";
return entityManager.createQuery(jpql).getResultList();
```

**Eksploatacija kroz Burp Suite:**
```graphql
query {
    searchUsers(searchTerm: "' OR '1'='1") {
        id
        username
        email
        password
        role
    }
}
```

**Rezultat:** Vraća sve korisnike sa lozinkama bez obzira na search term.

**Zaštita:** Korišćenje Spring Data parametrizovanih upita:
```java
return userRepository.findByUsernameContainingIgnoreCase(searchTerm);
```

---

### 2. JPQL Injection - postsByCategory

**Opis:** Parametar `category` se direktno ubacuje u JPQL upit. Napadač može da dobije postove iz skrivenih kategorija.

**Ranjiv kod:**
```java
String jpql = "SELECT p FROM Post p WHERE p.category = '" + category + "'";
return entityManager.createQuery(jpql).getResultList();
```

**Eksploatacija kroz Burp Suite:**
```graphql
query {
    postsByCategory(category: "tech' OR '1'='1") {
        id
        title
        content
        category
    }
}
```

**Rezultat:** Vraća sve postove uključujući skrivene (kategorija `hidden`) sa poverljivim podacima.

**Zaštita:** Korišćenje Spring Data metode:
```java
return postRepository.findByCategory(category);
```

---

### 3. Izloženi podaci - plaintext lozinke

**Opis:** API vraća lozinke svih korisnika u plaintext formatu bez ikakve autentifikacije. Lozinke se čuvaju u bazi kao plaintext.

**Eksploatacija kroz Burp Suite:**
```graphql
query {
    users {
        id
        username
        email
        password
        role
    }
}
```

**Rezultat:** Vraća sve korisnike sa plaintext lozinkama bez tokena.

**Zaštita:**
- Hashovanje lozinki BCrypt algoritmom
- Uklanjanje `password` polja iz GraphQL sheme
- Dodavanje JWT autentifikacije na `users` query

---

### 4. Introspection

**Opis:** GraphQL introspection je uključen i dostupan svima. Napadač može da otkrije kompletnu strukturu API-ja - sve tipove, polja, query-je i mutacije.

**Eksploatacija kroz Burp Suite:**
```graphql
{
    __schema {
        types {
            name
            fields {
                name
            }
        }
    }
}
```

**Rezultat:** Otkriva sve tipove uključujući `User` sa `password` poljem, `Post` sa `postPassword` poljem i sve dostupne mutacije.

**Zaštita:** Isključiti introspection u `application.properties`:
```properties
spring.graphql.schema.introspection.enabled=false
```

---

### 5. Broken Access Control - userById

**Opis:** Query `userById` vraća korisničke podatke uključujući lozinku bez ikakve autentifikacije. Napadač može da menja ID parametar i dobije podatke bilo kog korisnika.

**Eksploatacija kroz Burp Suite:**
```graphql
query {
    userById(id: "1") {
        id
        username
        email
        password
        role
    }
}
```

**Rezultat:** Vraća admin kredencijale bez tokena.

**Zaštita:** Dodavanje JWT validacije:
```java
private void checkAuth() {
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        throw new RuntimeException("Unauthorized");
    }
    String token = authHeader.substring(7);
    if (!jwtService.validateToken(token)) {
        throw new RuntimeException("Invalid token");
    }
}
```

---

### 6. Hidden Blog Post - postPassword

**Opis:** Post sa kategorijom `hidden` nije vidljiv u normalnom listanju postova, ali sadrži `postPassword` polje. Napadač koji otkrije ID skrivenog posta kroz introspection može direktno upitati to polje.

**Korak 1 - Otkrij shemu kroz introspection:**
Napadač vidi da `Post` tip ima `postPassword` polje.

**Korak 2 - Eksploatacija:**
```graphql
query {
    postById(id: "4") {
        id
        title
        content
        postPassword
    }
}
```

**Rezultat:** Vraća sadržaj skrivenog posta sa lozinkom.

**Zaštita:** Ukloniti `postPassword` polje iz GraphQL sheme ili dodati autentifikaciju.

---

## Mehanizmi zaštite (GraphQL-fix grana)

| Ranjivost | Zaštita |
|-----------|---------|
| JPQL Injection | Parametrizovani Spring Data upiti |
| Plaintext lozinke | BCrypt hashovanje |
| Izloženi podaci | Password polje uklonjeno iz sheme |
| Introspection | Isključen u application.properties |
| Broken Access Control | JWT token validacija |
| Hidden post | postPassword uklonjeno iz sheme |

---

## Projekat radili

- Marko Stranjančević RI 60/2023
- Nikola Sikirić RI 180/2023
- Marko Andrić RI 283/2023