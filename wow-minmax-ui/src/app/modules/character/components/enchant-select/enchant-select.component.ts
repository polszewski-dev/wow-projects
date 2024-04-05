import { Component, Input, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { DropdownSelectValueFormatter, ElementComparatorFn } from '../../../shared/components/dropdown-select/dropdown-select.component';
import { getIcon } from '../../../shared/util/Icon';
import { Enchant } from '../../model/equipment/Enchant';
import { EnchantOptions, getMatchingEnchantOptions } from '../../model/equipment/EnchantOptions';
import { EquippableItem } from '../../model/equipment/EquippableItem';
import { ItemSlot } from '../../model/equipment/ItemSlot';
import { CharacterStateService } from '../../services/character-state.service';
import { EquipmentOptionsStateService } from '../../services/equipment-options-state.service';

@Component({
	selector: 'app-enchant-select',
	templateUrl: './enchant-select.component.html',
	styleUrls: ['./enchant-select.component.css']
})
export class EnchantSelectComponent implements OnInit {
	@Input() itemSlot!: ItemSlot;

	equippedItem$!: Observable<EquippableItem | undefined>;
	enchantOptions$ = this.equipmentOptionsStateService.enchantOptions$;

	constructor(
		private characterStateService: CharacterStateService,
		private equipmentOptionsStateService: EquipmentOptionsStateService
	) {}

	ngOnInit(): void {
		this.equippedItem$ = this.characterStateService.itemSlotByType$(this.itemSlot);
	}

	getEnchantOptions(enchantOptions: EnchantOptions[], equippedItem: EquippableItem) {
		return getMatchingEnchantOptions(enchantOptions, equippedItem);
	}

	readonly enchantFormatter = new EnchantFormatter();

	readonly enchantComparator: ElementComparatorFn<Enchant> = (a, b) => a.name.localeCompare(b.name);

	onChange(enchant: Enchant) {
		this.characterStateService.equipEnchant(this.itemSlot, enchant);
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
