package ru.naimix.billing.service;

import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.naimix.billing.api.dto.CreateWalletRequest;
import ru.naimix.billing.api.dto.HoldRequest;
import ru.naimix.billing.api.dto.HoldView;
import ru.naimix.billing.api.dto.WalletView;
import ru.naimix.billing.domain.PaymentHold;
import ru.naimix.billing.domain.PaymentHoldRepository;
import ru.naimix.billing.domain.Wallet;
import ru.naimix.billing.domain.WalletRepository;
import ru.naimix.billing.error.HoldNotFoundException;
import ru.naimix.billing.error.WalletNotFoundException;

/**
 * Wallet operations with idempotent holds.
 */
@Service
public class BillingService {

    private final WalletRepository wallets;
    private final PaymentHoldRepository holds;

    /**
     * Creates the service.
     *
     * @param wallets wallet storage
     * @param holds hold storage
     */
    public BillingService(final WalletRepository wallets, final PaymentHoldRepository holds) {
        this.wallets = wallets;
        this.holds = holds;
    }

    /**
     * Opens a wallet.
     *
     * @param request creation payload
     * @return stored wallet
     */
    @Transactional
    public WalletView createWallet(final CreateWalletRequest request) {
        return toWalletView(wallets.findByOwnerId(request.ownerId()).orElseGet(() -> store(request)));
    }

    private Wallet store(final CreateWalletRequest request) {
        return wallets.save(new Wallet(request.ownerId(), request.initialBalance(), request.currency()));
    }

    /**
     * Returns the wallet balance.
     *
     * @param walletId wallet identifier
     * @return wallet projection
     */
    @Transactional(readOnly = true)
    public WalletView balance(final UUID walletId) {
        return toWalletView(requireWallet(walletId));
    }

    /**
     * Reserves funds exactly once per idempotency key.
     *
     * @param walletId wallet identifier
     * @param request hold payload
     * @return created or previously stored hold
     */
    @Transactional
    public HoldView hold(final UUID walletId, final HoldRequest request) {
        return existing(request.idempotencyKey()).orElseGet(() -> place(walletId, request));
    }

    /**
     * Captures a hold.
     *
     * @param holdId hold identifier
     * @return updated hold
     */
    @Transactional
    public HoldView capture(final UUID holdId) {
        return toHoldView(captured(requireHold(holdId)));
    }

    /**
     * Releases a hold and returns the funds.
     *
     * @param holdId hold identifier
     * @return updated hold
     */
    @Transactional
    public HoldView release(final UUID holdId) {
        return toHoldView(refunded(requireHold(holdId)));
    }

    private Optional<HoldView> existing(final String idempotencyKey) {
        return holds.findByIdempotencyKey(idempotencyKey).map(BillingService::toHoldView);
    }

    private HoldView place(final UUID walletId, final HoldRequest request) {
        final Wallet wallet = lockedWallet(walletId);
        return toHoldView(holds.save(holdOf(wallet, request)));
    }

    private PaymentHold holdOf(final Wallet wallet, final HoldRequest request) {
        return new PaymentHold(wallet.hold(request.amount()), request.amount(), request.idempotencyKey());
    }

    private PaymentHold captured(final PaymentHold hold) {
        hold.capture();
        return holds.save(hold);
    }

    private PaymentHold refunded(final PaymentHold hold) {
        lockedWallet(hold.getWalletId()).refund(hold.release());
        return holds.save(hold);
    }

    private Wallet lockedWallet(final UUID walletId) {
        return wallets.findByIdForUpdate(walletId).orElseThrow(() -> new WalletNotFoundException(walletId));
    }

    private Wallet requireWallet(final UUID walletId) {
        return wallets.findById(walletId).orElseThrow(() -> new WalletNotFoundException(walletId));
    }

    private PaymentHold requireHold(final UUID holdId) {
        return holds.findById(holdId).orElseThrow(() -> new HoldNotFoundException(holdId));
    }

    private static WalletView toWalletView(final Wallet wallet) {
        return new WalletView(wallet.getId(), wallet.getOwnerId(), wallet.getBalance(), wallet.getCurrency());
    }

    private static HoldView toHoldView(final PaymentHold hold) {
        return new HoldView(hold.getId(), hold.getWalletId(), hold.getAmount(), hold.getStatus());
    }
}
