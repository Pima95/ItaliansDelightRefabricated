# Adattamento e attribuzione degli asset Rustic — 1.2.0

L'archivio utente `Allegati.zip` contiene 32 voci: risorse grafiche, blockstates, modelli e un JSON di avanzamento. Non contiene le classi Java per le meccaniche originali.

## Usati nel prototipo
- `grapes.png` (nuovo item `grapes`), `grape_vine.png` (modello maturo), vecchie texture `grape_stem_0/1/2.png` già presenti, `grape_seeds.png` già presente.
- `chili_pepper.png` e `chili_pepper_seeds.png` erano già nel repository.
- Le 4 texture `chili_crop_0/1/2/3.png` e il modello `stake_crop` sono stati recuperati dal repository sorgente pubblico [Rustic](https://github.com/cadaverous-eris/Rustic), dato che mancavano nell'archivio.
- `grape_juice_still.png` e animazione già presenti sono ora usati per il fluido di succo d'uva nel client.
- `ghost_pepper.png` è importato ma non ancora registrato come gameplay item: nessuna coltivazione fittizia aggiunta.

## Non importati come JSON di gioco
- `grape_leaves_old.json` usa il vecchio `forge_marker`: incompatibile con Fabric 26.3.
- `grape_leaves.json` richiede rope, sostegni, proprietà `axis/distance/grapes` e logica di distribuzione non presenti nell'archivio; sarà adattato contestualmente alla meccanica vera.
- `winemaxxing.json` usa `rustic:alcohol`, vecchi predicati Forge e NBT 1.12.2. Importarlo ora sarebbe rotto: serve prima il sistema del vino con qualità persistente.
- Le animazioni di succo d'uva sono già nel progetto e non vengono sovrascritte in assenza di una necessità dimostrata.

## Licenza e crediti
Rustic di cadaverous_eris/cadaverous_queen è pubblicato come **MIT** sulle piattaforme ufficiali, ma verificare e mantenere i relativi copyright notice prima della distribuzione della release derivata. Fonte: https://modrinth.com/mod/rustic e repository https://github.com/cadaverous-eris/Rustic .

Il codice nuovo per raccolta e pigiatura in questa mod è una riscrittura Fabric 26.3, non un file Java Rustic 1.12.2 semplicemente copiato.

## Colore delle foglie della vite (Fabric 26.3)

I modelli originali Rustic `grape_leaves_0` e `grape_leaves_1` usano la texture neutra `minecraft:block/oak_leaves` e `tintindex: 0` su tutte le facce fogliari. In Rustic il colore veniva fornito dalla registrazione del blocco: importare soltanto il modello lascia le foglie grigie.

In Italian's Delight il client registra `BlockTintSources.foliage()` per `ModBlocks.GRAPE_LEAVES` tramite `BlockColorRegistry`. Il colore dipende quindi dal **bioma** come per le foglie vanilla, sia nelle foglie centrali sia in quelle laterali; Serene Seasons 26.3 intercetta il foliage color resolver per applicare anche i colori **stagionali** quando è installato. Senza Serene Seasons resta la normale variazione per bioma. La configurazione di colori stagionali di Serene Seasons e i tempi di ricostruzione dei chunk sono gestiti da quel mod.

Nessuna modifica a modelli 3D, hitbox, crescita, corde, frutti o classi lato server. La texture legnosa `grape_vine` e quella dei grappoli `grapes` rimangono senza tinta. Test statico: `.github/scripts/check_vineyard_supports.py`; il rendering e le transizioni di stagione vanno poi verificati in un client con Serene Seasons.

Fonti sorgenti: Rustic `src/main/resources/assets/rustic/models/block/grape_leaves_{0,1}.json`; Serene Seasons 26.3 `common/src/main/java/sereneseasons/season/SeasonColorHandlers.java`; Fabric API `BlockColorRegistry`.
