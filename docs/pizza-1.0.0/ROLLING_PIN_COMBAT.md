# Mattarello: statistiche d'attacco da pala di legno — Pizza 1.0.0

**Decisione dell'autore:** 21 ottobre 2026.  
**Branch:** `feature/pizza-1.0.0`  
**Stato:** codice implementato su GitHub, build automatica e collaudo in gioco da verificare.

## Richiesta e scelta implementativa

Il mattarello (`italiansdelight:rolling_pin`) deve avere **le stesse statistiche d'attacco della pala di legno vanilla** per renderlo utilizzabile anche in combattimento. L'intervento riguarda **esclusivamente i modificatori d'attacco** (danno e velocità), non il mining, non l'animazione né la logica pizza.

Nella registrazione `ModItems.ROLLING_PIN` vengono applicati esattamente gli attributi predefiniti di `Items.WOODEN_SHOVEL`, copiando `DataComponents.ATTRIBUTE_MODIFIERS` dallo stack vanilla:

```java
new Item.Properties()
        .durability(64)
        .attributes(Items.WOODEN_SHOVEL.getDefaultInstance()
                .get(DataComponents.ATTRIBUTE_MODIFIERS))
```

**Perché copiare direttamente il componente:** garantisce la corrispondenza con la pala di legno effettivamente registrata da Minecraft 26.2, senza duplicare valori numerici né aggiungere al mattarello le funzioni minerarie di una pala.

## Comportamenti da preservare

- **64 punti di durabilità** originali, invariati. Nessuna modifica a costo, suoni, tempi o azioni del mattarello.
- Clic destro con mattarello su impasto non completamente steso: **stende la pizza in un solo clic** e consuma un solo punto in Survival, non in Creative.
- Mattarello su impasto già steso: **divisione/ricomposizione dei quarti**, senza consumo di durabilità.
- Nessuna modifica al sistema dei condimenti, alla pala per pizza, alle immagini, ai nomi o all'inventario Creativa.
- Gli attributi della pala vengono usati per i colpi, **non** per tagliare o scavare blocchi più efficacemente; il mattarello non è registrato come pala.

## Verifiche

- [ ] In Minecraft 26.2 confrontare i tooltip del mattarello e di **Pala di legno**, nella stessa condizione e nella stessa mano: **danno d'attacco e velocità d'attacco devono coincidere**.
- [ ] In Survival colpire un mob con entrambi gli item e verificare il danno e il tempo di recupero degli attacchi; verificare anche il comportamento di durabilità in combattimento, senza presumere che sia quello di una pala vanilla.
- [ ] Stendere una nuova pizza: l'oggetto passa da 64 a 63 punti di durabilità; divisione e ricomposizione successive non ne consumano altri.
- [ ] Stesse interazioni in Creative senza consumo.
- [ ] Verificare che il mattarello non scavi blocchi con efficacia da pala di legno e che le altre meccaniche pizza restino intatte.
- [ ] Compilazione Gradle Java 25 e test di regressione superati.

**Registro discussione:** l'autore ha chiesto la modifica mentre verificava in gioco la nuova grafica delle pizze; la modifica alle statistiche del mattarello è indipendente dalle texture ancora in approvazione. Ogni decisione, anche minima, viene registrata nel branch GitHub come richiesto.
