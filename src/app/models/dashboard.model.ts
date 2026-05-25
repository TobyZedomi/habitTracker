import { HabitResponse } from "./habit.model";
import { ActivityLogResponse } from "./activityLog.model";

export interface DashboardResponse {
    totalHabits: number;
    activeHabits: number;
    totalCompletedSessions: number;
    totalDurationMinutes: number;
    totalDistanceKm: number;
    totalCaloriesBurned: number;

  habits: HabitResponse[];
  recentActivities: ActivityLogResponse[];

}