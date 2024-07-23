import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { SimulationService } from '../../services/simulation.service';

@Component({
  selector: 'app-file-upload',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './file-upload.component.html',
  styleUrl: './file-upload.component.css'
})
export class FileUploadComponent implements OnInit{

  fileForm: FormGroup;
  fileToUpload: File | null = null;
  responseMessage: string | undefined;

  constructor(private fb: FormBuilder, private router: Router,
    private simulationService: SimulationService
  ) {
    this.fileForm = this.fb.group({
      file: [null, Validators.required]
    });
  }

  ngOnInit(): void {}

  onFileChange(event: any) {
    if (event.target.files && event.target.files.length) {
      this.fileToUpload = event.target.files[0];
    }
  }
  onSubmit() {
    if (this.fileToUpload) {
      this.simulationService.uploadFile(this.fileToUpload).subscribe({
        next: (response: { message: string; filePath: string }) => {
          console.log('Upload response:', response);
          this.responseMessage = response.message;

          // Redirection après l'upload avec le chemin du fichier en tant que paramètre
          this.router.navigate(['/simulationView'], {
            queryParams: { filePath: encodeURIComponent(response.filePath) }
          });
        },
        error: (err) => {
          console.error('Upload error:', err);
          this.responseMessage = 'Erreur lors du téléversement du fichier.';
        }
      });
    } else {
      this.responseMessage = 'Veuillez sélectionner un fichier.';
    }
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    // Optionnel: Ajouter une classe pour indiquer que le fichier peut être déposé
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    const file = event.dataTransfer?.files[0];
    if (file) {
      this.fileToUpload = file;
      // Mettre à jour le formulaire avec le fichier
      this.fileForm.patchValue({ file: this.fileToUpload });
    }
  }
}
