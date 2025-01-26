# Password manager

Cel projektu

Celem projektu jest stworzenie Password Managera, czyli aplikacji, która umożliwia użytkownikowi bezpieczne przechowywanie, zarządzanie i generowanie haseł. Program ma zapewniać wysoki poziom bezpieczeństwa danych dzięki zastosowaniu szyfrowania oraz prosty i intuicyjny interfejs użytkownika.

Funkcjonalności
1) Rejestracja i logowanie użytkownika
    Tworzenie nowego konta użytkownika.
    Logowanie do aplikacji przy użyciu hasła głównego (master password), które nie jest przechowywane w formie jawnej (użycie funkcji skrótu, np. SHA-256).
   
    Okno przy uruchomiemiu programu
   
   ![image](https://github.com/user-attachments/assets/d6f9499f-c082-4bb6-9893-8cc5dedf9aeb)
   
    Okno rejestracji konta
   
   ![image](https://github.com/user-attachments/assets/01a85ddb-c281-4b80-b033-0a265db8cda7)



2) Przechowywanie haseł
    Dodawanie nowych haseł do bazy danych (np. login, hasło, URL strony).
    Odczyt zapisanych haseł po uwierzytelnieniu użytkownika.
    Usuwanie lub edytowanie istniejących wpisów.
   
   Okno po zalogowaniu
   
   ![image](https://github.com/user-attachments/assets/14df6ece-4c61-4b2c-aa9b-7530cff1dbff)

   Okno dodawania danych do tablicy
   
   ![image](https://github.com/user-attachments/assets/d208bf43-daa8-48e4-9f39-867e08ec724b)
   
   Przegląd konkretnych danych
   
   ![image](https://github.com/user-attachments/assets/ff922489-8690-47f0-8f2b-7c559f709f94)



3) Generowanie haseł
    Moduł generujący silne hasła.
   
5)Moduł szyfrownia i kodowanie danych.

Do przechowywania wszystkich zarejestrowanych użytkowników isnieje baza danych SQL w której są kolumny: name, password, klucz do szyfrowania RSA(służy do szyfrowania i deszyfrowania danych użytkownika, każde konto ma swój klucz).
Dane każdego użytkownika chroniono w zakodowanej postaci(za pomocą RSA i kodowania Base64) jego dane w osobnej tablicy, tablica ma kolumny title(może być np URL storny), login(np do konta strony internetowej), password i data ostatniej modyfikacji danych.

Aplikacja ma intuicyjny interfejs(jak można zauważyć z powyrzszych zrzutów) stworzony za pomocą biblioteki javafx(styl okien i obiektów głównie jest zroboiny za pomocą CSS).

W pratyce wszystkie dane(Bazy danych) muszą być przechowywane na zdalnym serwerze i program musi wysyłać żądania na niego i przetwarzać odpowiedzi, żeby pobierać te dane dla uzupełnienia tablicy i podtwierdzenia weryfikacji użytkownika.

Jeszcze mieliśmy pomysł na broninie od BrutForce, ale nie zmogli tego realizować.

Ivan Tymoshchuk 321749

Hleb Yazvinski 316794

