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

La presenza **effettiva** nel JAR finale è un controllo separato della
pipeline (non basta trovare un file nel repository). Il changelog e le note
di release devono rimandare a questi notices.

## Verifiche residue prima di rendere pubblica la release

1. Attribuzione puntuale delle altre texture legacy derivate da Italian's Delight
   e degli artisti individuali; ricontrollare la distribuzione sorgente
   originale se disponibile. **Aperta.**
2. Verificare se tutte le risorse Rustic storiche (es. grape/wine) debbano
   essere distribuite, senza rimuovere file oggi necessari a compatibilità
   dei mondi o texture: **aperta**, ottimizzazione facoltativa.
3. Verifica sul JAR candidato di MIT notice e dei rispettivi copyright:
   **da eseguire con la pipeline**.
4. Revisione legale finale delle attribuzioni individuali:
   **non si presume superata** dalla sola appartenenza del progetto a MIT.

**Decisione RC:** si può creare e testare internamente la release candidate.
Nessuna modifica agli asset già approvati, nessun tag definitivo, nessun
merge in `main` e nessuna pubblicazione senza la successiva autorizzazione.
