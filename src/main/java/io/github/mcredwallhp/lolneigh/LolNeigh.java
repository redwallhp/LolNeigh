package io.github.mcredwallhp.lolneigh;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;

import java.util.UUID;

public final class LolNeigh extends JavaPlugin implements Listener {


    String horseRespawnWorld;
    Double horseRespawnX;
    Double horseRespawnY;
    Double horseRespawnZ;
    Boolean bedMode;
    Boolean logThings;


    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(this, this);
        this.loadConfigOptions();
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = (Player) sender;

        if (!player.hasPermission("lolneigh.admin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to do that.");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("lolneigh-set")) {
            this.setHorseRespawnPoint(player.getLocation());
            player.sendMessage(ChatColor.GOLD + "Horse respawn location set!");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("lolneigh-reload")) {
            this.reloadConfig();
            this.loadConfigOptions();
            player.sendMessage(ChatColor.GOLD + "LolNeigh config reloaded!");
            return true;
        }

        return false;

    }


    public void loadConfigOptions() {
        this.horseRespawnWorld = this.getConfig().getString("horse_respawn.world", "world");
        this.horseRespawnX = this.getConfig().getDouble("horse_respawn.x", 0);
        this.horseRespawnY = this.getConfig().getDouble("horse_respawn.y", 70);
        this.horseRespawnZ = this.getConfig().getDouble("horse_respawn.z", 0);
        this.bedMode = this.getConfig().getBoolean("bed_mode", false);
        this.logThings = this.getConfig().getBoolean("log_things", true);
    }


    public void setHorseRespawnPoint(Location loc) {
        this.horseRespawnWorld = loc.getWorld().getName();
        this.horseRespawnX = loc.getX();
        this.horseRespawnY = loc.getY();
        this.horseRespawnZ = loc.getZ();
        this.getConfig().set("horse_respawn.world", this.horseRespawnWorld);
        this.getConfig().set("horse_respawn.x", this.horseRespawnX);
        this.getConfig().set("horse_respawn.y", this.horseRespawnY);
        this.getConfig().set("horse_respawn.z", this.horseRespawnZ);
        this.saveConfig();
    }


    @EventHandler
    public void onEntityPortalExitEvent(EntityPortalEnterEvent e) {

        Location portalLoc = e.getLocation();
        Entity entity = e.getEntity();

        // If entity exiting portal is a horse, and the world type is the End, handle the teleport
        if (entity.getType() == EntityType.HORSE && portalLoc.getWorld().getEnvironment() == World.Environment.THE_END) {
            if (this.bedMode) {
                this.teleportHorseToPlayerBed(entity);
            }
             else {
                this.teleportHorseToCoordinates(entity);
            }
        }

    }


    public void teleportHorseToCoordinates(Entity horse) {

        World toWorld = Bukkit.getWorld(this.horseRespawnWorld);
        Location toLoc = new Location(toWorld, this.horseRespawnX, this.horseRespawnY, this.horseRespawnZ);

        // Ensure the chunk is loaded before teleporting
        Chunk toChunk = toLoc.getChunk();
        if (!toChunk.isLoaded()) {
            toChunk.load();
        }

        horse.teleport(toLoc);

        this.logHorseEvent(horse, false);

    }


    public void teleportHorseToPlayerBed(Entity horse) {

        Horse h = (Horse) horse;
        AnimalTamer tamer = h.getOwner();

        // Horse has no owner, teleport to predefined spawn
        if (tamer == null) {
            this.teleportHorseToCoordinates(horse);
            this.logHorseEvent(horse, false);
            return;
        }

        // Teleport horse to player bed, falling back to predetermined spawn if not set
        UUID ownerUUID = tamer.getUniqueId();
        OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerUUID);
        Location bedLoc = owner.getBedSpawnLocation();
        if (bedLoc != null) {
            horse.teleport(bedLoc);
            this.logHorseEvent(horse, true);
        } else {
            this.teleportHorseToCoordinates(horse);
            this.logHorseEvent(horse, false);
        }

    }


    public void logHorseEvent(Entity entity, Boolean isGoingToBed) {

        if (!this.logThings) return;

        String msg;
        String owner;
        String owner_uuid;
        Horse h = (Horse) entity;
        AnimalTamer tamer = h.getOwner();

        if (tamer == null) {
            owner = "nobody";
            owner_uuid = "untamed";
        } else {
            owner = tamer.getName();
            owner_uuid = tamer.getUniqueId().toString();
        }

        if (isGoingToBed) {
            msg = String.format("[LolNeigh] Teleporting horse owned by %s (%s) to player bed.", owner, owner_uuid);
        } else {
            msg = String.format("[LolNeigh] Teleporting horse owned by %s (%s) to respawn point.", owner, owner_uuid);
        }

        Bukkit.getLogger().info(msg);

    }


}
