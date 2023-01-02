import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DpsBarComponent } from './dps-bar.component';

describe('DpsBarComponent', () => {
	let component: DpsBarComponent;
	let fixture: ComponentFixture<DpsBarComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ DpsBarComponent ]
		})
		.compileComponents();

		fixture = TestBed.createComponent(DpsBarComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
