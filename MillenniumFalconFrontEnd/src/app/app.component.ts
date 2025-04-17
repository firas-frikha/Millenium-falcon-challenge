import {Component} from '@angular/core';
import {InputFileComponent} from './input-file/input-file/input-file.component';

@Component({
  selector: 'app-root',
  imports: [
    InputFileComponent
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'MillenniumFalconFrontEnd';
}
