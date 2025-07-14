# Ball Maze

Aplikacja mobilna na system Android wykorzystująca akcelerometr oraz animacje przejść między ekranami.

## Technologie użyte w projekcie

- **Język programowania:** Java
- **System budowania:** Gradle
- **Framework:** Android SDK
- **Animacje:** Pliki XML (`fade_in.xml`, `fade_out.xml`, `slide_up.xml`)
- **Obsługa czujników:** Akcelerometr (`android.hardware.sensor.accelerometer`)

## Struktura aplikacji

- **SplashActivity** – ekran startowy aplikacji (główna aktywność)
- **MainActivity** – główny ekran aplikacji
- **GameActivity** – ekran gry

## Uprawnienia i funkcje

- Wymagany akcelerometr w urządzeniu

## Wygląd i animacje

- Animacje przejść między ekranami: zanikanie (`fade_in`, `fade_out`), przesuwanie w górę (`slide_up`)
- Motyw: `Theme.AppCompat.NoActionBar`

## Uruchomienie projektu

1. Sklonuj repozytorium
2. Otwórz projekt w Android Studio
3. Zbuduj i uruchom na urządzeniu z Androidem (wymagany akcelerometr)

## Autor

steedware
