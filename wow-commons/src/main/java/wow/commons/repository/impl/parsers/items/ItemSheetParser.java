package wow.commons.repository.impl.parsers.items;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemCategory;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Item;
import wow.commons.model.item.ItemSocketSpecification;
import wow.commons.model.item.SocketType;
import wow.commons.model.item.impl.ItemImpl;
import wow.commons.repository.PveRepository;
import wow.commons.repository.impl.ItemRepositoryImpl;

import java.util.List;

import static wow.commons.repository.impl.parsers.items.ItemBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class ItemSheetParser extends AbstractItemSheetParser {
	private final ExcelColumn colSocketTypes = column(ITEM_SOCKET_TYPES);
	private final ExcelColumn colItemSet = column(ITEM_ITEM_SET);

	private final ItemBaseExcelParser parser;
	
	public ItemSheetParser(String sheetName, PveRepository pveRepository, ItemRepositoryImpl itemRepository, ItemBaseExcelParser parser) {
		super(sheetName, pveRepository, itemRepository);
		this.parser = parser;
	}

	@Override
	protected void readSingleRow() {
		Item item = getItem();
		itemRepository.addItem(item);
	}

	private Item getItem() {
		var id = getId();
		var socketTypes = colSocketTypes.getList(SocketType::valueOf);
		var itemSetName = colItemSet.getString(null);
		var stats = readAttributes(ITEM_MAX_STATS);

		var description = getDescription();
		var timeRestriction = getTimeRestriction();
		var characterRestriction = getRestriction();
		var basicItemInfo = getBasicItemInfo();
		var socketSpecification = getSocketSpecification(socketTypes);

		var item = new ItemImpl(id, description, timeRestriction, characterRestriction, stats, basicItemInfo, socketSpecification, null);

		validateItem(item);

		if (itemSetName != null) {
			parser.addItemSetPiece(itemSetName, item);
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
