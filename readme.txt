ToySocialNetwork – Laboratorul 3 MAP

1. Descrierea aplicației

ToySocialNetwork este o aplicație Java care permite gestionarea utilizatorilor și a relațiilor de prietenie dintre aceștia.

Aplicația oferă operații CRUD pentru utilizatori și prietenii și permite analiza rețelei sociale prin intermediul algoritmilor pe grafuri, precum determinarea numărului de comunități și identificarea celei mai sociabile comunități.

Proiectul este organizat pe mai multe niveluri: domain, validation, repository, service și ui, fiecare având responsabilități distincte.

2. RepositoryFactory și trecerea la baza de date

Clasa `RepositoryFactory` implementează șablonul Simple Factory și centralizează crearea repository-urilor.

Tipul de repository este stabilit prin proprietatea `repository.type` din fișierul `config.properties`.

Sunt disponibile trei opțiuni:

- `MEMORY` – datele sunt păstrate în memorie, folosind `InMemoryRepository`.
- `FILE` – datele sunt păstrate în memorie și persistate în fișiere text, folosind `UserFileRepository` și `FriendshipFileRepository`.
- `DATABASE` – este rezervată pentru implementarea persistenței într-o bază de date.

Metodele din `UserDatabaseRepository` și `FriendshipDatabaseRepository` nu sunt încă implementate și aruncă `UnsupportedOperationException`.

Pentru trecerea la baza de date trebuie implementate operațiile repository-urilor folosind JDBC și interogări SQL, configurată conexiunea prin proprietățile `db.url`, `db.user` și `db.password` și selectată opțiunea `DATABASE`.

Serviciile nu trebuie rescrise, deoarece utilizează interfața generică `Repository`, independent de mecanismul de stocare.

3. Tratarea fișierelor lipsă și a liniilor corupte

Persistența în fișiere este implementată prin `FileRepository` și clasele sale derivate.

Dacă fișierul de date nu există la inițializare, repository-ul pornește cu o colecție goală. Fișierul este creat la prima operație de salvare care necesită persistență.

La citire, fiecare linie este transformată într-o entitate prin metoda `extractEntity()` și este validată înainte de introducerea în repository.

Dacă o linie conține date invalide, un format incorect sau un identificator duplicat, încărcarea este întreruptă și este raportată o excepție care indică problema și linia corespunzătoare.

Am ales această abordare de tip *fail-fast* pentru a evita pornirea aplicației cu date incomplete sau inconsistente.

La operațiile de adăugare, ștergere și actualizare, modificările efectuate cu succes sunt salvate în fișier prin metoda `writeToFile()`.

4. Principii SOLID

### SRP – Single Responsibility Principle

Fiecare clasă are o responsabilitate principală.

Clasele din `domain` reprezintă entitățile, validatorii verifică regulile de validare, repository-urile gestionează stocarea datelor, serviciile implementează logica aplicației, iar `ConsoleUI` gestionează interacțiunea cu utilizatorul.

### OCP – Open/Closed Principle

Aplicația poate fi extinsă cu noi mecanisme de persistență fără modificarea logicii serviciilor.

De exemplu, se poate implementa un repository pentru o bază de date care respectă interfața `Repository`.

Pentru selectarea unui nou tip de repository este necesară și extinderea configurației și a clasei `RepositoryFactory`.

### DIP – Dependency Inversion Principle

Clasele din nivelul service depind de abstracția `Repository`, nu de implementările concrete `InMemoryRepository`, `FileRepository` sau `DatabaseRepository`.

Astfel, mecanismul de persistență poate fi schimbat fără modificarea operațiilor de business.

### LSP – Liskov Substitution Principle

Implementările repository-urilor trebuie să poată înlocui interfața `Repository`, respectând contractul operațiilor definite de aceasta.

`InMemoryRepository` și `FileRepository` oferă implementări ale aceluiași contract.

Repository-urile pentru baze de date sunt momentan doar schelete și nu respectă încă funcționalitatea completă a contractului, deoarece operațiile lor aruncă `UnsupportedOperationException`.

5. Șabloane de proiectare

### Factory – RepositoryFactory

Clasa `RepositoryFactory` creează repository-urile concrete în funcție de tipul de stocare configurat.

Acest șablon separă logica de creare a obiectelor de logica aplicației.

### Template Method – FileRepository

Clasa abstractă `FileRepository` definește mecanismul general de citire și scriere a entităților.

Metodele `extractEntity()` și `createEntityAsString()` sunt implementate de `UserFileRepository` și `FriendshipFileRepository`, în funcție de formatul fiecărei entități.

Astfel, algoritmul general de persistență este reutilizat, iar detaliile de conversie sunt particularizate în clasele derivate.

### Strategy – Validator

Interfața `Validator<T>` definește o strategie generică de validare.

Clasele `UserValidator` și `FriendshipValidator` implementează reguli diferite, iar repository-urile primesc validatorul corespunzător prin constructor.

Astfel, logica de validare este separată de logica de stocare.

### Decorator – Persistența în fișiere

`FileRepository` extinde funcționalitatea oferită de `InMemoryRepository` prin adăugarea persistenței în fișiere.

Metodele `save()`, `delete()` și `update()` apelează implementarea din clasa părinte, apoi salvează modificările în fișier.

Această abordare este asemănătoare cu Decorator prin adăugarea unei responsabilități suplimentare, dar nu reprezintă o implementare clasică a șablonului Decorator, deoarece folosește moștenirea, nu compoziția.

6. Concluzie

Arhitectura aplicației urmărește separarea responsabilităților, reutilizarea codului și reducerea dependențelor dintre componente.

Folosirea interfețelor generice, a repository-urilor interschimbabile și a șabloanelor de proiectare facilitează întreținerea și extinderea aplicației, inclusiv implementarea ulterioară a persistenței într-o bază de date.