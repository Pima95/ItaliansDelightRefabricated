# Modelli e texture dei formaggi nel mondo

Aggiornamento del branch `feature/cheeses`: forme appoggiate su normali blocchi
e forme appese al Cheese Hook, inclusi i due ripiani del rack.
[Anteprima interattiva](MODELLI_FORMAGGI.html)
con rotazione, inclinazione e luce ridotta, costruita dai JSON e dai PNG reali.
L'anteprima usa illuminazione semplificata e non sostituisce la prova in gioco.

## Forme e dimensioni

Le misure sono in unità modello: 16 unità corrispondono a un blocco. Sono
proporzioni adattate alla leggibilità in Minecraft, non una scala in centimetri.
Le forme appoggiate sono cubiche e compatte. Le tre forme ammesse nel rack
usano esattamente le stesse dimensioni anche sulle superfici normali.

| Formaggio | Ingombro del corpo | Fresco | Finale |
|---|---|---|---|
| Parmigiano Reggiano | 12 × 4 × 12 | Crema chiaro, superficie delicata | Crosta ocra dorata con piccoli segni |
| Pecorino Romano | 8 × 4,5 × 8 | Bianco avorio | Crosta grigio-beige, più ruvida |
| Gorgonzola | 10 × 3,5 × 10 | Avorio uniforme | Crosta chiara con macchie verde-azzurre |
| Provolone appoggiato | 8 × 4,5 × 13 | Crema pallido | Giallo dorato |
| Scamorza appoggiata | 6 × 3,5 × 10 | Bianco latte | Paglierino / ambrato |
| Provolone appeso | 8 × 12 × 8, più lo spago | Crema pallido | Giallo dorato |
| Scamorza appesa | 6 × 8 × 6, più lo spago | Bianco latte | Paglierino dopo asciugatura; ambrato se affumicata |

Il Provolone ha corpo a pera e quattro fili sottili che ne seguono il profilo.
La Scamorza è più piccola, con bulbo inferiore, collo stretto e testina. I colori
riprendono gli item esistenti; la crosta del Pecorino e le macchie del Gorgonzola
sono accentuate per distinguere il prodotto pronto anche a distanza.

Fresco e finale condividono la geometria della propria famiglia. Provolone e
Scamorza appoggiati sono distesi e hanno geometrie cubiche semplificate; le
texture sono le stesse dei modelli appesi. Entrambe le asole degli appesi arrivano
fino a `y=6`, entrando di un pixel nell'uncino, il cui punto inferiore è a
`y=5`. Il Provolone arriva a `y=-9`; la Scamorza a `y=-3.5`, entro lo spazio tecnico già riservato sotto
il gancio.

## Risorse

- Modelli: `src/main/resources/assets/italiansdelight/models/block/`.
- Texture: `src/main/resources/assets/italiansdelight/textures/block/`.
- Sei nuove texture `aging_{parmigiano_reggiano,pecorino_romano,gorgonzola}_{fresh,mature}.png`.
- Cinque texture sostituite `hanging_{fresh_provolone,provolone,fresh_scamorza,scamorza,smoked_scamorza}.png`.
- Sei modelli base `aging_cheese_*_base.json` e due `hanging_*_base.json`.
- Undici varianti item appoggiate e cinque appese; lo stato tecnico della
  Scamorza affumicata ha anche un alias `mature=false` con lo stesso aspetto.
- Modelli `rack_lower_*` e `rack_upper_*` per ogni combinazione supportata.

Le undici texture opache 16×16 sono state generate con **imagegen integrato**,
modalità `generate`, e ridotte con campionamento nearest-neighbor. I prompt
sono conservati in [MODELLI_FORMAGGI_PROMPTS.json](MODELLI_FORMAGGI_PROMPTS.json).
Non contengono ombre direzionali dipinte: l'illuminazione viene dal gioco.
La mappatura UV segue le coordinate del corpo, con un texel per unità modello,
senza ricominciare l'intera texture su ogni gradino. Lo spago usa la texture
dedicata già presente.

`CheeseShapes.java` contiene i volumi corrispondenti ai corpi dei modelli:
selezione e collisione delle forme appoggiate dipendono dal formaggio, mentre
il gancio distingue Provolone e Scamorza. I volumi sono precalcolati e condivisi
tra fresco e finale; i fili sottili sono decorativi. Se si modificano le
geometrie, aggiornare anche questi volumi.

## Piazzamento e rack

Tutte le 11 forme intere sono piazzabili e ripiazzabili. Quelle finite
conservano il proprio stato; quelle fresche ricominciano con progresso zero.
Le Scamorze appoggiate sono decorative, senza asciugatura a terra.

Il rack accetta **solo Parmigiano, Pecorino e Gorgonzola**, freschi o stagionati.
Provolone e tutte le Scamorze sono esclusi. I modelli nei ripiani sono traslati
a `y=2` e `y=9`, senza ridimensionamento: il più alto termina a `y=6.5` sul
ripiano inferiore, lasciando spazio sotto la mensola superiore a `y=7`.

`lower_cheese` e `upper_cheese` sincronizzano l'aspetto al caricamento e quando
il contenuto cambia o matura. I vecchi flag di occupazione restano compatibili;
i timer non vengono inviati a ogni tick. Nei salvataggi precedenti un eventuale
Provolone già nel rack resta visibile e recuperabile, senza continuare a
maturare e senza poter essere reinserito. I modelli Cake sono stati rimossi.

## Riferimenti visivi

Fotografie e descrizioni consultate come riferimento, senza incorporare le
fotografie nelle texture della mod:

- [Parmigiano Reggiano, fotografia della forma intera](https://gulfwest.com/dairy-cheese): ruota larga e crosta dorata.
- [Consorzio Pecorino Romano, caratteristiche](https://www.pecorinoromano.com/pecorino-romano/caratteristiche): cilindro più alto rispetto al diametro; i toni grigio-beige seguono anche la sprite già presente nel gioco.
- [Consorzio Provolone Valpadana, forme](https://www.provolonevalpadana.it/en/caratteristiche-del-provolone-valpadana/forme-del-provolone-valpadana/): fotografia del Mandarone a pera e disposizione dello spago.
- [Il Latte, Scamorza Molisana](https://www.lattenews.it/la-scamorza-molisana/): fotografie delle forme appese e strozzatura superiore.
- [ONAF, Gorgonzola](https://www.onaf.it/uploads/public/4409_gorgonzola.pdf): forma cilindrica e crosta irregolare. Le macchie verde-azzurre sono una semplificazione grafica coerente con l'item della mod.

## Prove

Verifiche automatiche: build Gradle offline, risoluzione di parent e texture,
limiti delle UV e dei modelli, PNG 16×16 opachi, differenze tra le palette,
corrispondenza fra corpi e volumi Java. Anteprima controllata con i PNG esportati.

Superati cinque GameTest funzionali in un server Minecraft isolato (oltre
al test di base del framework): 11 item piazzabili/ripiazzabili, 12 rack e due
slot, rifiuto di Provolone/Scamorza, maturazione indipendente, salvataggio e
ripristino, recupero del contenuto dei vecchi rack, reset e drop corretti.

Da provare in Minecraft:

1. Piazzare e raccogliere tutte le 11 forme intere su blocchi pieni, incluse le
   versioni mature e le tre Scamorze; ripiazzare gli item recuperati.
2. Inserire Parmigiano, Pecorino e Gorgonzola freschi/stagionati in entrambi i
   ripiani. Devono comparire i rispettivi formaggi, senza torte o intersezioni
   con il legno. Provolone e ogni Scamorza devono essere rifiutati a slot vuoto.
3. Appendere Provolone fresco/finale e Scamorza fresca/asciugata/affumicata a
   cinque ganci; controllare anche i quattro orientamenti dell'uncino.
4. Verificare le asole a contatto con il gancio e lo spazio sotto ogni forma;
   provare a piazzare un blocco nel volume riservato.
5. Confrontare fresco e finale di giorno e in una stanza illuminata da torce.
6. Per provare rapidamente il cambio visivo durante il processo, usare
   `/data merge block X Y Z {ElapsedTicks:119999}` sul Parmigiano fresco
   piazzato. Per il Provolone fresco appeso usare `71999`; per la Scamorza
   fresca appesa `11999`. Al tick successivo la forma deve cambiare texture
   senza cambiare sagoma. Usare un mondo di prova.
7. Raccogliere e rompere forme fresche/finali, verificando i relativi item;
   ricaricare il mondo con qualche forma ancora piazzata/appesa.
8. Per il cambio visivo nel rack con Gorgonzola fresco sotto e Pecorino fresco
   sopra, usare `/data merge block X Y Z {LowerElapsedTicks:47999}`: solo il
   ripiano inferiore deve mostrare la forma finale al tick successivo.
