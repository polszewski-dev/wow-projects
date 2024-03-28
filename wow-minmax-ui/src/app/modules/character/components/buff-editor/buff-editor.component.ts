import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { Buff } from '../../model/buff/Buff';
import { BuffListType } from '../../model/buff/BuffListType';
import { BuffService } from '../../services/buff.service';

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
		this.buffService.getBuffs(this.selectedCharacterId, this.buffListType).subscribe(buffs => {
			this.buffs = buffs;
		});
	}

	onChange(buff: Buff) {
		this.buffService.enableBuff(this.selectedCharacterId, this.buffListType, buff).subscribe(buffs => {
			this.buffs = buffs;
			this.buffsChanged.emit();
		});
	}
}
