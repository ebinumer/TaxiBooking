package com.example.taxibooking.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.example.taxibooking.BaseActivity;
import com.example.taxibooking.R;
import com.example.taxibooking.data.prefrence.SessionManager;
import com.example.taxibooking.databinding.ActivityPaymentBinding;
import com.example.taxibooking.ui.auth.SignUpActivity;
import com.example.taxibooking.ui.home.HomeActivity;
import com.example.taxibooking.ui.trip.TripActivity;
import com.example.taxibooking.utils.NetworkManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends BaseActivity {
    private ActivityPaymentBinding binding;
    private String type;
    String checkIn, checkOut, total,image,location,name,Distence,Price;
    int adult, children, room;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionManager = new SessionManager(this);
        initView();
    }

    private void initView() {

         Distence = getIntent().getStringExtra("Distence");
         Price = getIntent().getStringExtra("price");
         binding.tvDistance.setText(Distence+ " Km");
         binding.tvDistance.setText(Distence+ " Km");
         binding.tvPound.setText(Price+ " £");

        binding.radioDebit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    binding.cardDebitDetails.setVisibility(View.VISIBLE);
                    binding.buttonConfirmOrder.setVisibility(View.GONE);
                    binding.radioCod.setChecked(false);
                } else {
                    binding.cardDebitDetails.setVisibility(View.GONE);
                }
            }
        });

        binding.radioCod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    binding.radioDebit.setChecked(false);
                    binding.buttonConfirmOrder.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.buttonDebitPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.etCard.getText().toString().isEmpty()) {
                    if (!binding.etExpiry.getText().toString().isEmpty()) {
                        if (!binding.etCvv.getText().toString().isEmpty()) {
                            type = "card";
                            if (NetworkManager.isNetworkAvailable(PaymentActivity.this)) {
                                goToOrderSuccess();
                            } else {
                                binding.containerNoInternet.setVisibility(View.VISIBLE);
                            }
                        } else binding.etCvv.setError("Please enter CVV");
                    } else binding.etExpiry.setError("Please enter expiry");
                } else binding.etCard.setError("Please enter card no");
            }
        });

        binding.buttonConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoading(PaymentActivity.this);
                type = "direct";
                if (NetworkManager.isNetworkAvailable(PaymentActivity.this)) {

                    Map<String, Object> trip = new HashMap<>();
                    trip.put("current_lat", sessionManager.getMyLat());
                    trip.put("current_long", sessionManager.getMyLang());
                    trip.put("destination_lat", sessionManager.getDestinationLat());
                    trip.put("destination_long", sessionManager.getDestinationLang());
                    trip.put("username", sessionManager.getUserName());
                    trip.put("mobile", sessionManager.getMobile());
                    trip.put("Charge", sessionManager.getMobile());
                    trip.put("order_status", "waiting");
                    trip.put("price", Price);
                    trip.put("distance", Distence);

                    FirebaseFirestore fireStoreInstance = getFireStoreInstance();
                    fireStoreInstance.collection("Trip")
                            .add(trip)
                            .addOnSuccessListener(documentReference -> goToOrderSuccess()).addOnFailureListener(e -> {
                                hideLoading();
                                showToast(PaymentActivity.this, getString(R.string.error));
                            });
//                    goToOrderSuccess();
                } else {
                    binding.containerNoInternet.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void goToOrderSuccess() {
        sessionManager.setReqTrip(true);
        Intent intent = new Intent(this, TripActivity.class);
        intent.putExtra("checkIn", checkIn);
        intent.putExtra("checkOut", checkOut);
        intent.putExtra("adult", adult);
        intent.putExtra("children", children);
        intent.putExtra("rooms", room);
        intent.putExtra("total", total);
        intent.putExtra("type", type);
        intent.putExtra("name", name);
        intent.putExtra("location", location);
        intent.putExtra("image",image);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finishAffinity();
    }


}