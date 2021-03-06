package com.acuddlyheadcrab.MCHungerGames.listeners;

import com.acuddlyheadcrab.MCHungerGames.HungerGames;
import com.acuddlyheadcrab.MCHungerGames.arenas.ArenaIO;
import com.acuddlyheadcrab.MCHungerGames.arenas.Arenas;
import com.acuddlyheadcrab.MCHungerGames.inventories.InventoryHandler;
import com.acuddlyheadcrab.util.PluginInfo;
import com.acuddlyheadcrab.util.YMLKeys;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class HungerListener implements Listener {
    public static HungerGames plugin;
    public static EventPriority priority;
    public HungerListener(HungerGames instance) {plugin = instance;}
    
    public static FileConfiguration config;
    // note to self: config is instantiated ONLY once initConfig() is called.
    public static void initConfig(){
        config = plugin.getConfig();
        BlockListener.initConfig();
        TributeListener.initConfig();
    }
    
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawn(CreatureSpawnEvent event){
        if(config.getBoolean(YMLKeys.OPS_DURGM_NOMOBS.key())){
            String arenakey = Arenas.getNearbyArena(event.getLocation());
            if(arenakey!=null&&Arenas.isInGame(arenakey)) event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e){
        boolean
            ag = config.getBoolean(YMLKeys.AG_ENABLED.key()),
            ag_autog = config.getBoolean(YMLKeys.AG_AUTOJOIN_ENABLED.key())
        ;
        if(config.getBoolean(YMLKeys.OPS_DEBUG_ONPLAYERJOIN.key())){
            PluginInfo.sendPluginInfo("AutoGames: "+ag);
            PluginInfo.sendPluginInfo("AutoGames_AutoJoin: "+ag_autog);
        }
        if(ag){
            if(ag_autog){
                String 
                    configarena = config.getString(YMLKeys.AG_AUTOJOIN_ARENA.key()),
                    arenakey = ArenaIO.getArenaByKey(configarena),
                    joinmsg = config.getString(YMLKeys.AG_AUTOJOIN_JOINMSG.key())
                ;
                if(config.getBoolean(YMLKeys.OPS_DEBUG_ONPLAYERJOIN.key())){
                    PluginInfo.sendPluginInfo("Matching \""+configarena+"\" to "+arenakey);
                    PluginInfo.sendPluginInfo("Player involved: "+e.getPlayer().getName());
                }
                if(arenakey!=null){
                    if(!(Arenas.isInGame(arenakey)||Arenas.isInCountdown(arenakey))){
                        if(!Arenas.getTribNames(arenakey).contains(e.getPlayer().getName())){
                            Arenas.addTrib(arenakey, e.getPlayer());
                            /* Hrm... should i use arenakey which is either an existing arena or NULL, 
                             * OR should i use configarena which is whatever is in their config? */
                            joinmsg = joinmsg.
                                    replaceAll("<arena>", arenakey).
                                    replaceAll("(&([a-f0-9]))", "\u00A7$2")
                            ;
                            e.getPlayer().sendMessage(joinmsg);
                        }
                    }
                } else {
                    PluginInfo.sendPluginInfo("Error joining player to arena \""+configarena+"\": No such arena");
                    PluginInfo.sendPluginInfo("Please replace \""+configarena+"\" in the config with an existing arena");
                }
            }
        }
        String arenakey = Arenas.getArenaByTrib(e.getPlayer());
        if(arenakey!=null){
            if(Arenas.isInGame(arenakey)||Arenas.isInCountdown(arenakey)){
                e.getPlayer().teleport(Arenas.getCenter(arenakey));
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDisconnect(PlayerQuitEvent e){
        String arenakey = Arenas.getArenaByTrib(e.getPlayer());
        if(arenakey!=null){
            if(Arenas.isInGame(arenakey)){
                if(config.getBoolean(YMLKeys.OPS_DURGM_DISQUALONDISC.key())){
                    Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE+""+e.getPlayer().getName()+" has been disqualified from "+arenakey+"!");
                    InventoryHandler.updateInventory(e.getPlayer());
                    Arenas.removeTrib(arenakey, e.getPlayer(), true);
                }
            }
        }
    }
    
}