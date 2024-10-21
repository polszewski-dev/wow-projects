import { Component, Input, OnInit } from '@angular/core';
import { Observable, filter, map } from 'rxjs';
import { Buff } from '../../model/buff/Buff';
import { BuffListType } from '../../model/buff/BuffListType';
import { CharacterStateService } from '../../services/character-state.service';

@Component({
	selector: 'app-buff-editor',
	templateUrl: './buff-editor.component.html',
	styleUrls: ['./buff-editor.component.css']
})
export class BuffEditorComponent implements OnInit {
	@Input({ required: true }) buffListType!: BuffListType;

	buffs$!: Observable<Buff[] | undefined>;

	constructor(private characterStateService: CharacterStateService) {}

	ngOnInit(): void {
		this.buffs$ = this.characterStateService.buffListByType$(this.buffListType);
	}

	onChange(buff: Buff) {
		this.characterStateService.enableBuff(this.buffListType, buff);
	}
}
