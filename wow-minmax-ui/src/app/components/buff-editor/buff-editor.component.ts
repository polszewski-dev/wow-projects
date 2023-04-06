import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { Buff } from 'src/app/model/buff/Buff';
import { BuffListType } from 'src/app/model/buff/BuffListType';
import { BuffService } from 'src/app/services/buff.service';

@Component({
	selector: 'app-buff-editor',
	templateUrl: './buff-editor.component.html',
	styleUrls: ['./buff-editor.component.css']
})
export class BuffEditorComponent implements OnChanges {
	@Input() selectedCharacterId!: string;
	@Input() buffListType!: BuffListType;
	@Output() buffsChanged = new EventEmitter<void>()

	buffs: Buff[] = [];

	constructor(private buffService: BuffService) {}

	ngOnChanges(changes: SimpleChanges): void {
		if (!changes['selectedCharacterId']) {
			return;
		}
		this.buffService.getBuffs(this.selectedCharacterId, this.buffListType).subscribe((buffs: Buff[]) => {
			this.buffs = buffs;
		});
	}

	onChange(buff: Buff): void {
		this.buffService.enableBuff(this.selectedCharacterId, this.buffListType, buff).subscribe((buffs: Buff[]) => {
			this.buffs = buffs;
			this.buffsChanged.emit();
		});
	}
}
