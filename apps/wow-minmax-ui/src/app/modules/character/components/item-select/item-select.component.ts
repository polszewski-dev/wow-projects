import { Component, Input, OnInit } from '@angular/core';
import { Observable, map } from 'rxjs';
import { DropdownSelectValueFormatter, ElementComparatorFn, GroupKeyComparatorFn, GroupKeyToStringFn } from '../../../shared/components/dropdown-select/dropdown-select.component';
import { PhaseId } from '../../../shared/model/character/PhaseId';
import { getIcon } from '../../../shared/util/Icon';
import { EquippableItem } from '../../model/equipment/EquippableItem';
import { Item } from '../../model/equipment/Item';
import { ItemOptions } from '../../model/equipment/ItemOptions';
import { ItemSlot } from '../../model/equipment/ItemSlot';
import { CharacterStateService } from '../../services/character-state.service';
import { EquipmentOptionsStateService } from '../../services/equipment-options-state.service';

@Component({
	selector: 'app-item-select',
	templateUrl: './item-select.component.html',
	styleUrls: ['./item-select.component.css']
})
export class ItemSelectComponent implements OnInit {
	@Input({ required: true }) itemSlot!: ItemSlot;

	equippedItem$!: Observable<[EquippableItem | undefined]>;
	itemOptions$!: Observable<ItemOptions | undefined>;

	constructor(
		private characterStateService: CharacterStateService,
		private equipmentOptionsStateService: EquipmentOptionsStateService
	) {}

	ngOnInit(): void {
		this.equippedItem$ = this.characterStateService.itemSlotByType$(this.itemSlot).pipe(
			map(item => [ item ])
		);
		this.itemOptions$ = this.equipmentOptionsStateService.itemOptionsByItemSlot$(this.itemSlot);
	}

	readonly itemFormatter = new ItemFormatter();

	readonly itemComparator: ElementComparatorFn<Item> = (a, b) => a.name.localeCompare(b.name);

	readonly itemGroupComparator: GroupKeyComparatorFn<Item> = (a, b) => {
		const positionA = Object.keys(PhaseId).indexOf(PhaseId[a.firstAppearedInPhase.id]);
		const positionB = Object.keys(PhaseId).indexOf(PhaseId[b.firstAppearedInPhase.id]);

		return positionB - positionA;
	}

	readonly itemGroupToString: GroupKeyToStringFn<Item> = item => item.firstAppearedInPhase.name;

	onChange(item: Item) {
		this.characterStateService.equipItemBestVariant(this.itemSlot!, item);
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

	trackKey(value: Item) {
		return '' + value.id;
	}
}

function escapeHtml(value: string) {
	return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
}
