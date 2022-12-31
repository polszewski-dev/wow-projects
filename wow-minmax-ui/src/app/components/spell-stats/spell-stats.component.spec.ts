import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SpellStatsComponent } from './spell-stats.component';

describe('SpellStatsComponent', () => {
	let component: SpellStatsComponent;
	let fixture: ComponentFixture<SpellStatsComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ SpellStatsComponent ]
		})
		.compileComponents();

		fixture = TestBed.createComponent(SpellStatsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
