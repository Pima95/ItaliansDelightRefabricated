# Catalogo ingredienti Pizza Update 1.0.0 — inventario da registri reali

**Ricognizione 12 ottobre 2026. Stato: PROPOSTA DI CATALOGO, NON LISTA GIA' APPROVATA ITEM PER ITEM, NON IMPLEMENTATA.**

## Ambito approvato

Il **catalogo ufficiale dei tipi** di topping deriva soltanto da **Italian's Delight Refabricated** e **Farmer's Delight Refabricated**. Item di altre mod sono ammessi come **sostituti esplicitamente equivalenti** di tipi gia' inclusi, attraverso tag comuni **specifici verificati** o mapping puntuali; un tag alimentare ampio **non** introduce nuovi tipi. Non trasformare automaticamente ogni alimento delle due mod in topping.

Questa ricognizione e' basata sui **registri degli item esistenti**, non sulle decisioni di gameplay gia' approvate: **ID verificato** = dichiarato nel sorgente della versione target; **topping candidato** = adatto da valutare, non ancora approvato o implementato come pizza topping; **escluso dal catalogo iniziale proposto** = non e' un condimento finale opportuno, salvo futura decisione. Tag e comportamento dei contenitori vanno verificati separatamente.

### Fonti esatte

- Italian's Delight, branch `feature/pizza-1.0.0`: [ModItems.java](https://github.com/Pima95/ItaliansDelightRefabricated/blob/feature/pizza-1.0.0/src/main/java/dev/italiansdelight/common/registry/ModItems.java), [ModBlocks.java](https://github.com/Pima95/ItaliansDelightRefabricated/blob/feature/pizza-1.0.0/src/main/java/dev/italiansdelight/common/registry/ModBlocks.java).
- Farmer's Delight Refabricated, branch ufficiale `fabric/latest/26.2` con `mod_version=3.6.28`: [ModItemIds.java](https://github.com/MehVahdJukaar/FarmersDelightRefabricated/blob/fabric/latest/26.2/src/main/java/vectorwing/farmersdelight/common/references/ModItemIds.java), [ModBlockItemIds.java](https://github.com/MehVahdJukaar/FarmersDelightRefabricated/blob/fabric/latest/26.2/src/main/java/vectorwing/farmersdelight/common/references/ModBlockItemIds.java) e [ModItems.java](https://github.com/MehVahdJukaar/FarmersDelightRefabricated/blob/fabric/latest/26.2/src/main/java/vectorwing/farmersdelight/common/registry/ModItems.java).
- Dipendenza effettiva di Italian's Delight: `fdrf_version=26.2-3.6.28+refabricated` in [gradle.properties](https://github.com/Pima95/ItaliansDelightRefabricated/blob/feature/pizza-1.0.0/gradle.properties).

## 1. Italian's Delight Refabricated — ID esistenti e candidati

Tutti gli ID delle prossime tabelle usano il namespace **`italiansdelight:`**, tranne diversa indicazione.

| Gruppo | ID verificati nel sorgente | Uso sulla pizza proposto | Stato |
| --- | --- | --- | --- |
| Pomodoro | `tomato_slice` | topping vegetale, **non salsa automatica** | candidato prioritario |
| Basilico | `basil` (item del blocco coltivabile) | erba aromatica | candidato prioritario |
| Mozzarella | `mozzarella`, `mozzarella_slice` | latticini, pezzi/fette | candidati prioritari; scegliere le equivalenze di formato |
| Latticini freschi | `bocconcino`, `ricotta`, `burrata` | latticini, pezzi/creme | candidati |
| Parmigiano | `parmigiano_reggiano_wedge`, `grated_parmigiano_reggiano` | latticini, scaglie/grattugiato | candidati prioritari |
| Pecorino | `pecorino_romano_wedge`, `grated_pecorino_romano` | latticini, scaglie/grattugiato | candidati prioritari |
| Gorgonzola | `gorgonzola_wedge` | latticini, pezzetti/crema | candidato prioritario |
| Provolone | `provolone_slice` | latticini, fette | candidato prioritario |
| Scamorza | `scamorza`, `smoked_scamorza`, `scamorza_slice`, `smoked_scamorza_slice` | latticini; decidere se includere forme intere oppure solo fette | candidati |
| Panna/creme | `cream_bowl`, `mascarpone` | categoria **latticini**, eventuale ruolo **salsa**; restituire la ciotola quando previsto | candidati; decidere ruolo per ciascuno |
| Salumi affettati | `prosciutto_crudo_slice`, `salame_slice`, `mortadella_slice`, `pancetta_slice` | proteine animali, fette | candidati prioritari |
| Salumi affettati (continua) | `guanciale_slice`, `bresaola_slice`, `coppa_slice`, `speck_slice` | proteine animali, fette | candidati prioritari |
| Sale | `salt` | eventuale condimento minimale; rendering ed efficacia da decidere | **non incluso automaticamente** |

**Altri ID reali NON da abilitare automaticamente come topping:** `rennet`, `curd`, `blue_mold_culture`, `apple_cider_vinegar`, `sheep_milk_bottle`, `whey_bottle`, prodotti preparatori `fresh_*`, `prepared_*`, `raw_*`, `salted_ham`, `smoked_prepared_speck`, e piatti gia' pronti (pasta, risotti, insalata di mozzarella). Anche i salumi **interi** `prosciutto_crudo`, `salame`, `mortadella`, `pancetta`, `guanciale`, `bresaola`, `coppa`, `speck` sono registrati ma **si propongono le rispettive fette**, senza consumare indiscriminatamente una forma intera sulla pizza. Le forme intere di formaggi non porzionati come `parmigiano_reggiano` o `gorgonzola` restano fuori fino a decisione esplicita.

**Attenzione alle categorie nutrizionali gia' approvate:** uova, olive, panna e olio d'oliva hanno una classificazione di gameplay, **ma non e' stato trovato nel registro principale di Italian's Delight un item autonomo `egg`, `olive` o `olive_oil`**. La panna **esiste** come `italiansdelight:cream_bowl`. Non inventare ID per ingredienti non registrati. L'uovo e' disponibile in Farmer's Delight Refabricated come `fried_egg`; olio/olive diventano utilizzabili **solo** quando esiste un tipo ufficiale nelle due mod o un item equivalente ad esso effettivamente registrato.

## 2. Farmer's Delight Refabricated 3.6.28 (MC 26.2) — ID verificati

Tutti gli ID di questa tabella usano namespace **`farmersdelight:`**. Anche `onion` e' reale, ma viene registrato come **BlockItem** attraverso `ModBlockItemIds.ONION_CROP` anziche' `ModItemIds`.

| Gruppo | ID verificati nel sorgente 26.2 | Uso sulla pizza proposto | Stato |
| --- | --- | --- | --- |
| Salsa di pomodoro | `tomato_sauce` | **ruolo salsa**, categoria nutrizionale salse | candidato prioritario; **resto: bowl** |
| Pomodoro fresco | `tomato` | topping vegetale, non salsa implicita | candidato prioritario |
| Cipolla | `onion` | vegetale, anelli/pezzetti | candidato prioritario |
| Cavolo | `cabbage`, `cabbage_leaf` | vegetale, preferibile forma foglia | candidati; decidere intero vs foglia |
| Uovo | `fried_egg` | proteine animali, topping solido | candidato prioritario |
| Bacon | `bacon`, `cooked_bacon` | proteine animali, striscioline | candidati; distinguere varianti |
| Prosciutto | `ham`, `smoked_ham` | proteine animali, affettato/pezzi | candidati |
| Pollo | `chicken_cuts`, `cooked_chicken_cuts` | proteine animali, pezzetti | candidati; preferire cotto se si sceglie |
| Merluzzo | `cod_slice`, `cooked_cod_slice` | proteine animali, pesce | candidati; valutare crudo/cotto |
| Salmone | `salmon_slice`, `cooked_salmon_slice` | proteine animali, pesce | candidati; valutare crudo/cotto |
| Zucca | `pumpkin_slice` | vegetale stagionale | candidato secondario |
| Carne macinata | `minced_beef`, `beef_patty` | proteine animali | **non nel primo insieme prioritario**: patties e lavorazioni ancora da valutare |
| Impasto | `wheat_dough` | **base della pizza**, non topping | riuso del percorso impasto gia' progettato |

**Da escludere come topping nel primo elenco proposto:** `rotten_tomato`, `raw_pasta`, `pie_crust`, `milk_bottle`, `hot_cocoa`, `apple_cider`, `melon_juice`, zuppe, risi, insalate e piatti completi (sandwich, hamburger, ecc.), dolci e dessert. Non significa escludere future ricette autonome: questa lista riguarda soltanto i condimenti applicati alla pizza.

## 3. Tag condivisi: effettivamente verificati vs da controllare

| Tag / dichiarazione | Verifica concreta nel progetto | Uso progettuale consentito |
| --- | --- | --- |
| `#c:foods/cheese` | **VERIFICATO:** file [`data/c/tags/item/foods/cheese.json`](https://github.com/Pima95/ItaliansDelightRefabricated/blob/feature/pizza-1.0.0/src/main/resources/data/c/tags/item/foods/cheese.json), che include i latticini di Italian's Delight | **classificazione latticini** e base per confrontare item; **non** rende automaticamente equivalenti tutti i formaggi o valido ogni formaggio sconosciuto |
| `#c:cheese` | **VERIFICATO:** [alias presente](https://github.com/Pima95/ItaliansDelightRefabricated/blob/feature/pizza-1.0.0/src/main/resources/data/c/tags/item/cheese.json), include `#c:foods/cheese` | compatibilita' storica di categoria; **non** identita' canonica specifica |
| `#c:crops/basil` | **VERIFICATO:** [`data/c/tags/item/crops/basil.json`](https://github.com/Pima95/ItaliansDelightRefabricated/blob/feature/pizza-1.0.0/src/main/resources/data/c/tags/item/crops/basil.json) include `italiansdelight:basil` | possibile identita' specifica basilico, controllando eventuali item di terze mod aggiunti al tag |
| `#c:crops/tomato` | **RIFERIMENTO VERIFICATO** nella ricetta [`mozzarella_salad.json`](https://github.com/Pima95/ItaliansDelightRefabricated/blob/feature/pizza-1.0.0/src/main/resources/data/italiansdelight/recipe/mozzarella_salad.json); **contenuto del tag nel runtime NON verificato** | candidato per pomodori equivalenti, controllando ID e contenuto caricato |
| `#c:foods/leafy_green` | **RIFERIMENTO VERIFICATO** nella medesima ricetta; contenuto effettivo nel runtime **NON verificato** | categoria verde a foglia soltanto, **non** equivalenza indiscriminata |
| Altri tag `#c:` specifici per mozzarella, bacon, uova, pesce, olio, cipolle ecc. | **NON ANCORA VERIFICATI** nella combinazione MC 26.2 + mod installate | **non inventare stringhe** o usarle come garanzia di supporto prima di esaminarne realmente il JSON/registry |

I tag `#c:foods/cheese` e `#c:cheese` sono **ampi**: consentono di conoscere la categoria nutrizionale, non di impedire automaticamente di mettere gorgonzola e mozzarella sullo stesso quarto. Per l'equivalenza servono gruppi specifici (es. mozzarella) verificati con item/tag adatti.

**Se un tag comune contiene item di altre mod non presenti tra i tipi ufficiali:** un filtro su **identita' canoniche ammesse** continua a essere necessario. Prima validare che l'item corrisponda a **uno dei tipi del catalogo**, poi gestire i bonus e il renderer. L'ItemStack consumato e' conservato per rimborso/remainder.

## 4. Decisioni ancora necessarie prima del codice

1. **Approvare o correggere i gruppi proposti** nelle due tabelle: una lista di ID reali non e' da sola il via libera a usarli tutti sulle pizze.
2. **Forma prima dell'applicazione:** mozzarella e scamorza intere o a fette; cavolo intero o foglie; formaggi a spicchi/grattugiati; carni crude vs gia' cotte; ciotole di panna/ricotta/mascarpone.
3. **Pomodoro di Farmer's Delight:** usare `tomato` come topping vegetale e `tomato_sauce` come salsa e' la proposta attuale, **non ancora una decisione confermata**.
4. **Equivalenze puntuali e tag reali:** mozzarella vs mozzarella_slice, pomodoro fresco di Farmer's Delight vs tomato_slice di Italian's Delight (non necessariamente lo stesso tipo fino alla decisione), bacon/ham vs salumi della nostra mod; controllare sovrapposizioni e trasformazioni prima di raggruppare.
5. **Remainder dei contenitori:** `farmersdelight:tomato_sauce` dichiara `craftRemainder(Items.BOWL)`, e `italiansdelight:cream_bowl`, `ricotta`, `mascarpone` dichiarano un resto ciotola. **Verificare in gioco** il meccanismo quando il contenuto viene applicato alla pizza, facendo restituire il recipiente **una sola volta** senza duplicare l'ingrediente.
6. **Tag comuni:** confermare JSON di ogni tag necessario nei JAR/datapack **26.2**, senza assumere che la sintassi o gli insiemi di versioni precedenti siano rimasti identici. Per i nomi qui non verificati lasciare spazio nel mapping per definizioni specifiche.

**Restano gia' confermati e invariati:** massimo sei tipi di topping per quarto, salsa opzionale e mai due salse sovrapposte, anti-duplicato di equivalenti, rimborsi con ItemStack originale, quattro fette per pizza cotta, punti fame e saturazione calcolati per categorie (tetto 6 ciascuno), niente effetti speciali e nessun taglio con coltelli. Non modificare Java/risorse eseguibili prima di approvazione.
