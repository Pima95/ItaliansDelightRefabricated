# Test caricamento equivalenze JSON — Pizza Update 1.0.0

> **Secondo passo di sviluppo, aggiunto al branch il 13 ottobre 2026.** Il registro statico canonico è già stato compilato e confermato dall'autore; **il nuovo reload listener deve ancora essere compilato e testato in Minecraft 26.2**. Non implica che i topping generici possano essere applicati alla pizza: le interazioni saranno implementate separatamente.

## File interessati

- [PizzaToppingMappings.java](../../src/main/java/dev/italiansdelight/common/pizza/PizzaToppingMappings.java) — lettura dei datapack, validazione, cache immutabile, log
- [PizzaToppingCatalog.java](../../src/main/java/dev/italiansdelight/common/pizza/PizzaToppingCatalog.java) — 31 identità, 33 ID ufficiali, lista degli item noti esclusi
- [ItaliansDelight.java](../../src/main/java/dev/italiansdelight/ItaliansDelight.java) — registrazione listener all'avvio

## 1. Build e avvio

In PowerShell, nella cartella del repository:

```powershell
git switch feature/pizza-1.0.0
git pull
java -version
.\gradlew.bat clean build
```

Usare **Java 25** per Minecraft 26.2. In seguito avviare un mondo di prova o un server dedicato con Italian's Delight e Farmer's Delight Refabricated.

**Esito atteso al caricamento dei server datapack, senza estensioni opzionali:** nel log compare una riga simile a:

```text
[Pizza] Loaded 33 topping item mappings (33 native expected, 0 optional; 0 accepted files, 0 invalid/blocked, 0 absent optional IDs, 0 conflicting external IDs, 0 missing native IDs)
```

Se il registro di 31 tipi è inizializzato, ma questa riga non appare, verificare registrazione del listener e caricamento dei datapack. In gioco eseguire `/reload`: la stessa riga deve comparire **una seconda volta**, senza crash.

## 2. Struttura dei datapack

Un mapping occupa **un file JSON**, ad esempio:

```text
world/datapacks/my-pizza-compat/
├── pack.mcmeta
└── data/
    └── my_pizza_compat/
        └── italiansdelight/
            └── pizza_equivalences/
                └── optional_mozzarella.json
```

**Attenzione:** `pack.mcmeta` deve dichiarare un formato datapack valido per la versione Minecraft in uso. Non usare un valore copiato da vecchi esempi senza verificarlo.

Il contenuto dell'esempio, **SOLO ILLUSTRATIVO**, è:

```json
{
  "item": "examplemod:mozzarella_slices",
  "canonical": "italiansdelight:pizza_toppings/mozzarella_slice"
}
```

`examplemod:mozzarella_slices` **non è un item verificato né un'integrazione ufficiale**. Sostituirlo con un ID esterno realmente esistente e **verificato come mozzarella a fette** prima di considerarlo un mapping di compatibilità valido.

Con la mod `examplemod` assente, il loader deve **ignorare** il mapping senza crash (conteggio degli ID opzionali assenti maggiore di zero). Non viene creato alcun item.

Il percorso è `data/<namespace>/italiansdelight/pizza_equivalences/<file>.json`; il namespace del percorso può appartenere al datapack ed è distinto da quello dell'item JSON.

## 3. Test negativi

Mettere uno alla volta i JSON in un **datapack di prova** e ricaricare con `/reload`. **Non inserire questi esempi nella cartella delle risorse ufficiali della mod.**

| Scenario | JSON di prova | Risultato richiesto |
| --- | --- | --- |
| Override nativo | `{"item":"farmersdelight:bacon","canonical":"italiansdelight:pizza_toppings/salame_slice"}` | Rifiutato; bacon resta canonico bacon |
| Item esplicitamente escluso | `{"item":"minecraft:beef","canonical":"italiansdelight:pizza_toppings/bacon"}` | Rifiutato; bistecca intera resta esclusa |
| Identità canonica sconosciuta | `{"item":"minecraft:paper","canonical":"italiansdelight:pizza_toppings/inesistente"}` | Rifiutato con avviso |
| Identità pianificata inattiva | `{"item":"minecraft:paper","canonical":"italiansdelight:pizza_toppings/carrot_slice"}` | Rifiutato fino all'implementazione delle nuove fette |
| Campo extra non ammesso | `{"item":"minecraft:paper","canonical":"italiansdelight:pizza_toppings/basil","priority":10}` | Rifiutato per formato scorretto |
| Tag al posto di item | `{"tag":"c:foods/cheese","canonical":"italiansdelight:pizza_toppings/mozzarella_slice"}` | Rifiutato; nessuna espansione dei tag |

Questi test usano `minecraft:paper` **solo per verificare la validazione**. Non lo si sta approvando come topping. Rimuovere tutti i test dopo la verifica.

### Conflitto fra due file

Per verificare la gestione dei conflitti **senza introdurre una compatibilità reale con ingredienti non adatti**, usare due file distinti che tentano di assegnare `minecraft:paper` a due identità diverse:

`file_a.json`:

```json
{
  "item": "minecraft:paper",
  "canonical": "italiansdelight:pizza_toppings/basil"
}
```

`file_b.json`:

```json
{
  "item": "minecraft:paper",
  "canonical": "italiansdelight:pizza_toppings/mozzarella_slice"
}
```

Risultato atteso: il mapping ambiguo di **paper** è escluso, gli altri mapping validi restano disponibili e compare un warning. **Non lasciare questi file installati dopo il test.**

Ripetere, se possibile, con due versioni dello **stesso percorso risorsa** distribuite in due datapack: il loader legge gli stack per identificare le collisioni tra pack diversi anche se uno ha precedenza per le risorse vanilla.

## 4. Controlli di stabilità

1. `/reload` ripetuto non deve duplicare le voci o cambiare i mapping nativi.
2. Un JSON malformato non deve impedire il funzionamento degli altri mapping validi.
3. Un ID di una mod non installata non deve obbligare all'installazione di quella mod.
4. Nessuna modifica visibile alla pizza deve comparire in questa fase: il prototipo mozzarella resta indipendente dal nuovo loader.
5. Nessun handler per tick deve essere richiesto per aggiornare la cache.
6. Il numero di voci native senza estensioni attive deve restare **33**; le due identità pianificate non aggiungono item inesistenti.

**Limite del test attuale:** il loader non è ancora utilizzato dai clic della pizza, quindi una compatibilità JSON potrà essere verificata inizialmente solo tramite log e lookup interno, non condendo fisicamente le pizze. I test di applicazione, rimozione e rimborso arriveranno con il sistema persistente dei topping per quarti.
