# Catalogo ingredienti Pizza Update 1.0.0 — inventario da registri reali

**Ricognizione 12 ottobre 2026. Stato: PROPOSTA DI CATALOGO, NON LISTA GIA' APPROVATA ITEM PER ITEM, NON IMPLEMENTATA.**

## Ambito approvato

Il **catalogo ufficiale dei tipi** di topping deriva soltanto da **Italian's Delight Refabricated** e **Farmer's Delight Refabricated**. Item di altre mod sono ammessi come **sostituti esplicitamente equivalenti** di tipi gia' inclusi, attraverso tag comuni **specifici verificati** o mapping puntuali; un tag alimentare ampio **non** introduce nuovi tipi. Non trasformare automaticamente ogni alimento delle due mod in topping.

Questa ricognizione e' basata sui **registri degli item esistenti**, non sulle decisioni di gameplay gia' approvate: **ID verificato** = dichiarato nel sorgente della versione target; **topping candidato** = adatto da valutare, non ancora approvato o implementato come pizza topping; **escluso dal catalogo iniziale proposto** = non e' un condimento finale opportuno, salvo futura decisione. Tag e comportamento dei contenitori vanno verificati separatamente.

### Fonti esatte

- Italian's Delight, branch `feature/pizza-1.0.0`: [ModItems.java](https://github.com/Pima95/ItaliansDelightRefabricated/blob/feature/pizza-1.0.0/src/main/java/dev/italiansdelight/common/registry/ModItems.java), [ModBlocks.java](https://github.com/Pima95/ItaliansDelightRefabricated/blob/feature/pizza-1.0.0/src/main/java/dev/italiansdelight/common/registry/ModBlocks.java).
- Farmer's Delight Refabricated, branch ufficiale `fabric/latest/26.2` con `mod_version=3.6.28`: [ModItemIds.java](https://github.com/MehVahdJukaar/FarmersDelightRefabricated/blob/fabric/latest/26.2/src/main/java/vectorwing/farmersdelight/common/references/ModItemIds.java), [ModBlockItemIds.java](https://github.com/MehVahdJukaar/FarmersDelightRefabricated/blob/fabric/latest/26.2/src/main/java/vectorwing/farmersdelight/common/references/ModBlockItemIds.java) e [ModItems.java](https://github.com/MehVahdJukaar/FarmersDelightRefabricated/blob/fabric/latest/26.2/src/main/java/vectorwing/farmersdelight/common/registry/ModItems.java).
- Dipendenza effettiva di Italian's Delight: `fdrf_version=26.2-3.6.28+refabricated` in [gradle.properties](https://github.com/Pima95/ItaliansDelightRefabricated/blob/feature/pizza-1.0.0/gradle.properties).

## Requisito di preparazione e dimensione — CONFERMATO (12 ottobre 2026)

**Nessun alimento puo' essere messo sulla pizza solo perche' commestibile, presente in una delle due mod autorizzate o incluso in un tag condiviso.** Il topping deve rappresentare un **ingrediente gia' pronto a essere distribuito sulla pizza**, porzionato e fisicamente plausibile per dimensioni/forma, oppure una **salsa/crema pronta da spalmare**. Il filtro si applica a tutti gli ItemStack, **anche se provenienti da mod terze equivalenti**.

**NON applicabili direttamente:** prosciutti cotti/crudi e salumi in forme intere, ruote intere di formaggio, grandi forme intere di scamorza, bistecche o grossi tagli di carne interi (es. `minecraft:beef` o `minecraft:cooked_beef`), cavoli/cipolle interi quando il risultato non rappresenterebbe una guarnizione credibile, ingredienti industriali/preparatori, cibi ancora da lavorare, e pasti completi. Non scalare graficamente un grande pezzo di carne/formaggio per farlo apparire una fetta: **serve una vera variante di item idonea**.

**Candidati idonei per forma (singole scelte da confermare in elenco):** formaggi gia' affettati, porzionati a spicchi o grattugiati; salumi gia' affettati; **carni/pesci gia' tagliati in porzioni idonee, sia crudi sia cotti**; pomodoro a fette, verdure a foglia/pezzetti pronti, erbe, salse e creme preparate. **La cottura preventiva NON e' richiesta per carne/pesce gia' porzionati (CONFERMATO)**; entrambi gli stati hanno **identici effetti alimentari sulla pizza**. Rimangono vietati tagli interi troppo grandi, crudi o cotti. Le scelte dei singoli tipi di topping restano da approvare.

**Crudo e cotto — CONFERMATO (12 ottobre 2026):** una volta che un tipo di carne o pesce e' incluso nel catalogo e l'item e' **gia' affettato/tagliato in dimensioni plausibili**, **entrambe le varianti crude e cotte sono applicabili sulla pizza**. **Stessi punti fame (+2 per categoria proteine animali), stessa saturazione (+2,5 per categoria) e nessun effetto aggiuntivo** per il quarto che le contiene; valgono sempre un solo bonus proteine animali per categoria e i tetti 6/6 della fetta. Esempi di coppie **registrate**: `farmersdelight:bacon` / `farmersdelight:cooked_bacon`, `farmersdelight:chicken_cuts` / `farmersdelight:cooked_chicken_cuts`, `farmersdelight:cod_slice` / `farmersdelight:cooked_cod_slice`, `farmersdelight:salmon_slice` / `farmersdelight:cooked_salmon_slice`. **La regola sullo stato e' approvata, mentre la lista specifica di tipi abilitati resta una shortlist da finalizzare.** Non introdurre penalita' di cibo crudo, bonus extra per cibo gia' cotto, effetti di stato o requisiti di ricottura. Conservare l'ItemStack di provenienza per eventuali rimborsi; **conversioni automatiche di item crudo in cotto durante la cottura della pizza non sono state definite**, quindi non inventarle. L'equivalenza anti-duplicato tra gli ID crudo/cotto dello stesso animale e' un dettaglio del futuro mapping canonico, non deducibile dal solo bonus nutrizionale.

**Cutting Board:** utilizzare una ricetta reale di **Farmer's Delight Refabricated** (o altra lavorazione consentita) per trasformare un ingrediente troppo grande nella relativa porzione, **se una tale ricetta e un output effettivamente idoneo esistono**. La Cutting Board non e' richiesta per i condimenti gia' porzionati; **non** e' un requisito per la stesura dell'impasto, il condimento o il servizio delle fette della pizza cotta. **Non inventare una ricetta o un nuovo item di output** prima di concordarli esplicitamente. Se un alimento e' troppo grande e non esiste ancora una variante lavorata idonea, resta **non applicabile**.

**Conseguenza per tag comuni/equivalenze:** validazione in ordine di **tipo ufficiale del catalogo -> ID/tag comune specifico -> variante/stato dell'item effettivamente pronto -> limiti di salsa, sei topping e anti-duplicato**. Un tag `c:foods/cheese`, per esempio, non rende applicabile una ruota intera solo perche' contiene una fetta compatibile. Le definizioni devono distinguere **forme intere e porzionate** anche quando condividono categoria nutrizionale. Se il test fallisce, il clic non consuma oggetti e non cambia la pizza.

**Criteri di test:** mozzarella a fette accettabile ma grande ruota di parmigiano no; salame a fette si, salame intero no; `farmersdelight:cooked_chicken_cuts` candidato, bistecca intera `minecraft:cooked_beef` no; `farmersdelight:tomato_sauce` candidato come salsa, una ciotola di pasto completo no; equivalenza cross-mod che **non** aggira il controllo su porzione/stato. Tutti i valori nutrizionali e i rimborsi gia' approvati restano invariati.

## 1. Italian's Delight Refabricated — ID esistenti e candidati

Tutti gli ID delle prossime tabelle usano il namespace **`italiansdelight:`**, tranne diversa indicazione.

| Gruppo | ID verificati nel sorgente | Uso sulla pizza proposto | Stato |
| --- | --- | --- | --- |
| Pomodoro | `tomato_slice` | topping vegetale, **non salsa automatica** | candidato prioritario |
| Basilico | `basil` (item del blocco coltivabile) | erba aromatica | candidato prioritario |
| Mozzarella | `mozzarella`, `mozzarella_slice` | latticini | **`mozzarella_slice` candidata prioritariamente**; mozzarella intera da valutare per dimensione, **non abilitare direttamente per default** |
| Latticini freschi | `bocconcino`, `ricotta`, `burrata` | latticini, pezzi/creme | candidati **solo se servibili in dimensione/forma appropriata**; crema pronta ammessa come principio, burrata intera non automaticamente |
| Parmigiano | `parmigiano_reggiano_wedge`, `grated_parmigiano_reggiano` | latticini, scaglie/grattugiato | candidati prioritari |
| Pecorino | `pecorino_romano_wedge`, `grated_pecorino_romano` | latticini, scaglie/grattugiato | candidati prioritari |
| Gorgonzola | `gorgonzola_wedge` | latticini, pezzetti/crema | candidato prioritario |
| Provolone | `provolone_slice` | latticini, fette | candidato prioritario |
| Scamorza | `scamorza`, `smoked_scamorza`, `scamorza_slice`, `smoked_scamorza_slice` | latticini | **fette `scamorza_slice`, `smoked_scamorza_slice` candidate**; **forme intere `scamorza`, `smoked_scamorza` non applicabili direttamente** |
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
| Pomodoro fresco | `tomato` | topping vegetale, non salsa implicita | **pomodoro intero non applicabile direttamente**; servono fette/porzioni idonee con item/ricetta verificati |
| Cipolla | `onion` | vegetale, anelli/pezzetti | **cipolla intera non applicabile direttamente**; anelli/pezzetti solo se esiste output tagliato appropriato |
| Cavolo | `cabbage`, `cabbage_leaf` | vegetale, foglie | **`cabbage` intero escluso**; `cabbage_leaf` candidato se compatibile come condimento |
| Uovo | `fried_egg` | proteine animali, topping solido | candidato prioritario |
| Bacon | `bacon`, `cooked_bacon` | proteine animali, striscioline | **ENTRAMBE le varianti ammesse per stato di cottura** se il tipo bacon viene incluso nel catalogo; **stessa nutrizione ed effetti** |
| Prosciutto | `ham`, `smoked_ham` | proteine animali | **forme intere NON utilizzabili direttamente**, anche se cotte/affumicate; richiedere un item di fette/pezzi realmente ottenibile tramite lavorazione |
| Pollo | `chicken_cuts`, `cooked_chicken_cuts` | proteine animali, pezzetti | **ENTRAMBE le varianti ammesse per stato di cottura** se il tipo pollo a pezzi viene incluso nel catalogo; stessa nutrizione |
| Merluzzo | `cod_slice`, `cooked_cod_slice` | proteine animali, pesce | **ENTRAMBE le varianti ammesse per stato di cottura** se il tipo merluzzo a fette viene incluso nel catalogo; stessa nutrizione |
| Salmone | `salmon_slice`, `cooked_salmon_slice` | proteine animali, pesce | **ENTRAMBE le varianti ammesse per stato di cottura** se il tipo salmone a fette viene incluso nel catalogo; stessa nutrizione |
| Zucca | `pumpkin_slice` | vegetale stagionale | candidato secondario |
| Carne macinata | `minced_beef`, `beef_patty` | proteine animali | **non applicabili direttamente nel primo elenco**; valutare solo forme pronte e porzioni realmente adatte, non patty/bistecca interi |
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
2. **Forma prima dell'applicazione (PRINCIPIO APPROVATO):** grandi forme intere di formaggio/salumi, prosciutto intero e bistecche intere non possono essere topping. Per **carne e pesce tagliati in porzioni adatte**, **crudo e cotto sono entrambi permessi e nutrizionalmente uguali**; verificare quali tipi precisi inserire nel catalogo (mozzarella/scamorza a fette, cavolo a foglie, formaggi a spicchi/grattugiati, carni/pesci a pezzi, bocconcino e ciotole di panna/ricotta/mascarpone). Le singole varianti non sono tutte approvate come tipi.
3. **Pomodoro di Farmer's Delight:** usare `tomato` come topping vegetale e `tomato_sauce` come salsa e' la proposta attuale, **non ancora una decisione confermata**.
4. **Equivalenze puntuali e tag reali:** mozzarella vs mozzarella_slice, pomodoro fresco di Farmer's Delight vs tomato_slice di Italian's Delight (non necessariamente lo stesso tipo fino alla decisione), bacon/ham vs salumi della nostra mod; controllare sovrapposizioni e trasformazioni prima di raggruppare.
5. **Remainder dei contenitori:** `farmersdelight:tomato_sauce` dichiara `craftRemainder(Items.BOWL)`, e `italiansdelight:cream_bowl`, `ricotta`, `mascarpone` dichiarano un resto ciotola. **Verificare in gioco** il meccanismo quando il contenuto viene applicato alla pizza, facendo restituire il recipiente **una sola volta** senza duplicare l'ingrediente.
6. **Tag comuni:** confermare JSON di ogni tag necessario nei JAR/datapack **26.2**, senza assumere che la sintassi o gli insiemi di versioni precedenti siano rimasti identici. Per i nomi qui non verificati lasciare spazio nel mapping per definizioni specifiche.

**Restano gia' confermati e invariati:** massimo sei tipi di topping per quarto, salsa opzionale e mai due salse sovrapposte, anti-duplicato di equivalenti, rimborsi con ItemStack originale, quattro fette per pizza cotta, punti fame e saturazione calcolati per categorie (tetto 6 ciascuno), niente effetti speciali e nessun taglio con coltelli. Non modificare Java/risorse eseguibili prima di approvazione.
