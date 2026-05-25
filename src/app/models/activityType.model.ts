export interface ActivityType{
    activityTypeId: number;
    name: string;
    activityDone?: string;
} 


export interface ActivityTypeResponse {
    activityTypes: ActivityType[];
}