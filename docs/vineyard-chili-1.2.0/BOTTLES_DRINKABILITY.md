# Bottiglie: revisione bevibilità (Minecraft 26.3)

Stato al 15 novembre 2026: **non abilitiamo nuove bevande** in questa modifica.

| Oggetto | Consumabile nel codice attuale | Note |
| --- | --- | --- |
| `sheep_milk_bottle` | Sì | Usa `ConsumableItem` e `MILK_BOTTLE` |
| `apple_juice_bottle` | No | Contenitore fluido / ingredienti |
| `golden_apple_juice_bottle` | No | Contenitore fluido; effetti da definire |
| `grape_juice_bottle` | No | Succo d'uva rossa (ID storico) |
| `white_grape_juice_bottle` | No | Succo d'uva bianca, nuovo fluido |
| `wine_bottle` | No | Salva la qualità; non ha consumable |
| `whey_bottle` | No | Siero, materiale di lavorazione |
| `olive_oil_bottle` | No | Ingrediente, non una bevanda |
| `rennet` | No | Caglio in bottiglia, ingrediente |
| `apple_cider_vinegar` | No | Aceto in bottiglia, ingrediente |
| `cherry_jam_bottle` | No | Marmellata, ingrediente in contenitore di vetro |

Per confronto: la `minecraft:honey_bottle` vanilla e la bottiglia di latte di Farmer's Delight sono già bevibili; non vengono modificate dalla nostra registrazione.

## Decisioni ancora aperte

- Quali succhi rendere bevibili, valori nutritivi, durata del consumo e restituzione della bottiglia vuota.
- Vino: qualità, effetti e comportamento del consumo.
- Ingredienti tecnici (caglio, siero, aceto, olio, marmellata): non presumere che siano bevande.
- Evitare di rompere gli scambi di fluido tramite Fabric Transfer API quando si implementerà il consumo.

Questa revisione riguarda solamente la **presenza di componenti/azioni di consumo**, non il fatto che i contenitori possano trasferire fluidi a tino, barili e calderoni. 
