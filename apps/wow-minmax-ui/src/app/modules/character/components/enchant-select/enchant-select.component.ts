import { Component, Input, OnInit } from '@angular/core';
import { createSelector, Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { DropdownSelectValueFormatter, ElementComparatorFn } from '../../../shared/components/dropdown-select/dropdown-select.component';
import { getIcon } from '../../../shared/util/Icon';
import { Enchant } from '../../model/equipment/Enchant';
import { getMatchingEnchantOptions } from '../../model/equipment/EnchantOptions';
import { EquippableItem } from '../../model/equipment/EquippableItem';
import { ItemSlot } from '../../model/equipment/ItemSlot';
import { CharacterModuleState } from '../../state/character-module.state';
import { equipEnchant } from '../../state/character/character.actions';
import { selectCharacterId, selectEquipmentSlot } from '../../state/character/character.selectors';
import { selectEnchantOptions } from '../../state/equipment-options/equipment-options.selectors';

@Component({
	selector: 'app-enchant-select',
	templateUrl: './enchant-select.component.html',
	styleUrls: ['./enchant-select.component.css']
})
export class EnchantSelectComponent implements OnInit {
	@Input({ required: true }) itemSlot!: ItemSlot;

	data$!: Observable<DataView>;

	constructor(private store: Store<CharacterModuleState>) {}

	ngOnInit(): void {
		this.data$ = this.store.select(createDataSelector(this.itemSlot));
	}

	onChange(characterId: string, equippedItem: EquippableItem, enchant: Enchant) {
		this.store.dispatch(equipEnchant({ characterId, equippedItem, itemSlot: this.itemSlot, enchant }));
	}

	readonly enchantFormatter = new EnchantFormatter();
	readonly enchantComparator = enchantComparator;
}

type DataView = {
	characterId: string;
	equippedItem: EquippableItem;
	enchantOptions: Enchant[];
} | null;

function createDataSelector(itemSlot: ItemSlot) {
	return createSelector(
		selectCharacterId,
		selectEquipmentSlot(itemSlot),
		selectEnchantOptions,
		(characterId, equippedItem, enchantOptions): DataView => {
			if (!characterId || !equippedItem) {
				return null;
			}

			const filteredEnchantOptions = getMatchingEnchantOptions(enchantOptions, equippedItem);

			if (filteredEnchantOptions.length == 0) {
				return null;
			}

			return {
				characterId,
				equippedItem,
				enchantOptions: filteredEnchantOptions
			};
		}
	)
}

const enchantComparator: ElementComparatorFn<Enchant> = (a, b) => a.name.localeCompare(b.name);

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

	trackKey(value: Enchant) {
		return '' + value.id;
	}
}
