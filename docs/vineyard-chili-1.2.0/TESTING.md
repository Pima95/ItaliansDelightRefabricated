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
1. Crea un mondo nuovo. Per l'uva ara il terreno e semina `grape_seeds`; disponi due pali a distanza di **3 blocchi**, alti 2, collegandoli con la corda originale Farmer's Delight (`farmersdelight:rope`) un blocco sopra la pianta. La fune deve essere orizzontale e collegata ai pali.
2. Coltiva la vite fino all'età 7: il tralcio deve comparire sopra lo stelo e allungarsi sulla corda fino a **un blocco per lato**, producendo grappoli soltanto nei segmenti con aria sotto. I grappoli si raccolgono con clic destro senza spezzare la vite.
3. Metti un `crop_stake` sopra un terreno arato e usa su di esso `chili_pepper_seeds`. Senza palo, i semi di peperoncino NON devono essere piantabili. Controlla crescita, raccolta ripetuta e possibilità del raro `ghost_pepper` (1 su 42 raccolte).
4. Sistema 1–2 corde di Farmer's Delight sopra il peperoncino maturo. La pianta deve colonizzarle verticalmente e produrre peperoncini su ogni segmento (altezza massima 3 blocchi).
5. Pianta sullo stesso tipo di palo anche i semi originali `farmersdelight:tomato_seeds`; i pomodori devono crescere con le **meccaniche reali di Farmer's Delight**, senza registrare un secondo item seme.
6. Spezza peperoncini, pomodori e tralci: devono rimanere i rispettivi pali e corde originali, senza duplicazioni. Verifica il comportamento sia a mano che con terreno arato distrutto.
7. Inserisci i grappoli nel Crushing Tub e salta dentro: ogni grappolo = **250 mB** di succo e semi; prova fino al limite di **8.000 mB**.
8. Preleva una bottiglia vuota (250 mB) e un secchio vuoto (1.000 mB); trasferisci il liquido al Barrel e al calderone. Verifica che non si mescoli con olio e latte.
9. Apri JEI e verifica la pigiatura con i semi come sottoprodotto e l'informazione sul succo.
10. Salva, riavvia il mondo e riprova colture e liquidi. Prova con e senza Serene Seasons.

## Non disponibili in questa prima iterazione
- La struttura base delle viti orizzontali è implementata con corda FDR e tralci; mancano miglioramenti grafici/stagionali e test manuali della configurazione completa.
- Il ghost pepper è ottenibile come prodotto raro; i suoi effetti speciali, la GUI del fermentatore e l'avanzamento sul vino restano da implementare.
- Varietà di uva bianca/rossa, ricette complete, spawn naturale.
- La bottiglia e il secchio del succo usano **modelli temporanei**; servono texture dedicate prima della release.

**Non pubblicare il JAR come v1.2.0 stabile** prima dei test e delle fasi rimanenti.

## Test della botte di fermentazione (prototipo)
```text
/give @p italiansdelight:brewing_barrel
/give @p italiansdelight:grape_juice_bucket 8
/give @p minecraft:glass_bottle 16
/give @p italiansdelight:wine_bottle
/give @p italiansdelight:ghost_pepper
```

1. Posiziona il blocco `brewing_barrel` e inserisci fino a otto secchi di succo d'uva, oppure bottiglie da 250 mB.
2. Clic a mano vuota per visualizzare quantità e avanzamento.
3. Attendi 12.000 tick lato server (10 minuti a 20 TPS) e verifica che il timer prosegua dopo un salvataggio/riavvio.
4. A fermentazione completata, clic destro con bottiglia di vetro vuota: preleva 250 mB di vino. Controlla la qualità nel tooltip.
5. Reinserisci una bottiglia di vino per inoculare la coltura (facoltativa e non consumata durante la fermentazione); recupera la coltura con Shift+clic usando una bottiglia vuota.
6. Il contenuto della botte è salvato anche nel BlockItem se la rompi e la riposizioni. Verifica senza duplicare bottiglie o fluidi.
7. Gli effetti di consumo delle bevande, la GUI di Rustic, vini rosso/bianco distinti e il trasferimento del vino con qualità nei normali barili restano da implementare.

Usa un mondo di prova, non un salvataggio importante. Il test automatico avvia e riapre un mondo, ma non simula il ciclo completo di fermentazione.

## Comandi aggiuntivi per questo aggiornamento

```text
/give @p italiansdelight:crop_stake 16
/give @p farmersdelight:rope 32
/give @p farmersdelight:tomato_seeds 16
/give @p italiansdelight:wine_bucket
```

**Ricetta unica del palo:** due tronchi sovrapposti nella griglia, tramite tag `#italiansdelight:stake_logs` che include i tag condivisi `#minecraft:logs` e `#c:logs` (quando presenti). Non esistono ricette separate per ogni tipo di legno.

**Test visuale JEI:** il liquido `grape_juice` deve apparire come *Succo d'uva/Grape Juice*, `wine` come *Vino/Wine* (non come chiave di traduzione), con la texture del vino nel viewer. Il `wine_bucket` usa il nuovo sprite con liquido viola, non quello vuoto. Verificare anche i tooltip delle singole bottiglie e dei secchi di vino dopo estrazione dalla botte.

**Nota:** il video YouTube allegato non è accessibile direttamente come flusso visuale attraverso il connettore. Le regole implementate sono state confrontate con i sorgenti originali di Rustic e con Farmer's Delight Refabricated 26.3 su GitHub.

## Correzioni posizionamento 12 novembre — prova giocatore

- **Farmland**: il palo di sostegno non ha più collisioni né una forma di blocco pieno; va posizionato su terreno arato senza convertirlo in terra normale. La forma di selezione resta sottile e cliccabile; mantiene la ricetta con due tronchi.
- **Corda orizzontale**: usa l'oggetto originale `farmersdelight:rope`, accovacciati con **Shift** e fai clic destro sulla **faccia laterale** di un blocco/palo. Una nuova corda orizzontale occupa il blocco adiacente lungo l'asse del clic. Per estendere, Shift+clic sulla **testa dell'ultima corda**. Nessun item duplicato; rompendo il segmento si recupera una corda FDR.
- **Corda verticale**: rimane invariata la posa standard di Farmer's Delight, senza Shift, e la salita di peperoncini e pomodori.
- **Uva**: i tralci crescono sui segmenti orizzontali; rompendo i tralci, si ripristina il segmento orizzontale corretto e non una corda verticale.
- **Succo d'uva**: `grape_juice_bottle` usa il modello standard della pozione/bottiglia di Minecraft con tintura viola, come il succo di mela.
- **Prova rapida**: in creativa usa `/give @p italiansdelight:crop_stake`, `/give @p farmersdelight:rope 16`, `/give @p italiansdelight:grape_seeds 16`. Ara un blocco e posiziona un palo; verifica che il terreno resti arato. Posiziona una fila orizzontale con Shift+clic a due blocchi di altezza, matura la vite e controlla grappoli/rottura.
- **Attenzione**: CI automatico conferma compilazione e avvio, ma la posa Shift+clic e la farmland vanno verificati nel mondo client.
