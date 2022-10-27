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
	protected void parseLine(String currentLine) {
		if (currentLine.startsWith("Phase ")) {
			this.phase = parsePhase(currentLine);
			return;
		}

		if (currentLine.startsWith("Item Level ")) {
			this.itemLevel = parseItemLevel(currentLine);
			return;
		}

		if (currentLine.startsWith("Requires Level ")) {
			this.requiredLevel = parseRequiredLevel(currentLine);
			return;
		}

		if (currentLine.equals("Binds when picked up")) {
			this.binding = Binding.BindsOnPickUp;
			return;
		}

		if (currentLine.equals("Binds when equipped")) {
			this.binding = Binding.BindsOnEquip;
			return;
		}

		if (currentLine.equals("Binds when used")) {
			this.binding = Binding.BindsOnEquip;
			return;
		}

		if (currentLine.equals("Unique") || currentLine.equals("Unique-Equipped")) {
			this.unique = true;
			return;
		}

		if (itemType == null && (this.itemType = ItemType.tryParse(currentLine)) != null) {
			return;
		}

		if (itemSubType == null && (this.itemSubType = ItemSubType.tryParse(currentLine)) != null) {
			return;
		}

		if (currentLine.equals("Red Socket")) {
			socketTypes.add(SocketType.Red);
			return;
		}

		if (currentLine.equals("Yellow Socket")) {
			socketTypes.add(SocketType.Yellow);
			return;
		}

		if (currentLine.equals("Blue Socket")) {
			socketTypes.add(SocketType.Blue);
			return;
		}

		if (currentLine.equals("Meta Socket")) {
			socketTypes.add(SocketType.Meta);
			return;
		}

		String socketBonusPrefix = "Socket Bonus: ";

		if (currentLine.startsWith(socketBonusPrefix)) {
			parseSocketBonus(currentLine, socketBonusPrefix);
			return;
		}

		if (currentLine.matches("Durability \\d+ / \\d+")) {
			return;
		}

		if (currentLine.startsWith("Classes: ")) {
			String sub = currentLine.substring("Classes: ".length());
			this.classRestriction = ParserUtil.getValues(sub, CharacterClass::parse);
			return;
		}

		if (currentLine.startsWith("Races: ")) {
			String sub = currentLine.substring("Races: ".length());
			this.raceRestriction = ParserUtil.getValues(sub, Race::parse);
			return;
		}

		if (currentLine.equals("Requires any Alliance race")) {
			this.sideRestriction = Side.Alliance;
			return;
		}

		if (currentLine.equals("Requires any Horde race")) {
			this.sideRestriction = Side.Horde;
			return;
		}

		Object[] itemSetParams = ParserUtil.parseMultipleValues("(.*) \\(\\d/(\\d)\\)", currentLine);
		if (itemSetParams != null) {
			parseItemSet(itemSetParams);
			return;
		}

		Object[] itemSetBonusParams = ParserUtil.parseMultipleValues("\\((\\d+)\\) Set ?: (.*)", currentLine);
		if (itemSetBonusParams != null) {
			parseItemSetBonus(itemSetBonusParams);
			return;
		}

		if (currentLine.equals("<Random enchantment>")) {
			this.randomEnchantment = true;
			return;
		}

		Object[] factionParams = ParserUtil.parseMultipleValues("Requires (.*?) - (Neutral|Friendly|Honored|Revered|Exalted)", currentLine);
		if (factionParams != null) {
			parseReputation(factionParams);
			return;
		}

		Object[] requiredProfessionParams = ParserUtil.parseMultipleValues("Requires (Alchemy|Engineering|Jewelcrafting|Tailoring) \\((\\d+)\\)", currentLine);
		if (requiredProfessionParams != null) {
			parseRequiredProfession(requiredProfessionParams);
			return;
		}

		Object[] requiredProfessionSpecParams = ParserUtil.parseMultipleValues("Requires (Gnomish Engineer|Goblin Engineer|Master Swordsmith|Mooncloth Tailoring|Shadoweave Tailoring|Spellfire Tailoring)", currentLine);
		if (requiredProfessionSpecParams != null) {
			parseRequiredProfessionSpec(requiredProfessionSpecParams);
			return;
		}

		if (currentLine.startsWith("Dropped by: ")) {
			this.droppedBy = parseDroppedBy(currentLine);
			return;
		}

		if (currentLine.startsWith("Drop Chance: ")) {
			this.dropChance = parseDropChance(currentLine);
			return;
		}

		if (currentLine.startsWith("Sell Price: ")) {
			this.sellPrice = parseSellPrice(currentLine);
			return;
		}

		Object[] weaponDamageParams = ParserUtil.parseMultipleValues("(\\d+) - (\\d+) (\\S+)? ?Damage", currentLine);
		if (weaponDamageParams != null) {
			parseWeaponDamage(weaponDamageParams);
			return;
		}

		Object[] weaponDpsParams = ParserUtil.parseMultipleValues("\\((\\d+\\.\\d+) damage per second\\)", currentLine);
		if (weaponDpsParams != null) {
			parseWeaponDps(weaponDpsParams);
			return;
		}

		Object[] weaponSpeedParams = ParserUtil.parseMultipleValues("Speed (\\d+.\\d+)", currentLine);
		if (weaponSpeedParams != null) {
			parseWeaponSpeedParams(weaponSpeedParams);
			return;
		}

		if (statParser.tryParse(currentLine)) {
			statLines.add(currentLine);
			return;
		}

		unmatchedLine(currentLine);
	}

	@Override
	protected void beforeParse() {
		this.statParser = StatPatternRepository.getInstance().getItemStatParser();
		this.socketTypes = new ArrayList<>();
		this.statLines = new ArrayList<>();
	}

	@Override
	protected void afterParse() {
		if (itemType == ItemType.Back && itemSubType == null) {
			this.itemSubType = ArmorSubType.Cloth;
		}

		if (itemType != null) {
			return;
		}

		if (itemSubType == WeaponSubType.HeldInOffHand) {
			this.itemType = ItemType.OffHand;
			return;
		}

		if (itemSubType == WeaponSubType.Wand) {
			this.itemType = ItemType.Ranged;
			return;
		}

		if (itemSubType == WeaponSubType.Gun || itemSubType == WeaponSubType.Bow || itemSubType == WeaponSubType.Crossbow || itemSubType == WeaponSubType.Thrown) {
			this.itemType = ItemType.Ranged;
			return;
		}

		throw new IllegalArgumentException("Can't determine item type for: " + name);
	}

	private int parseRequiredLevel(String line) {
		String sub = line.substring("Requires Level ".length());
		return Integer.parseInt(sub);
	}

	private void parseSocketBonus(String currentLine, String socketBonusPrefix) {
		String sub = currentLine.substring(socketBonusPrefix.length());
		if (SocketBonusParser.tryParseSocketBonus(sub) != null) {
			this.socketBonus = sub;
			return;
		}
		throw new IllegalArgumentException("Invalid socket bonus: " + currentLine);
	}

	private void parseItemSet(Object[] parsedValues) {
		this.itemSetName = (String) parsedValues[0];
		int numPieces = (int) parsedValues[1];

		Object[] setRestriction = ParserUtil.parseMultipleValues("Requires (Tailoring) \\((\\d+)\\)", lines.get(currentLineIdx + 1));
		if (setRestriction != null) {
			this.itemSetRequiredProfession = Profession.tryParse((String)setRestriction[0]);
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
		StatParser statParser = StatPatternRepository.getInstance().getItemStatParser();
		if (!statParser.tryParse(description)) {
			unmatchedLine(description);
		}
		itemSetBonuses.add(new ItemSetBonus(numPieces, description));
	}

	private void parseReputation(Object[] factionParams) {
		this.requiredFactionName = (String)factionParams[0];
		this.requiredFactionStanding = (String)factionParams[1];
	}

	private void parseRequiredProfession(Object[] params) {
		this.requiredProfession = Profession.tryParse((String)params[0]);
		this.requiredProfessionLevel = (Integer)params[1];
	}

	private void parseRequiredProfessionSpec(Object[] params) {
		this.requiredProfessionSpec = ProfessionSpecialization.valueOf(((String)params[0]).replaceAll("\\s", ""));
	}

	private String parseDroppedBy(String line) {
		return line.substring("Dropped by: ".length());
	}

	private Percent parseDropChance(String line) {
		String sub = line.substring("Drop Chance: ".length());
		return Percent.of(Double.parseDouble(sub.replace("%", "")));
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
