# Botte di fermentazione — GUI e logica operativa Rustic (21/11/2026)

## Riferimento originale
- [Rustic, ContainerBrewingBarrel.java](https://github.com/cadaverous-eris/Rustic/blob/master/src/main/java/rustic/common/tileentity/ContainerBrewingBarrel.java)
- [Rustic, GuiBrewingBarrel.java](https://github.com/cadaverous-eris/Rustic/blob/master/src/main/java/rustic/client/gui/GuiBrewingBarrel.java)
- [Wiki ufficiale Rustic: Brewing](https://github.com/cadaverous-eris/Rustic/wiki/Brewing)

La texture già esistente `assets/italiansdelight/textures/gui/brewing_barrel.png` è mantenuta senza modifiche. Asset originale 176×166 ricomposto a runtime in una schermata 176×198: fascia del titolo +20 px, spazio supplementare tra macchina e inventario +12 px, bordi e slot non deformati. Tutte le 16 varianti di legno condividono l'unico `MenuType` e un unico tipo di blocco entità.

## Layout e permessi (coordinate GUI 176×198, adattate da Rustic)

| Index | Posizione | Funzione | Contenuti ammessi |
|---|---|---|---|
| 0 | 62,27 | Contenitore serbatoio input (centro) | Bottiglia/secchio con succo d'uva viola o bianca; bottiglia e secchio vuoti per prelievo |
| 1 | 116,27 | Contenitore serbatoio output (destra) | Solo bottiglia di vetro vuota |
| 2 | 26,35 | Contenitore coltura (sinistra) | Bottiglia/secchio di vino di qualità nota, oppure bottiglia/secchio vuoto per recuperare la coltura |
| 3 | 62,83 | Restituzione contenitore input | Solo prelievo |
| 4 | 116,83 | Restituzione contenitore output | Solo prelievo |
| 5 | 26,75 | Restituzione contenitore coltura | Solo prelievo |

L'inventario giocatore parte da (8,116), la hotbar da y=174. La scritta `Inventory` si trova a y=104. La fascia del titolo parte da (8,6); nomi lunghi vengono spezzati in due righe, senza abbreviare il tipo di legno. Il titolo deriva dall'ID del blocco effettivamente posizionato: ad esempio `mangrove_brewing_barrel` usa la localizzazione `block.italiansdelight.mangrove_brewing_barrel`.

Gli slot superiori sono vere posizioni d'inventario persistenti; gli slot inferiori sono **solo output**. I contenitori sono trasformati solamente se il serbatoio può accettare/fornire il liquido **e** lo slot di ritorno non è bloccato. Non vengono scartate bottiglie né duplicati oggetti quando i serbatoi sono pieni. La selezione del succo bianco/viola è memorizzata per impedire miscele fra le due varietà.

Serbatoi: succo centrale **8000 mB**, vino destro **8000 mB**, coltura sinistra **1000 mB** come Rustic. GUI, progresso, quantità e qualità dei serbatoi sono sincronizzati tramite sette valori di `ContainerData`. Il serbatoio destro mostra il vino prodotto e consente di estrarlo nelle bottiglie, che conservano la qualità in `WINE_QUALITY`. La coltura conserva sia i mB sia la propria qualità quando viene depositata o recuperata.

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

La categoria JEI esistente `brewing_barrel_gui_guide` (identificatore conservato per evitare regressioni) mostra ora **due ricette vere**: succo d'uva viola o bianca → vino. L'anteprima usa i fluidi registrati e il volume massimo di 8000 mB. Tutte le 16 varianti dei barili sono stazioni JEI; il clic sulla freccia apre entrambe le ricette. Il testo distingue la coltura facoltativa dalla quantità di succo obbligatoria.

## Funzionamento completo del ciclo di fermentazione (adattamento Rustic)

Il codice sorgente originale Rustic è il riferimento per le meccaniche:
- [TileEntityBrewingBarrel](https://github.com/cadaverous-eris/Rustic/blob/master/src/main/java/rustic/common/tileentity/TileEntityBrewingBarrel.java)
- [BrewingBarrelRecipe](https://github.com/cadaverous-eris/Rustic/blob/master/src/main/java/rustic/common/crafting/BrewingBarrelRecipe.java)
- [Configurazione](https://github.com/cadaverous-eris/Rustic/blob/master/src/main/java/rustic/common/Config.java)

1. Inserendo succo d'uva viola/bianca nello slot centrale superiore, il serbatoio si riempie in unità da 250 mB (bottiglia) o 1000 mB (secchio). Un singolo serbatoio non miscela succhi viola e bianchi; i contenitori vuoti tornano nello slot inferiore.
2. Se c'è almeno del succo e il serbatoio del vino non è pieno, il barile fermenta automaticamente per **12.000 tick = 10 minuti reali a 20 TPS**, indipendentemente dalla quantità. Qualsiasi trasferimento effettivo di contenitore in uno dei tre serbatoi azzera il timer, come in Rustic.
3. Al termine vengono trasformati in vino `min(succo presente, 8000 - vino presente)` mB in un'unica operazione. Il liquido non scompare quando l'uscita è piena; si riavvia un ciclo solo se rimangono succo e spazio. Il vino prodotto è lo stesso tipo registrato `italiansdelight:wine`, anche partendo dal succo bianco: **non esiste ancora un oggetto `white_wine` distinto**.
4. La qualità di un serbatoio di vino prima vuoto è casuale **5–75%** in assenza di coltura, come l'originale `5 + nextInt(71)`. Con una coltura di vino disponibile, la qualità del nuovo lotto è `clamp(qualità coltura + casuale[-1,+4], 0, 100)`; la coltura non si consuma durante la fermentazione e può servire a generazioni successive.
5. Una coltura può essere aggiunta o recuperata con bottiglie da 250 mB e secchi da 1000 mB nell'ingresso sinistro. Non si mescolano nello stesso serbatoio colture di qualità diverse. Se l'uscita contiene già vino, Rustic **mantiene la qualità del lotto già presente** anche quando si aggiunge altro vino.
6. Bottiglie vuote nell'ingresso destro prelevano 250 mB di vino; la qualità viene salvata nel componente `WINE_QUALITY` della singola bottiglia. Gli slot inferiori sono sempre di sola estrazione; se il contenitore di ritorno non può essere collocato, il trasferimento non avviene.

### Compatibilità salvataggi

Sono mantenute le chiavi NBT `JuiceAmount`, `JuiceKind`, `WineAmount`, `WineQuality`, `CultureQuality`, `BrewTime` e l'inventario dei sei slot. La nuova chiave `CultureAmount` conserva i mB; negli attuali mondi già salvati, se è presente `CultureQuality` ma manca `CultureAmount`, la coltura viene recuperata come **250 mB** senza perdere qualità. I valori sono limitati alla capacità in lettura.

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
6. Lasciare 12.000 tick una botte con succo: verificare la produzione della stessa quantità di vino, frecce e bolle animate, qualità visibile e bottiglie prodotte con il componente qualità.
7. Aggiungere una coltura di 250 mB e testare cinque cicli successivi; non deve consumarsi durante la fermentazione. La qualità dei vini nuovi deve restare nel limite di ±1/+4 punti rispetto alla coltura.
8. Riempire il serbatoio di uscita fino a 8000 mB e lasciare del succo nell'ingresso: niente perdita, niente fermentazione mentre il vino è pieno. Dopo il prelievo, deve avviarsi un nuovo ciclo.
9. In un mondo con una vecchia botte già contenente vino o coltura, verificare che il recupero tramite bottiglia funzioni e mantenga la qualità. La vecchia coltura senza volume deve riapparire come 250 mB.
10. Confermare che JEI mostri succo viola e bianco come due ricette, l'uscita vino e i 16 catalizzatori.

Questa versione è un'implementazione nuova per Fabric/Minecraft 26.3, non un porting dei file Java Forge 1.12 di Rustic.
