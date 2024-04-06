import { Component, EventEmitter, Input, Output } from '@angular/core';
import { DropdownSelectValueFormatter, ElementComparatorFn, GroupKeyComparatorFn, GroupKeyToStringFn } from '../../../shared/components/dropdown-select/dropdown-select.component';
import { PhaseId } from '../../../shared/model/character/PhaseId';
import { getIcon } from '../../../shared/util/Icon';
import { EquipmentOptions } from '../../model/equipment/EquipmentOptions';
import { EquippableItem } from '../../model/equipment/EquippableItem';
import { Item } from '../../model/equipment/Item';
import { ItemChange, newItemChange } from '../../model/equipment/ItemChange';
import { ItemSlot } from '../../model/equipment/ItemSlot';
import { EquipmentService } from '../../services/equipment.service';

@Component({
	selector: 'app-item-select',
	templateUrl: './item-select.component.html',
	styleUrls: ['./item-select.component.css']
})
export class ItemSelectComponent {
	@Input() selectedCharacterId!: string;
	@Input() itemSlot!: ItemSlot;
	@Input() equippableItem?: EquippableItem;
	@Input() equipmentOptions?: EquipmentOptions;
	@Output() changed = new EventEmitter<ItemChange>();

	readonly itemFormatter = new ItemFormatter();

	constructor(private equipmentService: EquipmentService) {}

	get itemOptions() {
		return this.equipmentOptions?.itemOptions
				.find(x => x.itemSlot === this.itemSlot)?.items || [];
	}

	readonly itemComparator: ElementComparatorFn<Item> = (a, b) => a.name.localeCompare(b.name);

	readonly itemGroupComparator: GroupKeyComparatorFn<Item> = (a, b) => {
		const positionA = Object.keys(PhaseId).indexOf(PhaseId[a.firstAppearedInPhase.id]);
		const positionB = Object.keys(PhaseId).indexOf(PhaseId[b.firstAppearedInPhase.id]);

		return positionB - positionA;
	}

	readonly itemGroupToString: GroupKeyToStringFn<Item> = item => item.firstAppearedInPhase.name;

	onChange(item: Item) {
		this.equipmentService.changeItemBestVariant(this.selectedCharacterId, this.itemSlot!, item.id).subscribe(item => {
			this.equippableItem = item;
			this.changed.emit(newItemChange(this.itemSlot, item));
		});
	}
}

class ItemFormatter implements DropdownSelectValueFormatter<Item> {
	formatElement(value: Item) {
		return `
			<img src="${getIcon(value.icon)}"/>
			<span class="rarity-${value.rarity.toLowerCase()} item-header">&nbsp;${value.name}</span>
			<span class="item-descr" title="${value.detailedSource}">[${value.source}]</span>
			<span class="item-descr">${value.shortTooltip !== '' ? '(' + escapeHtml(value.shortTooltip) + ')' : ''}</span>
		`;
	}

	formatSelection(value: Item) {
		return `
			<img src="${getIcon(value.icon)}"/>
			<span class="rarity-${value.rarity.toLowerCase()} item-header">&nbsp;${value.name}</span>
			<span class="item-descr" title="${value.detailedSource}">[${value.source}]</span>
		`;
	}

	formatTooltip(value?: Item) {
		return value?.tooltip || "";
	}
}

function escapeHtml(value: string) {
	return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
}
