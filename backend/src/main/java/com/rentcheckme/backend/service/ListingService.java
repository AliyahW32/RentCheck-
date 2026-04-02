package com.rentcheckme.backend.service;

import com.rentcheckme.backend.dto.BudgetResponse;
import com.rentcheckme.backend.dto.ListingView;
import com.rentcheckme.backend.model.Listing;
import com.rentcheckme.backend.model.User;
import com.rentcheckme.backend.repository.ListingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListingService {

    private final ListingRepository listingRepository;

    public ListingService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    public List<ListingView> getListingsForUser(User user, BudgetResponse budget, List<String> areaIds) {
        return listingRepository.findAll().stream()
            .filter(listing -> listing.getCity().equals(user.getCity()))
            .filter(listing -> areaIds == null || areaIds.isEmpty() || areaIds.contains(listing.getAreaId()))
            .map(listing -> toListingView(listing, user, budget))
            .sorted((left, right) -> Double.compare(left.getMonthlyTotal(), right.getMonthlyTotal()))
            .toList();
    }

    private ListingView toListingView(Listing listing, User user, BudgetResponse budget) {
        int shareDivisor = user.getFinances().getRoommates() > 0 && "2 bed".equals(listing.getBeds())
            ? user.getFinances().getRoommates() + 1
            : 1;

        double monthlyHidden = listing.getUtilities() + listing.getInternet() + listing.getParking()
            + listing.getInsurance() + listing.getTransit() + listing.getPet() + listing.getFees().getAmenity();
        double monthlyTotal = (listing.getRent() + monthlyHidden) / shareDivisor;
        double moveInCash = (listing.getDeposit() + listing.getRent() + listing.getFees().getApplication()) / shareDivisor;

        String fitLabel = "Not realistic";
        String fitClassName = "fit-bad";

        if (monthlyTotal <= budget.getHousingBudget() && moveInCash <= user.getFinances().getCash()) {
            fitLabel = "Strong fit";
            fitClassName = "fit-good";
        } else if (monthlyTotal <= budget.getHousingBudget() * 1.1 && moveInCash <= user.getFinances().getCash() * 1.05) {
            fitLabel = "Tight fit";
            fitClassName = "fit-close";
        }

        return new ListingView(
            listing,
            monthlyHidden / shareDivisor,
            monthlyTotal,
            moveInCash,
            shareDivisor,
            fitLabel,
            fitClassName
        );
    }
}
