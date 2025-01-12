package com.whisent.tools_attack.mixin;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {


    @Shadow public abstract float getAttackStrengthScale(float p_36404_);

    @Shadow public abstract void sweepAttack();

    @Shadow public abstract void attack(Entity p_36347_);

    private float p_36404_;
    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
        this.getAttackStrengthScale(p_36404_);
        this.sweepAttack();
    }
    @Inject(method = "attack",at = @At("HEAD"))
    public void attackMixin(Entity target, CallbackInfo ci){

        if (target != null && !this.level().isClientSide()) {
            ItemStack mainHandItem = ((Player)(Object)this).getItemInHand(InteractionHand.MAIN_HAND);
            boolean axeCan = mainHandItem.getItem() instanceof AxeItem;
            boolean shovelCan = mainHandItem.getItem() instanceof ShovelItem;
            boolean swordCan = mainHandItem.getItem() instanceof SwordItem;
            boolean pickaxeCan = mainHandItem.getItem() instanceof PickaxeItem;
            boolean hoeCan = mainHandItem.getItem() instanceof HoeItem;
            boolean tridentCan = mainHandItem.getItem() instanceof TridentItem;
            boolean canSweep = axeCan || shovelCan || swordCan || hoeCan || tridentCan || pickaxeCan;
            if (canSweep) {
                float f2 = this.getAttackStrengthScale(0.5F);
                boolean flag = f2 > 0.9F;
                boolean flag2 = flag && this.fallDistance > 0.0F && !this.onGround() && !this.onClimbable() && !this.isInWater() &&
                        !this.hasEffect(MobEffects.BLINDNESS) && !this.isPassenger() && target instanceof LivingEntity;
                boolean flag3 = false;
                double d0 = (double)(this.walkDist - this.walkDistO);
                boolean flag4 = this.onGround() && d0 < (double)this.getSpeed();
                flag2 = flag2 && !this.isSprinting();
                if (!flag2 && !this.isSprinting() && flag4) {
                    if (flag && mainHandItem.getItem() instanceof SwordItem) {
                        flag3 = true;
                    }
                    if (pickaxeCan && f2>0.3F && flag4){
                        flag3 = true;
                    }
                    if ((axeCan || shovelCan || tridentCan)  && f2>0.6F && flag4) {
                        flag3 = true;
                    }
                }


                // 如果满足横扫攻击条件，则执行横扫攻击逻辑
                if (flag3) {
                    float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio((Player)(Object)this) * f2;

                    for(LivingEntity livingentity : this.level().getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(1.0D, 0.25D, 1.0D))) {
                        if (livingentity != this && livingentity != target && !this.isAlliedTo(livingentity) && (!(livingentity instanceof ArmorStand) || !((ArmorStand)livingentity).isMarker()) && this.distanceToSqr(livingentity) < 9.0D) {
                            livingentity.knockback((double)0.4F, (double) Mth.sin(this.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(this.getYRot() * ((float)Math.PI / 180F))));
                            livingentity.hurt(this.damageSources().playerAttack((Player)(Object)this), f3);
                        }
                    }
                    float SoundPitch = 0;
                    if (axeCan || pickaxeCan) {
                        SoundPitch = -0.2F;
                    }
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, this.getSoundSource(), 1.0F, 1.0F+SoundPitch);
                    this.sweepAttack();
                }
            }
        }
    }
}
