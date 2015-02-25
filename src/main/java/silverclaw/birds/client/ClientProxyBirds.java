package silverclaw.birds.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import silverclaw.birds.client.model.ModelCrow;
import silverclaw.birds.client.model.ModelKiwi;
import silverclaw.birds.client.model.ModelLyrebird;
import silverclaw.birds.client.model.ModelOstrich;
import silverclaw.birds.client.model.ModelPenguin;
import silverclaw.birds.client.model.ModelSeagull;
import silverclaw.birds.client.model.ModelVulture;
import silverclaw.birds.client.render.RenderCrow;
import silverclaw.birds.client.render.RenderKiwi;
import silverclaw.birds.client.render.RenderLyrebird;
import silverclaw.birds.client.render.RenderOstrich;
import silverclaw.birds.client.render.RenderPenguin;
import silverclaw.birds.client.render.RenderSeagull;
import silverclaw.birds.client.render.RenderVulture;
import silverclaw.birds.common.BirdItem;
import silverclaw.birds.common.Birds;
import silverclaw.birds.common.CommonProxyBirds;
import silverclaw.birds.common.FeatherVariant;
import silverclaw.birds.common.entity.EntityCrow;
import silverclaw.birds.common.entity.EntityKiwi;
import silverclaw.birds.common.entity.EntityLyrebird;
import silverclaw.birds.common.entity.EntityOstrich;
import silverclaw.birds.common.entity.EntityPenguin;
import silverclaw.birds.common.entity.EntitySeagull;
import silverclaw.birds.common.entity.EntityVulture;

public class ClientProxyBirds extends CommonProxyBirds {
	
	@Override
	public void registerRenderers() {
		
		RenderManager manager = Minecraft.getMinecraft().getRenderManager();
	
		RenderingRegistry.registerEntityRenderingHandler(EntityLyrebird.class,  
				new RenderLyrebird(manager, new ModelLyrebird(), 0.3f));
		
		RenderingRegistry.registerEntityRenderingHandler(EntityVulture.class,
				new RenderVulture(manager, new ModelVulture(), 0.5f));
		
		RenderingRegistry.registerEntityRenderingHandler(EntityOstrich.class,
				new RenderOstrich(manager, new ModelOstrich(), 1));
		
		RenderingRegistry.registerEntityRenderingHandler(EntityKiwi.class,
				new RenderKiwi(manager, new ModelKiwi(), 0.2f));
		
		RenderingRegistry.registerEntityRenderingHandler(EntitySeagull.class,
				new RenderSeagull(manager, new ModelSeagull(), 0.4f));
		
		RenderingRegistry.registerEntityRenderingHandler(EntityPenguin.class,
				new RenderPenguin(manager, new ModelPenguin(), 0.5f));
	}

	@Override
	public void registerItemsResources() {
		

		ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
	
		
		for(BirdItem item : BirdItem.values()) {
			
			mesher.register(item.getInstance(), 0,
					new ModelResourceLocation(item.getResourceName(), "inventory"));
		}
		
		registerFeathers();
	}

	private void registerFeathers() {
		
		ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

		String[] itemNames = new String [FeatherVariant.values().length + 1];
		itemNames [0] = "feather";
		
		int i = 1;
		
		for(FeatherVariant feather : FeatherVariant.values()) {
			
			itemNames[i++] = feather.getResourceName();
			mesher.register(Items.feather, feather.getMetaData(),
					new ModelResourceLocation(feather.getResourceName(), "inventory"));
		}
		ModelBakery.addVariantName(Items.feather, itemNames);
	}
}