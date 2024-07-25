import { provideRouter, Routes } from '@angular/router';
import { FileUploadComponent } from './component/file-upload/file-upload.component';
import { SimulationViewComponent } from './component/simulation-view/simulation-view.component';
import { AppComponent } from './app.component';
import { bootstrapApplication } from '@angular/platform-browser';
import { HomeComponent } from './component/home/home.component';

export const routes: Routes = [
    { path: '', component: HomeComponent },
    { path: 'fileUpload', component: FileUploadComponent }, 
    { path: 'simulationView', component: SimulationViewComponent }, 
  ];

  