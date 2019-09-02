package me.ktechnet.openmineai.Helpers;

import me.ktechnet.openmineai.Main;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

import java.util.ArrayList;

public class ToolHelper {
    EntityPlayerSP player = Minecraft.getMinecraft().player;

    public void SelectTool(Block toBreak) {
        int bestSlot = 28;
        Item bestItem = Items.AIR;
        for (int i = 0; i < 10; i++) {
            Item item = player.inventory.getStackInSlot(i).getItem();
            if (spadeRequired.contains(toBreak)) {
                if (spades.contains(item)) {
                    if (spades.indexOf(bestItem) < spades.indexOf(item)) {
                        bestSlot = i;
                        bestItem = item;
                    }
                }
            } else if (axeRequired.contains(toBreak)) {
                if (axe.contains(item)) {
                    if (axe.indexOf(bestItem) < axe.indexOf(item)) {
                        bestSlot = i;
                        bestItem = item;
                    }
                }
            } else {
                if (pickaxes.contains(item)) {
                    if (pickaxes.indexOf(bestItem) < pickaxes.indexOf(item)) {
                        bestSlot = i;
                        bestItem = item;
                    }
                }
            }
        }
        Main.logger.info("Set current item to " + bestSlot);
        player.inventory.currentItem = bestSlot;
    }

    public void SelectDisposable() {
        for (int i = 0; i < 10; i++) {
            Item item = player.inventory.getStackInSlot(i).getItem();
            if (item instanceof ItemBlock) {
                if (disposable.contains(((ItemBlock) item).getBlock())) {
                    player.inventory.currentItem = i;
                    break;
                }
            }
        }
    }

    public void SelectWaterbucket() {
        for (int i = 0; i < 10; i++) {
            Item item = player.inventory.getStackInSlot(i).getItem();
            if (item == Items.WATER_BUCKET) {
                player.inventory.currentItem = i;
                break;
            }
        }
    }

    private ArrayList<Block> disposable = new ArrayList<Block>() {
        {
            add(Blocks.DIRT);
            add(Blocks.SANDSTONE);
            add(Blocks.COBBLESTONE);
            add(Blocks.NETHERRACK);
            add(Blocks.STONE);
            add(Blocks.END_STONE);
        }
    };
    private ArrayList<Block> spadeRequired = new ArrayList<Block>() {
        {
            add(Blocks.DIRT);
            add(Blocks.GRASS);
            add(Blocks.SAND);
            add(Blocks.GRAVEL);
        }
    };
    private ArrayList<Block> axeRequired = new ArrayList<Block>() {
        {
            add(Blocks.WOODEN_SLAB);
            add(Blocks.DOUBLE_WOODEN_SLAB);
            add(Blocks.PLANKS);
            add(Blocks.LOG);
            add(Blocks.LOG2);
        }
    };
    private ArrayList<Item> pickaxes = new ArrayList<Item>() {
        {
            add(Items.WOODEN_PICKAXE);
            add(Items.STONE_PICKAXE);
            add(Items.IRON_PICKAXE);
            add(Items.GOLDEN_PICKAXE);
            add(Items.DIAMOND_PICKAXE);
        }
    };
    private ArrayList<Item> spades = new ArrayList<Item>() {
        {
            add(Items.WOODEN_SHOVEL);
            add(Items.STONE_SHOVEL);
            add(Items.IRON_SHOVEL);
            add(Items.GOLDEN_SHOVEL);
            add(Items.DIAMOND_SHOVEL);
        }
    };
    private ArrayList<Item> axe = new ArrayList<Item>() {
        {
            add(Items.WOODEN_AXE);
            add(Items.STONE_AXE);
            add(Items.IRON_AXE);
            add(Items.GOLDEN_AXE);
            add(Items.DIAMOND_AXE);
        }
    };
}
