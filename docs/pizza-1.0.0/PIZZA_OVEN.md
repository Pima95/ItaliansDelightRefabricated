# Pizza Oven: forno compatto e altri metodi di cottura

## Requisiti CONFERMATI

- **Pizza Oven dedicato** e possibilità di cuocere le pizze anche nei **forni vanilla esistenti** (fornace e affumicatore).
- Pizza Oven **compatto da un solo blocco (1×1×1)**, che cuoce **una pizza per volta**.
- Accetta **tutti i combustibili vanilla validi**, non solo legna.
- **Le pizze non bruciano mai**, indipendentemente da tempo trascorso e tipo di forno.
- La trasformazione da cruda a cotta deve conservare ingredienti, quantità, composizione e dati della pizza.
- **Pala FACOLTATIVA — CONFERMATO:** il Pizza Oven deve funzionare sia **con** sia **senza** la pala per pizza. La pala e' una comodita' di trasporto, non un requisito generale per utilizzare il forno. Il percorso per una pizza creata come item e' ancora da finalizzare.
- **Pizza preparata a terra — CONFERMATO:** non si raccoglie con clic destro a mano vuota. Per toglierla da terra **si rompe**, ottenendo **l'item pizza completo**, crudo o cotto secondo lo stato, con ingredienti, coperture, suddivisioni e ogni dato di preparazione/cottura. **Non** restituire separatamente impasto e ingredienti quando una pizza completa viene rotta. La pala resta un'alternativa facoltativa per trasportarla direttamente.
- **Percorso senza pala — CONFERMATO:** preparare la pizza sul terreno -> rompere la pizza per ottenere il relativo **item completo** -> inserire l'item nel Pizza Oven. Non serve alcun gesto di raccolta a mano vuota. L'item conserva la stessa ricetta personalizzata e gli stessi dati della pizza; inserimento, cottura ed estrazione non devono azzerarli.

## Struttura del Pizza Oven — PROPOSTA

- Forno orientabile verso il giocatore, apertura frontale, pizza visibile all'interno.
- Modello 3D con fuoco/fumo/particelle quando è attivo.
- **Nessuna GUI obbligatoria**: inserimento, rifornimento e raccolta tramite clic destro.
- Una BlockEntity conserva `ItemStack` intero, combustibile, energia e avanzamento; salva i dati quando il chunk si scarica.

### Interazioni proposte

| Gesto | Azione |
| --- | --- |
| Clic destro con pizza cruda tenuta come item | **Percorso diretto proposto, senza pala:** inserisce la pizza-item ottenuta **rompendo la pizza nel mondo** oppure via crafting, se il forno e' libero; preserva tutti i dati |
| Clic destro con fuel valido | Rifornisce lo slot combustibile |
| Clic destro a mano vuota | **Percorso diretto proposto, senza pala:** estrae la pizza pronta come item mantenendo tutte le informazioni |
| Shift + clic destro a mano vuota | **Percorso diretto proposto, senza pala:** estrae una pizza ancora cruda o con cottura incompleta, salvandone il progresso |
| Clic destro con pala per pizza | **Alternativa facoltativa confermata come obiettivo:** pala occupata per inserire e pala vuota per estrarre, mantenendo una sola pizza e tutti i dati. Gesti esatti e precedenze ancora da definire; meccanica non implementata |
| Rottura del forno | Recupera pizza e combustibile non consumato, senza duplicare |

La **facoltativita' della pala e' una decisione confermata**. Le interazioni dirette a mano vuota **sul forno** e con pizza-item elencate in tabella sono **proposte operative da finalizzare**, non gesti gia' approvati nel dettaglio. Il clic destro a mano vuota **sulla pizza posizionata nel mondo NON la raccoglie**. La rimozione richiede la **rottura del blocco pizza**, che restituisce **l'item pizza integro con tutti i dati**, sia crudo sia cotto, senza rimborsare separatamente impasto e topping. **Il trasferimento al forno senza pala e' quindi risolto:** la pizza raccolta come item puo' essere inserita direttamente nel Pizza Oven; la via alternativa via crafting rimane disponibile. I gesti esatti del forno in tabella restano proposte operative, salvo diversa approvazione. La priorita' tra pizza, combustibile e altri oggetti e la gestione dei remainder rimangono da definire. Per i dati della pala vedere [QUARTERS_AND_PEEL.md](QUARTERS_AND_PEEL.md).

## Combustibili

**Confermato:** compatibilità con tutti i combustibili vanilla.

**Comportamenti proposti da implementare/verificare:**
- usare il sistema di burn time di Minecraft/Fabric 26.2, senza lista hardcoded;
- uno slot interno per combustibile, fino allo stack consentito;
- memorizzare l'energia residua di un'unità di fuel per più pizze;
- **consumare energia solo quando una pizza sta effettivamente cuocendo**;
- fermare il consumo quando il forno è vuoto o la pizza è pronta;
- se esaurisce fuel a metà, mettere la cottura in pausa, non azzerarla;
- salvare progressi e sospenderli con chunk scaricato;
- gestire i contenitori del combustibile, ad esempio **secchio vuoto dopo lava**;
- preservare fuel e stato tra caricamenti, multiplayer e automazioni.

Il forno non deve continuare a cuocere una pizza dopo il 100%, perché **non deve mai bruciarsi**.

## Durate di cottura — valori PLACEHOLDER

| Sistema | Tempo proposto |
| --- | ---: |
| Pizza Oven già caldo | 20 secondi |
| Affumicatore vanilla | 30 secondi |
| Fornace vanilla | 60 secondi |

Non sono tempi approvati; verranno bilanciati dopo test. Eventuale preriscaldamento ancora da decidere.

## Automazione hopper — PROPOSTA

| Lato | Inventario |
| --- | --- |
| Sopra | Inserimento pizza cruda |
| Laterali | Inserimento combustibile |
| Sotto | **Solo estrazione pizza cotta** |
| Fronte | Uso manuale |

Nessun hopper deve poter estrarre pizza ancora cruda o parzialmente cotta. Testare hopper paralleli, rifornimento e estrazione, inclusi i remainder dei combustibili.

## Fornaci vanilla: problema tecnico da risolvere

La cottura di un item con data component personalizzati tramite **smelting ordinario** non garantisce il trasferimento automatico dei dati dall'input all'output. Per questo occorre progettare una soluzione **specifica per la pizza**, compatibile con Minecraft 26.2, Fabric, JEI, hopper e altre mod, che non alteri le ricette di oggetti estranei.

**La compatibilità è richiesta, ma non ancora implementata né verificata.** Prima della release va testata su pizza personalizzata con più condimenti: l'output deve conservare dati, nome dinamico e resa visiva.

## Ricetta per costruire il forno — PROPOSTA

Mattoni, mattoni di pietra e fornace vanilla; forma e quantità da scegliere nel bilanciamento. Non introdurre nuovi minerali o materiali obbligatori solo per costruire il forno.
