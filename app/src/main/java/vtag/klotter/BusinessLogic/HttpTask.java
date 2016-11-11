package vtag.klotter.BusinessLogic;
import vtag.klotter.DomainObjects.Message;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.os.AsyncTask;
import android.util.Log;
import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




public class HttpTask extends AsyncTask <Void, Void, String> {

    double longitude, latitude;

    public HttpTask(double longitude, double latitude)
    {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    protected String doInBackground(Void... locations) {
        Log.e("d", "Sending Get Request");

        try {
            URL url = new URL("http://private-anon-f48d6f4d5e-klotter.apiary-mock.com/get?x=" + longitude + "&y=" + latitude);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            }
            finally{
                urlConnection.disconnect();
            }
        }
        catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }

    protected void onPostExecute(String response) {
        if(response == null) {
            response = "THERE WAS AN ERROR";
        }
        Log.i("INFO", response);

        Message[] messageArray;

        try {
            Log.e("d", "did it make it here?");

            JSONArray jsonArray = new JSONArray(response);

            Log.e("d", "did it make it here?");
            messageArray = new Message[jsonArray.length()];

            Log.e("d", "messageArrayLength " + messageArray.length);

            JSONObject temp;

            for(int i = 0; i < jsonArray.length(); i++)
            {
                temp = jsonArray.getJSONObject(i);
                messageArray[i] = new Message(temp.getDouble("x"), temp.getDouble("y"), temp.getString("message"));

                Log.e("d", "message: " + temp.getString("message"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
