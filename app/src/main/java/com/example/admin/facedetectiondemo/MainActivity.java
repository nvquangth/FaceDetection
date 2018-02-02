package com.example.admin.facedetectiondemo;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.admin.adapter.DetectionAdapter;
import com.example.admin.model.Detection;
import com.example.admin.tmp.DatabaseHandler;
import com.example.admin.tmp.Help;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener {

    private CameraBridgeViewBase openCvCameraView;
    private CascadeClassifier faceDetect, humanDetect;
    private Mat grayscaleImage;
    private int absoluteFaceSize;

//    private ImageView imgCrop;

    private ListView lvDetection;
    private ArrayList<Detection> dsDetecion;
    private DetectionAdapter adapterDetection;
    private ArrayList<Rect> dsPhotoScanedRect;

    private Button btnScan;
    private boolean bClick = false;
    private int indexImg = 0;
    private Spinner spDetect, spColor;
    private ArrayList<String> dsDetect, dsColor;
    private ArrayAdapter<String> adapterDetect, adapterColor;
    private int indexDetect = 0, indexColor = 0;
    private Button btnNext;
//    private int indexTmp = 0;

//    private String DATABASE_NAME = "dbDetection.sqlite";
//    private String DATABASE_PATH = "/databases/";
//    private SQLiteDatabase database = null;

    private DatabaseHandler db = new DatabaseHandler(this);

    private BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case BaseLoaderCallback.SUCCESS:
                    initializeOpenCVDependencies();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }

        }
    };

    private void initializeOpenCVDependencies() {
        try{
            // Load file xml FaceDetect
            InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            FileOutputStream os  = new FileOutputStream(mCascadeFile);

            byte []buffer = new byte[4096];
            int bytesRead;
            while((bytesRead = is.read(buffer)) != -1){
                os.write(buffer, 0, bytesRead);
            }

            faceDetect = new CascadeClassifier(mCascadeFile.getAbsolutePath());


            // Load file xml HumanDetect
            is = getResources().openRawResource(R.raw.haarcascade_fullbody);
            cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            mCascadeFile = new File(cascadeDir, "haarcascade_fullbody.xml");
            os  = new FileOutputStream(mCascadeFile);

            while((bytesRead = is.read(buffer)) != -1){
                os.write(buffer, 0, bytesRead);
            }

            is.close();
            os.close();

            humanDetect = new CascadeClassifier(mCascadeFile.getAbsolutePath());

        }
        catch (Exception e){
            Log.e("OpenCvActivity", "Error loading cascade", e);
        }

        openCvCameraView.enableView();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        setContentView(openCvCameraView)

//        copyData();

        addControls();

        addEvents();

    }

//    private void copyData() {
//        // lay duong dan toi thu muc chua database
//        File dbFile = getDatabasePath(DATABASE_NAME);
//
//        // kiem tra file chua database da duoc copy chua, neu chua copy thi thuc hien copy
//        if(!dbFile.exists()){
//            try{
//                try{
//                    // luong du lieu dau vao tu file database trong assets
//                    InputStream myInput = getAssets().open(DATABASE_NAME);
//
//                    String outFileName = getApplicationInfo().dataDir + DATABASE_PATH + DATABASE_NAME;
//
//                    File f = new File(getApplicationInfo().dataDir + DATABASE_PATH);
//
//                    // kiem tra da co file chua, chua co thi tao (chi tao lan dau tien, cac lan sau chi thao tac tren file nay)
//                    if(!f.exists()){
//                        f.mkdir();
//                    }
//
//                    OutputStream myOutput = new FileOutputStream(outFileName);
//
//                    // moi lan doc 1024 byte
//                    byte [] buffer = new byte[1024];
//                    int length;
//
//                    // doc du lieu tu myInput vao buffer
//                    while((length = myInput.read(buffer)) > 0){
//                        // sau do ghi du lieu tu buffer vao muOutput
//                        myOutput.write(buffer, 0, length);
//                    }
//
//                    myOutput.flush();
//                    myOutput.close();
//                    myOutput.close();
//
//                }
//                catch (Exception ex){
//                    Log.e("Loi sao chep", ex.toString());
//                }
//            }
//            catch (Exception ex){
//                Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    private void addControls() {
        openCvCameraView = new JavaCameraView(this, -1);
        openCvCameraView = (CameraBridgeViewBase) findViewById(R.id.java_camera_view);

        lvDetection = (ListView) findViewById(R.id.lvPhotoScaned);
        dsDetecion = new ArrayList<>();
        adapterDetection = new DetectionAdapter(MainActivity.this, R.layout.custom_lv_photoscaned, dsDetecion);
        lvDetection.setAdapter(adapterDetection);

//        imgCrop = (ImageView) findViewById(R.id.imgCrop);
        btnScan = (Button) findViewById(R.id.btnCrop);
        btnNext = (Button) findViewById(R.id.btnNext);

        // spinner detect: cac che do detect
        spDetect = (Spinner) findViewById(R.id.spDetect);
        dsDetect = new ArrayList<>();
        dsDetect.add("Faces Detect");
        dsDetect.add("Humans Detect");
        adapterDetect = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, dsDetect);
        adapterDetect.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDetect.setAdapter(adapterDetect);

        // spinner color: cac che do mau
        spColor = (Spinner) findViewById(R.id.spColor);
        dsColor = new ArrayList<>();
        dsColor.add("RGB");
        dsColor.add("Gray");
        dsColor.add("Canny");
        adapterColor = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, dsColor);
        adapterColor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spColor.setAdapter(adapterColor);

    }

    private void addEvents(){

        openCvCameraView.setCvCameraViewListener(this);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bClick = true;
//                saveDetection(dsDetecion, indexTmp);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, DetailActivity.class);

                startActivity(intent);
            }
        });

        spDetect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                indexDetect = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                indexColor = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

//    private void saveDetection(Detection detection, int indexTmp) {
//
//        ContentValues row = new ContentValues();
//        row.put("Photoscaned", new Help().getBytes(detection.getPhotoScaned()));
//        row.put("DetectType", detection.getDetectType());
//        row.put("ColorType", detection.getColorType());
//
//        database.insert("Detection", null, row);
//
//    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        grayscaleImage = new Mat(height, width, CvType.CV_8UC4);
        absoluteFaceSize = (int) (height * 0.2);

    }

    @Override
    public void onCameraViewStopped() {
        grayscaleImage.release();
    }

    @Override
    public Mat onCameraFrame(Mat inputFrame) {

        Imgproc.cvtColor(inputFrame, grayscaleImage, Imgproc.CV_RGBA2mRGBA);

        switch (indexColor){
            case 0:{
                Imgproc.cvtColor(inputFrame, grayscaleImage, Imgproc.CV_RGBA2mRGBA);
                break;
            }
            case 1:{
                Imgproc.cvtColor(inputFrame, grayscaleImage, Imgproc.COLOR_RGB2GRAY);
                inputFrame = grayscaleImage;
                break;
            }
            case 2:{
                Imgproc.cvtColor(inputFrame, grayscaleImage, Imgproc.COLOR_RGB2GRAY);
//              // nguong
                Imgproc.Canny(grayscaleImage, inputFrame, 50, 150);
                break;
            }
            default: {
                Imgproc.cvtColor(inputFrame, grayscaleImage, Imgproc.CV_RGBA2mRGBA);
                break;
            }
        }

        final MatOfRect faceshumans = new MatOfRect();

        switch (indexDetect){
            case 0:{
                if(faceDetect != null){
                    faceDetect.detectMultiScale(grayscaleImage, faceshumans, 1.1, 2, 2, new Size(absoluteFaceSize, absoluteFaceSize), new Size());
                }
                break;
            }
            case 1:{
                if(faceDetect != null){
                    humanDetect.detectMultiScale(grayscaleImage, faceshumans, 1.1, 2, 2, new Size(absoluteFaceSize, absoluteFaceSize), new Size());
                }
                break;
            }
            default:{
                if(faceDetect != null){
                    faceDetect.detectMultiScale(grayscaleImage, faceshumans, 1.1, 2, 2, new Size(absoluteFaceSize, absoluteFaceSize), new Size());
                }
                break;
            }
        }


        // Lay duong dan thu muc de luu anh
        String filename = Environment.getExternalStorageDirectory().getPath();

        final Rect [] faceshumansArray = faceshumans.toArray();
        final int l = faceshumansArray.length;
        final Mat tmpInputFrame = inputFrame;

        if(l == 0)
            bClick = false;

        int tmp = dsDetecion.size();
        indexImg = db.getDetectionsCount() + 1;

        for(int i = 0; i < faceshumansArray.length; i++){

            switch (indexDetect){
                case 0:{
                    Core.rectangle(inputFrame, faceshumansArray[i].tl(), faceshumansArray[i].br(), new Scalar(0, 255, 0, 255),1);
                    break;
                }
                case 1:{
                    Core.rectangle(inputFrame, new  Point(faceshumansArray[i].x, faceshumansArray[i].y), new  Point(faceshumansArray[i].x + faceshumansArray[i].width, faceshumansArray[i].y + faceshumansArray[i].height), new Scalar(0, 255, 0));
                    break;
                }
                default:{
                    Core.rectangle(inputFrame, faceshumansArray[i].tl(), faceshumansArray[i].br(), new Scalar(0, 255, 0, 255),1);
                    break;
                }
            }

            if(bClick == true) {

                String detectType = "", colorType = "";
                if(indexDetect == 0)
                    detectType = "Faces";
                else
                    detectType = "Humans";

                if(indexColor == 0)
                    colorType = "RGB";
                else if(indexColor == 1)
                    colorType = "Gray";
                else
                    colorType = "Canny";

                final Rect tmpFacesArray = faceshumansArray[i];
                final String finalDetectType = detectType;
                final String finalColorType = colorType;

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Mat tmp = tmpInputFrame.submat(tmpFacesArray);

                        try {
                            Bitmap bmp = Bitmap.createBitmap(tmp.cols(), tmp.rows(), Bitmap.Config.ARGB_8888);
                            Utils.matToBitmap(tmp, bmp);

                            dsDetecion.add(new Detection(bmp, finalDetectType, finalColorType));

//                            db.addDetection(new Detection(bmp, finalDetectType, finalColorType));

//                            saveDetection(new Detection(bmp, finalDetectType, finalColorType), indexTmp);

                        } catch (Exception e) {
                            Log.d("MainError", "Loi");
                        }
                    }
                });

                // Luu anh vao bo nho dien thoai
                Highgui.imwrite(filename + "/Test/" + Integer.toString(indexImg) + ".jpg", inputFrame.submat(faceshumansArray[i]));
                indexImg++;

            }
        }

        if(bClick == true){
//            indexTmp = dsDetecion.size() - l;
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    adapterDetection.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Scan " + l + " ảnh", Toast.LENGTH_SHORT).show();
                }
            });

//            db.deleteAllDetection();

            for(int i = tmp; i < dsDetecion.size(); i++){
                db.addDetection(dsDetecion.get(i));
            }

//            for (Detection detection: dsDetecion){
//                db.addDetection(detection);
//            }

            Log.d("Read", "reading all contacts...");
            List<Detection> list = db.getAllDetections();

            for (Detection dt: list){
                String log = "ID = " + dt.getId() + "PHOT0 = " + (new Help().getBytes(dt.getPhotoScaned()).length) + ", DETECT_TYPE = " + dt.getDetectType() + ", COLOR_DETECT = " + dt.getColorType();
                Log.d("DetectsTest: ", log);
            }

        }

        bClick = false;

        return inputFrame;
    }

    @Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, baseLoaderCallback);
    }

}
