package com.project.diyetikserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.diyetikserver.Common.Common;
import com.project.diyetikserver.Interface.ItemClickListener;
import com.project.diyetikserver.Model.Category;
import com.project.diyetikserver.Model.Food;
import com.project.diyetikserver.ViewHolder.FoodViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import info.hoang8f.widget.FButton;

public class FoodList extends AppCompatActivity {

    //database
    FirebaseDatabase db;
    DatabaseReference foodList;
    FirebaseStorage storage;
    StorageReference storageReference;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RelativeLayout rootLayout;

    FloatingActionButton fab;

    String categoryId = "";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    MaterialEditText edtName,edtDescription, edtPrice, edtDiscount;
    FButton btnUpload, btnSelect;

    Food newFood;


    private  final int PICK_IMAGE_REQUEST = 71;
    Uri saveUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        // Firebase
        db = FirebaseDatabase.getInstance();
        foodList = db.getReference("Food");
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        //Load food list
        recyclerView = (RecyclerView) findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootLayout= (RelativeLayout)findViewById(R.id.rootLayout);

        fab= (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFoodDialog();
            }
        });

        if(getIntent()!= null)
            categoryId=getIntent().getStringExtra("CategoryId");
        if(!categoryId.isEmpty())
            loadListFood(categoryId);



    }

    private void showAddFoodDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Yeni yemek ekle!");
        alertDialog.setMessage("Lütfen tüm alanları doldurunuz.");


        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food_layout, null);
        edtName = add_menu_layout.findViewById(R.id.edtName);
        edtDescription = add_menu_layout.findViewById(R.id.edtDescription);
        edtPrice = add_menu_layout.findViewById(R.id.edtPrice);
        edtDiscount = add_menu_layout.findViewById(R.id.edtDiscount);

        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Bir Fotoğraf Seçin"), PICK_IMAGE_REQUEST);
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("Evet Ekle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (newFood != null) {
                    foodList.push().setValue(newFood);
                    Snackbar.make(rootLayout,"Yeni yemek "+newFood.getName()+"eklendi.",Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.setNegativeButton("Hayır Ekleme", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageFromGallery(resultCode, data);
        }
    }

    private void imageFromGallery(int resultCode, Intent data) {
        saveUri = data.getData();

        btnSelect.setText("Fotograf Seçildi");


    }
    private void chooseImage() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        i.setType("image/");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Fotoğraf seçiniz"), PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {

        if (saveUri != null) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Yükleniyor...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(FoodList.this, "Uploaded !", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            newFood = new Food();
                            newFood.setName(edtName.getText().toString());
                            newFood.setDescription(edtDescription.getText().toString());
                            newFood.setPrice(edtPrice.getText().toString());
                            newFood.setDiscount(edtDiscount.getText().toString());
                            newFood.setMenuId(categoryId);
                            newFood.setImage(uri.toString());


                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(FoodList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mDialog.setMessage("Yüklendi" + progress + "%");
                }
            });
        }
    }
    private void loadListFood(String categoryId) {
        Query listFoodByCategoryId=foodList.orderByChild("menuId").equalTo(categoryId);
        FirebaseRecyclerOptions<Food> options= new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(listFoodByCategoryId,Food.class)
                .build();

        adapter= new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder viewHolder, int position, @NonNull Food model) {
                viewHolder.foodName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).
                        into(viewHolder.foodImage);
                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                      // start new activity
                        Intent foodDetail = new Intent(FoodList.this,OrderDetail.class);
                        // Because category ıd is key ,so we just get key of this item
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());// send foodId to new Activity
                        startActivity(foodDetail);
                         }
                         });
            }


            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                View itemView= (View) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item,parent,false);
                return new FoodViewHolder(itemView);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        /*
        adapter= new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("menuId").equalTo(categoryId)
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {

                viewHolder.foodName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).
                        into(viewHolder.foodImage);
                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                      /*  // start new activity
                        Intent foodDetail = new Intent(FoodList.this,FoodDetail.class);
                        // Because category ıd is key ,so we just get key of this item
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());// send foodId to new Activity
                        startActivity(foodDetail);*/
                   // }
              //  });
            //}
        //};


    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            saveUri = data.getData();
            btnSelect.setText("Fotograf Seçildi");

        }
    }*/

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE)){
            showUpdateFoodDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(Common.DELETE)){
            deleteFood(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteFood(String key)
    {
        foodList.child(key).removeValue();
       // Toast.makeText(this,"Item deleted!!!",Toast.LENGTH_SHORT).show();
    }

    private void showUpdateFoodDialog(final String key, final Food ıtem) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Yemeği Düzenle");
        alertDialog.setMessage("Lütfen tüm alanları doldurunuz.");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_manu_layout = inflater.inflate(R.layout.add_new_food_layout, null);
        edtName = add_manu_layout.findViewById(R.id.edtName);
        edtDescription = add_manu_layout.findViewById(R.id.edtDescription);
        edtPrice = add_manu_layout.findViewById(R.id.edtPrice);
        edtDiscount = add_manu_layout.findViewById(R.id.edtDiscount);

        //set default value for view
        edtName.setText(ıtem.getName());
        edtDescription.setText(ıtem.getDescription());
        edtPrice.setText(ıtem.getPrice());
        edtDiscount.setText(ıtem.getDiscount());

        btnSelect = add_manu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_manu_layout.findViewById(R.id.btnUpload);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(ıtem);
            }
        });
        alertDialog.setView(add_manu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();


                    ıtem.setName(edtName.getText().toString());
                    ıtem.setDescription(edtDescription.getText().toString());
                    ıtem.setPrice(edtPrice.getText().toString());
                    ıtem.setDiscount(edtDiscount.getText().toString());

                    foodList.child(key).setValue(ıtem);
                    Snackbar.make(rootLayout," Yemek"+ıtem.getName()+"Düzenlendi.",Snackbar.LENGTH_SHORT).show();

            }
        });
        alertDialog.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

    }

    private void changeImage(final Food item) {

        if (saveUri != null) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Yükleniyor...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(FoodList.this, "Yüklendi !", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            item.setImage(uri.toString());

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(FoodList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mDialog.setMessage("Yüklendi" + progress + "%");
                }
            });
        }
    }
}
