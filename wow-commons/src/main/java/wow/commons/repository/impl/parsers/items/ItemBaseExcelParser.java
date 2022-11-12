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
				new SheetReader("item", this::readItems, colItemName),
				new SheetReader("set", this::readItemSets, colItemSetName),
				new SheetReader("gem", this::readGems, colGemName)
		);
	}

	private final ExcelColumn colItemId = column("id");
	private final ExcelColumn colItemName = column("name");
	private final ExcelColumn colItemRarity = column("rarity");
	private final ExcelColumn colItemItemLevel = column("item_level");
	private final ExcelColumn colItemReqLevel = column("req_level");
	private final ExcelColumn colItemBinding = column("binding");
	private final ExcelColumn colItemUnique = column("unique");
	private final ExcelColumn colItemItemType = column("item_type");
	private final ExcelColumn colItemItemSubtype = column("item_subtype");
	private final ExcelColumn colItemPhase = column("phase");
	private final ExcelColumn colItemSource = column("source");
	private final ExcelColumn colItemSocketTypes = column("socket_types");
	private final ExcelColumn colItemSocketBonus = column("socket_bonus");
	private final ExcelColumn colItemClassRestriction = column("class_restriction");
	private final ExcelColumn colItemRaceRestriction = column("race_restriction");
	private final ExcelColumn colItemSideRestriction = column("side_restriction");
	private final ExcelColumn colItemItemSet = column("item_set");
	private final ExcelColumn colItemReqProfession = column("req_profession");
	private final ExcelColumn colItemReqProfessionLevel = column("req_profession_level");
	private final ExcelColumn colItemReqProfessionSpec = column("req_profession_spec");
	private final ExcelColumn colItemSellPrice = column("sell_price");
	private final ExcelColumn colItemStat = column("stat");
	private final ExcelColumn colItemIcon = column("icon");
	private final ExcelColumn colItemTooltip = column("tooltip");

	private static final int MAX_ITEM_STATS = 10;

	private void readItems() {
		Item item = getItem();
		itemDataRepository.addItem(item);
	}

	private Item getItem() {
		var id = colItemId.getInteger();
		var name = colItemName.getString();
		var rarity = colItemRarity.getEnum(ItemRarity::valueOf);
		var itemLevel = colItemItemLevel.getInteger(0);
		var requiredLevel = colItemReqLevel.getInteger(0);
		var binding = colItemBinding.getEnum(Binding::valueOf, null);
		var unique = colItemUnique.getBoolean();
		var itemType = colItemItemType.getEnum(ItemType::valueOf);
		var itemSubType = colItemItemSubtype.getEnum(ItemSubType::tryParse, null);
		var phase = colItemPhase.getEnum(Phase::valueOf, Phase.TBC_P1);
		var source = colItemSource.getString();
		var socketTypes = colItemSocketTypes.getList(SocketType::valueOf);
		var socketBonus = colItemSocketBonus.getString(null);
		var classRestriction = colItemClassRestriction.getList(CharacterClass::valueOf);
		var raceRestriction = colItemRaceRestriction.getList(Race::valueOf);
		var sideRestriction = colItemSideRestriction.getEnum(Side::valueOf, null);
		var itemSetName = colItemItemSet.getString(null);
		var requiredProfession = colItemReqProfession.getEnum(Profession::valueOf, null);
		var requiredProfessionLevel = colItemReqProfessionLevel.getInteger(0);
		var requiredProfessionSpec = colItemReqProfessionSpec.getEnum(ProfessionSpecialization::valueOf, null);
		var sellPrice = Money.parse(colItemSellPrice.getString(null));
		var icon = colItemIcon.getString();
		var tooltip = colItemTooltip.getString();
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
			String line = colItemStat.multi(i).getString(null);
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
		if (itemType.getCategory() == ItemCategory.ARMOR && itemSubType == null) {
			throw new IllegalArgumentException(item.getName());
		}
		if (itemType.getCategory() == ItemCategory.WEAPON && itemSubType == null) {
			throw new IllegalArgumentException(item.getName());
		}
		if (itemType.getCategory() == ItemCategory.PROJECTILE && itemSubType == null) {
			throw new IllegalArgumentException(item.getName());
		}
		if ((itemType.getCategory() != ItemCategory.ARMOR && itemType.getCategory() != ItemCategory.WEAPON && itemType.getCategory() != ItemCategory.PROJECTILE) && itemSubType != null) {
			throw new IllegalArgumentException(item.getName());
		}
	}

	private final Map<String, List<Item>> setPiecesByName = new HashMap<>();

	private final ExcelColumn colItemSetName = column("name");
	private final ExcelColumn colItemSetReqProf = column("req_prof");
	private final ExcelColumn colItemSetReqProfLvl = column("req_prof_lvl");

	private static final int MAX_ITEM_SET_BONUSES = 6;

	private void readItemSets() {
		ItemSet itemSet = getItemSet();
		itemDataRepository.addItemSet(itemSet);
	}

	private ItemSet getItemSet() {
		var name = colItemSetName.getString();
		var itemSetBonuses = getItemSetBonuses();
		var classRestriction = List.<CharacterClass>of();// TODO brak ograniczenia klas dla Tier
		var requiredProfession = colItemSetReqProf.getEnum(Profession::valueOf, null);
		var requiredProfessionLevel = colItemSetReqProfLvl.getInteger(0);

		var itemSet = new ItemSet(name, null, itemSetBonuses, setPiecesByName.getOrDefault(name, List.of()));

		itemSet.getRestriction().setClassRestriction(classRestriction);
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

	private final ExcelColumn colGemId = column("id");
	private final ExcelColumn colGemName = column("name");
	private final ExcelColumn colGemRarity = column("rarity");
	private final ExcelColumn colGemItemLevel = column("item_level");
	private final ExcelColumn colGemPhase = column("phase");
	private final ExcelColumn colGemSource = column("source");
	private final ExcelColumn colGemColor = column("color");
	private final ExcelColumn colGemBinding = column("binding");
	private final ExcelColumn colGemUnique = column("unique");
	private final ExcelColumn colGemStats = column("stats");
	private final ExcelColumn colGemMetaEnablers = column("meta_enablers");
	private final ExcelColumn colGemSellPrice = column("sell_price");
	private final ExcelColumn colGemIcon = column("icon");
	private final ExcelColumn colGemTooltip = column("tooltip");

	private void readGems() {
		Gem gem = getGem();
		itemDataRepository.addGem(gem);
	}

	private Gem getGem() {
		var id = colGemId.getInteger();
		var name = colGemName.getString();
		var rarity = colGemRarity.getEnum(ItemRarity::valueOf);
		var itemLevel = colGemItemLevel.getInteger();
		var phase = colGemPhase.getEnum(Phase::valueOf, Phase.TBC_P0);
		var source = colGemSource.getString();
		var color = colGemColor.getEnum(GemColor::valueOf);
		var binding = colGemBinding.getEnum(Binding::valueOf, Binding.BINDS_ON_EQUIP);
		var unique = colGemUnique.getBoolean();
		var metaEnablers = colGemMetaEnablers.getList(MetaEnabler::valueOf);
		var sellPrice = Money.parse(colGemSellPrice.getString(null));
		var icon = colGemIcon.getString();
		var tooltip = colGemTooltip.getString();
		var stats = GemStatsParser.tryParseStats(colGemStats.getString());

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
