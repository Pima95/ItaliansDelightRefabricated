# Orchard 1.1.0 RC — verifica asset, attribuzioni e licenze

**Data:** 11 novembre 2026 · **Branch:** `feature/orchards-1.1.0`  
**Scopo:** preparazione tecnica della release candidate; **non è una certificazione legale definitiva e non autorizza una pubblicazione**.

## Riscontri documentali

- **Rustic**, autore indicato da CurseForge: `cadaverous_queen`;
  [pagina e dichiarazione MIT](https://www.curseforge.com/minecraft/mc-mods/rustic/license);
  [sorgenti pubblici](https://github.com/cadaverous-eris/Rustic).
  Nel tree sorgente pubblico preso a riferimento non risulta un file LICENSE
  autonomo con dati nominativi. La pagina CurseForge riporta la formula MIT
  generica: l'attribuzione esatta di eventuali contributori indipendenti
  richiede controllo per asset.
- Confronto bit-identical con Rustic, commit/tree
  `f40bd6f7c6db3cb6441db78cc11f7e6df2d52bd0`:
  **41** corrispondenze storiche, **39** ancora presenti identiche,
  **2** rimosse in precedenza. Vedi
  [l'inventario dei blob e percorsi](RUSTIC_ASSET_MATCHES.md).
  Include texture legacy eventualmente non usate dalle meccaniche 1.1.0.
- **Italian's Delight** originale: [pagina con licenza MIT](https://www.curseforge.com/minecraft/mc-mods/italians-delight/license);
  titolare pubblicato `PuurpZ` con più artisti/contributori.
  Alcune texture locali vengono dal progetto storico, ma per i singoli
  file non esiste ancora una corrispondenza binaria verificata.
- **Farmer's Delight Refabricated**: il file LICENSE upstream conferma
  `Copyright (c) 2020 vectorwing` e MIT; le crate Orchard sono state
  adattate da specifici sprite crate. Vedi
  [provenienza e testo della licenza](FDR_CRATES_LICENSE.md).
  Nessun modulo o dipendenza esterna viene incorporato nei file sorgenti
  per effetto del solo riferimento a Farmer's Delight.

## Packaging verificabile dalla build

La task `jar` di `build.gradle` incorpora i seguenti documenti:
- `LICENSE_italiansdelight` (licenza propria, già presente);
- `META-INF/THIRD_PARTY_NOTICES.md` (crediti Rustic/Italian's Delight, MIT);
- `META-INF/FDR_CRATES_LICENSE.md` (licenza/notice originario FDR crate).

Il [workflow CI della candidate](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/43010592706)
ha verificato **nel JAR compilato** queste tre voci, oltre alla
versione effettiva `1.1.0-rc.1` in `fabric.mod.json`. L'archivio
scaricabile è l'[artifact del commit candidato](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/43010592706/artifacts/13849559373).
Questo controllo sul packaging non sostituisce la revisione delle
attribuzioni individuali degli asset.

## Verifiche residue prima di rendere pubblica la release

1. Attribuzione puntuale delle altre texture legacy derivate da Italian's Delight
   e degli artisti individuali; ricontrollare la distribuzione sorgente
   originale se disponibile. **Aperta.**
2. Verificare se tutte le risorse Rustic storiche (es. grape/wine) debbano
   essere distribuite, senza rimuovere file oggi necessari a compatibilità
   dei mondi o texture: **aperta**, ottimizzazione facoltativa.
3. Verifica sul JAR candidato dei notice e del copyright FDR: **superata**
   dalla CI del 11 novembre 2026; confermata anche la versione interna.
   Non equivale a verificare tutta la titolarità delle texture.
4. Revisione legale finale delle attribuzioni individuali:
   **non si presume superata** dalla sola appartenenza del progetto a MIT.

**Decisione RC:** si può creare e testare internamente la release candidate.
Nessuna modifica agli asset già approvati, nessun tag definitivo, nessun
merge in `main` e nessuna pubblicazione senza la successiva autorizzazione.

## Decisione successiva: licenza Apache 2.0 (11 novembre 2026)

L'autore ha approvato **Apache License 2.0** come unica licenza di progetto
selezionabile su CurseForge e Modrinth. Il branch di sviluppo Orchard ora
adotta la licenza per i **contributi originali** di Piergiuseppe Mastroserio
(Pima95) e include il file `NOTICE` richiesto per la relativa attribuzione.

La licenza di progetto non modifica i diritti sui materiali importati da
Rustic, Italian's Delight originale o Farmer's Delight: continuano a valere
i rispettivi avvisi/licenze MIT (e le eventuali ulteriori condizioni dei
singoli artisti, ancora da verificare). Il JAR deve comprendere
`LICENSE_italiansdelight`, `META-INF/NOTICE`,
`META-INF/THIRD_PARTY_NOTICES.md` e
`META-INF/FDR_CRATES_LICENSE.md`.

La release pubblicata **1.0.0 resta MIT** e le concessioni già effettuate
non vengono revocate. Il cambio sul branch **non** costituisce autorizzazione
alla release definitiva o alla modifica delle schede CurseForge/Modrinth.
I test CI e il controllo dei nuovi notice nel JAR devono essere rieseguiti;
la revisione puntuale della provenienza degli asset rimane **aperta**.

## Credits originali verificati sui progetti pubblici (11 novembre 2026)

- **Italian's Delight originale (CurseForge):** PuurpZ risulta titolare
  del progetto; nella sezione del team compaiono gli artisti
  **nickhanic2021**, **JAMMSON**, **duynotsimp**. La presenza nel team
  non dimostra chi abbia disegnato ciascun asset attualmente presente
  nel nostro JAR. Fonte:
  https://www.curseforge.com/minecraft/mc-mods/italians-delight
- **Rustic (CurseForge):** il progetto è attribuito a **cadaverous_queen**;
  i ringraziamenti originali indicano **Nazenn** (texture create/modificate)
  e **Copygirl** (permesso di riutilizzare modelli/texture dei locker).
  Non attribuire loro specifici asset locali senza corrispondenza
  individuale. Fonte: https://www.curseforge.com/minecraft/mc-mods/rustic
- **Farmer's Delight:** licenza originale su GitHub verificata con
  `Copyright (c) 2020 vectorwing`, già preservato in
  `FDR_CRATES_LICENSE.md`; i cinque crate Orchard derivano da sprite
  preesistenti e non sono interamente artwork originale.
- **MIT originale:** le pagine licenza CurseForge di Rustic e
  Italian's Delight riportano un testo con campi copyright generici.
  Nella copia GitHub consultata di Rustic non è presente un file
  `LICENSE` autonomo che risolva nome/anno. Questi dati non possono
  essere inventati: avvisi originali più specifici, se recuperati,
  vanno conservati integralmente.

**Aggiornamento tecnico:** `THIRD_PARTY_NOTICES.md` riporta ora i
crediti pubblicamente identificabili, separandoli dalla titolarità
dei singoli file; la CI della RC ne verifica la presenza nel JAR.
Questo rafforza la documentazione, ma **non equivale a validazione
legale dei 39 asset Rustic né delle texture non ancora attribuite**.

**Aperti prima del rilascio:** inventariare i PNG legacy del progetto
Italian's Delight originale e verificare l'eventuale paternità
individuale; ottenere e conservare avvisi copyright più specifici
qualora esistano nei pacchetti originali. Non eliminare o sostituire
texture approvate senza prova di utilizzo e compatibilità.
