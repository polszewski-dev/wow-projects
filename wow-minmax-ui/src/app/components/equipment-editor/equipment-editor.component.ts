import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { Equipment } from 'src/app/model/equipment/Equipment';
import { EquipmentOptions } from 'src/app/model/equipment/EquipmentOptions';
import { EquipmentSocketStatus } from 'src/app/model/equipment/EquipmentSocketStatus';
import { EquippableItem } from 'src/app/model/equipment/EquippableItem';
import { ItemFilter } from 'src/app/model/equipment/ItemFilter';
import { ItemSlot } from 'src/app/model/equipment/ItemSlot';
import { ItemType } from 'src/app/model/equipment/ItemType';
import { ItemSlotGroup, getItemSlotGroup, getSlots } from 'src/app/model/upgrade/ItemSlotGroup';
import { Upgrade } from 'src/app/model/upgrade/Upgrade';
import { EquipmentService } from 'src/app/services/equipment.service';
import { ItemChange } from '../equipment-slot-editor/ItemChange';

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
			this.equipmentOptions = equipmentOptions;
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
