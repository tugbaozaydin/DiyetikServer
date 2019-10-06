package com.project.diyetikserver;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.diyetikserver.Common.Common;
import com.project.diyetikserver.Model.Shipper;
import com.project.diyetikserver.ViewHolder.ShipperViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

public class ShipperManagement extends AppCompatActivity {
    FloatingActionButton fabAdd;
    FirebaseDatabase database;
    DatabaseReference shippers;
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Shipper, ShipperViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_management);

        fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateShipperLayout();
            }
        });

        recyclerView = findViewById(R.id.recycler_shippers);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        database = FirebaseDatabase.getInstance();
        shippers = database.getReference(Common.SHIPPERS_TABLE);
        loadAllShippers();

    }

    private void loadAllShippers() {
        FirebaseRecyclerOptions<Shipper> allShipper = new FirebaseRecyclerOptions.Builder<Shipper>()
                .setQuery(shippers, Shipper.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Shipper, ShipperViewHolder>(allShipper) {
            @Override
            protected void onBindViewHolder(@NonNull ShipperViewHolder holder, final int position, @NonNull final Shipper model) {
                holder.shipper_name.setText(model.getName());
                holder.shipper_phone.setText(model.getPhone());
                holder.btn_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showEditDialog(adapter.getRef(position).getKey(),model);
                    }
                });
                holder.btn_remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeShipper(adapter.getRef(position).getKey());
                    }
                });
            }

            @NonNull
            @Override
            public ShipperViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.shipper_layout, viewGroup, false);
                return new ShipperViewHolder(itemView);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void removeShipper(String key) {
        shippers.child(key)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ShipperManagement.this,"Silindi",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShipperManagement.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        adapter.notifyDataSetChanged();
    }

    private void showEditDialog(String key,Shipper model) {
        AlertDialog.Builder create_shipper_dialog = new AlertDialog.Builder(ShipperManagement.this);
        create_shipper_dialog.setTitle("Update Shipper");
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.create_shipper_layout, null);
        final MaterialEditText edtName = view.findViewById(R.id.edtName);
        final MaterialEditText edtPhone = view.findViewById(R.id.edtPhone);
        final MaterialEditText edtPassword = view.findViewById(R.id.edtPassword);

edtName.setText(model.getName());
edtPhone.setText(model.getPhone());
edtPassword.setText(model.getPassword());

        create_shipper_dialog.setView(view);
        create_shipper_dialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        create_shipper_dialog.setPositiveButton(
                "UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Map<String,Object> update = new HashMap<>();
                        update.put("name",edtName.getText().toString());
                        update.put("phone",edtPhone.getText().toString());
                        update.put("password",edtPassword.getText().toString());

                        shippers.child(edtPhone.getText().toString())
                                .updateChildren(update)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ShipperManagement.this, "Taşıyıcı güncellendi", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ShipperManagement.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
        );
        create_shipper_dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        create_shipper_dialog.show();

    }

    private void showCreateShipperLayout() {
        AlertDialog.Builder create_shipper_dialog = new AlertDialog.Builder(ShipperManagement.this);
        create_shipper_dialog.setTitle("Create Shipper");
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.create_shipper_layout, null);
        final MaterialEditText edtName = view.findViewById(R.id.edtName);
        final MaterialEditText edtPhone = view.findViewById(R.id.edtPhone);
        final MaterialEditText edtPassword = view.findViewById(R.id.edtPassword);
        create_shipper_dialog.setView(view);
        create_shipper_dialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        create_shipper_dialog.setPositiveButton(
                "CREATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Shipper shipper = new Shipper();
                        shipper.setName(edtName.getText().toString());
                        shipper.setPhone(edtPhone.getText().toString());
                        shipper.setPassword(edtPassword.getText().toString());
                        shippers.child(edtPhone.getText().toString())
                                .setValue(shipper)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ShipperManagement.this, "Taşıyıcı oluşturuldu", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ShipperManagement.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
        );
        create_shipper_dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        create_shipper_dialog.show();

    }
}
