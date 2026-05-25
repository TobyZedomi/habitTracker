import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Component } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { ActivityLogService } from '../services/activityLog.service';
import { Router } from '@angular/router';
import { ActivityLogRequest } from '../models/activityLog.model';
import { AlertController } from '@ionic/angular';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { HabitService } from '../services/habit.service';
import { HabitResponse } from '../models/habit.model';
import { AiCoachService } from '../services/aiCoach.service';
import { addIcons } from 'ionicons';
import {
  calendarOutline,
  timeOutline,
  walkOutline,
  flameOutline,
  documentTextOutline
} from 'ionicons/icons';

@Component({
  selector: 'app-log-activity',
  templateUrl: './log-activity.page.html',
  styleUrls: ['./log-activity.page.scss'],
  standalone: true,
  imports: [IonicModule, CommonModule, ReactiveFormsModule]
})
export class LogActivityPage {
  habitId!: number;
  habit: HabitResponse | null = null;
  logForm!: FormGroup;
  loading: boolean = false;
  errorMessage = '';
  today: string = new Date().toISOString().split('T')[0];

  constructor(
    private activityLogService: ActivityLogService,
    private router: Router,
    private alertController: AlertController,
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private habitService: HabitService,
    private aiCoachService: AiCoachService
  ) {
    addIcons({
      calendarOutline,
      timeOutline,
      walkOutline,
      flameOutline,
      documentTextOutline
    });
  }

  ngOnInit() {
    this.loadHabitIdFromRoute();
    this.buildForm();
    this.loadHabitDetails();
  }

  ionViewWillEnter() {
    this.loadHabitIdFromRoute();
    this.buildForm();
  }

  loadHabitDetails() {
    if (!this.habitId) {
      return;
    }
    this.habitService.getOneHabitById(this.habitId).subscribe(
      (habit: HabitResponse) => {
        this.habit = habit;
      },
      (error) => {
        this.showErrorAlert('Failed to load habit details. Please try again later.');
      }
    );
  }

  loadHabitIdFromRoute() {
    const habitIdParam = this.route.snapshot.paramMap.get('habitId');
    if (habitIdParam) {
      this.habitId = Number(habitIdParam);
      return;
    }
  }

  buildForm() {
    this.logForm = this.formBuilder.group({
      habitId: [this.habitId, Validators.required],
      date: ['', Validators.required],
      durationMinutes: ['', Validators.min(0)],
      distanceKm: ['', Validators.min(0)],
      caloriesBurned: ['', Validators.min(0)],
      notes: ['']
    });
  }

  onSubmit() {
    if (this.logForm.invalid) {
      this.showErrorAlert('Please fill in the required fields.');
      return;
    }

    const formValue = this.logForm.value;
    const logData: ActivityLogRequest = {
      habitId: this.habitId,
      dateOfActivity: formValue.date,
      durationMinutes: formValue.durationMinutes ? Number(formValue.durationMinutes) : null,
      distanceKm: formValue.distanceKm ? Number(formValue.distanceKm) : null,
      caloriesBurned: formValue.caloriesBurned ? Number(formValue.caloriesBurned) : null,
      note: formValue.notes
    };

    this.activityLogService.logHabit(logData).subscribe(
      async () => {
        this.aiCoachService.refreshInsights().subscribe();
        await this.showSuccessAlert('Success', `Activity logged for ${this.habit?.activityName}.`);
        this.router.navigate(['/tabs/habit-detail', this.habitId], { replaceUrl: true });
      },
      async (error) => {
        await this.showErrorAlert('Failed to log activity. Please try again later.');
      }
    );
  }

  async showErrorAlert(message: string) {
    const alert = await this.alertController.create({
      header: 'Error',
      message: message,
      buttons: ['OK']
    });
    await alert.present();
  }

  async showSuccessAlert(header: string, message: string) {
    const alert = await this.alertController.create({
      header: header,
      message: message,
      cssClass: 'app-success-alert',
      buttons: ['OK']
    });
    await alert.present();
  }

  async logout() {
    await this.router.navigateByUrl('/login', { replaceUrl: true });
  }

  goBackToTab2() {
    this.router.navigate(['/tabs/tab2'], { replaceUrl: true });
  }

  goBackToHabitDetail() {
    this.router.navigate(['/tabs/habit-detail', this.habitId], { replaceUrl: true });
  }
}