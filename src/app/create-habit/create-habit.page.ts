import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  IonHeader, IonToolbar, IonTitle, IonContent, IonButton,
  IonIcon, IonCard, IonCardContent, IonItem, IonSelect,
  IonSelectOption, IonTextarea, IonInput, IonNote, IonText,
  IonSpinner, IonButtons, AlertController
} from '@ionic/angular/standalone';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HabitService } from '../services/habit.service';
import { Router } from '@angular/router';
import { ActivityTypeService } from '../services/activityType.service';
import { ActivityType } from '../models/activityType.model';
import { AiCoachService } from '../services/aiCoach.service';
import { addIcons } from 'ionicons';
import {
  logOutOutline,
  fitnessOutline,
  documentTextOutline,
  alarmOutline,
  flagOutline,
  repeatOutline,
  arrowBackOutline,
  gridOutline,
  homeOutline,
  statsChartOutline,
  trophyOutline,
  chevronDownOutline,
  listOutline
} from 'ionicons/icons';

@Component({
  selector: 'app-create-habit',
  templateUrl: './create-habit.page.html',
  styleUrls: ['./create-habit.page.scss'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    IonHeader,
    IonToolbar,
    IonTitle,
    IonContent,
    IonButton,
    IonIcon,
    IonCard,
    IonCardContent,
    IonItem,
    IonSelect,
    IonSelectOption,
    IonTextarea,
    IonInput,
    IonNote,
    IonText,
    IonSpinner,
    IonButtons
  ]
})
export class CreateHabitPage implements OnInit {

  habitForm: FormGroup;
  activityTypes: ActivityType[] = [];
  loading: boolean = false;
  errorMessage = '';

  activityTypeSelectOptions = {
    cssClass: 'activity-type-popover'
  };

  constructor(
    private formBuilder: FormBuilder,
    private habitService: HabitService,
    private activityTypeService: ActivityTypeService,
    private router: Router,
    private alertController: AlertController,
    private aiCoachService: AiCoachService
  ) {
    addIcons({
      logOutOutline,
      fitnessOutline,
      documentTextOutline,
      alarmOutline,
      flagOutline,
      repeatOutline,
      arrowBackOutline,
      gridOutline,
      homeOutline,
      statsChartOutline,
      trophyOutline,
      chevronDownOutline,
      listOutline
    });

    this.habitForm = this.formBuilder.group({
      activityTypeId: ['', Validators.required],
      description: [''],
      reminder: ['', Validators.required],
      target: ['', Validators.required],
      frequency: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.loadActivityTypes();
  }

  loadActivityTypes() {
    this.activityTypeService.getAllActivityTypes().subscribe(
      (types: ActivityType[]) => {
        this.activityTypes = types;
      },
      (error) => {
        this.errorMessage = 'Failed to load activity types. Please try again later.';
      }
    );
  }

  onSubmit() {
    if (this.habitForm.invalid) {
      this.habitForm.markAllAsTouched();
      this.errorMessage = 'Please fill in all required fields.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const formvalue = this.habitForm.value;
    const selectActivityType = this.activityTypes.find(type => type.activityTypeId === formvalue.activityTypeId);

    const habitData = {
      activityTypeId: formvalue.activityTypeId,
      description: formvalue.description,
      reminder: formvalue.reminder,
      target: formvalue.target,
      frequency: formvalue.frequency
    };

    this.habitService.createHabit(habitData).subscribe(
    async () => {
        this.loading = false;

        this.habitService.getLatestHabit().subscribe(
            async (latest) => {
                await this.showSuccessAlert(
                    'Habit Created!',
                    `${selectActivityType?.name} has been created successfully.`
                );
                this.habitForm.reset({
                    activityTypeId: '',
                    description: '',
                    reminder: '',
                    target: '',
                    frequency: ''
                });
                if (latest && latest.habitId) {
                    this.router.navigate(['/tabs/tab2'], {
                        queryParams: { newHabitId: latest.habitId },
                        replaceUrl: true
                    });
                } else {
                    this.router.navigate(['/tabs/tab2'], { replaceUrl: true });
                }
            },
            async () => {
                await this.showSuccessAlert(
                    'Habit Created!',
                    `${selectActivityType?.name} has been created successfully.`
                );
                this.habitForm.reset({
                    activityTypeId: '',
                    description: '',
                    reminder: '',
                    target: '',
                    frequency: ''
                });
                this.router.navigate(['/tabs/tab2'], { replaceUrl: true });
            }
        );
    },
    async (error) => {
    this.loading = false;
    const message = error?.error || 'Failed to create habit. Please try again later.';
    const alert = await this.alertController.create({
        message: message,
        buttons: ['OK']
    });
    await alert.present();
}
);
  }

  async showSuccessAlert(header: string, message: string) {
    const alert = await this.alertController.create({
      header: header,
      message: message,
      cssClass: 'app-success-alert',
      buttons: ['OK']
    });
    this.aiCoachService.refreshInsights().subscribe();
    await alert.present();
    await alert.onDidDismiss();
  }

  async logout() {
    await this.router.navigateByUrl('/login', { replaceUrl: true });
  }

  goBackToTab2() {
    this.router.navigate(['/tabs/tab2'], { replaceUrl: true });
  }
}