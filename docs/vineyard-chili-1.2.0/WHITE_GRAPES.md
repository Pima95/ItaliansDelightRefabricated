# Uva bianca — secondo vitigno (Minecraft 26.3, v1.2.0)

L'uva bianca riutilizza la crescita approvata della vite originale ma con identificativi **indipendenti**:
`white_grape_seeds`, `white_grape_stem`, `white_grape_leaves`, `white_grapes`.

- Nessun rename di `grape_seeds`, `grape_stem`, `grape_leaves`, `grapes`: i vecchi salvataggi rimangono compatibili.
- Farmland, otto età, corda orizzontale, espansione di un blocco per lato e raccolta senza rottura restano invariati.
- La chioma chiara usa gli stessi modelli delle foglie (tinta biome/Serene Seasons); solo la texture del grappolo cambia, senza tinta sul frutto.
- Quando la pergola viene rotta, ogni ramo torna alla stessa corda orizzontale con cui era stato piantato.
- L'uva bianca restituisce soltanto semi bianchi tramite ricetta e pigiatura.
- **Fase attuale**: anche l'uva bianca produce 250 mB dell'attuale `grape_juice`, per non alterare la botte di fermentazione o inventare un vino bianco senza un sistema di fluidi separati. Le varietà di mosto/vino si tratteranno in una fase successiva.
- Texture bianche in pixel art 16×16 e trasparenza alpha 0/255; derivate dalla sagoma di uva già esistente senza modificare il grappolo viola.
- Test automatici: registrazioni, JSON, differenza semi/drop, tinta foglie, ricetta Crushing Tub, JAR, test server con entrambe le varietà.
- Test client: piazza due viti affiancate con tralicci distinti, raccogli e ripeti; verifica che i grappoli e i semi rimangano della varietà piantata e che entrambe seguano lo stesso colore del bioma.

Le scelte sulla futura distinzione tra mosto bianco/rosso e vino bianco/rosso rimangono aperte.
