# ID mapping: 1.12.2 → Fabric

Old meta/damage-based variants are flattened into separate registry entries.

## Blocks

| Old ID (1.12.2) | Meta | New ID |
| --- | --- | --- |
| `openmodularturrets:turret_base` | 0 | `openmodularturrets:turret_base_tier_one` |
| `openmodularturrets:turret_base` | 1 | `openmodularturrets:turret_base_tier_two` |
| `openmodularturrets:turret_base` | 2 | `openmodularturrets:turret_base_tier_three` |
| `openmodularturrets:turret_base` | 3 | `openmodularturrets:turret_base_tier_four` |
| `openmodularturrets:turret_base` | 4 | `openmodularturrets:turret_base_tier_five` |
| `openmodularturrets:disposable_item_turret` | – | `openmodularturrets:disposable_turret` |
| `openmodularturrets:potato_cannon_turret` | – | `openmodularturrets:potato_cannon_turret` |
| `openmodularturrets:crossbow_turret` | – | `openmodularturrets:crossbow_turret` |
| `openmodularturrets:melee_turret` | – | `openmodularturrets:melee_turret` |
| `openmodularturrets:machine_gun_turret` | – | `openmodularturrets:machine_gun_turret` |
| `openmodularturrets:incendiary_turret` | – | `openmodularturrets:incendiary_turret` |
| `openmodularturrets:grenade_turret` | – | `openmodularturrets:grenade_turret` |
| `openmodularturrets:relativistic_turret` | – | `openmodularturrets:relativistic_turret` |
| `openmodularturrets:rocket_turret` | – | `openmodularturrets:rocket_turret` |
| `openmodularturrets:teleporter_turret` | – | `openmodularturrets:teleporter_turret` |
| `openmodularturrets:arc_turret` | – | `openmodularturrets:arc_turret` |
| `openmodularturrets:laser_turret` | – | `openmodularturrets:laser_turret` |
| `openmodularturrets:rail_gun_turret` | – | `openmodularturrets:railgun_turret` |
| `openmodularturrets:lever_block` | – | `openmodularturrets:lever_block` |
| `ompd:hardened` | 0 | `openmodularturrets:hardened_tier_one` |
| `ompd:hardened` | 1 | `openmodularturrets:hardened_tier_two` |
| `ompd:hardened` | 2 | `openmodularturrets:hardened_tier_three` |
| `ompd:hardened` | 3 | `openmodularturrets:hardened_tier_four` |
| `ompd:hardened` | 4 | `openmodularturrets:hardened_tier_five` |
| `ompd:wall` | 0 | `openmodularturrets:wall_tier_one` |
| `ompd:wall` | 1 | `openmodularturrets:wall_tier_two` |
| `ompd:wall` | 2 | `openmodularturrets:wall_tier_three` |
| `ompd:wall` | 3 | `openmodularturrets:wall_tier_four` |
| `ompd:wall` | 4 | `openmodularturrets:wall_tier_five` |
| `ompd:fence` | 0 | `openmodularturrets:fence_tier_one` |
| `ompd:fence` | 1 | `openmodularturrets:fence_tier_two` |
| `ompd:fence` | 2 | `openmodularturrets:fence_tier_three` |
| `ompd:fence` | 3 | `openmodularturrets:fence_tier_four` |
| `ompd:fence` | 4 | `openmodularturrets:fence_tier_five` |
| `ompd:camo_trap` | – | `openmodularturrets:camo_trap` |
| `openmodularturrets:plasma_turret` | – | `openmodularturrets:plasma_turret` |

## Block entities

| Old (1.12.2) | New ID |
| --- | --- |
| `TurretBase` | `openmodularturrets:turret_base` |
| all turret head tile entities | `openmodularturrets:turret` (one type for all turrets) |
| `LeverTileEntity` | `openmodularturrets:lever` |
| `ompd:ownedBlock` (`TileEntityPassiveTiered`) | `openmodularturrets:owned_block` |
| `ompd:camo_trap` (`TileEntityCamo`) | `openmodularturrets:camo_trap` |

Not ported: OMT power generator (`BlockPowerAttachment`, had no function), loot deleter (`BlockBaseAttachment`), expanders.

## Entities

| Old (1.12.2) | New ID |
| --- | --- |
| `openmodularturrets:bulletProjectile` | `openmodularturrets:bullet` |
| `openmodularturrets:blazingClayProjectile` | `openmodularturrets:blazing_clay` |
| `openmodularturrets:grenadeProjectile` | `openmodularturrets:grenade` |
| `openmodularturrets:rocketProjectile` | `openmodularturrets:rocket` |
| `openmodularturrets:arrowProjectile` | `openmodularturrets:arrow` |
| `openmodularturrets:plasmaProjectile` | `openmodularturrets:plasma` |
| *(not registered in oldmod)* `PotatoProjectile` | `openmodularturrets:potato` |
| *(not registered in oldmod)* `DisposableTurretProjectile` | `openmodularturrets:disposable` |

## Items

| Old ID (1.12.2) | Meta | New ID |
| --- | --- | --- |
| `openmodularturrets:ammo_meta` | 0 | `openmodularturrets:ammo_blazing_clay` |
| `openmodularturrets:ammo_meta` | 1 | `openmodularturrets:ammo_bullet` |
| `openmodularturrets:ammo_meta` | 2 | `openmodularturrets:ammo_ferro_slug` |
| `openmodularturrets:ammo_meta` | 3 | `openmodularturrets:ammo_grenade` |
| `openmodularturrets:ammo_meta` | 4 | `openmodularturrets:ammo_rocket` |
| `openmodularturrets:ammo_meta` | 5 | *(not ported: internal "fake disposable item")* |
| `openmodularturrets:intermediate_regular` | 0 | `openmodularturrets:io_bus` |
| `openmodularturrets:intermediate_tiered` | 0 | `openmodularturrets:sensor_tier_one` |
| `openmodularturrets:intermediate_tiered` | 1 | `openmodularturrets:sensor_tier_two` |
| `openmodularturrets:intermediate_tiered` | 2 | `openmodularturrets:sensor_tier_three` |
| `openmodularturrets:intermediate_tiered` | 3 | `openmodularturrets:sensor_tier_four` |
| `openmodularturrets:intermediate_tiered` | 4 | `openmodularturrets:sensor_tier_five` |
| `openmodularturrets:intermediate_tiered` | 5 | `openmodularturrets:chamber_tier_one` |
| `openmodularturrets:intermediate_tiered` | 6 | `openmodularturrets:chamber_tier_two` |
| `openmodularturrets:intermediate_tiered` | 7 | `openmodularturrets:chamber_tier_three` |
| `openmodularturrets:intermediate_tiered` | 8 | `openmodularturrets:chamber_tier_four` |
| `openmodularturrets:intermediate_tiered` | 9 | `openmodularturrets:chamber_tier_five` |
| `openmodularturrets:intermediate_tiered` | 10 | `openmodularturrets:barrel_tier_one` |
| `openmodularturrets:intermediate_tiered` | 11 | `openmodularturrets:barrel_tier_two` |
| `openmodularturrets:intermediate_tiered` | 12 | `openmodularturrets:barrel_tier_three` |
| `openmodularturrets:intermediate_tiered` | 13 | `openmodularturrets:barrel_tier_four` |
| `openmodularturrets:intermediate_tiered` | 14 | `openmodularturrets:barrel_tier_five` |
| `openmodularturrets:upgrade_meta` | 0 | `openmodularturrets:upgrade_accuracy` |
| `openmodularturrets:upgrade_meta` | 1 | `openmodularturrets:upgrade_efficiency` |
| `openmodularturrets:upgrade_meta` | 2 | `openmodularturrets:upgrade_fire_rate` |
| `openmodularturrets:upgrade_meta` | 3 | `openmodularturrets:upgrade_range` |
| `openmodularturrets:upgrade_meta` | 4 | `openmodularturrets:upgrade_scatter_shot` |
| `openmodularturrets:addon_meta` | 0 | `openmodularturrets:addon_concealer` |
| `openmodularturrets:addon_meta` | 1 | `openmodularturrets:addon_damage_amp` |
| `openmodularturrets:addon_meta` | 2 | *(not ported: potentia converter, no function in oldmod)* |
| `openmodularturrets:addon_meta` | 3 | `openmodularturrets:addon_recycler` |
| `openmodularturrets:addon_meta` | 4 | `openmodularturrets:addon_redstone_reactor` |
| `openmodularturrets:addon_meta` | 5 | *(not ported: serial port, computer integration out of scope)* |
| `openmodularturrets:addon_meta` | 6 | `openmodularturrets:addon_solar_panel` |
| `openmodularturrets:addon_meta` | 7 | `openmodularturrets:addon_fake_drops` |

New in the Fabric port (no old ID): `openmodularturrets:generator`.
