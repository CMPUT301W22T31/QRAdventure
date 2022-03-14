package com.example.qradventure;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Fragment of list of codes scanned by player
 * To be developed further
 */
public class MyCodesFragment extends Fragment {
    GridView qrList;
    FirebaseFirestore db;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyCodesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyCodesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyCodesFragment newInstance(String param1, String param2) {
        MyCodesFragment fragment = new MyCodesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        try {
            db = FirebaseFirestore.getInstance();

            super.onCreate(savedInstanceState);
            inflater.inflate(R.layout.fragment_my_codes, container, false);

            try {
                qrList = getView().findViewById(R.id.qr_list);
            }
            catch (Exception d) {
                Log.d("logs","this is null");
            }
            Account myAccount = CurrentAccount.getAccount();

            Log.d("logs", "Logged in as" + myAccount.getUsername());
            ArrayList<Record> accountRecords = myAccount.getMyRecords();
            //Log.d("logs", "" + accountRecords.size());
            for (Record record : accountRecords
            ) {
                Log.d("logs", record.getQRHash().substring(0, 4) + " " + record.getQRscore());
            }

            //String[] pts = {"23","342","34","34"};

            QRListAdapter qrListAdapter = new QRListAdapter(getActivity(), accountRecords);
            qrList.setAdapter(qrListAdapter);

//        Log.d("logs",""+ qrListAdapter.qrRecords.length);

            qrList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent(getActivity(), QRPageActivity.class);
                    intent.putExtra("QRtitle", accountRecords.get(position).getQRHash().substring(0, 4));
                    startActivity(intent);
                }
            });

            qrList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    String QRRecord = myAccount.getUsername() + "-" + myAccount.getMyRecords().get(position).getQRHash();
                    try {
                        db.collection("AccountDB")
                                .document(myAccount.getUsername())
                                .collection("My QR Records")
                                .document(QRRecord)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("logs", "DocumentSnapshot successfully deleted!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("logs", "Error deleting document", e);
                                    }
                                });
                        db.collection("QRDB")
                                .document(myAccount.getMyRecords().get(position).getQRHash())
                                .collection("Scanned By")
                                .document(myAccount.getUsername())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("logs", "DocumentSnapshot successfully deleted!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("logs", "Error deleting document", e);
                                    }
                                });
                        db.collection("RecordDB")
                                .document(QRRecord)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("logs", "DocumentSnapshot successfully deleted!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("logs", "Error deleting document", e);
                                    }
                                });
                    } catch (Exception e) {
                        Log.d("logs", e.toString());
                    }
                    Log.d("logs", QRRecord);
                    myAccount.getMyRecords().remove(position);
                    qrListAdapter.notifyDataSetChanged();
                    return false;
                }
            });
        }
        catch (Exception e) {
            Log.d("logs", "hi");
        }
        return inflater.inflate(R.layout.fragment_my_codes, container, false);
    }
}