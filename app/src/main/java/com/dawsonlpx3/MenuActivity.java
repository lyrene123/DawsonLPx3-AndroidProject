package com.dawsonlpx3;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * The MenuActivity defines an options menu that will appear in other Activities.
 * MenuActivity provides several features that an application user can access conveniently through
 * the menu at the top of the screen.
 *
 * @author Peter Bellefleur
 * @author Lyrene Labor
 * @author Philippe Langlois
 * @author Pengkim Sy
 */

public class MenuActivity extends AppCompatActivity {

    private final String URL = "https://www.dawsoncollege.qc.ca/";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //run superclass onCreateOptionsMenu method
        super.onCreateOptionsMenu(menu);
        //menu is defined in res/menu/options.xml
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle item selection
        switch (item.getItemId()) {
            //about option: display AboutFragment
            case R.id.about:
                //create intent, start activity
                Intent aboutIntent = new Intent(this, AboutFragment.class);
                startActivity(aboutIntent);
                return true;
            //dawson option: launch a web intent for the Dawson College home page
            case R.id.dawson:
                Uri webpage = Uri.parse(URL);
                Intent dawsonIntent = new Intent(Intent.ACTION_VIEW, webpage);
                if (dawsonIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(dawsonIntent);
                    return true;
                } else {
                    return false;
                }
                //settings option: display SettingsActivity
            case R.id.settings:
                //create intent, start activity
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            //default: call superclass implementation
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
