# CLAUDE.md — OpenModularTurrets, client source set (`src/client/java/omtteam/openmodularturrets/client`)

Moved out of the top-level `CLAUDE.md`, so that it is loaded only when you work in this folder; the top-level file has the index.
Section numbers (§) and relative paths (`../CLAUDE.md`, `../../OpenModularCore`) are those of the top-level file; paths are relative to the repository root.

## 6. Architecture: the client source set

```
(client source set)
omtteam.openmodularturrets.client
├── OMTClient.java               # shared client start-up (loader entrypoints in client/platform/<loader>)
├── api/                         # OMTClientPlugin entrypoint, TurretModelRegistrar
├── model/                       # Java entity-style turret models (TurretModel)
├── render/                      # turret and crank renderers, projectile renderers, item renderers,
│                                # rays (effect/RayRenderer, also the ray packet), placement outline
├── screen/                      # base GUI, configure screen, generator screen
├── tooltip/                     # item tooltips
├── compat/                      # ClientSubLevelCompat, jade/ (tooltips)
└── platform/                    # fabric/, neoforge/: client entrypoints, item renderers (the camo models are in OMLib)
```

## 5. Compat helpers: client

| Difference | Helper |
| --- | --- |
| Client: GUI drawing, tooltips, shift key | `client.compat.ClientCompat`: `blit` (256 × 256 GUI textures), `renderTooltip` / `renderComponentTooltip`, `hasShiftDown()`, `partialTick()`; the mouse wheel goes through `CompatScreen#onScroll` |
| Camouflage models, render layers | baked models: `FabricCamoModel` (`ForwardingBakedModel`) and `NeoForgeCamoModel` (`BakedModelWrapper`), which the loader client entrypoints wrap around the models by model id; Fabric render layers through `BlockRenderLayerMap`, NeoForge through `"render_type"` in the model JSON |
| Block entity and entity renderers, turret models | `BlockEntityRendererRegistrar`, `TurretRenderer`, `LeverRenderer`, `BillboardRenderer`, `ArrowShapedRenderer`, `PlasmaRenderer` and `TurretModel` are plain classes in `src/client/java`; `EntityRendererRegistrar` is shared; `OMTClient` and the entrypoints call the same methods on every branch |
| Turret and crank items (item renderers) | `ItemStackRenderer` (`TurretItemRenderer`, `LeverItemRenderer`), registered by `OMTClient.registerItemRenderers` (Fabric `BuiltinItemRendererRegistry`, NeoForge `getCustomRenderer` through `NeoForgeItemRenderer`) |
| World-space effects (rays, placement outline) | `ClientCompat.lines()` / `lightning()` / `renderLineBox`; Fabric draws them in `WorldRenderEvents.AFTER_TRANSLUCENT` (the label in `LAST`), NeoForge in `RenderLevelStageEvent` (`AFTER_TRANSLUCENT_BLOCKS`, the label in `AFTER_PARTICLES`) |
| Item models | every item has an item model `assets/openmodularturrets/models/item/<id>.json`; the item model definitions `assets/<namespace>/items/` exist only on the `1.21.11` branch |

## Second-loader design decisions: client

**Client**

- Shared client start-up class (`OMTClient`, like `OMLClient`) with `init()`, `registerScreens(ScreenRegistrar)` and tooltip content; `ScreenRegistrar` (generic method, takes `MenuScreens::register` and `event::register`) lives in `client/`.
- Renderers and colour providers expose their content (e.g. a `BlockColor` method plus the block list, a render method taking level, pose stack, buffers and camera); the loader entrypoints register them.
- Render types on NeoForge come from `"render_type"` in the model JSON; Fabric keeps `BlockRenderLayerMap` (keep both in sync).
- OMT (decided by the user): client turret plugins are handed in by the loader client entrypoint like on the server (`OMTClient.init(plugins)`); entity renderers, block entity renderers and model layers go through `OMTClient.registerEntityRenderers(EntityRendererRegistrar)`, `registerBlockEntityRenderers(BlockEntityRendererRegistrar)` (both generic interfaces in `client.render`, there to avoid a package cycle) and `registerModelLayers(BiConsumer)`; on NeoForge the camouflage disguise reaches the model as `CompatBlockEntity.RENDER_DATA` model data, and a colour provider on the hosts forwards the disguise's colour.
  It is registered for this mod's camouflage blocks only (`OMTClient.camoBlocks()`, NeoForge): `CamoModels.camoBlocks()` lists every `CamoBlock` in the registry, and a block takes only one colour provider, so registering it for Open Modular Lighting's floodlights took away their dye colour (fixed on 2026-11-02).

## Jade (from the second-loader design decisions)

**Jade**

- `@WailaPlugin` on the plugin without a loader condition (NeoForge finds it by the annotation, Fabric keeps the `jade` entrypoint).
- The plugin is split in two, so that no client class is loaded on a dedicated server: `compat.jade.OMTJadePlugin` and the providers (server data, and `lines(…)` that turn the data into tooltip lines) stay in main, `client.compat.jade.OMTJadeClientPlugin` and `JadeLinesComponent` (the tooltip) are in the client source set.
  Only `OMTJadePlugin` is registered with Jade (`jade` entrypoint, `@WailaPlugin`): Jade creates every plugin it finds on a dedicated server too, where the client class cannot be loaded (Fabric refuses it).
  Its `registerClient`, which Jade calls only on a client, creates `OMTJadeClientPlugin` by name and forwards to it (fixed on 2026-10-27: before, dedicated servers with Jade logged errors).
- Jade stays off the NeoForge GameTest server (it sends a packet to the vanilla mock players, which NeoForge refuses).
  It is in the NeoForge dev runs (`localRuntime`), which load only the mod itself, not the test mod (`loadedMods`).
