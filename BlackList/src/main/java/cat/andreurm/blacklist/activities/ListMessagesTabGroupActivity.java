package cat.andreurm.blacklist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import cat.andreurm.blacklist.activities.TabGroupActivity;

public class ListMessagesTabGroupActivity extends TabGroupActivity {

    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startChildActivity("Principal", new Intent(this, ListMessagesActivity.class));
    }

    @Override
    public void  onBackPressed  () {

    }
    
}
