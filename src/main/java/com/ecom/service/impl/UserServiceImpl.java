package com.ecom.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.UserDtls;
import com.ecom.repository.UserRepository;
import com.ecom.service.FileService;
import com.ecom.service.UserService;
import com.ecom.util.AppConstant;
import com.ecom.util.BucketType;
import com.ecom.util.CommonUtil;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    private CommonUtil commonUtil;

    @Autowired
    private FileService fileService;

    @Override
    public UserDtls saveUser(UserDtls user) {
        user.setRole("ROLE_USER");
        user.setIsEnable(true);
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public UserDtls getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public UserDtls saveUser(UserDtls user, MultipartFile img) {
        UserDtls saved = saveUser(user);
        if (img != null && !img.isEmpty()) {
            String imageUrl = commonUtil.getImageUrl(img, BucketType.PROFILE.getId());
            saved.setProfileImage(imageUrl);
            userRepository.save(saved);
            try {
                fileService.uploadFileS3(img, BucketType.PROFILE.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return saved;
    }

    @Override
    public UserDtls getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDtls> getUsers(String role) {
        return userRepository.findByRole(role);
    }

    @Override
    public Boolean updateAccountStatus(Integer id, Boolean status) {
        Optional<UserDtls> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            UserDtls user = userOpt.get();
            user.setIsEnable(status);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void increaseFailedAttempt(UserDtls user) {
        user.setFailedAttempt(user.getFailedAttempt() + 1);
        userRepository.save(user);
    }

    @Override
    public void userAccountLock(UserDtls user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
        userRepository.save(user);
    }

    @Override
    public boolean unlockAccountTimeExpired(UserDtls user) {
        long unlockTime = user.getLockTime().getTime() + AppConstant.UNLOCK_DURATION_TIME;
        if (System.currentTimeMillis() > unlockTime) {
            user.setAccountNonLocked(true);
            user.setFailedAttempt(0);
            user.setLockTime(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void resetAttempt(int userId) {
        UserDtls user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setFailedAttempt(0);
            userRepository.save(user);
        }
    }

    @Override
    public void updateUserResetToken(String email, String resetToken) {
        UserDtls user = userRepository.findByEmail(email);
        if (user != null) {
            user.setResetToken(resetToken);
            userRepository.save(user);
        }
    }

    @Override
    public UserDtls getUserByToken(String token) {
        return userRepository.findByResetToken(token);
    }

    @Override
    public UserDtls updateUser(UserDtls user) {
        return userRepository.save(user);
    }

    @Override
    public UserDtls updateUserProfile(UserDtls user, MultipartFile img) {
        Optional<UserDtls> optionalUser = userRepository.findById(user.getId());
        if (optionalUser.isEmpty()) return null;
        UserDtls dbUser = optionalUser.get();

        if (img != null && !img.isEmpty()) {
            String imageUrl = commonUtil.getImageUrl(img, BucketType.PROFILE.getId());
            dbUser.setProfileImage(imageUrl);
        }

        dbUser.setName(user.getName());
        dbUser.setMobileNumber(user.getMobileNumber());
        dbUser.setAddress(user.getAddress());
        dbUser.setCity(user.getCity());
        dbUser.setState(user.getState());
        dbUser.setPincode(user.getPincode());
        userRepository.save(dbUser);

        try {
            if (img != null && !img.isEmpty()) {
                fileService.uploadFileS3(img, BucketType.PROFILE.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dbUser;
    }

    @Override
    public UserDtls saveAdmin(UserDtls user) {
        user.setRole("ROLE_ADMIN");
        user.setIsEnable(true);
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public Boolean existsEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
