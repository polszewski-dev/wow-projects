import { Component, EventEmitter, Input, OnInit, Output, TemplateRef, ViewChild, ViewContainerRef } from '@angular/core';
import { EquipmentOptions } from '../../model/equipment/EquipmentOptions';
import { EquippableItem } from '../../model/equipment/EquippableItem';
import { ItemChange } from '../../model/equipment/ItemChange';
import { ItemSlot } from '../../model/equipment/ItemSlot';
import { ItemSocketStatus } from '../../model/equipment/ItemSocketStatus';
import { ItemSlotGroup } from '../../model/upgrade/ItemSlotGroup';
import { Upgrade } from '../../model/upgrade/Upgrade';

@Component({
	selector: 'app-equipment-slot-editor',
	templateUrl: './equipment-slot-editor.component.html',
	styleUrls: ['./equipment-slot-editor.component.css']
})
export class EquipmentSlotEditorComponent implements OnInit {
	@Input() selectedCharacterId!: string;
	@Input() itemSlot!: ItemSlot;
	@Input() equippableItem?: EquippableItem;
	@Input() equipmentOptions?: EquipmentOptions;
	@Input() socketStatus?: ItemSocketStatus;
	@Input() visible = true;
	@Input() slotGroup?: ItemSlotGroup;
	@Input() upgrades?: Upgrade[];

	@Output() itemChanged = new EventEmitter<ItemChange>();
	@Output() enchantChanged = new EventEmitter<ItemChange>();
	@Output() gemChanged = new EventEmitter<ItemChange>();
	@Output() equipUpgradeClicked = new EventEmitter<ItemSlotGroup>();

	readonly ItemSlot = ItemSlot;

	@ViewChild('template', { static: true }) template!: TemplateRef<any>;

	constructor(private viewContainerRef: ViewContainerRef) {}

	ngOnInit(): void {
		this.viewContainerRef.createEmbeddedView(this.template);
	}

	onItemChange(itemChange: ItemChange) {
		this.equippableItem = itemChange.item;
		this.itemChanged.emit(itemChange);
	}

	onEnchantChange(itemChange: ItemChange) {
		this.enchantChanged.emit(itemChange);
	}

	onGemChange(itemChange: ItemChange) {
		this.gemChanged.emit(itemChange);
	}

	onEquipUpgradeClicked(slotGroup: ItemSlotGroup) {
		this.equipUpgradeClicked.emit(slotGroup);
	}
}
