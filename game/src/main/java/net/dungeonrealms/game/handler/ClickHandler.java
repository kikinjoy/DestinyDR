package net.dungeonrealms.game.handler;

import lombok.Getter;
import net.dungeonrealms.DungeonRealms;
import net.dungeonrealms.GameAPI;
import net.dungeonrealms.common.game.database.DatabaseAPI;
import net.dungeonrealms.common.game.database.data.EnumData;
import net.dungeonrealms.common.game.database.data.EnumOperators;
import net.dungeonrealms.common.game.database.player.rank.Rank;
import net.dungeonrealms.game.donation.DonationEffects;
import net.dungeonrealms.game.item.ItemType;
import net.dungeonrealms.game.item.PersistentItem;
import net.dungeonrealms.game.item.items.functional.ItemGemNote;
import net.dungeonrealms.game.item.items.functional.ecash.ItemMount;
import net.dungeonrealms.game.item.items.functional.ecash.ItemPet;
import net.dungeonrealms.game.item.items.functional.ecash.ItemPetSelector;
import net.dungeonrealms.game.mastery.GamePlayer;
import net.dungeonrealms.game.mastery.Utils;
import net.dungeonrealms.game.mechanic.ParticleAPI;
import net.dungeonrealms.game.mechanic.PlayerManager;
import net.dungeonrealms.game.miscellaneous.ItemBuilder;
import net.dungeonrealms.game.miscellaneous.TradeCalculator;
import net.dungeonrealms.game.player.chat.Chat;
import net.dungeonrealms.game.player.combat.CombatLog;
import net.dungeonrealms.game.player.inventory.NPCMenus;
import net.dungeonrealms.game.player.inventory.PlayerMenus;
import net.dungeonrealms.game.player.inventory.SupportMenus;
import net.dungeonrealms.game.player.menu.CraftingMenu;
import net.dungeonrealms.game.player.support.Support;
import net.dungeonrealms.game.world.entity.type.mounts.EnumMounts;
import net.dungeonrealms.game.world.entity.type.pet.EnumPets;
import net.dungeonrealms.game.world.entity.util.EntityAPI;
import net.dungeonrealms.game.world.entity.util.MountUtils;
import net.dungeonrealms.game.world.entity.util.PetUtils;
import net.minecraft.server.v1_9_R2.NBTTagCompound;

import org.bukkit.*;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Nick on 10/2/2015.
 * 
 * TODO: Convert to new recode.
 */
public class ClickHandler {

	@Getter
    private static ClickHandler instance = new ClickHandler();

    public void doClick(InventoryClickEvent event) {
        String name = event.getInventory().getName();
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();
        if (slot == -999) return;

        switch (name) {
            case "Merchant":
                Inventory tradeWindow = event.getInventory();
                if (event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
                    event.setCancelled(true);
                    return;
                }
                if (!(event.isShiftClick()) || (event.isShiftClick() && slot < 27)) {
                    if (!(event.getSlotType() == InventoryType.SlotType.CONTAINER)) {
                        return;
                    }
                    if (event.getInventory().getType() == InventoryType.PLAYER) {
                        return;
                    }
                    if (slot > 26 || slot < 0) {
                        return;
                    }
                    if (!(slot == 0 || slot == 1 || slot == 2 || slot == 3 || slot == 9 || slot == 10 || slot == 11 || slot == 12 || slot == 18 || slot == 19
                            || slot == 20 || slot == 21) && !(slot > 27)) {
                        event.setCancelled(true);
                        tradeWindow.setItem(slot, tradeWindow.getItem(slot));
                        player.setItemOnCursor(event.getCursor());
                        player.updateInventory();
                    } else if (!(event.isShiftClick())) {
                        if ((event.getCursor() == null || event.getCursor().getType() == Material.AIR && event.getCurrentItem() != null && CraftItemStack.asNMSCopy(event.getCurrentItem()) != null && CraftItemStack.asNMSCopy(event.getCurrentItem()).getTag() != null && (!CraftItemStack.asNMSCopy(event.getCurrentItem()).getTag().hasKey("acceptButton")))) {
                            event.setCancelled(true);
                            ItemStack slotItem = tradeWindow.getItem(slot);
                            tradeWindow.setItem(slot, new ItemStack(Material.AIR));
                            event.setCursor(slotItem);
                            player.updateInventory();
                        } else if ((event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR && event.getCursor() != null)) {
                            if (CraftItemStack.asNMSCopy(event.getCurrentItem()) != null && CraftItemStack.asNMSCopy(event.getCurrentItem()).getTag() != null && (CraftItemStack.asNMSCopy(event.getCurrentItem()).getTag().hasKey("subType"))) {
                                event.setCancelled(true);
                                player.updateInventory();
                            }
                            event.setCancelled(true);
                            ItemStack currentItem = event.getCursor();
                            tradeWindow.setItem(slot, currentItem);
                            event.setCursor(new ItemStack(Material.AIR));
                            player.updateInventory();
                        } else if (event.getCurrentItem() != null && event.getCursor() != null && CraftItemStack.asNMSCopy(event.getCurrentItem()) != null && CraftItemStack.asNMSCopy(event.getCurrentItem()).getTag() != null && (!CraftItemStack.asNMSCopy(event.getCurrentItem()).getTag().hasKey("acceptButton"))) {
                            event.setCancelled(true);
                            ItemStack currentItem = event.getCursor();
                            ItemStack slotItem = event.getCurrentItem();
                            event.setCursor(slotItem);
                            event.setCurrentItem(currentItem);
                            player.updateInventory();
                        }
                    }
                }
                if (event.isShiftClick() && slot < 26) {
                    if (!(slot == 0 || slot == 1 || slot == 2 || slot == 3 || slot == 9 || slot == 10 || slot == 11 || slot == 12 || slot == 18
                            || slot == 19 || slot == 20 || slot == 21) && !(slot > 27)) {
                        event.setCancelled(true);
                        if (tradeWindow.getItem(slot) != null && tradeWindow.getItem(slot).getType() != Material.AIR) {
                            tradeWindow.setItem(slot, tradeWindow.getItem(slot));
                            player.updateInventory();
                        }
                    } else if (CraftItemStack.asNMSCopy(event.getCurrentItem()) != null && CraftItemStack.asNMSCopy(event.getCurrentItem()).getTag() != null && !CraftItemStack.asNMSCopy(event.getCurrentItem()).getTag().hasKey("acceptButton")) {
                        event.setCancelled(true);
                        ItemStack slotItem = event.getCurrentItem();
                        if (player.getInventory().firstEmpty() != -1) {
                            tradeWindow.setItem(slot, new ItemStack(Material.AIR));
                            player.getInventory().setItem(player.getInventory().firstEmpty(), slotItem);
                            player.updateInventory();
                        }
                    }
                }
                if (event.isShiftClick() && slot >= 27 && !(event.isCancelled()) && !(event.getCurrentItem().getType() == Material.BOOK)) {
                    event.setCancelled(true);
                    ItemStack slotItem = event.getCurrentItem();
                    int localSlot = event.getSlot();
                    int x = -1;
                    while (x < 26) {
                        x++;
                        if (!(x == 0 || x == 1 || x == 2 || x == 3 || x == 9 || x == 10 || x == 11 || x == 12 || x == 18 || x == 19 || x == 20 || x == 21)) {
                            continue;
                        }
                        ItemStack itemStack = tradeWindow.getItem(x);
                        if (!(itemStack == null)) {
                            continue;
                        }
                        tradeWindow.setItem(x, slotItem);
                        if (tradeWindow.getItem(x) != null) {
                            tradeWindow.getItem(x).setAmount(slotItem.getAmount());
                        }
                        player.getInventory().setItem(localSlot, new ItemStack(Material.AIR));
                        player.updateInventory();
                        break;
                    }
                }
                List<ItemStack> player_Offer = new ArrayList<>();
                int x = -1;
                while (x < 26) {
                    x++;
                    if (!(x == 0 || x == 1 || x == 2 || x == 3 || x == 9 || x == 10 || x == 11 || x == 12 || x == 18 || x == 19 || x == 20 || x == 21)) {
                        continue;
                    }
                    ItemStack itemStack = tradeWindow.getItem(x);
                    if (itemStack == null || itemStack.getType() == Material.AIR || CraftItemStack.asNMSCopy(itemStack) != null && CraftItemStack.asNMSCopy(itemStack).getTag() != null && CraftItemStack.asNMSCopy(itemStack).getTag().hasKey("acceptButton")) {
                        continue;
                    }
                    player_Offer.add(itemStack);
                }
                List<ItemStack> new_Offer = TradeCalculator.calculateMerchantOffer(player_Offer);
                x = -1;
                while (x < 26) {
                    x++;
                    if ((x == 0 || x == 1 || x == 2 || x == 3 || x == 4 || x == 9 || x == 10 || x == 11 || x == 12 || x == 13 || x == 22 || x == 18 || x == 19
                            || x == 20 || x == 21)) {
                        continue;
                    }
                    tradeWindow.setItem(x, new ItemStack(Material.AIR));
                }
                x = -1;
                while (x < 26) {
                    x++;
                    if (new_Offer.size() > 0) {
                        if ((x == 0 || x == 1 || x == 2 || x == 3 || x == 4 || x == 9 || x == 10 || x == 11 || x == 12 || x == 13 || x == 22 || x == 18 || x == 19
                                || x == 20 || x == 21)) {
                            continue;
                        }
                        int index = new_Offer.size() - 1;
                        ItemStack itemStack = new_Offer.get(index);
                        tradeWindow.setItem(x, itemStack);
                        new_Offer.remove(index);
                    }
                }
                player.updateInventory();
                if (CraftItemStack.asNMSCopy(event.getCurrentItem()) != null && CraftItemStack.asNMSCopy(event.getCurrentItem()).getTag() != null && CraftItemStack.asNMSCopy(event.getCurrentItem()).getTag().hasKey("acceptButton")) {
                    event.setCancelled(true);
                    if (event.getCurrentItem().getDurability() == 8) {
                        int player_Inv_Available = 0;
                        int inv_Needed = 0;
                        event.setCurrentItem(new ItemBuilder().setItem(Material.INK_SACK, (short) 10, ChatColor.GREEN + "Trade accepted.", new String[]{
                                ""
                        }).setNBTString("acceptButton", "whynot").build());
                        player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_HURT, 1F, 2.F);

                        for (ItemStack itemStack : player.getInventory().getStorageContents()) {
                            if (itemStack == null || itemStack.getType() == Material.AIR) {
                                player_Inv_Available++;
                            }
                        }
                        int slot_Variable = -1;
                        while (slot_Variable < 26) {
                            slot_Variable++;
                            if (!(slot_Variable == 5 || slot_Variable == 6 || slot_Variable == 7 || slot_Variable == 8 || slot_Variable == 14 || slot_Variable == 15
                                    || slot_Variable == 16 || slot_Variable == 17 || slot_Variable == 23 || slot_Variable == 24 || slot_Variable == 25 || slot_Variable == 26)) {
                                continue;
                            }
                            ItemStack itemStack = tradeWindow.getItem(slot_Variable);
                            if (itemStack == null || itemStack.getType() == Material.AIR || (CraftItemStack.asNMSCopy(itemStack) != null && CraftItemStack.asNMSCopy(itemStack).getTag() != null && CraftItemStack.asNMSCopy(itemStack).getTag().hasKey("acceptButton")) || itemStack.getType() == Material.THIN_GLASS) {
                                continue;
                            }
                            inv_Needed++;
                        }
                        if (player_Inv_Available < inv_Needed) {
                            player.sendMessage(ChatColor.RED + "Inventory is full.");
                            player.sendMessage(ChatColor.GRAY + "You require " + ChatColor.BOLD + (inv_Needed - player_Inv_Available) + ChatColor.GRAY + " more free slots to complete this trade!");
                            Bukkit.getScheduler().scheduleSyncDelayedTask(DungeonRealms.getInstance(), () -> {
                                InventoryCloseEvent closeEvent = new InventoryCloseEvent(player.getOpenInventory());
                                Bukkit.getServer().getPluginManager().callEvent(closeEvent);
                            }, 2L);
                            return;
                        }
                        slot_Variable = -1;
                        while (slot_Variable < 26) {
                            slot_Variable++;
                            if (!(slot_Variable == 5 || slot_Variable == 6 || slot_Variable == 7 || slot_Variable == 8 || slot_Variable == 14 || slot_Variable == 15
                                    || slot_Variable == 16 || slot_Variable == 17 || slot_Variable == 23 || slot_Variable == 24 || slot_Variable == 25 || slot_Variable == 26)) {
                                continue;
                            }
                            ItemStack itemStack = tradeWindow.getItem(slot_Variable);
                            if (itemStack == null || itemStack.getType() == Material.AIR || (CraftItemStack.asNMSCopy(itemStack) != null && CraftItemStack.asNMSCopy(itemStack).getTag() != null && CraftItemStack.asNMSCopy(itemStack).getTag().hasKey("acceptButton")) || itemStack.getType() == Material.THIN_GLASS) {
                                continue;
                            }
                            if (itemStack.getType() == Material.EMERALD) {
                            	itemStack = new ItemGemNote(player.getName(), itemStack.getAmount()).generateItem();
                            }
                            player.getInventory().setItem(player.getInventory().firstEmpty(), itemStack);
                        }
                        player.sendMessage(ChatColor.GREEN + "Trade Accepted.");

                        for (ItemStack stack : tradeWindow.getContents()) {
                            if (stack != null && stack.getType() == Material.MAGMA_CREAM) {
                                break;
                            }
                        }

                        player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_HURT, 1F, 1F);
                        tradeWindow.clear();

                        Bukkit.getScheduler().scheduleSyncDelayedTask(DungeonRealms.getInstance(), () -> {
                            player.updateInventory();
                            player.closeInventory();
                        }, 2L);

                        return;
                    }
                    player.updateInventory();
                }
                break;
            case "Friend Management":
                event.setCancelled(true);
                if (slot >= 44) return;
                if (slot == 1) {
                    PlayerMenus.openFriendsMenu(player);
                }
                if (slot == 0) {
                    player.sendMessage(ChatColor.GREEN + "Please enter the name of the player you would like to add...");
                    Chat.listenForMessage(player, chat -> {
                        Player target = Bukkit.getPlayer(chat.getMessage());
                        if (target != null) {
                            FriendHandler.getInstance().sendRequest(player, target);
                            player.sendMessage(ChatColor.GREEN + "Friend request sent to " + ChatColor.BOLD + target.getName() + ChatColor.GREEN + ".");
                        } else {
                            player.sendMessage(ChatColor.RED + "Oops, I can't find that player!");
                        }
                    }, p -> p.sendMessage(ChatColor.RED + "Action cancelled."));
                    return;
                }
                FriendHandler.getInstance().addOrRemove(player, event.getClick(), event.getCurrentItem());
                break;
            case "Friends":
                event.setCancelled(true);
                if (slot >= 54) return;
                if (slot == 0) {
                    PlayerMenus.openFriendInventory(player);
                    return;
                }
                FriendHandler.getInstance().remove(player, event.getClick(), event.getCurrentItem());
                break;
            case "Mailbox":
                event.setCancelled(true);
                /*if (slot == 0) { // @todo: Enable this code when we allow sending items.
                    player.sendMessage(ChatColor.RED + "You cannot send mail yet, this feature is coming soon...");
                    return;
                } else if (slot == 8) {
                    return;
                } else {*/
                if (event.getCurrentItem() != null) {
                    ItemStack clickedItem = event.getCurrentItem();
                    MailHandler.getInstance().giveItemToPlayer(clickedItem, player);
                }
                //}
                break;
            case "Pet Selection":
                event.setCancelled(true);
                if (event.getCurrentItem().getType() == Material.LEASH) {
                	PetUtils.removePet(player);
                    return;
                }
                
                ItemStack petSelector = event.getCurrentItem();
                
                if (ItemPetSelector.isType(petSelector, ItemType.PET_SELECTOR)) {
                	ItemPetSelector selector = (ItemPetSelector) PersistentItem.constructItem(petSelector);
                	if (event.getClick() == ClickType.LEFT) {
                        PetUtils.removePet(player);
                        String petName = selector.getPetName();
                        String toStore = selector.getPet().name() + "@" + petName;
                        DatabaseAPI.getInstance().update(player.getUniqueId(), EnumOperators.$SET, EnumData.ACTIVE_PET, toStore, true);
                        PetUtils.spawnPet(player, selector.getPet(), petName);
                    } else if (event.getClick() == ClickType.MIDDLE) {
                    	ItemPet.renamePet(player);
                    }
                }
                break;
            case "Mount Selection":
                event.setCancelled(true);
                if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null)
                	return;
                if (event.getCurrentItem().getType() == Material.BARRIER) {
                    PlayerMenus.openPlayerProfileMenu(player);
                    return;
                } else if (event.getCurrentItem().getType() == Material.LEASH) {
                	MountUtils.removeMount(player);
                    return;
                } else {
                	ItemMount.attemptSummonMount(player);
                }
                break;
            case "Player Effect Selection":
                event.setCancelled(true);
                if (event.getCurrentItem().getType() == Material.BARRIER) {
                    PlayerMenus.openPlayerProfileMenu(player);
                    return;
                }
                if (event.getCurrentItem().getType() == Material.ARMOR_STAND) {
                    if (DonationEffects.getInstance().PLAYER_PARTICLE_EFFECTS.containsKey(player)) {
                        DonationEffects.getInstance().PLAYER_PARTICLE_EFFECTS.remove(player);
                        player.sendMessage(ChatColor.GREEN + "You have disabled your effect.");
                    } else {
                        player.sendMessage(ChatColor.RED + "You don't have a player effect currently activated.");
                    }
                    return;
                }
                if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR && event.getCurrentItem().getType() != Material.BARRIER && event.getCurrentItem().getType() != Material.ARMOR_STAND) {
                    net.minecraft.server.v1_9_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(event.getCurrentItem());
                    if (nmsStack.getTag() == null || nmsStack.getTag().getString("playerTrailType") == null) {
                        player.sendMessage(ChatColor.RED + "Uh oh... Something went wrong with your player effect! Please inform a developer! [PTS-NBT]");
                        player.closeInventory();
                        return;
                    }
                    DatabaseAPI.getInstance().update(player.getUniqueId(), EnumOperators.$SET, EnumData.ACTIVE_TRAIL, nmsStack.getTag().getString("playerTrailType"), true);
                    DonationEffects.getInstance().PLAYER_PARTICLE_EFFECTS.put(player, ParticleAPI.ParticleEffect.getByName(nmsStack.getTag().getString("playerTrailType")));
                    player.sendMessage(ChatColor.GREEN + "The " + ChatColor.BOLD + ParticleAPI.ParticleEffect.getByName(nmsStack.getTag().getString("playerTrailType")).getDisplayName().toLowerCase() + ChatColor.GREEN + " trail has been activated.");
                }
                break;
            case "Profile":
                event.setCancelled(true);
                switch (slot) {
                    case 0:
                        GameAPI.getGamePlayer(player).getStats().openMenu(player);
                        break;
                    case 1:
                        PlayerMenus.openFriendInventory(player);
                        break;
                    case 6:
                        if (event.getClick() == ClickType.RIGHT)
                        	CraftingMenu.addTrailItem(player);
                        else
                        	PlayerMenus.openPlayerParticleMenu(player);
                        break;
                    case 7:
                        if (event.getClick() == ClickType.RIGHT)
                        	CraftingMenu.addMountItem(player);
                        else
                        	PlayerMenus.openPlayerMountMenu(player);
                        break;
                    case 8:
                        if (event.getClick() == ClickType.RIGHT)
                        	CraftingMenu.addPetItem(player);
                        else
                        	PlayerMenus.openPlayerPetMenu(player);
                        break;
                    case 16: {
                        if (event.getClick() == ClickType.RIGHT) return;
                        List<String> playerMounts = (ArrayList<String>) DatabaseAPI.getInstance().getData(EnumData.MOUNTS, player.getUniqueId());
                        if (!playerMounts.contains("MULE")) {
                            player.sendMessage(ChatColor.RED + "Purchase a storage mule from the Animal Tamer.");
                            return;
                        }
                        MountUtils.removeMount(player);
                        PetUtils.removePet(player);
                        if (CombatLog.isInCombat(player)) {
                            player.sendMessage(ChatColor.RED + "You cannot summon a storage mule while in combat!");
                            return;
                        }
                        net.minecraft.server.v1_9_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(event.getCurrentItem());
                        if (nmsStack.getTag() == null || nmsStack.getTag().getString("mountType") == null) {
                            player.sendMessage(ChatColor.RED + "Uh oh... Something went wrong with your mount! Please inform a developer! [P-NBT]");
                            player.closeInventory();
                            return;
                        }
                        player.sendMessage(ChatColor.GREEN + "Please wait whilst your storage mule is summoned...");
                        Bukkit.getScheduler().scheduleSyncDelayedTask(DungeonRealms.getInstance(), () -> {
                            if (!MountUtils.hasInventory(player)) {
                                MountUtils.spawnMount(player, EnumMounts.MULE);
                            } else {
                                player.sendMessage(ChatColor.RED + "Your mount is currently summoned, you fail to summon your storage mule.");
                            }
                        }, 60L);
                        player.closeInventory();
                        break;
                    }
                    case 17:
                        PlayerMenus.openPlayerMountSkinMenu(player);
                        break;
                    case 18:
                        NPCMenus.openECashPurchaseMenu(player);
                        break;
                    case 24:
                        PlayerMenus.openPlayerAchievementsMenu(player);
                        break;
                    case 26:
                        PlayerMenus.openToggleMenu(player);
                        break;
                    default:
                        break;
                }
                break;
            case "Wizard":
                GamePlayer gp = GameAPI.getGamePlayer(player);
                if (gp.getLevel() >= 10) {
                    if (gp.getStats().resetAmounts > 0) {
                        player.sendMessage(ChatColor.GREEN + "One free stat reset available. Type 'yes' or 'y' to continue.");
                        Chat.listenForMessage(player, e -> {
                            if (e.getMessage().equalsIgnoreCase("Yes") || e.getMessage().equalsIgnoreCase("y")) {
                                gp.getStats().freeResets -= 1;
                                player.sendMessage(ChatColor.GREEN + "Your stats have been reset.");
                            }
                        }, p -> p.sendMessage(ChatColor.RED + "Stat reset - " + ChatColor.BOLD + "cancelled" + ChatColor.RED + "."));
                    } else {
                        player.sendMessage(ChatColor.RED + "You have already used your free stat reset for your character.");
                        player.sendMessage(ChatColor.YELLOW + "You may purchase more resets from the E-Cash vendor!");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You need to be level 10 in order to use your one free stat reset.");
                }
                break;
            case "Toggles":
                event.setCancelled(true);
                if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

                PlayerManager.PlayerToggles playerToggle = PlayerManager.PlayerToggles.getByCommandName(event.getCurrentItem().getItemMeta().getDisplayName().substring(3));
                if (playerToggle != null) {
                    playerToggle.setToggleState(player, !(boolean) DatabaseAPI.getInstance().getData(playerToggle.getDbField(), player.getUniqueId()));
                    PlayerMenus.openToggleMenu(player);
                } else {
                    PlayerMenus.openPlayerProfileMenu(player);
                }

                break;
            case "Mount Skin Selection":
                event.setCancelled(true);
                if (event.getCurrentItem().getType() == Material.BARRIER) {
                    PlayerMenus.openPlayerProfileMenu(player);
                    return;
                }
                if (event.getCurrentItem().getType() == Material.ARMOR_STAND) {
                	MountUtils.removeMount(player);
                	player.sendMessage(ChatColor.GREEN + "Skin removed.");
                    DatabaseAPI.getInstance().update(player.getUniqueId(), EnumOperators.$SET, EnumData.ACTIVE_MOUNT_SKIN, "", true);
                    return;
                }
                if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR && event.getCurrentItem().getType() != Material.BARRIER && event.getCurrentItem().getType() != Material.ARMOR_STAND) {
                    MountUtils.removeMount(player);
                    net.minecraft.server.v1_9_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(event.getCurrentItem());
                    if (nmsStack.getTag() == null || nmsStack.getTag().getString("skinType") == null) {
                        player.sendMessage(ChatColor.RED + "Uh oh... Something went wrong with your mount skin! Please inform a developer! [MSS-NBT]");
                        player.closeInventory();
                        return;
                    }
                    DatabaseAPI.getInstance().update(player.getUniqueId(), EnumOperators.$SET, EnumData.ACTIVE_MOUNT_SKIN, nmsStack.getTag().getString("skinType"), true);
                    player.sendMessage(ChatColor.GREEN + "Your mount skin has been changed. Please re-summon your mount.");
                }
                break;
            case "Achievements":
                event.setCancelled(true);
                if (slot > 9) return;
                switch (slot) {
                    case 0:
                        PlayerMenus.openPlayerProfileMenu(player);
                        break;
                    case 2:
                        PlayerMenus.openExplorationAchievementMenu(player);
                        break;
                    case 3:
                        PlayerMenus.openCombatAchievementMenu(player);
                        break;
                    case 4:
                        PlayerMenus.openCharacterAchievementMenu(player);
                        break;
                    case 5:
                        PlayerMenus.openCurrencyAchievementMenu(player);
                        break;
                    case 6:
                        PlayerMenus.openSocialAchievementMenu(player);
                        break;
                    case 7:
                        PlayerMenus.openRealmAchievementMenu(player);
                        break;
                    case 8:
                        PlayerMenus.openEventAchievementMenu(player);
                        break;
                    default:
                        break;
                }
                break;
            case "Exploration Achievements":
                event.setCancelled(true);
                if (slot == 0) {
                    PlayerMenus.openPlayerAchievementsMenu(player);
                    return;
                }
                break;
            case "Character Achievements":
                event.setCancelled(true);
                if (slot == 0) {
                    PlayerMenus.openPlayerAchievementsMenu(player);
                    return;
                }
                break;
            case "Social Achievements":
                event.setCancelled(true);
                if (slot == 0) {
                    PlayerMenus.openPlayerAchievementsMenu(player);
                    return;
                }
                break;
            case "Combat Achievements":
                event.setCancelled(true);
                if (slot == 0) {
                    PlayerMenus.openPlayerAchievementsMenu(player);
                    return;
                }
                break;
            case "Currency Achievements":
                event.setCancelled(true);
                if (slot == 0) {
                    PlayerMenus.openPlayerAchievementsMenu(player);
                    return;
                }
                break;
            case "Realm Achievements":
                event.setCancelled(true);
                if (slot == 0) {
                    PlayerMenus.openPlayerAchievementsMenu(player);
                    return;
                }
                break;


            // CUSTOMER SUPPORT @todo: Move to own class to clean up & take advantage of own methods for reusing vars.
            case "Support Tools":
                event.setCancelled(true);
                if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || !Rank.isSupport(player))
                    break;

                NBTTagCompound tag = CraftItemStack.asNMSCopy(event.getCurrentItem()).getTag();
                String playerName = tag.getString("name");
                UUID uuid = UUID.fromString(tag.getString("uuid"));

                // Only continue if the playerName & uuid aren't empty.
                if (playerName.isEmpty() || uuid.toString().isEmpty()) break;

                switch (slot) {
                    case 4:
                        break;
                    case 19:
                        if (!playerName.equalsIgnoreCase(player.getDisplayName())) {
                            SupportMenus.openRankMenu(player, playerName, uuid);
                        } else {
                            player.sendMessage(ChatColor.RED + "You " + ChatColor.BOLD + ChatColor.UNDERLINE.toString() + "CANNOT" + ChatColor.RED + " change the rank of your own account.");
                            player.closeInventory();
                        }
                        break;
                    case 22:
                        SupportMenus.openLevelMenu(player, playerName, uuid);
                        break;
                    case 25:
                        SupportMenus.openECashMenu(player, playerName, uuid);
                        break;
                    case 28:
                        SupportMenus.openBankMenu(player, playerName, uuid);
                        break;
                    case 31:
                        SupportMenus.openHearthstoneMenu(player, playerName, uuid);
                        break;
                    case 34:
                        SupportMenus.openCosmeticsMenu(player, playerName, uuid);
                        break;

                    default:
                        player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Uh oh!" + ChatColor.BLUE + " This feature is coming soon....");
                        break;
                }
                break;
            case "Support Tools (Rank)":
                event.setCancelled(true);
                if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || !Rank.isSupport(player))
                    return;

                tag = CraftItemStack.asNMSCopy(event.getCurrentItem()).getTag();
                playerName = tag.getString("name");
                uuid = UUID.fromString(tag.getString("uuid"));

                // Only continue if the playerName & uuid aren't empty.
                if (playerName.isEmpty() || uuid.toString().isEmpty()) return;
                // Only developers can apply certain ranks (GM / Builder / Support / YouTuber)
                if (!Rank.isDev(player) && (slot == 29 || slot == 30 || slot == 32 || slot == 33)) return;

                String newRank;
                boolean subscriptionPrompt = false;

                switch (slot) {
                    case 4:
                        SupportMenus.openMainMenu(player, playerName);
                        return;
                    case 20:
                        newRank = "DEFAULT";
                        break;
                    case 21:
                        newRank = "SUB";
                        //subscriptionPrompt = true;
                        break;
                    case 22:
                        newRank = "SUB+";
                        //subscriptionPrompt = true;
                        break;
                    case 23:
                        newRank = "SUB++";
                        break;
                    case 24:
                        newRank = "PMOD";
                        break;
                    case 29:
                        newRank = "BUILDER";
                        break;
                    case 30:
                        newRank = "YOUTUBE";
                        break;
                    case 32:
                        newRank = "SUPPORT";
                        break;
                    case 33:
                        newRank = "GM";
                        break;
                    default:
                        return;
                }

                if (subscriptionPrompt) {
                    SupportMenus.openRankSubscriptionMenu(player, playerName, uuid, newRank);
                    return;
                }

                // Always update the database with the new rank.
                DatabaseAPI.getInstance().update(uuid, EnumOperators.$SET, EnumData.RANK, newRank, true, doAfter -> {
                    if (Bukkit.getPlayer(playerName) != null) {
                        Rank.getInstance().setRank(uuid, newRank);
                    } else {
                        GameAPI.updatePlayerData(uuid);
                    }
                });

                player.sendMessage(ChatColor.GREEN + "Successfully set the rank of " + ChatColor.BOLD + ChatColor.UNDERLINE + playerName + ChatColor.GREEN + " to " + ChatColor.BOLD + ChatColor.UNDERLINE + newRank + ChatColor.GREEN + ".");
                SupportMenus.openMainMenu(player, playerName);
                break;
            case "Support Tools (Subscription)":
                event.setCancelled(true);
                if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || !Rank.isSupport(player))
                    return;

                tag = CraftItemStack.asNMSCopy(event.getCurrentItem()).getTag();
                playerName = tag.getString("name");
                uuid = UUID.fromString(tag.getString("uuid"));
                String subscriptionRank;

                // Only continue if the playerName & uuid aren't empty.
                if (playerName.isEmpty() || uuid.toString().isEmpty()) return;

                String subscriptionType;
                switch (slot) {
                    case 4:
                        SupportMenus.openMainMenu(player, playerName);
                        return;
                    case 21:
                        subscriptionRank = tag.getString("rank");
                        subscriptionType = "add";
                        break;
                    case 22:
                        subscriptionRank = tag.getString("rank");
                        subscriptionType = "set";
                        break;
                    case 23:
                        subscriptionRank = tag.getString("rank");
                        subscriptionType = "remove";
                        break;
                    default:
                        player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Uh oh!" + ChatColor.BLUE + " This feature is coming soon....");
                        return;
                }

                if (subscriptionRank != null && subscriptionType != null) {
                    player.sendMessage(ChatColor.YELLOW + "Please enter the number of days you would to " + subscriptionType + ":");

                    final String customSubscriptionRank = subscriptionRank;
                    final String customSubscriptionType = subscriptionType;
                    Chat.listenForMessage(player, customAmount -> {
                        if (!customAmount.getMessage().equalsIgnoreCase("cancel") && !customAmount.getMessage().equalsIgnoreCase("exit")) {
                            try {
                                Support.modifySubscription(player, playerName, uuid, Integer.parseInt(customAmount.getMessage()), customSubscriptionType, customSubscriptionRank);
                            } catch (NumberFormatException e) {
                                player.sendMessage(ChatColor.RED + customAmount.getMessage() + " is not a valid number.");
                            }
                        }
                    }, null);
                }

                break;
            case "Support Tools (Level)":
                event.setCancelled(true);
                if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || !Rank.isSupport(player))
                    return;

                tag = CraftItemStack.asNMSCopy(event.getCurrentItem()).getTag();
                playerName = tag.getString("name");
                uuid = UUID.fromString(tag.getString("uuid"));

                // Only continue if the playerName & uuid aren't empty.
                if (playerName.isEmpty() || uuid.toString().isEmpty()) return;

                String levelType = "add";
                String variableName = "experience";
                switch (slot) {
                    case 4:
                        SupportMenus.openMainMenu(player, playerName);
                        return;
                    case 21:
                        levelType = "add";
                        variableName = "experience";
                        break;
                    case 22:
                        levelType = "set";
                        variableName = "experience";
                        break;
                    case 23:
                        levelType = "remove";
                        variableName = "experience";
                        break;
                    case 30:
                        levelType = "add";
                        variableName = "level";
                        break;
                    case 31:
                        levelType = "set";
                        variableName = "level";
                        break;
                    case 32:
                        levelType = "remove";
                        variableName = "level";
                        break;
                    default:
                        player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Uh oh!" + ChatColor.BLUE + " This feature is coming soon....");
                        return;
                }

                player.sendMessage(ChatColor.YELLOW + "Please enter the amount you would to " + levelType + ":");
                final String customLevelType = levelType;
                final String finalVariableName = variableName;
                Chat.listenForMessage(player, customAmount -> {
                    if (!customAmount.getMessage().equalsIgnoreCase("cancel") && !customAmount.getMessage().equalsIgnoreCase("exit")) {
                        try {
                            if (finalVariableName.equals("level")) {
                                Support.modifyLevel(player, playerName, uuid, Integer.parseInt(customAmount.getMessage()), customLevelType);
                            } else {
                                Support.modifyExp(player, playerName, uuid, Integer.parseInt(customAmount.getMessage()), customLevelType);
                            }
                        } catch (NumberFormatException e) {
                            player.sendMessage(ChatColor.RED + customAmount.getMessage() + " is not a valid number.");
                        }
                    }
                }, null);

                break;
            case "Support Tools (E-Cash)":
                event.setCancelled(true);
                if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || !Rank.isSupport(player))
                    return;

                tag = CraftItemStack.asNMSCopy(event.getCurrentItem()).getTag();
                playerName = tag.getString("name");
                uuid = UUID.fromString(tag.getString("uuid"));

                // Only continue if the playerName & uuid aren't empty.
                if (playerName.isEmpty() || uuid.toString().isEmpty()) return;

                boolean customInput = false;
                String ecashType = "add";
                switch (slot) {
                    case 4:
                        SupportMenus.openMainMenu(player, playerName);
                        return;
                    case 21:
                        customInput = true;
                        ecashType = "add";
                        break;
                    case 22:
                        customInput = true;
                        ecashType = "set";
                        break;
                    case 23:
                        customInput = true;
                        ecashType = "remove";
                        break;
                    case 29:
                        Support.modifyEcash(player, playerName, uuid, 500, "add");
                        break;
                    case 30:
                        Support.modifyEcash(player, playerName, uuid, 2500, "add");
                        break;
                    case 32:
                        Support.modifyEcash(player, playerName, uuid, 5000, "add");
                        break;
                    case 33:
                        Support.modifyEcash(player, playerName, uuid, 9999, "add");
                        break;
                    default:
                        player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Uh oh!" + ChatColor.BLUE + " This feature is coming soon....");
                        return;
                }

                if (customInput) {
                    player.sendMessage(ChatColor.YELLOW + "Please enter the amount you would to " + ecashType + ":");
                    final String customEcashType = ecashType;
                    Chat.listenForMessage(player, customAmount -> {
                        if (!customAmount.getMessage().equalsIgnoreCase("cancel") && !customAmount.getMessage().equalsIgnoreCase("exit")) {
                            try {
                                Support.modifyEcash(player, playerName, uuid, Integer.parseInt(customAmount.getMessage()), customEcashType);
                            } catch (NumberFormatException e) {
                                player.sendMessage(ChatColor.RED + customAmount.getMessage() + " is not a valid number.");
                            }
                        }
                    }, null);
                }
                break;
            case "Support Tools (Bank)":
                event.setCancelled(true);
                if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || !Rank.isSupport(player))
                    return;

                tag = CraftItemStack.asNMSCopy(event.getCurrentItem()).getTag();
                playerName = tag.getString("name");
                uuid = UUID.fromString(tag.getString("uuid"));

                // Only continue if the playerName & uuid aren't empty.
                if (playerName.isEmpty() || uuid.toString().isEmpty()) return;

                String bankType = "add";
                switch (slot) {
                    case 4:
                        SupportMenus.openMainMenu(player, playerName);
                        return;
                    case 21:
                        bankType = "add";
                        break;
                    case 22:
                        bankType = "set";
                        break;
                    case 23:
                        bankType = "remove";
                        break;
                    default:
                        player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Uh oh!" + ChatColor.BLUE + " This feature is coming soon....");
                        return;
                }

                player.sendMessage(ChatColor.YELLOW + "Please enter the amount you would to " + bankType + ":");
                final String customBankType = bankType;
                Chat.listenForMessage(player, customAmount -> {
                    if (!customAmount.getMessage().equalsIgnoreCase("cancel") && !customAmount.getMessage().equalsIgnoreCase("exit")) {
                        try {
                            Support.modifyGems(player, playerName, uuid, Integer.parseInt(customAmount.getMessage()), customBankType);
                        } catch (NumberFormatException e) {
                            player.sendMessage(ChatColor.RED + customAmount.getMessage() + " is not a valid number.");
                        }
                    }
                }, null);

                break;
            case "Support Tools (Hearthstone)":
                event.setCancelled(true);
                if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || !Rank.isSupport(player))
                    return;

                tag = CraftItemStack.asNMSCopy(event.getCurrentItem()).getTag();
                playerName = tag.getString("name");
                uuid = UUID.fromString(tag.getString("uuid"));

                // Only continue if the playerName & uuid aren't empty.
                if (playerName.isEmpty() || uuid.toString().isEmpty()) return;

                String hearthstoneLocation = null;
                switch (slot) {
                    case 4:
                        SupportMenus.openMainMenu(player, playerName);
                        return;
                    case 18:
                        hearthstoneLocation = "Cyrennica";
                        break;
                    case 19:
                        hearthstoneLocation = "Harrison_Field";
                        break;
                    case 20:
                        hearthstoneLocation = "Dark_Oak";
                        break;
                    case 21:
                        hearthstoneLocation = "Trollsbane";
                        break;
                    case 22:
                        hearthstoneLocation = "Tripoli";
                        break;
                    case 23:
                        hearthstoneLocation = "Gloomy_Hollows";
                        break;
                    case 24:
                        hearthstoneLocation = "Crestguard";
                        break;
                    case 25:
                        hearthstoneLocation = "Deadpeaks";
                        break;
                    default:
                        player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Uh oh!" + ChatColor.BLUE + " This feature is coming soon....");
                        return;
                }

                if (hearthstoneLocation != null) {
                    String finalLocation = hearthstoneLocation;
                    DatabaseAPI.getInstance().update(player.getUniqueId(), EnumOperators.$SET, EnumData.HEARTHSTONE, hearthstoneLocation.toUpperCase(), true, doAfter -> {
                        player.sendMessage(ChatColor.GREEN + "Successfully set the hearthstone of " + ChatColor.BOLD + ChatColor.UNDERLINE + playerName + ChatColor.GREEN + " to " + ChatColor.BOLD + ChatColor.UNDERLINE + Utils.ucfirst(finalLocation).replace("_", " ") + ChatColor.GREEN + ".");
                        GameAPI.updatePlayerData(uuid);
                    });
                    SupportMenus.openMainMenu(player, playerName);
                }
                break;
            case "Support Tools (Cosmetics)":
                event.setCancelled(true);
                if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || !Rank.isSupport(player))
                    return;

                tag = CraftItemStack.asNMSCopy(event.getCurrentItem()).getTag();
                playerName = tag.getString("name");
                uuid = UUID.fromString(tag.getString("uuid"));

                // Only continue if the playerName & uuid aren't empty.
                if (playerName.isEmpty() || uuid.toString().isEmpty()) return;

                switch (slot) {
                    case 4:
                        SupportMenus.openMainMenu(player, playerName);
                        return;
                    case 19:
                        SupportMenus.openTrailsMenu(player, playerName, uuid);
                        break;
                    case 22:
                        SupportMenus.openPetsMenu(player, playerName, uuid);
                        break;
                    case 25:
                        SupportMenus.openMountsMenu(player, playerName, uuid);
                        break;
                    default:
                        player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Uh oh!" + ChatColor.BLUE + " This feature is coming soon....");
                        return;
                }
                break;
            case "Support Tools (Trails)":
                event.setCancelled(true);
                if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || !Rank.isSupport(player))
                    return;

                tag = CraftItemStack.asNMSCopy(event.getCurrentItem()).getTag();
                playerName = tag.getString("name");
                uuid = UUID.fromString(tag.getString("uuid"));

                // Only continue if the playerName & uuid aren't empty.
                if (playerName.isEmpty() || uuid.toString().isEmpty()) return;

                if (slot == 4) {
                    SupportMenus.openCosmeticsMenu(player, playerName, uuid);
                    return;
                } else if (!tag.hasKey("trail")) {
                    player.sendMessage(ChatColor.RED + "Well this is embarrassing... something went horribly wrong. [1]");
                    return;
                }

                String trailName = tag.getString("trail").toUpperCase();
                if (!GameAPI.isStringTrail(trailName)) {
                    player.sendMessage(ChatColor.RED + "Well this is embarrassing... something went horribly wrong. [2]");
                    return;
                }
                ParticleAPI.ParticleEffect supportEffect = ParticleAPI.ParticleEffect.getByName(trailName);

                List<String> playerTrails = (ArrayList<String>) DatabaseAPI.getInstance().getData(EnumData.PARTICLES, uuid);
                boolean supportTrailLocked = false;
                if (!playerTrails.isEmpty()) {
                    if (playerTrails.contains(trailName)) {
                        supportTrailLocked = true;
                        DatabaseAPI.getInstance().update(uuid, EnumOperators.$PULL, EnumData.PARTICLES, trailName, true, doAfter -> {
                            player.sendMessage(ChatColor.GREEN + "You have " + ChatColor.BOLD + ChatColor.UNDERLINE + "LOCKED" + ChatColor.GREEN + " the " + ChatColor.BOLD + ChatColor.UNDERLINE + supportEffect.getDisplayName() + ChatColor.GREEN + " trail for " + ChatColor.BOLD + ChatColor.UNDERLINE + playerName + ChatColor.GREEN + ".");
                            GameAPI.updatePlayerData(uuid);
                        });
                    }
                }

                if (!supportTrailLocked)
                    DatabaseAPI.getInstance().update(uuid, EnumOperators.$PUSH, EnumData.PARTICLES, trailName,
                            true, doAfter -> {
                                player.sendMessage(ChatColor.GREEN + "You have " + ChatColor.BOLD + ChatColor.UNDERLINE + "UNLOCKED" + ChatColor.GREEN + " the " + ChatColor.BOLD + ChatColor.UNDERLINE + supportEffect.getDisplayName() + ChatColor.GREEN + " trail for " + ChatColor.BOLD + ChatColor.UNDERLINE + playerName + ChatColor.GREEN + ".");
                                GameAPI.updatePlayerData(uuid);
                            });

                SupportMenus.openCosmeticsMenu(player, playerName, uuid);
                break;
            case "Support Tools (Mounts)":
                event.setCancelled(true);
                if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || !Rank.isSupport(player))
                    return;

                tag = CraftItemStack.asNMSCopy(event.getCurrentItem()).getTag();
                playerName = tag.getString("name");
                uuid = UUID.fromString(tag.getString("uuid"));

                // Only continue if the playerName & uuid aren't empty.
                if (playerName.isEmpty() || uuid.toString().isEmpty()) return;

                switch (slot) {
                    case 4:
                        SupportMenus.openCosmeticsMenu(player, playerName, uuid);
                        return;
                    case 0: // @todo: change me
                        break;
                    default:
                        player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Uh oh!" + ChatColor.BLUE + " This feature is coming soon....");
                        return;
                }
                break;
            case "Support Tools (Pets)":
                event.setCancelled(true);
                if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || !Rank.isSupport(player))
                    return;

                tag = CraftItemStack.asNMSCopy(event.getCurrentItem()).getTag();
                playerName = tag.getString("name");
                uuid = UUID.fromString(tag.getString("uuid"));

                // Only continue if the playerName & uuid aren't empty.
                if (playerName.isEmpty() || uuid.toString().isEmpty()) return;

                if (slot == 4) {
                    SupportMenus.openCosmeticsMenu(player, playerName, uuid);
                    return;
                } else if (!tag.hasKey("pet")) {
                    player.sendMessage(ChatColor.RED + "Well this is embarrassing... something went horribly wrong. [1]");
                    return;
                }

                String petName = tag.getString("pet").toUpperCase();
                if (!GameAPI.isStringPet(petName)) {
                    player.sendMessage(ChatColor.RED + "Well this is embarrassing... something went horribly wrong. [2]");
                    return;
                }
                EnumPets supportPets = EnumPets.getByName(petName);

                List<String> playerSupportPets = (ArrayList<String>) DatabaseAPI.getInstance().getData(EnumData.PETS, uuid);
                boolean supportPetLocked = false;
                if (!playerSupportPets.isEmpty()) {
                    if (playerSupportPets.contains(petName)) {
                        supportPetLocked = true;
                        DatabaseAPI.getInstance().update(uuid, EnumOperators.$PULL, EnumData.PETS, petName, true, doAfter -> {
                            player.sendMessage(ChatColor.GREEN + "You have " + ChatColor.BOLD + ChatColor.UNDERLINE + "LOCKED" + ChatColor.GREEN + " the " + ChatColor.BOLD + ChatColor.UNDERLINE + supportPets.getDisplayName() + ChatColor.GREEN + " pet for " + ChatColor.BOLD + ChatColor.UNDERLINE + playerName + ChatColor.GREEN + ".");
                            GameAPI.updatePlayerData(uuid);
                        });
                    }
                }

                if (!supportPetLocked)
                    DatabaseAPI.getInstance().update(uuid, EnumOperators.$PUSH, EnumData.PETS, petName, true, doAfter -> {
                        player.sendMessage(ChatColor.GREEN + "You have " + ChatColor.BOLD + ChatColor.UNDERLINE + "UNLOCKED" + ChatColor.GREEN + " the " + ChatColor.BOLD + ChatColor.UNDERLINE + supportPets.getDisplayName() + ChatColor.GREEN + " pet for " + ChatColor.BOLD + ChatColor.UNDERLINE + playerName + ChatColor.GREEN + ".");
                        GameAPI.updatePlayerData(uuid);
                    });

                SupportMenus.openCosmeticsMenu(player, playerName, uuid);
                break;
            case "Game Master Toggles":
                event.setCancelled(true);
                if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

                switch (slot) {
                    case 0: // Vanish
                        if (GameAPI._hiddenPlayers.contains(player)) {
                            GameAPI._hiddenPlayers.remove(player);
                            for (Player player1 : Bukkit.getOnlinePlayers()) {
                                if (player1.getUniqueId().toString().equals(player.getUniqueId().toString())) {
                                    continue;
                                }
                                player1.showPlayer(player);
                            }
                            player.removePotionEffect(PotionEffectType.INVISIBILITY);
                            player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "You are now visible.");
                            GameAPI.sendNetworkMessage("vanish", player.getUniqueId().toString(), "false");
                            player.setCustomNameVisible(true);
                            player.setGameMode(GameMode.CREATIVE);
                            DatabaseAPI.getInstance().update(player.getUniqueId(), EnumOperators.$SET, EnumData.TOGGLE_VANISH, false, true);
                        } else {
                            // Remove Trail
                            if (DonationEffects.getInstance().PLAYER_PARTICLE_EFFECTS.containsKey(player)) {
                                DonationEffects.getInstance().PLAYER_PARTICLE_EFFECTS.remove(player);
                                player.sendMessage(ChatColor.GREEN + "Your have disabled your trail.");
                            }
                            
                            PetUtils.removePet(player);

                            // Make the user invisible / hidden.
                            GameAPI._hiddenPlayers.add(player);
                            player.setCustomNameVisible(false);
                            Bukkit.getOnlinePlayers().forEach(p -> p.hidePlayer(player));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false));
                            player.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "You are now hidden.");
                            GameAPI.sendNetworkMessage("vanish", player.getUniqueId().toString(), "true");
                            player.setGameMode(GameMode.SPECTATOR);
                            DatabaseAPI.getInstance().update(player.getUniqueId(), EnumOperators.$SET, EnumData.TOGGLE_VANISH, true, true);
                        }
                        break;

                    case 1: // Allow Fight
                        gp = GameAPI.getGamePlayer(player);
                        if (gp == null) break;
                        // invert invulnerable flag
                        gp.setInvulnerable(!gp.isInvulnerable());
                        // targettable should always be the opposite of invulnerable for allowfight
                        gp.setTargettable(!gp.isInvulnerable());

                        if (!gp.isTargettable())
                            EntityAPI.untargetEntity(player, 20);

                        Utils.sendCenteredMessage(player, ChatColor.AQUA.toString() + ChatColor.BOLD + "GM " +
                                "INVINCIBILITY - " + (gp.isInvulnerable() ? ChatColor.GREEN.toString() + ChatColor
                                .BOLD + "ENABLED" : ChatColor.RED.toString() + ChatColor.BOLD + "DISABLED"));
                        break;

                    case 2: // Toggle Stream Mode
                        gp = GameAPI.getGamePlayer(player);
                        if (gp == null) break;

                        // Change the status.
                        gp.setStreamMode(!gp.isStreamMode());

                        // Update Permission
                        player.performCommand("ncp notify " + (gp.isStreamMode() ? "off" : "on"));

                        // Clear Immediate Chat
                        for (int i = 0; i < 50; i++) {
                            player.sendMessage("");
                        }

                        // Prompt user about the change.
                        if (gp.isStreamMode()) {
                            player.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "Stream Mode - ENABLED");
                        } else {
                            player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Stream Mode - DISABLED");
                        }

                        break;

                    default:
                        break;
                }

                PlayerMenus.openGameMasterTogglesMenu(player);
                break;
            case "Head Game Master Toggles":
                event.setCancelled(true);
                if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

                switch (slot) {
                    case 0: // Game Master Extended Permissions
                        DungeonRealms.getInstance().isGMExtendedPermissions = !DungeonRealms.getInstance().isGMExtendedPermissions;
                        if (DungeonRealms.getInstance().isGMExtendedPermissions) {
                            player.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "Game Master Extended Permissions - ENABLED");
                        } else {
                            player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Game Master Extended Permissions - DISABLED");
                        }
                        break;
                    default:
                        break;
                }

                PlayerMenus.openHeadGameMasterTogglesMenu(player);
                break;
            /*case "E-Cash Effects":
                event.setCancelled(true);
                if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
                if (slot == 0) {
                    NPCMenus.openECashPurchaseMenu(player);
                    return;
                }
                nmsStack = CraftItemStack.asNMSCopy(event.getCurrentItem());
                if (nmsStack == null) return;
                if (nmsStack.getTag() == null) return;
                if (!nmsStack.getTag().hasKey("effectType")) return;
                if (!nmsStack.getTag().hasKey("eCash")) return;
                String effectType = nmsStack.getTag().getString("effectType");
                List<String> playerEffects = (ArrayList<String>) DatabaseAPI.getInstance().getData(EnumData.PARTICLES, player.getUniqueId());
                ParticleAPI.ParticleEffect effect = ParticleAPI.ParticleEffect.getByName(effectType);
                if (effect == null) {
                    return;
                }
                if (!playerEffects.isEmpty()) {
                    if (playerEffects.contains(effectType)) {
                        player.sendMessage(ChatColor.RED + "You already own the " + ChatColor.BOLD + ChatColor.UNDERLINE + effect.getDisplayName() + ChatColor.RED + " effect.");
                        return;
                    }
                }
                eCashCost = nmsStack.getTag().getInt("eCash");
                if (DonationEffects.getInstance().removeECashFromPlayer(player, eCashCost)) {
                    DatabaseAPI.getInstance().update(player.getUniqueId(), EnumOperators.$PUSH, EnumData.PARTICLES, effectType, true);
                    DatabaseAPI.getInstance().update(player.getUniqueId(), EnumOperators.$SET, EnumData.ACTIVE_TRAIL, effectType, true);
                    player.sendMessage(ChatColor.GREEN + "You have purchased the " + effect.getDisplayName() + " effect.");
                    if (!PlayerManager.hasItem(event.getWhoClicked().getInventory(), "trail")) {
                        player.getInventory().addItem(new ItemParticleTrail().generateItem());
                    }
                    player.closeInventory();
                } else {
                    player.sendMessage(ChatColor.RED + "You cannot afford this effect, you require " + ChatColor.BOLD + ChatColor.UNDERLINE + eCashCost + ChatColor.RED + " E-Cash");
                }
                break;
            case "E-Cash Skins":
                event.setCancelled(true);
                if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
                if (slot == 0) {
                    NPCMenus.openECashPurchaseMenu(player);
                    return;
                }
                nmsStack = CraftItemStack.asNMSCopy(event.getCurrentItem());
                if (nmsStack == null) return;
                if (nmsStack.getTag() == null) return;
                if (!nmsStack.getTag().hasKey("skinType")) return;
                if (!nmsStack.getTag().hasKey("eCash")) return;
                if (DatabaseAPI.getInstance().getData(EnumData.MOUNTS, player.getUniqueId()).equals("")) {
                    player.sendMessage(ChatColor.RED + "You cannot purchase a mount skin as you do not own a mount!");
                    return;
                }
                String skinType = nmsStack.getTag().getString("skinType");
                List<String> playerSkins = (ArrayList<String>) DatabaseAPI.getInstance().getData(EnumData.MOUNT_SKINS, player.getUniqueId());
                EnumMountSkins skin = EnumMountSkins.getByName(skinType);
                if (skin == null) {
                    return;
                }
                if (!playerSkins.isEmpty()) {
                    if (playerSkins.contains(skinType)) {
                        player.sendMessage(ChatColor.RED + "You already own the " + ChatColor.BOLD + ChatColor.UNDERLINE + skin.getDisplayName() + ChatColor.RED + " mount skin.");
                        return;
                    }
                }
                eCashCost = nmsStack.getTag().getInt("eCash");
                if (DonationEffects.getInstance().removeECashFromPlayer(player, eCashCost)) {
                    DatabaseAPI.getInstance().update(player.getUniqueId(), EnumOperators.$PUSH, EnumData.MOUNT_SKINS, skinType, true);
                    DatabaseAPI.getInstance().update(player.getUniqueId(), EnumOperators.$SET, EnumData.ACTIVE_MOUNT_SKIN, skinType, true);
                    player.sendMessage(ChatColor.GREEN + "You have purchased the " + skin.getDisplayName() + " mount skin.");
                    player.closeInventory();
                } else {
                    player.sendMessage(ChatColor.RED + "You cannot afford this mount skin, you require " + ChatColor.BOLD + ChatColor.UNDERLINE + eCashCost + ChatColor.RED + " E-Cash");
                }
                break;*/
        }
    }
}
