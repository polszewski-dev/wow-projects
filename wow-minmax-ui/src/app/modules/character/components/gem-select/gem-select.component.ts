import { Component, Input, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { DropdownSelectValueFormatter, ElementComparatorFn } from '../../../shared/components/dropdown-select/dropdown-select.component';
import { getIcon } from '../../../shared/util/Icon';
import { EquippableItem } from '../../model/equipment/EquippableItem';
import { Gem } from '../../model/equipment/Gem';
import { GemColor } from '../../model/equipment/GemColor';
import { GemOptions, getMatchingGemOptions } from '../../model/equipment/GemOptions';
import { ItemRarity } from '../../model/equipment/ItemRarity';
import { ItemSlot } from '../../model/equipment/ItemSlot';
import { CharacterStateService } from '../../services/character-state.service';
import { EquipmentOptionsStateService } from '../../services/equipment-options-state.service';

@Component({
	selector: 'app-gem-select',
	templateUrl: './gem-select.component.html',
	styleUrls: ['./gem-select.component.css']
})
export class GemSelectComponent implements OnInit {
	@Input() itemSlot!: ItemSlot;
	@Input() socketNo!: number;

	equippedItem$!: Observable<EquippableItem | undefined>;
	gemOptions$ = this.equipmentOptionsStateService.gemOptions$;

	constructor(
		private characterStateService: CharacterStateService,
		private equipmentOptionsStateService: EquipmentOptionsStateService
	) {}

	ngOnInit(): void {
		this.equippedItem$ = this.characterStateService.itemSlotByType$(this.itemSlot);
	}

	getGemOptions(gemOptions: GemOptions[], equippedItem: EquippableItem) {
		return getMatchingGemOptions(gemOptions, equippedItem, this.socketNo);
	}

	readonly gemFormatter = new GemFormatter();

	readonly gemComparator: ElementComparatorFn<Gem> = (a, b) => {
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

	onGemChange(gem: Gem) {
		this.characterStateService.equipGem(this.itemSlot, this.socketNo, gem);
	}
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
