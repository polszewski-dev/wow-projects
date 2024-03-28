import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
	selector: 'app-dropdown-select',
	templateUrl: './dropdown-select.component.html',
	styleUrls: ['./dropdown-select.component.css'],
	changeDetection: ChangeDetectionStrategy.OnPush
})
export class DropdownSelectComponent<T> {
	@Input() elements: T[] = [];
	@Input() selection?: T;
	@Output() selectionChanged = new EventEmitter<T>();

	readonly id: string;
	static idGen = 0;

	@Input() valueFormatter!: DropdownSelectValueFormatter<T>;
	@Input() elementComparator?: ElementComparatorFn<T>;
	@Input() groupKeyComparator? : GroupKeyComparatorFn<T>;
	@Input() groupKeyToString?: GroupKeyToStringFn<T>;

	constructor() {
		this.id = 'dropdown-select-' + ++DropdownSelectComponent.idGen;
	}

	onSelect(value: T) {
		this.selection = value;
		this.selectionChanged.emit(this.selection);
	}

	getItems() {
		const copy = [...this.elements];

		if (this.elementComparator) {
			copy.sort(this.elementComparator);
		}

		if (this.groupKeyComparator) {
			copy.sort(this.groupKeyComparator);
		}

		return this.createItems(copy);
	}

	private createItems(sortedElements: T[]) {
		const result: DropdownSelectItem<T>[] = [];
		let currentGroup = undefined;

		for (const element of sortedElements) {
			if (this.groupKeyToString !== undefined) {
				const elementGroup = this.groupKeyToString(element);

				if (currentGroup === undefined || currentGroup !== elementGroup) {
					currentGroup = elementGroup;
					result.push({
						type: 'group',
						name: currentGroup
					});
				}
			}

			result.push({
				type: 'value',
				value: element
			});
		}

		return result;
	}

	formatElement(value: T) {
		return this.valueFormatter.formatElement(value);
	}

	formatSelection(value?: T) {
		if (value === undefined || value === null) {
			return this.valueFormatter.emptySelection || '<i>-- empty --</i>';
		}
		return this.valueFormatter.formatSelection(value);
	}

	formatTooltip(value?: T) {
		return this.valueFormatter.formatTooltip(value);
	}
}

export type ElementComparatorFn<T> = (a: T, b: T) => number;
export type GroupKeyComparatorFn<T> = (a: T, b: T) => number;
export type GroupKeyToStringFn<T> = (a: T) => string;

export interface DropdownSelectValueFormatter<T> {
	formatElement: (value: T) => string;
	emptySelection?: string;
	formatSelection: (value: T) => string;
	formatTooltip: (value?: T) => string;
}

interface DropdownSelectGroup {
	type: 'group';
	name: string;
}

interface DropdownSelectValue<T> {
	type: 'value';
	value: T;
}

type DropdownSelectItem<T> = DropdownSelectGroup | DropdownSelectValue<T>;
