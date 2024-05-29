package com.example.gaminglog;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.gaminglog.account.UserManager;
import com.example.gaminglog.data.UserDbHelper;

public class GameDetailActivity extends AppCompatActivity {

    private GameDetailViewModel viewModel;
    private boolean isGameInLibrary = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ImageView imageView = findViewById(R.id.image_large);
        TextView nameView = findViewById(R.id.text_game_name);
        TextView descriptionTextView = findViewById(R.id.text_description);
        TextView releasedView = findViewById(R.id.text_released);
        TextView ratingView = findViewById(R.id.text_rating);

        int gameId = getIntent().getIntExtra("game_id", -1);
        if (gameId != -1) {
            viewModel = new ViewModelProvider(this).get(GameDetailViewModel.class);
            viewModel.init(gameId);

            viewModel.getGameDetails().observe(this, gameDetail -> {
                if (gameDetail != null) {
                    Glide.with(this)
                            .load(gameDetail.getBackgroundImage())
                            .into(imageView);
                    nameView.setText(gameDetail.getName());

                    // Parse HTML since descriptions from API contain HTML
                    Spanned formattedDescription;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        formattedDescription = Html.fromHtml(gameDetail.getDescription(), Html.FROM_HTML_MODE_LEGACY);
                    } else {
                        formattedDescription = Html.fromHtml(gameDetail.getDescription());
                    }
                    descriptionTextView.setText(formattedDescription);

                    releasedView.setText(gameDetail.getReleased());
                    ratingView.setText(String.valueOf(gameDetail.getRating()));
                }
            });
        }

        checkLibraryStatus();
    }

    // check if game is in user's library
    private void checkLibraryStatus() {
        UserDbHelper db = new UserDbHelper(this);
        int userId = getCurrentUserId();
        int gameId = getIntent().getIntExtra("game_id", -1);

        isGameInLibrary = db.isGameInLibrary(userId, gameId);
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_detail_menu, menu);
        checkGameInLibrary(menu);
        return true;
    }

    private void checkGameInLibrary(Menu menu) {
        UserDbHelper dbHelper = new UserDbHelper(this);
        int userId = getCurrentUserId();
        int gameId = getIntent().getIntExtra("game_id", -1);
        boolean isGameInLibrary = dbHelper.isGameInLibrary(userId, gameId);

        MenuItem libraryItem = menu.findItem(R.id.add_to_library);
        // if the game is in library, the remove will appear, else the add will appear
        if (isGameInLibrary) {
            libraryItem.setIcon(R.drawable.ic_remove_black_24dp);
            libraryItem.setTitle(R.string.remove_game_from_library);
        } else {
            libraryItem.setIcon(R.drawable.ic_add_black_24dp);
            libraryItem.setTitle(R.string.add_game_to_library);
        }
    }

    // back button, add and remove interactions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.add_to_library) {
            if (isGameInLibrary) {
                removeFromLibrary();
            } else {
                addToLibrary();
            }
            invalidateOptionsMenu();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    // add game to database and give toast to user
    private void addToLibrary() {
        UserDbHelper dbHelper = new UserDbHelper(this);
        int userId = getCurrentUserId();
        int gameId = getIntent().getIntExtra("game_id", -1);

        if (!dbHelper.isGameInLibrary(userId, gameId)) {
            dbHelper.addGameToLibrary(userId, gameId);
            Toast.makeText(this, "Game added to library", Toast.LENGTH_SHORT).show();
            isGameInLibrary = true;
        }
    }

    // remove game to database and give toast to user
    private void removeFromLibrary() {
        UserDbHelper dbHelper = new UserDbHelper(this);
        int userId = getCurrentUserId();
        int gameId = getIntent().getIntExtra("game_id", -1);

        dbHelper.removeGameFromLibrary(userId, gameId);
        Toast.makeText(this, "Game removed from library", Toast.LENGTH_SHORT).show();
        isGameInLibrary = false;
    }


    public int getCurrentUserId() {
        return UserManager.getInstance().getUserId();
    }


}