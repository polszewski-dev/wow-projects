import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Description } from '../../../shared/model/Description';
import { OptionGroup } from '../../model/OptionGroup';
import { OptionStatus } from '../../model/OptionStatus';

@Component({
	selector: 'app-option-select',
	templateUrl: './option-select.component.html',
	styleUrl: './option-select.component.css'
})
export class OptionSelectComponent<T extends Description & { id: number }> {
	@Input({ required: true })
	group!: OptionGroup<T>;

	@Input({ required: true })
	elementPrefix!: string;

	@Output()
	changed = new EventEmitter<OptionStatus<T>>()

	onChange(option: OptionStatus<T>) {
		this.changed.emit(option);
	}
}
