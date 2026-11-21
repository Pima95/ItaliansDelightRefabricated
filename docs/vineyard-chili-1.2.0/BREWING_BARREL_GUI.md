# Brewing Barrel 1.2.0 — GUI Rustic e sette prodotti fermentati

Aggiornato il 21/11/2026. Ramo `feature/vineyard-chili-1.2.0`.

## Regole comuni

- Tutte le 16 varianti di legno usano il medesimo `BrewingBarrelBlockEntity` e la stessa GUI 176×198, derivata dall'asset Rustic già approvato. Il titolo usa il nome localizzato del singolo tipo di legno.
- Inventario: tre slot di ingresso superiori (centro input, destra output, sinistra coltura) e tre slot di ritorno inferiori, solo per estrazione. Contenitori: 250 mB per bottiglia, 1000 mB per secchio; ogni trasferimento è atomico e controlla lo spazio nello slot di ritorno.
- Serbatoi: **8000 mB ingresso**, **8000 mB uscita**, **1000 mB coltura**. Una fermentazione richiede **12000 tick**, circa 10 minuti a 20 TPS; il contenuto prodotto è `min(ingresso, 8000 - uscita)` mB. Il serbatoio di uscita non mescola prodotti diversi.
- La coltura sinistra riceve vino rosso in bottiglia o secchio e ne memorizza la qualità; resta presente tra un lotto e l'altro. In assenza di coltura, la qualità iniziale di una bevanda fermentata è 5–75%; con coltura varia tra `qualità della coltura - 1` e `+ 4` (limitata a 0–100). Gli aceti non hanno qualità.
- L'aggiunta, l'estrazione o la rimozione della coltura riavvia il timer secondo il comportamento Rustico. Il vino già presente nel serbatoio conserva la propria qualità anche se viene aggiunto un nuovo lotto compatibile.

## Ricette reali nel Brewing Barrel

| Ingresso centrale | Coltura sinistra | Uscita destra | ID liquido prodotto |
|---|---|---|---|
| Succo d'uva rossa | Opzionale | Vino rosso | `italiansdelight:wine` |
| Succo d'uva bianca | Opzionale | Vino bianco | `italiansdelight:white_wine` |
| Succo di ciliegie | Opzionale | Vino di ciliegie | `italiansdelight:cherry_wine` |
| Miele (bottiglie/secchi) | Opzionale | Idromele | `italiansdelight:mead` |
| Succo di mela | **Almeno 250 mB** | Sidro | `italiansdelight:cider` |
| Succo di mela | **Vuota** | Aceto di mele (esistente) | `italiansdelight:apple_cider_vinegar` |
| Sidro | Qualsiasi | Aceto di mele | `italiansdelight:apple_cider_vinegar` |
| Vino rosso | Qualsiasi | Aceto di vino rosso | `italiansdelight:red_wine_vinegar` |

**Selezione sidro/aceto:** il succo di mela non inoculato continua a produrre aceto, preservando la ricetta già accettata. Per il sidro inserire prima una bottiglia di vino nella coltura (slot sinistro alto). Prima di un successivo lotto d'aceto, rimuovere la coltura con una bottiglia vuota. Il timer si azzera alla variazione.

Il miele resta il liquido virtuale `minecraft:honey` già usato nel Crushing Tub: non è stato inventato un fluido-mondo di miele. Il succo di ciliegie è un nuovo fluido non posizionabile, ottenuto pestando `italiansdelight:cherries` nel Crushing Tub (250 mB per frutto), con bottiglie/secchi di recupero.

## Asset e rendering

- Grafica JEI originale **130×80**: `assets/italiansdelight/textures/gui/jei/brewing_barrel.png` con frecce e bolle animate; nessun testo descrittivo sovrapposto.
- GUI in gioco: `assets/italiansdelight/textures/gui/brewing_barrel.png`; mantiene icone secchio/ampolla moderne già approvate. Animazioni native 16×16 ripetute verticalmente con scissor, senza stirare i pixel.
- **Già esistente nel repository:** texture `cider_still/flow` proveniente da Rustic, reimpiegata per il nuovo sidro.
- **Importati dal catalogo animato originale Rustic** (still e flow e rispettivi `.mcmeta`): `white_wine` (stile ambrosia chiara), `cherry_wine` (wildberry wine), `mead`, `cherry_juice` (wildberry juice), `red_wine_vinegar` (stile ale ambrato). I file vengono conservati in `textures/block/fluids/booze`.
- Bottiglie: modello vanilla delle pozioni con tinta costante per distinguere visivamente i vari prodotti, senza ridisegnare la texture Minecraft. Secchi: modelli che riusano i PNG già presenti (succo d'uva bianca, vino, miele, succo di mela, aceto di mele e succo d'uva rossa). Gli ID degli oggetti sono unici e sono tradotti in italiano e inglese.

Riferimenti upstream: [Rustic repository](https://github.com/cadaverous-eris/Rustic), [TileEntityBrewingBarrel.java](https://github.com/cadaverous-eris/Rustic/blob/master/src/main/java/rustic/common/tileentity/TileEntityBrewingBarrel.java), [BrewingBarrelRecipe.java](https://github.com/cadaverous-eris/Rustic/blob/master/src/main/java/rustic/common/crafting/BrewingBarrelRecipe.java).

## Utilità in cucina

Sono state aggiunte **16 ricette alternative di crafting** reali (categoria `misc`), almeno due per ogni nuova bevanda e per i prodotti precedenti, con restituzione del contenitore vuoto:
- Vino bianco → risotto ai funghi e risotto al Parmigiano.
- Vino di ciliegie → crostata di ciliegie e toast con marmellata di ciliegie.
- Idromele → torta di mandorle e crostata di ciliegie.
- Sidro → torta di mandorle e crostata di ciliegie.
- Aceto di vino rosso → insalata di mozzarella e bruschetta alle olive.
- Succo di ciliegie → crostata di ciliegie e toast con marmellata.
- Vino rosso → pasta all'amatriciana e risotto ai funghi.
- Aceto di mele → insalata di mozzarella e bruschetta alle olive.

Sono varianti per **prodotti esistenti**, non duplicati di cibi con nuove icone. Ogni recipe ha ID distinto in `data/italiansdelight/recipe/fermented`.

## Salvataggi e compatibilità

- `JuiceKind` conserva gli ID storici **0 rosso, 1 bianco, 2 mela**; aggiunge 3 ciliegia, 4 miele, 5 vino rosso, 6 sidro.
- `OutputKind` conserva **0 vino rosso, 1 aceto di mele**, aggiunge 2 vino bianco, 3 vino ciliegie, 4 idromele, 5 sidro, 6 aceto di vino rosso.
- Rimangono le chiavi storiche `JuiceAmount`, `WineAmount` (ora contenuto generico in uscita), `WineQuality` (per bevande fermentate), `CultureAmount`, `CultureQuality`, `BrewTime`. Tutti i valori sono limitati in lettura alla capacità.
- I vecchi barili privi di `OutputKind` restano vino rosso. Le colture già presenti senza `CultureAmount` sono migrate come una bottiglia da 250 mB. L'inventario persistente dei sei slot non è modificato.

## Test richiesti nel client

1. Inserire ogni succo o miele in bottiglia/secchio; recuperare il contenitore vuoto senza perdite o duplicazioni.
2. Fermentare ciascuno degli otto percorsi; verificare JEI, colori delle animazioni, tempi, output e nomi degli oggetti.
3. Senza coltura: **mela→aceto**; con coltura: **mela→sidro**; poi usare sidro→aceto.
4. Fermentare vino rosso→aceto di vino rosso, assicurandosi che nessuna qualità alcolica sia mostrata sull'aceto.
5. Tentare di miscelare prodotti diversi nel serbatoio d'uscita: deve bloccare la fermentazione senza perdere liquido.
6. Provare tutte le ricette alternative di cucina, controllando il ritorno dei contenitori vuoti e l'assenza di recipe book errors.
7. Rompere e riposizionare una botte con succo, vino e coltura: deve mantenere volumi, progressi, qualità e identità dei prodotti.
