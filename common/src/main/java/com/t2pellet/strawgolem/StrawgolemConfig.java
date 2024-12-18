package com.t2pellet.strawgolem;

import com.t2pellet.tlib.config.api.Config;
import com.t2pellet.tlib.config.api.property.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.ArrayList;

@Config.ModConfig(comment = "Config for Straw Golem")
public class StrawgolemConfig extends Config {

    public StrawgolemConfig() throws IOException, IllegalAccessException {
        super(Constants.MOD_ID);
    }

    @Section(name = "Harvesting", description = "Harvesting related options")
    public static class Harvesting {
        @Entry(comment = "Whether the golem should use the new AI when finding crops")
        public static final BoolProperty newAI = new BoolProperty(true);
        @Entry(comment = "Range for golems to harvest and deliver to a chest")
        public static final IntProperty harvestRange = new IntProperty(24, 8, 48);
        @Entry(comment = "Whether the golem should harvest gourd blocks like pumpkins and melons. Will apply over whitelist, but specific gourd blocks can still be blacklisted.")
        public static final BoolProperty shouldHarvestBlocks = new BoolProperty(true);
        @Entry(comment = "Blacklisted crops. Must use valid resource locations")
        public static final ListProperty<String> blacklist = createBlockIDList();
        @Entry(comment = "Whether to enable whitelist. (Will ignore blacklist)")
        public static final BoolProperty enableWhitelist = new BoolProperty(false);
        @Entry(comment = "Whitelisted crops. Only applies if enableWhitelist=true. Must use valid resource locations")
        public static final ListProperty<String>  whitelist = createBlockIDList();
        @Entry(comment = "Whether to enable vine-harvesting (multi-block vertical crops, like tomatoes from Farmer's Delight)")
        public static final BoolProperty enableVineHarvest = new BoolProperty(true);
        @Entry(comment = "Whether golems should forever ignore crops or storages that are inaccessible after finishing a harvest (the golems will not try to reach them again even if possible)")
        public static final BoolProperty permanentIgnore = new BoolProperty(false);
    }

    @Section(name = "Lifespan", description = "Golem lifespan options")
    public static class Lifespan {
        @Entry(comment = "Whether to enable lifespan/decay/hunger feature")
        public static final BoolProperty enabled = new BoolProperty(true);
        @Entry(comment = "Base health. Requires restart")
        public static final IntProperty baseHealth = new IntProperty(6, 1, 10);
        @Entry(comment = "Whether being in the rain accelerates decay")
        public static final BoolProperty rainAcceleratesDecay = new BoolProperty(true);
        @Entry(comment = "Whether being in water accelerates decay")
        public static final BoolProperty waterAcceleratesDecay = new BoolProperty(true);
        @Entry(comment = "How many ticks before checking to see if golem decays")
        public static final IntProperty ticksToDecayCheck = new IntProperty(6000);
        @Entry(comment = "Chance to decay on check. (decayChance=5 means 1/5 chance)")
        public static final IntProperty decayChance = new IntProperty(4, 1, 100);
        @Entry(comment = "How many ticks before a golem increases its hunger state")
        public static final IntProperty hungerTicks = new IntProperty(24000);
        @Entry(comment = "Chance to repair on wheat usage. (repairChance=5 means 1/5 chance)")
        public static final IntProperty repairChance = new IntProperty(3, 1 ,100);
        @Entry(comment = "Item to repair the golem with. (Requires restart)")
        public static final StringProperty repairItem = new StringProperty("minecraft:wheat", s -> {
            return ResourceLocation.isValidResourceLocation(s) && BuiltInRegistries.ITEM.containsKey(new ResourceLocation(s));
        });
        @Entry(comment = "Item to feed the golem with. (Requires restart)")
        public static final StringProperty feedItem = new StringProperty("minecraft:apple", s -> {
            return ResourceLocation.isValidResourceLocation(s) && BuiltInRegistries.ITEM.containsKey(new ResourceLocation(s));
        });
        @Entry(comment = "Durability for barrels equipped to a straw golem")
        public static final IntProperty barrelDurability = new IntProperty(100, 1, 1000);
        @Entry(comment = "Item to repair barrels with. (Requires restart)")
        public static final StringProperty barrelItem = new StringProperty("minecraft:planks", s -> {
            return ResourceLocation.isValidResourceLocation(s) && BuiltInRegistries.ITEM.containsKey(new ResourceLocation(s));
        });
        @Entry(comment = "How much durability to restore from barrelItem. Set to zero to disable")
        public static final IntProperty barrelRepairAmount = new IntProperty(25, 0, 250);
    }

    @Section(name = "Behaviour", description = "Golem and Mob behaviour options")
    public static class Behaviour {
        @Entry(comment = "Should golems run from raiders")
        public static final BoolProperty golemsRunFromRaiders = new BoolProperty(true);
        @Entry(comment = "Should golems run from monsters")
        public static final BoolProperty golemsRunFromMonsters = new BoolProperty(true);
        @Entry(comment = "Should golems run from sheep and cows")
        public static final BoolProperty golemsRunFromLivestock = new BoolProperty(true);
        @Entry(comment = "Should golems avoid players")
        public static final BoolProperty golemsRunFromPlayers = new BoolProperty(true);
        @Entry(comment = "Should raiders attack golems")
        public static final BoolProperty raidersAttackGolems = new BoolProperty(true);
        @Entry(comment = "Should monsters attack golems")
        public static final BoolProperty monstersAttackGolems = new BoolProperty(false);
        @Entry(comment = "Should sheep and cows munch on golems")
        public static final BoolProperty livestockEatGolems = new BoolProperty(true);
        @Entry(comment = "Should golems panic when damaged")
        public static final BoolProperty golemsPanicWhenHurt = new BoolProperty(true);
        @Entry(comment = "How far the golem can wander")
        public static final IntProperty golemWanderRange = new IntProperty(24, 8, 48);
        @Entry(comment = "Walk speed for straw golem")
        public static final FloatProperty golemWalkSpeed = new FloatProperty(0.5F);
        @Entry(comment = "Walk speed for straw golem")
        public static final FloatProperty golemRunSpeed = new FloatProperty(0.8F);
    }

    @Section(name = "Visual", description = "Visual related settings")
    public static class Visual {
        @Entry(comment = "Whether the golem should shiver when in an accelerated decay state")
        public static final BoolProperty golemShiversWhenDecayingFast = new BoolProperty(true);
        @Entry(comment = "Whether the golem should shiver when cold")
        public static final BoolProperty golemShiversWhenCold = new BoolProperty(true);
        @Entry(comment = "Whether the golem texture should change as it decays")
        public static final BoolProperty golemDecayingTexture = new BoolProperty(true);
        @Entry(comment = "Show harvesting animation for regular crops. (Requires restart)")
        public static final BoolProperty showHarvestItemAnimation = new BoolProperty(true);
        @Entry(comment = "Show harvesting animation for block crops. (Requires restart)")
        public static final BoolProperty showHarvestBlockAnimation = new BoolProperty(true);
        @Entry(comment = "Chance per tick for a dying golem to spawn a fly particle. Higher # = lower chance")
        public static final IntProperty dyingGolemFlyChance = new IntProperty(80, 1, 2000);
        @Entry(comment = "Chance per tick for a dying golem to spawn a food particle. Higher # = lower chance")
        public static final IntProperty starvingGolemFoodChance = new IntProperty(160, 1, 2000);
    }

    @Section(name = "Experimental", description = "Experimental settings that may improve the golems")
    public static class Experimental {
        @Entry(comment = "Whether the golem should use an experimental harvesting method")
        public static final BoolProperty experimentalHarvesting = new BoolProperty(false);
    }
    private static ListProperty<String> createBlockIDList() {
        return ListProperty.of(new ArrayList<>(), (s) -> {
            return ResourceLocation.isValidResourceLocation(s) && BuiltInRegistries.BLOCK.containsKey(new ResourceLocation(s));
        });
    }
}
