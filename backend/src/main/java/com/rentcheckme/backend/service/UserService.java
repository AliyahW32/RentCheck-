package com.rentcheckme.backend.service;

import com.rentcheckme.backend.dto.OnboardingRequest;
import com.rentcheckme.backend.dto.UserSummaryResponse;
import com.rentcheckme.backend.model.FinanceProfile;
import com.rentcheckme.backend.model.LocationPreference;
import com.rentcheckme.backend.model.MonthlySpendingEntry;
import com.rentcheckme.backend.model.PasswordProfile;
import com.rentcheckme.backend.model.User;
import com.rentcheckme.backend.model.UserPreferences;
import com.rentcheckme.backend.model.UserRole;
import com.rentcheckme.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final MapService mapService;
    private final PasswordService passwordService;

    public UserService(UserRepository userRepository, MapService mapService, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.mapService = mapService;
        this.passwordService = passwordService;
    }

    public List<UserSummaryResponse> getUsers() {
        return userRepository.findAll().stream().map(UserSummaryResponse::new).toList();
    }

    public User getUserOrDefault(String userId) {
        return userRepository.findById(userId).orElse(userRepository.findAll().getFirst());
    }

    public User getUserProfile(String userId) {
        return getUserOrDefault(userId);
    }

    public User createUser(OnboardingRequest request) {
        return userRepository.create(buildUser(null, request));
    }

    public User updateUser(String userId, OnboardingRequest request) {
        User existing = getUserOrDefault(userId);
        User updated = buildMergedUser(existing, request);
        return userRepository.save(updated);
    }

    public User mergeUserOverrides(User baseUser, String city, FinanceProfile finances) {
        FinanceProfile mergedFinances = finances == null
            ? baseUser.getFinances()
            : new FinanceProfile(
                finances.getIncome(),
                finances.getDebt(),
                finances.getSavings(),
                finances.getCash(),
                finances.getRoommates(),
                finances.getMonthlyEssentials()
            );

        return new User(
            baseUser.getId(),
            baseUser.getName(),
            baseUser.getEmail(),
            baseUser.getPhone(),
            baseUser.getRole(),
            city == null || city.isBlank() ? baseUser.getCity() : mapService.normalizeCity(city),
            mergedFinances,
            baseUser.getPreferences(),
            baseUser.getLocationPreference(),
            baseUser.getPasswordProfile(),
            baseUser.getMonthlySpendingEntries()
        );
    }

    private User buildUser(String id, OnboardingRequest request) {
        FinanceProfile finances = request.getFinances() == null
            ? new FinanceProfile(0, 0, 0, 0, 0, 0)
            : new FinanceProfile(
                request.getFinances().getIncome(),
                request.getFinances().getDebt(),
                request.getFinances().getSavings(),
                request.getFinances().getCash(),
                request.getFinances().getRoommates(),
                request.getFinances().getMonthlyEssentials()
            );

        UserPreferences preferences = new UserPreferences(
            request.getBeds() == null || request.getBeds().isBlank() ? "1 bed" : request.getBeds(),
            request.getMaxCommute(),
            request.getPriorities() == null ? List.of() : request.getPriorities(),
            request.getUseCase() == null ? "general budgeting" : request.getUseCase(),
            request.getBudgetingFor() == null ? "living" : request.getBudgetingFor()
        );

        LocationPreference locationPreference = new LocationPreference(
            request.getZipCode(),
            request.isUseCurrentLocation(),
            request.getCurrentLocationLabel()
        );

        return new User(
            id,
            request.getFullName(),
            request.getEmail(),
            request.getPhone(),
            request.getRole() == null ? UserRole.RENTER : request.getRole(),
            mapService.normalizeCity(request.getCity()),
            finances,
            preferences,
            locationPreference,
            buildPasswordProfile(request.getPassword(), null),
            List.of()
        );
    }

    private User buildMergedUser(User existing, OnboardingRequest request) {
        FinanceProfile currentFinances = existing.getFinances();
        FinanceProfile requestFinances = request.getFinances();
        FinanceProfile mergedFinances = requestFinances == null
            ? currentFinances
            : new FinanceProfile(
                requestFinances.getIncome(),
                requestFinances.getDebt(),
                requestFinances.getSavings(),
                requestFinances.getCash(),
                requestFinances.getRoommates(),
                requestFinances.getMonthlyEssentials()
            );

        UserPreferences currentPreferences = existing.getPreferences();
        UserPreferences mergedPreferences = new UserPreferences(
            request.getBeds() == null || request.getBeds().isBlank() ? currentPreferences.getBeds() : request.getBeds(),
            request.getMaxCommute() <= 0 ? currentPreferences.getMaxCommute() : request.getMaxCommute(),
            request.getPriorities() == null || request.getPriorities().isEmpty() ? currentPreferences.getPriorities() : request.getPriorities(),
            request.getUseCase() == null || request.getUseCase().isBlank() ? currentPreferences.getUseCase() : request.getUseCase(),
            request.getBudgetingFor() == null || request.getBudgetingFor().isBlank() ? currentPreferences.getBudgetingFor() : request.getBudgetingFor()
        );

        LocationPreference currentLocation = existing.getLocationPreference();
        LocationPreference mergedLocation = new LocationPreference(
            request.getZipCode() == null || request.getZipCode().isBlank() ? currentLocation.getZipCode() : request.getZipCode(),
            request.isUseCurrentLocation() || currentLocation.isUseCurrentLocation(),
            request.getCurrentLocationLabel() == null || request.getCurrentLocationLabel().isBlank()
                ? currentLocation.getCurrentLocationLabel()
                : request.getCurrentLocationLabel()
        );

        return new User(
            existing.getId(),
            request.getFullName() == null || request.getFullName().isBlank() ? existing.getName() : request.getFullName(),
            request.getEmail() == null || request.getEmail().isBlank() ? existing.getEmail() : request.getEmail(),
            request.getPhone() == null || request.getPhone().isBlank() ? existing.getPhone() : request.getPhone(),
            request.getRole() == null ? existing.getRole() : request.getRole(),
            request.getCity() == null || request.getCity().isBlank() ? existing.getCity() : mapService.normalizeCity(request.getCity()),
            mergedFinances,
            mergedPreferences,
            mergedLocation,
            buildPasswordProfile(request.getPassword(), existing.getPasswordProfile()),
            existing.getMonthlySpendingEntries()
        );
    }

    public User updateMonthlySpendingEntries(String userId, List<MonthlySpendingEntry> entries) {
        User existing = getUserOrDefault(userId);
        User updated = new User(
            existing.getId(),
            existing.getName(),
            existing.getEmail(),
            existing.getPhone(),
            existing.getRole(),
            existing.getCity(),
            existing.getFinances(),
            existing.getPreferences(),
            existing.getLocationPreference(),
            existing.getPasswordProfile(),
            entries
        );
        return userRepository.save(updated);
    }

    private PasswordProfile buildPasswordProfile(String plainPassword, PasswordProfile existingProfile) {
        if (plainPassword == null || plainPassword.isBlank()) {
            return existingProfile;
        }
        return passwordService.hashPassword(plainPassword);
    }
}
