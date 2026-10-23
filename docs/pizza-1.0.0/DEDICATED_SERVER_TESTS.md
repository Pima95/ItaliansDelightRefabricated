# Collaudo server dedicato — Italian's Delight Pizza 1.0.0

**Stato:** da eseguire e confermare dall'autore. Ultimo aggiornamento: 23 ottobre 2026.  
**Branch:** `feature/pizza-1.0.0`. **Non modificare** `main` o `mod_version=0.4.0` in questa fase.

## Scopo

Verificare gli aspetti che la compilazione Gradle e i controlli statici non simulano: caricamento dei mixin su un server Minecraft senza renderer client, persistenza su disco, operazioni simultanee di due giocatori, trasferimenti da hopper e rimborsi esattamente una volta.

## Preparazione

1. **Salvare un backup** del mondo. Usare la stessa build del JAR sul client e sul server, Minecraft **26.2**, Java **25**, Fabric Loader, Fabric API e Farmer's Delight Refabricated compatibili. JEI è richiesto solo sui client (non è necessario sul server).
2. Eseguire `git switch feature/pizza-1.0.0`, `git pull origin feature/pizza-1.0.0` e `.\gradlew.bat clean build`. Copiare il JAR prodotto in `build/libs` nella cartella `mods` del server dedicato, evitando eventuali JAR `-sources`.
3. Avviare il server dedicato Fabric seguendo le istruzioni e accettando l'EULA di Minecraft sul proprio server. **Non** usare come surrogato il server integrato di `runClient`. Conservare `logs/latest.log` e il commit testato. Configurare autorizzazioni e lista utenti in modo sicuro.
4. Collegare **due client** con le stesse mod di gameplay e JEI opzionale.

## Casi di test

| ID | Procedura | Risultato atteso |
|---|---|---|
| DS-01 | Avviare server senza schermate/renderer client | Nessun crash da classi `net.minecraft.client`, mixin o JEI |
| DS-02 | Giocatore A deposita una Pizza mista cotta sulla Cutting Board e A/B cliccano col coltello quasi contemporaneamente | **1 pizza consumata**, esattamente **4 spicchi totali**, ciascuno con i propri condimenti |
| DS-03 | A/B rimuovono simultaneamente un topping dalla stessa base cruda | Il singolo ingrediente viene rimborsato **al massimo una volta** |
| DS-04 | A/B servono lo stesso quarto di una pizza cotta | Un solo spicchio per quarto, nessun secondo prelievo |
| DS-05 | Due giocatori trasferiscono una pizza con le pale; ripetere con Pizza Oven occupato | **1 sola pizza** resta esistente, nessuna perdita/duplicazione; rifiuto non distruttivo |
| DS-06 | Cuocere con salsa, ricotta o panna in Pizza Oven e forni vanilla; hopper di uscita pieno, vuoto, con ciotole personalizzate | Ciotole rimborsate una sola volta, nessuna fusione di stack con componenti differenti |
| DS-07 | Salvare/riavviare con Pizza mista in pala, Pizza Oven a metà cottura e formaggio in stagionatura | Condimenti e stati coerenti; il progresso nel forno resta legato al forno |
| DS-08 | Rompere il supporto di uncino e formaggio durante sessione multiplayer | Un solo uncino/prodotto, senza drop mancanti o doppioni |
| DS-09 | Dopo `/reload`, preparare caglio/Bocconcino/Provolone con secchio o bottiglia d'acqua | Ricetta unificata valida, resti corretti, nessuna pozione non d'acqua ammessa |
| DS-10 | Chiudere e riaprire il client con JEI collegato al server; cercare ricette e guide di pizza/ricotta | Nessun crash o lista di ricette stantia |

## Registrazione

Per ogni test annotare **PASS/FAIL**, data, SHA del commit, configurazione, versione delle dipendenze, eventuale numero di oggetti prima/dopo e `latest.log` in caso di errore. Non spuntare la checklist di rilascio quando si è verificato soltanto un singolo caso della tabella. Un errore di duplicazione, perdita di item o crash server **blocca il rilascio**.

I controlli automatici `.github/scripts/check_pizza_server_side_safety.py` verificano la separazione superficiale degli entrypoint e alcuni ordini di commit; **non** avviano un server, non misurano TPS e non sostituiscono questi test.
