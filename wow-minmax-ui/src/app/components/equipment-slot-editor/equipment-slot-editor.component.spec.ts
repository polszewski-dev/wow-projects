import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EquipmentSlotEditorComponent } from './equipment-slot-editor.component';

describe('EquipmentSlotEditorComponent', () => {
	let component: EquipmentSlotEditorComponent;
	let fixture: ComponentFixture<EquipmentSlotEditorComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ EquipmentSlotEditorComponent ]
		})
		.compileComponents();

		fixture = TestBed.createComponent(EquipmentSlotEditorComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
