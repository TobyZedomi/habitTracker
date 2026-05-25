import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import {
  IonContent,
  IonHeader,
  IonToolbar,
  IonItem,
  IonInput,
  IonButton,
  IonIcon,
  IonText
} from '@ionic/angular/standalone';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth/auth.service';
import { PushNotificationService } from '../services/push-notification.service';
import { AlertController } from '@ionic/angular';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    IonContent,
    IonHeader,
    IonToolbar,
    IonItem,
    IonInput,
    IonButton,
    IonIcon,
    IonText
  ]
})
export class LoginPage {
  username = '';
  password = '';
  errorMessage = '';

  constructor(
    private auth: AuthService,
    private router: Router,
    private pushNotificationService: PushNotificationService,
    private alertController: AlertController
  ) {}

  async login() {
    this.errorMessage = '';

    if (!this.username.trim() || !this.password.trim()) {
      this.errorMessage = 'Username and password are required.';
      return;
    }

    try {
      const res = await this.auth.login({
        username: this.username.trim(),
        password: this.password
      });

      if (!res.success || !res.token) {
        this.errorMessage = 'Your username or password is incorrect. Please try again.';
        return;
      }

      await this.auth.setToken(res.token);
      await this.auth.loadCurrentUser();
      await this.pushNotificationService.initPush();

      const alert = await this.alertController.create({
        header: 'Welcome back!',
        message: `You are now logged in as ${this.username.trim()}.`,
        cssClass: 'app-success-alert',
        buttons: ['OK']
      });
      await alert.present();
      await alert.onDidDismiss();

      await this.router.navigateByUrl('/tabs/tab1', { replaceUrl: true });
    } catch (err: any) {
      this.errorMessage = 'Your username or password is incorrect. Please try again.';
    }
  }

  goToRegister() {
    this.router.navigateByUrl('/register');
  }
}