import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { SimulationService } from '../../services/simulation.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-file-upload',
  standalone: true,
  imports: [ReactiveFormsModule,CommonModule],
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
    // On vérifie si des fichiers ont été sélectionnés et si la longueur de la liste des fichiers est supérieure à zéro
    if (event.target.files && event.target.files.length) {
      
      this.fileToUpload = event.target.files[0];
  
      // Met à jour le champ `file` du formulaire (`fileForm`) avec le fichier sélectionné
      // `patchValue` est utilisé pour mettre à jour une partie du formulaire, ici le champ `file`
      this.fileForm.patchValue({ file: this.fileToUpload });
    }
  }
  
  onSubmit() {
    if (this.fileToUpload) {
        this.simulationService.uploadFile(this.fileToUpload).subscribe({
            next: (response: { message: string; filePath: string }) => {
                console.log('Upload response:', response);
                this.responseMessage = response.message;

                // Redirection après l'upload seulement si l'upload est réussi
                if (response.message === 'Fichier téléchargé avec succès.') {
                    this.router.navigate(['/simulationView'], {
                        queryParams: { filePath: encodeURIComponent(response.filePath) }
                    });
                }
            },
            error: (err) => {
                console.error('Upload error:', err);
                // si l'erreur provient du serveur ou d'un autre problème
                if (err.error && err.error.message) {
                    this.responseMessage = err.error.message;
                } else {
                    this.responseMessage = 'Erreur lors du téléversement du fichier.';
                }
            }
        });
    } else {
        this.responseMessage = 'Veuillez sélectionner un fichier.';
    }
}



  onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    const file = event.dataTransfer?.files[0];
    if (file) {
      this.fileToUpload = file;
      // Mise à jour le formulaire avec le fichier
      this.fileForm.patchValue({ file: this.fileToUpload });
    }
  }
}
