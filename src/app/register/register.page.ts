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
import { alertController } from '@ionic/core';

@Component({
  selector: 'app-register',
  templateUrl: './register.page.html',
  styleUrls: ['./register.page.scss'],
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
export class RegisterPage {
  username = '';
  displayName = '';
  email = '';
  password = '';
  confirmPassword = '';
  dateOfBirth = '';
  errorMessage = '';

  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  async register() {
    this.errorMessage = '';

    if (!this.username.trim()) {
      this.errorMessage = 'Username is required.';
      return;
    }

    if (!this.email.trim()) {
      this.errorMessage = 'Email address is required.';
      return;
    }

    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(this.email.trim())) {
      this.errorMessage = 'Please enter a valid email address.';
      return;
    }

   if (!this.password.trim()) {
    this.errorMessage = 'Password is required.';
    return;
}

if (this.password.length < 8) {
    this.errorMessage = 'Password must be at least 8 characters long.';
    return;
}

if (!/[A-Z]/.test(this.password)) {
    this.errorMessage = 'Password must contain at least one capital letter.';
    return;
}

if (!/[a-z]/.test(this.password)) {
    this.errorMessage = 'Password must contain at least one lowercase letter.';
    return;
}


if (!/[^a-zA-Z0-9]/.test(this.password)) {
    this.errorMessage = 'Password must contain at least one special character.';
    return;
}

if (this.password !== this.confirmPassword) {
    this.errorMessage = 'Passwords do not match. Please try again.';
    return;
}

if (!this.dateOfBirth) {
    this.errorMessage = 'Date of birth is required.';
    return;
}

const dob = new Date(this.dateOfBirth);
const today = new Date();
if (dob >= today) {
    this.errorMessage = 'Date of birth cannot be in the future.';
    return;
}

    try {
      const res = await this.auth.register({
        username: this.username.trim(),
        displayName: this.displayName.trim(),
        email: this.email.trim(),
        password: this.password,
        dateOfBirth: this.dateOfBirth
      });

      if (!res.success || !res.token) {
        this.errorMessage = res.message || 'Registration failed. Please try again.';
        return;
      }

     await this.auth.setToken(res.token);
await this.auth.loadCurrentUser();
await this.router.navigateByUrl('/tabs/tab1', { replaceUrl: true });

const alert = await alertController.create({
    header: 'Welcome!',
    message: `Account created successfully. Welcome to CoachHabits, ${this.username.trim()}.`,
    cssClass: 'app-success-alert',
    buttons: ['OK']
});
await alert.present();

      await this.router.navigateByUrl('/tabs/tab1', { replaceUrl: true });
    } catch (err: any) {
      this.errorMessage = err?.error?.message || 'Registration failed. Please check your details and try again.';
    }
  }

  goToLogin() {
    this.router.navigateByUrl('/login');
  }
}