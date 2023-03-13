package com.github.alexthe666.citadel;
import com.github.alexthe666.citadel.client.gui.GuiCitadelPatreonConfig;
import com.github.alexthe666.citadel.client.event.EventGetOutlineColor;
import net.minecraft.client.gui.screen.Screen;
import com.github.alexthe666.citadel.client.gui.GuiCitadelBook;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.OnlyIn;
import com.github.alexthe666.citadel.client.CitadelItemstackRenderer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import net.minecraft.item.Item;
import com.github.alexthe666.citadel.server.entity.datatracker.IEntityData;
import net.minecraft.entity.Entity;
import com.github.alexthe666.citadel.server.entity.datatracker.EntityProperties;
import com.github.alexthe666.citadel.server.entity.datatracker.EntityDataHandler;
import net.minecraft.entity.player.PlayerEntity;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.nbt.CompoundNBT;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.entity.LivingEntity;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.CustomizeSkinScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import com.github.alexthe666.citadel.client.CitadelPatreonRenderer;
import com.github.alexthe666.citadel.client.patreon.SpaceStationPatreonRenderer;
import java.io.IOException;
import com.github.alexthe666.citadel.client.model.TabulaModelHandler;
import net.minecraft.util.ResourceLocation;
import com.github.alexthe666.citadel.client.model.TabulaModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import noppes.npcs.entity.EntityCustomNpc;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = { Dist.CLIENT })
public class ClientProxy extends ServerProxy{
	
	public static TabulaModel CITADEL_MODEL;
    private static final ResourceLocation CITADEL_TEXTURE;
    private static final ResourceLocation CITADEL_TEXTURE_RED;
    private static final ResourceLocation CITADEL_TEXTURE_GRAY;
    
    @Override
    public void onPreInit() {
        try {
            ClientProxy.CITADEL_MODEL = new TabulaModel(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/citadel/models/citadel_model"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        CitadelPatreonRenderer.register("citadel", new SpaceStationPatreonRenderer(ClientProxy.CITADEL_TEXTURE));
        CitadelPatreonRenderer.register("citadel_red", new SpaceStationPatreonRenderer(ClientProxy.CITADEL_TEXTURE_RED));
        CitadelPatreonRenderer.register("citadel_gray", new SpaceStationPatreonRenderer(ClientProxy.CITADEL_TEXTURE_GRAY));
    }
    
    @SubscribeEvent
    public void openCustomizeSkinScreen(final GuiScreenEvent.InitGuiEvent event) {

    }
    
    @SubscribeEvent
    public void playerRender(final RenderPlayerEvent.Post event) {
        final MatrixStack matrixStackIn = event.getMatrixStack();
        final String username = event.getPlayer().getName().getUnformattedComponentText();
        if (!event.getPlayer().isWearing(PlayerModelPart.CAPE)) {
            return;
        }
        if (Citadel.PATREONS.contains(username)) {
            final CompoundNBT tag = CitadelEntityData.getOrCreateCitadelTag((LivingEntity)Minecraft.getInstance().player);
            final String rendererName = tag.contains("CitadelFollowerType") ? tag.getString("CitadelFollowerType") : "citadel";
            if (!rendererName.equals("none")) {
                final CitadelPatreonRenderer renderer = CitadelPatreonRenderer.get(rendererName);
                if (renderer != null) {
                    final float distance = tag.contains("CitadelRotateDistance") ? tag.getFloat("CitadelRotateDistance") : 2.0f;
                    final float speed = tag.contains("CitadelRotateSpeed") ? tag.getFloat("CitadelRotateSpeed") : 1.0f;
                    final float height = tag.contains("CitadelRotateHeight") ? tag.getFloat("CitadelRotateHeight") : 1.0f;
                    renderer.render(matrixStackIn, event.getBuffers(), event.getLight(), event.getPartialRenderTick(), event.getEntityLiving(), distance, speed, height);
                }
            }
        }
    }
    
    @Override
    public void handleAnimationPacket(final int entityId, final int index) {
        final PlayerEntity player = (PlayerEntity)Minecraft.getInstance().player;
        if (player != null) {
            final LivingEntity entity = (LivingEntity)player.world.getEntityByID(entityId);
            if (entity != null) {
            	if(entity instanceof EntityCustomNpc) {
            		EntityCustomNpc ent = (EntityCustomNpc) entity;
            		if (index == -1) {
            			((IAnimatedEntity)ent.modelData.getEntity(ent)).setAnimation(IAnimatedEntity.NO_ANIMATION);
                		
            		} else {
            			((IAnimatedEntity)ent.modelData.getEntity(ent)).setAnimation(((IAnimatedEntity)ent.modelData.getEntity(ent)).getAnimations()[index]);
                		
            		}
            		((IAnimatedEntity)ent.modelData.getEntity(ent)).setAnimationTick(0);
            	}
            }
        }
    }
    
    @Override
    public void handlePropertiesPacket(final String propertyID, final CompoundNBT compound, final int entityID) {
        if (compound == null) {
            return;
        }
        final PlayerEntity player = (PlayerEntity)Minecraft.getInstance().player;
        final Entity entity = player.world.getEntityByID(entityID);
        if (propertyID.equals("CitadelPatreonConfig") && entity instanceof LivingEntity) {
            CitadelEntityData.setCitadelTag((LivingEntity)entity, compound);
        }
        else if (entity != null) {
            final IEntityData extendedProperties = EntityDataHandler.INSTANCE.getEntityData((Object)entity, propertyID);
            if (extendedProperties instanceof EntityProperties) {
                final EntityProperties properties = (EntityProperties)(EntityProperties)extendedProperties;
                properties.loadTrackingSensitiveData(compound);
                properties.onSync();
            }
        }
    }
    
    @Override
    public Item.Properties setupISTER(final Item.Properties group) {
        return group.setISTER((Supplier)ClientProxy::getTEISR);
    }
    
    @OnlyIn(Dist.CLIENT)
    public static Callable<ItemStackTileEntityRenderer> getTEISR() {
        return (Callable<ItemStackTileEntityRenderer>)CitadelItemstackRenderer::new;
    }
    
    @Override
    public void openBookGUI(final ItemStack book) {
    }
    
    @SubscribeEvent
    public void outlineColorTest(final EventGetOutlineColor event) {
    }
    
    static {
        CITADEL_TEXTURE = new ResourceLocation("citadel", "textures/patreon/citadel_model.png");
        CITADEL_TEXTURE_RED = new ResourceLocation("citadel", "textures/patreon/citadel_model_red.png");
        CITADEL_TEXTURE_GRAY = new ResourceLocation("citadel", "textures/patreon/citadel_model_gray.png");
    }
}
