import { Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { getIcon } from '../util/Icon';

@Pipe({
	name: 'icon'
})
export class IconPipe implements PipeTransform {
	constructor(private sanitizer: DomSanitizer) {}

	transform(value: string, ...args: unknown[]): string {
		return getIcon(value);
	}
}
