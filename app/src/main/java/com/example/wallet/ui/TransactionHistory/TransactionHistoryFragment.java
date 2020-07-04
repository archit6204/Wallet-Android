package com.example.wallet.ui.TransactionHistory;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.wallet.R;
import com.example.wallet.ui.wallet.AddMoneyData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class TransactionHistoryFragment extends Fragment {

    private RecyclerView rvTransactionHistory;
    private List<TransactionHistoryData> transactionHistoryDataList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TransactionHistoryAdapter mTransactionHistoryAdapter;
    private ProgressBar progressBar;
    final DocumentReference userRef = db.collection("users").document("user");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*final DocumentReference userRef = db.collection("users").document("user");
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    AddMoneyData addMoneyData = documentSnapshot.toObject(AddMoneyData.class);
                    assert addMoneyData != null;
                    transactionHistoryDataList = (List<TransactionHistoryData>) documentSnapshot.get("transactionHistoryData");
                    *//*transactionHistoryDataList = addMoneyData.getTransactionHistoryData();*//*
                    Log.d("data", "getTransactionHistoryData data: " + transactionHistoryDataList);
                }
            }
        });*/

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_history, container, false);
        rvTransactionHistory = view.findViewById(R.id.rv_transaction_history);
        progressBar = view.findViewById(R.id.pb_transaction_history);
        prepareTransactionData();
        return view;
    }



    private void prepareTransactionData() {
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        AddMoneyData addMoneyData = document.toObject(AddMoneyData.class);
                        assert addMoneyData != null;
                        Map<String, Object> map = document.getData();
                        assert map != null;
                        /*for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey().equals("transactionHistoryData")) {
                               TransactionHistoryData transactionHistoryData = (TransactionHistoryData) entry.getValue();
                                transactionHistoryDataList.add(transactionHistoryData);
                                Log.d("TAG", entry.getValue().toString());
                            }
                        }*/
                        transactionHistoryDataList = addMoneyData.getTransactionHistoryData();
                        /*transactionHistoryDataList = (List<TransactionHistoryData>) document.get("transactionHistoryData");*/
                        /*mTransactionHistoryAdapter.notifyDataSetChanged();*/
                        setupRecyclerView();
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        Log.d("data", "DocumentSnapshot transactionHistoryDataList: " + transactionHistoryDataList);
                    } else {
                        Log.d("error", "No sucList<TransactionHistoryData>h document");
                    }
                } else {
                    Log.d("failed fetch", "get failed with ", task.getException());
                }
            }
        });
        /*TransactionHistoryData transactionHistoryData = new TransactionHistoryData("Debited from: JusTap Wallet",300, "Mumbai metro", "4 hours ago");
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
        transactionHistoryDataList.add(transactionHistoryData);*/
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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
    }
}