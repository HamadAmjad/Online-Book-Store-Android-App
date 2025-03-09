package com.fyp.bookworm.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.fyp.bookworm.R;
import com.fyp.bookworm.adapters.CartAdapter;
import com.fyp.bookworm.models.AddToCartModel;
import com.fyp.bookworm.models.OrderModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CartFragment extends Fragment implements PaymentResultListener {

    private RecyclerView recyclerView;
    private Button btnOrder;
    private ArrayList<AddToCartModel> addToCartModelArrayList = new ArrayList<>();
    OrderModel orderModel;
    private CartAdapter cartAdapter;
    AddToCartModel model;
    private DatabaseReference registerReference, cartReference, orderReference;
    private FirebaseAuth firebaseAuth;
    private String customerName,customerEmail,customerPhone, customerAddress, customerId;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;
    private String date;
    private static final String RAZORPAY_API_KEY = "rzp_test_KjqRfiA9X3EoOJ";

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        registerReference = FirebaseDatabase.getInstance().getReference("Register");
        cartReference = FirebaseDatabase.getInstance().getReference("Cart");
        orderReference = FirebaseDatabase.getInstance().getReference("Order");

        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("MMM-dd-yyyy");
        date = simpleDateFormat.format(calendar.getTime());

        Checkout.preload(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        recyclerView = view.findViewById(R.id._cartRecyclerView);
        btnOrder = view.findViewById(R.id._orderBtn);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(requireContext(), addToCartModelArrayList);
        recyclerView.setAdapter(cartAdapter);

        ReadCartItems();
        getUserDetails();

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPaymentMethodDialog();
            }
        });

        return view;
    }

    private void ReadCartItems() {
        cartReference.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;
                addToCartModelArrayList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        AddToCartModel addToCartModel = data.getValue(AddToCartModel.class);
                        addToCartModelArrayList.add(addToCartModel);
                    }
                    cartAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Failed to read cart items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserDetails() {
        registerReference.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;
                if (snapshot.exists()) {
                    customerName = snapshot.child("name").getValue(String.class);
                    customerEmail = snapshot.child("email").getValue(String.class);
                    customerPhone = snapshot.child("phone").getValue(String.class);
                    customerAddress = snapshot.child("address").getValue(String.class);
                    customerId = firebaseAuth.getCurrentUser().getUid();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Failed to fetch user details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPaymentMethodDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
        dialog.setTitle("Payment Method");
        dialog.setMessage("Please Select Any one Option");
        dialog.setPositiveButton("Online", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                processOrder("Online");
            }
        }).setNegativeButton("Cash on Delivery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                processOrder("Cash on Delivery");
            }
        });
        dialog.show();
    }

    private void processOrder(String paymentMethod) {
        cartReference.child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        model = dataSnapshot.getValue(AddToCartModel.class);
                        String orderId = orderReference.push().getKey();
                        orderModel = new OrderModel(customerName, model.getName(), customerId, model.getSellerId(),
                                model.getImage(), orderId, paymentMethod, "In Process", date, model.getMaterialId(), model.getPrice(), model.getQuantity());
                    }
                    if (paymentMethod.equals("Online")) {
                        startPaymentProcess();
                    } else {
                        placeOrder(orderModel, model.getId());
                        addToCartModelArrayList.clear();
                        cartAdapter.notifyDataSetChanged();
                        Toast.makeText(requireContext(), "Order placed successfully", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(requireContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Failed to process order", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void placeOrder(OrderModel orderModel, String cartItemId) {
        orderReference.child(orderModel.getOrderId()).setValue(orderModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                removeCartItem(cartItemId);
            }
        });
    }

    private void removeCartItem(String cartItemId) {
        cartReference.child(firebaseAuth.getCurrentUser().getUid()).child(cartItemId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                ReadCartItems();
            }
        });
    }

    private void startPaymentProcess() {
        Checkout checkout = new Checkout();
        checkout.setKeyID(RAZORPAY_API_KEY);
        checkout.setImage(R.drawable.book_worm_logo);

        JSONObject object = new JSONObject();
        try {
            object.put("name", customerName);
            object.put("description", "Pay online");

            int totalAmount = calculateTotalAmount() * 100;
            object.put("amount", totalAmount);
            object.put("currency", "PKR");

            JSONObject preFill = new JSONObject();
            preFill.put("email", customerEmail);
            preFill.put("contact", customerPhone.replaceFirst("0","92"));

            object.put("prefill", preFill);

            checkout.open(requireActivity(), object);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int calculateTotalAmount() {
        int totalAmount = 0;
        for (AddToCartModel model : addToCartModelArrayList) {
            totalAmount += model.getPrice();
        }
        return totalAmount;
    }

    @Override
    public void onPaymentSuccess(String s) {
        if (s != null) {
            orderReference.orderByChild("uid").equalTo(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    placeOrder(orderModel, model.getId());
                    addToCartModelArrayList.clear();
                    cartAdapter.notifyDataSetChanged();
                    Toast.makeText(requireContext(), "Order placed successfully", Toast.LENGTH_SHORT).show();
                    Toast.makeText(requireContext(), "Payment Successful", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(requireContext(), "Failed to update payment status", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(requireContext(), "Payment Failed", Toast.LENGTH_SHORT).show();
    }
}