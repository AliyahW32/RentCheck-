package com.rentcheckme.backend.model;

public class PasswordProfile {
    private String salt;
    private String hash;
    private int iterations;
    private String algorithm;

    public PasswordProfile() {
    }

    public PasswordProfile(String salt, String hash, int iterations, String algorithm) {
        this.salt = salt;
        this.hash = hash;
        this.iterations = iterations;
        this.algorithm = algorithm;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
}
