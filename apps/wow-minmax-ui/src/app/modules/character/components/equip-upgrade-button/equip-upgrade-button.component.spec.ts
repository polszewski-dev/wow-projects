import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EquipUpgradeButtonComponent } from './equip-upgrade-button.component';

describe('EquipUpgradeButtonComponent', () => {
	let component: EquipUpgradeButtonComponent;
	let fixture: ComponentFixture<EquipUpgradeButtonComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ EquipUpgradeButtonComponent ]
		})
		.compileComponents();

		fixture = TestBed.createComponent(EquipUpgradeButtonComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
