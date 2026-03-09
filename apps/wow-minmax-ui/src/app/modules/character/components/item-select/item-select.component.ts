import { Component, Input, OnInit } from '@angular/core';
import { createSelector, Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { DropdownSelectValueFormatter, ElementComparatorFn, GroupKeyComparatorFn, GroupKeyToStringFn } from '../../../shared/components/dropdown-select/dropdown-select.component';
import { PhaseId } from '../../../shared/model/character/PhaseId';
import { getIcon } from '../../../shared/util/Icon';
import { EquippableItem } from '../../model/equipment/EquippableItem';
import { Item } from '../../model/equipment/Item';
import { ItemSlot } from '../../model/equipment/ItemSlot';
import { CharacterModuleState } from '../../state/character-module.state';
import { equipItemBestVariant } from '../../state/character/character.actions';
import { selectPlayerId, selectEquipmentSlot } from '../../state/character/character.selectors';
import { selectItemOptions } from '../../state/equipment-options/equipment-options.selectors';

@Component({
	selector: 'app-item-select',
	templateUrl: './item-select.component.html',
	styleUrls: ['./item-select.component.css']
})
export class ItemSelectComponent implements OnInit {
	@Input({ required: true }) itemSlot!: ItemSlot;

	data$!: Observable<DataView>;

	constructor(private store: Store<CharacterModuleState>) {}

	ngOnInit(): void {
		this.data$ = this.store.select(createDataSelector(this.itemSlot));
	}

	onChange(playerId: string, item: Item) {
		this.store.dispatch(equipItemBestVariant({ playerId, itemSlot: this.itemSlot, item }));
	}

	readonly itemFormatter = new ItemFormatter();
	readonly itemComparator = itemComparator;
	readonly itemGroupComparator = itemGroupComparator;
	readonly itemGroupToString = itemGroupToString;
}

type DataView = {
	playerId: string;
    equippedItem: EquippableItem | null;
    itemOptions: Item[];
} | null;

function createDataSelector(itemSlot: ItemSlot) {
	return createSelector(
		selectPlayerId,
		selectEquipmentSlot(itemSlot),
		selectItemOptions(itemSlot),
		(playerId, equippedItem, itemOptions): DataView => {
			if (!playerId) {
				return null;
			}

			return {
				playerId,
				equippedItem,
				itemOptions: itemOptions?.items || []
			};
		}
	);
}

const itemComparator: ElementComparatorFn<Item> = (a, b) => a.name.localeCompare(b.name);

const itemGroupComparator: GroupKeyComparatorFn<Item> = (a, b) => {
	const positionA = Object.keys(PhaseId).indexOf(PhaseId[a.firstAppearedInPhase.id]);
	const positionB = Object.keys(PhaseId).indexOf(PhaseId[b.firstAppearedInPhase.id]);

	return positionB - positionA;
}

const itemGroupToString: GroupKeyToStringFn<Item> = item => item.firstAppearedInPhase.name;

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
