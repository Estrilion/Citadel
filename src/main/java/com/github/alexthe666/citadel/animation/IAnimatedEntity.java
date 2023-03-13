package com.github.alexthe666.citadel.animation;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.world.World;

public class IAnimatedEntity extends LivingEntity{


	protected IAnimatedEntity(EntityType<? extends LivingEntity> type, World worldIn) {
		super(type, worldIn);
		// TODO Auto-generated constructor stub
	}

	public static final String NO_ANIMATION = null;

	public void setAnimation(String noAnimation) {
		// TODO Auto-generated method stub
		
	}

	public String[] getAnimations() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAnimationTick(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Iterable<ItemStack> getArmorInventoryList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack getItemStackFromSlot(EquipmentSlotType slotIn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HandSide getPrimaryHand() {
		// TODO Auto-generated method stub
		return null;
	}

}
