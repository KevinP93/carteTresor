export interface Montagne {
    nom: string;
    x: number;
    y: number;
  }
  
  export interface Aventurier {
    nom: string;
    x: number;
    y: number;
    orientation: string;
    tresorsCollectes: number;
  }
  
  export interface Tresor {
    x: number;
    y: number;
    nombre: number;
  }
  
  export interface Carte {
    largeur: number;
    hauteur: number;
    montagnes: Montagne[];
    aventuriers: Aventurier[];
    tresors: Tresor[];
  }
  