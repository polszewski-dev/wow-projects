import { Component, Input } from '@angular/core';
import { Store } from '@ngrx/store';
import { DropdownSelectValueFormatter } from 'src/app/modules/shared/components/dropdown-select/dropdown-select.component';
import { ScriptInfo } from '../../model/ScriptInfo';
import { CharacterModuleState } from '../../state/character-module.state';
import { changeScript } from '../../state/character/character.actions';

@Component({
	selector: 'app-script-select',
	templateUrl: './script-select.component.html',
	styleUrl: './script-select.component.css'
})
export class ScriptSelectComponent {
	@Input({ required: true }) id!: string;
	@Input({ required: true }) characterId!: string;
	@Input({ required: true }) selectedScript!: ScriptInfo;
	@Input({ required: true }) availableScripts!: ScriptInfo[];

	constructor(private store: Store<CharacterModuleState>) {}

	onScriptChanged(script: ScriptInfo) {
		const characterId = this.characterId;

		this.store.dispatch(changeScript({ characterId, script }));
	}

	readonly scriptFormatter = new ScriptFormatter();
}

class ScriptFormatter implements DropdownSelectValueFormatter<ScriptInfo> {
	emptySelection = "--select--";

	formatSelection(value: ScriptInfo) {
		return '<strong>' + value.name + '</strong>';
	}

	formatElement(value: ScriptInfo) {
		return value.name;
	}

	formatTooltip(value?: ScriptInfo) {
		return value?.content || '';
	}

	trackKey(value: ScriptInfo) {
		return value.id;
	}
}
