package com.example.wallet.ui.TransactionHistory;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.example.wallet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class TransactionHistoryFragment extends Fragment {

    private TransactionHistoryAdapter mTransactionHistoryAdapter;
    private RecyclerView rvTransactionHistory;
    private List<TransactionHistoryData> transactionHistoryDataList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_history, container, false);
        rvTransactionHistory = view.findViewById(R.id.rv_transaction_history);
        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView() {
        mTransactionHistoryAdapter = new TransactionHistoryAdapter(transactionHistoryDataList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvTransactionHistory.setLayoutManager(mLayoutManager);
        rvTransactionHistory.setItemAnimator(new DefaultItemAnimator());
        rvTransactionHistory.setAdapter(mTransactionHistoryAdapter);
        /*rvTransactionHistory.addOnItemTouchListener(new RecyclerView.OnItemTouchListener(getContext(),
                new RecyclerView.OnItemTouchListener() {
                    @Override
                    public void onItemClick(View childView, int position) {
                        mPresenter.handleTransactionClick(position);
                    }

                    @Override
                    public void onItemLongPress(View childView, int position) {

                    }
                }) {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });*/
        prepareTransactionData();
    }

    private void prepareTransactionData() {
        DocumentReference documentsRef = db.collection("users").document("user");
        documentsRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        Log.d("data", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("error", "No such document");
                    }
                } else {
                    Log.d("failed fetch", "get failed with ", task.getException());
                }
            }
        });


        TransactionHistoryData transactionHistoryData = new TransactionHistoryData("Debited from: JusTap Wallet",300, "Mumbai metro", "4 hours ago");
        transactionHistoryDataList.add(transactionHistoryData);

        transactionHistoryData = new TransactionHistoryData(
                "Debited from: HDFC bank",
                1300,
                "JusTap ",
                "1 days ago");
        transactionHistoryDataList.add(transactionHistoryData);

        transactionHistoryData = new TransactionHistoryData(
                "Debited from: AXIS  bank",
                300,
                "JusTap ",
                "2 days ago");
        transactionHistoryDataList.add(transactionHistoryData);

        transactionHistoryData = new TransactionHistoryData("Debited from: UPI",3300, "JusTap ", "5 days ago");
        transactionHistoryDataList.add(transactionHistoryData);

        transactionHistoryData = new TransactionHistoryData("Debited from: UPI",66300, "JusTap ", "6 days ago");
        transactionHistoryDataList.add(transactionHistoryData);

        transactionHistoryData = new TransactionHistoryData("Debited from: JusTap Wallet",7340, "Delhi metro", "6 days ago");
        transactionHistoryDataList.add(transactionHistoryData);

        transactionHistoryData = new TransactionHistoryData("Debited from: JusTap Wallet",2, "Delhi metro", "2020");
        transactionHistoryDataList.add(transactionHistoryData);

        transactionHistoryData = new TransactionHistoryData("Debited from: JusTap Wallet",1, "Noida metro", "2019");
        transactionHistoryDataList.add(transactionHistoryData);

        transactionHistoryData = new TransactionHistoryData("Debited from: JusTap Wallet",77, "Noida metro", "2015");
        transactionHistoryDataList.add(transactionHistoryData);

        transactionHistoryData = new TransactionHistoryData("Debited from: JusTap Wallet",300, "Mumbai metro", "2015");
        transactionHistoryDataList.add(transactionHistoryData);

        mTransactionHistoryAdapter.notifyDataSetChanged();
    }
}