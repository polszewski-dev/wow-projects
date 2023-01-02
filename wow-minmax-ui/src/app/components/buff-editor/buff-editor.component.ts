import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { Buff } from 'src/app/model/buff/Buff';
import { ProfileInfo } from 'src/app/model/ProfileInfo';
import { BuffService as BuffService } from 'src/app/services/buff.service';

@Component({
	selector: 'app-buff-editor',
	templateUrl: './buff-editor.component.html',
	styleUrls: ['./buff-editor.component.css']
})
export class BuffEditorComponent implements OnChanges {
	@Input() selectedProfile?: ProfileInfo;
	@Output() buffsChanged = new EventEmitter<void>()

	buffs: Buff[] = [];

	constructor(private buffService: BuffService) {}

	ngOnChanges(changes: SimpleChanges) {
		if (!changes['selectedProfile']) {
			return;
		}
		this.buffService.getBuffs(this.selectedProfile!.profileId).subscribe((buffs: Buff[]) => {
			this.buffs = buffs;
		});
	}

	onChange(buff: Buff) {
		this.buffService.changeBuff(this.selectedProfile!.profileId, buff).subscribe((buffs: Buff[]) => {
			this.buffs = buffs;
			this.buffsChanged.emit();
		});
	}
}
