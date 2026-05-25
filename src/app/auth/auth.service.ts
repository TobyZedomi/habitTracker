import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders  } from '@angular/common/http';
import { Storage } from '@ionic/storage-angular';
import { firstValueFrom } from 'rxjs';
import { environment } from 'src/environments/environment';

export interface AuthResponse {
  success: boolean;
  message: string;
  token: string | null;
}

export interface RegisterPayload {
  username: string;
  displayName?: string;
  email: string;
  password: string;
  dateOfBirth?: string;
  user_image?: string;
}

export interface LoginPayload {
  username: string;
  password: string;
}

export interface UserResponse {
  username: string;
  display_name: string;
  email: string;
  dateOfBirth: string;
  isAdmin?: boolean;
  admin?: boolean;
  createdAt: string;
  user_image: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
 // private baseUrl = 'http://localhost:8080';
  //private baseUrl = 'http://192.168.1.16:8080';
    private baseUrl = 'http://habit-tracker-env.eba-ykqxpvem.eu-west-1.elasticbeanstalk.com';

 //private baseUrl = environment.apiBaseUrl;
  private storageReady = false;

  constructor(
    private http: HttpClient,
    private storage: Storage
  ) {}

  private async ensureStorage() {
    if (!this.storageReady) {
      await this.storage.create();
      this.storageReady = true;
    }
  }

  async register(payload: RegisterPayload): Promise<AuthResponse> {
    await this.ensureStorage();

    return firstValueFrom(
      this.http.post<AuthResponse>(`${this.baseUrl}/auth/register`, payload)
    );
  }

  async login(payload: LoginPayload): Promise<AuthResponse> {
    await this.ensureStorage();

    return firstValueFrom(
      this.http.post<AuthResponse>(`${this.baseUrl}/auth/login`, payload)
    );
  }

  async getMe(): Promise<UserResponse> {
  await this.ensureStorage();

  const token = await this.getToken();

  return firstValueFrom(
    this.http.get<UserResponse>(`${this.baseUrl}/users/me`, {
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`
      })
    })
  );
}

  async setToken(token: string): Promise<void> {
    await this.ensureStorage();
    await this.storage.set('token', token);
  }

  async getToken(): Promise<string | null> {
    await this.ensureStorage();
    return await this.storage.get('token');
  }

  async isLoggedIn(): Promise<boolean> {
    const token = await this.getToken();
    return !!token;
  }

  async setCurrentUser(user: UserResponse): Promise<void> {
    await this.ensureStorage();
    await this.storage.set('currentUser', user);
  }

  async getCurrentUser(): Promise<UserResponse | null> {
    await this.ensureStorage();
    return await this.storage.get('currentUser');
  }

  async loadCurrentUser(): Promise<UserResponse | null> {
    try {
      const user = await this.getMe();
      await this.setCurrentUser(user);
      return user;
    } catch (e) {
      return null;
    }
  }

  async logout(): Promise<void> {
    await this.ensureStorage();
    await this.storage.remove('token');
    await this.storage.remove('currentUser');
  }
}