package net.superkat.ziptoit.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.consume.UseAction;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.superkat.ziptoit.ZipToIt;
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
        if(user instanceof ZipcasterPlayer zipcasterPlayer) {
            if(zipcasterPlayer.shouldPlayZipcastActiveSound()) {
                world.playSound(null, user.getX(), user.getY(), user.getZ(),
                        SoundEvents.ITEM_TRIDENT_THUNDER.value(), SoundCategory.PLAYERS, 1f, 1.5f
                );
                world.playSound(null, user.getX(), user.getY(), user.getZ(),
                        SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 1f, 1f
                );
                world.playSound(null, user.getX(), user.getY(), user.getZ(),
                        SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.PLAYERS, 0.5f, 2f
                );
                world.playSound(null, user.getX(), user.getY(), user.getZ(),
                        SoundEvents.ITEM_TRIDENT_RETURN, SoundCategory.PLAYERS, 2f, 1f
                );
                zipcasterPlayer.setTicksSinceZipcastActivate(0);
            }
        }


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
        if(!player.getWorld().isClient) return;

        // I'm assuming because of a natural desync of data because nothing is perfect,
        // raycasting from the server gives opportunity to not land a hit on a block,
        // leaving the player unable to zipcast.
        // This is most noticeable trying to zipcast to the top of a block which is beneath air(empty blocks above)
        BlockHitResult raycast = ZipcastManager.raycastStickyHand(player, stickyHandStack, 0);
        if(raycast == null) return;

        ZipcastTarget zipcastTarget = ZipcastTarget.ofRaycast(player, raycast);
        ZipcastManager.startZipcast(player, zipcastTarget, true);

        // debug
        Vec3d pos = raycast.getPos();
        player.getWorld().addParticleClient(ParticleTypes.END_ROD, pos.getX(), pos.getY(), pos.getZ(), 0,0, 0);
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
