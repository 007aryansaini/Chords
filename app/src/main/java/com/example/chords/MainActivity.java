package com.example.chords;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.security.Permission;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
   ListView listview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         listview = findViewById(R.id.listView);

        Dexter.withContext(MainActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                      //  Toast.makeText(MainActivity.this , "Runtime Permission Given" , Toast.LENGTH_SHORT).show();
                        ArrayList<File> mySongs = fetchSongs(Environment.getExternalStorageDirectory());
                        String items[] = new String[mySongs.size()];
                        for(int i=0 ; i < mySongs.size() ; i++){
                                items[i] = mySongs.get(i).getName().replace(".mp3", " ");
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this , android.R.layout.simple_list_item_1 , items);
                            listview.setAdapter(adapter);
                            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(MainActivity.this , PlaySong.class);
                                    String currentSong = listview.getItemAtPosition(position).toString();
                                    intent.putExtra("songList" , mySongs);
                                    intent.putExtra("currentSong" , currentSong);
                                    intent.putExtra("position" , position);
                                    startActivity(intent);
                                }
                            });

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                         permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }

    public ArrayList<File> fetchSongs(File file){

        ArrayList arrayList = new ArrayList();

        File [] songs = file.listFiles();
        if(songs!=null){
            for(File myFiles : songs){
                if(!myFiles.isHidden() && myFiles.isDirectory()){
                    arrayList.addAll(fetchSongs(myFiles));
                }
                else {
                    if(myFiles.getName().endsWith(".mp3") && !myFiles.getName().startsWith(".")){
                        arrayList.add(myFiles);
                    }
                }
            }
        }
        return arrayList;
    }

}