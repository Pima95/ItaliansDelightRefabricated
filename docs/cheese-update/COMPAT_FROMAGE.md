# Italian's Delight — Compatibilità con Fromage

La compatibilità con **Fromage** deve essere opzionale: Fromage non è e non
deve diventare un requisito di Italian's Delight.

## Stato verificato

La versione pubblica di Fromage controllata durante questo sviluppo è la 1.3.6
per Minecraft 1.21.1 (Fabric/NeoForge). Italian's Delight è invece sviluppata
su Minecraft 26.2, quindi al momento non è possibile fare un test runtime
diretto delle due mod sulla stessa versione.

Dal changelog di Fromage 1.3.4 risulta che la mod ha introdotto numerosi
**common item tags** e tag propri per latte, formaggi, cagliata, strumenti,
ingredienti, stili e usi culinari.

Il Fabric common-tag catalog espone sia `c:foods/cheese` sia il tag storico
`c:cheese`. Italian's Delight pubblica quindi i propri prodotti caseari
utilizzabili dentro questi tag condivisi.

## Regola di integrazione

Non vengono aggiunti:

- dipendenze `depends` verso Fromage;
- dipendenze `suggests` necessarie al funzionamento;
- riferimenti Java a classi di Fromage;
- ID `fromage:*` inventati o non verificati.

Questo significa che, senza Fromage installata, non cambia nulla.

## Formaggi sovrapposti

Le due mod hanno almeno questi prodotti concettualmente sovrapposti:

- Mozzarella;
- Parmigiano Reggiano / Parmesan;
- Pecorino Romano / Pecorino;
- Ricotta.

Per ora l'interoperabilità sicura è garantita a livello di **formaggio
generico** tramite i common tag. Non viene invece forzata un'equivalenza
specifica tra, per esempio, una forma intera di Parmesan di Fromage e una
forma intera di Parmigiano Reggiano di Italian's Delight: le due mod hanno
quantità, porzioni e sistemi di maturazione differenti.

Quando Fromage pubblicherà una build compatibile con Minecraft 26.2, andranno
ispezionati i suoi tag specifici effettivi. Se esistono tag comuni per varietà
come mozzarella/parmesan/pecorino/ricotta, Italian's Delight dovrà aderire a
quegli stessi tag senza aggiungere una dipendenza.

## Tag Italian's Delight

File principale:

`src/main/resources/data/c/tags/item/foods/cheese.json`

Alias storico:

`src/main/resources/data/c/tags/item/cheese.json`

Il tag contiene unità di formaggio utilizzabili come ingredienti/porzioni. Le
forme fresche intermedie e le grandi forme non direttamente consumabili sono
escluse intenzionalmente per evitare che una ricetta generica da “1 formaggio”
consumi una forma intera dal valore molto superiore.

Sono già presenti inoltre altri common tag utili alla compatibilità casearia,
come `#c:drinks/milk`, `#c:foods/curd` e `#c:rennet`.
