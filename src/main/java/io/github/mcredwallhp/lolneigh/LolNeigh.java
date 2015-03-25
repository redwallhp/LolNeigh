package io.github.mcredwallhp.lolneigh;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;

public final class LolNeigh extends JavaPlugin implements Listener {


    String horseRespawnWorld;
    Double horseRespawnX;
    Double horseRespawnY;
    Double horseRespawnZ;


    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(this, this);
        this.horseRespawnWorld = this.getConfig().getString("horse_respawn.world", "world");
        this.horseRespawnX = this.getConfig().getDouble("horse_respawn.x", 0);
        this.horseRespawnY = this.getConfig().getDouble("horse_respawn.y", 70);
        this.horseRespawnZ = this.getConfig().getDouble("horse_respawn.z", 0);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = (Player) sender;

        if (!player.hasPermission("lolneigh.set")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to do that.");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("lolneigh-set")) {
            this.setHorseRespawnPoint(player.getLocation());
            player.sendMessage(ChatColor.GOLD + "Horse respawn location set!");
            return true;
        }

        return false;

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

            World toWorld = Bukkit.getWorld(this.horseRespawnWorld);
            Location toLoc = new Location(toWorld, this.horseRespawnX, this.horseRespawnY, this.horseRespawnZ);

            // Ensure the chunk is loaded before teleporting
            Chunk toChunk = toLoc.getChunk();
            if (!toChunk.isLoaded()) {
                toChunk.load();
            }

            entity.teleport(toLoc);

        }

    }


}
