# Pizza Update 1.0.0 — Layout JEI del Pizza Oven

**Aggiornato:** 20 ottobre 2026  
**Branch:** `feature/pizza-1.0.0`  
**Stato:** layout adattato alle categorie vanilla JEI, build e collaudo in Minecraft da registrare separatamente.

## Correzione del layout

Dopo gli screenshot del 20 ottobre, la vecchia categoria «Cottura delle pizze (guida)» mostrava un generico layout personalizzato 141×76, senza freccia o fiamma animate, con una pila di carbone fissa e tre anteprime già disponibili anche nelle categorie vanilla.

Ora la categoria **«Forno per pizza» / «Pizza Oven»** usa **le stesse API e dimensioni di JEI 26.2** adottate da `AbstractCookingCategory` per Smelting e Smoking:

- **82×54 pixel**, input a **(1,1)**, carburante a **(1,37)**, risultato a **(61,19)**, output evidenziato.
- Alimentazione visualizzata tramite **`SlotDisplay.AnyFuel.INSTANCE`** con ruolo `RENDER_ONLY`, come JEI vanilla: combustibili validi a rotazione invece del carbone obbligatorio e nessuna falsa dipendenza di crafting.
- `IRecipeCategory.createRecipeExtras` aggiunge **`addAnimatedRecipeArrowWidget(cookingTicks)`** a (26,17), **`addAnimatedRecipeFlameWidget(300)`** a (1,20) e testo dei secondi con la traduzione nativa `gui.jei.category.smelting.time.seconds`.
- Una sola **Margherita** cruda/cotta come esempio per il **Pizza Oven**, 400 tick = **20 s**. Lo stack cotto è generato usando `PizzaBaking.finish` e conserva ingredienti, componenti e nome.
- **Fornace** (1200 tick/60 s) e **affumicatore** (600 tick/30 s) continuano a comparire nelle categorie **Smelting** e **Smoking già gestite direttamente da JEI**, non sono duplicate nella categoria Pizza Oven.
- Il blocco del **Pizza Oven** rimane l'icona/catalizzatore della categoria. La freccia della sua GUI rimane cliccabile per aprire questa pagina. La GUI reale del blocco e tutte le meccaniche di cottura restano inalterate.

**Implementazione:** si estende `AbstractRecipeCategory<PizzaBakingJeiRecipe>`, parte dell'API pubblica di JEI, **non** `mezz.jei.library.plugins.vanilla.cooking.AbstractCookingCategory`, che è una classe di implementazione interna con generici/estensioni legati a `RecipeHolder<AbstractCookingRecipe>`. Si riproducono soltanto le chiamate ai widget pubblici corrispondenti.

## Checklist test Minecraft

- [ ] `gradlew clean build` con Java 25 supera la compilazione.
- [ ] Avvio client con JEI: nessun crash al caricamento delle categorie.
- [ ] Premendo **R** su una pizza cotta, trovare le categorie **Smelting**, **Smoking** e **Forno per pizza**.
- [ ] Nel **Forno per pizza** visualizzare **1/1** e il layout compatto identico a fornace/affumicatore: pizza cruda in alto a sinistra, carburante in basso, output a destra.
- [ ] Verificare **rotazione automatica dei combustibili** (legna, carbone, secchio di lava ecc. come previsto da JEI), non solo il carbone.
- [ ] Verificare **fiamma animata, freccia di progresso animata** e testo **20 s**.
- [ ] Controllare che **Smelting mostri 60 s e Smoking 30 s**, senza introdurre doppioni nella categoria Pizza Oven.
- [ ] Le informazioni sul nome e sui condimenti della Margherita rimangono nell'item di output.
- [ ] Clic sulla **freccia della GUI** del Pizza Oven: accesso alla categoria corretta.
- [ ] Le cinque anteprime di crafting shapeless, le categorie Cheese Vat, stagionatura, uncino e sale rimangono disponibili.
- [ ] Test reale su macchina, ricette con pizza assortita, hopper, combustibili, multiplayer e server dedicato: no duplicazioni o perdite di ingredienti.

**Limiti della verifica:** la build controlla compilazione e risorse; animazioni, rotazione combustibili e comportamento delle GUI devono essere confermati dall'autore con il client Minecraft.
