import { Component, Input } from '@angular/core';

@Component({
	selector: 'app-dps-bar',
	templateUrl: './dps-bar.component.html',
	styleUrls: ['./dps-bar.component.css']
})
export class DpsBarComponent {
	@Input() dps?: number;
	@Input() dpsDiff?: number;
}