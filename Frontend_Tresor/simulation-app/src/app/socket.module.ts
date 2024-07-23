import { NgModule } from '@angular/core';
import { SocketIoModule, SocketIoConfig } from 'ngx-socket-io';

const config: SocketIoConfig = { url: 'http://localhost:8080', options: {} };

@NgModule({
  imports: [SocketIoModule.forRoot(config)],
  exports: [SocketIoModule] // Exporter pour les utiliser dans d'autres modules
})
export class SocketModule { }
