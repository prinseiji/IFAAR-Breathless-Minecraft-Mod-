# Batch 71: Carrion fixes

- **It went off by itself.** The spores below Y=0 hurt every mob that is not on the immune list, and any damage set the Carrion off. It is now immune to the spores (`MobRules.immune`). On top of that it ignores damage with no one behind it (spores, suffocation, drowning); only attacks, projectiles, fire and explosions set it off.
- **Silence** makes you as quiet as sneaking. **Silence + Invisibility** makes it ignore you unless your hitbox touches its own.
