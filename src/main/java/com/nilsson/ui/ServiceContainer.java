package com.nilsson.ui;

import com.nilsson.service.*;

/**
 * A container class to make passing services down the UI-chain less cluttered.
 */
public class ServiceContainer {
    private final AuthService authService;
    private final MemberService memberService;
    private final RentalService rentalService;
    private final InventoryService inventoryService;
    private final ProfitsService profitsService;

    public ServiceContainer(AuthService authService,
                            MemberService memberService,
                            RentalService rentalService,
                            InventoryService inventoryService,
                            ProfitsService profitsService) {

        this.authService = authService;
        this.memberService = memberService;
        this.rentalService = rentalService;
        this.inventoryService = inventoryService;
        this.profitsService = profitsService;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public MemberService getMemberService() {
        return memberService;
    }

    public RentalService getRentalService() {
        return rentalService;
    }

    public InventoryService getInventoryService() {
        return inventoryService;
    }

    public ProfitsService getProfitsService() {
        return profitsService;
    }
}
