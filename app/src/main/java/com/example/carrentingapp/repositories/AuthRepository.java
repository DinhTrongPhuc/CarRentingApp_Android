package com.example.carrentingapp.repositories;

import com.example.carrentingapp.models.User;
import com.example.carrentingapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthRepository {
    public interface AuthCallback {
        void onSuccess(User user);
        void onFailure(String errorMsg);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onFailure(String errorMsg);
    }

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void register(String email, String password, String fullName, String phone, String role, int age, String driverLicense, String licenseExpiration, AuthCallback cb) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener(result -> {
                FirebaseUser fbUser = result.getUser();
                if (fbUser == null) { cb.onFailure("Lỗi tạo tài khoản"); return; }
                User user = new User(fbUser.getUid(), fullName, email, phone, role, age, driverLicense, licenseExpiration);
                db.collection(Constants.COLLECTION_USERS).document(fbUser.getUid())
                    .set(user)
                    .addOnSuccessListener(v -> cb.onSuccess(user))
                    .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
            })
            .addOnFailureListener(e -> {
                String msg = e.getMessage() != null && e.getMessage().contains("email address is already in use")
                    ? "Email đã được sử dụng" : e.getMessage();
                cb.onFailure(msg);
            });
    }

    public void login(String email, String password, AuthCallback cb) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener(result -> {
                FirebaseUser fbUser = result.getUser();
                if (fbUser == null) { cb.onFailure("Lỗi đăng nhập"); return; }
                db.collection(Constants.COLLECTION_USERS).document(fbUser.getUid()).get()
                    .addOnSuccessListener(doc -> {
                        User user = doc.toObject(User.class);
                        if (user != null) cb.onSuccess(user);
                        else cb.onFailure("Không tìm thấy dữ liệu người dùng");
                    })
                    .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
            })
            .addOnFailureListener(e -> cb.onFailure("Email hoặc mật khẩu không đúng"));
    }

    public void logout() { auth.signOut(); }

    public void updatePassword(String newPassword, SimpleCallback cb) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword)
                .addOnSuccessListener(v -> cb.onSuccess())
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
        } else cb.onFailure("Chưa đăng nhập");
    }

    public FirebaseUser getCurrentUser() { return auth.getCurrentUser(); }
}
