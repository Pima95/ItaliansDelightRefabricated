# Ciliegio da frutto — stato texture

## Sesta revisione — venature interne ordinate e fughe leggibili (31 ottobre 2026)

L'autore ha chiesto di distinguere meglio le fughe delle **sole assi di ciliegio**
e di riorganizzare il **solo tronco naturale di ciliegio**, mantenendone
assolutamente invariata la cornice.

- `fruit_cherry_log.png`: 162 pixel modificati **esclusivamente nell'area
  interna 14×14**; tutti i 60 pixel dei bordi conservati esattamente,
  verificati anche tramite SHA-256 del perimetro.
- Le ombre interne formano piccoli solchi longitudinali sfalsati e rilievi
  organizzati; niente punti scuri aggiunti senza una funzione strutturale.
  La palette naturale è stata volutamente contenuta a sei tonalità;
  la vena scura più lunga nella prova 2×2 non supera 6 pixel.
- `fruit_cherry_planks.png`: 49 pixel modificati, principalmente sui
  bordi orizzontali delle quattro assi, per contrasto leggibile ma
  non nero. Il dislivello medio di luminanza tra le file y=2/3,
  6/7, 10/11, 14/15 è circa 24,4 / 19,2 / 22,5 / 18,1;
  in precedenza era 13,6 / 10,9 / 12,1 / 10,0.
- Restano **intatti** tutte le texture di mandorlo (incluse le assi
  ripristinate), il tronco di ciliegio scortecciato, le due estremità
  del tronco, gli altri blocchi e `main`.
- Le texture sono 16×16 opache; l'anteprima 3×3 è una verifica
  preliminare, non sostituisce il giudizio in Minecraft.

## Rettifica — 31 ottobre 2026

La precedente modifica a `almond_planks.png` era stata un errore
di interpretazione: l'utente aveva indicato le assi di mandorlo soltanto
come riferimento. Le assi di mandorlo sono state ripristinate
**byte per byte** alla versione anteriore alle modifiche del 31/10
(oggetto Git `88bec8504bc672595a3f52ec93f8a2764c1c4003`).

Le fughe di `fruit_cherry_planks.png` sono state corrette localmente
con variazioni non uniformi di ombra, senza cambiare le altre texture
del ciliegio da frutto.

Separatamente, la texture laterale `almond_log.png` è stata
riorganizzata **solo internamente**, riducendo le isole cromatiche
scollegate e favorendo fibre coerenti. Il perimetro di 1 pixel
(60 pixel) rimane identico. La versione scortecciata e gli altri
asset approvati di mandorlo restano invariati. Collaudo estetico
in Minecraft ancora da effettuare.

## Quinta revisione — giunzioni verticali e fughe morbide (31 ottobre 2026)

- La **sola corteccia** `fruit_cherry_log.png` è stata ritoccata sui
  bordi superiore e inferiore: la differenza di luminosità media fra
  le due righe ora è circa 11,6 (prima 18,7), per ridurre la cucitura
  fra tronchi impilati. Le venature centrali non sono state cambiate.
- `fruit_cherry_planks.png` ha fughe più chiare ai pixel y=3,7,11,15;
  stessa disposizione vanilla delle tavole.
- Anche `almond_planks.png` è stata alleggerita nelle quattro fughe,
  senza toccare corteccia, sezioni, scortecciato, segnaletica o arredi
  approvati del mandorlo. I relativi controlli sono stati riallineati.
- Controlli su texture 16×16 opache e ripetizione 3×3 effettuati fuori
  dal gioco; la resa in Minecraft rimane da approvare.

## Quarta revisione — giunzioni 2×2 senza strisce continue (31 ottobre 2026)

**Feedback dell'autore:** entrambi i tronchi, soprattutto ripetuti in griglie
2×2, mostrano linee verticali molto evidenti. Le texture singole non sono
sufficienti per il collaudo: occorre osservare le ripetizioni.

- Rifatte **soltanto** le texture laterali `fruit_cherry_log.png` e
  `stripped_fruit_cherry_log.png` a 16×16 RGBA.
- Ristrutturati i profili delle fibre con variazioni di direzione
  brevi e motivate dal disegno; la corteccia presenta piccole
  lenticelle orizzontali e solchi più corti, lo scortecciato
  un chiaroscuro più delicato.
- Sui campioni **2×2**, la striscia scura verticale più lunga
  è al massimo di **6 pixel** per entrambe (in precedenza 13
  per la corteccia); nessuna striscia scura passa direttamente
  dal margine inferiore a quello superiore.
- Integrato un controllo di regressione che valuta le due texture
  ripetute, oltre a opacità, palette, fibra ed estensione delle
  campiture; i confronti estetici rimangono da fare in Minecraft.
- Assi, tagli superiori, asset di mandorlo e ulivo, modelli, codice,
  ricette, worldgen e branch `main` restano invariati.

**Stato:** nuova proposta da approvare nel gioco, anche su pareti 2×2.
Non confondere il test sulla continuità delle linee con un
collaudo estetico già approvato dall'autore.

## Terza revisione — organizzazione delle venature (31 ottobre 2026)

**Feedback:** l'autore ha respinto la seconda revisione perché il
chiaroscuro era distribuito in frammenti troppo casuali.

- Sono state riprogettate soltanto le **due texture laterali**,
  `fruit_cherry_log.png` e `stripped_fruit_cherry_log.png`.
- Si è ripartiti dal ritmo delle venature delle texture del mandorlo
  già approvate, mantenendo gruppi connessi di pixel e ricomponendoli
  in **pannelli di corteccia più larghi e direzionali**, con quattro
  piccole lenticelle orizzontali, ombreggiate in modo coerente.
- La versione scortecciata ha fibre più chiare, con segmenti lunghi,
  lievi scostamenti e chiaroscuri coordinati anziché puntini isolati.
- Palette: nove tonalità per la corteccia, otto per lo scortecciato.
  Niente antialias, alfa uniforme, dimensioni 16×16; la quantità di
  colori non è usata come surrogato della qualità grafica.
- Nuove verifiche statiche di **continuità verticale rispetto a quella
  orizzontale**, più un tetto a campiture uniformi troppo grandi.
  I vecchi limiti sulle isole identiche di 13/10 pixel sono stati
  rimossi, perché favorivano l'aggiunta artificiale di rumore.
- **Immutati**: assi e due sezioni superiori, blocchi, modelli,
  ricette, alberi, mandorlo, ulivo e `main`.

**Stato:** proposta da verificare e approvare in Minecraft, non ancora
conclusa sul piano estetico. Non procedere ai derivati prima
dell'approvazione.

## Revisione successiva — tronchi originali, 30 ottobre 2026

**Stato attuale:** `fruit_cherry_log.png` e
`stripped_fruit_cherry_log.png` **non sono più un semplice recolor
della quercia**. La prima versione ispirata a oak è stata superata per
queste due sole superfici laterali, su richiesta dell'autore.

- Corteccia: forma 16×16 con rilievi rosso-castani longitudinali e
  **brevi lenticelle orizzontali sfalsate**, carattere riconoscibile
  dei ciliegi fruttiferi. Nessun simbolo o decorazione applicata.
- Legno scortecciato: **fibre più continue, sottili e dolcemente curve**
  con leggere variazioni ambrate, senza i grandi solchi della corteccia.
- Restano invariati i due tagli `fruit_cherry_log_top.png` e
  `stripped_fruit_cherry_log_top.png`, come pure `fruit_cherry_planks.png`.
- Entrambi i nuovi PNG usano 16×16 pixel RGBA completamente opachi,
  la palette castano-ramata già scelta, nessun filtraggio/antialias.
- Nessuna variazione a registrazioni, crafting, alberi, item, tag,
  `main`, ulivo o mandorlo.

**Esito:** validazione tecnica dei due PNG completata, ma
**approvazione estetica e prova in Minecraft ancora da fare**.

---

## Seconda revisione — chiaroscuro delle fibre (30 ottobre 2026)

**Motivo:** l'autore ha respinto le grandi isole di colore quasi uniforme
nella corteccia e nel tronco scortecciato, giudicando la resa troppo piatta.

- Nuovo `fruit_cherry_log.png`: 10 tonalità castano-ramate con solchi corti
  ombreggiati, creste illuminate e lenticelle sfalsate. La distribuzione
  dei chiaroscuri è stata studiata confrontandola con il mandorlo approvato;
  non viene mantenuta la precedente geometria uniforme a bande.
- Nuovo `stripped_fruit_cherry_log.png`: 9 tonalità coordinate alle assi;
  fibre variate in lunghezza, piccole curve e accenti chiaroscuri.
- Test automatico sulle regioni 4-connesse di pixel identici: limite
  massimo 13 pixel per la corteccia e 10 per lo scortecciato.
- Entrambi i PNG restano opachi, 16×16, senza antialiasing.
- Gli altri tre PNG del ciliegio, il codice, il crafting, i blocchi
  di ulivo/mandorlo e `main` sono **immutati**.
- **Da approvare:** risultato estetico e ripetizione della texture in gioco,
  soprattutto sui tronchi affiancati e orientati orizzontalmente.

## Versione storica superata — recolor completo della quercia

**Stato: proposta grafica implementata; attesa di approvazione in Minecraft.**
**Branch:** `feature/orchards-1.1.0`.

Su richiesta dell'autore sono state sostituite le cinque texture originali della
prima iterazione con un **recolor pixel-per-pixel della quercia di Minecraft 26.2**.
Il riferimento è il repository di asset Minecraft
[`InventivetalentDev/minecraft-assets`](https://github.com/InventivetalentDev/minecraft-assets/tree/26.2/assets/minecraft/textures/block),
ref `26.2`, per le seguenti texture:

| Texture della mod | Base vanilla |
| --- | --- |
| `fruit_cherry_planks.png` | `oak_planks.png` |
| `fruit_cherry_log.png` | `oak_log.png` |
| `fruit_cherry_log_top.png` | `oak_log_top.png` |
| `stripped_fruit_cherry_log.png` | `stripped_oak_log.png` |
| `stripped_fruit_cherry_log_top.png` | `stripped_oak_log_top.png` |

## Regole della revisione

- Dimensione 16×16, PNG RGBA senza semitrasparenza, senza filtri o antialiasing.
- **Nessuna geometria o pattern inventati:** ogni pixel conserva la propria
  coordinata e le stesse aree delle cinque immagini vanilla.
- Il recolor applica esclusivamente la gamma castano-ramata già scelta per il
  ciliegio da frutto, con scortecciato più chiaro della corteccia.
- Le due sezioni conservano identico il nucleo centrale 14×14 del disegno vanilla;
  soltanto il bordo esterno cambia nel passaggio alla versione scortecciata.
- Non modificati codice dei blocchi, crafting, tag, loot, Creative, generazione
  degli alberi, altri legni approvati o `main`.

## Controlli effettuati

I cinque PNG sono 16×16 e opachi; il confronto del nucleo delle due sezioni
è identico pixel per pixel, come nelle risorse oak vanilla.
La build GitHub Actions e soprattutto la verifica estetica in Minecraft
**sono verifiche distinte**: il colore non è ancora approvato dall'autore.

## Approvazione

- [ ] Verificare dal vivo i cinque blocchi isolati e accostati, compresi
  tronco orizzontale e verticale, parte superiore e scortecciatura con ascia
- [ ] Confermare se questa tonalità e le venature oak recolor sono definitive
- [ ] Solo allora avviare le texture e i blocchi derivati del nuovo legno
