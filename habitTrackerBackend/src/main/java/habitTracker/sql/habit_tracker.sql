CREATE DATABASE IF NOT EXISTS habit_tracker;
USE habit_tracker;

CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(50) PRIMARY KEY,
    display_name VARCHAR(80) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    date_of_birth DATE NULL,
    is_admin BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    user_image VARCHAR(512) NULL
    );


CREATE TABLE IF NOT EXISTS activity_types (
    activity_type_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    activity_done VARCHAR(50) NULL
    );

CREATE TABLE IF NOT EXISTS habits (
    habit_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    activity_type_id INT NOT NULL,
    description VARCHAR(100) NOT NULL,
    habit_reminder VARCHAR(20) NULL,
    habit_target INT NOT NULL,
    habit_frequency INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (username) REFERENCES users(username),
    FOREIGN KEY (activity_type_id) REFERENCES activity_types (activity_type_id)
    );


CREATE TABLE IF NOT EXISTS habit_tracker_log (
    habit_tracker_log_id INT AUTO_INCREMENT PRIMARY KEY,
    habit_id INT NOT NULL,
    date_of_activity DATE NOT NULL,
    duration_minutes INT NULL,
    distance_km DECIMAL(6,2) NULL,
    calories_burned INT NULL,
    notes VARCHAR(255) NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (habit_id) REFERENCES habits(habit_id) ON DELETE CASCADE
    );


CREATE TABLE IF NOT EXISTS streaks (
   streak_id INT AUTO_INCREMENT PRIMARY KEY,
   habit_id INT NOT NULL,
   current_streak INT NOT NULL DEFAULT 0,
   longest_streak INT NOT NULL DEFAULT 0,
   last_completed_date DATE NULL,
   UNIQUE (habit_id),
   FOREIGN KEY (habit_id) REFERENCES habits(habit_id) ON DELETE CASCADE
    );


CREATE TABLE IF NOT EXISTS device_tokens (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL UNIQUE,
  fcm_token TEXT NOT NULL,
  sns_endpoint_arn TEXT,
  platform VARCHAR(20) DEFAULT 'android',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS ai_conversations (
 conversation_id   INT AUTO_INCREMENT PRIMARY KEY,
 username          VARCHAR(100) NOT NULL,
 conversation_uuid VARCHAR(36)  NOT NULL UNIQUE,
 s3_key            VARCHAR(500) NOT NULL,
 created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS ai_recommendations (
  recommendation_id INT AUTO_INCREMENT PRIMARY KEY,
   username          VARCHAR(100)  NOT NULL,
   type              VARCHAR(10)   NOT NULL,
   title             VARCHAR(500)  NOT NULL,
   author            VARCHAR(255),
   channel           VARCHAR(255),
   description       TEXT,
   url               VARCHAR(1000),
   created_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
   is_current        BOOLEAN       NOT NULL DEFAULT TRUE,
   FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ai_insights (
    insight_id   INT AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(100) NOT NULL,
    insight_text TEXT         NOT NULL,
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_current   BOOLEAN      NOT NULL DEFAULT TRUE,
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
    );


