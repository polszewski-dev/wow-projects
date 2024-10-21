import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GemSelectComponent } from './gem-select.component';

describe('GemSelectComponent', () => {
	let component: GemSelectComponent;
	let fixture: ComponentFixture<GemSelectComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ GemSelectComponent ]
		})
		.compileComponents();

		fixture = TestBed.createComponent(GemSelectComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
