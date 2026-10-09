# Condimenti e rendering 2.5D

## Direzione confermata

La pizza viene condita **cliccando sulla base posizionata nel mondo**. Il risultato deve mostrare ingredienti effettivamente scelti, non solo un'icona generica.

**Rendering 2.5D CONFERMATO:**
- salse **piatte** sul disco;
- formaggi, salumi, verdure, funghi ed erbe **leggermente in rilievo**;
- composizione leggibile sia da cruda sia da cotta.

Non è prevista una texture completa separata per ogni combinazione possibile.

## Categorie ingredienti e preset (PROPOSTE)

| Categoria | Preset visuale | Esempi |
| --- | --- | --- |
| Salse | `flat_sauce`, superficie colorata | pomodoro, pesto futuro, creme |
| Formaggi | `cheese_pieces`, chiazze/pezzi bassi | mozzarella, gorgonzola, scamorza, provolone |
| Formaggi grattugiati | `grated_cheese`, scaglie sparse | parmigiano, pecorino |
| Salumi rotondi | `slice_round` | salame |
| Salumi a falde | `slice_folded` | prosciutto crudo, speck, pancetta |
| Pezzi piccoli | `chunk_small` | funghi, verdure, mortadella |
| Anelli | `ring_small` | cipolla, peperone |
| Foglie/erbe | `leaf_small` | basilico, altre erbe |
| Altri mod non specifici | `generic_topping` | fallback basato sulla categoria |

**Rendering a livelli proposto**: (1) impasto, (2) salsa, (3) formaggi, (4) salumi/verdure, (5) erbe e dettagli. L'ordine nel rendering non impone l'ordine di aggiunta degli ingredienti.

## Crudo e cotto

| Proprietà | Crudo | Cotto |
| --- | --- | --- |
| Impasto | più chiaro | bordo più dorato |
| Salsa | brillante | un poco più scura |
| Formaggi | pezzi distinti | aspetto parzialmente fuso |
| Condimenti | tono fresco | lieve variazione di tono |
| Bruciatura | assente | **assente, sempre** |

Queste variazioni cromatiche sono idee estetiche, non specifiche asset già approvate.

## Quantità e limiti (PROPOSTE)

- Massimo iniziale di **8 porzioni**, incluse le salse.
- Ripetizioni per lo stesso ingrediente per creare versioni più condite.
- Quantità **logica** (porzioni realmente consumate) separata dalla densità **visiva** (quanti elementi disegnare).
- Distribuire pochi frammenti per 1 porzione, aumentarli con 2–3, fissare densità massima con molte porzioni per salvare la leggibilità.
- Non utilizzare un'unica enorme texture di salame/formaggio per rappresentare una porzione.

## Posizioni stabili — PROPOSTA TECNICA

Ogni pizza conserva ingredienti, quantità e un `render_seed`. Il renderer distribuisce in maniera **deterministica** posizioni, rotazioni e leggere variazioni di scala dei topping. Lo stesso item deve sembrare identico:
- durante condimento e cottura;
- dopo raccolta/riposizionamento;
- dopo salvataggio e ricaricamento;
- per giocatori diversi in multiplayer.

Valutare caching di mesh e ricomposizione solo quando cambia la pizza, non a ogni frame. Verificare costi di rendering con molte pizze simultanee in un chunk.

## Blocco nel mondo, forno, inventario, fette

- **Nel mondo**: modello completo con strati 2.5D.
- **Nel Pizza Oven**: stessa pizza visibile dentro la bocca del forno.
- **Item in inventario**: rappresentazione semplificata ma coerente con il gusto e la condizione cruda/cotta.
- **Pizza Slice**: piccola resa che mostri i condimenti principali della pizza originale (se il sistema fette viene confermato).

Le pizze cotte riposizionabili sono una proposta da validare.

## Compatibilità con altre mod

Separare **validità dell'ingrediente** (tag/definizione) e **preset grafico** (categoria, geometria, aspetto). Un ingrediente compatibile senza modello dedicato usa un fallback visuale del suo gruppo, senza apparire invisibile. Non accettare automaticamente ogni alimento come topping.

## Texture già presenti da conservare

- `src/main/resources/assets/italiansdelight/textures/item/pizza_margherita.png`
- `src/main/resources/assets/italiansdelight/textures/block/top_mushroom_pizza.png`

**Non spostare, cancellare, sovrascrivere o generare sostituzioni per questi asset senza richiesta esplicita.** Andranno studiati quando realizzeremo il renderer.

## Da definire

Altezze e dimensioni in voxel, forma del bordo, hitbox, modalità di render per ogni categoria, resa dei formaggi fusi, comportamento con salse sovrapposte, dettagli delle varianti per item in inventario e budget performance.
