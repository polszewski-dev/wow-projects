import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { Enchant } from 'src/app/model/equipment/Enchant';
import { Equipment } from 'src/app/model/equipment/Equipment';
import { EquipmentOptions } from 'src/app/model/equipment/EquipmentOptions';
import { EquipmentSocketStatus } from 'src/app/model/equipment/EquipmentSocketStatus';
import { EquippableItem } from 'src/app/model/equipment/EquippableItem';
import { Gem } from 'src/app/model/equipment/Gem';
import { GemColor } from 'src/app/model/equipment/GemColor';
import { Item } from 'src/app/model/equipment/Item';
import { ItemRarity } from 'src/app/model/equipment/ItemRarity';
import { ItemSlot } from 'src/app/model/equipment/ItemSlot';
import { ItemType } from 'src/app/model/equipment/ItemType';
import { ProfileInfo } from 'src/app/model/ProfileInfo';
import { ItemSlotGroup } from 'src/app/model/upgrade/ItemSlotGroup';
import { Upgrade } from 'src/app/model/upgrade/Upgrade';
import { EquipmentService } from 'src/app/services/equipment.service';
import { UpgradeService } from 'src/app/services/upgrade.service';
import { ItemChange } from '../equipment-slot-editor/ItemChange';

@Component({
	selector: 'app-equipment-editor',
	templateUrl: './equipment-editor.component.html',
	styleUrls: ['./equipment-editor.component.css']
})
export class EquipmentEditorComponent implements OnChanges {
	@Input() selectedProfile!: ProfileInfo;
	@Input() upgradesBySlotGroup: { [key in ItemSlotGroup]?: Upgrade[] } = {};
	@Output() equipmentChanged = new EventEmitter<void>()

	readonly itemSlots: ItemSlot[] = Object.values(ItemSlot);

	equipment?: Equipment;
	equipmentOptions?: EquipmentOptions;
	equipmentSocketStatus?: EquipmentSocketStatus;

	ItemSlot = ItemSlot;
	ItemType = ItemType;

	constructor(private equipmentService: EquipmentService) {}

	ngOnChanges(changes: SimpleChanges) {
		if (!changes['selectedProfile']) {
			return;
		}
		this.equipmentSocketStatus = undefined;
		this.equipmentService.getEquipment(this.selectedProfile.profileId).subscribe((equipment: Equipment) => {
			this.equipment = equipment;
		});
		this.equipmentService.getEquipmentOptions(this.selectedProfile.profileId).subscribe((equipmentOptions: EquipmentOptions) => {
			this.equipmentOptions = sort(equipmentOptions);
		});
		this.updateSocketStatus();
	}

	onItemChange(itemChange: ItemChange) {
		this.updateEquipment(itemChange);
		this.updateSocketStatus();
	}

	onEnchantChange(itemChange: ItemChange) {
		this.updateEquipment(itemChange);
	}

	onGemChange(itemChange: ItemChange) {
		this.updateEquipment(itemChange);
		this.updateSocketStatus();
	}

	onUpgradeCounterClicked(slotGroup: ItemSlotGroup) {
		const items = this.upgradesBySlotGroup[slotGroup]?.[0].itemDifference;
		this.equipmentService.changeItems(this.selectedProfile.profileId, slotGroup, items!).subscribe(() => {
			this.updateEquipmentSlots(slotGroup, items!);
			this.updateSocketStatus();
		});
	}

	updateEquipment(itemChange: ItemChange) {
		this.equipment!.itemsBySlot[itemChange.itemSlot] = itemChange.item;

		if (itemChange.itemSlot === ItemSlot.MAIN_HAND && itemChange.item?.item.itemType === ItemType.TWO_HAND) {
			this.equipment!.itemsBySlot[ItemSlot.OFF_HAND] = undefined;
		}

		this.equipmentChanged.emit();
	}

	updateEquipmentSlots(slotGroup: ItemSlotGroup, items: EquippableItem[]) {
		let itemSlots = this.getSlots(slotGroup);

		for (let i = 0; i < itemSlots.length; ++i) {
			this.equipment!.itemsBySlot[itemSlots[i]] = items[i];
		}

		this.equipmentChanged.emit();
	}

	updateSocketStatus() {
		this.equipmentService.getSocketStatus(this.selectedProfile!.profileId).subscribe((equipmentSocketStatus: EquipmentSocketStatus) => {
			this.equipmentSocketStatus = equipmentSocketStatus;
		});
	}

	resetEquipment() {
		this.equipmentService.resetEquipment(this.selectedProfile!.profileId).subscribe((equipment: Equipment) => {
			this.equipment = equipment;
			this.equipmentChanged.emit();
		});
	}

	getItemSlotGroup(itemSlot: ItemSlot): ItemSlotGroup | undefined {
		switch(itemSlot) {
			case ItemSlot.HEAD: return ItemSlotGroup.HEAD;
			case ItemSlot.NECK: return ItemSlotGroup.NECK;
			case ItemSlot.SHOULDER: return ItemSlotGroup.SHOULDER;
			case ItemSlot.BACK: return ItemSlotGroup.BACK;
			case ItemSlot.CHEST: return ItemSlotGroup.CHEST;
			case ItemSlot.WRIST: return ItemSlotGroup.WRIST;
			case ItemSlot.HANDS: return ItemSlotGroup.HANDS;
			case ItemSlot.WAIST: return ItemSlotGroup.WAIST;
			case ItemSlot.LEGS: return ItemSlotGroup.LEGS;
			case ItemSlot.FEET: return ItemSlotGroup.FEET;
			case ItemSlot.FINGER_1: return ItemSlotGroup.FINGERS;
			case ItemSlot.TRINKET_1: return ItemSlotGroup.TRINKETS;
			case ItemSlot.MAIN_HAND: return ItemSlotGroup.WEAPONS;
			case ItemSlot.RANGED: return ItemSlotGroup.RANGED;
			default:
				return undefined;
		}
	}

	getSlots(slotGroup: ItemSlotGroup): ItemSlot[] {
		switch(slotGroup) {
			case ItemSlotGroup.HEAD: return [ ItemSlot.HEAD ];
			case ItemSlotGroup.NECK: return [ ItemSlot.NECK ];
			case ItemSlotGroup.SHOULDER: return [ ItemSlot.SHOULDER ];
			case ItemSlotGroup.BACK: return [ ItemSlot.BACK ];
			case ItemSlotGroup.CHEST: return [ ItemSlot.CHEST ];
			case ItemSlotGroup.WRIST: return [ ItemSlot.WRIST ];
			case ItemSlotGroup.HANDS: return [ ItemSlot.HANDS ];
			case ItemSlotGroup.WAIST: return [ ItemSlot.WAIST ];
			case ItemSlotGroup.LEGS: return [ ItemSlot.LEGS ];
			case ItemSlotGroup.FEET: return [ ItemSlot.FEET ];
			case ItemSlotGroup.FINGERS: return [ ItemSlot.FINGER_1, ItemSlot.FINGER_2 ];
			case ItemSlotGroup.TRINKETS: return [ ItemSlot.TRINKET_1, ItemSlot.TRINKET_2 ];
			case ItemSlotGroup.WEAPONS: return [ ItemSlot.MAIN_HAND, ItemSlot.OFF_HAND ];
			case ItemSlotGroup.RANGED: return [ ItemSlot.RANGED ];
			default:
				throw new Error('Unhandled slot group' + slotGroup); 
		}
	}
}

function sort(equipmentOptions: EquipmentOptions): EquipmentOptions {
	for (let items of Object.values(equipmentOptions.itemsByItemSlot)) {
		items.sort((a, b) => itemOrder(a, b));
	}

	for (let enchants of Object.values(equipmentOptions.enchantsByItemType)) {
		enchants.sort((a, b) => enchantOrder(a, b));
	}

	for (let gems of Object.values(equipmentOptions.gemsBySocketType)) {
		gems.sort((a, b) => gemOrder(a, b));
	}

	return equipmentOptions;
}

function itemOrder(a: Item, b: Item): number {
	let cmp = a.score - b.score;
	if (cmp !== 0) {
		return -cmp;
	}
	return a.name.localeCompare(b.name);
}

function enchantOrder(a: Enchant, b: Enchant): number {
	return a.name.localeCompare(b.name);
}

function gemOrder(a: Gem, b: Gem): number {
	const aSourceIndex = sourceIndex(a);
	const bSourceIndex = sourceIndex(b);

	let cmp = aSourceIndex - bSourceIndex;
	if (cmp !== 0) {
		return cmp;
	}

	const aRarityIndex = Object.keys(ItemRarity).indexOf(a.rarity);
	const bRarityIndex = Object.keys(ItemRarity).indexOf(b.rarity);

	cmp = aRarityIndex - bRarityIndex;
	if (cmp !== 0) {
		return -cmp;
	}

	const aColorIndex = Object.keys(GemColor).indexOf(a.color);
	const bColorIndex = Object.keys(GemColor).indexOf(b.color);

	cmp = aColorIndex - bColorIndex;
	if (cmp !== 0) {
		return cmp;
	}

	return a.name.localeCompare(b.name);
}

function sourceIndex(gem: Gem): number {
	if (gem.source === 'Jewelcrafting' || gem.source === 'Enchanting') {
		return 1;
	}
	return 2;
}
