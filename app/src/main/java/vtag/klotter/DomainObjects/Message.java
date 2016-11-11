package vtag.klotter.DomainObjects;
import android.location.Location;


public class Message {

    int ownerID;
    String message;
    double longitude, latitude;

    String serializedMessage;

    public String toJson(){

        if(serializedMessage == null) {
            StringBuilder sb = new StringBuilder();

            sb.append("{ \"id\": ");
            sb.append(ownerID);
            sb.append(",\n\"message\": \"");
            sb.append(message);
            sb.append("\",\n\"x\": ");
            sb.append(longitude);
            sb.append(",\n\"y\": ");
            sb.append(latitude);
            sb.append("}");

            serializedMessage = sb.toString();
        }

        return serializedMessage;
    }

    public double distanceFrom(Location otherLocation)
    {
        double x = longitude - otherLocation.getLongitude();
        double y = latitude - otherLocation.getLatitude();

        return Math.sqrt(x*x + y*y);
    }

    /*************************************Getters******************************************/

    public double getLongitude(){
        return longitude;
    }

    public double getLatitude(){
        return latitude;
    }

    public String getMessage(){
        return message;
    }

    /**********************************Constructors****************************************/

    public Message(Location location, String message) {
        this.longitude = location.getLongitude();
        this.latitude = location.getLatitude();

        this.message = message;
    }

    public Message(double longitude, double latitude, String message) {
        this.longitude = longitude;
        this.latitude = latitude;

        this.message = message;
    }
}
