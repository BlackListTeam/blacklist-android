package cat.andreurm.blacklist.activities;

import java.util.Locale;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.readystatesoftware.viewbadger.BadgeView;

import org.apache.http.message.BasicRequestLine;

import cat.andreurm.blacklist.R;

public class TabHostActivity extends TabActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    BadgeView badge=null;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_intro);

        TabHost tabHost = getTabHost();

        TabHost.TabSpec events = tabHost.newTabSpec("Events");
        events.setIndicator("", getResources().getDrawable(R.drawable.icon_events_tab));
        Intent photosIntent = new Intent(this, EventsTabGroupActivity.class);
        events.setContent(photosIntent);

        TabHost.TabSpec messages = tabHost.newTabSpec("Messages");
        messages.setIndicator("", getResources().getDrawable(R.drawable.icon_messages_tab));
        Intent songsIntent = new Intent(this, ListMessagesTabGroupActivity.class);
        songsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        messages.setContent(songsIntent);

        TabHost.TabSpec code = tabHost.newTabSpec("Code");
        code.setIndicator("", getResources().getDrawable(R.drawable.icon_code_tab));
        Intent videosIntent = new Intent(this, CodeActivity.class);
        code.setContent(videosIntent);

        TabHost.TabSpec invite = tabHost.newTabSpec("Invite");

        invite.setIndicator("", getResources().getDrawable(R.drawable.icon_invite_tab));
        Intent codeIntent = new Intent(this, InviteActivity.class);
        invite.setContent(codeIntent);

        tabHost.addTab(events);
        tabHost.addTab(messages);
        tabHost.addTab(code);
        tabHost.addTab(invite);

        for(int i = 0; i<tabHost.getTabWidget().getChildCount();i++){
            tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.BLACK);
            tabHost.getTabWidget().getChildAt(i).getLayoutParams().height = 40;
        }

        LinearLayout.LayoutParams tab1=(LinearLayout.LayoutParams) getTabHost().getTabWidget().getChildAt(0).getLayoutParams();
        LinearLayout.LayoutParams tab2=(LinearLayout.LayoutParams) getTabHost().getTabWidget().getChildAt(1).getLayoutParams();
        LinearLayout.LayoutParams tab3=(LinearLayout.LayoutParams) getTabHost().getTabWidget().getChildAt(2).getLayoutParams();
        LinearLayout.LayoutParams tab4=(LinearLayout.LayoutParams) getTabHost().getTabWidget().getChildAt(3).getLayoutParams();

        tab1.topMargin=2;
        tab2.topMargin=4;

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {

               int i = getTabHost().getCurrentTab();
               if(i==0){
                   /*TabHost.TabSpec events = getTabHost().newTabSpec("Events");
                   events.setIndicator("", getResources().getDrawable(R.drawable.icon_events_tab));
                   Intent photosIntent = new Intent(getParent(), EventsTabGroupActivity.class);
                   events.setContent(photosIntent);*/
               }
               else if(i==1){
                   /*TabHost.TabSpec events = getTabHost().newTabSpec("Messages");
                   events.setIndicator("", getResources().getDrawable(R.drawable.icon_events_tab));
                   Intent photosIntent = new Intent(getParent(), ListMessagesTabGroupActivity.class);
                   events.setContent(photosIntent);*/
               }
               else if(i==2){
                   /*TabHost.TabSpec events = getTabHost().newTabSpec("Code");
                   events.setIndicator("", getResources().getDrawable(R.drawable.icon_events_tab));
                   Intent photosIntent = new Intent(getParent(), CodeActivity.class);
                   events.setContent(photosIntent);*/
               }
               else if(i==3){
                   /*TabHost.TabSpec events = getTabHost().newTabSpec("Invite");
                   events.setIndicator("", getResources().getDrawable(R.drawable.icon_events_tab));
                   Intent photosIntent = new Intent(getParent(), InviteActivity.class);
                   events.setContent(photosIntent);*/
               }
            }
        });

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            Fragment fragment = new DummySectionFragment();
            Bundle args = new Bundle();
            args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";

        public DummySectionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_events_intro_dummy, container, false);
            TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
            dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

}
