# Spicchi cotti riposizionabili — 1.1.0

## Regole di gioco

- Uno spicchio **già raccolto** (`italiansdelight:pizza_slice`) può essere
  rimesso su una pizza cotta parzialmente servita, in uno dei suoi quarti liberi.
- Può essere trasferito da una pizza a un'altra, conservando *esattamente* il
  proprio elenco di topping canonici, flag mozzarella storica, nome, texture
  e valori di cibo. Nessun topping crudo né ciotola vengono restituiti.
- Uno spicchio può essere piazzato **da solo** sopra un altro blocco valido
  secondo `PizzaDoughSupport`. Il posizionamento usa la medesima geometria
  di un quarto della pizza, non un item fluttuante sul terreno.
- Per inserire in una pizza incompleta, clic destro con lo spicchio in mano:
  la regione selezionata riceve la fetta se vuota; se la porzione cliccata
  è già occupata, il primo quarto libero viene scelto deterministicamente.
  Questo è necessario perché le porzioni mancanti non hanno una hitbox.
- La posa consuma **un solo spicchio** in Sopravvivenza; in Creativa non lo
  consuma. Una pizza piena, una superficie non approvata, un blocco occupato
  e il fallimento della mutazione non consumano l'oggetto.
- Una pizza *mai servita* con quattro quarti continua a droppare un solo
  `cooked_pizza`, con dati originali intatti. Tuttavia, **appena ne viene
  prelevato un quarto**, un flag `ServedEver` resta vero anche quando
  successivamente tutti e quattro gli spazi sono riempiti di nuovo.
- Una pizza con `ServedEver=true` **non può più essere recuperata integra**:
  rottura del blocco, perdita del supporto o prelievo con `Pizza Peel`
  non devono mai generare un `cooked_pizza` nuovo. Rottura/perdita del
  supporto restituisce esattamente gli spicchi ancora presenti, senza duplicati.
- Per i quarti reinseriti, il BlockEntity salva `InsertedSlices` come elenco
  di `PizzaSliceData` con l'indice fisico di destinazione. Per gli altri
  quarti mantiene i dati della pizza originale.
- Servire **di nuovo** uno spicchio reinserito elimina quel record
  `InsertedSlices`, lasciando libero lo slot per un altro spicchio.
- Persistenza: `ServedEver` e `InsertedSlices` sopravvivono al caricamento
  del chunk/mondo, con fallback compatibile per le vecchie pizze.
- Rendering: i topping sono ricostruiti per ogni quarto dalla fetta
  effettivamente presente, compresa la mozzarella legacy soltanto nel quarto
  interessato. Non riutilizzare i topping rimossi.

## Test manuali Minecraft 26.2 (non ancora eseguiti)

- [ ] Pizza A con quattro gusti: prelevare il quarto 2; rimetterlo sullo
      spazio vuoto. Lo stack diminuisce di una unità e il gusto originale
      ricompare. Prelevarlo una seconda volta e verificare nome/componenti.
- [ ] Pizza A senza quarto 1, Pizza B con spicchio diverso: trasferire da B
      a A; la texture sul mondo e lo spicchio successivamente raccolto
      devono essere del gusto di B, non di A.
- [ ] Riposizionare uno spicchio su pietra, terra, legno e altri supporti
      validi. Su chest, bordi e superfici non approvate la posa deve fallire
      senza consumare lo spicchio.
- [ ] Cliccare un quarto pieno di una pizza con almeno uno slot vuoto:
      deve utilizzare uno slot libero senza sostituire l'occupante.
- [ ] Rimettere gli ultimi spicchi di una pizza già servita fino a 4/4,
      romperla: devono cadere quattro **spicchi**, non una pizza intera.
- [ ] La pala da pizza deve poter recuperare un'intera pizza mai servita,
      ma non una pizza 4/4 ricostruita dagli spicchi.
- [ ] Spicchio singolo sul mondo: clic destro a mano vuota lo raccoglie.
      Rompere il supporto: drop **uno** spicchio, senza item duplicati.
- [ ] Riposizionare e togliere tre volte uno spicchio dallo stesso quarto:
      nessuna duplicazione, nessun errore `Destination already inserted`.
- [ ] Aprire/chiudere il mondo con pizza 1/4 e pizza ricostruita 4/4,
      ripetere la prova dei drop; provare anche il server dedicato.
- [ ] Inventario pieno e due giocatori sul medesimo quarto:
      nessuna perdita/doppio drop.
- [ ] Consumare uno spicchio trasferito: saturazione/Nourishment rimangono
      legati ai suoi dati, non alla pizza destinataria.

## Regole grafiche

I soli quattro PNG senza mozzarella filante (Bianca, Rossa, Guanciale/Pecorino,
Mortadella/Ricotta/Mandorle) ricevono una punta più lunga e triangolare:
sei nuovi pixel di crosta al posto della terminazione corta. I 38 pixel
della farcitura sono immutati; sagoma delle altre fette e Margherita originale
inalterata. Tutte le texture rimangono 16×16, alpha 0/255.

## Verifiche automatiche

- `.github/scripts/check_pizza_slice_replacement.py`: ordine delle
  mutazioni, dati persistenti, prevenzione recupero della pizza integra,
  migrazione della semantica di rendering, recupero di una singola fetta.
- `.github/scripts/check_pizza_support_drops.py`: nuovi drop del blocco
  servito e rimozione sicura del supporto.
- `.github/scripts/check_named_pizza_slices.py`: soli sei pixel di punta
  in aggiunta alle precedenti modifiche autorizzate. Le farciture originali
  dei quattro spicchi sono bloccate con SHA-256.

**Nessuna modifica di `main`; collaudi in gioco ancora necessari.**
