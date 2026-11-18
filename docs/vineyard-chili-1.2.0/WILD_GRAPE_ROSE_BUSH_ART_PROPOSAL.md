# Proposta grafica alternativa: cespuglio di rose

Riferimento visivo: la doppia texture vanilla `minecraft:block/rose_bush_bottom` e `rose_bush_top`. La nuova grafica è disegnata da zero; le rose originali sono usate solo come riferimento per silhouette, distribuzione di rami e fogliame, e posizione dei fiori.

Le due viti rimangono alte due blocchi, e mantengono `half=lower/upper`, generazione naturale e raccolta già implementati. **Nessuna modifica alle meccaniche o agli ID.**

Quattro nuovi PNG candidati, 16×16, con alpha esclusivamente 0/255:

- `wild_grape_vine_rose_bottom.png` e `wild_grape_vine_rose_top.png`
- `wild_white_grape_vine_rose_bottom.png` e `wild_white_grape_vine_rose_top.png`

Composizione: silhouette ariosa come il cespuglio di rose, rami sottili, foglie lobate verde scuro e chiaro, piccoli grappoli visibili tra le foglie al posto dei fiori. Varianti separate viola e giallo-verde.

Per il test in-game, i modelli delle due metà e gli item riferiscono i nuovi sprite `_rose_`. **I quattro file originali senza `_rose_` rimangono intatti**, in modo che il confronto o un ripristino siano immediati: basterà riassociare le texture vecchie nei quattro modelli e nei due modelli item.

Nessun branch creato.
