# Italian's Delight Refabricated 1.2.0 — porting Minecraft 26.1.2

**Branch esistente:** `feature/mc-26.1.2` · **versione:** `1.2.0-mc26.1.2` · **Java:** 25 · **Fabric**.

Questo port conserva le funzionalità della versione 1.2.0 per 26.2 (uva bianca e viola, peperoncini, bottiglie/succhi/vini, fermentazione, piatti/pizze, JEI e frutteti). Il legno di pioppo non viene portato perché non esiste in Minecraft 26.1.2. La storia del vecchio porting 1.1.0 resta nell'ancestry Git.

## Dipendenze e ambiente
- Minecraft 26.1.2, Java 25, Fabric Loader 0.20.1.
- Fabric API 0.155.3+26.1.2.
- Farmer's Delight Refabricated 26.1-3.6.30+refabricated.
- JEI Fabric CurseForge 9384703; Serene Seasons opzionale CurseForge 8351062 (+ GlitchCore, installato a parte).

Le versioni di queste dipendenze provengono dal precedente port 26.1.2 che aveva superato build e server smoke, ma le funzionalità aggiunte nella 1.2.0 necessitano della verifica CI e dei test client.

## Installazione e test
```powershell
git fetch origin
git switch feature/mc-26.1.2
git pull --ff-only origin feature/mc-26.1.2
.\gradlew.bat build --no-daemon
.\gradlew.bat runClient --no-daemon
```

La build crea `build/libs/italiansdelight-1.2.0-mc26.1.2.jar`. Verificare il risultato da GitHub Actions e provare JEI, texture, crescita, ricette, macchine, liquidi, salvataggio e caricamento. Usare un mondo di prova **nuovo** per Minecraft 26.1.2; non retrocedere salvataggi 26.2/26.3.

**Workflow:** `.github/workflows/mc-26.1.2.yml` (JAR + controlli) e `.github/workflows/mc-26.1.2-server.yml` (avvio server + caricamento del mondo). Nessuna release o tag viene creato automaticamente.
