package com.kinedical.model;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    @JsonIgnore
    private String password;

    private String phone;
    private Role role;
    private Profile profile;
    private Preferences preferences;
    private List<Double> vector;
    private Status status;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastLoginAt;

    public enum Role {
        PATIENT, DOCTOR, ADMIN
    }

    public enum Status {
        ACTIVE, INACTIVE, SUSPENDED
    }

    public static class Profile {
        private String fullName;
        private String gender;
        private Instant birthDate;
        private Address address;
        private String avatarUrl;
        private String language;

        public static class Address {
            private String street;
            private String city;
            private String province;
            private String country;
            private String postalCode;

            public String getStreet() {
                return street;
            }

            public void setStreet(String street) {
                this.street = street;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getPostalCode() {
                return postalCode;
            }

            public void setPostalCode(String postalCode) {
                this.postalCode = postalCode;
            }
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public Instant getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(Instant birthDate) {
            this.birthDate = birthDate;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
    }

    public static class Preferences {
        private List<String> interestTags;
        private List<String> preferredSpecialties;
        private Boolean notificationOptIn;
        private List<String> healthGoals;

        public List<String> getInterestTags() {
            return interestTags;
        }

        public void setInterestTags(List<String> interestTags) {
            this.interestTags = interestTags;
        }

        public List<String> getPreferredSpecialties() {
            return preferredSpecialties;
        }

        public void setPreferredSpecialties(List<String> preferredSpecialties) {
            this.preferredSpecialties = preferredSpecialties;
        }

        public Boolean getNotificationOptIn() {
            return notificationOptIn;
        }

        public void setNotificationOptIn(Boolean notificationOptIn) {
            this.notificationOptIn = notificationOptIn;
        }

        public List<String> getHealthGoals() {
            return healthGoals;
        }

        public void setHealthGoals(List<String> healthGoals) {
            this.healthGoals = healthGoals;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public List<Double> getVector() {
        return vector;
    }

    public void setVector(List<Double> vector) {
        this.vector = vector;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
}
