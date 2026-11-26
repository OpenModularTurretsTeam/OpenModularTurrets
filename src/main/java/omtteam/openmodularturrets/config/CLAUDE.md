# CLAUDE.md — OpenModularTurrets, config (`config/`)

Moved out of the top-level `CLAUDE.md`, so that it is loaded only when you work in this folder; the top-level file has the index.
Section numbers (§) and relative paths (`../CLAUDE.md`, `../../OpenModularCore`) are those of the top-level file; paths are relative to the repository root.

## Config

- No config library.
  Write a minimal Gson-based JSON config at `config/openmodularturrets.json`.
- Load it on init, write defaults if missing, and validate ranges.
- Fields are documented with `@Comment` and range-checked with `@Range` (package `config`).
  `ConfigWriter` writes them as `//` comments (JSONC) above each key, together with the default, and `ConfigRanges` clamps on load.
  Add both annotations to every new config field.
- Server-authoritative values that clients need (e.g. for tooltips) are synced on join.

## Config versions (from "Power & items")

- **Config versions:** `configVersion` in the config file; `ConfigMigration` (OMLib) runs the `ConfigMigrationSteps.STEPS` newer than the file, replacing old default values (only if unchanged) when a file of an older version is loaded.
  Bump `CURRENT_VERSION` and add a step when a default changes.
