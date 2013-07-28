package cat.andreurm.blacklist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import cat.andreurm.blacklist.R;

public class EventsTabGroupActivity extends TabGroupActivity {

    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startChildActivity("Principal", new Intent(this, EventsActivity.class));
    }
    
}
