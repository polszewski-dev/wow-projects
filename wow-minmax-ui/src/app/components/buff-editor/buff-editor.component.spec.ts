import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BuffEditorComponent } from './buff-editor.component';

describe('BuffEditorComponent', () => {
	let component: BuffEditorComponent;
	let fixture: ComponentFixture<BuffEditorComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ BuffEditorComponent ]
		})
		.compileComponents();

		fixture = TestBed.createComponent(BuffEditorComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
