import { Component, Input } from '@angular/core';
import { Store } from '@ngrx/store';
import { CharacterModuleState } from '../../state/character-module.state';
import { changeTalentLink } from '../../state/character/character.actions';

@Component({
	selector: 'app-talent-link-select',
	templateUrl: './talent-link-select.component.html',
	styleUrl: './talent-link-select.component.css'
})
export class TalentLinkSelectComponent {
	@Input({ required: true }) id!: string;
	@Input({ required: true }) characterId!: string;
	@Input({ required: true }) talentLink!: string;

	constructor(private store: Store<CharacterModuleState>) {}

	changeTalentLink() {
		let newTalentLink = prompt("Please enter new talent link:", this.talentLink);

		if (!newTalentLink) {
			return;
		}

		if (!newTalentLink.startsWith('https://www.wowhead.com/')) {
			alert('Valid link to Wowhead Talent Calculator is required');
			return;
		}

		this.store.dispatch(changeTalentLink({
			characterId: this.characterId,
			talentLink: newTalentLink
		}));
	}
}
