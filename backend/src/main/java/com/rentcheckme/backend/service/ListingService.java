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
    private final MapService mapService;

    public ListingService(ListingRepository listingRepository, MapService mapService) {
        this.listingRepository = listingRepository;
        this.mapService = mapService;
    }

    public List<ListingView> getListingsForUser(User user, BudgetResponse budget, List<String> areaIds) {
        String normalizedCity = mapService.normalizeCity(user.getCity());
        return listingRepository.findAll().stream()
            .filter(listing -> listing.getCity().equals(normalizedCity))
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
        double sharedRent = listing.getRent() / shareDivisor;
        double monthlyHiddenPerShare = monthlyHidden / shareDivisor;
        double monthlyTotal = sharedRent + monthlyHiddenPerShare;
        double moveInCash = (listing.getDeposit() + listing.getRent() + listing.getFees().getApplication()) / shareDivisor;
        double leftoverAfterHousing = Math.max(0, budget.getHousingBudget() - monthlyTotal);

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
            monthlyHiddenPerShare,
            monthlyTotal,
            moveInCash,
            sharedRent,
            leftoverAfterHousing,
            shareDivisor,
            fitLabel,
            fitClassName
        );
    }
}
