# CLAUDE.md — OpenModularTurrets, security, trusted players and ownership (`security/`)

Moved out of the top-level `CLAUDE.md`, so that it is loaded only when you work in this folder; the top-level file has the index.
Section numbers (§) and relative paths (`../CLAUDE.md`, `../../OpenModularCore`) are those of the top-level file; paths are relative to the repository root.

## Security / trusted players

- Each base stores:
  - **owner** (UUID + last known name)
  - a list of **TrustedPlayer** (UUID, name, **access level**)
- Access levels gate actions (OMLib's `core.security`):
  - a low level can open the GUI / view
  - a higher level can change targeting and settings
  - the highest level can manage trusted players
  - nobody but the owner can change the owner
- **Every C2S packet must re-validate permissions on the server** (player distance to the block, access level for the specific action).
  Never trust the client.
- Breaking or opening a base also respects permissions.
- **Access profiles:** a `TrustManager` only uses the levels of its `AccessProfile` (`ALL` for bases; `TRUSTED_AND_ADMIN` for the camouflage trap, whose trusted players can stand on it and whose admins use its menus and manage the list).
  Requested, loaded and pasted levels are clamped to the profile.
  Everything that manages a trust list goes through the `TrustHost` interface.
- **Teams:** members of the owner's vanilla scoreboard team count as trusted, but only at the lowest rank, so turrets do not shoot them.
- Use UUIDs for identity; names are display-only.
  Resolve names to UUIDs server-side via the server's profile cache when adding a player.
  A cache miss is a blocking request to Mojang, so `PlayerLookup` runs it on a background thread (one lookup per player at a time) and the handler re-checks distance and permission when the result arrives.

## Ownership for adventure maps (requested by the user on 2026-10-25; designed step by step with the user)

Unowned blocks no longer drop; anyone can claim them, and map makers get a creative wand.

- [x] Core: `TrustManager.clearOwner()` / `claim(player)`, `core.world.FloodFill` (breadth-first over faces, limit)
- [x] Unowned bases stay and are inert (`bases.unownedBasesActive`, default false); turrets attach to an unowned base only for game masters (`canUseGameMasterBlocks`)
- [x] Claiming: `security.OwnedHost` (bases and camo trap through `TrustHost` defaults, passive blocks through `OwnedBlockEntity`); a main-hand right-click without sneaking on an unowned block (on a turret: its base) claims it and the connected unowned blocks (`block.ClaimSpread`, in `block` to keep `security` out of the package cycle; turrets connect; `bases.claimSpreadLimit`, default 1024, nearest first); fake players only with `general.fakePlayersCanClaim`
- [x] Visibility: green sparkle (`block.UnownedMarker`, `animateTick`) on unowned bases, their turrets, hardened blocks, walls and fences (not the camo trap); Jade owner line "nobody (right-click to claim)" and a new provider for the passive blocks
- [x] Disown: owner-only "Disown" button with `ConfirmScreen` on the configure screen and the camo trap screen (`TrustPackets.Disown`); clears owner and trust list, open menus close through `stillValid`
- [x] Ownership wand (`ownership_wand`, game masters only): right-click claims (trust list cleared), sneak-right-click resets, spreading to connected blocks of the same owner (sneak-right-click into the air toggles, default on); placeholder texture (copy of the memory card)
- [x] Wand immunity: players holding it are still targeted and fired at, but `TurretImmunity` keeps everything off them (`TurretDamage.canDamage`, so also `OMTApi.canDamage`; the effect weapon dispatch; turret explosions deal a `TurretDamageSource` that `LivingEntityMixin` cancels)
- [ ] Real texture for the ownership wand
