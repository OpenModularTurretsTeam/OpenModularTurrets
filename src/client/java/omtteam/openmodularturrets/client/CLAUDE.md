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
├── compat/                      # ClientSubLevelCompat (identity pose), jade/ (tooltips)
└── platform/                    # fabric/, neoforge/: client entrypoints
```

## 5. Compat helpers: client

| Difference | Helper |
| --- | --- |
| Client: GUI drawing, tooltips, shift key, input events | `client.compat.ClientCompat`: `blit` (256 × 256 GUI textures; takes the render pipeline), `renderTooltip` / `renderComponentTooltip` (`setTooltipForNextFrame`), `hasShiftDown()`, `partialTick()`; GUI text colours need an alpha channel (`0xFF000000`): from 1.21.6 on, text without one is not drawn. Input events are `onClick(MouseButtonEvent, boolean)`, `keyPressed(KeyEvent)` and `onRelease(MouseButtonEvent)`; the mouse wheel goes through `CompatScreen#onScroll` |
| Camouflage models, render layers | block state models: `FabricCamoModel` (`WrapperBlockStateModel`) and `NeoForgeCamoModel` (`DelegateBlockStateModel`), in OMLib; the loader client entrypoints wrap the models by block state; Fabric render layers through `BlockRenderLayerMap` (with `ChunkSectionLayer`), NeoForge through `"render_type"` in the model JSON |
| Block entity and entity renderers, turret models (render states) | `src/client` holds `BlockEntityRendererRegistrar`, `TurretRenderer`, `LeverRenderer`, `BillboardRenderer`, `ArrowShapedRenderer`, `PlasmaRenderer` and `TurretModel` (decided by the user). A renderer copies what it needs into a render state when the frame is extracted and draws in `submit` through `SubmitNodeCollector`; drawing is deferred, so a model shared by many blocks must not be posed at submit time: `TurretModel` and `LeverRenderer.LeverModel` are `Model<S>` whose pose is applied in `setupAnim` right before drawing. `EntityRendererRegistrar` is shared (`EntityRendererProvider` has one type argument); `OMTClient` and the entrypoints call the same methods on both loaders |
| Turret and crank items (item renderers) | special model renderers (decided by the user): `TurretSpecialRenderer` / `LeverSpecialRenderer` with the item model types `openmodularturrets:turret` (with `"turret": "<id>"`) and `openmodularturrets:lever` in the item model definitions (`minecraft:special`, `base` = the old `builtin/entity` model for the display transforms), registered by `OMTClient.registerSpecialItemModels` (NeoForge: `RegisterSpecialModelRendererEvent`; Fabric API has no hook, so the client mixin `SpecialModelRenderersMixin` in `openmodularturrets.client.mixins.json` adds the types to vanilla's table) |
| World-space effects (rays, placement outline) | `ClientCompat.lines()` / `lightning()` / `renderLineBox`; Fabric draws them in `WorldRenderEvents.END_MAIN` (the new world render events have no after-translucent or last event), NeoForge in `RenderLevelStageEvent.AfterTranslucentBlocks` / `AfterParticles` |
| Item models | every item needs an item model definition `assets/openmodularturrets/items/<id>.json` that points at its `models/item/<id>.json`; without one the item shows the missing texture. The turret and lever items (item renderer, `builtin/entity` parents) get theirs with the item renderers |

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
- The plugin is split in two, because Jade 21's `ITooltip` extends a client class: `compat.jade.OMTJadePlugin` and the providers (server data, and `lines(…)` that turn the data into tooltip lines) stay in main, `client.compat.jade.OMTJadeClientPlugin` and `JadeLinesComponent` (the tooltip) are in the client source set.
  Only `OMTJadePlugin` is registered with Jade (`jade` entrypoint, `@WailaPlugin`): Jade creates every plugin it finds on a dedicated server too, where the client class cannot be loaded (Fabric refuses it, NeoForge fails on its client types).
  Its `registerClient`, which Jade calls only on a client, creates `OMTJadeClientPlugin` by name and forwards to it (registering the client half directly crashes dedicated servers with Jade).
- Jade stays off the NeoForge GameTest server (it sends a packet to the vanilla mock players, which NeoForge refuses).
  It is in the NeoForge dev runs (`localRuntime`), which load only the mod itself, not the test mod (`loadedMods`).
