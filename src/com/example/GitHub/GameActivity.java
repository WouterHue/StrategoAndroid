package com.example.GitHub;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.Android.R;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by wouter on 24/02/14.
 */
public class GameActivity extends Activity {
    private static final String URL_TILES= "http://10.0.2.2:8080/api/game/setStartPosition";
    private ImageView[][] tiles;
    private int[] piecesResources;
    private ImageView[] pieces;
    private GridLayout board;
    boolean gameStarted = false;
    private Integer tempImageResource;
    private int[] amountOfPieces;
    private boolean userIsBlue;
    private int marginBottomPiecesImages;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        initializeComponents();
        addListeners();
    }

    private void addListeners() {
        addBoardObserver();

        for (int i = 0; i < pieces.length; i++) {
            pieces[i].setOnClickListener(new piecesListener(i));

        }
    }

    private void addBoardObserver() {
        // We need this listener to get the width and height of the board and pass it into all the imageviews inside the board.
        ViewTreeObserver vto = board.getViewTreeObserver();
        if (vto != null) {
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    for(int i = 0 ;i< tiles.length;i++){
                        for (int j = 0 ;j< tiles[i].length;j++){
                            GridLayout.Spec row = GridLayout.spec(i);
                            GridLayout.Spec col = GridLayout.spec(j);
                            GridLayout.LayoutParams params = new GridLayout.LayoutParams(row,col);
                            params.width = board.getWidth()/board.getColumnCount();
                            params.height = board.getHeight()/board.getRowCount();
                            tiles[i][j].setLayoutParams(params);
                        }
                    }
                    for (int i = 0; i < piecesResources.length; i++) {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(board.getWidth()/board.getColumnCount(),board.getHeight()/board.getRowCount());
                        params.gravity = Gravity.CENTER;
                        params.setMargins(0,0,0,marginBottomPiecesImages);
                        pieces[i].setLayoutParams(params);
                    }
                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        board.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        board.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }

                }
            });
        }
    }

    private void initializeComponents() {
        if (userIsBlue) {
            piecesResources = new int[]{R.drawable.piece_b_bom, R.drawable.piece_b_maarschalk, R.drawable.piece_b_generaal, R.drawable.piece_b_kolonel, R.drawable.piece_b_majoor,
                    R.drawable.piece_b_kapitein, R.drawable.piece_b_luitenant, R.drawable.piece_b_sergeant, R.drawable.piece_b_mineur, R.drawable.piece_b_verkenner,
                    R.drawable.piece_b_spion, R.drawable.piece_b_vlag};
        } else {
            piecesResources = new int[]{R.drawable.piece_r_bom, R.drawable.piece_r_maarschalk, R.drawable.piece_r_generaal, R.drawable.piece_r_kolonel, R.drawable.piece_r_majoor,
                    R.drawable.piece_r_kapitein, R.drawable.piece_r_luitenant, R.drawable.piece_r_sergeant, R.drawable.piece_r_mineur, R.drawable.piece_r_verkenner,
                    R.drawable.piece_r_spion, R.drawable.piece_r_vlag};
        }

        marginBottomPiecesImages = (int)Utils.convertPixelsToDp(10,this);
        amountOfPieces = new int[]{6, 1, 1, 2, 3, 4, 4, 4, 5, 8, 1, 1};
        LinearLayout piecesLeft = (LinearLayout) findViewById(R.id.ll_pieces_left);
        LinearLayout piecesRight = (LinearLayout) findViewById(R.id.ll_pieces_right);
        pieces = new ImageView[piecesResources.length];

        for (int i = 0; i < piecesResources.length; i++) {
            pieces[i] = new ImageView(this);
            pieces[i].setImageResource(piecesResources[i]);
            pieces[i].setTag(piecesResources[i]);
            if (i < piecesResources.length / 2) {
                piecesLeft.addView(pieces[i]);
            } else {
                piecesRight.addView(pieces[i]);
            }

        }

        board = (GridLayout) findViewById(R.id.board);
        tiles = new ImageView[10][10];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                ImageView tile = new ImageView(this);
                tile.setImageResource(android.R.color.transparent);
                tile.setTag(R.string.tile_taken, false);
                tiles[i][j] = tile;
                board.addView(tiles[i][j]);
            }
        }
    }

    public void setReady(View view) {
        TextView commentary_text = (TextView)findViewById(R.id.commentary_board);
        if(allPiecesPlaced()){
            commentary_text.setText("");
            List<NameValuePair> urlparams = new ArrayList<NameValuePair>();
            urlparams.add(new BasicNameValuePair("username",((AppContext)getApplicationContext()).getUsername()));
            String pieces = "";
            for (int i = 6; i < tiles.length; i++) {
                for (int j = 0; j < tiles[i].length; j++) {
                    tiles[i][j].setBackgroundResource(android.R.color.transparent);
                    String pieceName = tiles[i][j].getResources().getResourceName((Integer) tiles[i][j].getTag(R.string.resource_name));
                    pieceName = pieceName.split("_")[2];
                    pieces += pieceName + ",";
                }
            }
            urlparams.add(new BasicNameValuePair("pieces",pieces));
            try {
                JSONObject data = new JsonController().excecuteRequest(urlparams,URL_TILES,"post");
                if (!data.getBoolean("verified")) {
                    commentary_text.setText("Unable to connect with the webserver.");
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else commentary_text.setText("Place all your pieces on the board first please");
    }

    private boolean allPiecesPlaced() {
        for (int i = 0; i < amountOfPieces.length; i++) {
            if (amountOfPieces[i] != 0) {
                return false;
            }
        }
        return true;
    }

    private class piecesListener implements View.OnClickListener{
        private int piecesCounter;


        private piecesListener(int piecesCounter) {
            this.piecesCounter = piecesCounter;
        }

        @Override
        public void onClick(View view) {
            tempImageResource = (Integer)view.getTag();
            for (int j = 6; j < tiles.length; j++) {
                for (int k = 0; k < tiles[j].length; k++) {
                    tiles[j][k].setBackgroundResource(R.drawable.board_imageviews_border_selectable);
                    final int finalK = k;
                    final int finalJ = j;
                    tiles[j][k].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (gameStarted == false) {
                                if (amountOfPieces[piecesCounter] > 0) {
                                    tiles[finalJ][finalK].setImageResource(tempImageResource);
                                    tiles[finalJ][finalK].setTag(R.string.resource_name,tempImageResource);
                                    amountOfPieces[piecesCounter]--;
                                    //in case tile already has a piece
                                    if ((Boolean) tiles[finalJ][finalK].getTag(R.string.tile_taken)) {
                                        int index = (Integer) tiles[finalJ][finalK].getTag(R.string.index_amount_of_pieces);
                                        amountOfPieces[index]++;
                                        pieces[index].setImageResource(piecesResources[index]);

                                    } else {

                                        tiles[finalJ][finalK].setTag(R.string.tile_taken, true);
                                    }
                                    tiles[finalJ][finalK].setTag(R.string.index_amount_of_pieces, piecesCounter);
                                    //when max amount of pieces you can put on board has been reached
                                    if (amountOfPieces[piecesCounter] == 0) {

                                        pieces[piecesCounter].setImageBitmap(Utils.grayScaleImage(BitmapFactory.decodeResource(getResources(), piecesResources[piecesCounter])));
                                    }
                                }
                            }
                        }
                    });
                }
            }
        }
    }
}
