package com.rentcheckme.backend.repository;

import com.rentcheckme.backend.model.FinanceProfile;
import com.rentcheckme.backend.model.User;
import com.rentcheckme.backend.model.UserPreferences;
import com.rentcheckme.backend.model.UserRole;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final List<User> users = List.of(
        new User("u-renter", "Jordan Carter", UserRole.RENTER, "Charlotte, NC",
            new FinanceProfile(4600, 350, 450, 5200, 0),
            new UserPreferences("1 bed", 35, List.of("budget", "walkability", "low fees"))),
        new User("u-agent", "Maya Singh", UserRole.AGENT, "Atlanta, GA",
            new FinanceProfile(6800, 420, 600, 9000, 0),
            new UserPreferences("1 bed", 30, List.of("inventory visibility", "commute", "move-in cash"))),
        new User("u-admin", "Casey Brooks", UserRole.ADMIN, "Durham, NC",
            new FinanceProfile(7200, 250, 800, 12500, 1),
            new UserPreferences("2 bed", 25, List.of("operations", "coverage", "risk")))
    );

    public List<User> findAll() {
        return users;
    }

    public Optional<User> findById(String userId) {
        return users.stream().filter(user -> user.getId().equals(userId)).findFirst();
    }
}
