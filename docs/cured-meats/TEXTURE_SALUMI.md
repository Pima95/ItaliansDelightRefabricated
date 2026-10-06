# Texture della filiera dei salumi

Catalogo dei 25 item registrati nella prima filiera e dei 14 stati appesi.
[Anteprima interattiva](TEXTURE_SALUMI.html) ·
[Prompt originali delle icone](TEXTURE_SALUMI_PROMPTS.json) ·
[Modelli aggiornati](MODELLI_SALUMI.md) ·
[Prompt dei materiali appesi](MODELLI_SALUMI_PROMPTS.json)

## Criteri grafici

- PNG **16×16**, coerenti con le sprite dei formaggi, le carni vanilla e
  Farmer's Delight Refabricated.
- Item su sfondo realmente trasparente, senza ombre esterne o aloni; sagome
  distinguibili anche alla dimensione originale dello slot.
- Pixel netti, colori raccolti in zone leggibili, dettagli ridotti alla scala
  di Minecraft. Il grasso usa toni avorio, la carne rosa/rossi smorzati e le
  superfici stagionate toni più asciutti e scuri.
- Stessa famiglia riconoscibile tra intermedio, salume finito e fetta. La
  Mortadella cotta rimane rosa chiaro; non assume la crosta di un salame stagionato.
- Texture dei modelli appesi **opache**, senza sagoma dell'item né sfondo
  trasparente, con colori corrispondenti allo stadio del prodotto. Lo spago
  continua a usare la texture già esistente `cheese_twine.png`.
- Le risorse esistenti di formaggi, utensili e altri ingredienti sono conservate.
  Ricette, tempi e valori alimentari restano invariati. I modelli appesi hanno
  sette geometrie dedicate, descritte in [MODELLI_SALUMI.md](MODELLI_SALUMI.md).

## Item e distinzioni

Le texture sono in `src/main/resources/assets/italiansdelight/textures/item/`;
il nome di ciascun PNG coincide con l'ID item.

| Famiglia | Intermedio | Finito | Fetta | Distinzione visiva |
|---|---|---|---|---|
| Prosciutto Crudo | `salted_ham` | `prosciutto_crudo` | `prosciutto_crudo_slice` | Coscia con osso; rosa nel salato, cotenna calda e carne rosata nel finito; fetta irregolare con bordo di grasso |
| Salame | `raw_salame` | `salame` | `salame_slice` | Cilindro stretto; budello crudo rosa, superficie asciutta chiara nel finito; impasto rosso con piccoli dadi di grasso |
| Mortadella | `raw_mortadella` | `mortadella` | `mortadella_slice` | Insaccato più largo; impasto rosa chiaro dopo cottura e grandi lardelli avorio, senza pistacchi |
| Pancetta | `prepared_pancetta` | `pancetta` | `pancetta_slice` | Pancetta tesa, rettangolare, con strati alternati di carne e grasso; fetta a striscia |
| Guanciale | `prepared_guanciale` | `guanciale` | `guanciale_slice` | Taglio affusolato/triangolare, prevalentemente grasso con una vena di carne e bordo più scuro |
| Bresaola | `prepared_bresaola` | `bresaola` | `bresaola_slice` | Taglio magro allungato, rosso vivo nel preparato e bordeaux nel finito; nessuna marezzatura grassa |
| Coppa | `prepared_coppa` | `coppa` | `coppa_slice` | Taglio ovale più largo, carne marezzata da venature di grasso; non puntini separati come il salame |
| Speck | `prepared_speck` | `speck` | `speck_slice` | Taglio rettangolare disossato, una fascia di grasso e superficie affumicata bruna |

Lo Speck ha anche `smoked_prepared_speck`: cotenna ambrata dopo l'affumicatura,
prima della stagionatura. Il finito è più scuro; il preparato non affumicato
rimane rosa e chiaro. Totale: 9 intermedi, 8 salumi finiti, 8 fette.

## Materiali per l'uncino

Le 14 texture sono in
`src/main/resources/assets/italiansdelight/textures/block/`, con prefisso
`hanging_` seguito dall'ID dello stato: `salted_ham`, `prosciutto_crudo`,
`raw_salame`, `salame`, `prepared_pancetta`, `pancetta`, `prepared_guanciale`,
`guanciale`, `prepared_bresaola`, `bresaola`, `prepared_coppa`, `coppa`,
`smoked_prepared_speck`, `speck`.

Dal 2 ottobre 2026 i 14 modelli usano sette basi specifiche, ereditate da
`hanging_cured_meat`. Ogni materiale è un atlante opaco: metà superiore per
cotenna/budello, metà inferiore per il taglio. Le UV esplicite assegnano alle
facce la superficie corretta, senza applicare l'icona trasparente dell'item
ai cuboidi. Il campionamento usa ritagli proporzionati e ruota il pannello
del budello sui lati, evitando pixel stirati lungo il corpo; sul fondo la
proiezione è invertita lungo Z per mantenere il disegno continuo.
Un quindicesimo materiale, `hanging_meat_bone.png`, serve per
l'osso del Prosciutto. Lo spago mantiene la texture già esistente.

Totale attuale: **25 icone e 15 materiali blocco**. I nuovi materiali e le
sagome sono visibili nell'[anteprima dei modelli](MODELLI_SALUMI.html).

La Mortadella e lo Speck non ancora affumicato non hanno una variante appesa
nella filiera corrente e quindi non richiedono texture blocco dedicate.

## Produzione e verifica

Asset creati con **imagegen integrato**, un'immagine per risorsa. Esportazione
con campionamento nearest-neighbor a 16×16, senza disegnare o sostituire le
sprite tramite codice; trasparenza generata conservata negli item.
Le icone hanno almeno un pixel di margine trasparente. Nelle fette sottili di
Pancetta e Speck i margini vuoti della sorgente sono ritagliati prima della
riduzione, per mantenere leggibili gli strati di carne e grasso.
I file dei prompt contengono destinazione, famiglia e stadio per ogni asset.
`TEXTURE_SALUMI_PROMPTS.json` documenta la prima generazione delle icone e dei
materiali generici; per i materiali appesi attuali fa fede
`MODELLI_SALUMI_PROMPTS.json`.

L'anteprima incorpora i PNG finali e i modelli JSON effettivi, consente di
confrontare gli stadi, cambiare sfondo e ruotare i prodotti appesi. Il rendering
è semplificato e non sostituisce la verifica in Minecraft.

Verifiche della prima generazione, completate il **1 ottobre 2026**
(per le successive modifiche ai modelli e ai materiali, vedere
[la verifica del 2 ottobre](MODELLI_SALUMI.md#verifica)):

- [x] 39 PNG validi a 16×16: 25 icone con margini trasparenti, 14 materiali
  appesi completamente opachi, nessuna immagine vuota.
- [x] Riferimenti alle texture locali risolti in tutti i modelli della mod;
  definizioni item e traduzioni EN/IT presenti per i 25 item.
- [x] Controllo visivo dell'anteprima con i PNG finali e tutti i 14 modelli appesi.
- [x] `build --offline` riuscita con Fabric Loom 1.18.2; le 39 texture e i 14
  modelli aggiornati sono presenti e corrispondono alle sorgenti nel JAR
  `build/libs/italiansdelight-0.2.0.jar`.

Da provare in gioco:

- [ ] Controllare tutti i 25 item nel gruppo creativo e in JEI: niente texture
  mancanti, fondi rettangolari, aloni o icone tagliate.
- [ ] Confrontare intermedio e finito di ciascuna famiglia, soprattutto i tre
  stadi dello Speck e la Mortadella prima/dopo la cottura.
- [ ] Appendere tutti i 14 stati ammessi e controllare ogni lato: superfici
  piene e differenza leggibile tra preparato e finito.
- [ ] Verificare le 8 fette come output del Cutting Board e su sfondi chiari/scuri.
- [ ] Ricontrollare i formaggi già esistenti accanto ai salumi per coerenza visiva.
