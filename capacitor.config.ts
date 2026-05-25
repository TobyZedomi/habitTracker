import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.tobyz.habittrackerapp',
  appName: 'habitTrackerApp',
  webDir: 'www',
  plugins: {
    SplashScreen: {
      launchShowDuration: 0
    },
    PushNotifications: {
      presentationOptions: ['badge', 'sound', 'alert']
    },
    CapacitorHttp: {
      enabled: true
    }
  },
  server: {
    androidScheme: 'http',
    cleartext: true
  },
  android: {
    allowMixedContent: true
  }
};

export default config;
