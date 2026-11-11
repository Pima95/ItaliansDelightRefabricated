# Audit globale dei PNG — verifica alpha 0/255

> **SOLO DOCUMENTAZIONE — NESSUNA TEXTURE CORRETTA.** Questa lista identifica i file che contengono effettivamente pixel con alpha intermedio. La presenza nell'elenco non autorizza correzioni automatiche, soprattutto per i fluidi.

## Ambito, metodo ed esito

- **Repository:** `Pima95/ItaliansDelightRefabricated`
- **Branch analizzato:** `feature/orchards-1.1.0`
- **Commit esatto analizzato:** `e453c6af7b6a695fb0a015778c96486fab1436ec` (04/11/2026)
- **Campo di applicazione:** tutti i 414 file `.png` presenti nell'albero Git (inclusi 409 PNG sotto `src/main/resources/assets/italiansdelight/textures/`, 3 PNG aggiuntivi e 2 immagini della documentazione).
- **Metodo:** download in sola lettura dei 414 file dal commit; decodifica dei dati pixel PNG (inclusi PNG indicizzati, palette e maschera `tRNS`, filtri di riga, animazioni a sprite verticali e scansione Adam7 se presente); conteggio dei pixel con alpha **maggiore di 0 e minore di 255**. Opaco `255` e totalmente trasparente `0` sono considerati conformi.
- **Errori di lettura:** 0.
- **Risultato:** **73 PNG da esaminare**, di cui **70 nella cartella delle texture del gioco** e **3 immagini PNG aggiuntive**.
- **PNG conformi:** **341** su 414. Nella cartella texture risultano conformi **339** su 409.
- **Pixel complessivi con alpha intermedio:** **286.157** (somma su tutti i file elencati, non numero di pixel unici).
- **Formato colonne:** `pixel` = numero di pixel con alpha non conforme; `alpha rilevati` = valore minimo e massimo tra i pixel non conformi, su scala 8-bit.

## 1. Fluidi — 17 file

**Percorso comune:** `src/main/resources/assets/italiansdelight/textures/block/fluids/`

**Attenzione:** diversi liquidi possono usare la traslucenza per ragioni di rendering. Sono non conformi alla regola richiesta, ma **prima di modificarli occorre verificare trasparenza e resa dei fluidi in gioco**. Non impostare automaticamente tutto a 255.

| File (relativo al percorso comune) | Pixel | Alpha rilevati |
|---|---:|---:|
| `apple_juice_flow.png` | 32.768 | 165–211 |
| `apple_juice_overlay.png` | 256 | 179 |
| `apple_juice_still.png` | 8192 | 168–226 |
| `booze/cider_flow.png` | 32.768 | 165–211 |
| `booze/cider_still.png` | 8192 | 168–226 |
| `booze/wine_flow.png` | 32.768 | 223–247 |
| `booze/wine_still.png` | 8192 | 225–252 |
| `golden_apple_juice_flow.png` | 32.768 | 165–211 |
| `golden_apple_juice_overlay.png` | 256 | 179 |
| `golden_apple_juice_still.png` | 8192 | 168–226 |
| `grape_juice_flow.png` | 32.768 | 193–228 |
| `grape_juice_overlay.png` | 256 | 204 |
| `grape_juice_still.png` | 8192 | 195–238 |
| `olive_oil_flow.png` | 32.768 | 165–211 |
| `olive_oil_overlay.png` | 256 | 179 |
| `olive_oil_still.png` | 8192 | 168–226 |
| `vanta_oil_flow.png` | 32.765 | 245–254 |

## 2. GUI — 3 file

**Percorso comune:** `src/main/resources/assets/italiansdelight/textures/gui/`

| File | Pixel | Alpha rilevati |
|---|---:|---:|
| `cheese_vat_old_old.png` | 32 | 1–11 |
| `cheese_vat_old.png` | 32 | 1–11 |
| `cheese_vat.png` | 32 | 1–11 |

## 3. Item — 48 file

**Percorso comune:** `src/main/resources/assets/italiansdelight/textures/item/`

| File | Pixel | Alpha rilevati |
|---|---:|---:|
| `bocconcino.png` | 37 | 5–254 |
| `bresaola_slice.png` | 70 | 252–254 |
| `bresaola.png` | 54 | 217–254 |
| `burrata.png` | 75 | 93–254 |
| `cheese_grater.png` | 81 | 8–254 |
| `coppa_slice.png` | 90 | 253–254 |
| `coppa.png` | 85 | 249–254 |
| `cream_bowl.png` | 64 | 60–254 |
| `fresh_gorgonzola.png` | 101 | 1–254 |
| `fresh_parmigiano_reggiano.png` | 100 | 211–254 |
| `fresh_pecorino_romano.png` | 59 | 253–254 |
| `fresh_provolone.png` | 79 | 253–254 |
| `fresh_scamorza.png` | 75 | 1–254 |
| `gorgonzola_wedge.png` | 68 | 5–254 |
| `gorgonzola.png` | 106 | 1–254 |
| `grated_parmigiano_reggiano.png` | 46 | 1–254 |
| `grated_pecorino_romano.png` | 49 | 3–254 |
| `guanciale_slice.png` | 59 | 1–254 |
| `guanciale.png` | 83 | 251–254 |
| `mascarpone.png` | 76 | 252–254 |
| `mortadella_slice.png` | 78 | 5–254 |
| `mortadella.png` | 86 | 17–254 |
| `pancetta_slice.png` | 50 | 252–254 |
| `pancetta.png` | 83 | 252–254 |
| `parmigiano_reggiano_wedge.png` | 86 | 1–254 |
| `parmigiano_reggiano.png` | 100 | 204–254 |
| `pecorino_romano_wedge.png` | 91 | 7–254 |
| `pecorino_romano.png` | 76 | 11–254 |
| `prepared_bresaola.png` | 41 | 253–254 |
| `prepared_coppa.png` | 82 | 253–254 |
| `prepared_guanciale.png` | 29 | 253–254 |
| `prepared_pancetta.png` | 60 | 1–254 |
| `prepared_speck.png` | 80 | 227–254 |
| `prosciutto_crudo_slice.png` | 52 | 4–254 |
| `prosciutto_crudo.png` | 61 | 3–254 |
| `provolone_slice.png` | 55 | 69–254 |
| `provolone.png` | 83 | 253–254 |
| `raw_mortadella.png` | 70 | 1–254 |
| `raw_salame.png` | 29 | 4–254 |
| `ricotta.png` | 84 | 1–254 |
| `salame_slice.png` | 24 | 9–254 |
| `salame.png` | 27 | 253–254 |
| `salted_ham.png` | 38 | 253–254 |
| `scamorza.png` | 87 | 91–254 |
| `smoked_prepared_speck.png` | 66 | 9–254 |
| `smoked_scamorza.png` | 71 | 253–254 |
| `speck_slice.png` | 36 | 4–254 |
| `speck.png` | 43 | 253–254 |

## 4. Immagini nella cartella delle texture, ma non in `block/`, `gui/` o `item/` — 2 file

**Percorso comune:** `src/main/resources/assets/italiansdelight/textures/`

| File | Pixel | Alpha rilevati |
|---|---:|---:|
| `icon.png` | 77 | 60 |
| `italians_delight_refabricated.png` | 1635 | 60–64 |

## 5. Altri PNG nel repository — 3 file

Non sono nella cartella `textures/`, ma sono stati controllati perché la richiesta riguarda **tutti i PNG presenti**.

| Percorso completo | Pixel | Alpha rilevati |
|---|---:|---:|
| `icon.png` | 541 | 60 |
| `src/main/resources/assets/italiansdelight/italians_delight_refabricated.png` | 957 | 60–64 |
| `src/main/resources/assets/italiansdelight/logo.png` | 77 | 60 |

## File controllati e risultati conformi

- Tutti i restanti **341 PNG** non contengono nessun pixel con alpha intermedio.
- Le **due immagini in `docs/cheese-update/images/`** non presentano alpha intermedi.
- Le texture che non compaiono in queste tabelle sono conformi al criterio alpha 0/255 **al commit verificato**; l'audit non valuta stile artistico, provenienza degli asset, palette o correttezza delle UV.

## Istruzioni per una futura correzione (NON eseguita)

1. Intervenire in una fase successiva sui file elencati, senza toccare automaticamente quelli già conformi.
2. Per item e GUI, preservare forme, colori e posizioni dei pixel visibili, eliminando solo gli alpha intermedi con un criterio grafico approvato.
3. Per fluidi, determinare prima se traslucenza e blend siano un requisito funzionale. Una conversione indiscriminata potrebbe rendere opachi acqua, vino e succhi.
4. Dopo la correzione ripetere un audit completo su un nuovo commit; preservare il report storico attuale.

**Stato:** audit completato, modifiche grafiche NON effettuate. `main` e release `1.0.0` non coinvolti.
