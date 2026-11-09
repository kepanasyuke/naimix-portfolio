package ru.naimix.billing.api;

import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.naimix.billing.api.dto.CreateWalletRequest;
import ru.naimix.billing.api.dto.HoldRequest;
import ru.naimix.billing.api.dto.HoldView;
import ru.naimix.billing.api.dto.WalletView;
import ru.naimix.billing.service.BillingService;

/**
 * Wallet and hold endpoints.
 */
@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final BillingService billingService;

    /**
     * Creates the controller.
     *
     * @param billingService billing service
     */
    public BillingController(final BillingService billingService) {
        this.billingService = billingService;
    }

    /**
     * Opens a wallet.
     *
     * @param request creation payload
     * @return stored wallet
     */
    @PostMapping("/wallets")
    public ResponseEntity<WalletView> createWallet(@Valid @RequestBody final CreateWalletRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(billingService.createWallet(request));
    }

    /**
     * Returns the wallet balance.
     *
     * @param walletId wallet identifier
     * @return wallet projection
     */
    @GetMapping("/wallets/{walletId}")
    public WalletView balance(@PathVariable final UUID walletId) {
        return billingService.balance(walletId);
    }

    /**
     * Reserves funds.
     *
     * @param walletId wallet identifier
     * @param request hold payload
     * @return created hold
     */
    @PostMapping("/wallets/{walletId}/holds")
    public ResponseEntity<HoldView> hold(@PathVariable final UUID walletId,
                                         @Valid @RequestBody final HoldRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(billingService.hold(walletId, request));
    }

    /**
     * Captures a hold.
     *
     * @param holdId hold identifier
     * @return updated hold
     */
    @PostMapping("/holds/{holdId}/capture")
    public HoldView capture(@PathVariable final UUID holdId) {
        return billingService.capture(holdId);
    }

    /**
     * Releases a hold.
     *
     * @param holdId hold identifier
     * @return updated hold
     */
    @PostMapping("/holds/{holdId}/release")
    public HoldView release(@PathVariable final UUID holdId) {
        return billingService.release(holdId);
    }
}
