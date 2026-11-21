# Botte di fermentazione — GUI Rustic (fase interfaccia, 18/11/2026)

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
| 2 | 26,35 | Contenitore coltura (sinistra) | Bottiglia di vino già esistente, oppure bottiglia di vetro vuota per recuperarla |
| 3 | 62,83 | Restituzione contenitore input | Solo prelievo |
| 4 | 116,83 | Restituzione contenitore output | Solo prelievo |
| 5 | 26,75 | Restituzione contenitore coltura | Solo prelievo |

L'inventario giocatore parte da (8,116), la hotbar da y=174. La scritta `Inventory` si trova a y=104. La fascia del titolo parte da (8,6); nomi lunghi vengono spezzati in due righe, senza abbreviare il tipo di legno. Il titolo deriva dall'ID del blocco effettivamente posizionato: ad esempio `mangrove_brewing_barrel` usa la localizzazione `block.italiansdelight.mangrove_brewing_barrel`.

Gli slot superiori sono vere posizioni d'inventario persistenti; gli slot inferiori sono **solo output**. I contenitori sono trasformati solamente se il serbatoio può accettare/fornire il liquido **e** lo slot di ritorno non è bloccato. Non vengono scartate bottiglie né duplicati oggetti quando i serbatoi sono pieni. La selezione del succo bianco/viola è memorizzata per impedire miscele fra le due varietà.

Serbatoi: succo centrale **8000 mB**, vino destro **8000 mB**, coltura sinistra **250 mB**. GUI e progressi sincronizzati tramite `ContainerData`. Il serbatoio destro mostra e permette di recuperare **solo vino già memorizzato** nei vecchi salvataggi; non lo crea. La coltura conserva la qualità del vino quando viene depositata e recuperata.

## Correzione rendering (21/11/2026)

- I fluidi animati (succo viola, succo bianco, vino) contengono **32 frame da 16×16**, salvati in texture 16×512 e gestiti dai relativi file `.png.mcmeta` (`frametime: 2`). Invece di deformare un singolo frame fino a 16×32, `BrewingBarrelScreen.tank` disegna tile da 16×16 **in scala 1:1**, una sopra l'altra, con scissor per mostrare solo la parte realmente piena. È aggiunta una sottile linea superficiale per riconoscere visivamente il livello anche con succhi scuri.
- Le vecchie silhouette bottiglia/secchio importate da Rustic non sono più utilizzate. I due slot ampolla ora disegnano lo **sprite ufficiale della GUI moderna del Brewing Stand** `minecraft:container/slot/potion`, recuperato dall'atlante di Minecraft. Lo slot del secchio disegna una versione attenuata del texture vanilla `minecraft:textures/item/bucket.png`. Tutti gli sprite seguono direttamente `slot.x` e `slot.y` (quadrato 16×16), quindi restano centrati sui sei slot anche quando cambiano le coordinate del menu.
- Non sono state ritoccate texture di blocchi, contenitori o liquidi, né attivata la fermentazione. Si modificano esclusivamente la visualizzazione dei serbatoi e le icone guida degli slot vuoti.
- Riferimenti: [Fabric Docs: GUI graphics](https://docs.fabricmc.net/develop/rendering/gui-graphics), [Minecraft 1.21.4: introduzione sprite separati per slot del Brewing Stand](https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-4), [GUI sprite moderno: container/slot/potion](https://mcasset.cloud/26.2-pre-3/assets/minecraft/textures/gui/sprites/container/slot).

### Verifiche client necessarie
1. Con 250 mB e 4000 mB di succo viola/bianco i pixel devono restare **quadrati**, non allungati. La superficie deve essere visibile e il livello crescere dal basso.
2. Osservare il movimento dei 32 frame della sprite (1 frame ogni 2 tick) e verificare che non si disegnino oltre i bordi.
3. Con botti vuote, lo slot del secchio usa l'icona attuale di Minecraft e i due slot bottiglia usano la sagoma della Brewing Stand moderna, tutti centrati sui 16×16 pixel.
4. Con oggetti negli slot, i segnaposto devono sparire. La GUI, il titolo e JEI devono restare identici.
5. Lasciare il succo inserito per molti tick: non deve essere creato vino.

## Rifiniture slot e leggibilità dei liquidi rossi (21/11/2026)

- **Secchio**: eliminato il disegno dell'item `minecraft:textures/item/bucket.png` con tinta `0x88888888` che sembrava una patina; ripristinata esclusivamente la silhouette vuota, già presente nel PNG della GUI Rustic ai pixel `(176,54)`. L'interno resta trasparente.
- **Ampolle**: si mantiene lo sprite moderno vanilla `minecraft:container/slot/potion`, ma adesso viene disegnato in un'area 14×14, con 1px di margine interno e una compensazione ottica di +1px in orizzontale. Le coordinate si ricavano dagli slot effettivi del menu e quindi restano valide per tutte le 16 varianti.
- **Succo d'uva viola e vino rosso**: i pixel animati trasparenti sopra al fondo scuro del serbatoio rendevano difficile distinguere le variazioni. Il metodo `tank` aggiunge un fondo opaco color vinaccia **soltanto nell'area occupata dal liquido**, delimitata dallo stesso scissor del livello; i frame animati 16×16 vengono disegnati sopra senza modificarli. Il succo bianco continua a usare la resa originale. L'animazione e la trasparenza degli asset in mondo/JEI sono invariate.
- Nessuna logica di fermentazione abilitata. Rimangono da verificare nel client allineamento visivo e contrasto a 250, 4000 e 8000 mB.

## JEI (guida, non ricetta)

È stata introdotta una categoria informativa `brewing_barrel_gui_guide`, con una pagina che descrive gli slot realmente disponibili e segnala la fermentazione disattivata. Tutte le 16 varianti dei barili sono registrate come stazioni JEI; il clic sull'area della freccia (posizione adattata) apre la guida. La categoria non presenta output di vino, non registra ricette di produzione e non abilita gameplay inesistente.

## Funzionalità deliberate per la fase 1
- Apertura con click destro tenendo qualunque oggetto o a mano vuota (non più accesso diretto ai serbatoi bypassando la GUI).
- Visualizzazione del vecchio asset GUI, icone segnaposto originali, indicatori animati di serbatoio e tooltip quantità.
- Shift-click fra GUI e inventario del giocatore, restrizioni slot rispettate anche lato server.
- Salvataggio, ricaricamento e conservazione dell'inventario e dei vecchi serbatoi quando la botte è raccolta come blocco.
- **Nessuna fermentazione**: `serverTick` non esegue ricette, non fa avanzare il timer e non trasforma il succo in vino. I valori di vino/qualità già salvati sono mantenuti.
- Da progettare in una fase successiva: ricette per vino rosso/bianco, tempi, quantità, qualità del prodotto, compatibilità di trasporto fluidi ulteriori e automatismi.

## Test manuali consigliati in Minecraft
1. Aprire botti di legni diversi con mano vuota e mano occupata; controllare i sei slot e gli indicatori.
2. Depositare succo viola nel centro (bottiglia e secchio), ritirare il contenitore vuoto dalla casella sotto; provare a riempire il serbatoio pieno: non deve perdere il contenitore.
3. Inserire succo bianco in una botte vuota separata; provare ad aggiungere viola alla stessa botte: la miscela deve essere rifiutata senza perdite.
4. Provare l'inserimento manuale e lo shift-click; i tre slot inferiori non devono accettare nulla.
5. Rompere/riposizionare la botte e riaprire la GUI: contenitori e livelli devono rimanere salvati.
6. Lasciare 12000 tick una botte piena di succo: il serbatoio del vino deve rimanere a zero.
7. In un mondo con vecchia botte già contenente vino, verificare che il recupero tramite bottiglia funzioni e mantenga la qualità.

Questa versione è un'implementazione nuova per Fabric/Minecraft 26.3, non un porting dei file Java Forge 1.12 di Rustic.
