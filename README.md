# Italian's Delight

**Italian's Delight** è un add-on per **Farmer's Delight Refabricated** su
**Fabric / Minecraft 26.2**, dedicato alla cucina italiana.

L'obiettivo del progetto non è aggiungere soltanto nuovi food item, ma costruire
catene di lavorazione integrate con i sistemi di Farmer's Delight, riutilizzando
quando possibile Cooking Pot, Cutting Board, fonti di calore, contenitori e tag
condivisi.

## Stato del progetto

La mod è attualmente in sviluppo, ma contiene già diverse funzionalità
utilizzabili in gioco.

### Cheese Vat

La **Cheese Vat** è una macchina personalizzata dedicata alla produzione di
formaggi e ingredienti caseari.

Attualmente supporta:

- inventario persistente;
- 3 slot ingrediente;
- slot per il contenitore;
- output separato dalla preview del prodotto cotto;
- ricette shapeless personalizzate;
- requisito di una fonte di calore;
- GUI dedicata con indicatore di progresso;
- shift-click;
- crafting remainder;
- sincronizzazione client/server;
- integrazione con JEI.

Le prime lavorazioni disponibili sono:

- latte + caglio → **cagliata**;
- allium → **caglio**;
- cagliata + sale → **mozzarella**.

### Integrazione con Farmer's Delight

Sono già presenti ricette che utilizzano direttamente i sistemi di Farmer's
Delight, tra cui:

- pasta + tomato sauce → **pasta al sugo** tramite Cooking Pot;
- riso + tomato sauce → **risotto al sugo** tramite Cooking Pot;
- mozzarella → **4 fette di mozzarella** tramite Cutting Board;
- pomodoro → **4 fette di pomodoro** tramite Cutting Board.

### Produzione del sale

Il sale può essere prodotto facendo evaporare l'acqua in un normale calderone
vanilla.

Un calderone completamente pieno richiede **12.000 tick utili** di evaporazione.

Il processo:

- avanza soltanto durante il giorno;
- si mette in pausa con pioggia o temporale;
- si mette in pausa se esiste qualsiasi blocco sopra il calderone;
- non avanza quando il chunk non è caricato;
- si azzera se l'acqua viene rimossa o ridotta oppure se il calderone viene
  rotto o sostituito;
- conserva il progresso tra i riavvii del mondo.

Quando l'evaporazione termina, sul fondo del calderone compare uno strato
visibile di sale. Interagendo con esso si ottengono casualmente **da 3 a 7
unità di sale** e il blocco torna a essere un calderone vuoto.

In modalità creativa il Pick Block permette inoltre di copiare direttamente il
calderone con il residuo di sale e ripiazzarlo mantenendo quello stato.

## Requisiti di sviluppo

| Componente | Versione |
|---|---|
| Minecraft | 26.2 |
| Java | 25 |
| Fabric Loader | 0.19.5 |
| Fabric API | 0.161.0+26.2 |
| Fabric Loom | 1.17-SNAPSHOT |
| Farmer's Delight Refabricated | 26.2-3.6.26+refabricated |
| JEI | Curse Maven file 8937443 |

Farmer's Delight Refabricated viene scaricato automaticamente tramite
**Cassian's Maven** durante la configurazione Gradle.

## Build

Su Windows:

```powershell
.\gradlew build
```

Su Linux/macOS:

```bash
./gradlew build
```

Il JAR risultante viene generato nella cartella:

```text
build/libs/
```

Per avviare il client di sviluppo:

### Windows

```powershell
.\gradlew runClient
```

### Linux/macOS

```bash
./gradlew runClient
```

## Struttura del codice

Il progetto separa la logica per responsabilità:

- `common/` — blocchi, BlockEntity, ricette, registri e logica condivisa;
- `client/` — GUI e codice esclusivamente client;
- `integration/jei/` — integrazione con JEI;
- `mixin/` — hook sulle meccaniche vanilla;
- `resources/assets/` — modelli, texture, blockstate e traduzioni;
- `resources/data/` — ricette, tag, loot table e advancement.

Il codice sorgente contiene commenti dedicati soprattutto nei punti in cui il
flusso non è immediatamente evidente, così da rendere più semplice la
manutenzione e l'aggiunta di nuove feature.

## Roadmap e documentazione

La roadmap completa, le decisioni di design, la struttura dettagliata del
progetto e lo stato delle feature sono mantenuti in:

[**docs/REQUISITI.md**](docs/REQUISITI.md)

Tra le funzionalità pianificate figurano:

- filiera completa della pasta;
- ulteriori formaggi e possibile stagionatura;
- nuove colture e ingredienti;
- forno a legna;
- pizza;
- advancement dedicati;
- ulteriore compatibilità con l'ecosistema Farmer's Delight.

## Licenza

Il progetto include un file [LICENSE](LICENSE) nella root del repository.
