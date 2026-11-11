# Modelli dei salumi appesi

Le 14 varianti appese usano **sette geometrie dedicate**, al posto del corpo
generico condiviso. Le sagome riprendono le icone esistenti e sono costruite
con cuboidi a gradini, come i formaggi appesi.

[Anteprima ruotabile con confronto item](MODELLI_SALUMI.html) ·
[Catalogo delle texture](TEXTURE_SALUMI.md) ·
[Prompt dei nuovi materiali](MODELLI_SALUMI_PROMPTS.json)

## Sagome

Le misure sono espresse in pixel del modello, con 16 pixel per blocco.
L'ingombro indicato riguarda il corpo, esclusi osso e legature.

| Famiglia | Sagoma | Larghezza × altezza × profondità | Stati |
|---|---|---|---|
| Prosciutto | Coscia asimmetrica, larga in basso, stinco e osso sporgente | 9 × 14 × 7 | Salato / Crudo |
| Salame | Insaccato sottile e lungo, estremità rastremate e due legature | 4 × 13 × 4 | Crudo / Stagionato |
| Pancetta | Lastra larga e sottile, spigoli smussati a gradini | 8 × 9,5 × 4 | Preparata / Stagionata |
| Guanciale | Taglio a goccia, largo in alto e appuntito in basso | 7 × 10,5 × 4,5 | Preparato / Stagionato |
| Bresaola | Taglio magro allungato, più spesso del Salame | 5,5 × 13 × 4,5 | Preparata / Stagionata |
| Coppa | Taglio corto e panciuto, con legature e fronte marezzato | 7,5 × 10,5 × 6 | Preparata / Stagionata |
| Speck | Trancio rettangolare più spesso della Pancetta, fascia di grasso in alto | 8 × 10 × 5 | Preparato affumicato / Stagionato |

Il riferimento principale è la sagoma degli item già presenti. Come riscontro
fotografico sono state consultate immagini di salumi interi, tra cui il
[Guanciale di Tito Speck](https://www.titospeck.it/products/guanciale-pezzo-intero).
Le fotografie non sono incluse nelle texture.

## Materiali e sospensione

- Ogni stato ha **due PNG opachi 16×16 separati**: `hanging_<stato>.png`
  per cotenna/budello e `hanging_<stato>_cut.png` per carne e grasso. I
  pannelli sono esportati dalle sorgenti originali a piena risoluzione,
  conservando 16 righe per materiale invece delle precedenti otto.
- Prosciutto, Pancetta, Guanciale, Coppa e Speck hanno un **piano di taglio
  frontale continuo**, sagomato secondo il prodotto. Le parti del corpo che
  mostrano il taglio terminano alla stessa profondità: nessuna terrazza di
  cotenna attraversa la carne. I bordi, il collo e il retro usano la superficie
  esterna. Salame e Bresaola conservano il taglio all'estremità inferiore.
- Le UV usano un pixel della texture per unità del modello, come i formaggi
  appesi. Il taglio è mappato come un unico disegno fra i cuboidi; il grasso
  resta a sinistra nel Prosciutto e in alto nello Speck. Le facce inferiori
  invertono Z secondo la convenzione Minecraft. Non si ruota o ingrandisce
  una porzione del vecchio atlante per riempire l'intero corpo.
- La mappatura mantiene la stessa densità di pixel nei due assi di ogni
  faccia. Sui lati il pannello del budello è ruotato di 90° per sfruttare
  l'asse lungo dell'atlante; sul taglio si usa un ritaglio proporzionato,
  mantenendo dritti gli strati di grasso. Le UV del fondo invertono Z come
  richiesto dai modelli Minecraft, così il disegno continua tra i cuboidi.
  Un margine separa il campionamento del budello da quello della carne.
- Le differenze fra preparato e stagionato rimangono leggibili: budello chiaro
  del Salame, carne scura della Bresaola, crosta pepata del Guanciale, cotenna
  ambrata o bruna dello Speck.
- L'osso del Prosciutto usa `hanging_meat_bone.png`. Le legature sono elementi
  del modello e riutilizzano `cheese_twine.png`.
- Il cappio arriva a **Y=6**, sovrapponendosi alla parte inferiore dell'uncino
  come nei modelli di Provolone e Scamorza. Il corpo scende sotto il blocco
  dell'uncino; il più lungo arriva a Y=-12.
- Le sagome di selezione in `CuredMeatShapes.java` seguono i corpi e l'osso,
  con un volume compatto per il cappio. Il sistema esistente continua a
  riservare il blocco sottostante all'uncino occupato.

I file `hanging_<famiglia>_base.json` contengono le geometrie; i 14 modelli
degli stati ereditano la propria base e scelgono i materiali `meat` (esterno)
e `cut` (taglio). Il genitore
`hanging_cured_meat.json` conserva solo le impostazioni e i materiali comuni.
Le icone degli item restano quelle già presenti.

## Verifica

L'anteprima incorpora i JSON e i PNG effettivi. Usa WebGL con controllo della
profondità, campionamento nearest-neighbor e illuminazione semplificata;
permette di osservare anche retro e fondo e di ingrandire una singola famiglia.
Non sostituisce il rendering in gioco.

Controlli del **2 ottobre 2026**:

- [x] Sette sagome distinte e 14 varianti collegate ai rispettivi stati dell'uncino.
- [x] Coordinate, UV, ereditarietà e riferimenti delle texture verificati.
- [x] Correzione UV: eliminate la dilatazione verticale dei pixel e le
  discontinuità sul fondo in tutte le sette basi. Controllate proporzioni
  dei pixel, continuità fra facce complanari e separazione dei pannelli
  dell'atlante, confrontando anche la convenzione UV di Minecraft 26.2.
- [x] Verifica della mappatura in scala 1:1, della continuità del taglio e
  dell'assegnazione di materiale esterno/taglio alle facce appropriate.
- [x] Quattordici coppie di materiali e un materiale osso a 16×16,
  completamente opachi: 29 PNG blocco.
- [x] Sagome Java confrontate con i corpi e l'osso dei modelli.
- [x] Anteprima osservata frontalmente, posteriormente e dal basso.
- [ ] Build offline con verifica dei 22 JSON dei modelli
  e dei 29 PNG rispetto alle sorgenti nel JAR
  `build/libs/italiansdelight-0.2.0.jar`, che include anche `CuredMeatShapes`.

Da provare in Minecraft:

- [ ] Ricontrollare la correzione rispetto agli screenshot segnalati: il
  taglio frontale deve restare continuo, senza fasce di cotenna intermedie
  o ingrandimenti di pochi pixel su tutto il corpo.
- [ ] Appendere ogni coppia preparato/finito e confrontarla con l'item tenuto in mano.
- [ ] Controllare davanti, dietro, ai lati e dal basso: nessun foro, faccia
  viola/nera, superficie sovrapposta o legatura staccata.
- [ ] Provare uncini nei quattro orientamenti, anche a ridosso di pareti e
  fra due chunk: il cappio deve toccare il metallo e il corpo restare visibile.
- [ ] Puntare il corpo e verificare selezione, recupero del prodotto e rottura;
  il blocco sottostante deve liberarsi quando l'uncino torna vuoto.
- [ ] Verificare il cambio di materiale durante la stagionatura e ricontrollare
  Provolone e Scamorza accanto ai salumi.
