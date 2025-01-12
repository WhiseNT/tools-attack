package com.whisent.tools_attack.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Enchantment.class)
public abstract class EnchantmentMixin {




    private static final String SWEEPING_EDGE_KEY = "minecraft:sweeping";
    @Inject(method = "canEnchant", at = @At("HEAD"), cancellable = true)
    private void onCanEnchant(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (BuiltInRegistries.ENCHANTMENT.getKey((Enchantment)(Object)this).toString().equals(SWEEPING_EDGE_KEY)) {
            Item item = stack.getItem();
            if (item instanceof SwordItem ||
                item instanceof AxeItem ||
                item instanceof ShovelItem ||
                item instanceof HoeItem ||
                item instanceof TridentItem ||
                item instanceof PickaxeItem) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }

}
