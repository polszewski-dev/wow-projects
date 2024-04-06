import { Component, EventEmitter, Input, Output } from '@angular/core';
import { DropdownSelectValueFormatter, ElementComparatorFn } from '../../../shared/components/dropdown-select/dropdown-select.component';
import { getIcon } from '../../../shared/util/Icon';
import { EquipmentOptions } from '../../model/equipment/EquipmentOptions';
import { EquippableItem } from '../../model/equipment/EquippableItem';
import { Gem } from '../../model/equipment/Gem';
import { GemColor } from '../../model/equipment/GemColor';
import { ItemChange, newItemChange } from '../../model/equipment/ItemChange';
import { ItemRarity } from '../../model/equipment/ItemRarity';
import { ItemSlot } from '../../model/equipment/ItemSlot';
import { EquipmentService } from '../../services/equipment.service';

@Component({
	selector: 'app-gem-select',
	templateUrl: './gem-select.component.html',
	styleUrls: ['./gem-select.component.css']
})
export class GemSelectComponent {
	@Input() selectedCharacterId!: string;
	@Input() itemSlot!: ItemSlot;
	@Input() equippableItem!: EquippableItem;
	@Input() equipmentOptions?: EquipmentOptions;
	@Input() socketNo!: number;
	@Output() changed = new EventEmitter<ItemChange>();

	readonly gemFormatter = new GemFormatter();

	constructor(private equipmentService: EquipmentService) {}

	getGemOptions(socketIdx: number) {
		return this.equipmentOptions?.gemOptions
				.find(x => x.socketType === this.equippableItem!.item.socketTypes[socketIdx])?.gems || []
	}

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

	onGemChange(socketIdx: number, gem: Gem) {
		const newItem: EquippableItem = {
			...this.equippableItem!,
			gems: [...this.equippableItem!.gems]
		};

		newItem.gems[socketIdx] = gem;

		this.equipmentService.changeItem(this.selectedCharacterId, this.itemSlot!, newItem).subscribe(item => {
			this.equippableItem = item;
			this.changed.emit(newItemChange(this.itemSlot, item));
		});
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
}
