import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Equipment } from '../../model/equipment/Equipment';
import { EquipmentOptions } from '../../model/equipment/EquipmentOptions';
import { EquipmentSocketStatus } from '../../model/equipment/EquipmentSocketStatus';
import { EquippableItem } from '../../model/equipment/EquippableItem';
import { ItemChange } from '../../model/equipment/ItemChange';
import { ItemFilter } from '../../model/equipment/ItemFilter';
import { ItemSlot } from '../../model/equipment/ItemSlot';
import { ItemType } from '../../model/equipment/ItemType';
import { ItemSlotGroup, getItemSlotGroup, getSlots } from '../../model/upgrade/ItemSlotGroup';
import { Upgrade } from '../../model/upgrade/Upgrade';
import { EquipmentService } from '../../services/equipment.service';

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
	@Output() itemFilterChanged = new EventEmitter<ItemFilter>();

	form = this.formBuilder.nonNullable.group({
		heroics: false,
		raids: false,
		worldBosses: false,
		pvpItems: false,
		greens: false,
		legendaries: false
	});

	readonly itemSlots = Object.values(ItemSlot);

	equipment?: Equipment;
	equipmentOptions?: EquipmentOptions;
	equipmentSocketStatus?: EquipmentSocketStatus;
	editGems = false;
	heroics = false;

	readonly ItemSlot = ItemSlot;
	readonly ItemType = ItemType;

	readonly getItemSlotGroup = getItemSlotGroup;

	constructor(
		private equipmentService: EquipmentService,
		private formBuilder: FormBuilder
	) {
		this.form.valueChanges.subscribe(value => {
			this.itemFilterChanged.emit(value as ItemFilter);
		});
	}

	ngOnChanges(changes: SimpleChanges): void {
		if (changes['itemFilter']) {
			this.form.setValue(this.itemFilter);
		}
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

	onEquipUpgradeClicked(slotGroup: ItemSlotGroup) {
		const items = this.upgradesBySlotGroup[slotGroup]![0].itemDifference!;
		this.equipmentService.changeItems(this.selectedCharacterId, slotGroup, items).subscribe(() => {
			this.updateEquipmentSlots(slotGroup, items);
			this.updateSocketStatus();
		});
	}

	private updateEquipment(itemChange: ItemChange) {
		this.equipItem(itemChange.itemSlot, itemChange.item);

		if (itemChange.itemSlot === ItemSlot.MAIN_HAND && itemChange.item?.item.itemType === ItemType.TWO_HAND) {
			this.equipItem(ItemSlot.OFF_HAND, undefined);
		}

		this.equipmentChanged.emit();
	}

	private updateEquipmentSlots(slotGroup: ItemSlotGroup, items: EquippableItem[]) {
		let itemSlots = getSlots(slotGroup);

		for (let i = 0; i < itemSlots.length; ++i) {
			this.equipItem(itemSlots[i], items[i]);
		}

		this.equipmentChanged.emit();
	}

	private updateSocketStatus() {
		this.equipmentService.getSocketStatus(this.selectedCharacterId).subscribe(equipmentSocketStatus => {
			this.equipmentSocketStatus = equipmentSocketStatus;
		});
	}

	private equipItem(itemSlot: ItemSlot, item?: EquippableItem) {
		this.equipment!.itemsBySlot[itemSlot] = item;
	}

	resetEquipment() {
		this.equipmentService.resetEquipment(this.selectedCharacterId).subscribe(equipment => {
			this.equipment = equipment;
			this.equipmentChanged.emit();
		});
	}
}
