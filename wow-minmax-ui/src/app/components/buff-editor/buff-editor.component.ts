import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { Buff } from 'src/app/model/buff/Buff';
import { ProfileInfo } from 'src/app/model/ProfileInfo';
import { BuffService } from 'src/app/services/buff.service';

@Component({
	selector: 'app-buff-editor',
	templateUrl: './buff-editor.component.html',
	styleUrls: ['./buff-editor.component.css']
})
export class BuffEditorComponent implements OnChanges {
	@Input() selectedProfileId!: string;
	@Output() buffsChanged = new EventEmitter<void>()

	buffs: Buff[] = [];

	constructor(private buffService: BuffService) {}

	ngOnChanges(changes: SimpleChanges): void {
		if (!changes['selectedProfileId']) {
			return;
		}
		this.buffService.getBuffs(this.selectedProfileId).subscribe((buffs: Buff[]) => {
			this.buffs = buffs;
		});
	}

	onChange(buff: Buff): void {
		this.buffService.changeBuff(this.selectedProfileId, buff).subscribe((buffs: Buff[]) => {
			this.buffs = buffs;
			this.buffsChanged.emit();
		});
	}
}
