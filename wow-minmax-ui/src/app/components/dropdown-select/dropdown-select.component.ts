import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { DropdownSelectValueFormatter } from './DropdownSelectValueFormatter';

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
	static idGen: number = 0;

	@Input() valueFormatter!: DropdownSelectValueFormatter<T>;

	constructor() {
		this.id = 'dropdown-select-' + ++DropdownSelectComponent.idGen;
	}

	onSelect(value: T): void {
		this.selection = value;
		this.selectionChanged.emit(this.selection);
	}

	formatElement(value: T): string {
		return this.valueFormatter.formatElement(value);
	}

	formatSelection(value?: T): string {
		if (value == undefined) {
			return this.valueFormatter.emptySelection;
		}
		return this.valueFormatter.formatSelection(value);
	}

	formatTooltip(value?: T): string {
		return this.valueFormatter.formatTooltip(value);
	}
}
