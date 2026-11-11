# Checklist pratica di collaudo — Orchard 1.1.0 ottimizzata

**Branch:** `feature/orchards-1.1.0` — **non è una release pubblica**.  
**Piattaforma:** Minecraft **26.2**, Java **25**, Fabric Loader **0.20.1+**,
Fabric API **0.161.0+26.2**, Farmer's Delight Refabricated
**26.2-3.6.29+refabricated**, JEI per le verifiche dell'interfaccia.  
**Stato del JAR:** la build collaudata inizialmente manteneva il nome
`italiansdelight-1.0.0.jar`. La nuova candidate di test usa
`1.1.0-rc.1`, distinta dalle release pubbliche 1.0.0 e 1.1.0.

## Azioni che puoi eseguire tu (in ordine)

1. **Scaricare il build di prova:** aprire [GitHub Actions — Development CI](https://github.com/Pima95/ItaliansDelightRefabricated/actions/workflows/pizza-build.yml), selezionare l'ultima esecuzione verde sul branch `feature/orchards-1.1.0`, e scaricare l'archivio `orchard-1.1.0-qa-<commit SHA>` dalla sezione Artifacts. Annotare lo SHA. **Non** prendere un JAR da `main`.
2. **Preparare un ambiente separato:** creare un profilo/istanza Minecraft Fabric 26.2 con Java 25 e le dipendenze indicate sopra. Nella cartella `mods`, rimuovere la vecchia copia di Italian's Delight prima di inserire **un solo** JAR di prova, altrimenti si rischia un conflitto per mod ID duplicato.
3. **Salvare una copia del mondo:** prima di aprire il tuo mondo della 1.0.0, effettuare un backup completo di `saves/<nome_mondo>`. Eseguire i test **su una copia** del mondo, non sull'originale.
4. **Cheese Vat — ricette e contenitori:** provare una lavorazione normale, una con siero e una senza siero disponibile/abilitato; verificare in particolare la scelta fra Ricotta e Crema quando gli ingredienti si sovrappongono. Controllare output, ciotole/bottiglie e che la cottura in corso non venga erroneamente completata o azzerata dopo una normale uscita/riapertura. Provare una volta `/reload` durante una lavorazione e osservare che la ricetta corretta venga riletta.
5. **Calderoni, Crushing Tub e fluidi:** verificare almeno un calderone pieno all'aperto e uno coperto (il secondo non deve produrre sale); osservare la pausa del processo con pioggia/notte. Mettere olive/mela nella Crushing Tub, saltare una volta e controllare che venga processato **un solo ingrediente per atterraggio**, senza processi camminando dentro. Riempire/svuotare Barrel e calderoni con bottiglia e secchio e controllare livello, restituzione contenitori e aspetto dei fluidi animati.
6. **Pizze crude/cotte e spicchi:** preparare una pizza con condimenti differenti per quarto, cuocerla, staccare gli spicchi e riposizionarli anche su una pizza diversa. Controllare che texture, ingredienti, nomi, sazietà e quantità rimangano invariati; una pizza già servita non deve mai tornare recuperabile come pizza intera. Salvare e ricaricare il mondo con una pizza parziale. Dopo `F3+T` (ricarica texture) verificare che la grafica rimanga corretta.
7. **Orchard e inventario:** verificare un ulivo, un mandorlo e un ciliegio da frutto, raccolta, bone meal sulle foglie naturali, registrazione in Creative e ricerca JEI; controllare che il 65% selezionato non sia stato alterato (non serve misurarlo con un test breve). Controllare torta di mandorle, crostata di ciliegie, marmellata e ricette già approvate senza cambiare la produzione.
8. **Compatibilità salvataggi:** aprire una **copia** di un mondo precedente con Cheese Vat, rack, Hook, Crushing Tub, pizze e alberi già esistenti. Controllare che inventari, liquidi, ricette in corso e blocchi non si azzerino; chiudere e rientrare almeno una volta.
9. **Riferire soltanto gli esiti:** comunicare `OK` o `KO` per Cheese Vat, sale/Crushing Tub/fluidi, pizze, Orchard/JEI e caricamento vecchio mondo. In caso di `KO`, allegare la parte pertinente di `logs/latest.log` e indicare lo SHA del JAR; una schermata è utile per i problemi visivi. Evitare test ripetuti non necessari.

## Controlli che non devi svolgere personalmente

- **Avvio server dedicato:** il workflow [Dedicated server boot smoke](https://github.com/Pima95/ItaliansDelightRefabricated/actions/workflows/orchard-server-smoke.yml) lo avvia in un mondo di prova temporaneo. Un risultato verde prova soltanto che un server headless arriva allo stato pronto, **non** la sincronizzazione con due client.
- **Serene Seasons + GlitchCore:** non è coperto dai normali test di compilazione né dalla prova server senza le mod aggiuntive. **Non segnare superato** finché non si esegue una prova compatibile con entrambe le mod installate.
- **Multiplayer reale e collisioni simultanee:** non è coperto dallo smoke headless. Se indisponibile, annotare come rischio residuo e decidere esplicitamente se la release possa procedere senza questa garanzia.
- **Benchmark:** non sono disponibili confronti FPS/TPS replicabili. La CI verde conferma l'assenza di errori rilevati dai test disponibili, non un incremento di prestazioni misurato.

## Passaggio alla release pubblica (solo dopo approvazione)

Prima della pubblicazione verificare `THIRD_PARTY_NOTICES.md` e le attribuzioni
degli asset Rustic e di eventuali contributori; decidere come gestire i test
rimasti aperti. La release candidate ora usa `mod_version=1.1.0-rc.1`.
Solo successivamente autorizzare il cambio a `mod_version=1.1.0`,
aggiornare le note, creare tag e rilasciare il JAR.
Non eseguire automaticamente merge su `main` o pubblicazione CurseForge.
