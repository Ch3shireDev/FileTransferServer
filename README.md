# FileTransferServer

Założenia projektu — aplikacja do przesyłania dowolnie dużych plików pomiędzy komputerami. Serwer działa i czeka na zgłoszenia. Zgłoszenia mogą być dwóch rodzajów:

1. Nadawca chce przesłać plik — dostaje zwrotny ticket, a następnie czeka na wysłanie pliku.
2. Odbiorca chce odebrać plik — przedstawia numer nadania i odbiera plik.

Po zakończeniu transmisji porównywane są skróty celem sprawdzenia, czy plik został przesłany prawidłowo.

## Podział projektu

Projekt zamierzam podzielić na 5 części:

1. `core` - główna biblioteka programistyczna, w której są przechowywane wszystkie wymagane struktury
2. `client-cli` - konsolowa aplikacja kliencka służąca za nadawcę i odbiorcę jednocześnie.
3. `server-cli` - konsolowa aplikacja serwerowa będąca pośrednikiem podczas transferu danych, przyznawaczem ticketów itd.

## Co jest do zrobienia

1. [x] Stworzyć minimalną wersję serwera, który odbiera dowolne informacje od nadawcy i zwraca ticket — numer, z którego użyciem należy zgłosić się po odbiór przesłanej informacji.
2. [x] Dodać funkcję odbierania przechowywanej informacji po wysłaniu ticketa.
3. 
## Bieżąca forma protokołu.

1. Alice chce wysłać plik do Boba za pośrednictwem Charliego.
2. Alice zgłasza chęć wysłania pliku Charliemu — podaje mu nazwę pliku oraz liczbę bajtów, z których składa się plik.
3. Charlie generuje ticket dla Alicji, w którym znajdują się szczegóły odbioru dla Boba — numer oraz hasło.
4. Alicja wysyła dane pliku do Charliego.
5. Alicja przesyła numer i hasło Bobowi w dowolny sposób.
6. Bob wysyła żądanie odebrania pliku — przesyła numer pliku w postaci adresu (np. `/tickets/123`). W odpowiedzi serwer zaczyna przesyłać mu plik.

### Doświadczenie po stronie Alicji

1. Alicja łączy się z Charliem, aby zgłosić wysyłanie pliku, wysyła dane pliku poprzez zapytanie HTTP `POST /`:

```json
{
  "filename": "a.png",
  "filesize": 213123
}
```

W zamian uzyskuje ticket z informacją o parametrach transferu pliku (`201 Created`):

```json
{
  "id": 123,
  "filename": "a.png",
  "filesize": 213123,
  "url": "/123"
}
```

2. Alicja łączy się z Charliem, by wysłać plik poprzez zapytanie HTTP `POST /123` (url pliku z ticketa). W ciele zapytania HTTP umieszcza binaria pliku. Połączenie będzie trwało tak długo, aż nie zostanie przesłany cały plik. Na koniec Alicja dostanie odpowiedź z serwera. Jeśli wszystko poszło ok, będzie to `200 OK`. Jeśli coś poszło nie tak, będzie to `400 Bad Request`.

### Doświadczenie po stronie Boba

1. Bob łączy się z Charliem, by odebrać od niego plik wskazany przez Alicję. Wysyła do Charliego zapytanie HTTP `GET /123`. Jeśli faktycznie istnieje oczekujący na niego plik od Alicji, otrzymuje wiadomość zwrotną `200 OK` wraz z nagłówkiem informującym o nazwie pliku oraz binariami tego pliku. Jeśli coś pójdzie nie tak, Bob otrzymuje wiadomość `400 Bad Request`.

### Działanie

```
Hostowanie serwera:

java -jar server-cli.jar --port 80

Wysyłanie pliku:

java -jar client-cli.jar --port 80 --send a.png

W zamian uzyskiwany jest URL pliku, w postaci /tickets/[numery]

Odbieranie pliku:

java -jar client-cli.jar --port 80 --receive /tickets/123
```