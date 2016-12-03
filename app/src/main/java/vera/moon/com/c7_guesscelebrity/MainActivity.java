package vera.moon.com.c7_guesscelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public String names;
    public GetActors actors;

    public Button b0,b1,b2,b3;

    public int correctAnswer = 0;
    public String[] answers = new String[4];

    public String p = "http://www.posh24.com/celebrities";

    public ArrayList<String> listanames = new ArrayList<>();
    public ArrayList<String> listapictures = new ArrayList<>();
    private int elegido;
    private ImageView imagenfinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b0 = (Button)findViewById(R.id.button);
        b1 = (Button)findViewById(R.id.button1);
        b2 = (Button)findViewById(R.id.button2);
        b3 = (Button)findViewById(R.id.button3);

        imagenfinal = (ImageView)findViewById(R.id.imageView);

        actors = new GetActors();
        try {
            String res = actors.execute("http://www.posh24.com/celebrities").get();
            Log.w("res",res);

            //Split content;
            String data[] = res.split("<div class=\"sidebarContainer\">");
            //get the images
            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(data[0]);

            while(m.find()){
                System.out.println();
                listapictures.add(m.group(1));
            }
            //Now get the names
            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(data[0]);

            while(m.find()){
                System.out.println();
                listanames.add(m.group(1));
            }


            setButtons();

        }catch(Exception e){e.printStackTrace();}
    }

    private void setButtons() throws ExecutionException, InterruptedException {
        Random rand = new Random();
        elegido = rand.nextInt(listanames.size());

        //downimages
        GetPics images = new GetPics();
        Bitmap celebrity = images.execute(listapictures.get(elegido)).get();
        //set imagen
        imagenfinal.setImageBitmap(celebrity);

        int falseanswer = 0;

        correctAnswer = rand.nextInt(4);
        for(int i=0;i<4;i++){
            if(correctAnswer == i){
                answers[i] = listanames.get(elegido);
            } else{
                falseanswer = rand.nextInt(listanames.size());
                while(falseanswer == elegido)
                    falseanswer = rand.nextInt(listanames.size());

                answers[i] = listanames.get(falseanswer);
            }
        }

        b0.setText(answers[0]);
        b1.setText(answers[1]);
        b2.setText(answers[2]);
        b3.setText(answers[3]);
    }

    public void chooseActor(View v) throws ExecutionException, InterruptedException {

        String tag = v.getTag().toString();

        if(tag.equals(correctAnswer+"")){

            Toast.makeText(this,"Correct!",Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(this,"Incorrect, the answer was "+listanames.get(elegido),Toast.LENGTH_SHORT).show();
        }

        setButtons();

    }

    //task to get pictures
    public class GetPics extends AsyncTask<String,Void,Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.connect();

                InputStream in = con.getInputStream();
                Bitmap b = BitmapFactory.decodeStream(in);

                return b;
            }
            catch(Exception e){}

            return null;
        }
    }
    //Task to get data from celebrities
    public class GetActors extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {

            String res = "";
            URL url;
            HttpURLConnection connection = null;

            try{

                url = new URL(strings[0]);
                connection = (HttpURLConnection)url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader read = new InputStreamReader(in);

                int data = read.read();
                while(data != -1){
                    char car = (char)data;
                    res += car;

                    data = read.read();
                }

                return res;

            }catch(Exception e){}
            //Pattern p = Pattern.compile("src=\"(.*?)\"");
            //Matcher m = p.matcher(names);

            return null;
        }
    }

}
