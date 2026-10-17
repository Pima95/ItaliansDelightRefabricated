# Pizza 1.0.0 — Nomi riconosciuti, nomi dinamici e tooltip

> **Nomi e tooltip CONFERMATI funzionanti dall'autore il 17 ottobre 2026.** La fase precedente 4C (pizza cotta piazzabile, quattro spicchi e nutrizione) è stata anch'essa confermata. I singoli test manuali qui sotto restano utili per regressioni; non sono automaticamente tutte prove completate.

## Implementazione

- `PizzaDisplayNames.java`: confronta **insiemi di ID canonici** e non item crudi/cotti, categorie nutrizionali o ordine dei clic; 17 preset attivi già componibili fra i 19 storicamente approvati.
- **Principessa sospesa** (pomodoro + panna incompatibili secondo la nuova esclusività), **Patate e Speck inattiva** (la nuova fetta di patata non è ancora registrata). Non sono state inventate ricette sostitutive.
- `PizzaNamedItem.java`: visualizza tooltip dai data component esistenti, senza mutarli. Le creazioni di `raw_pizza`, `cooked_pizza` e `pizza_slice` aggiungono il `minecraft:item_name` come **componente traducibile**, coerente con il funzionamento degli ItemStack moderni. `CUSTOM_NAME` impostati dall'utente hanno la normale precedenza vanilla.
- Item già conservati da salvataggi anteriori possono mantenere la vecchia etichetta finché non passano da un flusso che ricrea l'item. Il loro payload viene comunque letto dai nuovi tooltip: non si eseguono migrazioni invasive.
- La comparazione riguarda sempre **quattro quarti fisici**; tagli e riunioni grafiche non alterano il gusto. I quarti numerati seguono l'orientamento salvato: **1=sinistra/dietro, 2=sinistra/davanti, 3=destra/dietro, 4=destra/davanti**.
- Per i gusti non riconosciuti con **più di tre tipi** viene mostrato un nome breve con due ingredienti e il numero degli altri; tooltip completo senza abbreviazioni. Ordine degli ingredienti deterministico per ID canonico (non per ordine di posa o lingua).
- Testi in `it_it.json` e `en_us.json`; gli ID, non le stringhe tradotte, sono persistenti. Un ID sconosciuto di vecchi datapack appare come identificativo tecnico, senza perdita di item.

## Compilazione

```powershell
git switch feature/pizza-1.0.0
git pull
.\gradlew.bat clean build
.\gradlew.bat runClient
```

Non è stato eseguito un build Java 25 nel runtime dell'assistente: segnalare il **primo errore completo** se Gradle fallisce.

## Test 1 — Preset uniformi e correttezza del nome

1. Su una pizza intera **non divisa**, applicare salsa di pomodoro + mozzarella a fette + basilico. Rompere la base: nome atteso **Pizza Margherita**. Cuocerla: nome atteso ancora **Pizza Margherita**.
2. Piazzarla cotta, prendere le quattro fette: ciascuna deve chiamarsi **Spicchio di Margherita**. Le proprietà alimentari e la scomparsa delle quattro zone restano identiche alla fase 4C.
3. Provare base senza topping (Bianca), sola salsa (Rossa) e una Caprese bianca: deve bastare il **match esatto** su tutti e quattro i quarti.
4. Per Bianca/Rossa le fette riportano `Spicchio di pizza bianca`/`Spicchio di pizza rossa`, con testi equivalenti in inglese.
5. Aggiungere **un solo** speck a uno dei quarti della Margherita: la pizza intera **non** deve conservare il nome Margherita; i quarti senza speck possono invece continuare a generare `Spicchio di Margherita`.

## Test 2 — Pizza assortita: ogni spicchio resta indipendente

1. Suddividere in quattro: quarto 1 Margherita, quarto 2 panna + speck, quarto 3 sola salsa di pomodoro, quarto 4 combinazione mozzarella + basilico.
2. Nome pizza intera atteso **Pizza assortita**; nel tooltip devono comparire **Quarto 1: Margherita**, **Quarto 3: Rossa** e descrizioni complete per gli altri quarti.
3. Dopo la cottura, ottenere i quattro spicchi in ordine casuale: nomi `Spicchio di Margherita`, `Spicchio con Panna e Speck` (ordine degli ingredienti per ID), `Spicchio di pizza rossa`, `Spicchio con ...`. Nessun ingrediente dei quarti vicini nel tooltip.
4. Rompere una pizza già servita per due quarti: soltanto le altre due fette sono droppate, ognuna con nome e composizione propri.

## Test 3 — Nomi dinamici e abbreviazioni

1. Preparare una pizza uniforme con una **combinazione non approvata** (per esempio mozzarella e speck): nome `Pizza con ...`, non uno dei gusti NO della tabella trasformato in preset fisso.
2. Preparare una pizza uniforme con **4–6 topping differenti** ammessi per quarto: nome abbreviato con **primi due ingredienti + conteggio degli altri**, tooltip `Ingredienti:` con **tutti** gli ingredienti.
3. Variare l'ordine di inserimento, tagliare e riunire: nome e ordine della lista non devono cambiare a parità di composizione.
4. Preparare una pizza con un quarto aggiuntivo differente: deve chiamarsi `Pizza assortita` anche se gli altri tre quarti sono un gusto riconosciuto.

## Test 4 — Equivalenze, esclusioni, compatibilità

1. Ripetere il preset **Salmone e Ricotta** usando salmone a fette crudo e cotto in pizze diverse: **stesso nome riconosciuto**; ID canonico identico, stack originali preservati.
2. Provare un item equivalente di altra mod **solo se già abilitato** dal mapping verificato; non ammettere un item intero soltanto grazie a un tag generico.
3. **Principessa** con pomodoro e panna nella stessa zona deve rimanere vietata alle nuove applicazioni; non riconoscere eventuali vecchi salvataggi con questa combinazione come nuovo preset.
4. **Patate e Speck** NON deve risultare riconoscibile finché non saranno presenti e registrate le fette di patata.
5. Se un vecchio item contiene l'ex flag `legacy_mozzarella`, il tooltip deve renderlo distinguibile dalla mozzarella a fette e non assegnare un preset erroneo.

## Test 5 — Localizzazione e regressione gameplay

1. Cambiare lingua di Minecraft da **Italiano a English (US)** e viceversa senza rigenerare la pizza: nomi e tooltip devono cambiare lingua correttamente, senza mostrare le chiavi di traduzione.
2. Testare pizza cruda, pizza cotta e spicchi in inventario, item droppati con Q, hopper, Pick Block dove applicabile e dopo il salvataggio/riavvio.
3. Controllare che ogni spicchio conservi i valori `FOOD` precedenti, le proprie componenti, e che l'inventario pieno continui a impedire il servizio senza drop o perdita.
4. In multiplayer i due giocatori devono vedere i medesimi nomi (tradotti ciascuno nella propria lingua) e non devono esistere duplicazioni di item.
5. Verificare che il Pizza Oven mantenga cottura, fuoco, ciotole, hopper e animazioni e che non consumi/rimborsi topping extra.

## Registro test

- [ ] `gradlew.bat clean build` passa su Java 25.
- [ ] Nomi fissi solo per i gusti approvati e componibili.
- [ ] Pizza assortita e spicchi indipendenti funzionano.
- [ ] Nomi dinamici e tooltip completi funzionano.
- [ ] Traduzioni IT/EN, equivalenze e salvataggi funzionano.
- [ ] Nessuna regressione a rendering, fame, cottura e rimborsi.

**Non segnare i test come completati prima della conferma dell'autore.**
