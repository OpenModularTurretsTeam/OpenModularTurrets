# CLAUDE.md — OpenModularTurrets, client source set (`src/client/java/omtteam/openmodularturrets/client`)

Moved out of the top-level `CLAUDE.md`, so that it is loaded only when you work in this folder; the top-level file has the index.
Section numbers (§) and relative paths (`../CLAUDE.md`, `../../OpenModularCore`) are those of the top-level file; paths are relative to the repository root.

## 6. Architecture: the client source set

```
(client source set)
omtteam.openmodularturrets.client
├── OMTClient.java               # shared client start-up (loader entrypoints in client/platform/<loader>)
├── api/                         # OMTClientPlugin entrypoint, TurretModelRegistrar
├── model/                       # Java entity-style turret models
├── render/                      # turret and crank renderers, projectile renderers, item renderers,
│                                # rays (effect/RayRenderer, also the ray packet), placement outline
├── screen/                      # base GUI, configure screen, generator screen
├── tooltip/                     # item tooltips
├── compat/                      # ClientSubLevelCompat (identity pose), jade/ (tooltips)
└── platform/                    # fabric/, forge/: client entrypoints, item renderers
```

## 5. Compat helpers: client

| Difference | Helper |
| --- | --- |
| Client: GUI drawing, tooltips, shift key | `client.compat.ClientCompat`: `blit` (256 × 256 GUI textures), `renderTooltip` / `renderComponentTooltip`, `hasShiftDown()`, `partialTick()`; the mouse wheel goes through `CompatScreen#onScroll` |
| Camouflage models, render layers | baked models: `FabricCamoModel` (`ForwardingBakedModel`) and `ForgeCamoModel` (`BakedModelWrapper`), which the loader client entrypoints wrap around the camouflage blocks' models (by model id); Fabric render layers through `BlockRenderLayerMap`, Forge through `"render_type"` in the model JSON |
| Block entity and entity renderers, turret models | `BlockEntityRendererRegistrar`, `TurretRenderer`, `LeverRenderer`, `BillboardRenderer`, `ArrowShapedRenderer`, `PlasmaRenderer` and `TurretModel` draw directly into the buffers; `EntityRendererRegistrar` is shared; `OMTClient` and the entrypoints call the same methods on all branches |
| Turret and crank items (item renderers) | `ItemStackRenderer` (Fabric `BuiltinItemRendererRegistry`, Forge `ForgeItemRenderer`), used by the items whose models have the parent `builtin/entity` |
| World-space effects (rays, placement outline) | `ClientCompat.lines()` / `lightning()` / `renderLineBox`; Fabric draws them in `WorldRenderEvents.AFTER_TRANSLUCENT` / `LAST`, Forge in `RenderLevelStageEvent` at the stages `AFTER_TRANSLUCENT_BLOCKS` / `AFTER_PARTICLES` (`OMTForgeClient`) |
| Item models | every item has an item model `assets/openmodularturrets/models/item/<id>.json`; no item model definitions (`assets/<namespace>/items/`) on this branch (they exist only on `1.21.11`) |

## Second-loader design decisions: client

**Client**

- Shared client start-up class (`OMTClient`, like `OMLClient`) with `init()`, `registerScreens(ScreenRegistrar)` and tooltip content; `ScreenRegistrar` (generic method, takes `MenuScreens::register` and `event::register`) lives in `client/`.
- Renderers and colour providers expose their content (e.g. a `BlockColor` method plus the block list, a render method taking level, pose stack, buffers and camera); the loader entrypoints register them.
- Render types on Forge come from `"render_type"` in the model JSON; Fabric keeps `BlockRenderLayerMap` (keep both in sync).
- OMT (decided by the user): client turret plugins are handed in by the loader client entrypoint like on the server (`OMTClient.init(plugins)`); entity renderers, block entity renderers and model layers go through `OMTClient.registerEntityRenderers(EntityRendererRegistrar)`, `registerBlockEntityRenderers(BlockEntityRendererRegistrar)` (both generic interfaces in `client.render`, there to avoid a package cycle) and `registerModelLayers(BiConsumer)`; on Forge the camouflage disguise reaches the model as `CompatBlockEntity.RENDER_DATA` model data, and a colour provider on the hosts forwards the disguise's colour.
  It is registered for this mod's camouflage blocks only (`OMTClient.camoBlocks()`, Forge): a block takes only one colour provider, so registering it for every `CamoBlock` (`CamoModels.camoBlocks()`) would take away the dye colour of Open Modular Lighting's floodlights.

## Jade (from the second-loader design decisions)

**Jade**

- `@WailaPlugin` on the plugin without a loader condition (Forge finds it by the annotation, Fabric keeps the `jade` entrypoint).
- The plugin is split in two, so that no client class is loaded on a dedicated server: `compat.jade.OMTJadePlugin` and the providers (server data, and `lines(…)` that turn the data into tooltip lines) stay in main, `client.compat.jade.OMTJadeClientPlugin` and `JadeLinesComponent` (the tooltip) are in the client source set.
  Only `OMTJadePlugin` is registered with Jade (`jade` entrypoint, `@WailaPlugin`): Jade creates every plugin it finds on a dedicated server too, where the client class cannot be loaded (Fabric refuses it).
  Its `registerClient`, which Jade calls only on a client, creates `OMTJadeClientPlugin` by name and forwards to it.
- Jade stays off the Forge GameTest server.
  It is in the Forge dev runs (`localRuntime`), which load only the mod itself, not the test mod (`loadedMods`).
