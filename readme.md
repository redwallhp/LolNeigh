# LolNeigh

So, you have a fancy spawn building on your Minecraft server. What happens when someone takes their horse into the End and then rides it back out the exit portal? It gets trapped in spawn!

With LolNeigh, this problem is a thing of a past. The plugin catches horses as the exit the End and teleports them to safety, either a predefined corral or, with an optional config, the owner's bed.


## Commands

* `/lolneigh-set` — Set the location horses are teleported to
* `/lolneigh-reload` — Reload config


# Permissions

* `lolneigh.admin` — Allows access to the plugin's commands


## Config

```
# The location horses should go to instead of spawn
horse_respawn:
    world: "world"
    x: 0
    y: 70
    z: 0


# Optionally, attempt to move horses to the player's bed first,
# falling back to the respawn point above if that fails.
bed_mode: false


# Log teleports to console
log_things: true
```


## License

GNU Public License