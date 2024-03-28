import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { Equipment } from '../../model/equipment/Equipment';
import { EquipmentOptions } from '../../model/equipment/EquipmentOptions';
import { EquipmentSocketStatus } from '../../model/equipment/EquipmentSocketStatus';
import { EquippableItem } from '../../model/equipment/EquippableItem';
import { ItemFilter } from '../../model/equipment/ItemFilter';
import { ItemSlot } from '../../model/equipment/ItemSlot';
import { ItemType } from '../../model/equipment/ItemType';
import { ItemSlotGroup, getItemSlotGroup, getSlots } from '../../model/upgrade/ItemSlotGroup';
import { Upgrade } from '../../model/upgrade/Upgrade';
import { EquipmentService } from '../../services/equipment.service';
import { ItemChange } from '../equipment-slot-editor/equipment-slot-editor.component';

@Component({
	selector: 'app-equipment-editor',
	templateUrl: './equipment-editor.component.html',
	styleUrls: ['./equipment-editor.component.css']
})
export class EquipmentEditorComponent implements OnChanges {
	@Input() selectedCharacterId!: string;
	@Input() upgradesBySlotGroup: Partial<Record<ItemSlotGroup, Upgrade[]>> = {};
	@Input() itemFilter!: ItemFilter;
	@Output() equipmentChanged = new EventEmitter<void>();
	@Output() itemFilterChanged = new EventEmitter<void>();

	readonly itemSlots = Object.values(ItemSlot);

	equipment?: Equipment;
	equipmentOptions?: EquipmentOptions;
	equipmentSocketStatus?: EquipmentSocketStatus;
	editGems = false;
	heroics = false;

	readonly ItemSlot = ItemSlot;
	readonly ItemType = ItemType;

	readonly getItemSlotGroup = getItemSlotGroup;

	constructor(private equipmentService: EquipmentService) {}

	ngOnChanges(changes: SimpleChanges): void {
		if (!changes['selectedCharacterId']) {
			return;
		}
		this.equipmentSocketStatus = undefined;
		this.equipmentService.getEquipment(this.selectedCharacterId).subscribe(equipment => {
			this.equipment = equipment;
		});
		this.equipmentService.getEquipmentOptions(this.selectedCharacterId).subscribe(equipmentOptions => {
			this.equipmentOptions = equipmentOptions;
			this.editGems = equipmentOptions.editGems;
			this.heroics = equipmentOptions.heroics;
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
		const items = this.upgradesBySlotGroup[slotGroup]![0].itemDifference;
		this.equipmentService.changeItems(this.selectedCharacterId, slotGroup, items!).subscribe(() => {
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
		let itemSlots = getSlots(slotGroup);

		for (let i = 0; i < itemSlots.length; ++i) {
			this.equipment!.itemsBySlot[itemSlots[i]] = items[i];
		}

		this.equipmentChanged.emit();
	}

	updateSocketStatus() {
		this.equipmentService.getSocketStatus(this.selectedCharacterId).subscribe(equipmentSocketStatus => {
			this.equipmentSocketStatus = equipmentSocketStatus;
		});
	}

	resetEquipment() {
		this.equipmentService.resetEquipment(this.selectedCharacterId).subscribe(equipment => {
			this.equipment = equipment;
			this.equipmentChanged.emit();
		});
	}

	onFilterChange() {
		this.itemFilterChanged.emit();
	}
}
