package com.rentcheckme.backend.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentcheckme.backend.model.FinanceProfile;
import com.rentcheckme.backend.model.LocationPreference;
import com.rentcheckme.backend.model.MonthlySpendingEntry;
import com.rentcheckme.backend.model.PasswordProfile;
import com.rentcheckme.backend.model.User;
import com.rentcheckme.backend.model.UserPreferences;
import com.rentcheckme.backend.model.UserRole;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepository {

    private static final Path STORAGE_PATH = Path.of("data", "users.json");
    private final Map<String, User> users = new LinkedHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1000);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserRepository() {
        load();
        if (users.isEmpty()) {
            seedDefaults();
            persist();
        }
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public Optional<User> findById(String userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public Optional<User> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return users.values().stream()
            .filter(user -> email.equalsIgnoreCase(user.getEmail()))
            .findFirst();
    }

    public User save(User user) {
        users.put(user.getId(), user);
        persist();
        return user;
    }

    public User create(User user) {
        String id = "u-" + sequence.incrementAndGet();
        user.setId(id);
        return save(user);
    }

    private void load() {
        if (!Files.exists(STORAGE_PATH)) {
            return;
        }

        try {
            List<User> storedUsers = objectMapper.readValue(Files.readAllBytes(STORAGE_PATH), new TypeReference<>() { });
            storedUsers.forEach(user -> {
                users.put(user.getId(), user);
                long numericId = parseNumericId(user.getId());
                sequence.set(Math.max(sequence.get(), numericId));
            });
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load persisted users from " + STORAGE_PATH, exception);
        }
    }

    private void persist() {
        try {
            Files.createDirectories(STORAGE_PATH.getParent());
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(STORAGE_PATH.toFile(), users.values());
        } catch (IOException exception) {
            throw new IllegalStateException("Could not persist users to " + STORAGE_PATH, exception);
        }
    }

    private void seedDefaults() {
        users.clear();
        users.put("u-renter", new User("u-renter", "Jordan Carter", "jordan@example.com", "(704) 555-0181", UserRole.RENTER, "Charlotte, NC",
            new FinanceProfile(4600, 350, 450, 5200, 0, 980),
            new UserPreferences("1 bed", 35, List.of("budget", "walkability", "low fees"), "first apartment", "living"),
            new LocationPreference("28203", false, ""),
            defaultPasswordProfile(),
            defaultSpendingEntries("2026-02", "2026-03")));
        users.put("u-agent", new User("u-agent", "Maya Singh", "maya@example.com", "(404) 555-0118", UserRole.AGENT, "Atlanta, GA",
            new FinanceProfile(6800, 420, 600, 9000, 0, 1240),
            new UserPreferences("1 bed", 30, List.of("inventory visibility", "commute", "move-in cash"), "relocation", "both"),
            new LocationPreference("30312", false, ""),
            defaultPasswordProfile(),
            defaultSpendingEntries("2026-02", "2026-03")));
        users.put("u-admin", new User("u-admin", "Casey Brooks", "casey@example.com", "(919) 555-0108", UserRole.ADMIN, "Durham, NC",
            new FinanceProfile(7200, 250, 800, 12500, 1, 1180),
            new UserPreferences("2 bed", 25, List.of("operations", "coverage", "risk"), "team planning", "living"),
            new LocationPreference("27701", true, "Downtown Durham device location"),
            defaultPasswordProfile(),
            defaultSpendingEntries("2026-02", "2026-03")));
        sequence.set(1000);
    }

    private PasswordProfile defaultPasswordProfile() {
        return new PasswordProfile(
            "jsN67jg9lGi17iQ9jVX1EA==",
            "g4s/oDmkM0IGb4K+MzhImpgx2nOC6i4Zc1tVh4p7nTQ=",
            120000,
            "PBKDF2WithHmacSHA256"
        );
    }

    private long parseNumericId(String id) {
        if (id == null) {
            return 0;
        }
        String numeric = id.replaceAll("[^0-9]", "");
        if (numeric.isBlank()) {
            return 0;
        }
        return Long.parseLong(numeric);
    }

    private List<MonthlySpendingEntry> defaultSpendingEntries(String previousMonth, String currentMonth) {
        return List.of(
            new MonthlySpendingEntry(previousMonth, "Housing", "Rent and utilities", 1500, 1480),
            new MonthlySpendingEntry(previousMonth, "Food", "Groceries and dining", 500, 520),
            new MonthlySpendingEntry(previousMonth, "Transportation", "Transit, gas, parking", 300, 280),
            new MonthlySpendingEntry(currentMonth, "Housing", "Rent and utilities", 1500, 1525),
            new MonthlySpendingEntry(currentMonth, "Food", "Groceries and dining", 500, 470),
            new MonthlySpendingEntry(currentMonth, "Transportation", "Transit, gas, parking", 300, 325)
        );
    }
}
