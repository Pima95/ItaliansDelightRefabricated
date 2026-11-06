# Audit di coerenza — Italian's Delight e Serene Seasons (MC 26.2)

**Ambito:** branch `feature/orchards-1.1.0` dopo la decisione di
conservare le foglie ocra per tutto l'inverno e applicare solo un
rallentamento a basilico/cardoon coltivati.

**Riferimento upstream verificato:** `Glitchfiend/SereneSeasons`,
branch `26.2`, `SeasonalCropGrowthHandler.java`, `ModFertility.java`,
`FertilityConfig.java`, `ModTags.java`.
La regola predefinita fuori stagione è una possibilità di crescita
su sei. Le opzioni configurabili 1/2 possono impedire la crescita
o tentare di distruggere colture non protette.

## Implementato ora

1. **Fogliame invernale:** le fasi `seasonal=1` e `seasonal=2`
   di mandorlo e ciliegio puntano allo **stesso** modello autunnale.
   Le texture a rami spogli non vengono più utilizzate.
   L'ulivo rimane sempreverde e non vengono rimossi blocchi di foglie.
2. **Basilico coltivato:** tag blocco + item
   `sereneseasons:spring_crops` e `summer_crops`.
   Fuori stagione: crescita lenta gestita da Serene Seasons.
3. **Cardoon coltivato:** tag blocco + item
   `spring_crops`, `summer_crops` e `autumn_crops`.
   In inverno: crescita lenta gestita da Serene Seasons.
4. **Protezione da morte stagionale:** entrambi i blocchi
   sono nel tag `sereneseasons:unbreakable_infertile_crops`.
   Ciò evita il break quando un modpack usa
   `out_of_season_crop_behavior=2`, ma NON forza la modalità
   lenta se la configurazione globale è 1 o 2.
5. Nessuna nuova dipendenza obbligatoria, compatibilità aggiuntiva
   o modifica alle texture coltivate.

## Incoerenze / scelte ancora da decidere

### 1. Crescita dei frutti negli alberi — da armonizzare

I blocchi `OliveLeavesBlock`, `AlmondLeavesBlock`,
`FruitCherryLeavesBlock` usano `OrchardSeasonPolicy.stageCap()`:
fuori dalla finestra stagionale non avanzano e, se lo stadio
supera il tetto stagionale, i fiori e frutti tornano subito
a `stage=0` al successivo random tick. Questo è un calendario
di fruttificazione **più rigido della crescita lenta** delle
colture standard di Serene Seasons. Non è un problema di
distruzione della pianta, ma una differenza di gameplay.
**Proposta:** valutare un rallentamento della produzione
fuori stagione invece del blocco totale, oppure mantenere
le finestre botaniche come eccezione deliberata.

### 2. Serre e fertilità sotterranea non coprono gli alberi

Gli alberi interrogano direttamente `SeasonHelper`;
non utilizzano le normali regole di fertilità Serene Seasons.
Pertanto vetro-serra e coltivazione sottoterra non permettono
di anticipare la fruttificazione o derogare ai tetti degli
stadi, a differenza dei crop tradizionali.
**Proposta:** definire se la serra debba influenzare
anche ulivo, mandorlo e ciliegio, e con quali dimensioni.

### 3. Crescita dei germogli non vincolata alle stagioni

`olive_sapling`, `almond_sapling`,
`fruit_cherry_sapling` non sono elencati nei tag nativi
della fertilità Serene Seasons; i germogli possono quindi
formare alberi in qualunque stagione.
**Proposta:** assegnare periodi fertili specifici ai
germogli quando si definiranno tutte le finestre.

### 4. Basilico selvatico e cardoon selvatico

`WildBasilBlock` e `CardoonBlock` non sono `CropBlock`
con stadi di crescita: la bonemeal li moltiplica in zona.
La moltiplicazione resta attiva in tutte le stagioni,
mentre le rispettive colture su farmland rallentano.
**Proposta:** decidere se il comportamento naturale del
selvatico debba essere diverso dalla coltivazione o
se rallentare anche la diffusione con bone meal.

### 5. Stagioni e sottostagioni non hanno la stessa granularità

I tag ufficiali di Serene Seasons supportano solo
**primavera/estate/autunno/inverno**. Non esprimono
autonomamente `LATE_SPRING` o `EARLY_WINTER`.
I periodi "tarda primavera" e "inizio autunno/inverno"
sono indicazioni fenologiche, non finestre esatte
rappresentate dal tag. Per basilico e cardoon questo è
coerente con la scelta di crescita lenta fuori stagione,
ma non permette un cambio di velocità a metà stagione.

### 6. Mondi tropicali e dimensioni senza stagioni

L'integrazione dei frutti arborei ritorna al calendario
vanilla quando non vi sono quattro stagioni standard.
Le colture Serene Seasons usano invece una regola
specifica per i biomi tropicali: i crop inclusi tra
quelli estivi sono fertili. Basilico/cardoon hanno entrambi
il tag `summer_crops`. La differenza non crea crash
ma è da valutare se si desidera un calendario tropicale
personalizzato per gli alberi.

### 7. Configurazione di Serene Seasons (da comunicare)

`out_of_season_crop_behavior=0` (predefinito): crescita
rallentata a 1/6; `1`: crescita bloccata; `2`:
potenziale rottura delle colture non protette.
Il tag `unbreakable_infertile_crops` impedisce a
Serene Seasons di rompere basilico e cardoon coltivati
in modalità 2, **ma non cambia il comportamento globale
della mod configurata dall'utente**. Per ottenere la
scelta "crescono soltanto più lentamente" deve restare 0.

## Nessun cambiamento ulteriore automatico

Le decisioni non ancora approvate sono state soltanto
**documentate**. Il collaudo in gioco resta necessario
(con/senza Serene Seasons, con serra e in biomi differenti).
