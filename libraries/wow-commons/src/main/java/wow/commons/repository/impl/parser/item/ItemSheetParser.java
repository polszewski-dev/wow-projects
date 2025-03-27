package wow.commons.repository.impl.parser.item;

import wow.commons.model.categorization.ItemCategory;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.item.*;
import wow.commons.model.item.impl.ItemImpl;
import wow.commons.model.spell.SpellSchool;
import wow.commons.repository.spell.SpellRepository;

import static wow.commons.repository.impl.parser.item.ItemBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class ItemSheetParser extends AbstractItemSheetParser {
	private final ExcelColumn colSocketTypes = column(ITEM_SOCKET_TYPES);
	private final ExcelColumn colItemSet = column(ITEM_ITEM_SET);

	private final ItemExcelParser parser;

	public ItemSheetParser(String sheetName, SourceParserFactory sourceParserFactory, SpellRepository spellRepository, ItemExcelParser parser) {
		super(sheetName, sourceParserFactory, spellRepository);
		this.parser = parser;
	}

	@Override
	protected void readSingleRow() {
		var item = getItem();
		parser.addItem(item);
	}

	private Item getItem() {
		var id = getId();
		var description = getDescription();
		var timeRestriction = getTimeRestriction();
		var characterRestriction = getRestriction();
		var basicItemInfo = getBasicItemInfo();
		var pveRoles = getPveRoles();
		var itemSetName = colItemSet.getString(null);
		var weaponStats = getWeaponStats();

		var item = new ItemImpl(id, description, timeRestriction, characterRestriction, basicItemInfo, weaponStats, pveRoles);

		var source = new ItemSource(item);
		var effects = readItemEffects(ITEM_EFFECT_PREFIX, ITEM_MAX_EFFECTS, timeRestriction, source);
		var socketSpecification = getSocketSpecification(timeRestriction, source);
		var activatedAbility = getActivatedAbility(source);

		item.setEffects(effects);
		item.setSocketSpecification(socketSpecification);
		item.setActivatedAbility(activatedAbility);

		validateItem(item);

		if (itemSetName != null) {
			parser.addItemSetPiece(itemSetName, item);
		}

		return item;
	}

	private ItemSocketSpecification getSocketSpecification(TimeRestriction timeRestriction, ItemSource source) {
		var socketTypes = colSocketTypes.getList(SocketType::valueOf);

		if (socketTypes.isEmpty()) {
			return ItemSocketSpecification.EMPTY;
		}

		var socketBonus = readItemEffect(SOCKET_BONUS_PREFIX, 1, timeRestriction, source);

		return new ItemSocketSpecification(socketTypes, socketBonus);
	}

	private final ExcelColumn colWeaponDamageMin = column(ITEM_WEAPON_DAMAGE_MIN);
	private final ExcelColumn colWeaponDamageMax = column(ITEM_WEAPON_DAMAGE_MAX);
	private final ExcelColumn colWeaponDamageType = column(ITEM_WEAPON_DAMAGE_TYPE);
	private final ExcelColumn colWeaponDps = column(ITEM_WEAPON_DPS);
	private final ExcelColumn colWeaponSpeed = column(ITEM_WEAPON_SPEED);

	private WeaponStats getWeaponStats() {
		if (colWeaponDamageMin.isEmpty()) {
			return null;
		}

		var damageMin = colWeaponDamageMin.getInteger();
		var damageMax = colWeaponDamageMax.getInteger();
		var damageType = colWeaponDamageType.getEnum(SpellSchool::parse, null);
		var dps = colWeaponDps.getDouble();
		var speed = colWeaponSpeed.getDuration();

		return new WeaponStats(damageMin, damageMax, damageType, dps, speed);
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
