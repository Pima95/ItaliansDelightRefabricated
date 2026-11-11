# Minecraft 26.3 — Pioppo (Poplar): varianti Italian's Delight

**Branch:** `feature/mc-26.3`  
**Stato:** registrazioni, risorse, texture e ricette aggiunte; **porting complessivo 26.3 ancora da eseguire**.

Minecraft 26.3 introduce le assi e i derivati `minecraft:poplar_*`.
Sono stati aggiunti i tre blocchi che la mod produce per ciascuna famiglia
di legno disponibile:

| Identificatore | Uso | Texture |
| --- | --- | --- |
| `italiansdelight:poplar_cheese_aging_rack` | Rack di stagionatura a due ripiani | Direttamente `minecraft:block/poplar_planks`; nessuna PNG aggiuntiva |
| `italiansdelight:poplar_crushing_tub` | Tino di pigiatura; processi già esistenti | `textures/block/crushing_tub_poplar.png` |
| `italiansdelight:poplar_barrel` | Barile per fluidi; 16.000 mB | `textures/block/barrel_poplar.png` |

Le nuove PNG sono **16×16 RGBA**, con alpha **255** per ogni pixel;
le fasce e gli angoli metallici dei modelli esistenti sono preservati.
Le sfumature del legno sono state adattate alla famiglia chiara e grigio-calda
del pioppo, mantenendo i modelli originali di Crushing Tub e Barrel.

Sono presenti anche blockstate, modelli blocco/item, crafting, loot,
traduzioni it/en, registri Java, tipi block entity, inventory Creativa,
tag ascia/barili e JEI per il nuovo Crushing Tub.

### Altri derivati già presenti altrove

- Il **Poplar Shelf** è un blocco vanilla Minecraft 26.3.
- Il **Poplar Cabinet** è già incluso in Farmer's Delight Refabricated 26.3
  (introdotto nella versione 3.6.21, incluso nella 3.6.30).

Non aggiungiamo duplicati alla mod.

### Come verificare gli asset

Dalla radice del repository:

```powershell
git fetch origin
git switch feature/mc-26.3
git pull --ff-only origin feature/mc-26.3
py -3 .github/scripts/check_poplar_26_3.py
```

Lo script richiede Python 3 ma **non** librerie aggiuntive.

**Importante:** la base Gradle di questo branch è ancora Minecraft 26.2.
Prima di poter provare i nuovi blocchi in gioco è necessario completare
il porting delle API, mappature e dipendenze a 26.3.
La registrazione usa correttamente `Blocks.POPLAR_PLANKS`, che **non esiste
in Minecraft 26.2**: è normale che la compilazione attuale non possa
ancora riuscire. Non eseguire test di gioco finché la base non sarà portata.

Dopo il porting, collaudare client e server, le tre ricette, modelli e
texture, il rack con formaggi freschi/stagionati, il Crushing Tub con i
processi esistenti, riempimento/svuotamento di liquidi, recupero, contenuto
persistente dopo un riavvio e JEI. Nessun merge in `main` senza approvazione.
