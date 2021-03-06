package nc.multiblock.fission;

import static nc.recipe.NCRecipes.*;

import java.util.*;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.objects.*;
import nc.config.NCConfig;
import nc.init.NCBlocks;
import nc.multiblock.PlacementRule;
import nc.multiblock.PlacementRule.*;
import nc.multiblock.fission.salt.tile.*;
import nc.multiblock.fission.solid.tile.*;
import nc.multiblock.fission.tile.*;
import nc.recipe.RecipeHelper;
import nc.util.StringHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public abstract class FissionPlacement {
	
	/** List of all defined rule parsers. Earlier entries are prioritised! */
	public static final List<PlacementRule.RuleParser<IFissionPart>> RULE_PARSER_LIST = new LinkedList<>();
	
	/** Map of all defined placement rules. */
	public static final Object2ObjectMap<String, PlacementRule<IFissionPart>> RULE_MAP = new PlacementRule.PlacementMap<>();
	
	/** List of all defined tooltip builders. Earlier entries are prioritised! */
	public static final List<PlacementRule.TooltipBuilder<IFissionPart>> TOOLTIP_BUILDER_LIST = new LinkedList<>();
	
	public static PlacementRule.TooltipRecipeHandler tooltip_recipe_handler;
	
	/** Map of all localised tooltips. */
	public static final Object2ObjectMap<String, String> TOOLTIP_MAP = new Object2ObjectOpenHashMap<>();
	
	public static void preInit() {
		RULE_PARSER_LIST.add(new DefaultRuleParser());
		
		TOOLTIP_BUILDER_LIST.add(new DefaultTooltipBuilder());
	}
	
	public static void init() {
		tooltip_recipe_handler = new TooltipRecipeHandler();
		
		RULE_MAP.put("", new PlacementRule.Or<>(new ArrayList<>()));
		
		addRule("water_sink", NCConfig.fission_sink_rule[0], new ItemStack(NCBlocks.solid_fission_sink, 1, 0));
		addRule("iron_sink", NCConfig.fission_sink_rule[1], new ItemStack(NCBlocks.solid_fission_sink, 1, 1));
		addRule("redstone_sink", NCConfig.fission_sink_rule[2], new ItemStack(NCBlocks.solid_fission_sink, 1, 2));
		addRule("quartz_sink", NCConfig.fission_sink_rule[3], new ItemStack(NCBlocks.solid_fission_sink, 1, 3));
		addRule("obsidian_sink", NCConfig.fission_sink_rule[4], new ItemStack(NCBlocks.solid_fission_sink, 1, 4));
		addRule("nether_brick_sink", NCConfig.fission_sink_rule[5], new ItemStack(NCBlocks.solid_fission_sink, 1, 5));
		addRule("glowstone_sink", NCConfig.fission_sink_rule[6], new ItemStack(NCBlocks.solid_fission_sink, 1, 6));
		addRule("lapis_sink", NCConfig.fission_sink_rule[7], new ItemStack(NCBlocks.solid_fission_sink, 1, 7));
		addRule("gold_sink", NCConfig.fission_sink_rule[8], new ItemStack(NCBlocks.solid_fission_sink, 1, 8));
		addRule("prismarine_sink", NCConfig.fission_sink_rule[9], new ItemStack(NCBlocks.solid_fission_sink, 1, 9));
		addRule("slime_sink", NCConfig.fission_sink_rule[10], new ItemStack(NCBlocks.solid_fission_sink, 1, 10));
		addRule("end_stone_sink", NCConfig.fission_sink_rule[11], new ItemStack(NCBlocks.solid_fission_sink, 1, 11));
		addRule("purpur_sink", NCConfig.fission_sink_rule[12], new ItemStack(NCBlocks.solid_fission_sink, 1, 12));
		addRule("diamond_sink", NCConfig.fission_sink_rule[13], new ItemStack(NCBlocks.solid_fission_sink, 1, 13));
		addRule("emerald_sink", NCConfig.fission_sink_rule[14], new ItemStack(NCBlocks.solid_fission_sink, 1, 14));
		addRule("copper_sink", NCConfig.fission_sink_rule[15], new ItemStack(NCBlocks.solid_fission_sink, 1, 15));
		addRule("tin_sink", NCConfig.fission_sink_rule[16], new ItemStack(NCBlocks.solid_fission_sink2, 1, 0));
		addRule("lead_sink", NCConfig.fission_sink_rule[17], new ItemStack(NCBlocks.solid_fission_sink2, 1, 1));
		addRule("boron_sink", NCConfig.fission_sink_rule[18], new ItemStack(NCBlocks.solid_fission_sink2, 1, 2));
		addRule("lithium_sink", NCConfig.fission_sink_rule[19], new ItemStack(NCBlocks.solid_fission_sink2, 1, 3));
		addRule("magnesium_sink", NCConfig.fission_sink_rule[20], new ItemStack(NCBlocks.solid_fission_sink2, 1, 4));
		addRule("manganese_sink", NCConfig.fission_sink_rule[21], new ItemStack(NCBlocks.solid_fission_sink2, 1, 5));
		addRule("aluminum_sink", NCConfig.fission_sink_rule[22], new ItemStack(NCBlocks.solid_fission_sink2, 1, 6));
		addRule("silver_sink", NCConfig.fission_sink_rule[23], new ItemStack(NCBlocks.solid_fission_sink2, 1, 7));
		addRule("fluorite_sink", NCConfig.fission_sink_rule[24], new ItemStack(NCBlocks.solid_fission_sink2, 1, 8));
		addRule("villiaumite_sink", NCConfig.fission_sink_rule[25], new ItemStack(NCBlocks.solid_fission_sink2, 1, 9));
		addRule("carobbiite_sink", NCConfig.fission_sink_rule[26], new ItemStack(NCBlocks.solid_fission_sink2, 1, 10));
		addRule("arsenic_sink", NCConfig.fission_sink_rule[27], new ItemStack(NCBlocks.solid_fission_sink2, 1, 11));
		addRule("liquid_nitrogen_sink", NCConfig.fission_sink_rule[28], new ItemStack(NCBlocks.solid_fission_sink2, 1, 12));
		addRule("liquid_helium_sink", NCConfig.fission_sink_rule[29], new ItemStack(NCBlocks.solid_fission_sink2, 1, 13));
		addRule("enderium_sink", NCConfig.fission_sink_rule[30], new ItemStack(NCBlocks.solid_fission_sink2, 1, 14));
		addRule("cryotheum_sink", NCConfig.fission_sink_rule[31], new ItemStack(NCBlocks.solid_fission_sink2, 1, 15));
		
		addRule("standard_heater", NCConfig.fission_heater_rule[0], new ItemStack(NCBlocks.salt_fission_heater, 1, 0));
		addRule("iron_heater", NCConfig.fission_heater_rule[1], new ItemStack(NCBlocks.salt_fission_heater, 1, 1));
		addRule("redstone_heater", NCConfig.fission_heater_rule[2], new ItemStack(NCBlocks.salt_fission_heater, 1, 2));
		addRule("quartz_heater", NCConfig.fission_heater_rule[3], new ItemStack(NCBlocks.salt_fission_heater, 1, 3));
		addRule("obsidian_heater", NCConfig.fission_heater_rule[4], new ItemStack(NCBlocks.salt_fission_heater, 1, 4));
		addRule("nether_brick_heater", NCConfig.fission_heater_rule[5], new ItemStack(NCBlocks.salt_fission_heater, 1, 5));
		addRule("glowstone_heater", NCConfig.fission_heater_rule[6], new ItemStack(NCBlocks.salt_fission_heater, 1, 6));
		addRule("lapis_heater", NCConfig.fission_heater_rule[7], new ItemStack(NCBlocks.salt_fission_heater, 1, 7));
		addRule("gold_heater", NCConfig.fission_heater_rule[8], new ItemStack(NCBlocks.salt_fission_heater, 1, 8));
		addRule("prismarine_heater", NCConfig.fission_heater_rule[9], new ItemStack(NCBlocks.salt_fission_heater, 1, 9));
		addRule("slime_heater", NCConfig.fission_heater_rule[10], new ItemStack(NCBlocks.salt_fission_heater, 1, 10));
		addRule("end_stone_heater", NCConfig.fission_heater_rule[11], new ItemStack(NCBlocks.salt_fission_heater, 1, 11));
		addRule("purpur_heater", NCConfig.fission_heater_rule[12], new ItemStack(NCBlocks.salt_fission_heater, 1, 12));
		addRule("diamond_heater", NCConfig.fission_heater_rule[13], new ItemStack(NCBlocks.salt_fission_heater, 1, 13));
		addRule("emerald_heater", NCConfig.fission_heater_rule[14], new ItemStack(NCBlocks.salt_fission_heater, 1, 14));
		addRule("copper_heater", NCConfig.fission_heater_rule[15], new ItemStack(NCBlocks.salt_fission_heater, 1, 15));
		addRule("tin_heater", NCConfig.fission_heater_rule[16], new ItemStack(NCBlocks.salt_fission_heater2, 1, 0));
		addRule("lead_heater", NCConfig.fission_heater_rule[17], new ItemStack(NCBlocks.salt_fission_heater2, 1, 1));
		addRule("boron_heater", NCConfig.fission_heater_rule[18], new ItemStack(NCBlocks.salt_fission_heater2, 1, 2));
		addRule("lithium_heater", NCConfig.fission_heater_rule[19], new ItemStack(NCBlocks.salt_fission_heater2, 1, 3));
		addRule("magnesium_heater", NCConfig.fission_heater_rule[20], new ItemStack(NCBlocks.salt_fission_heater2, 1, 4));
		addRule("manganese_heater", NCConfig.fission_heater_rule[21], new ItemStack(NCBlocks.salt_fission_heater2, 1, 5));
		addRule("aluminum_heater", NCConfig.fission_heater_rule[22], new ItemStack(NCBlocks.salt_fission_heater2, 1, 6));
		addRule("silver_heater", NCConfig.fission_heater_rule[23], new ItemStack(NCBlocks.salt_fission_heater2, 1, 7));
		addRule("fluorite_heater", NCConfig.fission_heater_rule[24], new ItemStack(NCBlocks.salt_fission_heater2, 1, 8));
		addRule("villiaumite_heater", NCConfig.fission_heater_rule[25], new ItemStack(NCBlocks.salt_fission_heater2, 1, 9));
		addRule("carobbiite_heater", NCConfig.fission_heater_rule[26], new ItemStack(NCBlocks.salt_fission_heater2, 1, 10));
		addRule("arsenic_heater", NCConfig.fission_heater_rule[27], new ItemStack(NCBlocks.salt_fission_heater2, 1, 11));
		addRule("liquid_nitrogen_heater", NCConfig.fission_heater_rule[28], new ItemStack(NCBlocks.salt_fission_heater2, 1, 12));
		addRule("liquid_helium_heater", NCConfig.fission_heater_rule[29], new ItemStack(NCBlocks.salt_fission_heater2, 1, 13));
		addRule("enderium_heater", NCConfig.fission_heater_rule[30], new ItemStack(NCBlocks.salt_fission_heater2, 1, 14));
		addRule("cryotheum_heater", NCConfig.fission_heater_rule[31], new ItemStack(NCBlocks.salt_fission_heater2, 1, 15));
	}
	
	public static void addRule(String id, String rule, Object... blocks) {
		RULE_MAP.put(id, parse(rule));
		for (Object block : blocks) {
			tooltip_recipe_handler.addRecipe(block, id);
		}
	}
	
	public static void postInit() {
		for (Object2ObjectMap.Entry<String, PlacementRule<IFissionPart>> entry : RULE_MAP.object2ObjectEntrySet()) {
			for (PlacementRule.TooltipBuilder<IFissionPart> builder : TOOLTIP_BUILDER_LIST) {
				String tooltip = builder.buildTooltip(entry.getValue());
				if (tooltip != null) TOOLTIP_MAP.put(entry.getKey(), tooltip);
			}
		}
	}
	
	// Default Rule Parser
	
	public static PlacementRule<IFissionPart> parse(String string) {
		return PlacementRule.parse(string, RULE_PARSER_LIST);
	}
	
	/** Rule parser for all rule types available in base NC. */
	public static class DefaultRuleParser extends PlacementRule.DefaultRuleParser<IFissionPart> {
		
		@Override
		protected @Nullable PlacementRule<IFissionPart> partialParse(String s) {
			s = s.toLowerCase(Locale.ROOT);
			
			s = s.replaceAll("at exactly one vertex", "vertex");
			
			boolean exact = s.contains("exact"), atMost = s.contains("at most");
			boolean axial = s.contains("axial"), vertex = s.contains("vertex");
			
			if ((exact && atMost) || (axial && vertex)) return null;
			
			s = s.replaceAll("at least", "");
			s = s.replaceAll("exactly", "");
			s = s.replaceAll("exact", "");
			s = s.replaceAll("at most", "");
			s = s.replaceAll("axially", "");
			s = s.replaceAll("axial", "");
			s = s.replaceAll("at one vertex", "");
			s = s.replaceAll("at a vertex", "");
			s = s.replaceAll("at vertex", "");
			s = s.replaceAll("vertex", "");
			
			int amount = -1;
			String rule = null, type = null;
			
			String[] split = s.split(Pattern.quote(" "));
			for (int i = 0; i < split.length; i++) {
				if (StringHelper.NUMBER_S2I_MAP.containsKey(split[i])) {
					amount = StringHelper.NUMBER_S2I_MAP.getInt(split[i]);
				}
				else if (rule == null) {
					if (split[i].contains("wall") || split[i].contains("casing")) {
						rule = "casing";
					}
					else if (split[i].contains("moderator")) {
						rule = "moderator";
					}
					else if (split[i].contains("reflector")) {
						rule = "reflector";
					}
					else if (split[i].contains("cell")) {
						rule = "cell";
					}
					else if (split[i].contains("sink")) {
						rule = "sink";
						if (i > 0) type = split[i - 1];
						else return null;
					}
					else if (split[i].contains("vessel")) {
						rule = "vessel";
					}
					else if (split[i].contains("heater")) {
						rule = "heater";
						if (i > 0) type = split[i - 1];
						else return null;
					}
				}
			}
			
			if (amount < 0 || rule == null) return null;
			
			CountType countType = exact ? CountType.EXACTLY : (atMost ? CountType.AT_MOST : CountType.AT_LEAST);
			AdjacencyType adjType = axial ? AdjacencyType.AXIAL : (vertex ? AdjacencyType.VERTEX : AdjacencyType.STANDARD);
			
			if (rule.equals("casing")) {
				return new AdjacentCasing(amount, countType, adjType);
			}
			else if (rule.equals("moderator")) {
				return new AdjacentModerator(amount, countType, adjType);
			}
			else if (rule.equals("reflector")) {
				return new AdjacentReflector(amount, countType, adjType);
			}
			else if (rule.equals("cell")) {
				return new AdjacentCell(amount, countType, adjType);
			}
			else if (rule.equals("sink")) {
				return new AdjacentSink(amount, countType, adjType, type);
			}
			else if (rule.equals("vessel")) {
				return new AdjacentVessel(amount, countType, adjType);
			}
			else if (rule.equals("heater")) {
				return new AdjacentHeater(amount, countType, adjType, type);
			}
			
			return null;
		}
	}
	
	// Adjacent
	
	public static abstract class Adjacent extends PlacementRule.Adjacent<IFissionPart> {
		
		public Adjacent(String dependency, int amount, CountType countType, AdjacencyType adjType) {
			super(dependency, amount, countType, adjType);
		}
	}
	
	public static class AdjacentCasing extends Adjacent {
		
		public AdjacentCasing(int amount, CountType countType, AdjacencyType adjType) {
			super("casing", amount, countType, adjType);
		}
		
		@Override
		public boolean satisfied(IFissionPart part, EnumFacing dir) {
			return isCasing(part.getMultiblock(), part.getTilePos().offset(dir));
		}
	}
	
	public static class AdjacentModerator extends Adjacent {
		
		public AdjacentModerator(int amount, CountType countType, AdjacencyType adjType) {
			super("moderator", amount, countType, adjType);
		}
		
		@Override
		public boolean satisfied(IFissionPart part, EnumFacing dir) {
			return isActiveModerator(part.getMultiblock(), part.getTilePos().offset(dir));
		}
	}
	
	public static class AdjacentReflector extends Adjacent {
		
		public AdjacentReflector(int amount, CountType countType, AdjacencyType adjType) {
			super("reflector", amount, countType, adjType);
		}
		
		@Override
		public boolean satisfied(IFissionPart part, EnumFacing dir) {
			return isActiveReflector(part.getMultiblock(), part.getTilePos().offset(dir));
		}
	}
	
	public static class AdjacentCell extends Adjacent {
		
		public AdjacentCell(int amount, CountType countType, AdjacencyType adjType) {
			super("cell", amount, countType, adjType);
		}
		
		@Override
		public boolean satisfied(IFissionPart part, EnumFacing dir) {
			return isFunctionalCell(part.getMultiblock(), part.getTilePos().offset(dir));
		}
	}
	
	public static class AdjacentSink extends Adjacent {
		
		protected final String sinkType;
		
		public AdjacentSink(int amount, CountType countType, AdjacencyType adjType, String sinkType) {
			super(sinkType + "_sink", amount, countType, adjType);
			this.sinkType = sinkType;
		}
		
		@Override
		public void checkIsRuleAllowed(String ruleID) {
			super.checkIsRuleAllowed(ruleID);
			if (countType != CountType.AT_LEAST && sinkType.equals("any")) {
				throw new IllegalArgumentException((countType == CountType.EXACTLY ? "Exact 'any sink'" : "'At most n of any sink'") + " placement rule with ID \"" + ruleID + "\" is disallowed due to potential ambiguity during rule checks!");
			}
		}
		
		@Override
		public boolean satisfied(IFissionPart part, EnumFacing dir) {
			return isValidSink(part.getMultiblock(), part.getTilePos().offset(dir), sinkType);
		}
	}
	
	public static class AdjacentVessel extends Adjacent {
		
		public AdjacentVessel(int amount, CountType countType, AdjacencyType adjType) {
			super("vessel", amount, countType, adjType);
		}
		
		@Override
		public boolean satisfied(IFissionPart part, EnumFacing dir) {
			return isFunctionalVessel(part.getMultiblock(), part.getTilePos().offset(dir));
		}
	}
	
	public static class AdjacentHeater extends Adjacent {
		
		protected final String heaterType;
		
		public AdjacentHeater(int amount, CountType countType, AdjacencyType adjType, String heaterType) {
			super(heaterType + "_heater", amount, countType, adjType);
			this.heaterType = heaterType;
		}
		
		@Override
		public void checkIsRuleAllowed(String ruleID) {
			super.checkIsRuleAllowed(ruleID);
			if (countType != CountType.AT_LEAST && heaterType.equals("any")) {
				throw new IllegalArgumentException((countType == CountType.EXACTLY ? "Exact 'any heater'" : "'At most n of any heater'") + " placement rule with ID \"" + ruleID + "\" is disallowed due to potential ambiguity during rule checks!");
			}
		}
		
		@Override
		public boolean satisfied(IFissionPart part, EnumFacing dir) {
			return isValidHeater(part.getMultiblock(), part.getTilePos().offset(dir), heaterType);
		}
	}
	
	// Helper Methods
	
	public static boolean isCasing(FissionReactor reactor, BlockPos pos) {
		TileEntity tile = reactor.WORLD.getTileEntity(pos);
		return tile instanceof TileFissionPart && ((TileFissionPart) tile).getPartPositionType().isGoodForWall();
	}
	
	public static boolean isActiveModerator(FissionReactor reactor, BlockPos pos) {
		IFissionComponent component = reactor.getPartMap(IFissionComponent.class).get(pos.toLong());
		return (component != null && component.isActiveModerator()) || (reactor.activeModeratorCache.contains(pos.toLong()) && RecipeHelper.blockRecipe(fission_moderator, reactor.WORLD, pos) != null);
	}
	
	public static boolean isActiveReflector(FissionReactor reactor, BlockPos pos) {
		return reactor.activeReflectorCache.contains(pos.toLong()) && RecipeHelper.blockRecipe(fission_reflector, reactor.WORLD, pos) != null;
	}
	
	public static boolean isFunctionalCell(FissionReactor reactor, BlockPos pos) {
		TileSolidFissionCell cell = reactor.getPartMap(TileSolidFissionCell.class).get(pos.toLong());
		return cell == null ? false : cell.isFunctional();
	}
	
	public static boolean isValidSink(FissionReactor reactor, BlockPos pos, String sinkName) {
		TileSolidFissionSink sink = reactor.getPartMap(TileSolidFissionSink.class).get(pos.toLong());
		return sink == null ? false : sink.isFunctional() && (sinkName.equals("any") || sink.sinkName.equals(sinkName));
	}
	
	public static boolean isFunctionalVessel(FissionReactor reactor, BlockPos pos) {
		TileSaltFissionVessel vessel = reactor.getPartMap(TileSaltFissionVessel.class).get(pos.toLong());
		return vessel == null ? false : vessel.isFunctional();
	}
	
	public static boolean isValidHeater(FissionReactor reactor, BlockPos pos, String heaterName) {
		TileSaltFissionHeater heater = reactor.getPartMap(TileSaltFissionHeater.class).get(pos.toLong());
		return heater == null ? false : heater.isFunctional() && (heaterName.equals("any") || heater.heaterName.equals(heaterName));
	}
	
	// Default Tooltip Builder
	
	public static class DefaultTooltipBuilder extends PlacementRule.DefaultTooltipBuilder<IFissionPart> {}
	
	// Tooltip Recipes
	
	public static class TooltipRecipeHandler extends PlacementRule.TooltipRecipeHandler {
		
		public TooltipRecipeHandler() {
			super("fission");
		}
	}
}
