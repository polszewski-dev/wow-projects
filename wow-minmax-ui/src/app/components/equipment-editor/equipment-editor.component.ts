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
import { getItemSlotGroup, getSlots, ItemSlotGroup } from 'src/app/model/upgrade/ItemSlotGroup';
import { Upgrade } from 'src/app/model/upgrade/Upgrade';
import { EquipmentService } from 'src/app/services/equipment.service';
import { ItemChange } from '../equipment-slot-editor/ItemChange';
import { ItemFilter } from 'src/app/model/equipment/ItemFilter';

@Component({
	selector: 'app-equipment-editor',
	templateUrl: './equipment-editor.component.html',
	styleUrls: ['./equipment-editor.component.css']
})
export class EquipmentEditorComponent implements OnChanges {
	@Input() selectedCharacterId!: string;
	@Input() upgradesBySlotGroup: { [key in ItemSlotGroup]?: Upgrade[] } = {};
	@Input() itemFilter!: ItemFilter;
	@Output() equipmentChanged = new EventEmitter<void>();
	@Output() itemFilterChanged = new EventEmitter<void>();

	readonly itemSlots: ItemSlot[] = Object.values(ItemSlot);

	equipment?: Equipment;
	equipmentOptions?: EquipmentOptions;
	equipmentSocketStatus?: EquipmentSocketStatus;
	editGems: boolean = false;
	heroics: boolean = false;

	readonly ItemSlot = ItemSlot;
	readonly ItemType = ItemType;

	readonly getItemSlotGroup = getItemSlotGroup;

	constructor(private equipmentService: EquipmentService) {}

	ngOnChanges(changes: SimpleChanges): void {
		if (!changes['selectedCharacterId']) {
			return;
		}
		this.equipmentSocketStatus = undefined;
		this.equipmentService.getEquipment(this.selectedCharacterId).subscribe((equipment: Equipment) => {
			this.equipment = equipment;
		});
		this.equipmentService.getEquipmentOptions(this.selectedCharacterId).subscribe((equipmentOptions: EquipmentOptions) => {
			this.equipmentOptions = sort(equipmentOptions);
			this.editGems = equipmentOptions.editGems;
			this.heroics = equipmentOptions.heroics;
		});
		this.updateSocketStatus();
	}

	onItemChange(itemChange: ItemChange): void {
		this.updateEquipment(itemChange);
		this.updateSocketStatus();
	}

	onEnchantChange(itemChange: ItemChange): void {
		this.updateEquipment(itemChange);
	}

	onGemChange(itemChange: ItemChange): void {
		this.updateEquipment(itemChange);
		this.updateSocketStatus();
	}

	onUpgradeCounterClicked(slotGroup: ItemSlotGroup): void {
		const items = this.upgradesBySlotGroup[slotGroup]![0].itemDifference;
		this.equipmentService.changeItems(this.selectedCharacterId, slotGroup, items!).subscribe(() => {
			this.updateEquipmentSlots(slotGroup, items!);
			this.updateSocketStatus();
		});
	}

	updateEquipment(itemChange: ItemChange): void {
		this.equipment!.itemsBySlot[itemChange.itemSlot] = itemChange.item;

		if (itemChange.itemSlot === ItemSlot.MAIN_HAND && itemChange.item?.item.itemType === ItemType.TWO_HAND) {
			this.equipment!.itemsBySlot[ItemSlot.OFF_HAND] = undefined;
		}

		this.equipmentChanged.emit();
	}

	updateEquipmentSlots(slotGroup: ItemSlotGroup, items: EquippableItem[]): void {
		let itemSlots = getSlots(slotGroup);

		for (let i = 0; i < itemSlots.length; ++i) {
			this.equipment!.itemsBySlot[itemSlots[i]] = items[i];
		}

		this.equipmentChanged.emit();
	}

	updateSocketStatus(): void {
		this.equipmentService.getSocketStatus(this.selectedCharacterId).subscribe((equipmentSocketStatus: EquipmentSocketStatus) => {
			this.equipmentSocketStatus = equipmentSocketStatus;
		});
	}

	resetEquipment(): void {
		this.equipmentService.resetEquipment(this.selectedCharacterId).subscribe((equipment: Equipment) => {
			this.equipment = equipment;
			this.equipmentChanged.emit();
		});
	}

	onFilterChange() {
		this.itemFilterChanged.emit();
	}
}

function sort(equipmentOptions: EquipmentOptions): EquipmentOptions {
	equipmentOptions.itemOptions.forEach(x => x.items.sort((a, b) => itemOrder(a, b)));
	equipmentOptions.enchantOptions.forEach(x => x.enchants.sort((a, b) => enchantOrder(a, b)));
	equipmentOptions.gemOptions.forEach(x => x.gems.sort((a, b) => gemOrder(a, b)));
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
