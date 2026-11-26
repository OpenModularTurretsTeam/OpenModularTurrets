# CLAUDE.md — OpenModularTurrets, OpenComputers: Rebooted (`platform/neoforge/opencomputers/`)

Moved out of the top-level `CLAUDE.md`, so that it is loaded only when you work in this folder; the top-level file has the index.
Section numbers (§) and relative paths (`../CLAUDE.md`, `../../OpenModularCore`) are those of the top-level file; paths are relative to the repository root.

## Computers: OpenComputers: Rebooted (1.21.1-neoforge only, decided by the user on 2026-11-04)

The 1.12.2 computer integration, ported for OpenComputers: Rebooted (the only OpenComputers for the modern versions, NeoForge only); CC: Tweaked stays out of scope.

- [x] Core: `core.computer.BaseComputerCommands` (1.12.2's commands and messages; `ComputerTurrets`, `PlayerNames`), computer control in `TurretBaseLogic` (`setComputerControl`, saved as `ComputerControl`), `acceptsComputers()` (tier 5 or Serial Port); unit tests
- [x] Turrets: `TurretBlockEntity.setYawPitch` / `setAutoFire` / `forceShot`, `TurretBaseBlockEntity.getTurret(face)`, `blockentity.base.BaseComputerTurrets`; `ComputerControlGameTests`
- [x] Serial Port addon (`addon_serial_port`, 1.12.2 recipe and texture)
- [x] OpenComputers glue in `platform/neoforge/opencomputers` (`OpenComputersCompat`, `TurretBaseDriver`, `TurretBaseEnvironment` with 1.12.2's callbacks, `NeoForgePlayerNames` over online players and NeoForge's `UsernameCache`), registered in `OMTNeoForge.onCommonSetup`; `OpenComputersGameTests` on the NeoForge 1.21.1 game test server; OpenComputers in the dev runs and `runenv/1.21.1-neoforge`
- [ ] Checked by hand in a NeoForge 1.21.1 client with OpenComputers (Adapter next to a base, the Lua calls, aiming and firing under computer control)
