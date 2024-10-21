import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EnchantSelectComponent } from './enchant-select.component';

describe('EnchantSelectComponent', () => {
	let component: EnchantSelectComponent;
	let fixture: ComponentFixture<EnchantSelectComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ EnchantSelectComponent ]
		})
		.compileComponents();

		fixture = TestBed.createComponent(EnchantSelectComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
