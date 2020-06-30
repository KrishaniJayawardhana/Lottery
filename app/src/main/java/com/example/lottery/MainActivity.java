package com.example.lottery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lottery.ServiceManager.APIService;
import com.example.lottery.ServiceManager.APIUtils;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ImageView img;
    private Button btn,SpeakBtn,camera,gallary;
    private TextView lotNum;
    private TextToSpeech mtts,mtts2;
    String SpeakOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = findViewById(R.id.img);
        btn = findViewById(R.id.btn);
        lotNum = findViewById(R.id.lotNum);
        SpeakBtn = findViewById(R.id.speakBtn);
        gallary = findViewById(R.id.gallary);

 // Select the image from correct location and it will upload to the Android Applicatio
        // choose the image and send post request to flask testapi service

        gallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent2 = new Intent("android.media.action.READ_EXTERNAL_STORAGE");
                intent2.setType("image/*");
                intent2.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult((Intent.createChooser(intent2, "Select Picture")),1024);
                Log.d("Gallary","Gallary");

            }
        });

    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    // set the image into mobile application.
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap photo = null;
        if (resultCode == RESULT_OK ) {


            if (requestCode ==1005) {
                photo = (Bitmap) data.getExtras().get("data");
                img.setImageBitmap(photo);
                }

            else {
                Uri uri = data.getData();
                try {
                    photo = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    img.setImageBitmap(photo);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        mtts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if (status == TextToSpeech.SUCCESS) {
                    int result = mtts.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    }
                }

            }
        });

//Encording image
        final Bitmap finalPhoto1 = photo;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                finalPhoto1.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                encoded = encoded.replaceAll("\n","");
                ImageRequest imageRequest = new ImageRequest(encoded);
                APIService apiService = APIUtils.getApiService();
                imagePass(imageRequest,apiService);

            }

        });


        SpeakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });

    }


    private void imagePass (final ImageRequest imageRequest, APIService apiService) {

        final KProgressHUD kProgressHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDimAmount(0.5f)
                .show();

        apiService.imagePass(imageRequest).enqueue(new Callback<ImageResponse>() {


            /**
             * Invoked for a received HTTP response.
             * <p>
             * Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
             * Call {@link Response#isSuccessful()} to determine if the response indicates success.
             *
             * @param call
             * @param response
             */
            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {

                try {
                    kProgressHUD.dismiss();
                    Log.d("USER_REGISTRATION", response.raw().request().url().toString());
                    if (response.isSuccessful()) {
                        Log.d("USER_REGISTRATION", response.message());
                        ImageResponse imageResponse  = response.body();
                        if (response.body().getStatus().equals("200")){

                            SpeakOut = response.body().getresult();
//                            String[] separated = SpeakOut.split(" :");
//                            separated [0];
                            SpeakOut.replaceAll(" ",",");

                            lotNum.setText(SpeakOut);

//                            Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Request Fail", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Error"+e, Toast.LENGTH_SHORT).show();
                }

            }

            /**
             * Invoked when a network exception occurred talking to the server or when an unexpected
             * exception occurred creating the request or processing the response.
             *
             * @param call
             * @param t
             */
            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {

                    kProgressHUD.dismiss();

                    Toast.makeText(MainActivity.this, "Service Error", Toast.LENGTH_LONG).show();

            }
        });
}


    private void speak() {

        mtts.speak(SpeakOut, TextToSpeech.QUEUE_FLUSH, null);

    }

    private void speakTwo(String txt){

        mtts.speak(SpeakOut,TextToSpeech.QUEUE_ADD,null);

    }

}