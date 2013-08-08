package cat.andreurm.blacklist.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by air on 18/07/13.
 */
public class MessageThread implements Parcelable{
    public int mt_id;
    public String from;
    public String subject;
    public ArrayList<Message> messages;

    public MessageThread(){

    }

    public MessageThread(Parcel source){
        mt_id = source.readInt();
        from = source.readString();
        subject = source.readString();
        source.readTypedList(messages, Message.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mt_id);
        parcel.writeString(from);
        parcel.writeString(subject);
        parcel.writeTypedList(messages);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MessageThread createFromParcel(Parcel in) {
            return new MessageThread(in);
        }
        public MessageThread[] newArray(int size) {
            return new MessageThread[size];
        }
    };
}
