package com.example.wallet.ui.TransactionHistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wallet.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class TransactionHistoryAdapter
        extends RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder> {

    private List<TransactionHistoryData> transactions;

    public TransactionHistoryAdapter(List<TransactionHistoryData> transactionHistoryDataList) {
        this.transactions = transactionHistoryDataList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTransactionStatus;
        public TextView tvTransactionAmount;
        public TextView tvTransactionDate;
        public TextView tvTransactionType;
        public TextView tvBeneficiaryInstrument;
        public TextView tvTransactionInstrument;

        public ViewHolder(View v) {
            super(v);
            tvTransactionAmount = v.findViewById(R.id.tv_history_amount);
            tvTransactionDate = v.findViewById(R.id.tv_transaction_date);
            tvTransactionType = v.findViewById(R.id.tv_transaction_type);
            tvBeneficiaryInstrument = v.findViewById(R.id.tv_beneficiary_instrument);
            tvTransactionInstrument = v.findViewById(R.id.tv_transaction_instrument);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_transaction_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TransactionHistoryData transaction = transactions.get(position);

        holder.tvTransactionAmount.setText(transaction.getTransactionAmountWithCurrency());
        holder.tvTransactionDate.setText(transaction.getTransactionFormattedDateAndTime());
        holder.tvBeneficiaryInstrument.setText(transaction.getWalletId());
        holder.tvTransactionInstrument.setText(transaction.getTransactionId());

        /*if (isBalancePositive(balance) && context != null) {
            int color = ContextCompat.getColor(context, R.color.colorAccentBlue);
            holder.tvTransactionAmount.setTextColor(color);
        }*/

       /* switch (transaction.getTransactionType()) {
            case DEBIT:
                holder.tvTransactionStatus.setText(Constants.DEBIT);
                break;
            case CREDIT:
                holder.tvTransactionStatus.setText(Constants.CREDIT);
                break;
            case OTHER:
                holder.tvTransactionStatus.setText(Constants.OTHER);
                break;
        }*/
    }

    /*private boolean isBalancePositive(String balance) {
        balance = balance.replaceAll("[,.]", "");
        return Double.parseDouble(balance) > 0;
    }*/

    @Override
    public int getItemCount() {
        if (transactions != null) {
            return transactions.size();
        } else {
            return 0;
        }
    }

    /*public void setContext(Context context) {
    }*/

    /*public void setData(List<TransactionHistoryData> transactions) {
        this.transactions = transactions;

    }

    public ArrayList<TransactionHistoryData> getTransactions() {
        return (ArrayList<TransactionHistoryData>) transactions;
    }

    public TransactionHistoryData getTransaction(int position) {
        return transactions.get(position);
    }*/

}

