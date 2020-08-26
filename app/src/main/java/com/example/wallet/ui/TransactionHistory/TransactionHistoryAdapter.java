package com.example.wallet.ui.TransactionHistory;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wallet.R;

import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class TransactionHistoryAdapter
        extends RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder> {

    private List<TransactionHistoryData> transactions;
    private ClickListener clickListener;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public TransactionHistoryAdapter(List<TransactionHistoryData> transactionHistoryDataList, ClickListener clickListener) {
        this.transactions = transactionHistoryDataList;
        this.clickListener = clickListener;
        Log.d("data", "transactions data: " + transactions);
        transactions.sort(Comparator.comparing(TransactionHistoryData::getTransactionDateAndTime).reversed());
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvTransactionStatus;
        public TextView tvTransactionAmount;
        public TextView tvTransactionDate;
        public TextView tvTransactionType;
        public TextView tvBeneficiaryInstrument;
        public TextView tvTransactionInstrument;
        public ImageView ivTransactionTypeLogo;
        public View cvTransactionItem;
        private ClickListener mListener;

        public ViewHolder(View v, ClickListener clickListener) {
            super(v);
            this.mListener = clickListener;
            v.setOnClickListener(this);
            tvTransactionAmount = v.findViewById(R.id.tv_history_amount);
            tvTransactionDate = v.findViewById(R.id.tv_transaction_date);
            tvTransactionType = v.findViewById(R.id.tv_transaction_type);
            tvBeneficiaryInstrument = v.findViewById(R.id.tv_beneficiary_instrument);
            tvTransactionInstrument = v.findViewById(R.id.tv_transaction_instrument);
            ivTransactionTypeLogo = v.findViewById(R.id.iv_transaction_type_logo);
            cvTransactionItem = v.findViewById(R.id.cv_transaction_item);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(getAdapterPosition(), v);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_transaction_history, parent, false);
        return new ViewHolder(v, clickListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionHistoryData transaction = (TransactionHistoryData) transactions.get(position);
        if (transaction != null) {
            Log.d("data", "transactionFormattedDateAndTime data: " + transaction.transactionFormattedDateAndTime());
            holder.tvTransactionAmount.setText(transaction.transactionAmountWithCurrency());
            holder.tvTransactionDate.setText(transaction.transactionFormattedDateAndTime());
            holder.tvBeneficiaryInstrument.setText(transaction.getWalletId());
            holder.tvTransactionInstrument.setText(transaction.getTransactionType());
            String walletId = transaction.getWalletId();
            String transactionType = transaction.getTransactionType();
            String[] transactionTypeArray = transactionType.split(":");
            String[] walletIdArray = walletId.split(" ");
            if (walletId.equalsIgnoreCase("JusTap")) {
                holder.tvTransactionType.setText("Money added to");
                holder.ivTransactionTypeLogo.setImageResource(R.drawable.ic_transaction_wallet);
            }
            if (walletIdArray.length == 1) {
                if ("justap".equals(walletIdArray[0].toLowerCase())) {
                    holder.tvTransactionType.setText("Money added to");
                    holder.ivTransactionTypeLogo.setImageResource(R.drawable.ic_transaction_wallet);
                } else {
                    holder.tvTransactionType.setText("Send to");
                    holder.ivTransactionTypeLogo.setImageResource(R.drawable.ic_user);
                }
            }
            if (walletIdArray.length > 1) {
                switch (walletIdArray[1].toLowerCase()) {
                    case "metro":
                        holder.tvTransactionType.setText("Paid to");
                        holder.ivTransactionTypeLogo.setImageResource(R.drawable.ic_underground_metro);
                        break;
                    case "bus":
                        holder.tvTransactionType.setText("Paid to");
                        holder.ivTransactionTypeLogo.setImageResource(R.drawable.ic_bus_ticket);
                        break;
                    case "wallet":
                        holder.tvTransactionType.setText("Money added to");
                        holder.ivTransactionTypeLogo.setImageResource(R.drawable.ic_transaction_wallet);
                        break;
                    default:
                        holder.tvTransactionType.setText("Send to");
                        holder.ivTransactionTypeLogo.setImageResource(R.drawable.ic_user);
                }
            }
            if (walletIdArray[0].equalsIgnoreCase("metro")) {
                holder.ivTransactionTypeLogo.setImageResource(R.drawable.ic_underground_metro);
            }
            if (transactionTypeArray[0].equalsIgnoreCase("Credited to")) {
                holder.tvTransactionType.setText("Received from");
            }
            holder.cvTransactionItem.setOnClickListener(v -> clickListener.onClick(holder.getAdapterPosition(), v));
        }
    }

    @Override
    public int getItemCount() {
        if (transactions != null) {
            return transactions.size();
        } else {
            return 0;
        }
    }

    interface ClickListener {
        void onClick(int position, View v);
    }
}


