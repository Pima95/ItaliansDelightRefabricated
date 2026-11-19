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
