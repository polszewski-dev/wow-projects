import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { Enchant } from 'src/app/model/equipment/Enchant';
import { Equipment } from 'src/app/model/equipment/Equipment';
import { EquipmentOptions } from 'src/app/model/equipment/EquipmentOptions';
import { EquipmentSocketStatus } from 'src/app/model/equipment/EquipmentSocketStatus';
import { Gem } from 'src/app/model/equipment/Gem';
import { GemColor } from 'src/app/model/equipment/GemColor';
import { Item } from 'src/app/model/equipment/Item';
import { ItemRarity } from 'src/app/model/equipment/ItemRarity';
import { ItemSlot } from 'src/app/model/equipment/ItemSlot';
import { ItemType } from 'src/app/model/equipment/ItemType';
import { ProfileInfo } from 'src/app/model/ProfileInfo';
import { EquipmentService } from 'src/app/services/equipment.service';
import { ItemChange } from '../equipment-slot-editor/ItemChange';

@Component({
	selector: 'app-equipment-editor',
	templateUrl: './equipment-editor.component.html',
	styleUrls: ['./equipment-editor.component.css']
})
export class EquipmentEditorComponent implements OnChanges {
	@Input() selectedProfile!: ProfileInfo;
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
			this.updateSocketStatus();
		});
		this.equipmentService.getEquipmentOptions(this.selectedProfile.profileId).subscribe((equipmentOptions: EquipmentOptions) => {
			this.equipmentOptions = sort(equipmentOptions);
		});
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

	updateEquipment(itemChange: ItemChange) {
		this.equipment!.itemsBySlot[itemChange.itemSlot] = itemChange.item;

		if (itemChange.itemSlot === ItemSlot.MAIN_HAND && itemChange.item?.item.itemType === ItemType.TWO_HAND) {
			this.equipment!.itemsBySlot[ItemSlot.OFF_HAND] = undefined;
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
