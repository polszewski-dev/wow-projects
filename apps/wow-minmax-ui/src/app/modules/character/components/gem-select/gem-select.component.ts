import { Component, Input, OnInit } from '@angular/core';
import { createSelector, Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { DropdownSelectValueFormatter, ElementComparatorFn } from '../../../shared/components/dropdown-select/dropdown-select.component';
import { getIcon } from '../../../shared/util/Icon';
import { EquippableItem } from '../../model/equipment/EquippableItem';
import { Gem } from '../../model/equipment/Gem';
import { GemColor } from '../../model/equipment/GemColor';
import { getMatchingGemOptions } from '../../model/equipment/GemOptions';
import { ItemRarity } from '../../model/equipment/ItemRarity';
import { ItemSlot } from '../../model/equipment/ItemSlot';
import { CharacterModuleState } from '../../state/character-module.state';
import { equipGem } from '../../state/character/character.actions';
import { selectCharacterId, selectEquipmentSlot } from '../../state/character/character.selectors';
import { selectGemOptions } from '../../state/equipment-options/equipment-options.selectors';

@Component({
	selector: 'app-gem-select',
	templateUrl: './gem-select.component.html',
	styleUrls: ['./gem-select.component.css']
})
export class GemSelectComponent implements OnInit {
	@Input({ required: true }) itemSlot!: ItemSlot;
	@Input({ required: true }) socketNo!: number;

	data$!: Observable<DataView>;

	constructor(private store: Store<CharacterModuleState>) {}

	ngOnInit(): void {
		this.data$ = this.store.select(createDataSelector(this.itemSlot, this.socketNo));
	}

	onGemChange(characterId: string, equippedItem: EquippableItem, gem: Gem) {
		this.store.dispatch(equipGem({ characterId, equippedItem, itemSlot: this.itemSlot, socketNo: this.socketNo, gem }));
	}

	readonly gemFormatter = new GemFormatter();
	readonly gemComparator = gemComparator;
}

type DataView = {
	characterId: string;
    equippedItem: EquippableItem;
    gemOptions: Gem[];
} | null;

function createDataSelector(itemSlot: ItemSlot, socketNo: number) {
	return createSelector(
		selectCharacterId,
		selectEquipmentSlot(itemSlot),
		selectGemOptions,
		(characterId, equippedItem, gemOptions): DataView => {
			if (!characterId || !equippedItem || !(socketNo < equippedItem.gems.length)) {
				return null;
			}

			return {
				characterId,
				equippedItem,
				gemOptions: getMatchingGemOptions(gemOptions, equippedItem, socketNo)
			};
		}
	);
}

const gemComparator: ElementComparatorFn<Gem> = (a, b) => {
	const aSourceIndex = sourceIndex(a);
	const bSourceIndex = sourceIndex(b);

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

function sourceIndex(gem: Gem) {
	if (gem.source === 'Jewelcrafting' || gem.source === 'Enchanting') {
		return 1;
	}
	return 2;
}

class GemFormatter implements DropdownSelectValueFormatter<Gem> {
	formatElement(value: Gem) {
		return `
			<img src="${getIcon(value.icon)}"/>
			<span class="rarity-${value.rarity.toLowerCase()} item-header">&nbsp;${value.name}</span>
		`;
	}

	formatSelection(value: Gem) {
		return `
			<img src="${getIcon(value.icon)}"/>
			<span class="rarity-${value.rarity.toLowerCase()} item-header">&nbsp;${value.name}</span>
		`;
	}

	formatTooltip(value?: Gem) {
		return value?.tooltip || "";
	}

	trackKey(value: Gem) {
		return '' + value.id;
	}
}
