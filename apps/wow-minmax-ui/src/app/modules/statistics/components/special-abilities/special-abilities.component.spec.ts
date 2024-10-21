import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SpecialAbilitiesComponent } from './special-abilities.component';

describe('SpecialAbilitiesComponent', () => {
	let component: SpecialAbilitiesComponent;
	let fixture: ComponentFixture<SpecialAbilitiesComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ SpecialAbilitiesComponent ]
		})
		.compileComponents();

		fixture = TestBed.createComponent(SpecialAbilitiesComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
