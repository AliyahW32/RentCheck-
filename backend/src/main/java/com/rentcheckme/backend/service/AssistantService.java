package com.rentcheckme.backend.service;

import com.rentcheckme.backend.dto.AssistantQueryRequest;
import com.rentcheckme.backend.dto.AssistantResponse;
import com.rentcheckme.backend.dto.DashboardResponse;
import com.rentcheckme.backend.dto.ListingView;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssistantService {

    private static final List<String> ALLOWED_KEYWORDS = List.of(
        "rent", "budget", "housing", "apartment", "listing", "move-in", "deposit",
        "roommate", "commute", "area", "neighborhood", "fees", "afford", "lease", "city"
    );

    private final DashboardService dashboardService;
    private final MapService mapService;

    public AssistantService(DashboardService dashboardService, MapService mapService) {
        this.dashboardService = dashboardService;
        this.mapService = mapService;
    }

    public AssistantResponse answer(AssistantQueryRequest request) {
        String message = request.getMessage() == null ? "" : request.getMessage().toLowerCase();
        if (ALLOWED_KEYWORDS.stream().noneMatch(message::contains)) {
            return new AssistantResponse(false,
                "I can only answer questions about housing affordability, listings, neighborhoods, move-in costs, roommates, and budgeting inside RentCheck Me.");
        }

        DashboardResponse dashboard = dashboardService.getDashboard(request.getUserId(), request.getAreaIds());
        ListingView topListing = dashboard.getListings().stream().findFirst().orElse(null);

        if (message.contains("move-in") || message.contains("deposit")) {
            String sample = topListing == null
                ? "There are no listings in the selected area right now."
                : topListing.getTitle() + " needs about $" + Math.round(topListing.getMoveInCash()) + " upfront.";
            return new AssistantResponse(true,
                "You currently have a housing budget of $" + Math.round(dashboard.getBudget().getHousingBudget())
                    + " per month and $" + Math.round(dashboard.getUser().getFinances().getCash())
                    + " in move-in cash. " + sample
                    + " Focus on application fee plus first month plus deposit before stretching on monthly rent.");
        }

        if (message.contains("roommate")) {
            ListingView sharedListing = dashboard.getListings().stream()
                .filter(listing -> listing.getShareDivisor() > 1)
                .findFirst()
                .orElse(null);
            if (sharedListing == null) {
                return new AssistantResponse(true,
                    "There is no shared listing in the current area filter. Clear the map selection or switch cities to compare roommate options.");
            }
            return new AssistantResponse(true,
                "A roommate changes the math most on shared 2-bedroom listings. "
                    + sharedListing.getTitle() + " lands near $" + Math.round(sharedListing.getMonthlyTotal())
                    + " per person monthly in the current filter.");
        }

        List<String> areaNames = mapService.areaNames(dashboard.getUser().getCity(), request.getAreaIds());
        String areaSentence = areaNames.isEmpty()
            ? "across " + dashboard.getUser().getCity()
            : "inside " + String.join(", ", areaNames);
        String suggestion = topListing == null
            ? "No listing currently matches the selected area."
            : topListing.getTitle() + " is the best current fit at about $" + Math.round(topListing.getMonthlyTotal()) + " per month.";

        return new AssistantResponse(true,
            "Your current affordability cap is $" + Math.round(dashboard.getBudget().getHousingBudget())
                + " per month " + areaSentence + ". " + suggestion
                + " Hidden monthly costs are already included, so compare total monthly cost instead of base rent alone.");
    }
}
