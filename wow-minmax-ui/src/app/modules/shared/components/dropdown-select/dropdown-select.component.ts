import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
	selector: 'app-dropdown-select',
	templateUrl: './dropdown-select.component.html',
	styleUrls: ['./dropdown-select.component.css'],
	changeDetection: ChangeDetectionStrategy.OnPush,
	providers: [
		{
			provide: NG_VALUE_ACCESSOR,
			useExisting: DropdownSelectComponent,
			multi: true
		}
	]
})
export class DropdownSelectComponent<T> implements ControlValueAccessor {
	@Input({ required: true }) id!: string;
	@Input({ required: true }) elements: T[] = [];
	@Input() value!: T | null | undefined;
	@Output() valueChanged = new EventEmitter<T>();

	@Input({ required: true }) valueFormatter!: DropdownSelectValueFormatter<T>;
	@Input() elementComparator?: ElementComparatorFn<T>;
	@Input() groupKeyComparator? : GroupKeyComparatorFn<T>;
	@Input() groupKeyToString?: GroupKeyToStringFn<T>;

	disabled: boolean = false;

	private onChange = (value: T): void => {};
	private onTouched = (): void => {};

	writeValue(obj: any): void {
		this.value = obj;
	}

	registerOnChange(fn: any): void {
		this.onChange = fn;
	}

	registerOnTouched(fn: any): void {
		this.onTouched = fn;
	}

	setDisabledState(isDisabled: boolean): void {
		this.disabled = isDisabled;
	}

	onSelect(value: T) {
		this.value = value;
		this.onChange(value);
		this.onBlur();
		this.valueChanged.emit(value);
	}

	onBlur() {
		this.onTouched();
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

	formatSelection(value: T | null | undefined) {
		if (value === undefined || value === null) {
			return this.valueFormatter.emptySelection || '<i>-- empty --</i>';
		}
		return this.valueFormatter.formatSelection(value);
	}

	formatTooltip(value: T | null | undefined) {
		if (value === undefined || value === null) {
			return '';
		}
		return this.valueFormatter.formatTooltip(value);
	}

	trackKey(item: DropdownSelectItem<T>) {
		if (item.type == 'group') {
			return "G@" + item.name;
		}
		return "V@" + this.valueFormatter.trackKey(item.value);
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
	trackKey: (value: T) => string;
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
