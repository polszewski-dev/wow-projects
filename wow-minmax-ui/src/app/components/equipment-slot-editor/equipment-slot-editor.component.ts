import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, TemplateRef, ViewChild, ViewContainerRef } from '@angular/core';
import { Enchant } from 'src/app/model/equipment/Enchant';
import { EquipmentOptions } from 'src/app/model/equipment/EquipmentOptions';
import { EquippableItem } from 'src/app/model/equipment/EquippableItem';
import { Gem } from 'src/app/model/equipment/Gem';
import { Item } from 'src/app/model/equipment/Item';
import { ItemSlot } from 'src/app/model/equipment/ItemSlot';
import { ItemSocketStatus } from 'src/app/model/equipment/ItemSocketStatus';
import { ProfileInfo } from 'src/app/model/ProfileInfo';
import { EquipmentService } from 'src/app/services/equipment.service';
import { DropdownSelectValueFormatter } from '../dropdown-select/DropdownSelectValueFormatter';
import { ItemChange } from './ItemChange';

@Component({
	selector: 'app-equipment-slot-editor',
	templateUrl: './equipment-slot-editor.component.html',
	styleUrls: ['./equipment-slot-editor.component.css']
})
export class EquipmentSlotEditorComponent implements OnInit {
	@Input() selectedProfile!: ProfileInfo;
	@Input() itemSlot!: ItemSlot;
	@Input() equippableItem?: EquippableItem;
	@Input() equipmentOptions?: EquipmentOptions;
	@Input() socketStatus?: ItemSocketStatus;
	@Input() visible: boolean = true;

	@Output() itemChanged = new EventEmitter<ItemChange>();
	@Output() enchantChanged = new EventEmitter<ItemChange>();
	@Output() gemChanged = new EventEmitter<ItemChange>();

	ItemSlot = ItemSlot;

	itemFormatter = new ItemFormatter();
	enchantFormatter = new EnchantFormatter();
	gemFormatter = new GemFormatter();

	@ViewChild('template', { static: true }) template!: TemplateRef<any>;

	constructor(private equipmentService: EquipmentService, private viewContainerRef: ViewContainerRef) {}

	ngOnInit(): void {
		this.viewContainerRef.createEmbeddedView(this.template);
	}

	onItemChange(item: Item) {
		this.equipmentService.changeItem(this.selectedProfile!.profileId, this.itemSlot!, item.id).subscribe(item => {
			this.equippableItem = item;
			this.itemChanged.emit(this.getItemChange());
		});
	}

	onEnchantChange(enchant: Enchant) {
		this.equipmentService.changeEnchant(this.selectedProfile!.profileId, this.itemSlot!, enchant.id).subscribe(item => {
			this.equippableItem = item;
			this.enchantChanged.emit(this.getItemChange());
		});
	}

	onGemChange(socketIdx: number, gem: Gem) {
		this.equipmentService.changeGem(this.selectedProfile!.profileId, this.itemSlot!, socketIdx, gem.id).subscribe(item => {
			this.equippableItem = item;
			this.gemChanged.emit(this.getItemChange());
		});
	}

	getItemOptions(): Item[] {
		return this.equipmentOptions?.itemsByItemSlot[this.itemSlot] || [];
	}

	getEnchantOptions(): Enchant[] {
		return this.equipmentOptions?.enchantsByItemType[this.equippableItem!.item.itemType] || []
	}

	getGemOptions(socketIdx: number): Gem[] {
		return this.equipmentOptions?.gemsBySocketType[this.equippableItem!.item.socketTypes[socketIdx]] || []
	}

	private getItemChange(): ItemChange {
		return {
			itemSlot: this.itemSlot,
			item: this.equippableItem
		};
	}
}

function getIcon(iconId: string): string {
	return `https://wow.zamimg.com/images/wow/icons/small/${iconId}.jpg`
}

class ItemFormatter implements DropdownSelectValueFormatter<Item> {
	formatElement(value: Item): string {
		return `
			<img src="${getIcon(value.icon)}"/>
			<span class="rarity-${value.rarity.toLowerCase()} item-header">&nbsp;${value.name}</span>
			<span class="item-descr">[${value.source}]</span>
			<span class="item-descr">(${escapeHtml(value.attributes)})</span>
		`;
	}

	formatSelection(value?: Item): string {
		if (!value) {
			return '<i>-- empty --</i>';
		}
		return `
			<img src="${getIcon(value.icon)}"/>
			<span class="rarity-${value.rarity.toLowerCase()} item-header">&nbsp;${value.name}</span>
			<span class="item-descr">[${value.source}]</span>
		`;
	}

	formatTooltip(value?: Item):string {
		return value?.attributes || "";
	}
}

class EnchantFormatter implements DropdownSelectValueFormatter<Enchant> {
	formatElement(value: Enchant): string {
		return `
			<img src="${getIcon(value.icon)}"/>
			<span class="rarity-${value.rarity.toLowerCase()} item-header">&nbsp;${value.name}</span>
			<span class="item-descr">(${escapeHtml(value.attributes)})</span>
		`;
	}

	formatSelection(value?: Enchant): string {
		if (!value) {
			return '<i>-- empty --</i>';
		}
		return `
			<img src="${getIcon(value.icon)}"/>
			<span class="rarity-${value.rarity.toLowerCase()} item-header">&nbsp;${value.name}</span>
		`;
	}

	formatTooltip(value?: Enchant):string {
		return value?.attributes || "";
	}
}

class GemFormatter implements DropdownSelectValueFormatter<Gem> {
	formatElement(value: Gem): string {
		return `
			<img src="${getIcon(value.icon)}"/>
			<span class="rarity-${value.rarity.toLowerCase()} item-header">&nbsp;${value.name}</span>
			<span class="item-descr">(${escapeHtml(value.attributes)})</span>
		`;
	}

	formatSelection(value?: Gem): string {
		if (!value) {
			return '<i>-- empty --</i>';
		}
		return `
			<img src="${getIcon(value.icon)}"/>
			<span class="rarity-${value.rarity.toLowerCase()} item-header">&nbsp;${value.name}</span>
		`;
	}

	formatTooltip(value?: Gem):string {
		return value?.attributes || "";
	}
}

function escapeHtml(value: string): string {
	return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
}
