# Pizza Oven: forno compatto e altri metodi di cottura

## Requisiti CONFERMATI

- **Pizza Oven dedicato** e possibilità di cuocere le pizze anche nei **forni vanilla esistenti** (fornace e affumicatore).
- Pizza Oven **compatto da un solo blocco (1×1×1)**, che cuoce **una pizza per volta**.
- Accetta **tutti i combustibili vanilla validi**, non solo legna.
- **Le pizze non bruciano mai**, indipendentemente da tempo trascorso e tipo di forno.
- La trasformazione da cruda a cotta deve conservare ingredienti, quantità, composizione e dati della pizza.

## Struttura del Pizza Oven — PROPOSTA

- Forno orientabile verso il giocatore, apertura frontale, pizza visibile all'interno.
- Modello 3D con fuoco/fumo/particelle quando è attivo.
- **Nessuna GUI obbligatoria**: inserimento, rifornimento e raccolta tramite clic destro.
- Una BlockEntity conserva `ItemStack` intero, combustibile, energia e avanzamento; salva i dati quando il chunk si scarica.

### Interazioni proposte

| Gesto | Azione |
| --- | --- |
| Clic destro con pizza cruda | Inserisce se il forno è libero |
| Clic destro con fuel valido | Rifornisce lo slot combustibile |
| Clic destro a mano vuota | Estrae la pizza **pronta** |
| Shift + clic destro vuoto | Recupera una pizza ancora cruda/con cottura incompleta |
| Clic destro con pala per pizza | Eventuale inserimento/estrazione agevolati; pala facoltativa |
| Rottura del forno | Recupera pizza e combustibile non consumato, senza duplicare |

La priorità tra pizza, combustibile e altri oggetti e la gestione dei remainder sono ancora da definire.

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
