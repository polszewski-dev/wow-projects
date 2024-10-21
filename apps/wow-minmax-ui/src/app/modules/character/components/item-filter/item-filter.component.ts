import { Component } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ItemFilter } from '../../model/equipment/ItemFilter';
import { EquipmentOptionsStateService } from '../../services/equipment-options-state.service';
import { UpgradeStateService } from '../../services/upgrade-state.service';

@Component({
	selector: 'app-item-filter',
	templateUrl: './item-filter.component.html',
	styleUrls: ['./item-filter.component.css']
})
export class ItemFilterComponent {
	readonly form = this.formBuilder.nonNullable.group({
		heroics: false,
		raids: false,
		worldBosses: false,
		pvpItems: false,
		greens: false,
		legendaries: false
	});

	constructor(
		private upgradeStateService: UpgradeStateService,
		private equipmentOptionsStateService: EquipmentOptionsStateService,
		private formBuilder: FormBuilder
	) {}

	ngOnInit(): void {
		this.form.setValue(this.upgradeStateService.itemFilterSnapshot);

		this.form.valueChanges.subscribe(value => {
			this.upgradeStateService.updateItemFilter(value);
		});
	}

	get heroics() {
		return this.equipmentOptionsStateService.equipmentOptionsSnapshot!.heroics;
	}
}
