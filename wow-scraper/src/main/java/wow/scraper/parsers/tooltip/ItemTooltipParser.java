package wow.scraper.parsers.tooltip;

import lombok.Getter;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ArmorSubType;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.categorization.WeaponSubType;
import wow.commons.model.item.ItemSetBonus;
import wow.commons.model.item.SocketType;
import wow.commons.model.item.WeaponStats;
import wow.commons.model.professions.Profession;
import wow.commons.model.pve.GameVersion;
import wow.commons.model.spells.SpellSchool;
import wow.commons.util.parser.ParserUtil;
import wow.commons.util.parser.Rule;
import wow.scraper.model.JsonItemDetailsAndTooltip;
import wow.scraper.parsers.gems.SocketBonusParser;
import wow.scraper.parsers.stats.StatParser;
import wow.scraper.parsers.stats.StatPatternRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-10-29
 */
@Getter
public class ItemTooltipParser extends AbstractTooltipParser {
	private StatParser statParser;

	private List<SocketType> socketTypes;
	private Attributes socketBonus;

	private WeaponStats weaponStats;

	private String itemSetName;
	private List<String> itemSetPieces;
	private List<ItemSetBonus> itemSetBonuses;
	private Profession itemSetRequiredProfession;
	private Integer itemSetRequiredProfessionLevel;

	private boolean randomEnchantment;

	public ItemTooltipParser(JsonItemDetailsAndTooltip itemDetailsAndTooltip, GameVersion gameVersion, StatPatternRepository statPatternRepository) {
		super(itemDetailsAndTooltip, gameVersion, statPatternRepository);
	}

	@Override
	protected Rule[] getRules() {
		return new Rule[] {
				rulePhase,
				ruleItemLevel,
				ruleRequiresLevel,
				ruleBindsWhenPickedUp,
				ruleBindsWhenEquipped,
				ruleBindsWhenUsed,
				ruleUnique,
				ruleUniqueEquipped,
				Rule.tryParse(ItemType::tryParse, x -> this.itemType = x),
				Rule.tryParse(ItemSubType::tryParse, x -> this.itemSubType = x),
				Rule.exact("Red Socket", () -> socketTypes.add(SocketType.RED)),
				Rule.exact("Yellow Socket", () -> socketTypes.add(SocketType.YELLOW)),
				Rule.exact("Blue Socket", () -> socketTypes.add(SocketType.BLUE)),
				Rule.exact("Meta Socket", () -> socketTypes.add(SocketType.META)),
				Rule.prefix("Socket Bonus: ", this::parseSocketBonus),
				ruleDurability,
				ruleClassRestriction,
				ruleRaceRestriction,
				ruleAllianceRestriction,
				ruleHordeRestriction,
				ruleFactionRestriction,
				ruleProfessionRestriction,
				ruleProfessionSpecializationRestriction,
				ruleDroppedBy,
				ruleDropChance,
				ruleSellPrice,
				ruleQuote,
				Rule.regex("(.*) \\(\\d/(\\d)\\)", this::parseItemSet),
				Rule.regex("\\((\\d+)\\) Set ?: (.*)", this::parseItemSetBonus),
				Rule.exact("<Random enchantment>", () -> this.randomEnchantment = true),
				Rule.regex("(\\d+) - (\\d+) (\\S+)? ?Damage", this::parseWeaponDamage),
				Rule.regex("\\((\\d+\\.\\d+) damage per second\\)", this::parseWeaponDps),
				Rule.regex("Speed (\\d+.\\d+)", this::parseWeaponSpeedParams),
				Rule.test(line -> statParser.tryParse(line), x -> {}),
		};
	}

	@Override
	protected void beforeParse() {
		this.statParser = statPatternRepository.getItemStatParser();
		this.socketTypes = new ArrayList<>();
	}

	@Override
	protected void afterParse() {
		if (itemLevel == null) {
			itemLevel = 1;
		}
		fixItemType();
	}

	private void fixItemType() {
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

	private void parseSocketBonus(String value) {
		this.socketBonus = new SocketBonusParser(statPatternRepository).tryParseSocketBonus(value);
		if (socketBonus == null) {
			throw new IllegalArgumentException("Invalid socket bonus: " + value);
		}
	}

	private void parseItemSet(Object[] parsedValues) {
		this.itemSetName = (String) parsedValues[0];
		int numPieces = (int) parsedValues[1];

		Object[] setRestriction = ParserUtil.parseMultipleValues("Requires (Tailoring) \\((\\d+)\\)", lines.get(currentLineIdx + 1));
		if (setRestriction.length > 0) {
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
		StatParser setBonusParser = statPatternRepository.getItemStatParser();
		if (!setBonusParser.tryParse(description)) {
			unmatchedLine(description);
		}
		itemSetBonuses.add(new ItemSetBonus(numPieces, description, setBonusParser.getParsedStats()));
	}

	private void parseWeaponDamage(Object[] weaponDamageParams) {
		ensureWeaponStats();
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
		ensureWeaponStats();
		weaponStats.setWeaponDps(Double.parseDouble((String)weaponDpsParams[0]));
	}

	private void parseWeaponSpeedParams(Object[] weaponSpeedParams) {
		ensureWeaponStats();
		weaponStats.setWeaponSpeed(Double.parseDouble((String)weaponSpeedParams[0]));
	}

	private void ensureWeaponStats() {
		if (weaponStats == null) {
			this.weaponStats = new WeaponStats();
		}
	}
}