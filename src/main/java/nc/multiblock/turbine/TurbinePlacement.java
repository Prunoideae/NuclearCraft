package nc.multiblock.turbine;

import java.util.*;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.objects.*;
import nc.config.NCConfig;
import nc.init.NCBlocks;
import nc.multiblock.PlacementRule;
import nc.multiblock.PlacementRule.*;
import nc.multiblock.turbine.tile.*;
import nc.util.StringHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public abstract class TurbinePlacement {
	
	/** List of all defined rule parsers. Earlier entries are prioritised! */
	public static final List<PlacementRule.RuleParser<ITurbinePart>> RULE_PARSER_LIST = new LinkedList<>();
	
	/** Map of all defined placement rules. */
	public static final Object2ObjectMap<String, PlacementRule<ITurbinePart>> RULE_MAP = new PlacementMap<>();
	
	/** List of all defined tooltip builders. Earlier entries are prioritised! */
	public static final List<PlacementRule.TooltipBuilder<ITurbinePart>> TOOLTIP_BUILDER_LIST = new LinkedList<>();
	
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
		
		addRule("magnesium_coil", NCConfig.turbine_coil_rule[0], new ItemStack(NCBlocks.turbine_dynamo_coil, 1, 0));
		addRule("beryllium_coil", NCConfig.turbine_coil_rule[1], new ItemStack(NCBlocks.turbine_dynamo_coil, 1, 1));
		addRule("aluminum_coil", NCConfig.turbine_coil_rule[2], new ItemStack(NCBlocks.turbine_dynamo_coil, 1, 2));
		addRule("gold_coil", NCConfig.turbine_coil_rule[3], new ItemStack(NCBlocks.turbine_dynamo_coil, 1, 3));
		addRule("copper_coil", NCConfig.turbine_coil_rule[4], new ItemStack(NCBlocks.turbine_dynamo_coil, 1, 4));
		addRule("silver_coil", NCConfig.turbine_coil_rule[5], new ItemStack(NCBlocks.turbine_dynamo_coil, 1, 5));
		
		addRule("connector", NCConfig.turbine_connector_rule[0], new ItemStack(NCBlocks.turbine_coil_connector));
	}
	
	public static void addRule(String id, String rule, Object... blocks) {
		RULE_MAP.put(id, parse(rule));
		for (Object block : blocks) {
			tooltip_recipe_handler.addRecipe(block, id);
		}
	}
	
	public static void postInit() {
		for (Object2ObjectMap.Entry<String, PlacementRule<ITurbinePart>> entry : RULE_MAP.object2ObjectEntrySet()) {
			for (PlacementRule.TooltipBuilder<ITurbinePart> builder : TOOLTIP_BUILDER_LIST) {
				String tooltip = builder.buildTooltip(entry.getValue());
				if (tooltip != null) TOOLTIP_MAP.put(entry.getKey(), tooltip);
			}
		}
	}
	
	// Default Rule Parser
	
	public static PlacementRule<ITurbinePart> parse(String string) {
		return PlacementRule.parse(string, RULE_PARSER_LIST);
	}
	
	/** Rule parser for all rule types available in base NC. */
	public static class DefaultRuleParser extends PlacementRule.DefaultRuleParser<ITurbinePart> {
		
		@Override
		protected @Nullable PlacementRule<ITurbinePart> partialParse(String s) {
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
					if (split[i].contains("bearing")) {
						rule = "bearing";
					}
					else if (split[i].contains("connector")) {
						rule = "connector";
					}
					else if (split[i].contains("coil")) {
						rule = "coil";
						if (i > 0) type = split[i - 1];
						else return null;
					}
				}
			}
			
			if (amount < 0 || rule == null) return null;
			
			CountType countType = exact ? CountType.EXACTLY : (atMost ? CountType.AT_MOST : CountType.AT_LEAST);
			AdjacencyType adjType = axial ? AdjacencyType.AXIAL : (vertex ? AdjacencyType.VERTEX : AdjacencyType.STANDARD);
			
			if (rule.equals("bearing")) {
				return new AdjacentBearing(amount, countType, adjType);
			}
			else if (rule.equals("connector")) {
				return new AdjacentConnector(amount, countType, adjType);
			}
			else if (rule.equals("coil")) {
				return new AdjacentCoil(amount, countType, adjType, type);
			}
			
			return null;
		}
	}
	
	// Adjacent
	
	public static abstract class Adjacent extends PlacementRule.Adjacent<ITurbinePart> {
		
		public Adjacent(String dependency, int amount, CountType countType, AdjacencyType adjType) {
			super(dependency, amount, countType, adjType);
		}
		
		@Override
		public void checkIsRuleAllowed(String ruleID) {
			if (amount > 4) {
				throw new IllegalArgumentException("Adjacency placement rule with ID \"" + ruleID + "\" can not require more than four adjacencies!");
			}
			if (adjType == AdjacencyType.VERTEX) {
				throw new IllegalArgumentException("Vertex placement rule with ID \"" + ruleID + "\" is disallowed as turbine dynamos have no depth!");
			}
			super.checkIsRuleAllowed(ruleID);
		}
	}
	
	public static class AdjacentBearing extends Adjacent {
		
		public AdjacentBearing(int amount, CountType countType, AdjacencyType adjType) {
			super("bearing", amount, countType, adjType);
		}
		
		@Override
		public void checkIsRuleAllowed(String ruleID) {
			if (amount > 1) {
				throw new IllegalArgumentException("Adjacency placement rule with ID \"" + ruleID + "\" can not require more than one rotor bearing!");
			}
			super.checkIsRuleAllowed(ruleID);
		}
		
		@Override
		public boolean satisfied(ITurbinePart part, EnumFacing dir) {
			return isRotorBearing(part.getMultiblock(), part.getTilePos().offset(dir));
		}
	}
	
	public static class AdjacentConnector extends Adjacent {
		
		public AdjacentConnector(int amount, CountType countType, AdjacencyType adjType) {
			super("connector", amount, countType, adjType);
		}
		
		@Override
		public boolean satisfied(ITurbinePart part, EnumFacing dir) {
			return isCoilConnector(part.getMultiblock(), part.getTilePos().offset(dir));
		}
	}
	
	public static class AdjacentCoil extends Adjacent {
		
		protected final String coilType;
		
		public AdjacentCoil(int amount, CountType countType, AdjacencyType adjType, String coilType) {
			super(coilType + "_coil", amount, countType, adjType);
			this.coilType = coilType;
		}
		
		@Override
		public void checkIsRuleAllowed(String ruleID) {
			super.checkIsRuleAllowed(ruleID);
			if (countType != CountType.AT_LEAST && coilType.equals("any")) {
				throw new IllegalArgumentException((countType == CountType.EXACTLY ? "Exact 'any coil'" : "'At most n of any coil'") + " placement rule with ID \"" + ruleID + "\" is disallowed due to potential ambiguity during rule checks!");
			}
		}
		
		@Override
		public boolean satisfied(ITurbinePart part, EnumFacing dir) {
			return isDynamoCoil(part.getMultiblock(), part.getTilePos().offset(dir), coilType);
		}
	}
	
	// Helper Methods
	
	public static boolean isRotorBearing(Turbine turbine, BlockPos pos) {
		return turbine.getPartMap(TileTurbineRotorBearing.class).get(pos.toLong()) != null;
	}
	
	public static boolean isCoilConnector(Turbine turbine, BlockPos pos) {
		TileTurbineDynamoPart part = turbine.getPartMap(TileTurbineDynamoPart.class).get(pos.toLong());
		return part instanceof TileTurbineCoilConnector && part.isInValidPosition;
	}
	
	public static boolean isDynamoCoil(Turbine turbine, BlockPos pos, String coilName) {
		TileTurbineDynamoPart part = turbine.getPartMap(TileTurbineDynamoPart.class).get(pos.toLong());
		return part instanceof TileTurbineDynamoCoil && part.isInValidPosition && (coilName.equals("any") || part.partName.equals(coilName));
	}
	
	// Default Tooltip Builder
	
	public static class DefaultTooltipBuilder extends PlacementRule.DefaultTooltipBuilder<ITurbinePart> {}
	
	// Tooltip Recipes
	
	public static class TooltipRecipeHandler extends PlacementRule.TooltipRecipeHandler {
		
		public TooltipRecipeHandler() {
			super("turbine");
		}
	}
}
