package com.example.admin.facedetectiondemo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.admin.adapter.DetectionAdapter;
import com.example.admin.model.Detection;
import com.example.admin.tmp.DatabaseHandler;
import com.example.admin.tmp.Help;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private ArrayList<Detection> dsDetections;
    private ListView lvDetection;
    private DetectionAdapter detectionAdapter;

    private Button btnSoSanh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        DatabaseHandler db = new DatabaseHandler(this);

        dsDetections = (ArrayList<Detection>) db.getAllDetections();
        lvDetection = (ListView) findViewById(R.id.lvDetections);
        detectionAdapter = new DetectionAdapter(this, R.layout.custom_lv_photoscaned, dsDetections);
        lvDetection.setAdapter(detectionAdapter);

        Toast.makeText(DetailActivity.this, "nDetections = " + dsDetections.size(), Toast.LENGTH_SHORT).show();

        btnSoSanh = (Button) findViewById(R.id.btnSoSanh);

        btnSoSanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dsDetections.size() > 1) {
                    xuLySoSanh();
                }
            }
        });

    }

    public int compareFeature(){
        Bitmap bmp1 = dsDetections.get(0).getPhotoScaned();
        Bitmap bmp2 = dsDetections.get(1).getPhotoScaned();
        Mat mat1 = new Mat();
        Mat mat2 = new Mat();

        Utils.bitmapToMat(bmp1, mat1);
        Utils.bitmapToMat(bmp2, mat2);

        int retVal = 0;

        MatOfKeyPoint keyPoint1 = new MatOfKeyPoint();
        MatOfKeyPoint keyPoint2 = new MatOfKeyPoint();

        Mat descriptors1 = new Mat();
        Mat descriptors2 = new Mat();

        FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
        DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);

        detector.detect(mat1, keyPoint1);
        detector.detect(mat2, keyPoint2);

        extractor.compute(mat1, keyPoint1, descriptors1);
        extractor.compute(mat2, keyPoint2, descriptors2);

        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

        MatOfDMatch matches = new MatOfDMatch();

        if (descriptors1.cols() == descriptors2.cols()) {
            matcher.match(descriptors1, descriptors2, matches);

            // Check matches of key points
            DMatch [] match = matches.toArray();
            double max_dist = 0;
            double min_dist = 100;

            for (int i = 0; i < descriptors1.rows(); i++) {
                double dist = match[i].distance;
                if (dist < min_dist)
                    min_dist = dist;
                if (dist > max_dist)
                    max_dist = dist;
            }

            for (int i = 0; i < descriptors1.rows(); i++) {
                if (match[i].distance <= 10) {
                    retVal++;
                }
            }

        }

        return retVal;

    }

    private void xuLySoSanh() {
        int ret = compareFeature();
        if (ret > 0)
            Toast.makeText(this, "Giong nhau", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Khac nhau", Toast.LENGTH_SHORT).show();
    }

}
