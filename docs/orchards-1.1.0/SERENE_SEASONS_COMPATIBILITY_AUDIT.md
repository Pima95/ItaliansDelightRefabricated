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

## Stato delle precedenti incoerenze — verifica aggiornata

| Tema | Decisione attuale | Stato |
|---|---|---|
| Frutti fuori stagione | **No, all'aperto**; sì sotto vetro-serra | Risolto |
| Serre per alberi | Tag ufficiale `sereneseasons:greenhouse_glass` sopra le foglie entro 16 blocchi | Risolto |
| Sapling | Tag stagionali vanilla; ulivo tutto l'anno, mandorlo primavera/autunno, ciliegio primavera | Risolto |
| Basilico e cardoon coltivati | Rallentamento con tag nativi Serene Seasons | Già risolto |
| Piante selvatiche | Non si forzano stagioni sulla propagazione con bonemeal, come `WildCropBlock` in Farmer's Delight | Coerente |
| Granularità del calendario | **Quattro stagioni**, nessuna nuova logica di sottostagione per colture o sapling | Deciso |
| Tropicale / dimensioni senza stagioni | Mantengono il precedente fallback degli alberi; i crop usano Serene Seasons | Invariato |
| Sottosuolo per gli alberi | Non aggiunto alcun bypass sotterraneo; unica eccezione è la serra | Non richiesto |

### Note tecniche

Il calendario dei frutti arborei già implementato viene mantenuto
esattamente com'era; `maxStage()` è consultato dal tick e dal
raccoglitore lato server. Solo la **presenza del vetro-serra** consente
di oltrepassare i tetti stagionali. Il livello grafico delle foglie
diventa verde sotto vetro-serra anche in autunno/inverno affinché i
frutti rimangano visibili.

I sapling rimangono blocchi **vanilla `SaplingBlock`**. Nessuna nuova
classe che modifica i random tick o gestisce le 12 sottostagioni è stata
aggiunta. Il loro rallentamento fuori stagione e il bonus relativo
nei periodi fertili derivano dai tag `spring_crops`,
`autumn_crops` e `year_round_crops` propri di Serene Seasons.

La propagazione del cardoon e basilico selvatico è allineata a
`WildCropBlock` di Farmer's Delight Refabricated 26.2: bonemeal
diffonde piante, senza crescita progressiva stagionale.

**Configurazione da ricordare:** Serene Seasons con
`out_of_season_crop_behavior=0` rallenta a 1/6 per impostazione
predefinita; il tag `unbreakable_infertile_crops` protegge
basilico, cardoon e i tre sapling da eventuali modalità distruttive.
Non cambia le preferenze globali scelte dal giocatore.

**Da testare in Minecraft:** estensione verticale della serra,
disposizione delle chiome, raccolta fuori stagione, eventuali
modpack che alterano il tag del vetro, sapling e comportamento
senza Serene Seasons. I test GitHub sono statici e di compilazione.
