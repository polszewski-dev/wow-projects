import { Pipe, PipeTransform } from '@angular/core';
import { getIcon } from '../util/Icon';

@Pipe({
	name: 'icon'
})
export class IconPipe implements PipeTransform {
	transform(value: string, ...args: unknown[]) {
		return getIcon(value);
	}
}
