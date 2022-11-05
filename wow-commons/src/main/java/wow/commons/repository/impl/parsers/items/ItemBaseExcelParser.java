package wow.commons.repository.impl.parsers.items;

import wow.commons.model.Money;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.*;
import wow.commons.model.item.*;
import wow.commons.model.professions.Profession;
import wow.commons.model.professions.ProfessionSpecialization;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;
import wow.commons.model.sources.SourceParser;
import wow.commons.model.unit.CharacterClass;
import wow.commons.model.unit.Race;
import wow.commons.repository.PVERepository;
import wow.commons.repository.impl.ItemDataRepositoryImpl;
import wow.commons.repository.impl.parsers.gems.GemStatsParser;
import wow.commons.repository.impl.parsers.gems.SocketBonusParser;
import wow.commons.repository.impl.parsers.stats.StatParser;
import wow.commons.repository.impl.parsers.stats.StatPatternRepository;
import wow.commons.util.ExcelParser;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
public class ItemBaseExcelParser extends ExcelParser {
	private final ItemDataRepositoryImpl itemDataRepository;
	private final PVERepository pveRepository;

	public ItemBaseExcelParser(ItemDataRepositoryImpl itemDataRepository, PVERepository pveRepository) {
		this.itemDataRepository = itemDataRepository;
		this.pveRepository = pveRepository;
	}

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath("/xls/item_base.xls");
	}

	@Override
	protected Stream<SheetReader> getSheetReaders() {
		return Stream.of(
				new SheetReader("item", this::readItems, COL_ITEM_NAME),
				new SheetReader("set", this::readItemSets, COL_ITEM_SET_NAME),
				new SheetReader("gem", this::readGems, COL_GEM_NAME)
		);
	}

	private final ExcelColumn COL_ITEM_ID = column("id");
	private final ExcelColumn COL_ITEM_NAME = column("name");
	private final ExcelColumn COL_ITEM_RARITY = column("rarity");
	private final ExcelColumn COL_ITEM_ITEM_LEVEL = column("item_level");
	private final ExcelColumn COL_ITEM_REQ_LEVEL = column("req_level");
	private final ExcelColumn COL_ITEM_BINDING = column("binding");
	private final ExcelColumn COL_ITEM_UNIQUE = column("unique");
	private final ExcelColumn COL_ITEM_ITEM_TYPE = column("item_type");
	private final ExcelColumn COL_ITEM_ITEM_SUBTYPE = column("item_subtype");
	private final ExcelColumn COL_ITEM_PHASE = column("phase");
	private final ExcelColumn COL_ITEM_SOURCE = column("source");
	private final ExcelColumn COL_ITEM_SOCKET_TYPES = column("socket_types");
	private final ExcelColumn COL_ITEM_SOCKET_BONUS = column("socket_bonus");
	private final ExcelColumn COL_ITEM_CLASS_RESTRICTION = column("class_restriction");
	private final ExcelColumn COL_ITEM_RACE_RESTRICTION = column("race_restriction");
	private final ExcelColumn COL_ITEM_SIDE_RESTRICTION = column("side_restriction");
	private final ExcelColumn COL_ITEM_ITEM_SET = column("item_set");
	private final ExcelColumn COL_ITEM_REQ_PROFESSION = column("req_profession");
	private final ExcelColumn COL_ITEM_REQ_PROFESSION_LEVEL = column("req_profession_level");
	private final ExcelColumn COL_ITEM_REQ_PROFESSION_SPEC = column("req_profession_spec");
	private final ExcelColumn COL_ITEM_DROPPED_BY = column("dropped_by");
	private final ExcelColumn COL_ITEM_DROP_CHANCE = column("drop_chance");
	private final ExcelColumn COL_ITEM_SELL_PRICE = column("sell_price");
	private final ExcelColumn COL_ITEM_STAT = column("stat");
	private final ExcelColumn COL_ITEM_ICON = column("icon");
	private final ExcelColumn COL_ITEM_TOOLTIP = column("tooltip");

	private static final int MAX_ITEM_STATS = 10;

	private void readItems() {
		Item item = getItem();
		itemDataRepository.addItem(item);
	}

	private Item getItem() {
		var id = COL_ITEM_ID.getInteger();
		var name = COL_ITEM_NAME.getString();
		var rarity = COL_ITEM_RARITY.getEnum(ItemRarity::valueOf);
		var itemLevel = COL_ITEM_ITEM_LEVEL.getInteger(0);
		var requiredLevel = COL_ITEM_REQ_LEVEL.getInteger(0);
		var binding = COL_ITEM_BINDING.getEnum(Binding::valueOf, null);
		var unique = COL_ITEM_UNIQUE.getBoolean();
		var itemType = COL_ITEM_ITEM_TYPE.getEnum(ItemType::valueOf);
		var itemSubType = COL_ITEM_ITEM_SUBTYPE.getEnum(ItemSubType::tryParse, null);
		var phase = COL_ITEM_PHASE.getEnum(Phase::valueOf, Phase.TBC_P1);
		var source = COL_ITEM_SOURCE.getString();
		var socketTypes = COL_ITEM_SOCKET_TYPES.getList(SocketType::valueOf);
		var socketBonus = COL_ITEM_SOCKET_BONUS.getString(null);
		var classRestriction = COL_ITEM_CLASS_RESTRICTION.getList(CharacterClass::valueOf);
		var raceRestriction = COL_ITEM_RACE_RESTRICTION.getList(Race::valueOf);
		var sideRestriction = COL_ITEM_SIDE_RESTRICTION.getEnum(Side::valueOf, null);
		var itemSetName = COL_ITEM_ITEM_SET.getString(null);
		var requiredProfession = COL_ITEM_REQ_PROFESSION.getEnum(Profession::valueOf, null);
		var requiredProfessionLevel = COL_ITEM_REQ_PROFESSION_LEVEL.getInteger(0);
		var requiredProfessionSpec = COL_ITEM_REQ_PROFESSION_SPEC.getEnum(ProfessionSpecialization::valueOf, null);
		var droppedBy = COL_ITEM_DROPPED_BY.getString(null);
		var dropChance = COL_ITEM_DROP_CHANCE.getPercent(null);
		var sellPrice = Money.parse(COL_ITEM_SELL_PRICE.getString(null));
		var icon = COL_ITEM_ICON.getString();
		var tooltip = COL_ITEM_TOOLTIP.getString();
		var stats = getStats();

		var itemSource = SourceParser.parse(source, pveRepository);
		var item = new Item(id, name, rarity, itemType, itemSubType, Set.of(itemSource), getSocketSpecification(socketTypes, socketBonus), stats, null);

		item.setItemLevel(itemLevel);
		item.getRestriction().setRequiredLevel(requiredLevel);
		item.setBinding(binding);
		item.setUnique(unique);
		item.getRestriction().setPhase(phase);
		item.getRestriction().setClassRestriction(classRestriction);
		item.getRestriction().setRaceRestriction(raceRestriction);
		item.getRestriction().setSideRestriction(sideRestriction);
		item.getRestriction().setRequiredProfession(requiredProfession);
		item.getRestriction().setRequiredProfessionLevel(requiredProfessionLevel);
		item.getRestriction().setRequiredProfessionSpec(requiredProfessionSpec);
		item.setSellPrice(sellPrice);
		item.setIcon(icon);
		item.setTooltip(tooltip);

		validateItem(item);

		if (itemSetName != null) {
			setPiecesByName.computeIfAbsent(itemSetName, x -> new ArrayList<>()).add(item);
		}

		return item;
	}

	private Attributes getStats() {
		StatParser parser = StatPatternRepository.getInstance().getItemStatParser();

		for (int i = 1; i <= MAX_ITEM_STATS; ++i) {
			String line = COL_ITEM_STAT.multi(i).getString(null);
			if (line != null && !parser.tryParse(line)) {
				throw new IllegalArgumentException("Can't parse: " + line);
			}
		}

		return parser.getParsedStats();
	}

	private static ItemSocketSpecification getSocketSpecification(List<SocketType> socketTypes, String socketBonus) {
		if (socketTypes.isEmpty()) {
			if (socketBonus != null) {
				throw new IllegalArgumentException();
			}
			return ItemSocketSpecification.EMPTY;
		}

		if (socketBonus == null) {
			throw new IllegalArgumentException();
		}

		Attributes socketBonusAttributes = SocketBonusParser.tryParseSocketBonus(socketBonus);

		if (socketBonusAttributes == null) {
			throw new IllegalArgumentException("Invalid socket bonus: " + socketBonus);
		}

		return new ItemSocketSpecification(socketTypes, socketBonusAttributes);
	}

	private static void validateItem(Item item) {
		ItemType itemType = item.getItemType();
		ItemSubType itemSubType = item.getItemSubType();

		if (itemType == null) {
			return;// checked after all items are read
		}
		if (itemType.getCategory() == ItemCategory.Armor && itemSubType == null) {
			throw new IllegalArgumentException(item.getName());
		}
		if (itemType.getCategory() == ItemCategory.Weapon && itemSubType == null) {
			throw new IllegalArgumentException(item.getName());
		}
		if (itemType.getCategory() == ItemCategory.Projectile && itemSubType == null) {
			throw new IllegalArgumentException(item.getName());
		}
		if ((itemType.getCategory() != ItemCategory.Armor && itemType.getCategory() != ItemCategory.Weapon && itemType.getCategory() != ItemCategory.Projectile) && itemSubType != null) {
			throw new IllegalArgumentException(item.getName());
		}
	}

	private final Map<String, List<Item>> setPiecesByName = new HashMap<>();

	private final ExcelColumn COL_ITEM_SET_NAME = column("name");
	private final ExcelColumn COL_ITEM_SET_REQ_PROF = column("req_prof");
	private final ExcelColumn COL_ITEM_SET_REQ_PROF_LVL = column("req_prof_lvl");

	private static final int MAX_ITEM_SET_BONUSES = 6;

	private void readItemSets() {
		ItemSet itemSet = getItemSet();
		itemDataRepository.addItemSet(itemSet);
	}

	private ItemSet getItemSet() {
		var name = COL_ITEM_SET_NAME.getString();
		var itemSetBonuses = getItemSetBonuses();
		var requiredProfession = COL_ITEM_SET_REQ_PROF.getEnum(Profession::valueOf, null);//TODO
		var requiredProfessionLevel = COL_ITEM_SET_REQ_PROF_LVL.getInteger(0);

		var itemSet = new ItemSet(name, null, itemSetBonuses, setPiecesByName.getOrDefault(name, List.of()));

		// TODO brak ograniczenia klas dla Tier
		itemSet.getRestriction().setRequiredProfession(requiredProfession);
		itemSet.getRestriction().setRequiredLevel(requiredProfessionLevel);

		for (Item item : itemSet.getPieces()) {
			item.setItemSet(itemSet);
		}

		return itemSet;
	}

	private List<ItemSetBonus> getItemSetBonuses() {
		List<ItemSetBonus> itemSetBonuses = new ArrayList<>();

		for (int i = 1; i <= MAX_ITEM_SET_BONUSES; i++) {
			int numPieces = column("bonus" + i + "_p").getInteger(-1);
			String description = column("bonus" + i + "_d").getString(null);

			if (description != null) {
				ItemSetBonus itemSetBonus = getItemSetBonus(numPieces, description);
				itemSetBonuses.add(itemSetBonus);
			}
		}

		return itemSetBonuses;
	}

	private static ItemSetBonus getItemSetBonus(int numPieces, String description) {
		ItemSetBonus itemSetBonus = new ItemSetBonus(numPieces, description);
		StatParser statParser = StatPatternRepository.getInstance().getItemStatParser();
		if (statParser.tryParse(description)) {
			itemSetBonus.setBonusStats(statParser.getParsedStats());
			return itemSetBonus;
		}
		throw new IllegalArgumentException("Missing bonus: " + itemSetBonus.getDescription());
	}

	private final ExcelColumn COL_GEM_ID = column("id");
	private final ExcelColumn COL_GEM_NAME = column("name");
	private final ExcelColumn COL_GEM_RARITY = column("rarity");
	private final ExcelColumn COL_GEM_ITEM_LEVEL = column("item_level");
	private final ExcelColumn COL_GEM_PHASE = column("phase");
	private final ExcelColumn COL_GEM_SOURCE = column("source");
	private final ExcelColumn COL_GEM_COLOR = column("color");
	private final ExcelColumn COL_GEM_BINDING = column("binding");
	private final ExcelColumn COL_GEM_UNIQUE = column("unique");
	private final ExcelColumn COL_GEM_STATS = column("stats");
	private final ExcelColumn COL_GEM_META_ENABLERS = column("meta_enablers");
	private final ExcelColumn COL_GEM_SELL_PRICE = column("sell_price");
	private final ExcelColumn COL_GEM_ICON = column("icon");
	private final ExcelColumn COL_GEM_TOOLTIP = column("tooltip");

	private void readGems() {
		Gem gem = getGem();
		itemDataRepository.addGem(gem);
	}

	private Gem getGem() {
		var id = COL_GEM_ID.getInteger();
		var name = COL_GEM_NAME.getString();
		var rarity = COL_GEM_RARITY.getEnum(ItemRarity::valueOf);
		var itemLevel = COL_GEM_ITEM_LEVEL.getInteger();
		var phase = COL_GEM_PHASE.getEnum(Phase::valueOf, Phase.TBC_P0);
		var source = COL_GEM_SOURCE.getString();
		var color = COL_GEM_COLOR.getEnum(GemColor::valueOf);
		var binding = COL_GEM_BINDING.getEnum(Binding::valueOf, Binding.BindsOnEquip);
		var unique = COL_GEM_UNIQUE.getBoolean();
		var metaEnablers = COL_GEM_META_ENABLERS.getList(MetaEnabler::valueOf);
		var sellPrice = Money.parse(COL_GEM_SELL_PRICE.getString(null));
		var icon = COL_GEM_ICON.getString();
		var tooltip = COL_GEM_TOOLTIP.getString();
		var stats = GemStatsParser.tryParseStats(COL_GEM_STATS.getString());

		var gemSource = SourceParser.parse(source, pveRepository);
		var gem = new Gem(id, name, rarity, Set.of(gemSource), color, metaEnablers, stats);

		gem.getRestriction().setPhase(phase);
		gem.setItemLevel(itemLevel);
		gem.setBinding(binding);
		gem.setUnique(unique);
		gem.setSellPrice(sellPrice);
		gem.setIcon(icon);
		gem.setTooltip(tooltip);

		return gem;
	}
}
