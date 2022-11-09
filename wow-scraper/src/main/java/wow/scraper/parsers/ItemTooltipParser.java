package wow.scraper.parsers;

import lombok.Getter;
import wow.commons.model.Money;
import wow.commons.model.Percent;
import wow.commons.model.categorization.*;
import wow.commons.model.item.ItemSetBonus;
import wow.commons.model.item.SocketType;
import wow.commons.model.item.WeaponStats;
import wow.commons.model.professions.Profession;
import wow.commons.model.professions.ProfessionSpecialization;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.unit.CharacterClass;
import wow.commons.model.unit.Race;
import wow.commons.repository.impl.parsers.gems.SocketBonusParser;
import wow.commons.repository.impl.parsers.stats.StatParser;
import wow.commons.repository.impl.parsers.stats.StatPatternRepository;
import wow.commons.util.ParserUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-10-29
 */
@Getter
public class ItemTooltipParser extends AbstractTooltipParser {
	private StatParser statParser;

	private Phase phase;
	private Integer requiredLevel;
	private Integer itemLevel;
	private Binding binding;
	private boolean unique;
	private ItemType itemType;
	private ItemSubType itemSubType;

	private List<SocketType> socketTypes;
	private String socketBonus;

	private List<CharacterClass> classRestriction;
	private List<Race> raceRestriction;
	private Side sideRestriction;

	private WeaponStats weaponStats;
	private List<String> statLines;

	private String itemSetName;
	private List<String> itemSetPieces;
	private List<ItemSetBonus> itemSetBonuses;
	private Profession itemSetRequiredProfession;
	private Integer itemSetRequiredProfessionLevel;

	private boolean randomEnchantment;

	private String requiredFactionName;
	private String requiredFactionStanding;

	private Profession requiredProfession;
	private Integer requiredProfessionLevel;
	private ProfessionSpecialization requiredProfessionSpec;

	private String droppedBy;
	private Percent dropChance;
	private Money sellPrice;

	public ItemTooltipParser(Integer itemId, String htmlTooltip) {
		super(itemId, htmlTooltip);
	}

	@Override
	protected Rule[] getRules() {
		return new Rule[] {
				Rule.prefix("Phase ", x -> this.phase = parsePhase(x)),
				Rule.prefix("Item Level ", x -> this.itemLevel = parseItemLevel(x)),
				Rule.prefix("Requires Level ", x ->	this.requiredLevel = parseRequiredLevel(x)),
				Rule.exact("Binds when picked up", () -> this.binding = Binding.BINDS_ON_PICK_UP),
				Rule.exact("Binds when equipped", () -> this.binding = Binding.BINDS_ON_EQUIP),
				Rule.exact("Binds when used", () -> this.binding = Binding.BINDS_ON_EQUIP),
				Rule.exact("Unique", () -> this.unique = true),
				Rule.exact("Unique-Equipped", () -> this.unique = true),
				Rule.tryParse(ItemType::tryParse, x -> this.itemType = x),
				Rule.tryParse(ItemSubType::tryParse, x -> this.itemSubType = x),
				Rule.exact("Red Socket", () -> socketTypes.add(SocketType.RED)),
				Rule.exact("Yellow Socket", () -> socketTypes.add(SocketType.YELLOW)),
				Rule.exact("Blue Socket", () -> socketTypes.add(SocketType.BLUE)),
				Rule.exact("Meta Socket", () -> socketTypes.add(SocketType.META)),
				Rule.prefix("Socket Bonus: ", this::parseSocketBonus),
				Rule.regex("Durability \\d+ / \\d+", x -> {}),
				Rule.prefix("Classes: ", x -> this.classRestriction = ParserUtil.getValues(x, CharacterClass::parse)),
				Rule.prefix("Races: ", x -> this.raceRestriction = ParserUtil.getValues(x, Race::parse)),
				Rule.exact("Requires any Alliance race", () -> this.sideRestriction = Side.ALLIANCE),
				Rule.exact("Requires any Horde race", () -> this.sideRestriction = Side.HORDE),
				Rule.regex("(.*) \\(\\d/(\\d)\\)", this::parseItemSet),
				Rule.regex("\\((\\d+)\\) Set ?: (.*)", this::parseItemSetBonus),
				Rule.exact("<Random enchantment>", () -> this.randomEnchantment = true),
				Rule.regex("Requires (.*?) - (Neutral|Friendly|Honored|Revered|Exalted)", this::parseReputation),
				Rule.regex("Requires (Alchemy|Engineering|Jewelcrafting|Tailoring) \\((\\d+)\\)", this::parseRequiredProfession),
				Rule.regex("Requires (Gnomish Engineer|Goblin Engineer|Master Swordsmith|Mooncloth Tailoring|Shadoweave Tailoring|Spellfire Tailoring)", this::parseRequiredProfessionSpec),
				Rule.prefix("Dropped by: ", x -> this.droppedBy = x),
				Rule.prefix("Drop Chance: ", x -> this.dropChance = parseDropChance(x)),
				Rule.prefix("Sell Price: ", x -> this.sellPrice = parseSellPrice(x)),
				Rule.regex("(\\d+) - (\\d+) (\\S+)? ?Damage", this::parseWeaponDamage),
				Rule.regex("\\((\\d+\\.\\d+) damage per second\\)", this::parseWeaponDps),
				Rule.regex("Speed (\\d+.\\d+)", this::parseWeaponSpeedParams),
				Rule.test(statParser::tryParse, x -> statLines.add(x)),
		};
	}

	@Override
	protected void beforeParse() {
		this.statParser = StatPatternRepository.getInstance().getItemStatParser();
		this.socketTypes = new ArrayList<>();
		this.statLines = new ArrayList<>();
	}

	@Override
	protected void afterParse() {
		if (itemType == ItemType.BACK && itemSubType == null) {
			this.itemSubType = ArmorSubType.CLOTH;
		}

		if (itemType != null) {
			return;
		}

		if (itemSubType == WeaponSubType.HELD_IN_OFF_HAND) {
			this.itemType = ItemType.OFF_HAND;
			return;
		}

		if (itemSubType == WeaponSubType.WAND) {
			this.itemType = ItemType.RANGED;
			return;
		}

		if (itemSubType == WeaponSubType.GUN || itemSubType == WeaponSubType.BOW || itemSubType == WeaponSubType.CROSSBOW || itemSubType == WeaponSubType.THROWN) {
			this.itemType = ItemType.RANGED;
			return;
		}

		throw new IllegalArgumentException("Can't determine item type for: " + name);
	}

	private int parseRequiredLevel(String value) {
		return Integer.parseInt(value);
	}

	private void parseSocketBonus(String value) {
		if (SocketBonusParser.tryParseSocketBonus(value) != null) {
			this.socketBonus = value;
			return;
		}
		throw new IllegalArgumentException("Invalid socket bonus: " + value);
	}

	private void parseItemSet(Object[] parsedValues) {
		this.itemSetName = (String) parsedValues[0];
		int numPieces = (int) parsedValues[1];

		Object[] setRestriction = ParserUtil.parseMultipleValues("Requires (Tailoring) \\((\\d+)\\)", lines.get(currentLineIdx + 1));
		if (setRestriction != null) {
			this.itemSetRequiredProfession = Profession.parse((String)setRestriction[0]);
			this.itemSetRequiredProfessionLevel = (Integer)setRestriction[1];
			++currentLineIdx;
		}

		this.itemSetPieces = new ArrayList<>();

		for (int i = 1; i <= numPieces; ++i) {
			itemSetPieces.add(lines.get(currentLineIdx + i));
		}

		currentLineIdx += numPieces;

		// the tooltip actually has wrong number of pieces!!!
		if (itemSetName.equals("Netherweave Vestments") || itemSetName.equals("Imbued Netherweave")) {
			itemSetPieces.add(lines.get(currentLineIdx + 1));
			++currentLineIdx;
		}
	}

	private void parseItemSetBonus(Object[] itemSetBonusParams) {
		if (itemSetBonuses == null) {
			this.itemSetBonuses = new ArrayList<>();
		}
		int numPieces = (int)itemSetBonusParams[0];
		String description = (String)itemSetBonusParams[1];
		StatParser setBonusParser = StatPatternRepository.getInstance().getItemStatParser();
		if (!setBonusParser.tryParse(description)) {
			unmatchedLine(description);
		}
		itemSetBonuses.add(new ItemSetBonus(numPieces, description));
	}

	private void parseReputation(Object[] factionParams) {
		this.requiredFactionName = (String)factionParams[0];
		this.requiredFactionStanding = (String)factionParams[1];
	}

	private void parseRequiredProfession(Object[] params) {
		this.requiredProfession = Profession.parse((String)params[0]);
		this.requiredProfessionLevel = (Integer)params[1];
	}

	private void parseRequiredProfessionSpec(Object[] params) {
		this.requiredProfessionSpec = ProfessionSpecialization.parse((String)params[0]);
	}

	private Percent parseDropChance(String value) {
		return Percent.of(Double.parseDouble(value.replace("%", "")));
	}

	private void parseWeaponDamage(Object[] weaponDamageParams) {
		if (weaponStats == null) {
			this.weaponStats = new WeaponStats();
		}
		String damageType;
		if (weaponDamageParams.length == 3) {
			weaponStats.setWeaponDamageMin((int)weaponDamageParams[0]);
			weaponStats.setWeaponDamageMax((int)weaponDamageParams[1]);
			damageType = (String)weaponDamageParams[2];
		} else {
			weaponStats.setWeaponDamageMin((int)weaponDamageParams[0]);
			weaponStats.setWeaponDamageMax((int)weaponDamageParams[0]);
			damageType = (String)weaponDamageParams[1];
		}
		if (damageType != null) {
			weaponStats.setDamageType(SpellSchool.parse(damageType));
		}
	}

	private void parseWeaponDps(Object[] weaponDpsParams) {
		if (weaponStats == null) {
			this.weaponStats = new WeaponStats();
		}
		weaponStats.setWeaponDps(Double.parseDouble((String)weaponDpsParams[0]));
	}

	private void parseWeaponSpeedParams(Object[] weaponSpeedParams) {
		if (weaponStats == null) {
			this.weaponStats = new WeaponStats();
		}
		weaponStats.setWeaponSpeed(Double.parseDouble((String)weaponSpeedParams[0]));
	}
}
