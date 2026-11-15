# Bevibilità delle bottiglie e compatibilità con la sete (Minecraft 26.3)

Stato al 15 novembre 2026: **cinque nuove bottiglie bevibili**.

| Bottiglia | Bevibile | Funzionamento |
| --- | --- | --- |
| `sheep_milk_bottle` | Sì, preesistente | ConsumableItem e Milk Bottle di Farmer's Delight |
| `apple_juice_bottle` | Sì | Succo senza effetti aggiuntivi |
| `golden_apple_juice_bottle` | Sì | Rigenerazione II per 5 secondi e Assorbimento I per 2 minuti, come la mela d'oro normale |
| `grape_juice_bottle` | Sì | Succo d'uva rossa (ID storico) |
| `white_grape_juice_bottle` | Sì | Succo d'uva bianca, fluido separato |
| `olive_oil_bottle` | Sì | Bevibile, ma **non disseta** |
| `wine_bottle` | No | Mantiene qualità, effetti ancora da definire |
| `whey_bottle` | No | Siero, ingrediente |
| `rennet` | No | Caglio, ingrediente |
| `apple_cider_vinegar` | No | Aceto, ingrediente |
| `cherry_jam_bottle` | No | Marmellata, ingrediente |

La registrazione delle bottiglie usa lo stesso `ConsumableItem` di Farmer's Delight: restituisce un contenitore di vetro per consumo, anche dalle pile, e conserva i provider Fabric Fluid Transfer per macchine/barili/calderoni. I secchi non vengono resi bevibili.

## Compatibilità opzionale con la sete

Farmer's Delight Refabricated usa il tag condiviso `c:drinks`. Rustic Delight usa `Consumables.defaultDrink()` per i liquidi consumabili, compreso l'olio da cucina, ma l'olio non disseta in Thirst Was Taken 2. Quindi:
- Tag `c:drinks`: aggiunti i quattro succhi; olio escluso per non farlo risultare dissetante.
- Tag `c:drink_containing/bottle`: classificazione delle cinque bottiglie.
- **Thirst Was Taken 2 (Fabric MC 26.3)**: supporto senza dipendenza opzionale in `data/italiansdelight/thirstwastaken2/drinks/italiansdelight_beverages.json`. I succhi ristorano 6 punti sete e 4 quenched; latte di pecora 4/0; olio 0/0. Le configurazioni e blacklist del server hanno precedenza sui datapack.
- **Dehydration**: i documenti della mod pubblicata coprono solo Minecraft fino a 1.21.1, non 26.3. Non si introduce un'integrazione Java non compatibile; una versione portata a 26.3 richiederà una verifica dedicata.

Senza mod della sete le bottiglie funzionano normalmente e non mostrano alcuna barra aggiuntiva.

## Verifiche client richieste

1. Consumare ogni succo e olio anche a fame piena. Controllare una bottiglia vuota restituita per porzione, anche se lo stack contiene più bottiglie.
2. Verificare il succo dorato con effetti Rigenerazione II (100 tick) e Assorbimento I (2400 tick).
3. Testare travasi in entrambi i sensi con botti, calderoni e Fabric FluidStorage, anche dopo salvataggio/ricarica.
4. Con Thirst Was Taken 2 installato verificare i valori configurati, il comportamento dell'olio e la priorità delle configurazioni server.
5. Controllare il nuovo sprite animato 16×512 del succo d'uva bianca (32 fotogrammi da 16×16 e frame time 2).
