# Grattugia per Formaggi

Item: `italiansdelight:cheese_grater`, disponibile nel gruppo creativo della mod.
La ricetta si sblocca nel ricettario quando si raccoglie un lingotto di ferro.

Crafting su banco da lavoro, in una qualsiasi delle tre colonne:

```text
L  = lingotto di ferro
P  = pepita di ferro
P  = pepita di ferro
```

Produce una grattugia. Durabilità: **250**, come il coltello in ferro di Farmer's
Delight Refabricated. Non impilabile, riparabile con ferro.

Sul tagliere, usare la grattugia su uno spicchio di Parmigiano Reggiano o Pecorino
Romano: ogni azione consuma uno spicchio e un punto di durabilità e produce
**4 unità grattugiate**. Il coltello resta lo strumento per ricavare spicchi e
fette dalle forme; non produce più il grattugiato. La lavorazione usa il normale
sistema di ricette del Cutting Board ed è esposta alla sua integrazione JEI.

Verifica automatica del 30 settembre 2026: build riuscita e quattro GameTest
funzionali superati su server Minecraft isolato (oltre al test di base del
framework). Provati crafting nelle tre colonne e disposizioni errate,
durabilità uguale al coltello della mod installata, lavorazione reale sul
tagliere per entrambi gli spicchi, rifiuto del coltello, consumo di un punto per
azione, rottura all'ultimo uso e porzionatura delle forme ancora con coltello.
Controllata anche la pubblicazione della grattugia nell'elenco degli attrezzi
del tagliere sincronizzato ai client.

La configurazione corrente richiede Loom `1.18`, non risolto da Gradle durante
la verifica. Build e GameTest sono stati eseguiti usando il modulo già disponibile
`net.fabricmc:fabric-loom:1.17.21` tramite lo script temporaneo
`build/cheese-grater/smoke.gradle`, senza modificare `build.gradle`.
Il report è in `build/cheese-grater/results.xml` e il JAR pronto da provare in
`build/libs/italiansdelight-0.1.0.jar`.

Verifiche manuali:

- [ ] Crafting verticale e sblocco del ricettario con un lingotto di ferro.
- [ ] Texture leggibile in inventario e corretta impugnatura in prima/terza persona.
- [ ] Entrambi gli spicchi producono 4 grattugiati con la grattugia.
- [ ] Usando il coltello sullo spicchio, formaggio e attrezzo rimangono intatti.
- [ ] Barra di durabilità visibile dopo l'uso e rottura all'ultimo utilizzo.
- [ ] In JEI, le due ricette del grattugiato mostrano la grattugia come attrezzo.
