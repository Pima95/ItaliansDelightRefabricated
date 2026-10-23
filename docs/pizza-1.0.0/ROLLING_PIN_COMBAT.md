# Mattarello: statistiche d'attacco da pala di legno — Pizza 1.0.0

**Decisione dell'autore:** 21 ottobre 2026.  
**Branch:** `feature/pizza-1.0.0`  
**Stato:** crash di inizializzazione corretto sul branch; compilazione e nuovo avvio `runClient` da confermare.

## Richiesta e scelta implementativa

Il mattarello (`italiansdelight:rolling_pin`) deve avere **le stesse statistiche d'attacco della pala di legno vanilla** per renderlo utilizzabile anche in combattimento. L'intervento riguarda **esclusivamente i modificatori d'attacco** (danno e velocità), non il mining, non l'animazione né la logica pizza.

### Crash riprodotto dall'autore il 21 ottobre 2026 — corretto nel codice

L'autore ha inviato il log di `./gradlew.bat runClient`. Il gioco si bloccava durante l'inizializzazione (`Initializing game`): `ExceptionInInitializerError`, causato da **`NullPointerException: Components not bound yet`** su `ModItems.<clinit>(ModItems.java:126)`. La precedente implementazione chiamava `Items.WOODEN_SHOVEL.getDefaultInstance()` **durante la registrazione statica degli item**, prima che Minecraft avesse associato i componenti di default. **La build compilava regolarmente, ma il client crashava all'avvio.**

**Correzione:** nessun `ItemStack` vanilla viene creato o letto durante la registrazione. Si costruiscono direttamente i modificatori `ItemAttributeModifiers` secondo le baseline della pala di legno: bonus del materiale legno + **1.5F** al modificatore del danno, e **-3.0F** alla velocità d'attacco (MAINHAND). La pala vanilla non è istanziata, e non si aggiunge alcuna componente di mining.

```java
private static final ItemAttributeModifiers ROLLING_PIN_COMBAT_ATTRIBUTES =
        ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID,
                                1.5F + ToolMaterial.WOOD.attackDamageBonus(),
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(Item.BASE_ATTACK_SPEED_ID,
                                -3.0F,
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();

// Registrazione sicura: nessun getDefaultInstance()
new Item.Properties()
        .durability(64)
        .attributes(ROLLING_PIN_COMBAT_ATTRIBUTES);
```

**Motivazione:** le costanti e i parametri combattimento sono sicuri in fase di registrazione; invece uno stack completo `WOODEN_SHOVEL` non lo è, finché la fase di bootstrap dei componenti non è conclusa. La `tool`/`shovel` property non viene utilizzata, evitando effetti collaterali sullo scavo.

Il test `.github/scripts/check_rolling_pin_combat.py` verifica che non venga reintrodotta la chiamata prematura a `getDefaultInstance()`, che i due attributi abbiano baseline e slot corretti, che restino 64 utilizzi e che non venga aggiunto `.shovel(...)`. La workflow GitHub Actions esegue questo controllo **prima** di `gradlew clean build`. L'esito positivo della CI **non equivale** al collaudo di avvio tramite `runClient`, che richiede conferma dell'autore.

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
- [ ] **CRITICO:** nuovo `./gradlew.bat runClient` dopo il fix senza `NullPointerException: Components not bound yet` e senza `ExceptionInInitializerError`.

**Registro discussione:** l'autore ha chiesto la modifica mentre verificava in gioco la nuova grafica delle pizze; la modifica alle statistiche del mattarello è indipendente dalle texture ancora in approvazione. Ogni decisione, anche minima, viene registrata nel branch GitHub come richiesto.
