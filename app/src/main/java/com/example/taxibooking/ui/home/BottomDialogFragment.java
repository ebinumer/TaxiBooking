package com.example.taxibooking.ui.home;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.taxibooking.R;
import com.example.taxibooking.data.prefrence.SessionManager;
import com.example.taxibooking.ui.SplashActivity;
import com.example.taxibooking.ui.trip.TripActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class BottomDialogFragment extends BottomSheetDialogFragment {
    Double distance;
    private SessionManager sessionManager;
   /* public static BottomDialogFragment newInstance(Double fare, Double distance) {
        BottomDialogFragment fragment = new BottomDialogFragment();
        this.fare = fare;
        return fragment;
    }*/

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public BottomDialogFragment(Double distance) {
        this.distance = distance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.booking_confirm_layout, container, false);
        TextView distanceText = view.findViewById(R.id.tv_distance);
        Button btnConfirm = view.findViewById(R.id.btn_confirm);
        distanceText.setText(df.format(distance / 1000) + " Km");
        calculateFare();
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.setReqTrip(true);
                Intent i;
                i = new Intent(requireContext(), TripActivity.class);
                startActivity(i);
            }
        });

        return view;
    }


    private void calculateFare() {
        double totalFare;
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(distance / 1000));
        int intValue = bigDecimal.intValue();
        Log.d("fare calculation", String.valueOf(intValue));
    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.booking_confirm_layout, null);
        dialog.setContentView(contentView);
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) contentView.getParent());
        mBehavior.setPeekHeight(300);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }
}
