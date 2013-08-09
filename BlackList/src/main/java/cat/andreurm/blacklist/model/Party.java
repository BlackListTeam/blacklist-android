package cat.andreurm.blacklist.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by air on 18/07/13.
 */
public class Party implements Parcelable {
    public int party_id;
    public String name;
    public String date;
    public String cover;
    public String image;
    public String[] gallery;
    public String info;
    public String price_info;
    public String place_text;
    public String location_date;
    public String map;
    public String address;

    public int max_escorts;
    public float latitude;
    public float longitude;
    public int max_rooms;
    public Boolean vip_allowed;
    public Boolean es_actual;

    public int id_view;

    public Party() {}

    private Party(Parcel parcel) {
        party_id = parcel.readInt();
        max_escorts = parcel.readInt();
        max_rooms = parcel.readInt();
        latitude = parcel.readFloat();
        longitude = parcel.readFloat();

        boolean[] temp = new boolean[1];
        parcel.readBooleanArray(temp);
        vip_allowed = temp[0];

        temp = new boolean[1];
        parcel.readBooleanArray(temp);
        es_actual = temp[0];

        name = parcel.readString();
        address = parcel.readString();
        date = parcel.readString();
        cover = parcel.readString();
        image = parcel.readString();
        info = parcel.readString();
        price_info = parcel.readString();
        place_text = parcel.readString();
        location_date = parcel.readString();
        map = parcel.readString();
        parcel.readStringArray(gallery);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(party_id);
        parcel.writeInt(max_escorts);
        parcel.writeInt(max_rooms);
        parcel.writeFloat(latitude);
        parcel.writeFloat(longitude);
        parcel.writeString(name);
        parcel.writeString(cover);
        parcel.writeString(image);
        parcel.writeStringArray(gallery);
        parcel.writeString(date);
        parcel.writeString(address);
        parcel.writeString(info);
        parcel.writeString(price_info);
        parcel.writeString(place_text);
        parcel.writeString(location_date);
        parcel.writeString(map);
        parcel.writeBooleanArray(new boolean[]{vip_allowed});
        parcel.writeBooleanArray(new boolean[]{es_actual});
    }

    public static final Parcelable.Creator<Party> CREATOR = new Parcelable.Creator<Party>(){


        @Override
        public Party createFromParcel(Parcel parcel) {
            return new Party(parcel);
        }

        @Override
        public Party[] newArray(int i) {
            return new Party[i];
        }
    };

}
