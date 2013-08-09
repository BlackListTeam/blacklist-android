package cat.andreurm.blacklist.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by air on 18/07/13.
 */
public class Message implements Parcelable{
    public int m_id;
    public boolean answer;
    public String pay_link;
    public String text;
    public String date;

    public Message(){

    }

    public Message(Parcel source){

        m_id = source.readInt();
        answer = source.readByte()== 1;
        pay_link = source.readString();
        text= source.readString();
        date=source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(m_id);
        parcel.writeByte((byte) (answer ? 1 : 0));
        parcel.writeString(pay_link);
        parcel.writeString(text);
        parcel.writeString(date);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
