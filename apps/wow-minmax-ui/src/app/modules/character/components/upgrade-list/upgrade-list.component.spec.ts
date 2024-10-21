import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpgradeListComponent } from './upgrade-list.component';

describe('UpgradeListComponent', () => {
	let component: UpgradeListComponent;
	let fixture: ComponentFixture<UpgradeListComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ UpgradeListComponent ]
		})
		.compileComponents();

		fixture = TestBed.createComponent(UpgradeListComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
