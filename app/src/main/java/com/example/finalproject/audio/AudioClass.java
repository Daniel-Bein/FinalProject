package com.example.finalproject.audio;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.finalproject.R;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class AudioClass extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String ACTIVITY_NAME = "AUDIO_FINDER";
    ArrayList<AudioDetail> audiodeets = new ArrayList<>();

    EditText audioField;
    SharedPreferences prefs = null;
    ProgressBar progressBar;
    BaseAdapter adapter;
    ListView list;
    String audioURL = "https://www.theaudiodb.com/api/v1/json/1/search.php?s=";
    EditText keyword;

    /**
     * List adadpter for audio details
     */
    private class listAdapt extends BaseAdapter{

        @Override
        public int getCount() { return audiodeets.size(); }

        @Override
        public AudioDetail getItem(int position) {
            return audiodeets.get(position);
        }

    /**
     *
     * @param position of item
     * @return id of item postion
     */
    @Override
        public long getItemId(int position) {
            return getItem(position).getID();
        }

    /**
     *
     * @param position of audio album
     * @param convertView inflate layout of activity
     * @param parent view group parent
     * @return row
     */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;

            if(convertView == null){
                row = getLayoutInflater().inflate(R.layout.activity_audio_s, null);
            }

            TextView albumTitle = row.findViewById(R.id.numberTextViewIDAudio);
            TextView genraId = row.findViewById(R.id.titleTextViewAudio);
            TextView salesId = row.findViewById(R.id.urlTextViewAudio);

            AudioDetail audioD = getItem(position);
            albumTitle.setText(audioD.getAlbum());
            genraId.setText(audioD.getGenre());
            salesId.setText(audioD.getSales());

            return row;
        }
    }


    /**
     *
     * @param savedInstanceState and set contentview of audio acitivty
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        /**
         * shared prefs for audio search files and saves file
         */
        prefs = getSharedPreferences("AudioSearchFile", Context.MODE_PRIVATE);
        String savedString = prefs.getString("ReserveName", null);
        audioField = findViewById(R.id.searchInputTextIDAudio);
        audioField.setText(savedString);

        /**
         *  audio tool bar to switch between apps
         */
        Toolbar tbar = (Toolbar) findViewById(R.id.toolbarAudio);
        tbar.setTitle("Audio Search");
        setSupportActionBar(tbar);

        /**
         * drawer layout to switch between apps
         */
        DrawerLayout drawer = findViewById(R.id.audio_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /**
         * navigation view  for drawer
         */
        NavigationView navigationView = findViewById(R.id.audio_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /**
         * intitialize audio class list adapter
         */
        adapter = new AudioClass.listAdapt();


        /**
         * listview for audio
         */
        list = findViewById(R.id.listViewAudio);
        /**

        /**
         * click listener for toast message to display position id of albums
         */
        list.setOnItemClickListener((parent, view, position, id) -> {
            Toast.makeText(AudioClass.this, "This is album number " + (position +1) + ", of your searched artist", Toast.LENGTH_SHORT).show();
        });

        /**
         * button to send entered artist to retrieve albums data
         */
        Button sendButton = findViewById(R.id.audioButton);
        keyword = findViewById(R.id.searchInputTextIDAudio);
        /**
         * add progress bar under search for artist search button press
         */
        progressBar = findViewById(R.id.progressBarIDAudio);
        progressBar.setVisibility(View.GONE);
        progressBar.setMax(100);


        sendButton.setOnClickListener(v -> {
         progressBar.setVisibility(View.VISIBLE);

            try {
                retrieveData();
                saveSharedPrefs(audioField.getText().toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            /**
             * Snackbar pop up to show that it is retreving data from url
             */
            Snackbar.make(sendButton, R.string.searching, Snackbar.LENGTH_LONG).show();
        });

    }

    /**
     *
     * @param menu inflater for options menu aka audio toolbar
     * @return audio toolbar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_audio_toolbar, menu);

        MenuItem searchItem = menu.findItem(R.id.toolbarAudio);
        return true;
    }

    /**
     * on menu itemselected looks at toolbar and what to launch when each menu item is selected toolbar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId())
        {

            case R.id.audioItem1:
                Intent goToAudio = new Intent(AudioClass.this, AudioClass.class);
                startActivity(goToAudio);
                break;



            case R.id.audioHelpIcon:
                /**
                 * alert dialog for audio help info in toolbar
                 */
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(getResources().getString(R.string.help_title))

                        .setMessage(getResources().getString(R.string.alert_help_audio))

                        .setNeutralButton(getResources().getString((R.string.help_ok)),(click, arg) -> { } )

                        .create().show();

                break;
        }

        return true;
    }

    /**
     *
     * @param item - menuitems for different applications to execute -navigation menu
     * @return alert dialog for help item and retun activity based on menu item clicked
     */
    @Override
    public boolean onNavigationItemSelected( MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.audioItem1:
                Intent goToAudio = new Intent(AudioClass.this, AudioClass.class);
                startActivity(goToAudio);
                break;


            case R.id.audioHelpIcon:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(getResources().getString(R.string.help_title))

                        .setMessage(getResources().getString(R.string.alert_help_audio))

                        .setNeutralButton(getResources().getString((R.string.help_ok)),(click, arg) -> { } )

                        .create().show();
                break;
        }
        return false;
    }

    /**
     *
     * @param stringToSave - saves the sharedprefs of the audiosearch being retrevied and reserves in memory upon relaunch
     */
    private void saveSharedPrefs(String stringToSave) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ReserveName", stringToSave);
        editor.commit();
    }

    /**
     *  retreieve  album data from the URL based on artist searched for in edit text
     * @throws UnsupportedEncodingException
     */
    private void retrieveData() throws UnsupportedEncodingException
    {
        AudioQuery req = new AudioQuery();
        req.execute(audioURL + URLEncoder.encode(keyword.getText().toString(), "UTF-8"));
    }

    private class AudioQuery extends AsyncTask<String, Integer, String> {
        /**
         *
         * @param urls establish connection to url and retreive album data string associated with artist entered in edit text
         * @return add album information from url into listview on app based on artist searched for
         */
        @Override
        protected String doInBackground(String... urls) {
            String icon = "";
            String returnOnPostExecute = "";

            try {

                URL audioSearchUrl = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) audioSearchUrl.openConnection();
                InputStream response = urlConnection.getInputStream();


                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                String result = sb.toString();


                JSONObject audioResults = new JSONObject(result);
                JSONArray audioArray = audioResults.getJSONArray("album");

               audiodeets.clear();

                for(int i=0; i< audioArray.length(); i++) {
                    JSONObject audioJson = audioArray.getJSONObject(i);

                    String thumbNail = audioJson.getString("strAlbumThumb");
                    String strAlbum = audioJson.getString("strAlbum");
                    String strGenre = audioJson.getString("strGenre");
                    String intSales = Integer.toString(audioJson.getInt("intSales"));

                    strAlbum = "Album :" + strAlbum;
                    strGenre = "Genre :" + strGenre;
                    intSales = "Sales :" + intSales;


                    Bitmap image = null;


                    AudioDetail audioDetail = new AudioDetail(intSales, strAlbum, strGenre, thumbNail);
                   if(image != null) audioDetail.setImageData(image);

                    audiodeets.add(audioDetail);

                    publishProgress(i*100 / audioArray.length());
                }

                publishProgress(100);

            }  catch (Exception e) {
                e.printStackTrace();
            }

            return "Done";
        }

        @Override
        protected void onProgressUpdate(Integer... values){
            super.onProgressUpdate();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String fromDoIntBackground){
            super.onPostExecute(fromDoIntBackground);
            progressBar.setVisibility(View.INVISIBLE);
            if(!audiodeets.isEmpty()) list.setAdapter(adapter);
        }

        private boolean fileExists(String fileName){
            File file = getBaseContext().getFileStreamPath(fileName);
            return file.exists();
        }
    }


    /**
     * audio detail class storing the details of albums such as name of album, genre, sales of album and image of album
     */
    private class AudioDetail {

        private String album, genre, sales, image;
        private Bitmap imageData;

        private long ID;
        private boolean saved;

        /**
         *
         * @param album - name/s of albums of artist searched
         * @param url - genre of type of music for album
         * @param sales - number of albums sold
         * @param image - image of album cover art
         */
        public AudioDetail(String album, String url, String sales, String image) {
            this.album = album;
            this.genre = url;
            this.sales = sales;
            this.image = image;
        }

        public void setAlbum(String album) {
            this.album = album;
        }

        public void setGenre(String genre) {
            this.genre = genre;
        }

        public void setSales(String sales) {
            this.sales = sales;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public void setImageData(Bitmap imageData) {
            this.imageData = imageData;
        }

        public void setID(long ID) {
            this.ID = ID;
        }

        public void setSaved(boolean saved) {
            this.saved = saved;
        }

        public String getAlbum() {
            return album;
        }

        public String getGenre() {
            return genre;
        }

        public String getSales() {
            return sales;
        }

        public String getImage() {
            return image;
        }

        public Bitmap getImageData() {
            return imageData;
        }

        public long getID() {
            return ID;
        }

        public boolean isSaved() {
            return saved;
        }
    }
}
