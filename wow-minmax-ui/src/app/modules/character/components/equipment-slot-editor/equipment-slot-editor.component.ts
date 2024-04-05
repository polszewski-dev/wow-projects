import { Component, Input, OnInit, TemplateRef, ViewChild, ViewContainerRef } from '@angular/core';
import { ItemSlot } from '../../model/equipment/ItemSlot';
import { getItemSlotGroup } from '../../model/upgrade/ItemSlotGroup';

@Component({
	selector: 'app-equipment-slot-editor',
	templateUrl: './equipment-slot-editor.component.html',
	styleUrls: ['./equipment-slot-editor.component.css']
})
export class EquipmentSlotEditorComponent implements OnInit {
	@Input() itemSlot!: ItemSlot;
	@Input() visible = true;

	@ViewChild('template', { static: true }) template!: TemplateRef<any>;

	constructor(private viewContainerRef: ViewContainerRef) {}

	ngOnInit(): void {
		this.viewContainerRef.createEmbeddedView(this.template);
	}

	get slotGroup() {
		return getItemSlotGroup(this.itemSlot);
	}
}
