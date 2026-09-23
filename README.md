# Italian's Delight

Add-on per [Farmer's Delight Refabricated](https://github.com/MehVahdJukaar/FarmersDelightRefabricated)
(Fabric, Minecraft 26.2) che aggiunge cucina italiana: ingredienti, catene di
lavorazione e piatti, integrati nei sistemi già esistenti di Farmer's Delight
(Cutting Board, Cooking Pot, Stove) invece di reinventare tutto da zero.

## Requisiti per lo sviluppo

- JDK 25
- Fabric Loom >= 1.16 (via Gradle, già configurato in `build.gradle`)
- Farmer's Delight Refabricated per 26.2 (scaricato automaticamente da
  Cassian's Maven in fase di build — vedi `gradle.properties`)

## Stato del progetto

Scheletro iniziale: struttura cartelle, toolchain Gradle/Fabric, entrypoint e
un paio di item di esempio per fissare il pattern di registrazione. Nessun
contenuto definitivo ancora.

Per i dettagli su decisioni di design, struttura del progetto e roadmap, vedi
[`docs/REQUISITI.md`](docs/REQUISITI.md).

## Build

```
./gradlew build
```

(richiede `gradlew`/`gradlew.bat` — verranno generati al primo `gradle wrapper`
o li aggiungiamo nello step successivo insieme al resto del toolchain Gradle)
