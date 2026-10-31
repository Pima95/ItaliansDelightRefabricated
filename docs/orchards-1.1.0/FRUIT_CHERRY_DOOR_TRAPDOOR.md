# Ciliegio da frutto — porta e botola

**Branch:** `feature/orchards-1.1.0` (Minecraft 26.2 / Fabric).

## Implementazione

- **Porta** `fruit_cherry_door` — tre porte da sei assi; modello vanilla
  a due metà, con cerniere sul solo lato corretto.
- **Botola** `fruit_cherry_trapdoor` — due botole da sei assi; tre modelli
  aperta, inferiore, superiore, con piccoli dettagli metallici in
  prossimità del traverso centrale.
- I due blocchi usano `FRUIT_CHERRY_BLOCK_SET_TYPE` già registrato,
  quindi i suoni e il comportamento redstone sono quelli del legno.
- **Nuova grafica originale** 16×16, disegnata appositamente per il
  ciliegio da frutto: un unico finestrino ad arco, un rinforzo ligneo
  a V sul pannello inferiore e una botola con due trafori a losanga,
  separati da una traversa centrale. Non sono recolor della quercia
  né imitazioni delle decorazioni del mandorlo.
- Per la porta sono state create due sprite delle estremità senza
  cerniere: questo evita che i rivetti appaiano anche sul lato
  della maniglia. Anche l'icona inventario è un disegno specifico,
  coerente con l'arco e il pannello inferiore della nuova porta.
- Sono inclusi Creative, traduzioni, loot (porta soltanto metà inferiore),
  ricette, tag da legno e minabilità con ascia.

## Revisione delle texture — 31 ottobre 2026

- **Item porta:** riposizionato nella sagoma 10×14 pixel standard degli
  item porta vanilla, lasciando trasparenti le prime due righe. Mantiene
  però un'unica apertura ad arco e il rinforzo inferiore originale.
- **Porta superiore:** aggiunti rilievi intagliati, chiaroscuri e venature
  direzionali sui montanti e sulle traverse. La forma dell'arco e le
  parti metalliche non sono cambiate.
- **Porta inferiore:** **invariata byte per byte**, perché già approvata.
- **Botola:** spezzate le campiture monocromatiche dei bordi esterni e
  della cornice interna mediante passaggi di tono che seguono le fibre.
  I due fori a losanga, la traversa e gli elementi metallici conservano
  le stesse posizioni; nessun effetto rumore casuale.
- Tutti i disegni restano 16×16 con trasparenze nette e palette legno
  approvata; l'effetto estetico va ancora verificato in gioco.

## Rilievo del legno sulla botola — 31 ottobre 2026

L'utente ha chiesto di eliminare la sensazione di superficie piatta,
**senza modificare il motivo** già realizzato. La texture
`fruit_cherry_trapdoor.png` ora ha nuove ombre di incasso intorno
ai trafori, riflessi differenziati sulla traversa centrale e venature
direzionali sulle quattro sezioni della cornice.

Sono rimasti **identici pixel per pixel** i due ritagli trasparenti
(24 pixel), le quattro posizioni della cerniera superiore e i due
pixel metallici della chiusura inferiore. La tavolozza usa soltanto
i colori già appartenenti al ciliegio. Le campiture uniformi
più estese passano da 21 a 13 pixel, senza inserire rumore casuale.
La porta (parte superiore, parte inferiore e icona), gli altri legni
e tutti i modelli/ricette restano invariati.

Il nuovo audit verifica l'impronta delle trasparenze, le posizioni
della ferramenta, la varietà delle sfumature e la dimensione
massima delle campiture monocromatiche. Resta da confermare
la resa estetica direttamente nel gioco.

## Controlli

Il test `.github/scripts/check_fruit_cherry_door_trapdoor.py` verifica
registrazioni, tag, loot, modelli e orientamenti, i tre pixel di
cerniera esclusivamente sul lato corretto, l'opacità/trasparenza dei
sprite, la geometria esclusiva degli intagli ad arco/losanga e l'assenza
di modifiche alle texture base approvate.

## Collaudo manuale ancora necessario

- [ ] Orientamenti porta nord/sud/est/ovest, destra/sinistra, aperta/chiusa
- [ ] Nessuna cerniera visibile sul lato della maniglia
- [ ] Chiusura/apertura manuale e tramite redstone
- [ ] Parte alta e bassa coerenti e non invertite
- [ ] Botola aperta, chiusa, superiore, inferiore e collegata alla redstone
- [ ] Item porta coerente con sprite in mondo, ricette e quantità
- [ ] Trasparenze visibili correttamente e assenza di texture viola/nere

Non procedere con i cartelli o le barche prima dell'approvazione estetica
della porta e della botola.
