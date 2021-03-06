package silverclaw.vividbirds.common;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConfigHandler {

	private static final String CATEGORY_SPAWNING = "SPAWNING";
	private static final String CATEGORY_LYREBIRD = "LYREBIRD";
	
	private static Configuration config;
	
	public ConfigHandler(FMLPreInitializationEvent event) {

		config = new Configuration(event.getSuggestedConfigurationFile());
	}
	
	public void startConfig() {
		
		config.load();
		
		config.addCustomCategoryComment(CATEGORY_SPAWNING,
				"Select which birds should spawn and how many.\n"
				+	"(spawn probability,\n"
				+	"minimum amount per spawn,\n"
				+ 	"maximum amount per spawn\n)");
		
		config.addCustomCategoryComment(CATEGORY_LYREBIRD,
				"Lyrebird specific settings.");
	}
	
	
	public int[] getSpawnConfig(Class<? extends EntityLivingBase> living, Type biomeType,
			int defaultSpawnProbability, int defaultMinSpawn, int defaultMaxSpawn) {
	
		return config.get(CATEGORY_SPAWNING,
				living.getName().replaceFirst("Entity", "")
					+ "_" + biomeType.name(), 
				new int [] {
						defaultSpawnProbability,
						defaultMinSpawn,
						defaultMaxSpawn
						}
		).getIntList();
	}
	
	public String[] getLyrebirdLivingSounds(String [] defaults) {
		
		return config.get(
				CATEGORY_LYREBIRD,
				"LivingSounds",
				defaults)
				.getStringList();
	}

	public String[] getLyrebirdHurtSounds(String [] defaults) {
		
		return config.get(
				CATEGORY_LYREBIRD,
				"HurtSounds",
				defaults)
				.getStringList();
	}
	
	public String[] getLyrebirdDeatchSounds(String [] defaults) {
		
		return config.get(
				CATEGORY_LYREBIRD,
				"DeathSounds",
				defaults)
				.getStringList();
	}
	
	public void endConfig() {
		
		if(config.hasChanged()) {
			config.save();
		}
	}
}
