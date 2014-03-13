package com.example.GitHub;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.*;
import com.example.Android.R;
import com.example.GitHub.alarms.PlayerReadyAlarm;
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
    private static final String URL_START_POSITION= "http://10.0.2.2:8080/api/game/getStartPosition?";
    private static final String URL_PLAYER_STATUS= "http://10.0.2.2:8080/api/game/getReady?";
    private static final String URL_ENEMY_STATUS= "http://10.0.2.2:8080/api/game/getEnemyStatus?";
    private static final String URL_FIGHT = "http://10.0.2.2:8080/api/game/fight?";
    private static final String URL_MOVE_PIECE = "http://10.0.2.2:8080/api/game/movePiece";
    private ImageView[][] tiles;
    private int[] piecesResources;
    private ImageView[] pieces;
    private String color;
    private GridLayout board;
    private Integer tempImageResource;
    private int[] amountOfPieces;
    private boolean playerIsBlue;
    private boolean playerTurn;
    private boolean gameStarted;
    private int gameId;
    private int playerId;
    private int oldRow = 0;
    private int oldColumn = 0;
    private ImageView tempPiece;
    private int marginBottomPiecesImages;
    private IntentFilter playerStatusFilter;
    private PlayerReadyAlarm playerReadyAlarm;
    private TextView txt_commentary;
    private View viewTurnIndicator;
    private BroadcastReceiver playerStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!gameStarted) {
                polPLayerStatus();
            } else polEnemyStatusAndGetMoveIfPossible();

            /*
            Boolean ready = polPLayerStatus();
            if (ready) {
                playerReadyAlarm.CancelAlarm(context);
                if (!gameStarted) {
                    placeEnemyPlayerSetup(getEnemyPlayerSetup());
                } else {
                    playerTurn = true;
                    viewTurnIndicator.setBackgroundColor(getResources().getColor(R.color.green));
                }
            }
            */
        }
    };

    private void polEnemyStatusAndGetMoveIfPossible() {
        List<NameValuePair> urlparams = new ArrayList<NameValuePair>();
        urlparams.add(new BasicNameValuePair("playerId",playerId+""));
        try {
            JSONObject data = new JsonController().excecuteRequest(urlparams,URL_ENEMY_STATUS,"get");
            boolean isReady = data.getBoolean("isReady");
            if (!isReady) {
                playerReadyAlarm.CancelAlarm(getApplicationContext());
                setMoveEnemy(data.getInt("oldIndex"), data.getInt("newIndex"));
                playerTurn = true;
                viewTurnIndicator.setBackgroundColor(getResources().getColor(R.color.green));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setMoveEnemy(int oldIndex, int newIndex) {
        String oldIndexStr  = oldIndex+"";
        int oldRow = oldIndexStr.charAt(0);
        int oldColomn = oldIndexStr.charAt(1);

        String newIndexStr  = newIndex+"";
        int newRow = newIndexStr.charAt(0);
        int newColoumn = newIndexStr.charAt(1);

        tiles[newRow][newColoumn] = tiles[oldRow][oldColomn];
        tiles[oldRow][oldColumn].setImageResource(android.R.color.transparent);
        tiles[oldRow][oldColumn].setTag(R.string.is_piece, false);
        tiles[oldRow][oldColumn].setTag(R.string.resource_id, android.R.color.transparent);

    }

    private String getEnemyPlayerSetup() {
        List<NameValuePair> urlparams = new ArrayList<NameValuePair>();
        urlparams.add(new BasicNameValuePair("gameId",gameId+""));
        urlparams.add(new BasicNameValuePair("color",color+""));
        try {
            JSONObject data = new JsonController().excecuteRequest(urlparams,URL_START_POSITION,"get");
            return data.getString("pieces");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void polPLayerStatus() {
        List<NameValuePair> urlparams = new ArrayList<NameValuePair>();
        urlparams.add(new BasicNameValuePair("gameId",gameId+""));
        try {
            JSONObject data = new JsonController().excecuteRequest(urlparams,URL_PLAYER_STATUS,"get");
            Boolean isReady = data.getBoolean("isReady");
            if (data != null && isReady) {
                playerReadyAlarm.CancelAlarm(getApplicationContext());
                placeEnemyPlayerSetup(getEnemyPlayerSetup());
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Intent intent = getIntent();
        playerId = intent.getIntExtra("playerId",0);
        gameId = intent.getIntExtra("gameId",0);
        color = intent.getStringExtra("color");
        if (color.equalsIgnoreCase("blue")) {
            playerIsBlue = true;
            playerTurn = false;
        } else {
            playerIsBlue = false;
            playerTurn = true;
        }

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
        playerStatusFilter = new IntentFilter("getPlayerStatus");
        playerReadyAlarm = new PlayerReadyAlarm();
        viewTurnIndicator = findViewById(R.id.view_turn_indicator);
        gameStarted = false;
        txt_commentary = (TextView)findViewById(R.id.commentary_board);
        if (playerIsBlue) {
            piecesResources = new int[]{R.drawable.b11, R.drawable.b10, R.drawable.b9, R.drawable.b8, R.drawable.b7,
                    R.drawable.b6, R.drawable.b5, R.drawable.b4, R.drawable.b3, R.drawable.b2,
                    R.drawable.b1, R.drawable.b0};
        } else {
            piecesResources = new int[]{R.drawable.r11, R.drawable.r10, R.drawable.r9, R.drawable.r8, R.drawable.r7,
                    R.drawable.r6, R.drawable.r5, R.drawable.r4, R.drawable.r3, R.drawable.r2,
                    R.drawable.r1, R.drawable.r0};
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
        for (int i = 0; i < amountOfPieces.length; i++) {
            amountOfPieces[i] = 0;
        }
        for (int i = 6; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (playerIsBlue) {
                    tiles[i][j].setImageResource(R.drawable.b1);
                    tiles[i][j].setTag(R.string.resource_id,R.drawable.b1);
                }
                else {
                    tiles[i][j].setImageResource(R.drawable.r1);
                    tiles[i][j].setTag(R.string.resource_id,R.drawable.r1);
                }
            }
        }
    }

    public void setReady(View view) {
        if(allPiecesPlaced()){
            txt_commentary.setText("");
            for (ImageView piece : pieces) {
                piece.setOnClickListener(null);
            }
            addBoardListener();

            List<NameValuePair> urlparams = new ArrayList<NameValuePair>();
            urlparams.add(new BasicNameValuePair("gameId",gameId+""));
            urlparams.add(new BasicNameValuePair("playerId",playerId+""));
            String pieces = "";
            for (int i = 6; i < tiles.length; i++) {
                for (int j = 0; j < tiles[i].length; j++) {
                    tiles[i][j].setBackgroundResource(android.R.color.transparent);
                    String pieceName = tiles[i][j].getResources().getResourceName((Integer) tiles[i][j].getTag(R.string.resource_id));
                    pieceName = pieceName.split("/")[1];
                    pieces += pieceName + ",";
                }
            }
            urlparams.add(new BasicNameValuePair("pieces",pieces));
            try {
                JSONObject data = new JsonController().excecuteRequest(urlparams,URL_TILES,"post");
                if (data != null && !pieces.isEmpty() && pieces != null) {
                    String piecesEnemy = data.getString("pieces");
                    placeEnemyPlayerSetup(piecesEnemy);
                } else playerReadyAlarm.setAlarm(this);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else txt_commentary.setText("Place all your pieces on the board first please");


    }


    private void placeEnemyPlayerSetup(String piecesEnemy) {
        gameStarted = true;
        ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.switcher_ready_button);
        switcher.showNext();
        String[]piecesArray = piecesEnemy.split(",");
        int counter = piecesArray.length-1;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (playerIsBlue) {
                    tiles[i][j].setImageResource(R.drawable.redpiece);
                } else {
                    tiles[i][j].setImageResource(R.drawable.bluepiece);
                }
                tiles[i][j].setTag(R.string.resource_id, getImageResource(piecesArray[counter]));
                counter--;
            }
        }
        for (ImageView piece : pieces) {
            piece.setOnClickListener(null);
        }
        if (!playerTurn) {
            viewTurnIndicator.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            playerReadyAlarm.setAlarm(this);
        }
        else{
            viewTurnIndicator.setBackgroundColor(getResources().getColor(R.color.green));
            playerReadyAlarm.CancelAlarm(this);
        }
        addBoardListener();
    }

    private void addBoardListener() {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                final int finalI = i;
                final int finalJ = j;
                // remember which tile is a piece / enemy piece / obstacle
                tiles[i][j].setTag(R.string.is_obstacle,false);
                tiles[i][j].setTag(R.string.is_allowed_tile,false);
                if (i < 4) {
                    tiles[i][j].setTag(R.string.is_piece, false);
                    tiles[i][j].setTag(R.string.is_enemy_piece, true);
                } else if (i > 5) {
                    tiles[i][j].setTag(R.string.is_piece, true);
                    tiles[i][j].setTag(R.string.is_enemy_piece, false);

                } else {
                    tiles[i][j].setTag(R.string.is_piece, false);
                    tiles[i][j].setTag(R.string.is_enemy_piece, false);
                }
                //Dont put listeners on obstacles(maybe delete this later if we check this with isPiece)
                if (i > 5 || i < 4) {
                    final int finalI1 = i;
                    final int finalJ1 = j;
                    tiles[i][j].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (playerTurn) {
                                makeMove(finalI1, finalJ1);
                            }
                        }
                    });
                } else {
                    if (j != 2 && j != 3 && j != 6 && j != 7) {
                        tiles[i][j].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (playerTurn) {
                                    makeMove(finalI, finalJ);
                                }
                            }
                        });
                    } else {
                        tiles[i][j].setTag(R.string.is_obstacle,true);
                    }
                }
            }
        }
    }
    private void makeMove(int row, int column) {
        boolean isPiece = (Boolean)tiles[row][column].getTag(R.string.is_piece);
        //check if tile is a piece so you can select a new one
        if (isPiece) {
            resetAllowedTiles();
            txt_commentary.setText("Select a valid tile or select another piece to move");
            tiles[oldRow][oldColumn].setBackground(null);
            tiles[row][column].setBackgroundResource(R.drawable.board_imageviews_border_selectable);
            oldRow = row;
            oldColumn = column;
            tempPiece = tiles[row][column];
            showPossibleMoveTiles(row, column);
        } else {
            //to prevent an exception when our first click is not a piece
            if (tempPiece != null) {
                if ((Boolean) tiles[row][column].getTag(R.string.is_allowed_tile)) {
                    //RESET AFTER we check if tile is an allowed tile
                    resetAllowedTiles();
                    if (!(Boolean) tiles[row][column].getTag(R.string.is_enemy_piece)) {
                        tiles[row][column].setImageDrawable(tempPiece.getDrawable());
                        tiles[row][column].setTag(R.string.is_piece, true);
                        tiles[row][column].setTag(R.string.resource_id, tempPiece.getTag(R.string.resource_id));
                        tiles[oldRow][oldColumn].setImageResource(android.R.color.transparent);
                        tiles[oldRow][oldColumn].setTag(R.string.is_piece, false);
                        tiles[oldRow][oldColumn].setTag(R.string.resource_id, android.R.color.transparent);
                        txt_commentary.setText("");
                    } else {
                        fight(oldRow, oldColumn,row,column);
                    }
                    switchTurnAndMovePiece(oldRow,oldColumn,row,column);
                } else txt_commentary.setText("Select a valid tile please");
            } else txt_commentary.setText("Select a piece first please");

        }

    }

    private void switchTurnAndMovePiece(int oldRow, int oldColumn, int row, int column) {
        String oldIndex = oldRow + "" + oldColumn;
        String newIndex = row + "" + column;
        String index = oldIndex + "," + newIndex;
        viewTurnIndicator.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        playerTurn = false;
        List<NameValuePair> urlparams = new ArrayList<NameValuePair>();
        urlparams.add(new BasicNameValuePair("playerId", playerId + ""));
        urlparams.add(new BasicNameValuePair("index", index + ""));
        try {
            JSONObject data = new JsonController().excecuteRequest(urlparams, URL_MOVE_PIECE,"post");
            playerReadyAlarm.setAlarm(this);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void fight(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
        String piece = tiles[sourceRow][sourceColumn].getResources().getResourceName((Integer)tiles[sourceRow][sourceColumn].getTag(R.string.resource_id));
        piece = piece.split("/")[1];
        String pieceEnemy = tiles[targetRow][targetColumn].getResources().getResourceName((Integer)tiles[targetRow][targetColumn].getTag(R.string.resource_id));
        pieceEnemy = pieceEnemy.split("/")[1];

        List<NameValuePair> urlparams = new ArrayList<NameValuePair>();
        urlparams.add(new BasicNameValuePair("piecePlayer",piece));
        urlparams.add(new BasicNameValuePair("pieceEnemy",pieceEnemy));
        try {
            JSONObject data = new JsonController().excecuteRequest(urlparams,URL_FIGHT,"get");
            int result = data.getInt("result");
            //TIE
            if (result == 0) {
                tiles[targetRow][targetColumn].setImageResource(android.R.color.transparent);
                tiles[targetRow][targetColumn].setTag(R.string.is_enemy_piece,false);
            }
            // ENEMY FLAG DESTROYED
            else if (result == 2) {
                endGame();
            }
            //PLAYER WINS FIGHT
            else if (result == 1) {
                tiles[targetRow][targetColumn].setTag(R.string.is_enemy_piece, false);
                tiles[targetRow][targetColumn].setTag(R.string.is_piece, true);
                tiles[targetRow][targetColumn].setTag(R.string.is_allowed_tile, false);
                tiles[targetRow][targetColumn].setTag(R.string.resource_id, tiles[sourceRow][sourceColumn].getTag(R.string.resource_id));
                tiles[targetRow][targetColumn].setImageResource((Integer) tiles[sourceRow][sourceColumn].getTag(R.string.resource_id));
            }
            tiles[targetRow][targetColumn].setBackground(null);
            tiles[sourceRow][sourceColumn].setImageResource(android.R.color.transparent);
            tiles[sourceRow][sourceColumn].setTag(R.string.is_piece, false);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void endGame() {

    }

    private void resetAllowedTiles() {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if ((Boolean) tiles[i][j].getTag(R.string.is_allowed_tile) && !(Boolean) tiles[i][j].getTag(R.string.is_enemy_piece)) {
                    tiles[i][j].setImageResource(android.R.color.transparent);
                    tiles[i][j].setTag(R.string.is_allowed_tile, false);
                }
            }
        }
    }

    private void showPossibleMoveTiles(int row, int column) {
        String pieceName = tiles[row][column].getResources().getResourceName((Integer) tiles[row][column].getTag(R.string.resource_id));
        pieceName = pieceName.split("/")[1];
        if (pieceName.equalsIgnoreCase("b0") || pieceName.equalsIgnoreCase("b11") || pieceName.equalsIgnoreCase("r0") || pieceName.equalsIgnoreCase("r11")) {
            txt_commentary.setText("You cannot move a flag or bomb");
        } else if (pieceName.equalsIgnoreCase("b2") || pieceName.equalsIgnoreCase("r2")) {
            setResourceOfPossibleTilesScout(row,column);
        } else {
            boolean right = false;
            boolean left = false;
            boolean bottom = false;
            boolean top = false;
            if (row - 1 < 0) {
                top = true;
            } else if (row + 1 > 9) {
                bottom = true;
            }
            if (column - 1 < 0) {
                left = true;
            } else if (column + 1 > 9) {
                right = true;
            }
            if (top == false && bottom == false && left == false && right == false) {
                setResourceOfPossibleTiles(row, column, 1);
            } else if (left && top == false && bottom == false) {
                setResourceOfPossibleTiles(row, column, 2);
            } else if (top && right == false && left == false) {
                setResourceOfPossibleTiles(row, column, 3);
            } else if (right && top == false && bottom == false) {
                setResourceOfPossibleTiles(row, column, 4);
            } else if (bottom && left == false && right == false) {
                setResourceOfPossibleTiles(row, column, 5);
            } else if (top && left) {
                setResourceOfPossibleTiles(row, column, 6);
            } else if (top && right) {
                setResourceOfPossibleTiles(row, column, 7);
            } else if (bottom && right) {
                setResourceOfPossibleTiles(row, column, 8);
            } else if (bottom && left) {
                setResourceOfPossibleTiles(row, column, 9);
            }
        }

    }

    private void setResourceOfPossibleTiles(int row, int column,int position) {
    /*
    position "1" = not near any wall
    position "2" = near left wall
    position "3" = near top wall
    position "4" = near right wall
    position "5" = near bottom wall
    position "6" = top left corner
    position "7" = top right corner
    position "8" = bottom right corner
    position "9" = bottom left corner
    */
        if (position != 3 && position != 6 && position != 7) {
            if (!(Boolean) tiles[row - 1][column].getTag(R.string.is_piece) && !(Boolean) tiles[row - 1][column].getTag(R.string.is_obstacle)
                    && !(Boolean) tiles[row - 1][column].getTag(R.string.is_enemy_piece)) {
                tiles[row - 1][column].setImageResource(R.color.selectable_tile);
                tiles[row - 1][column].setTag(R.string.is_allowed_tile,true);
            } else if ((Boolean) tiles[row - 1][column].getTag(R.string.is_enemy_piece)) {
                tiles[row - 1][column].setBackgroundResource(R.drawable.board_imageviews_border_enemy);
                tiles[row - 1][column].setTag(R.string.is_allowed_tile,true);
            }
        }
        if (position != 5 && position != 8 && position != 9) {
            if (!(Boolean) tiles[row + 1][column].getTag(R.string.is_piece) && !(Boolean) tiles[row + 1][column].getTag(R.string.is_obstacle)
                    && !(Boolean) tiles[row + 1][column].getTag(R.string.is_enemy_piece)) {
                tiles[row + 1][column].setImageResource(R.color.selectable_tile);
                tiles[row + 1][column].setTag(R.string.is_allowed_tile, true);
            } else if ((Boolean) tiles[row + 1][column].getTag(R.string.is_enemy_piece)) {
                tiles[row + 1][column].setBackgroundResource(R.drawable.board_imageviews_border_enemy);
                tiles[row + 1][column].setTag(R.string.is_allowed_tile, true);
            }
        }
        if (position != 4 && position != 7 && position != 8) {
            if (!(Boolean) tiles[row][column + 1].getTag(R.string.is_piece) && !(Boolean) tiles[row][column + 1].getTag(R.string.is_obstacle)
                    && !(Boolean) tiles[row][column + 1].getTag(R.string.is_enemy_piece)) {
                tiles[row][column + 1].setImageResource(R.color.selectable_tile);
                tiles[row][column + 1].setTag(R.string.is_allowed_tile,true);
            } else if ((Boolean) tiles[row][column + 1].getTag(R.string.is_enemy_piece)) {
                tiles[row][column + 1].setBackgroundResource(R.drawable.board_imageviews_border_enemy);
                tiles[row][column + 1].setTag(R.string.is_allowed_tile,true);
            }
        }
        if (position != 2 && position != 6 && position != 9) {
            if (!(Boolean) tiles[row][column - 1].getTag(R.string.is_piece) && !(Boolean) tiles[row][column - 1].getTag(R.string.is_obstacle)
                    && !(Boolean) tiles[row][column - 1].getTag(R.string.is_enemy_piece)) {
                tiles[row][column - 1].setImageResource(R.color.selectable_tile);
                tiles[row][column - 1].setTag(R.string.is_allowed_tile,true);
            } else if ((Boolean) tiles[row][column - 1].getTag(R.string.is_enemy_piece)) {
                tiles[row][column - 1].setBackgroundResource(R.drawable.board_imageviews_border_enemy);
                tiles[row][column - 1].setTag(R.string.is_allowed_tile,true);
            }
        }
    }

    private void setResourceOfPossibleTilesScout(int row,int column) {
        int rowCounter = row;
        int columnCounter = column;
        //Check HORIZONTALY how far SCOUT can move
        while (rowCounter < 9) {
            rowCounter++;
            // prevent TILE from jumping over OBSTACLES OR PIECES
            if (!makeTileSelectableIfPossible(rowCounter, column)) {
                rowCounter = 9;
            }
        }
        rowCounter = row;
        while (rowCounter > 0) {
            rowCounter--;
            if (!makeTileSelectableIfPossible(rowCounter, column)) {
                rowCounter = 0;
            }
        }

        //check VERTICALLY how far SCOUT can go
        while (columnCounter < 9) {
            columnCounter++;
            if (!makeTileSelectableIfPossible(row, columnCounter)) {
                columnCounter = 9;
            }
        }
        columnCounter = column;
        while (columnCounter > 0) {
            columnCounter--;
            if (!makeTileSelectableIfPossible(row, columnCounter)) {
                columnCounter = 0;
            }
        }

    }

    private boolean makeTileSelectableIfPossible(int rowCounter, int columnCounter) {
        if (!(Boolean) tiles[rowCounter][columnCounter].getTag(R.string.is_piece) && !(Boolean) tiles[rowCounter][columnCounter].getTag(R.string.is_enemy_piece)
                && !(Boolean) tiles[rowCounter][columnCounter].getTag(R.string.is_obstacle)) {
            tiles[rowCounter][columnCounter].setImageResource(R.color.selectable_tile);
            tiles[rowCounter][columnCounter].setTag(R.string.is_allowed_tile,true);
            return true;
        }
        return false;
    }


    private int getImageResource(String uri) {
        int imageResource = getResources().getIdentifier(uri, "drawable", getPackageName());
        return imageResource;
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
                            if (amountOfPieces[piecesCounter] > 0) {
                                tiles[finalJ][finalK].setImageResource(tempImageResource);
                                tiles[finalJ][finalK].setTag(R.string.resource_id, tempImageResource);
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
                    });
                }
            }
        }
    }


    @Override
    protected void onResume() {
        registerReceiver(playerStatusReceiver, playerStatusFilter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(playerStatusReceiver);
        super.onPause();
    }

}
