package wow.commons.repository.impl.parsers.items;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.*;
import wow.commons.model.config.Description;
import wow.commons.model.config.Restriction;
import wow.commons.model.item.BasicItemInfo;
import wow.commons.model.item.Item;
import wow.commons.model.item.ItemSocketSpecification;
import wow.commons.model.item.SocketType;
import wow.commons.model.professions.Profession;
import wow.commons.model.professions.ProfessionSpecialization;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;
import wow.commons.model.sources.Source;
import wow.commons.model.unit.CharacterClass;
import wow.commons.model.unit.Race;
import wow.commons.repository.PVERepository;
import wow.commons.repository.impl.ItemDataRepositoryImpl;
import wow.commons.repository.impl.parsers.gems.SocketBonusParser;
import wow.commons.repository.impl.parsers.stats.StatParser;
import wow.commons.repository.impl.parsers.stats.StatPatternRepository;
import wow.commons.util.ExcelSheetReader;
import wow.commons.util.SourceParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static wow.commons.repository.impl.parsers.items.ItemBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class ItemSheetReader extends ExcelSheetReader {
	private final ExcelColumn colId = column(ITEM_ID);
	private final ExcelColumn colName = column(ITEM_NAME);
	private final ExcelColumn colRarity = column(ITEM_RARITY);
	private final ExcelColumn colItemLevel = column(ITEM_ITEM_LEVEL);
	private final ExcelColumn colReqLevel = column(ITEM_REQ_LEVEL);
	private final ExcelColumn colBinding = column(ITEM_BINDING);
	private final ExcelColumn colUnique = column(ITEM_UNIQUE);
	private final ExcelColumn colItemType = column(ITEM_ITEM_TYPE);
	private final ExcelColumn colItemSubtype = column(ITEM_ITEM_SUBTYPE);
	private final ExcelColumn colPhase = column(ITEM_PHASE);
	private final ExcelColumn colSource = column(ITEM_SOURCE);
	private final ExcelColumn colSocketTypes = column(ITEM_SOCKET_TYPES);
	private final ExcelColumn colSocketBonus = column(ITEM_SOCKET_BONUS);
	private final ExcelColumn colClassRestriction = column(ITEM_CLASS_RESTRICTION);
	private final ExcelColumn colRaceRestriction = column(ITEM_RACE_RESTRICTION);
	private final ExcelColumn colSideRestriction = column(ITEM_SIDE_RESTRICTION);
	private final ExcelColumn colItemSet = column(ITEM_ITEM_SET);
	private final ExcelColumn colReqProfession = column(ITEM_REQ_PROFESSION);
	private final ExcelColumn colReqProfessionLevel = column(ITEM_REQ_PROFESSION_LEVEL);
	private final ExcelColumn colReqProfessionSpec = column(ITEM_REQ_PROFESSION_SPEC);
	private final ExcelColumn colStat = column(ITEM_STAT);
	private final ExcelColumn colIcon = column(ITEM_ICON);
	private final ExcelColumn colTooltip = column(ITEM_TOOLTIP);

	private static final int MAX_ITEM_STATS = 10;

	private final PVERepository pveRepository;
	private final ItemDataRepositoryImpl itemDataRepository;
	private final Map<String, List<Item>> setPiecesByName;
	
	public ItemSheetReader(String sheetName, PVERepository pveRepository, ItemDataRepositoryImpl itemDataRepository, Map<String, List<Item>> setPiecesByName) {
		super(sheetName);
		this.pveRepository = pveRepository;
		this.itemDataRepository = itemDataRepository;
		this.setPiecesByName = setPiecesByName;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colName;
	}

	@Override
	protected void readSingleRow() {
		Item item = getItem();
		itemDataRepository.addItem(item);
	}

	private Item getItem() {
		var id = colId.getInteger();
		var name = colName.getString();
		var rarity = colRarity.getEnum(ItemRarity::valueOf);
		var itemLevel = colItemLevel.getInteger(0);
		var requiredLevel = colReqLevel.getInteger(0);
		var binding = colBinding.getEnum(Binding::valueOf, null);
		var unique = colUnique.getBoolean();
		var itemType = colItemType.getEnum(ItemType::valueOf);
		var itemSubType = colItemSubtype.getEnum(ItemSubType::tryParse, null);
		var phase = colPhase.getEnum(Phase::valueOf);
		var source = colSource.getString();
		var socketTypes = colSocketTypes.getList(SocketType::valueOf);
		var socketBonus = colSocketBonus.getString(null);
		var classRestriction = colClassRestriction.getList(CharacterClass::valueOf);
		var raceRestriction = colRaceRestriction.getList(Race::valueOf);
		var sideRestriction = colSideRestriction.getEnum(Side::valueOf, null);
		var itemSetName = colItemSet.getString(null);
		var requiredProfession = colReqProfession.getEnum(Profession::valueOf, null);
		var requiredProfessionLevel = colReqProfessionLevel.getInteger(0);
		var requiredProfessionSpec = colReqProfessionSpec.getEnum(ProfessionSpecialization::valueOf, null);
		var icon = colIcon.getString();
		var tooltip = colTooltip.getString();
		var stats = getStats();

		Description description = new Description(name, icon, tooltip);
		Restriction restriction = Restriction.builder()
				.phase(phase)
				.requiredLevel(requiredLevel)
				.classRestriction(classRestriction)
				.raceRestriction(raceRestriction)
				.sideRestriction(sideRestriction)
				.requiredProfession(requiredProfession)
				.requiredProfessionLevel(requiredProfessionLevel)
				.requiredProfessionSpec(requiredProfessionSpec)
				.build();
		Set<Source> sources = new SourceParser(pveRepository, itemDataRepository).parse(source);
		BasicItemInfo basicItemInfo = new BasicItemInfo(rarity, binding, unique, itemLevel, sources);
		ItemSocketSpecification socketSpecification = getSocketSpecification(socketTypes, socketBonus);

		Item item = new Item(id, description, restriction, stats, itemType, itemSubType, basicItemInfo, socketSpecification, null);

		validateItem(item);

		if (itemSetName != null) {
			setPiecesByName.computeIfAbsent(itemSetName, x -> new ArrayList<>()).add(item);
		}

		return item;
	}

	private Attributes getStats() {
		StatParser parser = StatPatternRepository.getInstance().getItemStatParser();

		for (int i = 1; i <= MAX_ITEM_STATS; ++i) {
			String line = colStat.multi(i).getString(null);
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
}
