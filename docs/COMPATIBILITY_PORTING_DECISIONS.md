# Decisioni ufficiali — Compatibilità e porting di Italian's Delight Refabricated

**Data decisione:** 11 novembre 2026  
**Stato:** decisioni approvate dall'autore, lavoro di porting NON ancora avviato  
**Riferimento:** release pubblicata `v1.1.0` (Minecraft 26.2, Fabric)  
**Ambito:** organizzazione delle versioni Minecraft e dei mod loader da supportare

> Questo documento registra le decisioni di prodotto. Non equivale a dichiarare
> le nuove versioni già compatibili. Qualsiasi compatibilità dovrà essere
> confermata da compilazione, avvio e test in gioco prima della pubblicazione.

## 1. Decisione principale: ridurre lo sviluppo multiversione immediato

Per contenere la complessità e proseguire lo sviluppo delle funzionalità
della mod, **si limita il porting a breve termine alla famiglia Minecraft 26.x,
con Fabric**.

| Minecraft | Decisione | Note |
| --- | --- | --- |
| **26.2** | **MANTENERE** | Base di riferimento della versione 1.1.0 già funzionante e pubblicata. Nessuna regressione ammessa. |
| **26.3** | **PORTARE ORA** | Nuovo obiettivo prioritario. Necessario adattare le API cambiate. |
| **26.1.2** | **PORTARE ORA** | Secondo obiettivo della famiglia 26.x. |
| **26.1.1** | **INCLUDERE nel supporto 26.1.x** | Verificare se possa utilizzare lo stesso JAR della 26.1.2; in caso contrario, creare un build dedicato. |
| **26.1** | **INCLUDERE nel supporto 26.1.x** | Stessa verifica e stesso criterio della 26.1.1. |

**Obiettivo attuale:** rendere disponibili build Fabric per Minecraft
**26.3, 26.2, 26.1.2, 26.1.1 e 26.1**, senza dare per scontato che
un unico JAR sia binariamente compatibile con più versioni.

La compatibilità delle versioni `26.1` e `26.1.1` dovrà essere verificata
singolarmente, anche nel caso in cui si adotti un'unica build della serie
`26.1.x`. Verificare per ogni target la disponibilità e le versioni
corrispondenti di Farmer's Delight Refabricated, Fabric API, Fabric Loader,
JEI e delle integrazioni opzionali.

## 2. Porting rinviati fino al completamento delle funzionalità della mod

**NON iniziare adesso** il porting per le versioni Minecraft precedenti
alla famiglia 26.x. Le versioni discusse in precedenza restano candidati,
non obiettivi attivi:

- Minecraft **1.21.11**
- Minecraft **1.21.8**
- Minecraft **1.21.1**
- Minecraft **1.20.1**
- Eventuali altre versioni che risulteranno importanti al momento del
  porting completo.

**Motivo della decisione:** evitare che differenze nelle API storiche,
nel formato dei dati e nella piattaforma Java rallentino lo sviluppo
funzionale principale e obblighino a ripetere ogni modifica su più codebase.

Una volta **completato integralmente lo sviluppo delle funzionalità**
della mod, verrà eseguita una **nuova valutazione** delle versioni di
Minecraft effettivamente necessarie, e solo allora inizierà il porting
completo. La lista finale rimane da decidere in quel momento, in base a
domanda, dipendenze disponibili e fattibilità tecnica.

## 3. Forge e NeoForge: porting futuri, non in questa fase

L'autore ha deciso che, **solo dopo il completamento dello sviluppo della
mod**, sarà affrontata anche la distribuzione su altri mod loader:

- **Forge** — porting futuro, non avviato.
- **NeoForge** — porting futuro, non avviato.

Non assumere che codice Fabric, integrazioni e dipendenze possano essere
riutilizzati senza modifiche. Prima di avviare questi porting occorrerà
verificare la disponibilità di **Farmer's Delight** e delle API necessarie
per ogni specifica coppia Minecraft/mod loader.

La scelta della struttura tecnica multiloader (ad esempio moduli comuni
e adattatori specifici) è **rinviata**, non approvata definitivamente.

## 4. Ordine di lavoro concordato

1. **Proteggere il riferimento Minecraft 26.2**: conservare funzionalità,
   ID di registrazione, dati e comportamenti già approvati.
2. **Porting Fabric Minecraft 26.3**: aggiornare dipendenze e API, compilare
   e validare le meccaniche.
3. **Porting Fabric Minecraft 26.1.2**: adattare le incompatibilità
   specifiche e verificare le dipendenze.
4. **Verifica esplicita Minecraft 26.1.1 e 26.1**: preferire il riuso
   dell'artifact 26.1.2 solo se realmente compatibile; altrimenti valutare
   JAR distinti.
5. **Continuare lo sviluppo della mod** senza estendere, in questa fase,
   la matrice a Minecraft 1.21.x / 1.20.1 o a Forge / NeoForge.
6. **A sviluppo concluso**: riesaminare e avviare il porting completo,
   inclusi Forge e NeoForge, con un piano tecnico aggiornato.

## 5. Criteri prima di considerare un target supportato

Per **ogni versione** candidata verificare almeno:

- Dipendenze effettivamente reperibili e compatibili (Minecraft,
  Fabric API, Farmer's Delight Refabricated, JEI ove utilizzato).
- Build del JAR con versione interna e metadata corretti.
- Avvio Minecraft lato client **e** server dedicato senza errori.
- Funzionamento di Cheese Vat, stagionatura, pizza, Cutting Board,
  Crushing Tub, Barrel, calderoni e trasferimento fluidi.
- Worldgen, crescita, fruttificazione e raccolta di ulivo,
  mandorlo e ciliegio; test anche su nuovi chunk.
- Persistenza dei dati di blocchi, liquidi e pizze dopo salvataggio
  e riavvio; compatibilità dei salvataggi da valutare separatamente
  senza promettere downgrade fra versioni Minecraft differenti.
- Verifica delle integrazioni opzionali senza rendere obbligatoria
  Serene Seasons.
- Controllo di package, licenze, notice e caricamento corretto
  sulle piattaforme di distribuzione.

Compilazione riuscita **non** significa automaticamente compatibilità
approvata o pubblicabile.

## 6. Regole per il repository e per la pubblicazione

- **Nessun nuovo branch** senza autorizzazione esplicita dell'autore.
- Per gli interventi di sviluppo usare il branch già esistente
  `feature/orchards-1.1.0`, salvo decisione esplicita successiva.
- Non modificare il codice né i JAR di release già pubblicati mentre
  si redige o aggiorna questo piano.
- **Nessun merge in `main`, tag o pubblicazione** di nuovi porting
  senza test e approvazione dell'autore.
- Non cambiare i registry ID esistenti senza una necessità dimostrata
  e un piano di migrazione.
- Tenere separati **decisioni approvate**, **compatibilità in prova**
  e **compatibilità verificata e pubblicata**.

## 7. Stato di avanzamento

| Target | Stato all'11/11/2026 |
| --- | --- |
| Fabric 26.2 | Pubblicata — riferimento `v1.1.0` |
| Fabric 26.3 | Deciso, **non implementato** |
| Fabric 26.1.2 | Deciso, **non implementato** |
| Fabric 26.1.1 | Deciso, **non verificato** |
| Fabric 26.1 | Deciso, **non verificato** |
| Minecraft 1.21.x / 1.20.1 | **Rinviato** al completamento della mod |
| Forge / NeoForge | **Rinviato** al completamento della mod |

---

**Nota di manutenzione:** aggiornare questo documento quando viene approvato
un nuovo target, quando una build supera i test o quando l'autore cambia la
strategia di compatibilità. Non sostituire gli stati previsionali con
affermazioni di compatibilità senza verifica.
