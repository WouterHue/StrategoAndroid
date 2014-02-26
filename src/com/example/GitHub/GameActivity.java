package com.example.GitHub;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.example.Android.R;

/**
 * Created by wouter on 24/02/14.
 */
public class GameActivity extends Activity {
    private ImageView[][] tiles;
    private int[] bluePiecesResources;
    private int[] redPiecesResources;
    private ImageView[] bluePieces;
    private ImageView[] redPieces;
    private GridLayout board;
    boolean gameStarted = false;
    private Integer tempImageResource;
    private int[] amountOfPieces;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        initializeComponents();
        addListeners();
    }

    private void addListeners() {
        addBoardObserver();

        for (int i = 0; i < bluePieces.length; i++) {
            bluePieces[i].setOnClickListener(new piecesListener(i,true));
            redPieces[i].setOnClickListener(new piecesListener(i,false));
            /*
            final int finalI = i;
            bluePieces[i].setOnClickListener(new View.OnClickListener() {
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
                                        if (amountOfPieces[finalI] > 0) {
                                            tiles[finalJ][finalK].setImageResource(tempImageResource);
                                            amountOfPieces[finalI]--;
                                            //in case tile already has a piece
                                            if ((Boolean) tiles[finalJ][finalK].getTag(R.string.tile_taken)) {
                                                int index = (Integer) tiles[finalJ][finalK].getTag(R.string.index_amount_of_pieces);
                                                amountOfPieces[index]++;
                                                bluePieces[index].setImageResource(bluePiecesResources[index]);

                                            } else {

                                                tiles[finalJ][finalK].setTag(R.string.tile_taken, true);
                                            }
                                            tiles[finalJ][finalK].setTag(R.string.index_amount_of_pieces, finalI);
                                            //when max amount of pieces you can put on board has been reached
                                            if (amountOfPieces[finalI] == 0) {

                                                bluePieces[finalI].setImageResource(R.color.green);
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }

                }
            });*/
        }
    }

    private void addBoardObserver() {
        // We need this listener to get the width and height of the board and pass it into all the imageviews inside the board.
        ViewTreeObserver vto = board.getViewTreeObserver();
        if (vto != null) {
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
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
                    for (int i = 0; i < bluePiecesResources.length; i++) {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(board.getWidth()/board.getColumnCount(),board.getHeight()/board.getRowCount());
                        params.gravity = Gravity.CENTER;
                        bluePieces[i].setLayoutParams(params);
                        redPieces[i].setLayoutParams(params);
                    }
                    board.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                }
            });
        }
    }

    private void initializeComponents() {
        bluePiecesResources = new int[]{R.drawable.piece_b_bom,R.drawable.piece_b_maarschalk,R.drawable.piece_b_generaal,R.drawable.piece_b_kolonel,R.drawable.piece_b_majoor,
                R.drawable.piece_b_kapitein,R.drawable.piece_b_luitenant,R.drawable.piece_b_sergeant,R.drawable.piece_b_mineur,R.drawable.piece_b_verkenner,
                R.drawable.piece_b_spion,R.drawable.piece_b_vlag};
        redPiecesResources = new int[]{R.drawable.piece_r_bom,R.drawable.piece_r_maarschalk,R.drawable.piece_r_generaal,R.drawable.piece_r_kolonel,R.drawable.piece_r_majoor,
                R.drawable.piece_r_kapitein,R.drawable.piece_r_luitenant,R.drawable.piece_r_sergeant,R.drawable.piece_r_mineur,R.drawable.piece_r_verkenner,
                R.drawable.piece_r_spion,R.drawable.piece_r_vlag};

        amountOfPieces = new int[]{6,1,1,2,3,4,4,4,5,8,1,1};
        LinearLayout lLBluePiecesLeft  = (LinearLayout)findViewById(R.id.ll_blue_pieces_left);
        LinearLayout lLBluePiecesRight = (LinearLayout)findViewById(R.id.ll_blue_pieces_right);
        LinearLayout lLRedPiecesLeft  = (LinearLayout)findViewById(R.id.ll_red_pieces_left);
        LinearLayout lLRedPiecesRight = (LinearLayout)findViewById(R.id.ll_red_pieces_right);
        bluePieces = new ImageView[bluePiecesResources.length];
        redPieces = new ImageView[redPiecesResources.length];

        for (int i = 0; i < bluePiecesResources.length; i++) {
            bluePieces[i] = new ImageView(this);
            redPieces[i] = new ImageView(this);
            bluePieces[i].setImageResource(bluePiecesResources[i]);
            redPieces[i].setImageResource(redPiecesResources[i]);
            bluePieces[i].setTag(bluePiecesResources[i]);
            redPieces[i].setTag(redPiecesResources[i]);
            if(i<bluePiecesResources.length/2){
                lLBluePiecesLeft.addView(bluePieces[i]);
                lLRedPiecesLeft.addView(redPieces[i]);
            }
            else{
                lLBluePiecesRight.addView(bluePieces[i]);
                lLRedPiecesRight.addView(redPieces[i]);
            }

        }

        board = (GridLayout)findViewById(R.id.board);
        tiles = new ImageView[10][10];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                ImageView tile= new ImageView(this);
                tile.setImageResource(android.R.color.transparent);
                tile.setTag(R.string.tile_taken,false);
                // tile.setImageResource(R.color.green);
                tiles[i][j] = tile;
                board.addView(tiles[i][j]);
            }
        }



    }

    public void setBlueReady(View view) {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                tiles[i][j].setBackgroundResource(android.R.color.transparent);
            }
        }
    }

    public void setRedReady(View view) {
    }

    private class piecesListener implements View.OnClickListener{
        private int piecesCounter;


        private piecesListener(int piecesCounter,boolean isBluePiece) {
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
                                    amountOfPieces[piecesCounter]--;
                                    //in case tile already has a piece
                                    if ((Boolean) tiles[finalJ][finalK].getTag(R.string.tile_taken)) {
                                        int index = (Integer) tiles[finalJ][finalK].getTag(R.string.index_amount_of_pieces);
                                        amountOfPieces[index]++;
                                        bluePieces[index].setImageResource(bluePiecesResources[index]);

                                    } else {

                                        tiles[finalJ][finalK].setTag(R.string.tile_taken, true);
                                    }
                                    tiles[finalJ][finalK].setTag(R.string.index_amount_of_pieces, piecesCounter);
                                    //when max amount of pieces you can put on board has been reached
                                    if (amountOfPieces[piecesCounter] == 0) {

                                        bluePieces[piecesCounter].setImageResource(R.color.green);
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
