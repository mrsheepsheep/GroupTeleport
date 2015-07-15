package fr.mrsheepsheep.grouptp;

import net.ess3.api.InvalidWorldException;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.commands.WarpNotFoundException;

public class GroupTP extends JavaPlugin implements CommandExecutor {

	Essentials ess = null;
	Permission perms = null;

	public void onEnable(){
		setupEssentials();
		setupPermissions();
		getCommand("gtp").setExecutor(this);
	}

	public boolean isGroup(String string){
		for (String group : perms.getGroups())
			if (group.equalsIgnoreCase(string)) return true;
		return false;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){

		if (sender.hasPermission("grouptp.tp")){
			if (args.length > 0){
				if (isGroup(args[0])){
					if (args.length == 2){
						Location loc = null;
						if (args[1].equalsIgnoreCase("me")){
							if (sender instanceof Player){
								Player p = (Player) sender;
								loc = p.getLocation();
							}
							else
								sender.sendMessage(ChatColor.RED + "Vous devez être en jeu pour faire ça!");
						}
						else
						{
							if (getServer().getPlayer(args[1]) != null){
								loc = getServer().getPlayer(args[1]).getLocation();
							}
							else
								sender.sendMessage(ChatColor.RED + "Le joueur n'est pas connecté.");
						}
						if (loc != null){
							int n = teleportGroup(args[0], loc);
							sender.sendMessage(ChatColor.GREEN + "" + n + " joueurs téléportés.");
						}
					}
					else if (args.length == 3){
						if (args[1].equalsIgnoreCase("warp")){
							Location loc = null;
							try {
								loc = ess.getWarps().getWarp(args[2]);
								int n = teleportGroup(args[0], loc);
								sender.sendMessage(ChatColor.GREEN + "" + n + " joueurs téléportés vers le warp " + args[2] + ".");
							} catch (WarpNotFoundException e) {
								sender.sendMessage(ChatColor.RED + "Warp invalide.");
							} catch (InvalidWorldException e) {
								sender.sendMessage(ChatColor.RED + "Monde du warp invalide.");
							}
						}

					}
					else if (args.length == 5){
						if (isDouble(args[1]) && isDouble(args[2]) && isDouble(args[3])){
							double x = Double.valueOf(args[1]);
							double y = Double.valueOf(args[2]);
							double z = Double.valueOf(args[3]);
							World w = null;
							if (getServer().getWorld(args[4]) != null){
								w = getServer().getWorld(args[4]);
								Location loc = new Location(w, x, y, z);
								int n = teleportGroup(args[0], loc);
								sender.sendMessage(ChatColor.GREEN + "" + n + " joueurs téléportés.");
							}
							else
							{
								sender.sendMessage(ChatColor.RED + "Monde invalide!");
							}
						}
						else
						{
							sender.sendMessage(ChatColor.RED + "Coordonnées invalides!");
						}
					}
				}
				else
					sender.sendMessage(ChatColor.RED + args[0] + " n'est pas un groupe valide.");				
			}
			else
			{
				sender.sendMessage(ChatColor.GOLD + "Commandes possibles:");
				sender.sendMessage("* " + ChatColor.YELLOW + "/gtp <groupe> <joueur>");
				sender.sendMessage("* " + ChatColor.YELLOW + "/gtp <groupe> <X> <Y> <Z> <monde>");
				sender.sendMessage("* " + ChatColor.YELLOW + "/gtp <groupe> warp <warp>");
				sender.sendMessage("* " + ChatColor.YELLOW + "/gtp <groupe> me");
			}
		}
		else
			sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
		return true;
	}

	public int teleportGroup(String group, Location loc){
		int n = 0;
		for (Player p : getServer().getOnlinePlayers()){
			if (perms.getPrimaryGroup(p).equalsIgnoreCase(group)){
				n++;
				p.teleport(loc);
				p.sendMessage(ChatColor.GREEN + "Vous avez été téléporté!");
			}
		}
		return n;

	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}

	private void setupEssentials(){
		Plugin p = getServer().getPluginManager().getPlugin("Essentials");
		if (p != null){
			ess = (Essentials) p;
		}
		else
		{
			getLogger().warning("Erreur ! Essentials n'a pas été trouvé :(");
			setEnabled(false);
		}
	}

	public static boolean isDouble(String s) {
		try { 
			Double.parseDouble(s); 
		} catch(NumberFormatException e) { 
			return false; 
		} catch(NullPointerException e) {
			return false;
		}
		return true;
	}
}
