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

## Verifiche automatiche della release

Il workflow `.github/workflows/port-mc2612.yml` esegue i controlli statici,
l'audit dei dati, `gradlew build` con Java 25, controlla il file
`italiansdelight-1.1.0-mc26.1.2.jar` e carica un artefatto di test.
Il secondo workflow `.github/workflows/port-mc2612-server.yml` avvia un
server Fabric 26.1.2, verifica il primo `Done` e riapre lo stesso mondo,
per individuare errori di caricamento delle risorse o del salvataggio.

### Principali API ripristinate per 26.1.2

- `BonemealableBlock` senza il parametro `BonemealSource`.
- `MapCodec` dei blocchi recuperati dalle implementazioni precedenti.
- `TreeConfiguration` e chiavi `configured_feature` per i quattro alberi.
- `StrippableBlockRegistry`, `CompostableRegistry` e `FuelValues`.
- `LevelRenderEvents.END_EXTRACTION` per la guida tagli pizza.
- Metodi `Player.drop`, `PoseStack.mulPose` e cooking codecs dell'API 26.1.
- Integrazione opzionale Serene Seasons con rilevamento dei biomi tropicali.
- 146 loot table/avanzamenti riportati al formato data pack 26.1.2.
- File di worldgen configurati e placed feature ripristinati dal 26.2.

**La build CI non è una certificazione dei comportamenti di gioco.**
Oltre al test server occorrono test manuali del client, delle ricette,
dell'inventario, del comportamento stagionale e delle interazioni con fluidi.

### Diagnostica Windows

Se `clean` fallisce a causa della directory `build` in uso, non forzare
la cancellazione: chiudere il client e l'IDE prima di riprovare la build.
L'uso di `--no-daemon --max-workers=1` limita l'utilizzo di risorse.

## Test sperimentale di compatibilità con Minecraft 26.1 e 26.1.1

Il file `fabric.mod.json` accetta temporaneamente `">=26.1 <=26.1.2"` per consentire
l'avvio **dello stesso JAR** con Minecraft 26.1, 26.1.1 e 26.1.2.
La compilazione GitHub Actions continua a usare **Minecraft 26.1.2**.
Questa modifica serve a verificare la compatibilità, **non la dimostra**.

Non serve alcun `fabric_loader_dependencies.json` o altro override esterno.

1. Esegui `git fetch origin`, `git switch feature/mc-26.1.2`, quindi
   `git pull --ff-only origin feature/mc-26.1.2` per ricevere la modifica.
2. Esegui `.\\gradlew.bat build --no-daemon --max-workers=1` senza `clean`.
3. Installa lo stesso `build/libs/italiansdelight-1.1.0-mc26.1.2.jar`
   in **tre istanze Minecraft distinte**: 26.1, 26.1.1 e 26.1.2.
4. Installa in ogni istanza Fabric Loader, Fabric API e Farmer's Delight
   Refabricated adatti a quella specifica versione di Minecraft.
   Aggiungi JEI se disponibile; inizialmente lascia fuori Serene Seasons.
5. Usa un nuovo mondo per ogni istanza. Verifica caricamento, ricette JEI,
   frutteti, pizza, fluidi, barili, calderoni e stagionatura; non aprire
   i salvataggi 26.1.2 con Minecraft 26.1 o 26.1.1.
6. Solo dopo i test confermati valuteremo se mantenere l'intervallo di
   compatibilità nel file di pubblicazione della mod.

**Nota:** il nome del JAR rimane `italiansdelight-1.1.0-mc26.1.2.jar`
perché identifica la versione dell'SDK usato nella compilazione, non le
versioni ulteriori con cui si sta verificando la compatibilità.
