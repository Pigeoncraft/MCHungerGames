package com.acuddlyheadcrab.MCHungerGames;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.acuddlyheadcrab.util.ConfigKeys;
import com.acuddlyheadcrab.util.Utility;

public class Arenas {
    
	private static FileConfiguration config;
    public static HungerGames hungergames;
    public Arenas(HungerGames instance){
    	hungergames = instance;
	}
    
    public static void submitNewArena(String name, Location center, double maxdist, List<String> gms, List<String> tribs, boolean ingame){
    	String arenapath = ConfigKeys.ARENAS+name;
    	config.set(arenapath, null);
    	config.set(getPathType(name, "center.World"), center.getWorld().getName());
    	config.set(getPathType(name, "center.x"), center.getX());
    	config.set(getPathType(name, "center.y"), center.getY());
    	config.set(getPathType(name, "center.z"), center.getZ());
    	config.set(getPathType(name, "maxdist"), maxdist);
    	config.set(getPathType(name, "gms"), gms);
    	config.set(getPathType(name, "tribs"), tribs);
    	config.set(getPathType(name, "ingame"), ingame);
    	hungergames.saveConfig();
    }
    
    public static void initConfig(){
        config = hungergames.getConfig();
    }
    
    public static void deleteArena(String arenakey){
    	configSet(getPathType(arenakey, "self"), null);
    }
    
    public static Location getCenter(String arenakey){
        String worldstring = config.getString(getPathType(arenakey, "center.world"));
        World world = Bukkit.getWorld(worldstring);
        if(world==null) throw new NullPointerException("Could not find world \""+worldstring+"\"");
        double
            x = config.getDouble(getPathType(arenakey, "center.x")),
            y = config.getDouble(getPathType(arenakey, "center.y")),
            z = config.getDouble(getPathType(arenakey, "center.z"))
        ;
        Location center = new Location(world, x, y, z);
        return center;
    }
    
    public static double getMaxDist(String arenakey){
        return config.getDouble(getPathType(arenakey, "maxdist"));
    }
    
    public static List<String> getGMs(String arenakey){
        return config.getStringList(getPathType(arenakey, "gms"));
    }
    
    public static List<Player> getOnlineGMs(String arenakey){
        List<Player> onlinegms = new ArrayList<Player>();
        for(String gm_s : getGMs(arenakey)){
            try{
                Player player = Bukkit.getPlayer(gm_s); 
                if(player!=null) onlinegms.add(player);
            }catch(NullPointerException e){}
        }
        return onlinegms;
    }
    
    public static List<String> getTribs(String arenakey){
        return config.getStringList(getPathType(arenakey, "tribs"));
    }
    
    public static List<Player> getOnlineTribs(String arenakey){
        List<Player> onlinetribs = new ArrayList<Player>();
        for(String trib_s : getTribs(arenakey)){
            try{
                Player player = Bukkit.getPlayer(trib_s); 
                if(player!=null) onlinetribs.add(player);
            }catch(NullPointerException e){}
        }
        return onlinetribs;
    }
    
    public static boolean isInGame(String arenakey){
        return config.getBoolean(getPathType(arenakey,"ingame"));
    }
    
    public static void renameArena(String arenakey, String renameto){
        submitNewArena(renameto, getCenter(arenakey), getMaxDist(arenakey), getGMs(arenakey), getTribs(arenakey), isInGame(arenakey));
        deleteArena(arenakey);
        hungergames.saveConfig();
    }
    
    public static void setCenter(String arenakey, Location loc){
        configSet(getPathType(arenakey, "center.world"), loc.getWorld().getName());
        configSet(getPathType(arenakey, "center.x"), loc.getX());
        configSet(getPathType(arenakey, "center.y"), loc.getY());
        configSet(getPathType(arenakey, "center.z"), loc.getZ());
    }
    
    public static void setMaxDist(String arenakey, double maxdist){
        configSet(getPathType(arenakey, "maxdist"), maxdist);
    }
    
    public static void setGMs(String arenakey, List<String> gms){
        configSet(getPathType(arenakey, "gms"), gms);
    }
    
    public static void addGM(String arenakey, String player){
    	List<String> gms = getGMs(arenakey);
    	gms.add(player);
    	setGMs(arenakey, gms);
    }
    
    public static void removeGM(String arenakey, String player){
    	List<String> gms = getGMs(arenakey);
    	gms.remove(player);
    	setGMs(arenakey, gms);
    }
    
    public static void setTribs(String arenakey, List<String> tribs){
        configSet(getPathType(arenakey, "tribs"), tribs);
    }
    
    public static void addTrib(String arenakey, String player){
    	List<String> tribs = getTribs(arenakey);
    	tribs.add(player);
    	setTribs(arenakey, tribs);
    }
    
    public static void removeTrib(String arenakey, String player){
    	List<String> tribs = getTribs(arenakey);
    	tribs.remove(player);
    	setTribs(arenakey, tribs);
    }
    
    public static void setInGame(String arenakey, boolean ingame){
        configSet(getPathType(arenakey, "ingame"), ingame);
        List<String> currentgames = config.getStringList(ConfigKeys.CURRENT_GAMES.key());
        if(ingame) currentgames.add(arenakey); else currentgames.remove(arenakey);
        configSet(ConfigKeys.CURRENT_GAMES.key(), currentgames);
    }
    
    
    public static void configSet(String path, Object object){
        config.set(path, object);
        hungergames.saveConfig();
    }
    
    public static String getPathType(String arenakey, String type){
        String arenapath = ConfigKeys.ARENAS.key()+arenakey;
        if(type.equalsIgnoreCase("center.world")) return arenapath+ConfigKeys.ARN_CENTER_WRLD.key();
        if(type.equalsIgnoreCase("center.x")) return arenapath+ConfigKeys.ARN_CENTER_X.key();
        if(type.equalsIgnoreCase("center.y")) return arenapath+ConfigKeys.ARN_CENTER_Y.key();
        if(type.equalsIgnoreCase("center.z")) return arenapath+ConfigKeys.ARN_CENTER_Z.key();
        if(type.equalsIgnoreCase("maxdist"))return arenapath+ConfigKeys.ARN_MAXDIST.key();
        if(type.equalsIgnoreCase("gms"))return arenapath+ConfigKeys.ARN_GMS.key();
        if(type.equalsIgnoreCase("tribs"))return arenapath+ConfigKeys.ARN_TRIBS.key();
        if(type.equalsIgnoreCase("ingame"))return arenapath+ConfigKeys.ARN_INGAME.key();
        if(type.equalsIgnoreCase("countdown"))return arenapath+ConfigKeys.ARN_INCOUNTDOWN.key();
        if(type.equalsIgnoreCase("self")) return arenapath;
        return null;
    }
    
    public static boolean isWithinArena(String arenakey, Location loc){
        if(getCenter(arenakey).getWorld()!=loc.getWorld()) return false;
        if(loc.distance(getCenter(arenakey))<=getMaxDist(arenakey)) return true; else return false; 
    }
    
    public static String getNearbyArena(Location loc){
        for(String arenakey : Utility.getArenasKeys())
            if(isWithinArena(arenakey, loc)) return arenakey;
        return null;
    }
    
    public static boolean isTribute(String arenakey, Player player){
        for(String trib : getTribs(arenakey))
            if(Bukkit.getPlayer(trib)!=null&&player.equals(Bukkit.getPlayer(trib))) return true;
        return false;
    }
    
    public static String getArenaByTrib(Player player){
        for(String arenakey : Utility.getArenasKeys())
            if(isTribute(arenakey, player)) return arenakey;
        return null;
    }
    
    public static boolean isGM(String arenakey, Player player){
        for(String gm : getGMs(arenakey))
            if(Bukkit.getPlayer(gm)!=null&&player.equals(Bukkit.getPlayer(gm))) return true;
        return false;
    }
    
    public static String getArenaByGM(Player player){
        for(String arenakey : Utility.getArenasKeys())
            if(isGM(arenakey, player)) return arenakey;
        return null;
    }

    public static void tpAllOnlineTribs(String arenakey) {
        for(Player trib : getOnlineTribs(arenakey)){
//                TODO: add backup inventories with DAT file handling
            Location 
                center  = getCenter(arenakey), 
                rand = Utility.getRandomChunkLocation(center, 5)
            ;
            
            
//            System.out.println("Trying to teleport "+trib.getName()+" to "+rand.getBlockX()+", "+rand.getBlockY()+", "+rand.getBlockZ());
            System.out.println("Rand: "+rand);
            System.out.println("Trib: "+trib);
            trib.teleport(rand);
            trib.getInventory().clear();
            trib.setGameMode(GameMode.SURVIVAL);
            trib.sendMessage(ChatColor.LIGHT_PURPLE+"You have been teleported to "+arenakey);
        }
    }
    
    public static void startGame(final String arenakey, int countdown){
        Arenas.tpAllOnlineTribs(arenakey);
        configSet(ConfigKeys.GAME_COUNT.key(), getGameCount()+1);
        startCountdown(arenakey, countdown);
    }

    public static int getGameCount() {
        return config.getInt(ConfigKeys.GAME_COUNT.key());
    }

    public static void initGames() {
        List<String> currentgames = config.getStringList(ConfigKeys.CURRENT_GAMES.key());
//        checks for any extra games (aka removes unecessary)
        for(int c=0;c<currentgames.size();c++){
            String game = currentgames.get(c);
            boolean contains = Utility.getArenasKeys().contains(game), ingame = Arenas.isInGame(game);
            if(!contains||!ingame){
                currentgames.remove(c);
            }
        }
//        checks if any arenas are excluded (aka adds not included ones)
        for(String arena : Utility.getArenasKeys()){
            if(currentgames.contains(arena)){
                if(!Arenas.isInGame(arena)) currentgames.remove(arena);
            } else {
                if(Arenas.isInGame(arena)) currentgames.add(arena);
            }
        }
        configSet(ConfigKeys.CURRENT_GAMES.key(), currentgames);
    }
    
    public static boolean isInCountdown(String arenakey){
        return config.getInt(getPathType(arenakey, "countdown"))>0 &&
            config.get(getPathType(arenakey, "countdown"))!=null
        ;
    }
    
    public static void startCountdown(final String arenakey, final int seconds){
//        initialize path
        System.out.println("Setting "+arenakey+" in countdown with "+seconds+" to go!");
        config.set(getPathType(arenakey, "countdown"), seconds);
//        cycle through each second
        Bukkit.getScheduler().scheduleSyncRepeatingTask(hungergames, new Runnable() {
            @Override
            public void run() {
                int second = config.getInt(getPathType(arenakey, "countdown"));
                Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE+"[MCHungerGames] "+second);
                if(second==0){
                    Bukkit.getScheduler().cancelTasks(hungergames);
                    configSet(getPathType(arenakey, "countdown"), null);
                    setInGame(arenakey, true);
                    Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE+arenakey+" is now in game!");
                } else countdown(arenakey);
            }
        }, 20, 20);
//        set arean in game here
    }
    
    public static void countdown(String arenakey){
        String path = getPathType(arenakey, "countdown");
        configSet(path, config.getInt(path)-1);
    }
}
