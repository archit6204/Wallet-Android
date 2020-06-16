package com.example.wallet.ui.wallet;

public class AvailableBalanceData {

    private String transactionId;
    private int moneyAmount;
    private String walletId;

    public AvailableBalanceData(){

    }

    public AvailableBalanceData(String transactionId, int moneyAmount, String walletId) {
        this.transactionId = transactionId;
        this.moneyAmount = moneyAmount;
        this.walletId = walletId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public int getMoneyAmount() {
        return moneyAmount;
    }

    public String getWalletId() {
        return walletId;
    }
}