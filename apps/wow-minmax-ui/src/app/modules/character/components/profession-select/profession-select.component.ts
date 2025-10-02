import { Component, Input } from '@angular/core';
import { Store } from '@ngrx/store';
import { DropdownSelectValueFormatter } from 'src/app/modules/shared/components/dropdown-select/dropdown-select.component';
import { getIcon } from 'src/app/modules/shared/util/Icon';
import { Profession } from '../../model/Profession';
import { CharacterModuleState } from '../../state/character-module.state';
import { changeProfession } from '../../state/character/character.actions';
import { selectCharacter } from '../../state/character/character.selectors';

@Component({
	selector: 'app-profession-select',
	templateUrl: './profession-select.component.html',
	styleUrl: './profession-select.component.css'
})
export class ProfessionSelectComponent {
	@Input({ required: true }) id!: string;
	@Input({ required: true }) characterId!: string;
	@Input({ required: true }) professionIdx!: number;
	@Input({ required: true }) selectedProfession!: Profession | null;
	@Input({ required: true }) availableProfessions!: Profession[];

	character$ = this.store.select(selectCharacter);
	
	constructor(private store: Store<CharacterModuleState>) {}

	onProfessionChanged(profession: Profession) {
		const characterId = this.characterId;
		const professionIdx = this.professionIdx;

		this.store.dispatch(changeProfession({ characterId, professionIdx, profession }));
	}

	readonly professionFormatter = new ProfessionFormatter();
}

class ProfessionFormatter implements DropdownSelectValueFormatter<Profession> {
	emptySelection = "--select--";

	formatSelection(value: Profession) {
		return `<img src="${getIcon(value.icon)}" class="me-2"/><span>${value.name}</span>`;
	}

	formatElement(value: Profession) {
		const indentClass = !!value.specializationId ? 'ms-3': '';

		return `<img src="${getIcon(value.icon)}" class="me-2 ${indentClass}"/><span>${value.name}</span>`;
	}

	formatTooltip(value?: Profession) {
		return value?.tooltip || '';
	}

	trackKey(value: Profession) {
		return value.specializationId || value.id;
	}
}
