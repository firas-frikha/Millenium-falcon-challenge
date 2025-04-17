import {Component} from '@angular/core';
import {ReactiveFormsModule} from '@angular/forms';
import {MillenniumFalconBackendService} from './millennium-falcon-backend-service';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-input-file',
  imports: [
    ReactiveFormsModule,
    NgIf,
  ],
  templateUrl: './input-file.component.html',
  styleUrl: './input-file.component.css'
})

export class InputFileComponent {

  file: File = null;
  survivalPercentage: number = null;

  constructor(private millenniumFalconBackend: MillenniumFalconBackendService) {
  }

  onFileChange(files: FileList) {
    this.survivalPercentage = null;

    this.file = files[0];
  }

  compute() {
    if (!this.file) return;

    let reader = new FileReader();

    reader.readAsText(this.file);

    reader.onload = () => {
      this.millenniumFalconBackend.compute(reader.result).subscribe({
        next: res => {
          this.survivalPercentage = res * 100;
        },
        error: err => {
          console.error(err);
        }
      })
    };
  }

  getColorForPercentage(pct: number): string {
    const p = Math.max(0, Math.min(100, pct));
    const hue = (p * 120) / 100;           // 0→120
    return `hsl(${hue}, 100%, 50%)`;
  }
}
