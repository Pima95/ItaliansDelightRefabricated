# Ciliegio da frutto — Fase A: derivati edilizi

**Branch:** `feature/orchards-1.1.0`
**Stato:** sei blocchi implementati, test manuali in Minecraft da eseguire.
**Texture:** unicamente `fruit_cherry_planks.png` già approvata, senza nuovi PNG.
Le cinque texture base del legno sono protette da checksum e non modificate.

## Blocchi e ricette
- `fruit_cherry_stairs`: scale, 4 unità da 6 assi.
- `fruit_cherry_slab`: lastre, 6 unità da 3 assi; lastra doppia restituisce 2 unità.
- `fruit_cherry_fence`: recinzione, 3 unità da 4 assi e 2 bastoni.
- `fruit_cherry_fence_gate`: cancelletto, 1 unità da 2 assi e 4 bastoni.
- `fruit_cherry_button`: pulsante, 1 unità da 1 asse.
- `fruit_cherry_pressure_plate`: pedana, 1 unità da 2 assi.

Il comportamento e i modelli sono basati sulle varianti vanilla già usate
per il mandorlo, adattate all'identificatore `fruit_cherry_*`.
Sono inclusi registrazioni, tag block/item, axe mining, loot,
inventario Creativa e combustibilità.

## Collaudo in Minecraft da effettuare
- [ ] Tutti e sei i blocchi appaiono una volta in Creative.
- [ ] Ricette con quantità corrette, inclusi recipe book e JEI.
- [ ] Scale in quattro orientamenti, gradini normali e angoli.
- [ ] Lastre alte, basse, doppie; drop 2 per lastra doppia.
- [ ] Recinzioni connesse su quattro lati e agli altri recinti.
- [ ] Cancelli aperti e chiusi, anche tramite redstone.
- [ ] Pulsanti e pedane: durata e suoni da legno, segnali redstone.
- [ ] Rottura e posizionamento; efficacia ascia e comportamento al fuoco.
- [ ] Rendering su blocchi adiacenti, test server e salvataggio mondo.

La compilazione e i controlli statici non sostituiscono il collaudo in gioco.
Prossimo lotto dopo approvazione: porta e botola di ciliegio da frutto.
