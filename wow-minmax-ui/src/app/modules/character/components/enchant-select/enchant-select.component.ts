import { Component, EventEmitter, Input, Output } from '@angular/core';
import { DropdownSelectValueFormatter, ElementComparatorFn } from '../../../shared/components/dropdown-select/dropdown-select.component';
import { getIcon } from '../../../shared/util/Icon';
import { Enchant } from '../../model/equipment/Enchant';
import { EnchantOptions } from '../../model/equipment/EnchantOptions';
import { EquipmentOptions } from '../../model/equipment/EquipmentOptions';
import { EquippableItem } from '../../model/equipment/EquippableItem';
import { Item } from '../../model/equipment/Item';
import { ItemChange, newItemChange } from '../../model/equipment/ItemChange';
import { ItemSlot } from '../../model/equipment/ItemSlot';
import { EquipmentService } from '../../services/equipment.service';

@Component({
	selector: 'app-enchant-select',
	templateUrl: './enchant-select.component.html',
	styleUrls: ['./enchant-select.component.css']
})
export class EnchantSelectComponent {
	@Input() selectedCharacterId!: string;
	@Input() itemSlot!: ItemSlot;
	@Input() equippableItem!: EquippableItem;
	@Input() equipmentOptions?: EquipmentOptions;
	@Output() changed = new EventEmitter<ItemChange>();

	readonly enchantFormatter = new EnchantFormatter();

	constructor(private equipmentService: EquipmentService) {}

	get enchantOptions() {
		return this.equipmentOptions?.enchantOptions.find(x => isMatchingEnchant(x, this.equippableItem.item))?.enchants || []
	}

	readonly enchantComparator: ElementComparatorFn<Enchant> = (a, b) => a.name.localeCompare(b.name);

	onChange(enchant: Enchant) {
		const newItem: EquippableItem = {
			...this.equippableItem!,
			enchant: enchant
		};
		this.equipmentService.changeItem(this.selectedCharacterId, this.itemSlot, newItem).subscribe(item => {
			this.equippableItem = item;
			this.changed.emit(newItemChange(this.itemSlot, item));
		});
	}
}

class EnchantFormatter implements DropdownSelectValueFormatter<Enchant> {
	formatElement(value: Enchant) {
		return `
			<img src="${getIcon(value.icon)}"/>
			<span class="rarity-${value.rarity.toLowerCase()} item-header">&nbsp;${value.name}</span>
		`;
	}

	formatSelection(value: Enchant) {
		return `
			<img src="${getIcon(value.icon)}"/>
			<span class="rarity-${value.rarity.toLowerCase()} item-header">&nbsp;${value.name}</span>
		`;
	}

	formatTooltip(value?: Enchant) {
		return value?.tooltip || "";
	}
}

function isMatchingEnchant(x: EnchantOptions, item: Item) {
	return x.itemType === item.itemType && x.itemSubType === item.itemSubType
}
