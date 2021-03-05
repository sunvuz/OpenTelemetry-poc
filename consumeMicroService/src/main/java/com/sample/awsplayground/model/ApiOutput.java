package com.sample.awsplayground.model;

public class ApiOutput {
        private int userId;

        private int id;

        private String title;

        private boolean completed;

        public void setUserId(int userId){
            this.userId = userId;
        }
        public int getUserId(){
            return this.userId;
        }
        public void setId(int id){
            this.id = id;
        }
        public int getId(){
            return this.id;
        }
        public void setTitle(String title){
            this.title = title;
        }
        public String getTitle(){
            return this.title;
        }
        public void setCompleted(boolean completed){
            this.completed = completed;
        }
        public boolean getCompleted(){
            return this.completed;
        }
    }


