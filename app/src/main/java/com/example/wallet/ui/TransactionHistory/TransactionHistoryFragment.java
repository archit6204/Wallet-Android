package com.example.wallet.ui.TransactionHistory;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.wallet.R;
import com.example.wallet.ui.utils.GlobalVariables;
import com.example.wallet.ui.wallet.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private String userName;
    private TextView noTransactionFound;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalVariables globalVariables = (GlobalVariables) Objects.requireNonNull(getActivity()).getApplication();
        assert globalVariables != null;
        userName = globalVariables.getUserName();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_history, container, false);
        rvTransactionHistory = view.findViewById(R.id.rv_transaction_history);
        progressBar = view.findViewById(R.id.pb_transaction_history);
        noTransactionFound = view.findViewById(R.id.tv_no_transaction_found);
        prepareTransactionData();
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void prepareTransactionData() {
        DocumentReference userRef = db.collection("users").document(userName);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                assert document != null;
                if (document.exists()) {
                    UserData addMoneyData = document.toObject(UserData.class);
                    assert addMoneyData != null;
                    Map<String, Object> map = document.getData();
                    assert map != null;
                    transactionHistoryDataList = addMoneyData.getTransactionHistoryData();
                    Log.d("data", "transactionHistoryDataList.size:" + transactionHistoryDataList.size());
                    if (transactionHistoryDataList.size() >= 1) {
                        TransactionHistoryData transactionHistoryData = transactionHistoryDataList.get(0);
                        if (transactionHistoryData == null) {
                            rvTransactionHistory.setVisibility(View.GONE);
                            noTransactionFound.setVisibility(View.VISIBLE);
                            userRef.update("transactionHistoryData", FieldValue.arrayRemove((TransactionHistoryData) null));
                        } else {
                            setupRecyclerView();
                        }
                    } else {
                        rvTransactionHistory.setVisibility(View.GONE);
                        noTransactionFound.setVisibility(View.VISIBLE);
                    }

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
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setupRecyclerView() {
        mTransactionHistoryAdapter = new TransactionHistoryAdapter(transactionHistoryDataList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvTransactionHistory.setLayoutManager(mLayoutManager);
        rvTransactionHistory.setItemAnimator(new DefaultItemAnimator());
        rvTransactionHistory.setAdapter(mTransactionHistoryAdapter);
        if (transactionHistoryDataList.size() > 1) {

        }
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