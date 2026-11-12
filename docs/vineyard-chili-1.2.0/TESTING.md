# Test manuale — v1.2.0 prototipo uva e peperoncini

## Aggiornamento e avvio (PowerShell, dalla cartella repository)
```powershell
git status
git fetch origin
git switch feature/vineyard-chili-1.2.0
git pull --ff-only origin feature/vineyard-chili-1.2.0
java -version
.\gradlew.bat runClient --no-daemon --max-workers=1
```

Non utilizzare `clean` se i file di build sono bloccati o lo spazio su disco è limitato. Usa Minecraft **26.3**, Fabric e Java 25.

## Oggetti di test in Creative
```text
/give @p italiansdelight:grape_seeds 16
/give @p italiansdelight:chili_pepper_seeds 16
/give @p italiansdelight:grapes 16
/give @p italiansdelight:chili_pepper 16
/give @p italiansdelight:grape_juice_bottle 4
/give @p italiansdelight:grape_juice_bucket
/give @p italiansdelight:crushing_tub
/give @p italiansdelight:barrel
/give @p minecraft:bone_meal 64
```

## Prove in gioco
1. Crea un mondo nuovo. Semina entrambe le colture su terreno arato e verifica i modelli 0..7; non serve ancora costruire un traliccio.
2. Usa farina d'ossa per maturare le piante, poi mano **vuota** e click destro: l'uva e il peperoncino devono cadere e la pianta deve restare, tornando alla fase 3.
3. Ripeti maturazione e raccolta e prova a rompere una pianta matura: verificare semi e frutti senza duplicazioni.
4. Inserisci i grappoli nel Crushing Tub e salta dentro: ogni grappolo = **250 mB** di succo e semi; prova fino al limite di **8.000 mB**.
5. Preleva una bottiglia vuota (250 mB) e un secchio vuoto (1.000 mB); trasferisci il liquido al Barrel e al calderone. Verifica che non si mescoli con olio e latte.
6. Apri JEI e verifica la pigiatura con i semi come sottoprodotto e l'informazione sul succo.
7. Salva, riavvia il mondo e riprova colture e liquidi. Prova con e senza Serene Seasons.

## Non disponibili in questa prima iterazione
- Viti orizzontali su traliccio e grappoli appesi alle foglie come nella Rustic originale.
- Ghost pepper ottenibile tramite coltivazione, botti di fermentazione, qualità del vino, wine bottle e relativo avanzamento.
- Varietà di uva bianca/rossa, ricette complete, spawn naturale.
- La bottiglia e il secchio del succo usano **modelli temporanei**; servono texture dedicate prima della release.

**Non pubblicare il JAR come v1.2.0 stabile** prima dei test e delle fasi rimanenti.
