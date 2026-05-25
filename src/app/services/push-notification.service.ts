import { Injectable } from '@angular/core';
import { Capacitor } from '@capacitor/core';
import {
  PushNotifications,
  Token,
  PushNotificationSchema,
  ActionPerformed,
  PermissionStatus
} from '@capacitor/push-notifications';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from '../auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class PushNotificationService {
  
  constructor(
    private router: Router,
    private http: HttpClient,
    private authService: AuthService
  ) { }
  
  async initPush() {
    if (Capacitor.getPlatform() !== 'web') {
      await this.registerPush();
    }
  }
  
  private async registerPush() {
    let permissionStatus: PermissionStatus = await PushNotifications.requestPermissions();
    
    if (permissionStatus.receive === 'granted') {
      console.log('Push notification permission granted');
    } else {
      console.error('Push notification permission not granted');
      return;
    }
    
    await PushNotifications.register();
    
    PushNotifications.addListener('registration', async (token: Token) => {
      console.log('Push registration success, token: ' + token.value);
      
      const jwt = await this.authService.getToken();
      if (!jwt) {
        console.error('No JWT found, skipping token registration');
        return;
      }
      
      try {
        await firstValueFrom(
          this.http.post(
            'http://habit-tracker-env.eba-ykqxpvem.eu-west-1.elasticbeanstalk.com/api/push/register',
            { token: token.value, platform: 'android' },
            { headers: new HttpHeaders({ 'Authorization': 'Bearer ' + jwt }) }
          )
        );
        console.log('Push token sent to server successfully');
      } catch (error) {
        console.error('Error sending push token to server: ', error);
      }
    });
    
    PushNotifications.addListener('registrationError', (error: any) => {
      console.error('Push registration error: ', error);
    });
    
    PushNotifications.addListener('pushNotificationReceived', (notification: PushNotificationSchema) => {
      console.log('Push notification received: ', notification);
    });
    
    PushNotifications.addListener('pushNotificationActionPerformed', (action: ActionPerformed) => {
      console.log('Push action performed: ', action);
      const habitId = action.notification.data?.habitId;
      if (habitId) {
        this.router.navigate(['/tabs/habit-detail', habitId]);
      }
    });
  }
}