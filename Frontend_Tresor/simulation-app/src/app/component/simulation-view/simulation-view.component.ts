import { Component } from '@angular/core';
import { SimulationService } from '../../services/simulation.service';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-simulation-view',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './simulation-view.component.html',
  styleUrl: './simulation-view.component.css'
})
export class SimulationViewComponent {
  filePath: string | null = null;
  simulationResult: string = '';
  constructor(private route: ActivatedRoute, private simulationService: SimulationService) {}

  ngOnInit(): void {
    // Récupérer le chemin du fichier depuis les paramètres de la route
    this.route.queryParams.subscribe(params => {
      this.filePath = decodeURIComponent(params['filePath']);
    });
  }

   startHunt() {
    if (this.filePath) {
      this.simulationService.simulate(this.filePath).subscribe({
        next: (response: { message: string; result: string }) => {
          console.log('Simulation response:', response);
          this.simulationResult = response.result;
          this.downloadFile();
        },
        error: (err) => {
          console.error('Simulation error:', err);
          this.simulationResult = 'Erreur lors de la simulation.';
        }
      });
    }
  }
  downloadFile() {
    const url = 'http://localhost:8080/api/download'; // URL de l'endpoint de téléchargement

    // Créer un élément de lien temporaire pour le téléchargement
    const link = document.createElement('a');
    link.href = url;
    link.download = 'output.txt'; // Nom par défaut du fichier téléchargé
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

}
