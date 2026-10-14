# Test selezione dei quarti e mattarello — Pizza Update 1.0.0

> **Fase 3B, codice aggiunto su `feature/pizza-1.0.0`; nuova build e prove in gioco necessarie.**
> Questa fase abilita **solo la suddivisione/riunione logica col mattarello**; nessun rendering delle linee e nessuna nuova applicazione di topping consumabile ancora attivi. Non considerare la mancanza di linee grafiche un errore del codec: l'overlay è previsto nella fase di rendering.

## 1. Build e regressioni

In PowerShell dalla cartella della mod:

```powershell
git switch feature/pizza-1.0.0
git pull
.\gradlew.bat clean build
```

Aprire un mondo di prova, piazzare impasto di Farmer's Delight e stenderlo fino allo stadio finale (a mano oppure con il mattarello). Il bordo del modello finale deve restare spesso **1 pixel** e la stesura con il mattarello deve continuare a costare **1 punto di durabilità**. Le operazioni di divisione e riunione **non** devono consumare ulteriore durabilità.

## 2. Divisione salvata: osservare la BlockEntity

Sulla **pizza completamente stesa**:

1. Digitare `/data get block X Y Z` utilizzando le coordinate esatte del blocco pizza (non del blocco di supporto). Prima di dividere, `PizzaComposition.division_state` deve valere **0**.
2. Guardare ad esempio **verso Nord** e fare clic destro sulla pizza con il mattarello: `division_state = 1`, `orientation = "north"`.
3. Clic destro con mattarello sulla **metà sinistra** rispetto alla direzione originale: `division_state = 3` (solo metà sinistra divisa in quarti).
4. Clic destro con mattarello sulla **metà destra**: `division_state = 7` (quattro quarti disponibili).
5. Un altro clic su una metà già suddivisa non deve modificare lo stato, consumare durabilità o creare un'ulteriore zona.
6. Usare **Shift + clic destro col mattarello** su un quarto sinistro: `division_state = 5` (solo metà destra ancora suddivisa). Poi su un quarto destro: `division_state = 1`. Infine su una delle due metà: `division_state = 0`.
7. Ripetere il test iniziando dalla **metà destra**: gli stati intermedi diventano `5` e poi `7`. Verificare anche lo stato misto (`3` oppure `5`).

**Nota:** i dati sono salvati nel campo `PizzaComposition` della BlockEntity. Le linee di taglio **non sono ancora mostrate sul modello**: per verificare questa fase utilizzare `/data get block`.

## 3. Orientamento e spareggi

- Eseguire la **prima** divisione guardando a Nord, Est, Sud e Ovest, su pizze di prova separate. La proprietà `orientation` deve memorizzare il cardinale selezionato, non cambiare quando il giocatore si gira o ricarica il mondo.
- **Linea parallela allo sguardo**: la prima divisione crea metà sinistra/destra, non davanti/dietro.
- Alle diagonali esatte il verso scelto è quello **in senso orario**: tra N/E -> E, E/S -> S, S/O -> O, O/N -> N.
- Un clic sul **confine centrale** sceglie la metà **destra**; su una metà divisa in quarti, il clic sulla linea trasversale sceglie la parte **davanti**. All'incrocio esatto vince **davanti-destra**.
- La tolleranza tecnica è di `1/1024` di blocco: testare i clic quasi sul confine senza aspettarsi due zone per un clic.
- Provare in particolare le divisioni **miste**: se solo la metà sinistra è suddivisa, cliccare l'altra metà deve selezionare l'intera metà destra, non un quarto nascosto.

## 4. Salvataggio, compatibilità e assenza di duplicazioni

- Uscire e rientrare nel mondo quando `division_state = 3` o `5`; `division_state` e `orientation` devono rimanere invariati.
- Cambiare orientamento del giocatore **dopo** il primo taglio e dividere la seconda metà: la geometria resta agganciata all'orientamento salvato.
- Verificare il prototipo mozzarella esistente e la stesura: non devono essere rimossi né modificati in questa fase.
- Verificare che le operazioni di divisione/riunione non inseriscano dati inventati in `applications` e non consumino ingredienti.

## 5. Logica dei condimenti preparata ma ancora non attiva nei clic

[PizzaCompositionOperations.java](../../src/main/java/dev/italiansdelight/common/pizza/PizzaCompositionOperations.java) contiene funzioni di composizione **senza effetti nel mondo** per:

- applicare una variante solo ai quarti privi dello stesso **ID canonico**, con un solo `ItemStack` originale per applicazione non vuota;
- rifiutare atomicamente il superamento di **sei tipi per quarto** o l'invasione di una zona con una **salsa diversa**;
- trattare crudo/cotto e equivalenze esterne come lo stesso tipo tramite il lookup già validato;
- rimuovere sempre l'ultima applicazione che copre la zona (ordine **LIFO**) e rimborsare l'item originale **solo quando l'ultima parte dell'applicazione viene rimossa**.

**Non provare a testare queste funzioni condendo la pizza in gioco, per ora.** La loro attivazione deve avvenire dopo aver realizzato il trasferimento della pizza formata in un item persistente al momento della rottura del blocco: diversamente si rischierebbe di perdere i condimenti consumati. Il rendering dei topping e la sincronizzazione multiplayer della nuova composizione restano attività successive.
