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

### Interventi effettivi — primo lotto di conversione
- Commit 2bdfbbf712d5d1297c5c677b6a4c0fa62f19fe3a: eliminate 66 risorse dedicate a poplar/pale_oak; adattati 16 file condivisi (5 classi Java, tag, lingue, script). Restano 14 varianti di legno; audit statico dell'intero src/main per riferimenti ai due legni superato nella prima CI.
- Commit a3bd48f5f14209628bac9de3f1ad3a8b92ffa9d8: toolchain nativa MDG 2.0.148, NeoForge 21.1.267, Java 21, Gradle 9.2.1, metadata TOML e CI. FD 1.3.4 (CF 8765184), JEI 19.51.0.418 API compileOnly e runtime opt-in, Serene Seasons 10.1.0.3 (CF 6182596) compileOnly. Upstream FD 1.21 richiede NeoForge >=21.1.219 e FML >=4; scelto il range [21.1.219,21.2). Versione mod ancora provvisoria, nessuna release.
- Prima CI https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/45869485842: audit statico legni/JSON PASS; build FAIL dopo risoluzione dipendenze e avvio compileJava (import Fabric residui, tipi di rendering post-1.21.1, BlockEntity e API vanilla). Il primo contatore import Fabric era errato a causa di escaping della regex: corretto nel presente aggiornamento. Non usare il valore zero del primo report.
- Commit 00ea6a3, 537cac4, c165772, b793185: sostituzione Identifier -> ResourceLocation in 38 file Java (client, blocchi, pizze, registri e JEI). Non equivale a completamento delle API dei singoli file.
- Commit 6e067e3, 0e033f2: creati 21 modelli inventory 1.21.1 per blocchi senza models/item, con parent ricavato dalle definizioni moderne e verificato quando locale. Tutti i 370 item moderni rimasti hanno ora un corrispondente file models/item. Le definizioni moderne restano temporaneamente come riferimento per la conversione: i modelli condizionali come pizza_peel devono essere portati con API 1.21.1 prima della rimozione definitiva. Nessuna prova grafica.
- Commit 66cb088: sostituito FabricLoader con ModList per presenza Serene Seasons; backport compostabilità a ComposterBlock.COMPOSTABLES per 29 voci (0.30/0.65/0.85). Collegare al common setup dopo la conversione dei registri; API stagionali e avvio con/senza mod ancora da verificare.
- Prossimo blocco: convertire entrypoint, ordine dei registri, eventi comuni, storage/NBT, networking e fluidi; poi renderer e schemi dati. Il porting non compila ancora, nessun test client/server effettuato. Mod della sete ancora da adattare e verificare una per una contro le rispettive API NeoForge 1.21.1 e la compatibilità FD.


### Secondo lotto di conversione — registri, NBT, eventi e fluidi
- 6225390: introdotto PortNbt (CompoundTag/HolderLookup.Provider) e adattate sei block entity: impasto e pizza cotta, formaggio stagionato, uncino, rastrelliera, Crushing Tub. Conservati i dati di composizione e sincronizzazione tramite pacchetto vanilla; test dei salvataggi ancora da fare.
- a65f579: migrazione SavedData e eventi chunk/tick per evaporazione sale; persistenza e tempi da collaudare.
- 5a899e5: eliminate le tre mensole vanilla dei legni IDR (ulivo, mandorlo, ciliegio da frutto), perché ShelfBlock non esiste in 1.21.1. Rimosse 41 risorse dedicate e modificati 6 file condivisi; non confondere mensole con rastrelliere formaggi o cabinet FD, mantenuti.
- 1b808c4: adattati serializer, ingredienti e risultati delle ricette Cheese Vat, stagionatura ed essiccazione e relativi consumatori/prime categorie JEI.
- 8631911, c8f9e1a: FluidTank, FluidStack, mB, capability IFluidHandler/IFluidHandlerItem, eventi interazione e entrypoint @Mod. Bottiglie 250 mB, secchi 1000 mB; conservare componenti fluidi, qualità vino e restituzione dei contenitori. Rendering, automazione e trasferimenti effettivi da testare.
- 37bf7a8: registrazione 173 blocchi, 11 tipi BE, BlockItem e 3 sementi via RegisterEvent; BlockEntityTypeAddBlocksEvent per cartelli/cabinet FD; adattati tipi legno, stripping e flammability. Non è confermato il corretto avvio.
- 2316e69: componenti dati (4), menu (3), particelle, ricette (9 registrazioni), cartelli (6), trunk/foliage placers (8) spostati negli eventi registri.
- 2bd85f5: registrazione 19 fluidi e 17 FluidType, condivisione tipo source/flowing di olio e succo di mela; mai generare nuove sorgenti. Estensioni client/texture e correttezza dei tipi da verificare.
- cb31472: eventi click (impasto/pala, corde, aratura), caricamento chunk e tick (migrazioni), onLoad per sincronizzazione divisione pizza, AddReloadListenerEvent per equivalenze, tab creativa nativa. Meccaniche originali preservate nel codice, non ancora testate.
- CI conclusa sul commit 37bf7a8: audit PASS, compile FAIL (https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/45871372402), compilatore mostra 100 errori prima del limite di output; non significa che siano tutti gli errori. CI sul commit cb31472 avviata/pending al momento della verifica (https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/45871767551). Nessun build verde né prova client/server.
- Restano da portare in particolare ModItems/consumabili originali FD, ModBoats, altre BE/menu, ricette pizze/furnace, blocchi/colture, mixin, worldgen (configured_feature, struttura config, nomi state provider e alternativa nativa a in_order FDR), renderer/client e JEI, Serene Seasons, risorse/ricette/tag e singole mod della sete. Verificare anche la correttezza delle conversioni già committate e l'ordine registri: nessuna fase funzionale è spuntata prima di una prova reale.
- Non fornire istruzioni di checkout per la prova utente finché clean build non termina con successo. Nessuna pubblicazione o modifica main.


### Terzo lotto — worldgen, macchine, ricette, alimenti e rendering
- da2dfc4: sostituita BiomeModifications Fabric con 10 modificatori NeoForge; WildGrapeFeature usa Feature<NoneFeatureConfiguration>, nuova InOrderFeature locale per preservare le tre collocazioni (vite, terra brulla, arboscello secco) senza richiedere FDR.
- 4116f31: trasferite 11 configurazioni da worldgen/feature a configured_feature, con struttura type/config e state provider 1.21.1. f7ad338: adattate 10 placed_feature, conservati rarity/count e liste biomi. Le sostituzioni di substrate_overworld con minecraft:dirt e below_trunk_provider con dirt_provider/force_dirt=false, nonché i parametri minimi degli alberi, vanno confrontate in gioco con le regole originali; nessuna prova di generazione effettuata.
- 8ba5eef: CompoundTag/HolderLookup e ContainerHelper 1.21.1 per Pizza Oven, Brewing Barrel e Cheese Vat; conservati inventari, qualità del vino, progressi, whey e pending batch; forno usa getBurnTime(RecipeType.SMELTING), identificatore della ricetta del Cheese Vat torna ResourceLocation. Restano da portare i callback di rimozione e controllare il rilascio degli inventari/esperienza.
- 1a75605: serializer delle ricette PizzaSmelting/PizzaSmoking/PizzaCrafting convertiti a SimpleCookingSerializer e SimpleCraftingRecipeSerializer; risultato dinamico e restituzione ciotole preservati nel sorgente, mixin furnace/cutting board ancora da verificare.
- 3f63b61: 134 item assegnati nel registro ITEM, tier 1.21.1, FoodProperties con nourishment FD (durate SHORT/MEDIUM/LONG), DrinkableItem e MilkBottleItem FD, MilkBucketItem vanilla, consumo e tooltip del vino; fette pizza con gli stessi parametri nutritivi e nourishment condizionale. Nessuna prova di consumo o avvio.
- a14dcfd: predicati ItemProperties per 23 varianti non-base ciascuna di raw_pizza, cooked_pizza e pizza_slice (69 riferimenti a modelli esistenti); Margherita e mista mantengono modello base. Rimosso l'uso del componente ITEM_MODEL moderno in PizzaDisplayNames, ripristinata riparabilità della grattugia con il materiale del tier ferro. Pala carica e overlay dinamico ancora da portare, nessuna prova grafica.
- 4171ac9: PizzaToppingVisuals, LiquidSurfaceVisuals e cinque renderer BE convertiti a MultiBufferSource/PoseStack; campionamento dell'atlas e tint 1.21.1, item del Crushing Tub attraverso ItemRenderer; registrazione client, overlay divisioni, estensioni FluidType e pala ancora da convertire. Le coordinate e PNG esistenti sono mantenute. Rivedere prestazioni e comportamento effettivo, nessuna prova di rendering.
- c9118a7: esteso limite javac da 100 a 2000 errori per rendere disponibile una diagnosi più completa, senza saltare file, mascherare errori o disabilitare controlli.
- CI sull'ultimo commit della sessione precedente 11ddbc8 (run 45871824018): audit PASS, compilazione FAIL. Al momento del controllo, run 45890591811 pending e run 45890494060 in corso, mentre diverse esecuzioni intermedie sono state annullate automaticamente dal workflow concorrente. Non attribuire successo a questi commit.
- Prossimo lavoro: entrypoint client e registrazioni fluidi/render/schermate/particelle; barche (conservare tutte le varianti e ID), pala carica e overlay; firme blocchi e colture, menu/GUI, restanti mixin e API, JEI/Serene Seasons, revisione ricette/loot/tag e mod opzionali della sete. Solo dopo un clean build riuscito fornire i comandi Git per il collaudo locale.
- Verificato che main resta dd0d7cef35a6d74a3db9448c07172ea57767f64a. Nessuna release, nessun merge.


### Quarto lotto — client, colture, JEI, GUI, alberi e API residue
- Baseline CI a452ae7, run 45890652229: audit PASS, compilazione FAIL, 552 diagnostici javac distinti (il log ripete la lista); non sono 1104 errori indipendenti.
- bb3cb48: registrazioni client native per cinque BE renderer, tre menu/schermate, particella, 17 FluidType con sprite, colori foglie e sei barche. Le barche mantengono gli ID e le PNG, usano entità dedicate e item che le posizionano con la logica di collisione vanilla. Verificare anche la variante legno e i drop da caduta (la variante vanilla sottostante è per ora oak), dimensioni, multiplayer, baule e reimbarco.
- 8d31cf5: overlay di divisione pizza attraverso RenderLevelStageEvent, pala con BakedModelWrapper/BEWLR e rendering reale dell'ItemStack caricato nelle sole pose in mano; coordinate derivate dalle geometrie esistenti, da verificare visivamente.
- f47b439: 6 classi di coltura/foglie (olivo, mandorlo, ciliegio, vite, vite selvatica, peperoncino sospeso); LeavesBlock 1.21.1 e firme fertilizzazione/neighbor; suoli DIRT/SAND conformi al WildCropBlock originale FD; ripristino corde adattato al modello HangingTomatoBlock FD, da verificare in particolare i drop e i callback di rimozione. Il nuovo superclass LeavesBlock non riproduce automaticamente eventuali particelle ambientali più recenti.
- a0339b5, d6322cb: 16 classi blocchi con firme updateShape, fertilizzazione e ponte ItemInteractionResult; conservare comportamento PASS/FAIL/SUCCESS e controllare il fallback all'interazione senza item.
- 5c29813, 0281c3a: nove file tipi ricetta JEI e due raccoglitori, dieci categorie; passaggio a JEI 19 RecipeType/GuiGraphics, recipe manager client, millibucket (1000/250), stack fluidi e fuel list. Riferimenti a codice upstream mezz/JustEnoughItems branch 1.21.1. Rivedere plugin principale, widget, slot, ingredienti, tooltip e categorie rispetto all'API esatta; non verificato a runtime.
- f785889: tre schermate e WheyTankRenderer adattati a GuiGraphics/renderBg/renderLabels/renderTooltip e mouseClicked 1.21.1; mantenute coordinate e texture, da testare.
- 5169e63: otto trunk/foliage placers convertiti a TreeConfiguration e LevelSimulatedReader; guard WorldGenLevel e setDirtAt. Geometria e parametri preservati nel sorgente, da verificare con sapling e worldgen.
- 85b4e72: firme cartelli, inventari dei tre menu (27+9), nomi effetti e registry get, drop refund e ModList.isLoaded.
- 1aeacd0: Serene Seasons 1.21.1 usesTropicalSeasons + dimension whitelist + BLACKLISTED_BIOMES, in accordo con SeasonHandler/SeasonHooks upstream; greenhouse_glass entro 16 blocchi, preservato. Dipendenza opzionale, test con e senza.
- 92d068c: cauldron/forno/barile ItemInteractionResult, codec BaseEntityBlock, rimozione forno e Cheese Vat con rilascio inventario ed esperienza e correzione nome noCollission. Verificare che preview/pending container non siano duplicati e che il loot preservi correttamente i dati.
- Tutti i commit sono sul branch neoforge/mc-1.21.1. Le compilazioni intermedie sono state annullate dalla concorrenza della CI; ancora nessuna build verde confermata. Prossimo step: esaminare la prima CI conclusa del lotto, correggere incompatibilità residue senza escludere sorgenti, completare i mixin, plugin JEI, registri/lifecycle, compatibilità thirst e risorse/datapack; testare client/server. Non fornire comandi checkout fino a clean build verificato.


### Quinto lotto — riduzione errori Java e conversione delle risorse
- CI d97fd6c, run 45892471672: audit PASS, compilazione FAIL con 32 errori distinti rispetto ai 552 del lotto precedente (−94,2%).
- 6b8f70e: codec dei sei BaseEntityBlock, firme pick-block, rimozione hook, fertilizzazione chili, sound event e builder entità barche. Restano da verificare i drop da caduta delle barche e la pulizia delle celle riservate del gancio.
- 2591bbb: Sheep import, 10 registrazioni catalyst JEI, lookup ingredienti pizza e GlitchCore NeoForge 1.21.1-2.1.0.2 (CurseForge 955399:8109792) come compileOnly; opzione -Pwith_serene_seasons=true per runtime di test insieme a Serene Seasons. Nessuna nuova dipendenza obbligatoria per il giocatore.
- 69d62cb: wrapper burn del forno conforme al descriptor del patch NeoForge 1.21.1 (ritorno booleano, RegistryAccess, RecipeHolder, inventario, max stack, furnace); restituzione contenitori solo a cottura riuscita. Mixin particelle CheeseHook trasferito a ParticleEngine.destroy/crack. Verificato il metodo processStoredItemUsingTool del CuttingBoard upstream FD. Ancora necessario collaudo dei mixin a runtime.
- f15f376 e commit successivo: normalizzazione nel processResources Gradle di ingredienti delle ricette (stringhe item/tag, alternative neoforge:compound) e loot table (conditions/functions, match_block e neoforge:can_item_perform_ability), sulla base degli schemi upstream 1.21.1. I JSON sorgente conservano la sintassi originale per facilitare il confronto con main; il JAR e il dev runtime usano i JSON convertiti. Non cambia deliberatamente alcun ID, risultato, quantità, probabilità o proprietà. La conversione non è un validatore completo di codec; verificare con un avvio reale e controllare il log del reload dei datapack.
- Non considerare una compilazione verde equivalente a un port collaudato. Necessari test di avvio, registri, ricette, modelli, loot e mod opzionali; nessun merge/release.

- CI f15f376, run 45899131278: **clean build SUCCESS**, Java compile/processResources (270 recipes)/jar/sourcesJar all completed in 1m 2s. The separate metadata verification failed because it incorrectly asserted classfile major 61 (Java 17) instead of 65 (Java 21). Corrected the verification to require major 65, not to bypass it. The additional loot table normalization is newer than the successful build and must be validated on the latest head. First local compile test is now appropriate using the known successful f15f376 commit or the branch once the corrected CI is green; runtime gameplay remains unverified.


### Sesto lotto — Windows e test di avvio
- CI 958bb6e, run 45899391562, audit + clean build + verifica JAR Java 21: SUCCESS.
- Log locale Windows: `Unable to delete directory build/generated/sources/annotationProcessor/java/main`, dopo completamento createMinecraftArtifacts. Non è un errore javac; probabile handle aperto da IDE/daemon/scanner o permesso. Non è possibile identificare il processo dal solo log.
- Su Windows Gradle usa ora `build-neoforge/` per separare i prodotti del porting dai residui Fabric. Opzione `-PportBuildDir=...` per percorso alternativo; non scegliere directory che contengono file personali perché Gradle clean la elimina. Il vecchio build/ non viene rimosso automaticamente. Client, server e gametest usano cartelle separate in run-neoforge/, per non aprire mondi Fabric.
- Procedura PowerShell: chiudere client e sincronizzazione IDE, git pull --ff-only, .\gradlew.bat --stop, .\gradlew.bat clean build --no-daemon --no-parallel --max-workers=2. Se il lock persiste, chiudere IntelliJ e usare PowerShell esterno o un percorso di build nuovo; non disabilitare protezioni. Non cancellare run/.
- CI aggiunge Windows clean build e ricompilazione, e avvio GameTestServer con tre prove: recipe manager (otto ID rappresentativi), tank (simulazione, passi 250 mB, esclusione lava), creazione block entity del barile. Sono verifiche nuove, non ancora risultati di test. Non sostituiscono il collaudo grafico e delle altre meccaniche.

### Settimo lotto — crash creazione mondo / registri worldgen
- CI f618858, run 45902141331: audit, compilazione, JAR e Windows PASS; GameTest FAIL durante il caricamento dei registri: int provider minecraft:trapezoid non registrato in 1.21.1. Il crash client riportato dall'utente attraversa RegistryDataLoader/WorldLoader/CreateWorldScreen; il log client fornito non contiene la causa iniziale, ma è coerente con la CI.
- Sostituiti 18 provider in sette JSON (due cluster vite e cinque placed feature) con minecraft:weighted_list nativo, schema distribution/data/weight verificato nei dati vanilla 1.21.1 (trees_birch). Per intervalli simmetrici [-n,n], plateau 0, peso n+1-|offset|, identica distribuzione marginale triangolare; non identica sequenza casuale per seed. Conservati range, rarity, count, biomi, predicati, terra brulla e arboscelli. Audit aggiunge controllo ricorsivo dei random_offset contro questa regressione. Nessun contenuto disabilitato.
- Necessaria nuova CI GameTest e prova client: non dichiarare l'avvio risolto prima dei risultati. Nessun merge o release.

### Ottavo lotto — blocchi custom invisibili nel mondo
- CI 5a5c267, run 45903585899: audit, Linux/Windows build e GameTest PASS. L'utente conferma di poter entrare nel mondo.
- Segnalazione utente: diversi blocchi con modello custom invisibili. BaseEntityBlock 1.21.1 restituisce INVISIBLE di default; PizzaOvenBlock aveva già override MODEL, mentre AgingCheeseBlock, BarrelBlock, BrewingBarrelBlock, CheeseAgingRackBlock, CheeseHookBlock, CheeseVatBlock e CrushingTubBlock ne erano privi. Aggiunto override MODEL alle sette classi, incluse tutte le varianti legno registrate; conservati modelli, texture, state, forme, BE, fluidi e logica. CheeseHookOccupiedSpaceBlock resta volutamente invisibile.
- Aggiunto GameTest customBlockModels: verifica RenderShape.MODEL per tutti gli stati di ogni blocco delle famiglie interessate e del forno, e INVISIBLE per il blocco tecnico. Non è un test del baking client o della resa grafica; necessario nuovo runClient e verifica dei modelli, layer trasparenti e texture. Nessun merge/release.

### Nono lotto — risorse scartate silenziosamente al reload
- CI 831af37 / 45904716057: cinque GameTest, audit e build Linux/Windows PASS. Il log rivela però 13 ricette, 19 advancement e un tag c:crops scartati. Non considerare il precedente PASS un collaudo integrale.
- processResources converte ingredienti Fabric any/components e item ability alla sintassi NeoForge/FD 1.21.1; la pozione d'acqua rimane vincolata al componente potion_contents. Sound FD usa oggetto sound_id (verificato in cutting/acacia_log.json del branch 1.21 upstream). Adattato iron_chain a chain in ingredienti e criteri advancement, recipes a recipe nel trigger recipe_unlocked. Corretti quattro ID duplicati nel tag c:crops. Nessuna modifica alle quantità/ricompense o rimozione di ricette.
- Aggiunti GameTest per tutte le ricette impacchettate IDR e per accettazione secchio/bottiglia d'acqua e rifiuto pozione curativa/bottiglia vuota. CI controlla gli errori di parsing del log, anche quando il server termina normalmente. Nuovi test non ancora verificati. Nessun merge/release.

### Decimo lotto — persistenza componenti, fluidi, fermentazione e test opzionali
- CI e4306d4 / 45905826190: sette GameTest PASS; tutte le 270 ricette IDR presenti; controllo log di reload PASS, oltre alle build Linux/Windows e all'audit.
- Corretto equals/hashCode dei record PizzaToppingApplication e PizzaPeelCargo: ItemStack 1.21.1 ha identità di riferimento, quindi usare ItemStack.matches e hashItemAndComponents per componenti persistenti. Nessuna modifica ai codec o al formato dei salvataggi; equivalenza canonica delle fette invariata.
- Aggiunti tre GameTest: persistenza NBT pizza/pala, cottura con contenitore e nome; capability di bottiglie/secchi, esclusione mix e salvataggio del serbatoio; fermentazione dal contenitore alla bottiglia con qualità e cultura persistenti. Il test di fermentazione parte dal tick finale dopo ripristino NBT, non verifica l'intera durata in un mondo reale.
- Matrice CI GameTest con Serene Seasons disattivata/attivata (+ GlitchCore); non rende obbligatoria la dipendenza e non certifica ancora le singole meccaniche stagionali. Nuova CI da verificare. Test client, JEI e compatibilità thirst ancora aperti. Nessun merge/release.


### Undicesimo lotto — ripresa del porting e smoke test client (27/11/2026)
- CI 82e1e30 / 45906180607: audit, clean build Linux e Windows, verifica JAR, ricompilazione Windows e 10/10 GameTest PASS in entrambe le configurazioni (solo FD e FD + Serene Seasons/GlitchCore). Verificati i log: nessun errore di reload IDR rilevato dal controllo CI.
- Aggiunto controllo client opt-in -PportClientSmoke=true, con matrice GitHub Actions Xvfb con/senza JEI. Verifica caricamento iniziale, modelli baked rappresentativi dei blocchi e item, wrapper e modello carico della pala, presenza effettiva di JEI. Termina il client solo in questa modalità; non modifica gameplay o mondi. La CI richiede il marker esplicito IDR_CLIENT_SMOKE_PASS.
- Il nuovo controllo non è ancora eseguito; non certifica resa grafica, texture singole, apertura mondo, sincronizzazione multiplayer o registrazione ricette JEI in gioco. Restano test manuali e compatibilità thirst/automazione. Nessun merge, release o pubblicazione.


### Dodicesimo lotto — risorse grafiche 1.21.1
- CI cbea595 / 46034714933: 7/7 job PASS; avvio client con/senza JEI (1628 stati in ciascun caso), build Linux/Windows e 10/10 GameTest per configurazione Serene Seasons. I log client mostrano tuttavia 32 varianti mancanti dei fluidi world-placeable, 14 template cartelli vanilla non presenti in 1.21.1, e PNG whey non decodificabile (unknown PNG chunk type). Audio OpenAL del runner non disponibile, distinto dai difetti IDR.
- Aggiunti blockstate e modelli particle-only per olio e succo di mela con variante vuota, come water vanilla 1.21.1. Cartelli dei tre legni passati a modello particle-only e geometria vanilla block entity, come oak_sign/hanging_sign 1.21.1. Registrati WoodType in Sheets e riutilizzati gli sprite entity legacy di ulivo/ciliegio. Per mandorlo, assente sprite entity legacy: se il PNG moderno ha UV 64x32 viene riutilizzato, altrimenti generazione dal texture tiling del legno originale; resa grafica ancora da confrontare. Nessuna modifica alle texture sorgente o alle meccaniche. Whey ricodificato in PNG RGBA nel processResources senza cambiare pixel; da verificare la lettura Java.
- Estesi controlli client ai cartelli, sprite fluidi e sprite sign atlas; CI respinge errori di modelli e texture IDR anche quando l'avvio termina regolarmente. Nuova CI da verificare. Test JEI in mondo e compatibilità thirst/automazione ancora aperti. Nessun merge/release.


### Tredicesimo lotto — profili indipendenti compatibilità sete
- Artefatti nativi verificati: TWT 2.1.5 CF 679270:7017923; TW2 1.8.0+1.21.1-neoforge CF 1700787:9105254; TAN 10.1.0.13 CF 246391:5833252 (GlitchCore già fissata). Nessuna dipendenza obbligatoria aggiunta e nessun cambio dei valori originali.
- TAN: sorgente branch 1.21.1 (ThirstHandler, ModTags, drinks.json) conferma tag thirst/{2,4,6}_thirst_drinks e hydration/{20,40,60}_hydration_drinks, letti rispettivamente come punti sete e fattori 0.2/0.4/0.6. I dati esistenti corrispondono allo schema. TW2 documenta data/<namespace>/thirstwastaken2/drinks e API thirstValues(ItemStack); aggiunta verifica a runtime degli importi per succhi/latte/vino e olio. TWT: repository upstream originale non contiene il branch 1.21.1; per ora solo avvio e presenza della mod, non verifica del loader hydration_items.
- Gradle -PportThirst=twt|twt2|tan seleziona un solo artefatto opzionale. CI separata per ogni profilo con GameTest e marker esplicito, in aggiunta alle prove baseline. Verifiche API tramite reflection confinata ai test, senza linkare classi opzionali nel codice di gioco. Nuovi job non ancora confermati; non dichiarare compatibilità prima dei risultati e del test consumo effettivo.
- Riferimenti: https://www.curseforge.com/minecraft/mc-mods/thirst-was-taken/files/7017923 ; https://www.curseforge.com/minecraft/mc-mods/thirst-was-taken-2/files/9105254 ; https://www.curseforge.com/minecraft/mc-mods/tough-as-nails/files/5833252 ; https://github.com/Glitchfiend/ToughAsNails/tree/1.21.1 ; https://github.com/whoisnighter/ThirstWasTaken2.

- CI c0c599e / 46037623730: audit PASS, processResources FAIL su ImageIO (Error reading PNG image data). Il PNG whey legacy è quindi illeggibile anche dal decoder Java; ricodifica diretta non possibile. Sostituita nel solo output 1.21.1 la texture statica legacy con il primo frame del PNG whey_still funzionante (lo sprite animato è quello usato da LiquidSurfaceVisuals). Non è un recupero pixel-identico del file difettoso; originali sorgente preservati. La nuova CI dovrà confermare sia compilazione sia risorse grafiche.


### Quattordicesimo lotto — logo Margherita, cutout e capability nel mondo
- CI 6b9031c / 46037899607: tutti e dieci i job PASS (build Linux/Windows, audit, GameTest con/senza Serene Seasons, avvio client con/senza JEI e tre profili sete). Confermati TW2/TAN tramite i test API/tag già presenti; TWT solo avvio/presenza, consumo effettivo da verificare. Nessuna dichiarazione di collaudo visivo completo.
- Segnalazione utente: icona della mod non Margherita e fondi neri su sapling, foglie vite e tutti gli stadi dei grappoli. Il logo TOML puntava al PNG cassetta; impostato direttamente il PNG approvato pizza_margherita.png (verificato visivamente) e logoBlur=false. Non modificato il logo dei branch Fabric.
- Analizzati 10 blockstate e 25 modelli referenziati, 19 privi di render_type; aggiunto minecraft:cutout ai 19, inclusi sapling nei vasi, canopia, stelo maturo, vite, grappoli bianchi e viola a tutti gli stadi. I sei modelli già cutout restano intatti. Nessuna modifica alle texture, forme, tinte o meccaniche di crescita. Il livello è dichiarato nei JSON nativi NeoForge, senza ricorrere al setter deprecato.
- Client smoke ora verifica getRenderTypes per ogni stato dei 10 blocchi e rifiuta solid, incluso multipart. Nuovo GameTest effettua lookup reale Capabilities.FluidHandler.BLOCK su barili e calderone, simulazione/trasferimento, rifiuto miscela, livelli e rimozione capability. Non equivale a test con Create installato. Nuova CI non ancora confermata; nessun merge o release.

- Audit trasparenza esteso: cardo e vaso + 33 modelli di porte/botole dei tre legni erano senza render_type; aggiunto minecraft:cutout preservando i PNG e geometria. Le altre colture e rope sono già cutout (pomodori staked usano i modelli nativi FD). Il test client ora include anche tutte queste famiglie. Nessun cambiamento ai branch Fabric. CI da verificare.

- CI 8ac4d31 / 46042296400: audit e build Linux PASS; nuovo GameTest nativeWorldFluidCapabilities fallisce solo all'asserzione finale che richiedeva capability null sul calderone vuoto. Upstream NeoForge 1.21.1 CauldronFluidContent registra il CauldronWrapper per Blocks.CAULDRON, quindi tale aspettativa era errata. Corretto test per esigere ritorno blocco vanilla, assenza BE, gestore nuovo/vuoto e simulazione acqua non distruttiva. Non alterato gameplay. Il riempimento automatico iniziale di un calderone vanilla con liquido IDR non è certificato: occorre distinguere il gestore vanilla da quello del calderone IDR già riempito. CI finale da verificare. Fonte: https://github.com/neoforged/NeoForge/blob/1.21.1/src/main/java/net/neoforged/neoforge/fluids/CauldronFluidContent.java.


### Quindicesimo lotto — collaudo grafico utente e integrazione Create
- L'utente ha verificato il gioco e confermato il funzionamento delle correzioni grafiche (logo, sapling/viti). CI ced3a8c / 46042660426: 10/10 job PASS, incluse capability native nel mondo, tutti i profili sete, cutout e client con/senza JEI. Questo non certifica la resa visiva di ogni singolo asset o tutte le meccaniche opzionali.
- Selezionata distribuzione ufficiale Create 6.0.10 per Minecraft 1.21.1 NeoForge, CurseForge 328085:7963363 (21/04/2026). Nuovo profilo -Pwith_create=true soltanto localRuntime, nessun link di classi Create in gameplay e nessuna nuova dipendenza obbligatoria. GameTest dedicato e marker obbligatorio nella CI. Fonte artefatto: https://www.curseforge.com/minecraft/mc-mods/create/files/7963363 .
- Test opt-in piazza barile IDR e pipe/tank Create reali, interroga FluidPropagator.hasFluidCapability e FlowSource.FluidHandler con BlockFace, verifica probe da 1 mB non distruttivo e trasferisce 375 mB nei due sensi via FluidUtil sui due handler reali, senza perdita o miscela. API osservata nel branch upstream mc1.21.1/dev (6.0.11 in sviluppo); compatibilità della firma con 6.0.10 va confermata dalla CI. Non è ancora una prova del motore/pompa o rete cinetica a tick reali. Il calderone rimane a passi da 250 mB come Fabric: il probe da 1 mB di Create non lo può estrarre; nessuna modifica arbitraria alla meccanica originale, da valutare separatamente. Fonte: https://github.com/Creators-of-Create/Create/blob/mc1.21.1/dev/src/main/java/com/simibubi/create/content/fluids/FlowSource.java .
- Rendering fluidi esterni: se getStillTexture() restituisce un ID non presente in atlas, continuare la ricerca convenzionale/fallback già esistente anziché mostrare missing texture. Non cambiano sprite/tinte dei fluidi IDR.
- Nuova CI in attesa, nessun merge/release. Restano pompe reali, JEI in mondo, consumo effettivo mod sete, stagioni, multiplayer.
