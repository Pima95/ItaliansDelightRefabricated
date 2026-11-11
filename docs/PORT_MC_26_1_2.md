# Porting 1.1.0 a Minecraft Java 26.1.2

Branch: `feature/mc-26.1.2` (basato sull'ultimo `feature/mc-26.3`).

## Configurazione verificata
- Minecraft 26.1.2, Java 25, Fabric Loader 0.20.1
- Fabric API 0.155.3+26.1.2
- Farmer's Delight Refabricated 26.1-3.6.30+refabricated
- JEI Fabric 29.53.0.115 (CurseForge 9384703)
- Serene Seasons Fabric opzionale 26.1.2-26.1.2.0.4 (CurseForge 8351062);
  installare GlitchCore compatibile quando si usa Serene Seasons.

## Differenze dal ramo 26.3
Il legno vanilla Poplar compare solo in 26.3: rimosse le sole varianti
`poplar_crushing_tub`, `poplar_barrel` e `poplar_cheese_aging_rack`,
comprese registrazioni, tag, ricette, modelli, PNG, loot e traduzioni.
Le altre funzionalità rimangono nel sorgente.

## Comandi PowerShell
```powershell
git status
git fetch origin
git switch feature/mc-26.1.2
git pull --ff-only origin feature/mc-26.1.2
java -version
py -3 .github/scripts/check_mc2612_port.py
.\gradlew.bat build --no-daemon --max-workers=1
.\gradlew.bat runClient --no-daemon --max-workers=1
```

Non usare `clean` se Windows blocca `build`. Il JAR ottenuto sarà
`build/libs/italiansdelight-1.1.0-mc26.1.2.jar`.
Avviare client/server Minecraft 26.1.2 in una directory distinta; non
declassare i salvataggi 26.3. Pubblicare soltanto dopo build CI riuscita,
avvio client e server, prove di JEI, frutteti, pizza, fluidi e Serene Seasons.
