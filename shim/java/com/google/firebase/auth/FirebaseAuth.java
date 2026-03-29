package com.google.firebase.auth;

import com.google.firebase.FirebaseApp;
import com.google.android.gms.tasks.Task;

public class FirebaseAuth {
    private static FirebaseAuth sInstance;

    public static FirebaseAuth getInstance() {
        if (sInstance == null) sInstance = new FirebaseAuth();
        return sInstance;
    }

    public static FirebaseAuth getInstance(FirebaseApp app) {
        return getInstance();
    }

    public FirebaseUser getCurrentUser() { return null; }
    public void addAuthStateListener(AuthStateListener listener) {}
    public void removeAuthStateListener(AuthStateListener listener) {}
    public Task<Void> signOut() { return new Task<>((Void) null); }
    public String getUid() { return null; }

    public interface AuthStateListener {
        void onAuthStateChanged(FirebaseAuth auth);
    }
}
