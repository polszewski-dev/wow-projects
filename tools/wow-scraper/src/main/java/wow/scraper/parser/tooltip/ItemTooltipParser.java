package wow.scraper.parser.tooltip;

import lombok.Getter;
import wow.commons.model.Duration;
import wow.commons.model.categorization.ArmorSubType;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.categorization.WeaponSubType;
import wow.commons.model.effect.Effect;
import wow.commons.model.item.ItemSetBonus;
import wow.commons.model.item.SocketType;
import wow.commons.model.item.WeaponStats;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.spell.ActivatedAbility;
import wow.commons.model.spell.SpellSchool;
import wow.commons.util.parser.ParsedMultipleValues;
import wow.commons.util.parser.ParserUtil;
import wow.commons.util.parser.Rule;
import wow.scraper.config.ScraperContext;
import wow.scraper.model.JsonItemDetails;
import wow.scraper.model.RandomEnchant;
import wow.scraper.parser.effect.ItemStatParser;
import wow.scraper.parser.stat.StatParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * User: POlszewski
 * Date: 2022-10-29
 */
@Getter
public class ItemTooltipParser extends AbstractItemTooltipParser {
	private ItemStatParser itemStatParser;
	private ItemStatParser socketBonusParser;
	private List<SocketType> socketTypes;
	private ActivatedAbility activatedAbility;

	private WeaponStats weaponStats;

	private String itemSetName;
	private List<String> itemSetPieces;
	private List<ItemSetBonus> itemSetBonuses;
	private ProfessionId itemSetRequiredProfession;
	private Integer itemSetRequiredProfessionLevel;

	private List<RandomEnchant> randomEnchants;
	private Integer selectedRandomEnchantIdx;

	public ItemTooltipParser(JsonItemDetails itemDetails, GameVersionId gameVersion, ScraperContext scraperContext) {
		super(itemDetails, gameVersion, scraperContext);
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
				ruleRightClickToRead,
				Rule.exact("Quest Item", () -> {}),
				Rule.regex("(.*) \\(\\d/(\\d)\\)", this::parseItemSet),
				Rule.regex("\\((\\d+)\\) Set ?: (.*)", this::parseItemSetBonus),
				Rule.exact("<Random enchantment>", this::fetchRandomEnchants),
				Rule.regex("\\+?\\ ?(\\d+) - (\\d+) (\\S+)? ?Damage", this::parseWeaponDamage),
				Rule.regex("\\((\\d+\\.\\d+) damage per second\\)", this::parseWeaponDps),
				Rule.regex("Speed (\\d+.\\d+)", this::parseWeaponSpeedParams),
				Rule.regex("(\\d+) Charges?", x -> {}),
				Rule.test(itemStatParser::tryParseItemEffect, x -> {}),
				Rule.test(this::parseActivatedAbility, x -> {})
		};
	}

	@Override
	protected void beforeParse() {
		this.itemStatParser = newItemStatParser(gameVersion -> getStatPatternRepository().getItemStatParser(gameVersion));
		this.socketBonusParser = newItemStatParser(gameVersion -> getStatPatternRepository().getSocketBonusStatParser(gameVersion));
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
		if (!socketBonusParser.tryParseItemEffect(value)) {
			throw new IllegalArgumentException("Invalid socket bonus: " + value);
		}
	}

	private void parseItemSet(ParsedMultipleValues parsedValues) {
		this.itemSetName = parsedValues.get(0);
		int numPieces = parsedValues.getInteger(1);

		ParsedMultipleValues setRestriction = ParserUtil.parseMultipleValues("Requires (Tailoring) \\((\\d+)\\)", lines.get(currentLineIdx + 1));
		if (!setRestriction.isEmpty()) {
			this.itemSetRequiredProfession = ProfessionId.parse(setRestriction.get(0));
			this.itemSetRequiredProfessionLevel = setRestriction.getInteger(1);
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

	private void parseItemSetBonus(ParsedMultipleValues itemSetBonusParams) {
		if (itemSetBonuses == null) {
			this.itemSetBonuses = new ArrayList<>();
		}

		ItemSetBonus itemSetBonus = getItemSetBonus(itemSetBonusParams);

		if (itemSetBonus != null) {
			itemSetBonuses.add(itemSetBonus);
		}
	}

	private ItemSetBonus getItemSetBonus(ParsedMultipleValues itemSetBonusParams) {
		var numPieces = itemSetBonusParams.getInteger(0);
		var description = itemSetBonusParams.get(1);
		var bonusParser = newItemStatParser(gameVersion -> getStatPatternRepository().getItemStatParser(gameVersion));

		if (bonusParser.tryParseItemEffect(description)) {
			var bonusEffect = bonusParser.getUniqueItemEffect().orElseThrow();
			return new ItemSetBonus(numPieces, bonusEffect);
		}

		unmatchedLine(description);
		return null;
	}

	private boolean parseActivatedAbility(String line) {
		var spell = getItemSpellRepository().getActivatedAbility(gameVersion, line);
		if (spell.isEmpty()) {
			return false;
		}
		if (activatedAbility != null) {
			throw new IllegalArgumentException("Multiple activated abilities!!!");
		}
		this.activatedAbility = spell.get();
		return true;
	}

	private ItemStatParser newItemStatParser(Function<GameVersionId, StatParser> statParserFactory) {
		return new ItemStatParser(gameVersion, statParserFactory, getItemSpellRepository());
	}

	private void parseWeaponDamage(ParsedMultipleValues weaponDamageParams) {
		ensureWeaponStats();
		String damageType;
		if (weaponDamageParams.size() == 3) {
			weaponStats = weaponStats.withDamageMin(weaponDamageParams.getInteger(0));
			weaponStats = weaponStats.withDamageMax(weaponDamageParams.getInteger(1));
			damageType = weaponDamageParams.get(2);
		} else {
			weaponStats = weaponStats.withDamageMin(weaponDamageParams.getInteger(0));
			weaponStats = weaponStats.withDamageMax(weaponDamageParams.getInteger(0));
			damageType = weaponDamageParams.get(1);
		}
		if (damageType != null) {
			weaponStats = weaponStats.withDamageType(SpellSchool.parse(damageType));
		}
	}

	private void parseWeaponDps(ParsedMultipleValues weaponDpsParams) {
		ensureWeaponStats();
		weaponStats = weaponStats.withWeaponDps(Double.parseDouble(weaponDpsParams.get(0)));
	}

	private void parseWeaponSpeedParams(ParsedMultipleValues weaponSpeedParams) {
		ensureWeaponStats();
		double speed = Double.parseDouble(weaponSpeedParams.get(0));
		weaponStats = weaponStats.withWeaponSpeed(Duration.seconds(speed));
	}

	private void ensureWeaponStats() {
		if (weaponStats == null) {
			this.weaponStats = new WeaponStats(0, 0, null, 0, Duration.ZERO);
		}
	}

	private void fetchRandomEnchants() {
		var html = getWowheadFetcher().fetchRaw(gameVersion, "item=" + getItemId());
		var parser = new RandomEnchantParser(
				html,
				() -> newItemStatParser(getStatPatternRepository()::getItemStatParser)
		);

		parser.parse();
		this.randomEnchants = parser.getRandomEnchants();
	}

	@Override
	public int getItemId() {
		if (selectedRandomEnchantIdx != null) {
			return super.getItemId() + 1_000_000 * (selectedRandomEnchantIdx + 1);
		}

		return super.getItemId();
	}

	@Override
	public String getName() {
		if (selectedRandomEnchantIdx != null) {
			return super.getName() + " " + getSelectedRandomEnchant().suffix();
		}

		return super.getName();
	}

	public List<Effect> getEffects() {
		if (selectedRandomEnchantIdx != null) {
			var copy = new ArrayList<>(itemStatParser.getItemEffects());
			copy.addAll(getSelectedRandomEnchant().effects());
			return copy;
		}

		return itemStatParser.getItemEffects();
	}

	public Optional<Effect> getSocketBonus() {
		return socketBonusParser.getUniqueItemEffect();
	}

	public Optional<ActivatedAbility> getActivatedAbility() {
		return Optional.ofNullable(activatedAbility);
	}

	public boolean hasRandomEnchant() {
		return randomEnchants != null;
	}

	public int getRandomEnchantCount() {
		return randomEnchants.size();
	}

	public void setRandomEnchantIdx(int index) {
		this.selectedRandomEnchantIdx = index;
	}

	private RandomEnchant getSelectedRandomEnchant() {
		return randomEnchants.get(selectedRandomEnchantIdx);
	}
}
