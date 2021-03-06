package silverclaw.vividbirds.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import silverclaw.vividbirds.common.FeatherVariant;
import silverclaw.vividbirds.common.entity.ai.EntityAIFlyingAvoidAttacker;
import silverclaw.vividbirds.common.entity.ai.EntityAIPickupItem;

import com.google.common.base.Predicate;

public class EntityVulture extends EntityMob {
	
	private final static Potion[] PREY_EFFECTS = new Potion [] {
		
		Potion.blindness,
		Potion.confusion,
		Potion.harm,
		Potion.hunger,
		Potion.moveSlowdown,
		Potion.poison,
		Potion.weakness,
		Potion.wither
	};

	private final static Item [] FOOD_ITEMS = new Item [] {
		
		Items.beef,
		Items.chicken,
		Items.cooked_beef,
		Items.cooked_chicken,
		Items.cooked_fish,
		Items.cooked_mutton,
		Items.cooked_porkchop,
		Items.cooked_rabbit,
		Items.egg,
		Items.fish,
		Items.mutton,
		Items.porkchop,
		Items.rabbit,
		Items.rabbit_stew
	};
		
	public EntityVulture(World worldObj) {
		
		super(worldObj);

		setSize(1f, 1.6f);
		stepHeight = 1.2f;
		limbSwingAmount = 0.8f;
				
		tasks.addTask(2, new EntityAITempt(this, 1.3, Items.rotten_flesh, false));
		tasks.addTask(1, new EntityAIPanic(this, 1.4f));
		
		tasks.addTask(2, new EntityAILeapAtTarget(this, 0.5F));
		tasks.addTask(3, new EntityAIAvoidEntity<EntityLivingBase>(
				this, EntityLivingBase.class,
				new Predicate<EntityLivingBase>() {

			@Override
			public boolean apply(EntityLivingBase entity) {
					
					if(entity instanceof EntityVulture) return false;
					if(entity instanceof EntityWaterMob) return false;
					if(entity instanceof EntityCreeper) {
						return entity.getHealth() < 2; 
					}
					if (entity.isBurning()) return entity.getHealth()
							> entity.getMaxHealth()/3;
					if(entity.getHealth() < 2.5f) return false;
					for(Potion potion : PREY_EFFECTS) {
						if(entity.getActivePotionEffect(potion) != null) 
							return entity.getHealth() > entity.getMaxHealth()/2;
					}
				return true;
			}
		}, 3, 1.1, 1.4));
		
		tasks.addTask(4, new EntityAIFlyingAvoidAttacker(
				this, 1.3f, 500, 400, 85, 20, getMaxHealth() / 2));
		
		tasks.addTask(1, new EntityAISwimming(this));
		
		tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityLivingBase.class, 1.1, true));
		tasks.addTask(5, new EntityAIWatchClosest(this, EntityLivingBase.class, 18.0F));
		tasks.addTask(5, new EntityAIWander(this, 1.0));
		tasks.addTask(6, new EntityAILookIdle(this));
		
		targetTasks.addTask(3, new EntityAINearestAttackableTarget<EntityLivingBase>(
				this, EntityLivingBase.class, false));
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
		targetTasks.addTask(1, new EntityAIPickupItem(this, Items.rotten_flesh, 8, 1.1f, 0.3f));
		
		for(Item item : FOOD_ITEMS) {
			
			targetTasks.addTask(3, new EntityAIPickupItem(this, item, 6, 1.0f, 0.3f));
		}

	}

	@Override
	public void setAttackTarget(EntityLivingBase target) {
		
		if(target instanceof EntityVulture) return;
		super.setAttackTarget(target);
	}

	@Override
	public boolean attackEntityAsMob(Entity target) {
		
		boolean success = super.attackEntityAsMob(target);
		if(target instanceof EntityLivingBase) {
			if(rand.nextFloat() < (worldObj.getDifficulty() == EnumDifficulty.HARD ? 0.3f : 0.1f)) {
				((EntityLivingBase) target).addPotionEffect(new PotionEffect(
						rand.nextBoolean() ? Potion.poison.id : Potion.weakness.id, rand.nextInt(500)));
			}
		}
		return success;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource damageSource, float amount) {
		
		if(damageSource == DamageSource.cactus) return false;
		boolean success = super.attackEntityFrom(damageSource, amount);
		Entity attacker = damageSource.getEntity();		
		if(attacker != null) {
			float punch = isInWater() ? 0.05f : 0.12f;
			motionY += 0.25f + punch;
			motionX += (this.posX - attacker.posX) * punch;
			motionZ += (this.posZ - attacker.posZ) * punch;
			limbSwingAmount *= 6;
			setJumping(true);
		}
		return success;
	}

	@Override
	protected void dropFewItems(boolean drop, int amount) {
		
		entityDropItem(new ItemStack(Items.feather, rand.nextInt(2), 
				FeatherVariant.VULTURE.getMetaData()), 0.4f);
		if(rand.nextFloat() < 0.8f) {
			dropItem(Items.bone, 1);
		} else {
			dropItem(Items.rotten_flesh, 2);
		}
	}

	@Override
	protected void applyEntityAttributes() {
		
		super.applyEntityAttributes();
		
		switch(worldObj.getDifficulty()) {
		
		case EASY:
			getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1);
			getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(20);
			getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(7);
			break;
			
		case NORMAL:
			getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(2);
			getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40);
			getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8);
			break;
			
		case HARD:
			getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3);
			getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(45);
			getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(9);
			break;
			
		case PEACEFUL:
			// should not spawn in peaceful
			break;
			
		default:
			break;
		}
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25);
}

	@Override
	public void fall(float p_fall_1_, float p_fall_2_) {}

	@Override
	protected String getLivingSound() {
		return "birdmod:vulture";
	}
}
