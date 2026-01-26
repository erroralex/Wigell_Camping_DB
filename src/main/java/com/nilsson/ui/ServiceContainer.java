package com.nilsson.ui;

import com.nilsson.service.AuthService;
import com.nilsson.service.InventoryService;
import com.nilsson.service.MemberService;
import com.nilsson.service.RentalService;

/**
 * A container class to make passing services down the UI-chain less cluttered.
 */
public class ServiceContainer {
    private final AuthService authService;
    private final MemberService memberService;
    private final RentalService rentalService;
    private final InventoryService inventoryService;

    public ServiceContainer(AuthService authService,
                            MemberService memberService,
                            RentalService rentalService,
                            InventoryService inventoryService) {

        this.authService = authService;
        this.memberService = memberService;
        this.rentalService = rentalService;
        this.inventoryService = inventoryService;
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
}
