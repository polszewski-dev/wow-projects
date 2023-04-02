import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { Buff } from 'src/app/model/buff/Buff';
import { BuffService } from 'src/app/services/buff.service';

@Component({
	selector: 'app-buff-editor',
	templateUrl: './buff-editor.component.html',
	styleUrls: ['./buff-editor.component.css']
})
export class BuffEditorComponent implements OnChanges {
	@Input() selectedCharacterId!: string;
	@Output() buffsChanged = new EventEmitter<void>()

	buffs: Buff[] = [];

	constructor(private buffService: BuffService) {}

	ngOnChanges(changes: SimpleChanges): void {
		if (!changes['selectedCharacterId']) {
			return;
		}
		this.buffService.getBuffs(this.selectedCharacterId).subscribe((buffs: Buff[]) => {
			this.buffs = buffs;
		});
	}

	onChange(buff: Buff): void {
		this.buffService.changeBuff(this.selectedCharacterId, buff).subscribe((buffs: Buff[]) => {
			this.buffs = buffs;
			this.buffsChanged.emit();
		});
	}
}
