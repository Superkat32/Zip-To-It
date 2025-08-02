package net.superkat.ziptoit.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.superkat.ziptoit.ZipToIt;
import net.superkat.ziptoit.duck.ZipcasterPlayer;
import net.superkat.ziptoit.zipcast.ZipcastManager;

public class StickyHandItem extends Item {

    public StickyHandItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ZipToIt.LOGGER.info("Using!");
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        ZipToIt.LOGGER.info("And stop!");
        zipcastPlayer(user, stack);
        return super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ZipToIt.LOGGER.info("And finish!");
        return super.finishUsing(stack, world, user);
    }

    public void zipcastPlayer(LivingEntity player, ItemStack stickyHandStack) {
        if(!(player instanceof ZipcasterPlayer zipcasterPlayer)) return;

        BlockHitResult raycast = ZipcastManager.raycastStickyHand(player, stickyHandStack, 0);
        if(raycast == null) return;

        Vec3d pos = raycast.getPos();
        zipcasterPlayer.ziptoit$zipcastToPos(pos);
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
