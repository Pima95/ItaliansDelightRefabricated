# Guida permanente al porting NeoForge — Minecraft 1.21.1

Piano approvato il 26/11/2026. Branch esclusivo `neoforge/mc-1.21.1`, derivato dal commit main `dd0d7cef35a6d74a3db9448c07172ea57767f64a`. Stato iniziale: **porting non compilato e non collaudato**. Aggiornare questo documento con commit, esiti, problemi e decisioni ad ogni fase; non spuntare test non eseguiti.

## 1. Regole vincolanti
- Non modificare main né i branch Fabric; nessun merge automatico, tag, release o pubblicazione.
- Porting nativo NeoForge, senza Fabric API, Loom, Sinytra Connector o dipendenze Fabric.
- Preservare mod ID `italiansdelight`, package `dev.italiansdelight`, ID dei contenuti supportati, meccaniche, bilanciamento, ricette, texture approvate e traduzioni. Conservare Apache-2.0, NOTICE, crediti e licenze di terze parti.
- Eccezione esplicita: eliminare contenuti che dipendono da blocchi vanilla assenti nella 1.21.1, non simulare i blocchi mancanti. Non rimuovere funzionalità per far compilare il progetto.
- Nome visualizzato proposto Italian's Delight; versione proposta 1.2.0-mc1.21.1-neoforge, da definire prima di release: il main contiene anche contenuti in sviluppo successivi alla 1.2.0. Conservare tutti i contenuti effettivamente presenti nello snapshot, non solo quelli rilasciati.
- Nessuna garanzia di apertura di mondi salvati con Minecraft 26.3 in 1.21.1.

## 2. Toolchain e dipendenze
Minecraft 1.21.1; NeoForge 21.1.x; Java 21 LTS; ModDevGradle, Gradle wrapper compatibile, Mojang mappings. Sostituire fabric.mod.json con META-INF/neoforge.mods.toml e configurare client/server, risorse, mixin, JAR e source JAR, includendo tutte le attribuzioni. Riferimento: https://github.com/NeoForgeMDKs/MDK-1.21.1-ModDevGradle e https://docs.neoforged.net/docs/1.21.1/.

| Dipendenza | Strategia |
| --- | --- |
| Farmer's Delight originale (Vectorwing) | Obbligatoria; candidato 1.21.1-1.3.4, Modrinth XTVZDOol; verificare API/artefatto. Mod ID farmersdelight. |
| JEI | Opzionale; candidato 19.51.0.418 per NeoForge 1.21.1; common-api + neoforge-api compileOnly, implementazione runtime solo per sviluppo/test. |
| Serene Seasons | Opzionale; candidato 10.1.0.3; controllare API, tag, GlitchCore transitiva (candidato 2.1.0.2) e compatibilità esatta. |
| Thirst Was Taken | Opzionale; verificare release NeoForge 1.21.1 e API/tag del JAR, compatibilità FD, eventuali dipendenze. |
| Thirst Was Taken 2 | Opzionale; verificare release NeoForge 1.21.1 e schema drink datapack specifico, non riusare acriticamente 26.3. |
| Tough As Nails | Opzionale; verificare versione NeoForge 1.21.1 e tag effettivamente letti da quella versione, eventuale GlitchCore. |
| Dehydration | Opzionale soltanto se esiste distribuzione nativa 1.21.1 NeoForge; altrimenti documentare non disponibile e non installare Fabric. |
| Create / Create Crafts & Additions | Opzionali; controllare plant_foods/plants, fluid capabilities, versioni e dipendenze effettive. |
| Fabric Loader, Fabric API, Farmer's Delight Refabricated | Eliminare dalla configurazione NeoForge. |

Versioni candidate non equivalgono a compatibilità verificata. Fissare coordinate/versioni esatte e range metadata solo dopo controllo di artefatti, loader, MC, dipendenze transitive e test. Nessuna mod della sete obbligatoria. Testare ciascuna separatamente e non sovrapporre sistemi della sete senza supporto esplicito.

### Metodo di verifica upstream
Usare il codice di Farmer's Delight originale, branch 1.21, e il tag/commit corrispondente al JAR scelto: https://github.com/vectorwing/FarmersDelight/tree/1.21. Verificare metadata, build, Cooking Pot, Cutting Board, ItemUtils, CommonTags, FeastBlock, Rope, colture, compostaggio, ricette, tag, rendering e classi di integrazione. Primo inventario upstream (dd8b9b0bfec916a2da88a6d64e3a37f4a02c3259): Gradle usa API JEI common e neoforge compileOnly; risorse generate per Serene Seasons e createaddition; nessun percorso esplicito thirst/hydration/dehydration/toughasnails rilevato nell'albero. Questo non prova assenza di supporto: controllare anche i sorgenti delle mod della sete che possono implementare la compatibilità dal proprio lato.
Risorse IDR attuali: data/dehydration/tags/item/*, data/toughasnails/tags/item/{hydration,thirst}/*, data/italiansdelight/hydration_items/italiansdelight_drinks.json, data/italiansdelight/thirstwastaken2/drinks/italiansdelight_beverages.json, data/sereneseasons e data/createaddition. Verificare schema, namespace, unità, valori di idratazione, consumo singolo, effetti e contenitori restituiti; confrontare bevande e cibi originali FD. Non duplicare gli effetti se una mod registra già la bevanda. Registrare esito e motivazione per ogni integrazione, anche se indisponibile. Riferimenti: https://modrinth.com/mod/farmers-delight/version/1.21.1-1.3.4 ; https://modrinth.com/mod/thirst-was-taken ; https://github.com/whoisnighter/ThirstWasTaken2.

## 3. Inventario e contenuti vanilla incompatibili
Snapshot iniziale: 2884 file, 187 Java, 2013 JSON, 283 file sotto recipe, 380 definizioni item moderne. Inventariare ogni file Java e tutte le risorse; distinguere verificato, adattato, escluso con motivazione, testato.
- Eliminare pioppo (poplar) e quercia pallida (pale_oak) nel solo branch NeoForge, compresi barili, brewing barrel, cheese aging rack, crop stake, crushing tub e qualsiasi altra variante. 66 file con questi nomi nel percorso nello snapshot iniziale; non sono l'intero insieme di riferimenti.
- Eliminare registrazioni, costanti, tabelle/array, item creativi, ticker, riferimenti block entity, ricette, loot, tag, asset, traduzioni e test dedicati; ripulire i riferimenti condivisi senza cancellare i contenuti supportati.
- Verificare tutto il catalogo vanilla 1.21.1: pale garden, resin, creaking, ingredienti, effetti, suoni, entità, componenti, attributi, biomi, texture, classi introdotte successivamente. Non eliminare gli alberi originali IDR (ulivo, mandorlo, ciliegio da frutto). Documentare ulteriori esclusioni motivate.
- Controllare altre differenze FD originale / FDR: ingredienti e blocchi assenti, tag e ricette; sostituire solo con equivalenti reali senza alterare meccaniche approvate.

## 4. Conversione Java
- Entry point @Mod, mod event bus e game bus, setup client isolato (nessuna classe client sul server); DeferredRegister e ordine di inizializzazione, blocchi/item/menu/entità/fluidi/particelle/componenti/ricette/worldgen.
- Rivedere ogni classe, firme e API vanilla; ResourceLocation, registri, Holder, Data Components, alimenti/consumo, NBT e salvataggi, block entity, ticker, menu, slot, sincronizzazione, payload networking; conversione eventi Fabric (interazioni, ticking, compostaggio, collocamento).
- FluidTransferBridge, FabricFluidItemContainers, CompatibleFluidTank, catalogo fluidi: IFluidHandler/IFluidHandlerItem, FluidStack, FluidTank, RegisterCapabilitiesEvent, FluidType/estensioni client; convertire unità Fabric in mB senza perdita o duplicazione; simulazione, riempimento/svuotamento, compatibilità tubi, contenitori restituiti.
- Worldgen: BiomeModifiers NeoForge, configured/placed features, codec di trunk/foliage placers, biomi, distribuzioni, stagioni, viti selvatiche.
- Client: GUI e renderer per pizze e fluidi, tint foglie per bioma/stagione, modelli dinamici, overlay quarti, pala, cartelli, barche, particelle, layer e registrazione; API 1.21.1 non quelle 26.3.
- Mixin: esaminare LevelMixin, PizzaCuttingBoardMixin, PizzaVanillaFurnaceMixin, SheepMixin, CheeseHookParticlesMixin. Verificare target e firme nel JAR 1.21.1; usare eventi quando equivalenti, nessun mixin rotto o silenziato. Per cutting board verificare processStoredItemUsingTool e il comportamento upstream, consumo, drop, statistiche.
- JEI: plugin opzionale, API 19, categorie/ingredienti/subtype/catalyst/GUI e fluidi; avvio senza JEI. Serene Seasons: bridge opzionale, controllo presenza prima di caricare classi, stagioni e tag.

## 5. Risorse e dati
Convertire tutte le definizioni assets/italiansdelight/items (introdotte dopo 1.21.1) nei modelli models/item e sistemi dinamici compatibili; non perdere varianti o texture. Verificare blockstates, models, tint, atlas, pack.mcmeta, particle JSON, traduzioni, GUI. Verificare tutte le ricette (vanilla, Cooking Pot, Cutting Board, custom codec), ingredienti, result, remainder, tag, loot table, advancements, worldgen e dati di compatibilità; mantenere cartelle singolari già adottate in 1.21.1. Conservare texture PNG salvo adattamento tecnico indispensabile.

## 6. Checklist funzionale
- [ ] Pizze, quattro quarti indipendenti, condimenti, spicchi, pala, forno, taglio, cottura e ricomposizione.
- [ ] Cheese Vat, formaggi, grattugia, rastrelliere, uncini, salumi e stagionatura.
- [ ] Sale, calderoni, barili, Crushing Tub, fluidi, secchi e bottiglie.
- [ ] Ulivi, mandorli, ciliegi da frutto, legni, barche, cartelli e derivati supportati.
- [ ] Basilico, cardo, viti, grappoli, pali e corde, peperoncini; crescita/raccolta e worldgen.
- [ ] Brewing Barrel, fermentazione, vini, succhi, aceti, altre bevande, effetti.
- [ ] Pasta, risotti, secondi, antipasti, taglieri e ogni contenuto effettivo del main; nessun tagliere/cibo scartato per convenienza.
- [ ] Cooking Pot, Cutting Board, JEI, Serene Seasons, automazione e sete.

## 7. Fasi e criteri di accettazione
1. [x] Creare branch da main e salvare guida stabile.
2. [ ] Inventario completo, upstream, matrice versioni e contenuti assenti; rimozione legni incompatibili.
3. [ ] Toolchain Gradle, metadata, dipendenze e CI dedicata Java 21; non attivare pubblicazione.
4. [ ] Registrazioni e codice comune; avanzate, fluidi, networking, worldgen.
5. [ ] Client e rendering.
6. [ ] Risorse e integrazioni, revisione integrale Java/JSON.
7. [ ] Build pulito, correggere errori e warning rilevanti; verificare JAR, licenze, nessun riferimento Fabric residuo in runtime.
8. [ ] Avvio client e server dedicato; nuovo mondo, caricamento ricette e loot/tag senza errori, modelli senza missing texture; testare ogni crafting, GUI, liquidi, generazione, crescita, salvataggio/riapertura, multiplayer.
9. [ ] Test con sola dipendenza obbligatoria, JEI presente/assente, Serene Seasons presente/assente, ogni mod della sete singolarmente, automazione e combinazioni compatibili; verificare log e crash.
10. [ ] README e changelog NeoForge, report test con versione JAR e commit, documentazione esclusioni e problemi residui. Release solo dopo approvazione.

Usare commit piccoli e coerenti. CI e test automatici quando possibile, test manuali esplicitamente registrati; build verde non significa porting funzionante. Non promettere retrocompatibilità dei mondi 26.3.

## 8. Registro
- 26/11/2026: branch creato; inventario preliminare del main e upstream FD 1.21, guida approvata. Tutte le verifiche di compilazione/esecuzione ancora pendenti.

### Prima ricognizione operativa (26/11/2026)
- [x] Lettura mirata di ModBlocks, ModItems, ModBlockEntities, ModItemGroups, ModSigns, ModBoats, ItaliansDelightClient.
- ModBlocks: dieci registrazioni incompatibili (5 per legno: crop stake, brewing barrel, crushing tub, barrel, cheese aging rack); riferimenti diretti Blocks.PALE_OAK_PLANKS e Blocks.POPLAR_PLANKS, registrazioni flammability. ModBlockEntities: 8 riferimenti a varianti incompatibili. ModItemGroups: 10 riferimenti nella creative tab. Nessun match testuale pale_oak/poplar nelle altre quattro classi lette. Ricerca estesa a tutte le classi e JSON ancora da completare prima della cancellazione atomica dei contenuti.
- Thirst Was Taken originale 2.1.5: artefatto NeoForge 1.21.1 e supporto dichiarato per Farmer's Delight: https://modrinth.com/mod/thirst-was-taken/version/1.21.1-2.1.5. Verificare loader hydration_items e valori nel sorgente/JAR.
- Thirst Was Taken 2: distribuzioni distinte NeoForge 1.21.1 e Fabric. La documentazione attuale dichiara supporto FD 1.21.1 su NeoForge; verificare release esatta e schema, perché le release precedenti avevano limitazioni. https://modrinth.com/mod/thirst-was-taken-2 e https://whoisnighter.github.io/ThirstWasTaken2/docs/installation.
- Tough As Nails: esistono release native NeoForge 1.21.1 (serie 10.1.0.x, esempio https://www.curseforge.com/minecraft/mc-mods/tough-as-nails/files/5766204); scegliere la versione dopo verifica sorgente e GlitchCore.
- Dehydration ufficiale 1.4.2 per 1.21.1 è Fabric: https://www.curseforge.com/minecraft/mc-mods/dehydration/files/9269540. Esiste un porting indipendente non ufficiale https://www.curseforge.com/minecraft/mc-mods/dehydration-unofficial, da valutare senza renderlo obbligatorio e senza dichiarare supporto prima di verificarne artefatto e schema.
- Letti i file attuali hydration_items e thirstwastaken2/drinks e due tag TAN (hydration/20 e thirst/2): intenzione di bilanciamento originale 2 punti bevande alcoliche, 4 latte ovino, 6 succhi; TW2 quenched 4 per succhi, 0 per latte e alcolici, olio esplicitamente zero. TAN ha due famiglie di tag, non presumere che entrambe siano supportate in 1.21.1. Preservare intenzione, adattare unità/schema in base al comportamento delle bevande FD e alle API di ciascuna mod.
- Stato: analisi iniziata, nessuna cancellazione o conversione Java eseguita; non dichiarare build o runtime funzionanti. Prima modifica funzionale: rimozione coerente di tutte le varianti non disponibili, dopo verifica riferimenti condivisi.
