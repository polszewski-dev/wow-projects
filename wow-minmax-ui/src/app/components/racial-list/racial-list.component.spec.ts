import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RacialListComponent } from './racial-list.component';

describe('RacialListComponent', () => {
	let component: RacialListComponent;
	let fixture: ComponentFixture<RacialListComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ RacialListComponent ]
		})
		.compileComponents();

		fixture = TestBed.createComponent(RacialListComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
