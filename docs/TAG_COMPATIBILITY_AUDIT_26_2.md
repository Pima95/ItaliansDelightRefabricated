# Audit tag JEI e Fabric 26.2 — revisione del 9 novembre 2026

Fonti: [wiki Fabric](https://wiki.fabricmc.net/tutorial:tags); [ConventionalItemTags ufficiale 26.2](https://github.com/FabricMC/fabric-api/blob/26.2/fabric-convention-tags-v2/src/main/java/net/fabricmc/fabric/api/tag/convention/v2/ConventionalItemTags.java); [ConventionalBlockTags ufficiale 26.2](https://github.com/FabricMC/fabric-api/blob/26.2/fabric-convention-tags-v2/src/main/java/net/fabricmc/fabric/api/tag/convention/v2/ConventionalBlockTags.java).

## Metodo
Confrontate le 265 costanti `ConventionalItemTags` e 127 costanti `ConventionalBlockTags` di Fabric 26.2 con i registri della mod e i 96 file di tag già presenti nel repository. JEI mostra i tag caricati dai datapack (non un elenco universale di tag di JEI). Le categorie senza corrispondenze effettive (minerali, armature, pozioni, ecc.) non sono state aggiunte.

## Nuove associazioni confermate
- `c:item/natural_logs/overworld.json` (3 voci)
- `c:item/natural_logs.json` (3 voci)
- `c:item/natural_woods.json` (3 voci)
- `c:item/stripped_logs.json` (3 voci)
- `c:item/stripped_woods.json` (3 voci)
- `c:block/natural_logs/overworld.json` (3 voci)
- `c:block/natural_logs.json` (3 voci)
- `c:block/natural_woods.json` (3 voci)
- `c:block/stripped_logs.json` (3 voci)
- `c:block/stripped_woods.json` (3 voci)
- `c:item/foods.json` (38 voci)
- `c:item/foods/vegetable.json` (3 voci)
- `c:item/buckets.json` (6 voci)
- `c:item/buckets/milk.json` (1 voci)
- `c:item/drinks.json` (2 voci)
- `c:item/drink_containing/bottle.json` (1 voci)
- `c:item/drink_containing/bucket.json` (1 voci)
- `c:item/seeds.json` (3 voci)

Le classificazioni sono in file `data/c/tags/item/` e `data/c/tags/block/` con `replace:false`; i nomi coincidono con le costanti ufficiali Fabric 26.2.

### Motivazioni
- **Legni**: `c:natural_logs/overworld`, `c:natural_logs` distinguono i tronchi integri; `c:natural_woods` i legni a sei lati; `c:stripped_logs` e `c:stripped_woods` distinguono le forme scortecciate; per i tre legni ulivo, mandorlo, ciliegio da frutto, sia item che blocchi.
- **Cibi**: `c:foods` comprende esattamente 38 item verificati con `Item.Properties.food`, `registerFoodItem` o `registerBowlFoodItem` (piatti di pasta/risotto, pizza *a fette*, latticini edibili e salumi *a fette*, ecc.). Non include pizze intere, ruote di formaggio, salumi interi e frutti non edibili. Le fette di pomodoro/carota/patata sono `c:foods/vegetable`.
- **Fluidi**: i 6 secchi in `c:buckets`; latte di pecora in `c:buckets/milk`; solo i recipienti di latte ovino effettivamente bevibili in `c:drinks`, `c:drink_containing/bottle`, `c:drink_containing/bucket`. Il siero, l'olio, il caglio, l'aceto e il succo di mela in forma attuale non sono bevande.
- **Semi**: `c:seeds` contiene semi di mela, basilico e cardo coltivato; questo non rende i semi di mela automaticamente piantabili, né modifica `minecraft:villager_plantable_seeds`.

## Tag già presenti e confermati
`c:fences/wooden`, `c:fence_gates/wooden`, `c:barrels/wooden` per blocchi e item, le categorie `c:storage_blocks` dei crate, `c:crops`, `c:crops/basil`, `c:crops/cardoon`, `c:foods/cheese`, `c:foods/curd`, `c:drinks/milk`, `c:dusts/salt`, `c:rennet`; tag `minecraft`, `italiansdelight`, `sereneseasons`.

## Correzione della revisione precedente
Eliminati `c:fruits/cherry` e `c:fruits/olive`: non sono definiti nell'API convenzionale di Fabric 26.2. Il tag ufficiale `c:foods/fruit` riguarda invece frutti consumabili, e non si applica agli item `olives`, `almonds`, `cherries` attualmente registrati senza componente food. Le materie prime rimangono in `c:crops`.

## Verifica in Minecraft
Ricaricare i datapack/mod e controllare nella schermata tag JEI: `#c:natural_logs/overworld`, `#c:stripped_logs`, `#c:foods`, `#c:foods/vegetable`, `#c:seeds`, `#c:buckets`, `#c:drink_containing/bottle`. Verificare che nessuna ricetta o comportamento sia mutato. Tag di terze parti possono aggiungere elementi diversi a seconda del modpack.
