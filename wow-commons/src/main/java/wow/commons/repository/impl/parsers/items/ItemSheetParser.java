package wow.commons.repository.impl.parsers.items;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemCategory;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.config.Description;
import wow.commons.model.config.Restriction;
import wow.commons.model.item.BasicItemInfo;
import wow.commons.model.item.Item;
import wow.commons.model.item.ItemSocketSpecification;
import wow.commons.model.item.SocketType;
import wow.commons.repository.PveRepository;
import wow.commons.repository.impl.ItemDataRepositoryImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static wow.commons.repository.impl.parsers.items.ItemBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class ItemSheetParser extends AbstractItemSheetParser {
	private final ExcelColumn colSocketTypes = column(ITEM_SOCKET_TYPES);
	private final ExcelColumn colItemSet = column(ITEM_ITEM_SET);

	private final Map<String, List<Item>> setPiecesByName;
	
	public ItemSheetParser(String sheetName, PveRepository pveRepository, ItemDataRepositoryImpl itemDataRepository, Map<String, List<Item>> setPiecesByName) {
		super(sheetName, pveRepository, itemDataRepository);
		this.setPiecesByName = setPiecesByName;
	}

	@Override
	protected void readSingleRow() {
		Item item = getItem();
		itemDataRepository.addItem(item);
	}

	private Item getItem() {
		var id = getId();
		var socketTypes = colSocketTypes.getList(SocketType::valueOf);
		var itemSetName = colItemSet.getString(null);
		var stats = readAttributes(ITEM_MAX_STATS);

		Description description = getDescription();
		Restriction restriction = getRestriction();
		BasicItemInfo basicItemInfo = getBasicItemInfo();
		ItemSocketSpecification socketSpecification = getSocketSpecification(socketTypes);

		Item item = new Item(id, description, restriction, stats, basicItemInfo, socketSpecification, null);

		validateItem(item);

		if (itemSetName != null) {
			setPiecesByName.computeIfAbsent(itemSetName, x -> new ArrayList<>()).add(item);
		}

		return item;
	}

	private ItemSocketSpecification getSocketSpecification(List<SocketType> socketTypes) {
		if (socketTypes.isEmpty()) {
			return ItemSocketSpecification.EMPTY;
		}

		Attributes socketBonus = readAttributes(SOCKET_BONUS_PREFIX, SOCKET_BONUS_MAX_STATS);

		return new ItemSocketSpecification(socketTypes, socketBonus);
	}

	private void validateItem(Item item) {
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
