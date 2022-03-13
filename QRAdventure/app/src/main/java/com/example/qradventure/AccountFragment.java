package com.example.qradventure;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {
    Account account;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            account = CurrentAccount.getAccount();
            try {
                TextView displayTotalScore = view.findViewById(R.id.total_score);

                displayTotalScore.setText(detailFormatter(getTotalScore()));
                TextView displayCodesScanned = view.findViewById(R.id.codes_scanned);
                displayCodesScanned.setText(detailFormatter(getCodesScanned()));
                TextView displayLowestQR = view.findViewById(R.id.lowest_qr);
                displayLowestQR.setText(detailFormatter(getLowestQR()));
                TextView displayHighestQR = view.findViewById(R.id.highest_qr);
                displayHighestQR.setText(detailFormatter(getHighestQR()));
            }
            catch (Exception e) {
                Log.d("logs", e.toString());
            }
            String username = account.getUsername();
            Log.d("logs", username);
            String email = account.getEmail();
            String phoneNumber = account.getPhoneNumber();
            TextView displayUsername = view.findViewById(R.id.user_username);
            displayUsername.setText(username);
            TextView displayEmail = view.findViewById(R.id.user_email);
            displayEmail.setText(email);
            TextView displayPhoneNumber = view.findViewById(R.id.user_phone_number);
            displayPhoneNumber.setText(phoneNumber);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        Log.d("logs", "setting text out here");


        return inflater.inflate(R.layout.fragment_account, container, false);
    }
    // if the number is too big, put it in this format
    // for example, if the user has 1345 qr's scanned
    // it would say 1.34k in the page
    private String detailFormatter(Number number) {
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.00").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }
    // gets the lowest QR the player has scan to display
    private int getLowestQR() {
        int smallest = account.getMyRecords().get(0).getQRscore();
        for (Record record: account.getMyRecords()
        ) {
            if (record.getQRscore() < smallest){
                smallest = record.getQRscore();
            }
        }
        return smallest;
    }

    // gets the highest QR the player has scan to display
    private int getHighestQR() {
        int biggest = account.getMyRecords().get(0).getQRscore();
        for (Record record: account.getMyRecords()
        ) {
            if (record.getQRscore() > biggest){
                biggest = record.getQRscore();
            }
        }
        return biggest;
    }

    // gets the number of codes scanned by player so we can display it
    private int getCodesScanned() {
        return account.getMyRecords().size();
    }

    // gets the cumulative score player has scanned so we can display it
    private int getTotalScore() {
        int sum = 0;
        for (Record record: account.getMyRecords()
        ) {
            sum += record.getQRscore();
        }
        return sum;
    }

}