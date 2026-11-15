# CREDIT — Italian's Delight Refabricated

Aggiornato il 15 novembre 2026. Crediti, provenienza delle risorse e attribuzioni: **questa pagina è un riepilogo** e non sostituisce i documenti di licenza.

## Italian's Delight Refabricated

- Creatore e contributi originali: **Piergiuseppe Mastroserio (Pima95)**.
- Repository: https://github.com/Pima95/ItaliansDelightRefabricated
- Codice e opere originali del progetto: **Apache License 2.0**, vedere `LICENSE` e `NOTICE`.
- Le parti derivate da altre mod **non** vengono automaticamente concesse sotto Apache 2.0; conservano le rispettive licenze e attribuzioni.

## Italian's Delight (mod originale)

- Autore/progetto originale: **PuurpZ**, con il team di Italian's Delight.
- Artisti accreditati dalla pagina del progetto: **nickhanic2021**, **JAMMSON** e **duynotsimp**. Sono crediti di progetto, **non** assegnazioni verificate ai singoli PNG.
- https://www.curseforge.com/minecraft/mc-mods/italians-delight — licenza dichiarata: **MIT**.
- Alcuni asset legacy richiedono ancora una verifica puntuale di provenienza; vedere `THIRD_PARTY_NOTICES.md`.

## Farmer's Delight / Farmer's Delight Refabricated

- **vectorwing**: Farmer's Delight originale, copyright esplicito `Copyright (c) 2020 vectorwing` e licenza MIT.
- Port e contributi Refabricated: https://github.com/MehVahdJukaar/FarmersDelightRefabricated.
- Italian's Delight Refabricated utilizza API e meccaniche di Farmer's Delight, oltre a elementi grafici di alcune cassette adattate. Per i file e la licenza MIT applicabile alle cassette: `docs/orchards-1.1.0/FDR_CRATES_LICENSE.md`.

## Rustic e Rustic Delight

- **cadaverous_queen**: Rustic originale, https://github.com/cadaverous-eris/Rustic.
- La descrizione di Rustic ringrazia anche **Nazenn** (interventi sulle texture) e **Copygirl** (permesso d'uso per modelli/texture dei locker). Ciò non prova la paternità di uno specifico asset presente nella nostra mod.
- Rustic è una fonte di riferimento per gli impianti di lavorazione/fermentazione; esistono inoltre risorse legacy confrontate con Rustic: `docs/orchards-1.1.0/RUSTIC_ASSET_MATCHES.md`.
- **Rustic Delight** è un progetto distinto usato per confronto tecnico: https://github.com/PhantomWing/RusticDelight. Non attribuiamo a quest'ultimo gli asset di Rustic senza prova.

## Alcocraft — modello della botte di fermentazione

- **Alcocraft: Beer & Stuff** originale di **hadrus91** (GitHub `hadrus`): https://www.curseforge.com/minecraft/mc-mods/alcocraft-beer-and-stuff e https://github.com/hadrus/Alcocraft.
- La pagina del **progetto originale** riporta la licenza **MIT**. I file sono stati confrontati direttamente con il repository originale, non con un porting.
- **Asset utilizzati**: modello `spruce_keg.json` di Blockbench (22 elementi, UV, trasformazioni visualizzazione) e texture `spruce_keg.png` 64×64. I file caricati corrispondono esattamente ai blob dell'originale `hadrus/Alcocraft`:
  - modello: `0082a9b347986f1b6b0c99c60aea1c652cccd9ec`;
  - texture: `15bc4dcee27b3cb34a82d9bbf9ac50a43bdbee26`.
- **Modifiche per Italian's Delight**: sostituzione namespace e riferimenti `alcocraft:spruce_keg`, adozione dell'ID già esistente `italiansdelight:brewing_barrel`, varianti per 16 tipi di legno (con recolor della texture), aggiornamento dell'orientamento e degli asset di inventario. **Nessun codice Java, ricetta birra o interfaccia di Alcocraft è stata importata**.
- La stringa `Made with Blockbench` identifica lo strumento usato nel modello originale, non un autore.
- **Nota licenza MIT**: alcune pagine pubblicano il modello standard di licenza con le parole segnaposto `<year> <copyright holders>`, senza una data o un titolare compilato. Ciò non impedisce di usare le risorse sotto MIT: significa solo che non dobbiamo inventare una riga di copyright. Conserviamo autore e provenienza originali e il testo della licenza; se nell'archivio sorgente si trova un avviso preciso, verrà incluso letteralmente.

## Ecosistema e strumenti

- **Mojang Studios / Minecraft**: gioco e relative risorse di base.
- **Fabric / Fabric API**: loader e API; **JEI**: consultazione ricette; **Serene Seasons**: compatibilità opzionale; **Thirst Was Taken 2**: supporto facoltativo tramite datapack.
- Citare una dipendenza o uno strumento **non significa** attribuirgli la paternità delle texture della nostra mod.

## Distribuzione e revisione

I crediti sono separati per contributi originali, adattamenti grafici, compatibilità e ispirazioni. L'Apache 2.0 del progetto non elimina i vincoli delle risorse MIT originali. Prima di rilasciare la 1.2.0, verificare che `LICENSE`, `NOTICE`, `THIRD_PARTY_NOTICES.md`, il testo MIT applicabile e questo `CREDIT.md` siano presenti anche nel JAR distribuito, e completare l'audit degli asset legacy non ancora attribuiti.
