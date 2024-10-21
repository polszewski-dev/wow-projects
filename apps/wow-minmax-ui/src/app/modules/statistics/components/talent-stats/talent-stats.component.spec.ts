import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TalentStatsComponent } from './talent-stats.component';

describe('TalentStatsComponent', () => {
	let component: TalentStatsComponent;
	let fixture: ComponentFixture<TalentStatsComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ TalentStatsComponent ]
		})
		.compileComponents();

		fixture = TestBed.createComponent(TalentStatsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
