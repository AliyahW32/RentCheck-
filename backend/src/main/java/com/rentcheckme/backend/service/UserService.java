package com.rentcheckme.backend.service;

import com.rentcheckme.backend.dto.UserSummaryResponse;
import com.rentcheckme.backend.model.FinanceProfile;
import com.rentcheckme.backend.model.User;
import com.rentcheckme.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserSummaryResponse> getUsers() {
        return userRepository.findAll().stream().map(UserSummaryResponse::new).toList();
    }

    public User getUserOrDefault(String userId) {
        return userRepository.findById(userId).orElse(userRepository.findAll().getFirst());
    }

    public User mergeUserOverrides(User baseUser, String city, FinanceProfile finances) {
        FinanceProfile mergedFinances = finances == null
            ? baseUser.getFinances()
            : new FinanceProfile(
                finances.getIncome(),
                finances.getDebt(),
                finances.getSavings(),
                finances.getCash(),
                finances.getRoommates()
            );

        return new User(
            baseUser.getId(),
            baseUser.getName(),
            baseUser.getRole(),
            city == null || city.isBlank() ? baseUser.getCity() : city,
            mergedFinances,
            baseUser.getPreferences()
        );
    }
}
