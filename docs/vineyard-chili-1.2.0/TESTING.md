# Collaudo e preparazione al rilascio — 1.2.0

**Branch:** `feature/vineyard-chili-1.2.0` · **Minecraft:** 26.3 · **Java:** 25.  
Questa è una **checklist di verifica**, non una dichiarazione che ogni test sia già stato eseguito.

## 1. Preparazione

- Usa un **mondo di prova** e fai il backup di qualunque salvataggio importante.
- Installa le dipendenze indicate in [gradle.properties](../../gradle.properties): Fabric, Fabric API e Farmer's Delight Refabricated della stessa versione Minecraft. JEI è utile per controllare le ricette.
- Verifica che GitHub Actions completi sia [CI e JAR](../../.github/workflows/vineyard-chili-1.2.0.yml) sia [avvio/riapertura server](../../.github/workflows/vineyard-chili-server.yml).
- Nessuna build automatica sostituisce la prova grafica nel client.

Su Windows, dalla cartella del repository:

```powershell
git fetch origin
git switch feature/vineyard-chili-1.2.0
git pull --ff-only origin feature/vineyard-chili-1.2.0
java -version
.\gradlew.bat runClient --no-daemon --max-workers=1
```

Non usare `clean` se il sistema ha poco spazio o processi Gradle che tengono bloccati i file.

## 2. Test di gameplay (client)

- [ ] **Creativa e JEI:** nessun item duplicato o texture mancante. I nuovi piatti sono nella categoria corretta; macedonia accanto all'insalata di mozzarella. Pizze Principessa e piccante alle olive disponibili con sprite crudo/cotto/spicchio e nomi corretti.
- [ ] **Vigne:** ara la terra e pianta uva rossa e bianca con tralicci e corde originali FDR. Verifica collegamento delle corde, crescita laterale, stadi di maturazione, raccolta/ripresa e restituzione della corda quando rompi un ramo. La farmland non deve scomparire.
- [ ] **Viti selvatiche:** esplora nuovi chunk temperati e cerca entrambe le varietà a due blocchi, con terra brulla e arboscelli secchi occasionali; verifica drop alla rottura e compatibilità delle cesoie.
- [ ] **Peperoncini e pomodori:** verifica piantumazione, crescita, hitbox attraversabile, raccolta ripetuta, comportamento su pali/corde, Ghost Pepper e assenza di regressioni dei pomodori FDR.
- [ ] **Crushing Tub e liquidi:** spremi grappoli rossi/bianchi e ciliegie; verifica bottiglie da 250 mB, secchi da 1.000 mB, colori, nomi, trasferimento fra Barrel e calderone e assenza di mescolamenti indesiderati.
- [ ] **Brewing Barrel:** apri la GUI, verifica posizionamento degli slot, titolo per legno, animazioni, JEI e tutti gli **otto** percorsi di trasformazione descritti nel [riferimento tecnico](TECHNICAL.md). Controlla i 12.000 tick, la coltura e il rifiuto di prodotti diversi nello stesso serbatoio.
- [ ] **Bevande:** consuma succhi, vini, idromele e sidro; verifica qualità ed effetti, resa dei contenitori e assenza di duplicazioni. Controlla il succo di mela dorata e la non-idratazione dell'olio; testa anche con e senza Thirst Was Taken 2.
- [ ] **Cucina:** prepara le quattro nuove pietanze in Cooking Pot, macedonia e biscotti con crafting, tutte le alternative con prodotti fermentati e restituzione di ciotole/bottiglie. Controlla JEI e recipe book. L'Amatriciana normale deve provenire solo dalla Cooking Pot.
- [ ] **Pizza:** applica pomodoro, panna e ricotta in ordini differenti su quarti distinti; verifica base/guarnizione, rimozione e rimborso, cottura, spicchi, ricollocazione e salvataggio senza perdita di condimenti.

## 3. Regressioni e compatibilità

- [ ] **Mondo vecchio:** con una **copia** di un salvataggio 1.1.0, verifica inventario, Cheese Vat, salumi/formaggi appesi, Olive/Almond/Fruit Cherry, Crushing Tub, pizza, Barrel e fluidi. Salva, chiudi e riapri.
- [ ] **Mondo nuovo:** controlla worldgen, crescita e maturazione dopo il riavvio. Verifica che non compaiano blocchi nero-magenta e che i prodotti fermentati conservino quantità e qualità.
- [ ] **Optional:** ripeti i test pertinenti con Serene Seasons/GlitchCore e Thirst Was Taken 2, senza convertirli in dipendenze obbligatorie.
- [ ] **Server:** conferma oltre al test automatico anche le interazioni rilevanti con un client collegato; segnala chiaramente se il multiplayer reale non è stato provato.
- [ ] **Distribuzione:** apri il JAR destinato al rilascio e verifica versione `fabric.mod.json`, dipendenze, traduzioni, asset e presenza di `LICENSE_italiansdelight`, `META-INF/NOTICE`, `META-INF/THIRD_PARTY_NOTICES.md`, `META-INF/FDR_CRATES_LICENSE.md` e crediti.
- [ ] **Pubblicazione:** verifica che il JAR sia esattamente `1.2.0-mc26.3`, approva il commit e il checksum della build testata; crea tag e release **solo dopo** autorizzazione. In caso di modifiche successive al test, rigenera e ricollauda un nuovo JAR.

## 4. Comandi utili per il mondo di prova

```text
/give @p italiansdelight:grape_seeds 16
/give @p italiansdelight:white_grape_seeds 16
/give @p italiansdelight:chili_pepper_seeds 16
/give @p italiansdelight:crop_stake 16
/give @p farmersdelight:rope 32
/give @p italiansdelight:brewing_barrel
/give @p minecraft:bone_meal 64
```

Con `Shift` + clic sulla faccia laterale puoi avviare o estendere una corda orizzontale secondo la meccanica implementata; senza `Shift` rimane la posa verticale FDR.

## Esito prima del rilascio

Annota qui la versione/commit effettivamente verificato, le prove eseguite e i problemi residui. **Nessun check automatico o questa checklist equivale a un'approvazione del rilascio**. Per descrivere i risultati effettivi apri i workflow del commit e conserva la conferma dei test manuali.

[Documentazione 1.2.0](README.md) · [Riferimento tecnico](TECHNICAL.md) · [Crediti e attribuzioni](../../CREDIT.md)

## 5. Scaricare e identificare il candidato corretto

1. Aprire la [CI Vineyard & Chili 1.2.0](https://github.com/Pima95/ItaliansDelightRefabricated/actions/workflows/vineyard-chili-1.2.0.yml) e scegliere l'esecuzione **riuscita relativa al commit che si vuole approvare**.
2. Scaricare l'artifact `italiansdelight-1.2.0-mc26.3-release-candidate-<SHA del commit>`, contenente **un JAR giocabile** `italiansdelight-1.2.0-mc26.3.jar` e il suo `SHA256SUMS`.
3. Su Windows eseguire `Get-FileHash -Algorithm SHA256 .\italiansdelight-1.2.0-mc26.3.jar` e confrontare il valore con `SHA256SUMS`. Su Linux/macOS: `sha256sum -c SHA256SUMS`.
4. Provare quel medesimo file nel client, annotando **commit, checksum, dipendenze e risultati**. Non usare `sources` o `dev` JAR.
5. Solo dopo approvazione esplicita scegliere la procedura di merge/tag/pubblicazione. **Questa preparazione non crea un nuovo branch, un tag o una GitHub Release.**
