# Chiusura delle ottimizzazioni per la candidata Pizza 1.0.0

**Data:** 23 ottobre 2026  
**Branch:** `feature/pizza-1.0.0`  
**Ambito:** decisioni di rilascio sulle proposte elencate in [OPTIMIZATION_REVIEW_2026-10-19.md](OPTIMIZATION_REVIEW_2026-10-19.md).

## Criterio di chiusura

L'obiettivo di questa fase è **chiudere la revisione delle ottimizzazioni prima della release**, non dichiarare implementate indistintamente tutte le proposte. Una modifica che introduce cache di dati di mondo, ricette o render senza un piano di invalidazione verificato può essere più rischiosa del costo che evita. In assenza di benchmark FPS/MSPT/allocazioni e di collaudo multiplayer, la decisione conservativa è documentare il rinvio, **non rimuovere i controlli di correttezza**.

## Esito delle quindici proposte

| ID | Decisione per 1.0.0 | Motivazione o riferimento |
|---|---|---|
| OPT-001 | **Rinviata, da profilare** | Overlay: 2.601 lookup per estrazione nel caso peggiore teorico; ridurre la finestra cambierebbe la visibilità delle guide. |
| OPT-002 | **Implementata in precedenza** | Anteprima di cottura separata dalla produzione/rimborso delle ciotole. |
| OPT-003 | **Rinviata, da profilare** | Cache Cheese Vat richiede invalidazione per ingredienti, recipienti, siero e datapack. |
| OPT-004 | **Rinviata, da profilare** | Render topping: evitare cache di stati mutabili senza verificare il ciclo di vita del renderer. |
| OPT-005 | **Rinviata, da profilare** | Indicizzazione sale per chunk caricati richiede test di scaricamento/riavvio e dei tick utili. |
| OPT-006 | **Rinviata, da profilare** | Cache durate delle stagionature richiede invalidazione dopo `/reload` e cambio ricetta. |
| OPT-007 | **Implementata in precedenza** | Eliminato l'aggiornamento blocco duplicato dei topping. |
| OPT-008 | **Rinviata, da profilare** | L'hook globale `Level.setBlock` tutela l'attivazione/azzeramento del sale; manca alternativa equivalente testata. |
| OPT-009 | **Rinviata, da profilare** | Il matching shapeless ha massimo tre slot; l'eliminazione di validazioni atomiche non è giustificata. |
| OPT-010 | **Implementata in precedenza** | Rimborso contenitori condiviso fra i forni. |
| OPT-011 | **Rifinitura implementata il 23/10** | `PizzaDisplayNames`: riutilizzo dello snapshot del primo quarto e della lista ordinata negli stessi metodi; identiche regole di naming. |
| OPT-012 | **Implementata in precedenza** | Evitati array temporanei nel bounding della pala. |
| OPT-013 | **Non necessaria per 1.0.0** | Refactoring presentazionale JEI senza guadagno gameplay dimostrato; la GUI approvata rimane invariata. |
| OPT-014 | **Implementata in precedenza** | Generazione e controllo delle varianti di ricetta degli scaffali. |
| OPT-015 | **Nessuna modifica opportuna** | Mantenute validazioni e copie difensive per tutelare output e componenti. |

Queste decisioni chiudono **la selezione delle ottimizzazioni pre-release**. Non costituiscono un benchmark né una dichiarazione di prestazioni garantite su server popolati.

## Rifinitura OPT-011

In `PizzaDisplayNames`, `wholePresetId` riutilizza il set immutabile del quarto 0 mentre verifica i restanti quarti; `wholeName` legge una sola volta lo stesso quarto; `shortIngredients` utilizza la lista già ordinata per comporre fino a tre ingredienti, preservando la stringa «nessuno» per una lista vuota. Nessuna cache globale, nessun nuovo stato persistente e nessuna modifica a identificativi, texture, traduzioni o componenti dell'item.

**Verifica disponibile:** revisione statica del diff; il workflow del branch compila Java 25 ed esegue i controlli di consistenza. **Non eseguita qui:** una misura FPS/MSPT o il test runtime comparativo degli output. Come regressione funzionale, controllare nomi per Bianca, Margherita, Pizza mista e gusti liberi con 1–6 ingredienti, in IT/EN.

## Misure utili dopo la release candidata

- **Client:** 0/20/100 impasti visibili e 0/25/100 pizze con topping, FPS, tempo del frame e allocazioni prima/dopo.
- **Cheese Vat:** 0/16/64 vasche con input validi e invalidi; conteggio lookup ricette per tick, prova `/reload`, MSPT.
- **Sale:** 0/100/1.000 calderoni con chunk caricati e scaricati; verificare esattamente 12.000 tick utili e persistenza.
- **Stagionatura:** 0/100/500 rack/uncini attivi, prima e dopo datapack reload.
- **Multiplayer:** verifica dei pacchetti di aggiornamento e delle operazioni concorrenti prima di intervenire nuovamente sulle notifiche.

Eseguire le misure **prima** di ottimizzare le parti rinviate e conservare i risultati nella cartella `docs`.

## Confini del rilascio

I test Cheese Vat e l'audit conclusivo sono stati dichiarati completati dall'autore il 23 ottobre 2026. Il test multiplayer **non è ancora eseguibile dall'autore** e rimane un gate separato: non va segnato come superato. Il numero del JAR di sviluppo è stato successivamente aggiornato a `mod_version=1.0.0` su richiesta dell'autore; non creare tag, eseguire merge o pubblicare la 1.0.0 senza approvazione finale; seguire [RELEASE_READINESS.md](pizza-1.0.0/RELEASE_READINESS.md).
