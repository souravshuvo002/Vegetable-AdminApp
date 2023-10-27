package com.sourav.adminapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.content.CursorLoader;
import dmax.dialog.SpotsDialog;
import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.sourav.adminapp.Api.ApiClient;
import com.sourav.adminapp.Api.ApiService;
import com.sourav.adminapp.Api.ApiURL;
import com.sourav.adminapp.Common.Common;
import com.sourav.adminapp.Helper.CheckPermission;
import com.sourav.adminapp.Model.Result;
import com.sourav.adminapp.Model.Unit;
import com.sourav.adminapp.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UpdateFoodsActivity extends AppCompatActivity {

    private ImageView imageViewBanner, imageViewGallery, imageViewCam;
    private LinearLayout linearLayShowImage;
    private Button buttonUpload, buttonRemove;
    private TextInputEditText editTextName, editTextPrice, editTextDiscountPrice, editTextDesc, editTextMinUnitAmount;
    private Spinner spinnerUnitType, spinner_Status;
    private String ID_FOOD, FOOD_NAME, FOOD_PRICE, FOOD_DISCOUNT_PRICE, FOOD_DESC, FOOD_MIN_UNIT, FOOD_IMAGE_URL, FOOD_STATUS;

    public static final String DATE_FORMAT = "yyyyMMdd_HHmmss";
    public static final String IMAGE_DIRECTORY = "Vegetable";
    private static final int PICK_CAMERA_IMAGE = 2;
    private static final int PICK_GALLERY_IMAGE = 1;
    private File file;
    private File sourceFile;
    private File compressedImageFile;
    private SimpleDateFormat dateFormatter;
    private Uri imageCaptureUri;
    private Uri resultUri;
    private Uri imageUri;
    private Bitmap bp;
    private byte[] pic1;

    private List<Unit> unitList;
    private String unit_type, status;
    private boolean isSelected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_foods);

        // Change status bar color
        changeStatusBarColor("#00574B");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Update foods");

        ID_FOOD = getIntent().getStringExtra("ID");
        FOOD_NAME = getIntent().getStringExtra("FOOD_NAME");
        FOOD_PRICE = getIntent().getStringExtra("FOOD_PRICE");
        FOOD_DISCOUNT_PRICE = getIntent().getStringExtra("FOOD_DISCOUNT_PRICE");
        FOOD_DESC = getIntent().getStringExtra("FOOD_DESC");
        FOOD_MIN_UNIT = getIntent().getStringExtra("FOOD_MIN_UNIT");
        FOOD_IMAGE_URL = getIntent().getStringExtra("FOOD_IMAGE_URL");
        FOOD_STATUS = getIntent().getStringExtra("FOOD_STATUS");


        // getting views
        linearLayShowImage = (LinearLayout) findViewById(R.id.linearLayShowImage);
        editTextName = (TextInputEditText) findViewById(R.id.editTextName);
        editTextPrice = (TextInputEditText) findViewById(R.id.editTextPrice);
        editTextDiscountPrice = (TextInputEditText) findViewById(R.id.editTextDiscountPrice);
        editTextDesc = (TextInputEditText) findViewById(R.id.editTextDesc);
        editTextMinUnitAmount = (TextInputEditText) findViewById(R.id.editTextMinUnitAmount);
        spinnerUnitType = (Spinner) findViewById(R.id.spinner_unit);
        spinner_Status = (Spinner) findViewById(R.id.spinner_Status);

        imageViewBanner = (ImageView) findViewById(R.id.imageViewBanner);
        imageViewCam = (ImageView) findViewById(R.id.imageViewCam);
        imageViewGallery = (ImageView) findViewById(R.id.imageViewGallery);

        buttonUpload = (Button) findViewById(R.id.btnUpload);
        buttonRemove = (Button) findViewById(R.id.buttonRemove);


        editTextName.setText(FOOD_NAME);
        editTextPrice.setText(FOOD_PRICE);
        editTextDiscountPrice.setText(FOOD_DISCOUNT_PRICE);
        editTextDesc.setText(FOOD_DESC);
        editTextMinUnitAmount.setText(FOOD_MIN_UNIT);


        if (FOOD_STATUS.equalsIgnoreCase("0")) {
            spinner_Status.setSelection(1);
        } else {
            spinner_Status.setSelection(0);
        }

        Glide.with(UpdateFoodsActivity.this)
                .load(ApiURL.SERVER_URL + FOOD_IMAGE_URL)
                .into(imageViewBanner);

        // for Camera Error fix
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        // make directory for temp image
        file = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }

        dateFormatter = new SimpleDateFormat(
                DATE_FORMAT, Locale.US);


        loadUnitData();

        imageViewCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImageFromCamera();
            }
        });

        imageViewGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();
            }
        });

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelected = false;
                linearLayShowImage.setVisibility(View.GONE);
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editTextName.getText().toString()) ||
                        TextUtils.isEmpty(editTextPrice.getText().toString()) ||
                        TextUtils.isEmpty(editTextDiscountPrice.getText().toString()) ||
                        TextUtils.isEmpty(editTextDesc.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Some fields are empty", Toast.LENGTH_LONG).show();
                }
                if (!isSelected) {
                    uploadFoods();
                } else {
                    uploadFoodsImage();
                }
            }
        });

    }


    private void loadUnitData() {

        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> call = service.getUnit();

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                unitList = response.body().getUnitList();
                final List<String> NameID = new ArrayList<>();
                final Map<String, String> Map = new HashMap<String, String>();

                for (int i = 0; i < response.body().getUnitList().size(); i++) {
                    Map.put(response.body().getUnitList().get(i).getUnit_type(), response.body().getUnitList().get(i).getId());
                    NameID.add(response.body().getUnitList().get(i).getUnit_type());
                }

                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(UpdateFoodsActivity.this, android.R.layout.simple_spinner_item, NameID);
                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                spinnerUnitType.setAdapter(dataAdapter);

                spinnerUnitType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String string = spinnerUnitType.getSelectedItem().toString();
                        unit_type = string;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadFoodsImage() {

        String min_unit_amount = "";
        if (editTextMinUnitAmount.getText().toString().isEmpty()) {
            min_unit_amount = "null";
            unit_type = "null";
        } else {
            min_unit_amount = editTextMinUnitAmount.getText().toString();
        }

        if(spinner_Status.getSelectedItem().toString().equalsIgnoreCase("Active"))
        {
            status = "1";
        }
        else
        {
            status = "0";
        }

        RequestBody ID = RequestBody.create(MediaType.parse("text/plain"), ID_FOOD);
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), editTextName.getText().toString());
        RequestBody price = RequestBody.create(MediaType.parse("text/plain"), editTextPrice.getText().toString());
        RequestBody disPrice = RequestBody.create(MediaType.parse("text/plain"), editTextDiscountPrice.getText().toString());
        RequestBody desc = RequestBody.create(MediaType.parse("text/plain"), editTextDesc.getText().toString());
        RequestBody minUnitAmount = RequestBody.create(MediaType.parse("text/plain"), min_unit_amount);
        RequestBody unit = RequestBody.create(MediaType.parse("text/plain"), unit_type);
        RequestBody id_menu = RequestBody.create(MediaType.parse("text/plain"), Common.menu_id);
        RequestBody STATUS = RequestBody.create(MediaType.parse("text/plain"), status);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), compressedImageFile);


        MultipartBody.Part body = MultipartBody.Part.createFormData("image", compressedImageFile.getName(), requestFile);

        /**
         * Uploading Task to te perform here
         *
         * Database Table Field (name, link(image))
         *
         */
        final android.app.AlertDialog waitingDialog = new SpotsDialog(UpdateFoodsActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");
        //building retrofit object
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);

        //defining the call
        Call<Result> call = service.updateFoodItemImage(ID, name, price, disPrice, desc, minUnitAmount, unit, id_menu, STATUS, body);

        //calling the api
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                editTextName.setText(null);
                editTextPrice.setText(null);
                editTextDiscountPrice.setText(null);
                editTextDesc.setText(null);
                editTextMinUnitAmount.setText(null);
                linearLayShowImage.setVisibility(View.GONE);
                //imageViewPrescription.setImageResource(R.drawable.add_image_bg);
                waitingDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.e("Message: ", t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                waitingDialog.dismiss();
            }
        });
    }

    private void uploadFoods() {

        String min_unit_amount = "";
        if (editTextMinUnitAmount.getText().toString().isEmpty()) {
            min_unit_amount = "null";
            unit_type = "null";
        } else {
            min_unit_amount = editTextMinUnitAmount.getText().toString();
        }

        if(spinner_Status.getSelectedItem().toString().equalsIgnoreCase("Active"))
        {
            status = "1";
        }
        else
        {
            status = "0";
        }

        final android.app.AlertDialog waitingDialog = new SpotsDialog(UpdateFoodsActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");


        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> call = service.updateFoodItem(ID_FOOD,
                editTextName.getText().toString(),
                editTextPrice.getText().toString(),
                editTextDiscountPrice.getText().toString(),
                editTextDesc.getText().toString(),
                min_unit_amount,
                unit_type,
                Common.menu_id,
                status);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                editTextName.setText(null);
                editTextPrice.setText(null);
                editTextDiscountPrice.setText(null);
                editTextDesc.setText(null);
                editTextMinUnitAmount.setText(null);
                linearLayShowImage.setVisibility(View.GONE);
                //imageViewPrescription.setImageResource(R.drawable.add_image_bg);
                waitingDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.e("Message: ", t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                waitingDialog.dismiss();
            }
        });

    }


    // Image take from Gallery
    private void selectImageFromGallery() {
        CheckPermission checkPermission = new CheckPermission(UpdateFoodsActivity.this);
        if (checkPermission.checkPermission()) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_GALLERY_IMAGE);
        } else {
            checkPermission.requestPermission();
        }
    }

    // Image take from camera
    private void captureImageFromCamera() {
        CheckPermission checkPermission = new CheckPermission(UpdateFoodsActivity.this);
        if (checkPermission.checkPermission()) {
            sourceFile = new File(file, "img_"
                    + dateFormatter.format(new Date()).toString() + ".png");
            imageCaptureUri = Uri.fromFile(sourceFile);

            Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureUri);
            startActivityForResult(intentCamera, PICK_CAMERA_IMAGE);
        } else {
            checkPermission.requestPermission();
        }
    }

    // remove image
    private void removeImage() {
        imageViewBanner.setImageResource(R.drawable.add_image_bg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case PICK_GALLERY_IMAGE:
                    Uri imageUri = data.getData();
                    //imageView.setImageURI(imageUri);
                    linearLayShowImage.setVisibility(View.VISIBLE);
                    imageViewBanner.setVisibility(View.VISIBLE);
                    Glide.with(this).load(imageUri).into(imageViewBanner);
                    //filePath = getRealPathFromURI(imageUri);
                    sourceFile = new File(getRealPathFromURI(imageUri));
                    isSelected = true;
                    compressImageWithZetbaitsuLibrary(sourceFile);       // Here i'm compress the image

                    break;
                case PICK_CAMERA_IMAGE:
                    if (imageCaptureUri == null) {
                        Toast.makeText(getApplicationContext(), "Uri empty", Toast.LENGTH_LONG).show();
                    } else {
                        //imageView.setImageURI(imageCaptureUri);
                        linearLayShowImage.setVisibility(View.VISIBLE);
                        imageViewBanner.setVisibility(View.VISIBLE);
                        isSelected = true;
                        Glide.with(this).load(imageCaptureUri).into(imageViewBanner);
                        compressImageWithZetbaitsuLibrary(sourceFile);
                    }

                    break;
            }
        }
    }

    private void compressImageWithZetbaitsuLibrary(File sourceFile) {
        new Compressor(this)
                .setMaxWidth(1024)
                .setMaxHeight(1024)
                .setQuality(80)
                .setCompressFormat(Bitmap.CompressFormat.WEBP)
                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES + "/" + IMAGE_DIRECTORY).getAbsolutePath())
                .compressToFileAsFlowable(sourceFile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) {
                        compressedImageFile = file;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }


    private void changeStatusBarColor(String color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
