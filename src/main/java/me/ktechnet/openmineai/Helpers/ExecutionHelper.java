package me.ktechnet.openmineai.Helpers;

import me.ktechnet.openmineai.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import scala.collection.parallel.ParIterableLike;

import java.math.BigDecimal;

public class ExecutionHelper {
    public String GetCardinal(int xOffset, int zOffset) {
        String xCardinal = "";
        String zCardinal = "";
        switch (xOffset) {
            case -1:
                xCardinal = "W";
                break;
            case 1:
                xCardinal = "E";
                break;
        }
        switch (zOffset) {
            case -1:
                zCardinal = "N";
                break;
            case 1:
                zCardinal = "S";
                break;
        }
        return zCardinal + xCardinal;
    }
    public Integer GetRotation(String cardinal) {
        switch (cardinal) {
            case "N":
                return 180;
            case "NE":
                return -135;
            case "E":
                return -90;
            case "SE":
                return -45;
            case "S":
                return 0;
            case "SW":
                return 45;
            case "W":
                return 90;
            case "NW":
                return 135;
        }
        return 0;
    }
    public void Centre(String cardinal) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        switch (cardinal) {
            case "N":
                boolean leftPositive = player.posX < 0;
                Move(leftPositive, "X");
                break;
            case "S":
                boolean leftPositive2 = player.posX > 0;
                Move(leftPositive2, "X");
                break;
            case "E":
                boolean leftPositive3 = player.posZ < 0;
                Move(leftPositive3, "Z");
                break;
            case "W":
                boolean leftPositive4 = player.posZ > 0;
                Move(leftPositive4, "Z");
                break;
            //case "SE":
            //    Move(true, "X");
            //    Move(false, "Z");
            //    break;
            //case "SW":
            //    Move(true, "X");
            //    Move(true, "Z");
            //    break;
            //case "NW":
            //    Move(false, "X");
            //    Move(true, "Z");
            //    break;
            //case "NE":
            //    Move(false, "X");
            //    Move(false, "Z");
            //    break;
        }
    }
    private void Move(boolean leftPositive, String toWatch) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (toWatch.equals("X")) {
            boolean flag = false;
            if (leftPositive) {
                if (GetDecimal(player.posX) < 0.36) {
                    PlayerControl.StrafeLeft = true;
                } else if (GetDecimal(player.posX) > 0.64) {
                    PlayerControl.StrafeRight = true;
                }
            } else {
                if (GetDecimal(player.posX) < 0.36) {
                    PlayerControl.StrafeRight = true;
                } else if (GetDecimal(player.posX) > 0.64) {
                    PlayerControl.StrafeLeft = true;
                }
            }
            while (!flag) {
                Double decimal = GetDecimal(player.posX);
                if (decimal > 0.35 && decimal < 0.65) flag = true;
            }
            PlayerControl.StrafeLeft = false;
            PlayerControl.StrafeRight = false;
        } else if (toWatch.equals("Z")) {
            boolean flag = false;
            if (leftPositive) {
                if (GetDecimal(player.posZ) < 0.36) {
                    PlayerControl.StrafeLeft = true;
                } else if (GetDecimal(player.posZ) > 0.64) {
                    PlayerControl.StrafeRight = true;
                }
            } else {
                if (GetDecimal(player.posZ) < 0.36) {
                    PlayerControl.StrafeRight = true;
                } else if (GetDecimal(player.posZ) > 0.64) {
                    PlayerControl.StrafeLeft = true;
                }
            }
            while (!flag) {
                Double decimal = GetDecimal(player.posZ);
                if (decimal > 0.35 && decimal < 0.65) flag = true;
            }
            PlayerControl.StrafeLeft = false;
            PlayerControl.StrafeRight = false;
        }
    }
    private double GetDecimal(double in) {
        BigDecimal bigDecimal = new BigDecimal(in);
        int intValue = bigDecimal.intValue();
        return Math.abs(bigDecimal.subtract(new BigDecimal(intValue)).doubleValue());
    }
}
