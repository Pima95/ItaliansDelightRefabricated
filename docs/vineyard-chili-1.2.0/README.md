# Versione 1.2.0 — Vineyard & Chili

**Stato:** candidata al rilascio con JAR versione `1.2.0-mc26.3`, **non ancora pubblicata**.  
**Branch:** `feature/vineyard-chili-1.2.0` · **Minecraft:** 26.3 · **Java:** 25 · **Mod loader:** Fabric.

Questa versione amplia Italian's Delight Refabricated con coltivazione dell'uva e dei peperoncini, bevande, fermentazione e nuove ricette. Mantiene le meccaniche già distribuite della 1.0.0 (Pizza Update) e 1.1.0 (Orchard Update).

## Cosa aggiunge

### Vigne e peperoncini

Due varietà di uva, rossa e bianca, con semi, grappoli, pergole e tralci cresciuti su corde orizzontali di Farmer's Delight. Esistono viti selvatiche alte due blocchi, generate in gruppi nei biomi temperati con terra brulla e arboscelli secchi occasionali. Il peperoncino cresce a più stadi, su terreno e sostegni, e può produrre il raro Ghost Pepper.

### Spremitura, succhi e bevande

La Crushing Tub produce succhi da uva e ciliegie; bottiglie, secchi, Barrel e calderoni consentono la gestione dei liquidi supportati. Le bottiglie di succhi e le bevande fermentate previste dalla mod sono consumabili; il succo di mela dorata applica effetti specifici. L'integrazione con Thirst Was Taken 2 è opzionale.

### Brewing Barrel

La botte di fermentazione esiste in varianti per i diversi tipi di legno, con interfaccia grafica, serbatoi, coltura opzionale, qualità dei prodotti e ricette per vino rosso, bianco, vino di ciliegie, idromele, sidro e aceti.

### Nuovi cibi e pizze

Aggiunte quattro preparazioni in Cooking Pot (due paste e due risotti), la macedonia di uva e ciliegie, i biscotti alle mandorle e i gusti **Pizza piccante alle olive** e **Pizza Principessa**. I condimenti cremosi delle pizze rispettano l'ordine di applicazione su ciascun quarto. La focaccia è stata rimandata.

## Requisiti e installazione

Il ramo 1.2.0 usa Minecraft **26.3**, Fabric Loader **0.20.1**, Fabric API **0.166.0+26.3**, Farmer's Delight Refabricated **26.3-3.6.30+refabricated** e Java **25**. I valori effettivi sono in [gradle.properties](../../gradle.properties).

**Il JAR `italiansdelight-1.2.0-mc26.3.jar` è un candidato da collaudare: non presentarlo come una release stabile finché il test client e la pubblicazione non sono stati autorizzati.** La 1.1.0 già pubblicata usa invece Minecraft 26.2. Per il download di una versione stabile consulta [Releases](https://github.com/Pima95/ItaliansDelightRefabricated/releases).

## Dove leggere

- [Riferimento tecnico](TECHNICAL.md): coltivazioni, liquidi, fermentazione, ricette, salvataggi, compatibilità e asset.
- [Compilazione e test](TESTING.md): procedura di prova e checklist di rilascio.
- [Crediti](../../CREDIT.md) e [attribuzioni di terze parti](../../THIRD_PARTY_NOTICES.md): autori, asset adattati e limiti delle verifiche.
- [Documentazione generale](../README.md): indice unico del repository.

**Nota sullo stato:** la compilazione automatica e l'avvio su server dedicato sono stati superati prima di questa riorganizzazione documentale. I test grafici e di gameplay riportati nella checklist devono essere eseguiti e confermati nel client; non dichiararli superati senza riscontri.
