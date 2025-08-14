package net.superkat.ziptoit.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import net.superkat.ziptoit.duck.ZipcasterPlayer;
import net.superkat.ziptoit.zipcast.ZipcastManager;
import net.superkat.ziptoit.zipcast.movement.ZipcastTarget;

public class StickyHandItem extends Item {

    public static final int TICKS_UNTIL_ZIPCAST_ACTIVE_SOUND = 1200; // 1 minute

    public StickyHandItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
//        if(user instanceof ZipcasterPlayer zipcasterPlayer) {
//            if(zipcasterPlayer.shouldPlayZipcastActiveSound()) {
//                world.playSound(null, user.getX(), user.getY(), user.getZ(),
//                        SoundEvents.ITEM_TRIDENT_THUNDER.value(), SoundCategory.PLAYERS, 1f, 1.5f
//                );
//                world.playSound(null, user.getX(), user.getY(), user.getZ(),
//                        SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 1f, 1f
//                );
//                world.playSound(null, user.getX(), user.getY(), user.getZ(),
//                        SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.PLAYERS, 0.5f, 2f
//                );
//                world.playSound(null, user.getX(), user.getY(), user.getZ(),
//                        SoundEvents.ITEM_TRIDENT_RETURN, SoundCategory.PLAYERS, 2f, 1f
//                );
//                zipcasterPlayer.setTicksSinceZipcastActivate(0);
//            }
//        }
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        zipcastPlayer(user, stack);
        return super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    public void zipcastPlayer(LivingEntity player, ItemStack stickyHandStack) {
        if(!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;
        if(!player.getWorld().isClient) return;

        if(zipcasterPlayer.ticksSinceZipcastActivate() <= 10) return;
        if(zipcasterPlayer.isZipcasting() && !zipcasterPlayer.allowZipcastDuringZipcast()) return;

        // I'm assuming because of a natural desync of data because nothing is perfect,
        // raycasting from the server gives opportunity to not land a hit on a block,
        // leaving the player unable to zipcast.
        // This is most noticeable trying to zipcast to the top of a block which is beneath air(empty blocks above)
        BlockHitResult raycast = ZipcastManager.raycastStickyHand(player, stickyHandStack, 0);
        if(raycast == null) return;

        ZipcastTarget zipcastTarget = ZipcastTarget.ofRaycast(player, raycast);
        ZipcastManager.startZipcast(player, zipcastTarget, true);
    }

    public static Item getStickyHand(DyeColor color) {
        return switch (color) {
            case RED -> ZipToItItems.RED_STICKY_HAND;
            case ORANGE -> ZipToItItems.ORANGE_STICKY_HAND;
            case YELLOW -> ZipToItItems.YELLOW_STICKY_HAND;
            case LIME -> ZipToItItems.LIME_STICKY_HAND;
            case GREEN -> ZipToItItems.GREEN_STICKY_HAND;
            case BLUE -> ZipToItItems.BLUE_STICKY_HAND;
            case CYAN -> ZipToItItems.CYAN_STICKY_HAND;
            case LIGHT_BLUE -> ZipToItItems.LIGHT_BLUE_STICKY_HAND;
            case PINK -> ZipToItItems.PINK_STICKY_HAND;
            case MAGENTA -> ZipToItItems.MAGENTA_STICKY_HAND;
            case PURPLE -> ZipToItItems.PURPLE_STICKY_HAND;
            case WHITE -> ZipToItItems.WHITE_STICKY_HAND;
            case LIGHT_GRAY -> ZipToItItems.LIGHT_GRAY_STICKY_HAND;
            case GRAY -> ZipToItItems.GRAY_STICKY_HAND;
            case BROWN -> ZipToItItems.BROWN_STICKY_HAND;
            case BLACK -> ZipToItItems.BLACK_STICKY_HAND;
        };
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 1200;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPYGLASS;
    }
}
