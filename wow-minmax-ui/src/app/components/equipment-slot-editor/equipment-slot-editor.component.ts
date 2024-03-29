import { Component, EventEmitter, Input, OnInit, Output, TemplateRef, ViewChild, ViewContainerRef } from '@angular/core';
import { PhaseId } from 'src/app/model/character/PhaseId';
import { Enchant } from 'src/app/model/equipment/Enchant';
import { EquipmentOptions } from 'src/app/model/equipment/EquipmentOptions';
import { EquippableItem } from 'src/app/model/equipment/EquippableItem';
import { Gem } from 'src/app/model/equipment/Gem';
import { GemColor } from 'src/app/model/equipment/GemColor';
import { Item } from 'src/app/model/equipment/Item';
import { ItemRarity } from 'src/app/model/equipment/ItemRarity';
import { ItemSlot } from 'src/app/model/equipment/ItemSlot';
import { ItemSocketStatus } from 'src/app/model/equipment/ItemSocketStatus';
import { ItemSlotGroup } from 'src/app/model/upgrade/ItemSlotGroup';
import { Upgrade } from 'src/app/model/upgrade/Upgrade';
import { EquipmentService } from 'src/app/services/equipment.service';
import { getIcon } from 'src/app/util/Icon';
import { DropdownSelectValueFormatter, ElementComparatorFn, GroupKeyComparatorFn, GroupKeyToStringFn } from '../dropdown-select/dropdown-select.component';
import { ItemChange } from './ItemChange';

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
	@Input() visible: boolean = true;
	@Input() slotGroup?: ItemSlotGroup;
	@Input() upgrades?: Upgrade[];

	@Output() itemChanged = new EventEmitter<ItemChange>();
	@Output() enchantChanged = new EventEmitter<ItemChange>();
	@Output() gemChanged = new EventEmitter<ItemChange>();
	@Output() upgradeCounterClicked = new EventEmitter<ItemSlotGroup>();

	readonly ItemSlot = ItemSlot;

	readonly itemFormatter = new ItemFormatter();
	readonly enchantFormatter = new EnchantFormatter();
	readonly gemFormatter = new GemFormatter();

	@ViewChild('template', { static: true }) template!: TemplateRef<any>;

	constructor(private equipmentService: EquipmentService, private viewContainerRef: ViewContainerRef) {}

	ngOnInit(): void {
		this.viewContainerRef.createEmbeddedView(this.template);
	}

	onItemChange(item: Item): void {
		this.equipmentService.changeItemBestVariant(this.selectedCharacterId, this.itemSlot!, item.id).subscribe(item => {
			this.equippableItem = item;
			this.itemChanged.emit(this.getItemChange());
		});
	}

	onEnchantChange(enchant: Enchant): void {
		const newItem:EquippableItem = {
			...this.equippableItem!,
			enchant: enchant
		};
		this.equipmentService.changeItem(this.selectedCharacterId, this.itemSlot!, newItem).subscribe(item => {
			this.equippableItem = item;
			this.enchantChanged.emit(this.getItemChange());
		});
	}

	onGemChange(socketIdx: number, gem: Gem): void {
		const newItem:EquippableItem = {
			...this.equippableItem!,
			gems: [...this.equippableItem!.gems]
		};

		newItem.gems[socketIdx] = gem;

		this.equipmentService.changeItem(this.selectedCharacterId, this.itemSlot!, newItem).subscribe(item => {
			this.equippableItem = item;
			this.gemChanged.emit(this.getItemChange());
		});
	}

	getItemOptions(): Item[] {
		return this.equipmentOptions?.itemOptions
				.find(x => x.itemSlot === this.itemSlot)?.items || [];
	}

	getEnchantOptions(): Enchant[] {
		return this.equipmentOptions?.enchantOptions
				.find(x => x.itemType === this.equippableItem!.item.itemType && x.itemSubType === this.equippableItem!.item.itemSubType)?.enchants || []
	}

	getGemOptions(socketIdx: number): Gem[] {
		return this.equipmentOptions?.gemOptions
				.find(x => x.socketType === this.equippableItem!.item.socketTypes[socketIdx])?.gems || []
	}

	onUpgradeCounterClick(): void {
		this.upgradeCounterClicked.emit(this.slotGroup);
	}

	private getItemChange(): ItemChange {
		return {
			itemSlot: this.itemSlot,
			item: this.equippableItem
		};
	}

	readonly itemComparator: ElementComparatorFn<Item> = (a, b) => a.name.localeCompare(b.name);

	readonly itemGroupComparator: GroupKeyComparatorFn<Item> = (a, b) => {
		const positionA = Object.keys(PhaseId).indexOf(PhaseId[a.firstAppearedInPhase.id]);
		const positionB = Object.keys(PhaseId).indexOf(PhaseId[b.firstAppearedInPhase.id]);

		return positionB - positionA;
	}

	readonly itemGroupToString: GroupKeyToStringFn<Item> = item => item.firstAppearedInPhase.name;

	readonly enchantComparator: ElementComparatorFn<Enchant> = (a, b) => a.name.localeCompare(b.name);

	readonly gemComparator: ElementComparatorFn<Gem> = (a, b) => {
		const aSourceIndex = EquipmentSlotEditorComponent.sourceIndex(a);
		const bSourceIndex = EquipmentSlotEditorComponent.sourceIndex(b);

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

	private static sourceIndex(gem: Gem): number {
		if (gem.source === 'Jewelcrafting' || gem.source === 'Enchanting') {
			return 1;
		}
		return 2;
	}

	getUpgradeLevel(changePct: number) {
		return changePct > 10 ? 5 :
			changePct > 5 ? 4 :
			changePct > 3 ? 3 :
			changePct > 1 ? 2 :
			1;
	}
}

class ItemFormatter implements DropdownSelectValueFormatter<Item> {
	formatElement(value: Item): string {
		return `
			<img src="${getIcon(value.icon)}"/>
			<span class="rarity-${value.rarity.toLowerCase()} item-header">&nbsp;${value.name}</span>
			<span class="item-descr" title="${value.detailedSource}">[${value.source}]</span>
			<span class="item-descr">${value.shortTooltip !== '' ? '(' + escapeHtml(value.shortTooltip) + ')' : ''}</span>
		`;
	}

	formatSelection(value: Item): string {
		return `
			<img src="${getIcon(value.icon)}"/>
			<span class="rarity-${value.rarity.toLowerCase()} item-header">&nbsp;${value.name}</span>
			<span class="item-descr" title="${value.detailedSource}">[${value.source}]</span>
		`;
	}

	formatTooltip(value?: Item):string {
		return value?.tooltip || "";
	}
}

class EnchantFormatter implements DropdownSelectValueFormatter<Enchant> {
	formatElement(value: Enchant): string {
		return `
			<img src="${getIcon(value.icon)}"/>
			<span class="rarity-${value.rarity.toLowerCase()} item-header">&nbsp;${value.name}</span>
		`;
	}

	formatSelection(value: Enchant): string {
		return `
			<img src="${getIcon(value.icon)}"/>
			<span class="rarity-${value.rarity.toLowerCase()} item-header">&nbsp;${value.name}</span>
		`;
	}

	formatTooltip(value?: Enchant):string {
		return value?.tooltip || "";
	}
}

class GemFormatter implements DropdownSelectValueFormatter<Gem> {
	formatElement(value: Gem): string {
		return `
			<img src="${getIcon(value.icon)}"/>
			<span class="rarity-${value.rarity.toLowerCase()} item-header">&nbsp;${value.name}</span>
		`;
	}

	formatSelection(value: Gem): string {
		return `
			<img src="${getIcon(value.icon)}"/>
			<span class="rarity-${value.rarity.toLowerCase()} item-header">&nbsp;${value.name}</span>
		`;
	}

	formatTooltip(value?: Gem):string {
		return value?.tooltip || "";
	}
}

function escapeHtml(value: string): string {
	return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
}
