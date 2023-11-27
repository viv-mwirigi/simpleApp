package com.example.linkedin;

public class User {

        private String name;
        private String email;
        private String bio;
        private String phoneNumber;
        private String gender;
        private String skills;
        private String imageURL; // URL for the user's image

        // Constructor
        public User(String name, String email, String bio, String phoneNumber, String gender, String skills, String imageURL) {
            this.name = name;
            this.email = email;
            this.bio = bio;
            this.phoneNumber = phoneNumber;
            this.gender = gender;
            this.skills = skills;
            this.imageURL = imageURL;
        }

        // Getters and setters for each attribute (omitted for brevity)
        // ...


}
