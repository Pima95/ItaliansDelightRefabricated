# Riferimento tecnico — Italian's Delight 1.2.0

Questo documento raccoglie le informazioni tecniche aggiornate e consolidate della 1.2.0. È un riferimento per lo sviluppo e il controllo della release, **non** una lista di prove manuali già completate. Per provarne il funzionamento vedi [TESTING.md](TESTING.md).

## 1. Baseline e compatibilità

- Branch `feature/mc-26.2`; build candidata `1.2.0-mc26.2` (non ancora pubblicata) per Minecraft **26.2**, Java **25** e Fabric. I requisiti reali sono definiti in `gradle.properties` e `build.gradle`.
- Preservare gli ID degli item, blocchi e fluidi della 1.0.0 e 1.1.0 e i dati dei mondi esistenti. Non effettuare rename distruttivi né sovrascrivere salvataggi durante i test.
- Farmer's Delight Refabricated è una dipendenza obbligatoria; JEI fornisce categorie e visualizzazione di ricette. Serene Seasons/GlitchCore e Thirst Was Taken 2 sono integrazioni opzionali.
- Le categorie e i tag `farmersdelight:*` / `c:*` facilitano l'interoperabilità, ma **non** implicano supporto verificato per qualsiasi altra mod.
- Strategia dei porting: la base 1.2.0 per **26.3** è su `main`; questo branch porta le stesse funzionalità su **26.2** usando le API e il formato worldgen di quella versione, senza Poplar. Valutare poi `26.1.x` separatamente. Le versioni 1.21.x/1.20.x, Forge e NeoForge restano rinviate. Nessun JAR condiviso tra versioni è da presumere compatibile senza test.

## 2. Coltivazioni

### Uva rossa e bianca

Entrambe le varietà hanno semi e blocchi propri (per esempio `grape_seeds` e `white_grape_seeds`), mantenendo gli ID già usati per l'uva rossa. Usano farmland, crescita per stadi e le **corde originali di Farmer's Delight** tese orizzontalmente tra pali, con tralci laterali che portano i grappoli. La raccolta delle uve mature non distrugge la vite; grappoli e semi rispettano la varietà. Il colore delle foglie dipende dal bioma e dall'integrazione stagionale dove presente. Le corde verticali di FDR conservano il comportamento originale.

I grappoli coltivati mostrano più stadi visivi di maturazione e sono raccoglibili soltanto quando maturi. Al momento della rottura dei tralci va ripristinata la corda orizzontale sottostante, non una corda verticale.

Le **viti selvatiche** sono piante a due blocchi: generano nei biomi temperati (pianure, prati, foreste, foreste fiorite, foreste di betulle e boschetti di ciliegi). Ogni varietà ha tentativi di patch di ordine **1 ogni 100 chunk idonei**, con gruppi dispersi e decorazioni occasionali di `coarse_dirt` e `dead_bush`. Le patch nascono **solo in nuovi chunk**. La raccolta delle selvatiche avviene rompendo la pianta, con trattamento delle cesoie compatibile con FDR.

### Peperoncini e sostegni

I peperoncini seguono la logica di crescita ispirata ai pomodori di FDR: fasi acerbe, frutto maturo raccoglibile, ricrescita e attraversamento senza collisione piena. Sono supportate le corde e i pali previsti; il Ghost Pepper è un risultato raro della raccolta. Le interazioni con i pali non devono trasformare la farmland in terra normale o far perdere corde e semi. Il comportamento dei pomodori originali di Farmer's Delight non va alterato.

## 3. Liquidi e fermentazione

La Crushing Tub restituisce **250 mB** di succo per grappolo d'uva di ciascuna varietà e mantiene fluidi distinti per uva rossa/bianca. Sono presenti anche succo di ciliegie, succhi di mela, olio, latte, siero, caglio e gli altri liquidi già introdotti.

Il **Brewing Barrel** usa lo stesso sistema di blocco e interfaccia per le varianti di legno (16 nel design attuale). La GUI misura **176×198**, con tre slot superiori (coltura, ingresso, uscita) e tre inferiori per il ritorno dei contenitori. Serbatoi: **8.000 mB ingresso**, **8.000 mB uscita**, **1.000 mB coltura**. La fermentazione dura **12.000 tick** (circa dieci minuti a 20 TPS). Bottiglie e secchi corrispondono rispettivamente a **250 mB** e **1.000 mB**. I trasferimenti devono essere atomici; un serbatoio d'uscita non miscela prodotti incompatibili.

### Trasformazioni della botte

| Ingresso | Condizione | Prodotto |
| --- | --- | --- |
| Succo d'uva rossa | Coltura opzionale | Vino rosso (`wine`) |
| Succo d'uva bianca | Coltura opzionale | Vino bianco (`white_wine`) |
| Succo di ciliegie | Coltura opzionale | Vino di ciliegie (`cherry_wine`) |
| Miele | Coltura opzionale | Idromele (`mead`) |
| Succo di mela | Coltura presente (almeno 250 mB) | Sidro (`cider`) |
| Succo di mela | Nessuna coltura | Aceto di mele |
| Sidro | Qualsiasi | Aceto di mele |
| Vino rosso | Qualsiasi | Aceto di vino rosso |

Le bevande fermentate possono avere una **qualità individuale**; gli aceti non usano qualità alcolica. La qualità è salvata con l'oggetto e con il contenuto della botte; a differenza degli aceti, le bottiglie di vino, idromele e sidro sono state rese consumabili con effetti legati alla qualità. La presenza della coltura influisce sui risultati dei lotti; la modifica della coltura riavvia il timer.

Restano gli identificatori storici e le chiavi NBT di persistenza quali `JuiceAmount`, `WineAmount`, `WineQuality`, `CultureAmount`, `CultureQuality` e `BrewTime`. Il codice gestisce migrazione e limiti dei volumi; questi percorsi richiedono prova di caricamento e ricollocazione in un mondo reale.

### Bevande e contenitori

Sono consumabili i succhi di mela (normale e dorata), uva rossa e bianca, ciliegie, olio d'oliva e le bevande fermentate previste. Il succo di mela dorata conferisce **Rigenerazione II (5 s)** e **Assorbimento I (2 minuti)**. L'olio è bevibile ma non disseta. Siero, caglio, aceti e marmellata non sono bevande. Con Thirst Was Taken 2 i valori di sete sono definiti in `data/italiansdelight/thirstwastaken2/drinks/italiansdelight_beverages.json`; senza la mod opzionale non devono essere introdotte dipendenze obbligatorie.

### JEI e grafica

La GUI JEI del fermentatore usa l'asset `assets/italiansdelight/textures/gui/jei/brewing_barrel.png`; l'interfaccia in gioco usa `assets/italiansdelight/textures/gui/brewing_barrel.png`. I liquidi hanno animazioni, nomi localizzati e icone distinte per secchi/bottiglie. La provenienza degli sprite adattati da **Rustic** e del modello di botte derivato da **Alcocraft** è descritta nei [crediti](../../CREDIT.md) e nelle [attribuzioni di terze parti](../../THIRD_PARTY_NOTICES.md).

## 4. Nuovi alimenti e pizze

| Nuovo alimento | Preparazione |
| --- | --- |
| Pasta piccante alle olive | Cooking Pot |
| Pasta con ricotta e pancetta | Cooking Pot |
| Risotto con scamorza e olive | Cooking Pot |
| Pasta con pesto di mandorle e speck | Cooking Pot |
| Macedonia di uva e ciliegie | Crafting shapeless con ciotola |
| Biscotti alle mandorle | Crafting shapeless, quattro biscotti |

Le ricette della Cooking Pot richiedono il contenitore di servizio e devono restituire correttamente eventuali contenitori degli ingredienti. Le texture dei biscotti richiamano la sagoma dei biscotti di Minecraft/FDR, la macedonia usa la sagoma dell'insalata di mozzarella, i risotti conservano il piatto dei risotti preesistenti. L'inventario creativo colloca la macedonia immediatamente dopo l'insalata di mozzarella.

La 1.2.0 aggiunge **Pizza piccante alle olive** (pomodoro, mozzarella, olive snocciolate e peperoncino) e **Pizza Principessa** (pomodoro, mozzarella, prosciutto crudo e panna): con questi gusti le pizze riconosciute sono **23**. Ogni quarto conserva condimenti e ordine propri; fra salse e creme l'**ultima applicata è la base**, le precedenti diventano guarnizioni visive, e la rimozione dell'ultima ripristina la precedente. Rimangono i limiti ai condimenti e la conservazione dei dati quando la pizza viene divisa, cotta, servita o ricollocata.

La **focaccia non è inclusa** in questa versione. La Pasta all'Amatriciana normale si prepara nella **Cooking Pot**, non tramite una ricetta shapeless aggiuntiva nella crafting table.

## 5. Risorse grafiche, licenze e manutenzione

**Prompt approvati per le texture:** consultare [docs/PROMPTS.md](../PROMPTS.md), mantenuto nella cartella principale della documentazione. Il testo originale è riportato integralmente lì.

- Item in pixel art **16×16**, trasparenza netta (alpha 0/255), senza antialiasing sui pixel visibili, salvo eccezioni deliberate previste dai modelli; non sovrascrivere asset approvati senza verifica.
- I modelli di blocco dei grappoli devono usare sprite dell'atlante **block**, non percorsi risolti solo nell'atlante degli item, per evitare il nero-magenta.
- Le texture dei Brewing Barrel usano il modello a 22 elementi derivato da Alcocraft con palette dei legni adattate; le animazioni dei liquidi mantengono dimensioni/metadata corretti.
- Nessuna modifica ai registry ID di release, alle ricette approvate o ai salvataggi deve essere motivata unicamente da una riorganizzazione dei documenti.
- Licenze e attribuzioni: [LICENSE](../../LICENSE), [NOTICE](../../NOTICE), [CREDIT](../../CREDIT.md), [THIRD_PARTY_NOTICES](../../THIRD_PARTY_NOTICES.md), [licenza delle crate FDR](../FDR_CRATES_LICENSE.md). L'origine individuale di alcuni sprite legacy non risulta ancora pienamente attribuita.

## 6. Sorgenti e cronologia

Le implementazioni sono in `src/main/java/dev/italiansdelight/` e i dati/asset in `src/main/resources/`. I controlli essenziali sono in `.github/scripts/` e i workflow attivi sono quelli della 1.2.0.

Le specifiche di fase precedenti (inclusi i vecchi audit dei fluidi, le palette dettagliate, i prototipi Rustic e la documentazione separata delle colture) sono nella [cronologia Git precedente alla riorganizzazione](https://github.com/Pima95/ItaliansDelightRefabricated/tree/3801dd9762312c30706137bf06da88023f0ad6a3/docs); il loro contenuto **non** va considerato più aggiornato del codice corrente.
