import { Component } from '@angular/core';
import { SimulationService } from '../../services/simulation.service';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { Carte } from '../../model/carte';

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
  isSimulationStarted = false;
  carte: Carte | undefined;

  constructor(private route: ActivatedRoute, private simulationService: SimulationService) {}

  ngOnInit(): void {
    // Récupérer le chemin du fichier depuis les paramètres de la route
    this.route.queryParams.subscribe(params => {
      this.filePath = decodeURIComponent(params['filePath']);
    });
  }

  startHunt() {
    if (this.filePath) {
      this.isSimulationStarted = true;
      this.simulationService.simulate(this.filePath).subscribe({
        next: (response) => {
          console.log('Simulation response:', response);
          this.simulationResult = response.message;
          this.carte = response.result; // Utilisation directe de l'objet carte
          this.downloadFile();
        },
        error: (err) => {
          console.error('Simulation error:', err);
          this.simulationResult = 'Erreur lors de la simulation.';
          this.isSimulationStarted = false;
        }
      });
    }
  }
  
  
  
  downloadFile() {
    this.simulationService.downloadFile().subscribe(
      (response: Blob) => {
        const url = window.URL.createObjectURL(response);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'output.txt';
        link.click();
        window.URL.revokeObjectURL(url);
      },
      (error) => {
        console.error('Erreur lors du téléchargement du fichier', error);
      }
    );
  }

  

  getGrid(): { symbol: string, count?: number, orientation?: string, nom?: string, tresorsCollectes?: number }[][] {
    if (!this.carte) {
      return [];
    }
  
    const hauteur = this.carte.hauteur ?? 0;
    const largeur = this.carte.largeur ?? 0;
  
    const grid = Array.from({ length: hauteur }, () =>
      Array(largeur).fill({ symbol: ' ', count: undefined, orientation: undefined, nom: undefined, tresorsCollectes: undefined })
    );
  
    // Remplit la grille avec des montagnes
    (this.carte.montagnes ?? []).forEach(m => {
      if (m.y >= 0 && m.y < hauteur && m.x >= 0 && m.x < largeur) {
        grid[m.y][m.x] = { symbol: 'M' }; // M pour Montagne
      }
    });
  
    // Remplit la grille avec des aventuriers, y compris leur orientation et autres propriétés
    (this.carte.aventuriers ?? []).forEach(a => {
      if (a.y >= 0 && a.y < hauteur && a.x >= 0 && a.x < largeur) {
        grid[a.y][a.x] = { symbol: 'A', orientation: a.orientation, nom: a.nom, tresorsCollectes: a.tresorsCollectes }; // A pour Aventurier
      }
    });
  
    // Remplit la grille avec des trésors, mais filtre ceux avec `nombre` égal à 0
    (this.carte.tresors ?? []).forEach(t => {
      if (t.y >= 0 && t.y < hauteur && t.x >= 0 && t.x < largeur && t.nombre > 0) {
        grid[t.y][t.x] = { symbol: 'T', count: t.nombre }; // T pour Trésor avec le nombre
      }
    });
  
    return grid;
  }
    
  

}
