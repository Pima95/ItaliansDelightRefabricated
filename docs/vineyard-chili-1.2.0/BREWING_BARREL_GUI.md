# Botte di fermentazione — GUI e logica operativa Rustic (21/11/2026)

## Riferimento originale
- [Rustic, ContainerBrewingBarrel.java](https://github.com/cadaverous-eris/Rustic/blob/master/src/main/java/rustic/common/tileentity/ContainerBrewingBarrel.java)
- [Rustic, GuiBrewingBarrel.java](https://github.com/cadaverous-eris/Rustic/blob/master/src/main/java/rustic/client/gui/GuiBrewingBarrel.java)
- [Wiki ufficiale Rustic: Brewing](https://github.com/cadaverous-eris/Rustic/wiki/Brewing)

La texture già esistente `assets/italiansdelight/textures/gui/brewing_barrel.png` è mantenuta senza modifiche. Asset originale 176×166 ricomposto a runtime in una schermata 176×198: fascia del titolo +20 px, spazio supplementare tra macchina e inventario +12 px, bordi e slot non deformati. Tutte le 16 varianti di legno condividono l'unico `MenuType` e un unico tipo di blocco entità.

## Layout e permessi (coordinate GUI 176×198, adattate da Rustic)

| Index | Posizione | Funzione | Contenuti ammessi |
|---|---|---|---|
| 0 | 62,27 | Contenitore serbatoio input (centro) | Bottiglie/secchi con succo d'uva rossa, succo d'uva bianca o succo di mela; contenitori vuoti per il prelievo |
| 1 | 116,27 | Contenitore serbatoio output (destra) | Bottiglia di vetro o secchio vuoto, per prelevare vino rosso oppure aceto di mele |
| 2 | 26,35 | Contenitore coltura (sinistra) | Bottiglia/secchio di vino di qualità nota, oppure bottiglia/secchio vuoto per recuperare la coltura |
| 3 | 62,83 | Restituzione contenitore input | Solo prelievo |
| 4 | 116,83 | Restituzione contenitore output | Solo prelievo |
| 5 | 26,75 | Restituzione contenitore coltura | Solo prelievo |

L'inventario giocatore parte da (8,116), la hotbar da y=174. La scritta `Inventory` si trova a y=104. La fascia del titolo parte da (8,6); nomi lunghi vengono spezzati in due righe, senza abbreviare il tipo di legno. Il titolo deriva dall'ID del blocco effettivamente posizionato: ad esempio `mangrove_brewing_barrel` usa la localizzazione `block.italiansdelight.mangrove_brewing_barrel`.

Gli slot superiori sono vere posizioni d'inventario persistenti; gli slot inferiori sono **solo output**. I contenitori sono trasformati solamente se il serbatoio può accettare/fornire il liquido **e** lo slot di ritorno non è bloccato. Non vengono scartate bottiglie né duplicati oggetti quando i serbatoi sono pieni. Il tipo del succo (rosso, bianco, mela) è memorizzato per evitare miscele di liquidi diversi.

Serbatoi: ingresso centrale **8000 mB**, uscita a destra **8000 mB**, coltura a sinistra **1000 mB**. I dati sincronizzati sono **otto**, inclusi tipo di succo, tipo di prodotto e qualità. Il serbatoio di uscita contiene **o vino o aceto**, senza miscelarli. La qualità `WINE_QUALITY` si applica solo al vino. Il tipo è persistito nella nuova chiave `OutputKind`, con valore predefinito vino per conservare i salvataggi precedenti.

## Correzione rendering (21/11/2026)

- I fluidi animati (succo viola, succo bianco, vino) contengono **32 frame da 16×16**, salvati in texture 16×512 e gestiti dai relativi file `.png.mcmeta` (`frametime: 2`). Invece di deformare un singolo frame fino a 16×32, `BrewingBarrelScreen.tank` disegna tile da 16×16 **in scala 1:1**, una sopra l'altra, con scissor per mostrare solo la parte realmente piena. È aggiunta una sottile linea superficiale per riconoscere visivamente il livello anche con succhi scuri.
- Le vecchie silhouette bottiglia/secchio importate da Rustic non sono più utilizzate. I due slot ampolla ora disegnano lo **sprite ufficiale della GUI moderna del Brewing Stand** `minecraft:container/slot/potion`, recuperato dall'atlante di Minecraft. Lo slot del secchio disegna una versione attenuata del texture vanilla `minecraft:textures/item/bucket.png`. Tutti gli sprite seguono direttamente `slot.x` e `slot.y` (quadrato 16×16), quindi restano centrati sui sei slot anche quando cambiano le coordinate del menu.
- Non sono state ritoccate texture di blocchi, contenitori o liquidi, né attivata la fermentazione. Si modificano esclusivamente la visualizzazione dei serbatoi e le icone guida degli slot vuoti.
- Riferimenti: [Fabric Docs: GUI graphics](https://docs.fabricmc.net/develop/rendering/gui-graphics), [Minecraft 1.21.4: introduzione sprite separati per slot del Brewing Stand](https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-4), [GUI sprite moderno: container/slot/potion](https://mcasset.cloud/26.2-pre-3/assets/minecraft/textures/gui/sprites/container/slot).

### Verifiche client necessarie
1. Con 250 mB e 4000 mB di succo viola/bianco i pixel devono restare **quadrati**, non allungati. La superficie deve essere visibile e il livello crescere dal basso.
2. Osservare il movimento dei 32 frame della sprite (1 frame ogni 2 tick) e verificare che non si disegnino oltre i bordi.
3. Con botti vuote, le icone moderne sono integrate in `brewing_barrel.png`, rispettivamente al pixel `(176,54)` (secchio con curva interna) e `(176,70)` (ampolla), sempre a 16×16.
4. Con oggetti negli slot, i segnaposto devono sparire. La GUI, il titolo e JEI devono restare identici.
5. Lasciare il succo inserito per **12.000 tick**: deve trasformarsi in vino; osservare le frecce, le bolle e il mantenimento dei volumi.

## Rifiniture slot e leggibilità dei liquidi rossi (21/11/2026)

- **Secchio (nuova sagoma 16×16)**: contorno derivato dal secchio moderno Minecraft, con **curva interna** che scende verso il centro per mostrare la profondità (riga 6: due segmenti laterali, riga 7: segmento centrale). Il centro resta grigio uniforme come lo sfondo dello slot, senza patine, usando i medesimi colori della GUI.
- **Ampolle (grandezza reale 16×16)**: il contorno derivato dalla maschera dell'oggetto vanilla `minecraft:item/glass_bottle` (bbox x=4..12, y=2..14) è ora inserito direttamente nella **texture principale `brewing_barrel.png`**, senza file separati o ridimensionamenti. I due segnaposto usano le celle già presenti a `(176,54)` per il secchio e `(176,70)` per la bottiglia: tutto il resto dell'immagine è rimasto identico.
- **Succo d'uva viola e vino rosso**: i pixel animati trasparenti sopra al fondo scuro del serbatoio rendevano difficile distinguere le variazioni. Il metodo `tank` aggiunge un fondo opaco color vinaccia **soltanto nell'area occupata dal liquido**, delimitata dallo stesso scissor del livello; i frame animati 16×16 vengono disegnati sopra senza modificarli. Il succo bianco continua a usare la resa originale. L'animazione e la trasparenza degli asset in mondo/JEI sono invariate.
- La fermentazione è ora attiva. Rimane la verifica client dell'allineamento visivo, del contrasto a 250, 4000 e 8000 mB e delle animazioni di progresso.

## JEI (ricette attive)

La categoria JEI conserva il vecchio identificatore `brewing_barrel_gui_guide`, ma usa **l'asset originale già presente** `assets/italiansdelight/textures/gui/jei/brewing_barrel.png`, a **130×80 pixel**, senza stiramenti. Le caselle dei fluidi sono quelle di Rustic: ingresso **(40,24)**, uscita **(94,24)** e coltura opzionale **(4,32)**. Le frecce e le bolle animate sono ricavate dalle aree già presenti nello stesso PNG, non sono aggiunti file grafici. Nessun paragrafo è più disegnato sopra la GUI; le informazioni su durata e coltura sono visualizzate in tooltip.

Sono registrate **solo due ricette funzionanti**, visibili anche cliccando la freccia nella GUI e disponibili per tutte le 16 varianti di legno:
- **Succo d'uva rossa → vino**, 12.000 tick, coltura di vino facoltativa per qualità;
- **Succo di mela → aceto di mele**, 12.000 tick, nessuna coltura necessaria.

**Il succo d'uva bianca non fermenta** fino all'introduzione di un prodotto separato per il vino bianco. Rimane depositabile e prelevabile per non perdere contenuto salvato nei mondi.

## Funzionamento completo del ciclo di fermentazione (adattamento Rustic)

Il codice sorgente originale Rustic è il riferimento per le meccaniche:
- [TileEntityBrewingBarrel](https://github.com/cadaverous-eris/Rustic/blob/master/src/main/java/rustic/common/tileentity/TileEntityBrewingBarrel.java)
- [BrewingBarrelRecipe](https://github.com/cadaverous-eris/Rustic/blob/master/src/main/java/rustic/common/crafting/BrewingBarrelRecipe.java)
- [Configurazione](https://github.com/cadaverous-eris/Rustic/blob/master/src/main/java/rustic/common/Config.java)

1. Inserendo succo d'uva rossa, bianca o succo di mela nello slot centrale superiore, il serbatoio si riempie in unità da 250 mB (bottiglia) o 1000 mB (secchio). Liquidi diversi non si mescolano. I contenitori vuoti tornano nello slot inferiore.
2. Il barile fermenta automaticamente per **12.000 tick = 10 minuti a 20 TPS** soltanto se l'ingresso contiene succo d'uva rossa o succo di mela, l'uscita dispone di spazio e il tipo di prodotto corrisponde. Qualunque trasferimento effettivo nei serbatoi azzera il timer, come in Rustic. Il succo bianco rimane fermo, senza produrre vino rosso.
3. Al termine viene trasformata la quantità `min(ingresso, 8000 - uscita)` mB in un'unica operazione. L'uva rossa produce `italiansdelight:wine`; la mela produce `italiansdelight:apple_cider_vinegar`, già registrato nella mod, usando le sue bottiglie e i suoi secchi. Se l'uscita contiene un prodotto diverso, la ricetta non parte e non viene perso liquido. **Nessun vino bianco viene generato per errore.**
4. La qualità di un serbatoio di vino prima vuoto è casuale **5–75%** in assenza di coltura, come l'originale `5 + nextInt(71)`. Con una coltura di vino disponibile, la qualità del nuovo lotto è `clamp(qualità coltura + casuale[-1,+4], 0, 100)`; la coltura non si consuma durante la fermentazione e può servire a generazioni successive.
5. Una coltura può essere aggiunta o recuperata con bottiglie da 250 mB e secchi da 1000 mB nell'ingresso sinistro. Non si mescolano nello stesso serbatoio colture di qualità diverse. Se l'uscita contiene già vino, Rustic **mantiene la qualità del lotto già presente** anche quando si aggiunge altro vino.
6. Bottiglie di vetro e secchi vuoti prelevano rispettivamente 250 e 1000 mB dal serbatoio destro, ottenendo vino o aceto coerentemente con il prodotto presente. Il vino conserva `WINE_QUALITY` su ogni recipiente; l'aceto non ha qualità. Gli slot inferiori sono sempre di sola estrazione e i trasferimenti non avvengono quando il relativo ritorno è bloccato.

### Compatibilità salvataggi

Sono mantenute le chiavi NBT `JuiceAmount`, `JuiceKind`, `WineAmount`, `WineQuality`, `CultureQuality`, `BrewTime` e l'inventario dei sei slot. **`WineAmount` continua a essere la chiave storica del volume in uscita**, anche per l'aceto, evitando modifiche incompatibili ai salvataggi. `OutputKind` (0=vino, 1=aceto) distingue il prodotto, con 0 per i mondi precedenti. La nuova chiave `CultureAmount` conserva i mB; negli attuali mondi già salvati, se è presente `CultureQuality` ma manca `CultureAmount`, la coltura viene recuperata come **250 mB** senza perdere qualità. I valori sono limitati alla capacità in lettura.

### Conservate dal prototipo GUI
- Apertura con click destro tenendo qualunque oggetto o a mano vuota (non più accesso diretto ai serbatoi bypassando la GUI).
- Visualizzazione del vecchio asset GUI, icone segnaposto originali, indicatori animati di serbatoio e tooltip quantità.
- Shift-click fra GUI e inventario del giocatore, restrizioni slot rispettate anche lato server.
- Salvataggio, ricaricamento e conservazione dell'inventario e dei vecchi serbatoi quando la botte è raccolta come blocco.
- La fermentazione ora riproduce tempo, qualità, gestione coltura e svuotamento/riempimento di Rustic.
- Restano fuori da questo intervento la creazione di bevande non ancora registrate (idromele, sidro, ecc.), effetti alcolici sull'utilizzatore e l'eventuale distinta ricetta di vino bianco.

## Test manuali consigliati in Minecraft
1. Aprire botti di legni diversi con mano vuota e mano occupata; controllare i sei slot e gli indicatori.
2. Depositare succo viola nel centro (bottiglia e secchio), ritirare il contenitore vuoto dalla casella sotto; provare a riempire il serbatoio pieno: non deve perdere il contenitore.
3. Inserire succo bianco in una botte vuota separata; provare ad aggiungere viola alla stessa botte: la miscela deve essere rifiutata senza perdite.
4. Provare l'inserimento manuale e lo shift-click; i tre slot inferiori non devono accettare nulla.
5. Rompere/riposizionare la botte e riaprire la GUI: contenitori e livelli devono rimanere salvati.
6. Lasciare 12.000 tick una botte con succo d'uva rossa: verificare produzione di vino, frecce e bolle animate, qualità e bottiglie. Ripetere con succo di mela: deve produrre aceto; la qualità non va mostrata.
7. Aggiungere una coltura di 250 mB e testare cinque cicli successivi; non deve consumarsi durante la fermentazione. La qualità dei vini nuovi deve restare nel limite di ±1/+4 punti rispetto alla coltura.
8. Riempire il serbatoio di uscita fino a 8000 mB e lasciare del succo nell'ingresso: niente perdita, niente fermentazione mentre il vino è pieno. Dopo il prelievo, deve avviarsi un nuovo ciclo.
9. In un mondo con una vecchia botte già contenente vino o coltura, verificare che il recupero tramite bottiglia funzioni e mantenga la qualità. La vecchia coltura senza volume deve riapparire come 250 mB.
10. Confermare che JEI mostri **rosso→vino e mela→aceto** sopra l'asset originale 130×80, senza testi sovrapposti, e tutti i 16 catalizzatori. Con succo bianco controllare che il progresso resti fermo.

Questa versione è un'implementazione nuova per Fabric/Minecraft 26.3, non un porting dei file Java Forge 1.12 di Rustic.
