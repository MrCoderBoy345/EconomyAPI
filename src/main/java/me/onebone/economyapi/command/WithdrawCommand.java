package me.onebone.economyapi.command;

/*
 * EconomyAPI: Core of economy system for Nukkit
 * Copyright (C) 2016  onebone <jyc00410@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import me.onebone.economyapi.EconomyAPI;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemPaper;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;

public class WithdrawCommand extends Command{
	private EconomyAPI plugin;
	
	public WithdrawCommand(EconomyAPI plugin) {
		super("withdraw", "extract money from the bank", "/withdraw <amount>");
		
		this.plugin = plugin;
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if(!this.plugin.isEnabled()) return false;
		if(!sender.hasPermission("economyapi.command.withdraw")){
			sender.sendMessage(TextFormat.RED + "You don't have permission to use this command.");
			return false;
		}
		
		if(args.length < 1){
			sender.sendMessage(TextFormat.RED + "Usage: " + this.getUsage());
			return true;
		}
        Player player = (Player) sender;
		try{
			double amount = Double.parseDouble(args[0]);
			if(amount <= 0){
				sender.sendMessage(this.plugin.getMessage("takemoney-invalid-number", sender));
				return true;
			}
			
            int result = this.plugin.reduceMoney(player, amount);
			switch(result){
			case EconomyAPI.RET_INVALID:
				sender.sendMessage(this.plugin.getMessage("takemoney-player-lack-of-money", new String[]{player.getName(), Double.toString(amount), Double.toString(this.plugin.myMoney(player))}, sender));
				return true;
			case EconomyAPI.RET_CANCELLED:
				sender.sendMessage(this.plugin.getMessage("takemoney-failed", new String[]{player.getName()}, sender));
				return true;
			case EconomyAPI.RET_SUCCESS:
                sender.sendMessage(TextFormat.YELLOW + "Successfully withdrawn " + this.plugin.getMonetaryUnit() + amount);
                Item moneynote = new ItemPaper();
                CompoundTag tag = new CompoundTag("value");
                tag.putDouble("money", amount);
                moneynote.setNamedTag(tag);
                moneynote.setCustomName(TextFormat.YELLOW + "Money Note\n"+this.plugin.getMonetaryUnit()+moneynote.getNamedTag().getDouble("money")+"\nSigned by "+player.getName());
                player.getInventory().addItem(moneynote);
				return true;
			}
		}catch(NumberFormatException e){
			sender.sendMessage(this.plugin.getMessage("takemoney-must-be-number", sender));
		}
		return true;
	}

}
