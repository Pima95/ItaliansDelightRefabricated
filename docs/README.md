# Documentazione — Italian's Delight Refabricated

**Da dove cominciare?** Tutta la documentazione corrente è raggiungibile da questa pagina. Le release precedenti hanno un solo changelog ciascuna, mentre la versione in sviluppo ha tre documenti: panoramica, funzionamento tecnico e collaudo.

| Sto cercando… | Dove leggere |
| --- | --- |
| **Cos'è la mod e quali versioni sono disponibili?** | [README del progetto](../README.md) |
| **Come installarla?** | [Versioni e requisiti](../README.md#versioni-e-installazione) · [Release pubblicate](https://github.com/Pima95/ItaliansDelightRefabricated/releases) |
| **Che cosa contiene la 1.2.0?** | [Vineyard & Chili — panoramica](vineyard-chili-1.2.0/README.md) |
| **Come funzionano uva, peperoncini, liquidi, barili e pizze?** | [Riferimento tecnico 1.2.0](vineyard-chili-1.2.0/TECHNICAL.md) |
| **Come compilarla e provarla prima della pubblicazione?** | [Checklist test 1.2.0](vineyard-chili-1.2.0/TESTING.md) |
| **Dove sono i prompt e le regole approvate per le texture?** | [Prompt approvati](PROMPTS.md) |
| **Che cosa è cambiato nel tempo?** | [Changelog generale](../CHANGELOG.md) |
| **Chi ha realizzato la mod e le risorse?** | [Crediti e provenienza](../CREDIT.md) |
| **Quali licenze e attribuzioni si applicano?** | [LICENSE](../LICENSE) · [NOTICE](../NOTICE) · [Materiale di terzi](../THIRD_PARTY_NOTICES.md) · [MIT texture crate FDR](FDR_CRATES_LICENSE.md) |
| **Come segnalare un problema?** | [Issue GitHub](https://github.com/Pima95/ItaliansDelightRefabricated/issues) |

## Versioni

- [**1.0.0 — Pizza Update**](pizza-1.0.0/CHANGELOG.md) — pubblicata il **23 ottobre 2026** per Minecraft 26.2.
- [**1.1.0 — Orchard Update**](orchards-1.1.0/CHANGELOG.md) — pubblicata l'**11 novembre 2026** per Minecraft 26.2; è una release storica.
- **1.2.0 — Vineyard & Chili** — **candidata al rilascio, in collaudo**, per Minecraft 26.1.2 sul branch `feature/mc-26.1.2`. Questa build non è ancora una release, mentre le versioni superiori sono gestite separatamente.

## Per chi sviluppa

Per la 1.2.0 consulta in ordine [panoramica](vineyard-chili-1.2.0/README.md), [riferimento tecnico](vineyard-chili-1.2.0/TECHNICAL.md) e [collaudo](vineyard-chili-1.2.0/TESTING.md). Il codice e `gradle.properties` restano la fonte autorevole per API, versioni e funzionamento.

La [CI del branch](../.github/workflows/mc-26.1.2.yml) compila ed esegue i controlli statici; il [test server](../.github/workflows/mc-26.1.2-server.yml) verifica avvio e riapertura di un mondo di prova. I due workflow **non** certificano la grafica e le interazioni nel client.

## Documentazione storica

I vecchi audit, piani provvisori, prove per singola texture e annotazioni sui prototipi sono stati eliminati **solo dal branch di sviluppo attuale**: si recuperano nei [commit precedenti alla riorganizzazione](https://github.com/Pima95/ItaliansDelightRefabricated/tree/3801dd9762312c30706137bf06da88023f0ad6a3/docs). Non sono stati cancellati dalla cronologia Git, né dalle release già pubblicate.

I documenti di licenza effettivamente necessari alla distribuzione rimangono presenti nel repository e nel JAR. Non confondere le vecchie note di progetto con le funzioni realmente disponibili nella 1.2.0.
