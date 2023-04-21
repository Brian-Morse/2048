import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class My2048 {
    
    //Class variables
    private static int sizeRow;
    private static int sizeCol;
    private static JLabel[][] board;
    private static JFrame frame;
    private static JPanel totPanel;
    private static JPanel mainPanel;
    private static JPanel scorePanel;
    private static int score;
    private static int highScore;
    private static JLabel curScore;
    private static JLabel highScoreLabel;
    private static File fileHS;

    
    //Number info
    private static final int MAIN_BLOCK = 2;
    private static final int SECOND_BLOCK = 4;
    private static final double MAIN_PROB = 0.9;
    private static final int SIZE_SMALL = 10;
    private static final int SMALL_START = 1;
    private static final int SIZE_MEDIUM = 36;
    private static final int MEDIUM_START = 2;
    private static final int LARGE_START = 3;
    private static final int OFFSET = 1;

    //GUI Info
    private static final int FONT_SIZE = 24;
    private static final int SCORE_FONT_SIZE = 16;
    private static final int SQUARE_SIZE = 100;
    private static final int PIX_GAP = 3;
    private static final String CUR_SCORE = "Score: ";
    private static final String HIGH_SCORE = "High Score: ";
    private static final String FILE_FORMAT = "highscore%dx%d.txt";
    private static final String HIGH_SCORE_FILE_FORMAT = 
            "%d is the high score for a %dx%d 2048 board, achieved on %s";
    private static final Color DEFAULT_COLOR = new Color(205,193,180);
    private static final Color SMALL_TEXT_COLOR = 
            new Color(120,111,102);
    private static final Color BIG_TEXT_COLOR = new Color(255,255,255);
    private static final Color TWO_COLOR = new Color(238,230,219);
    private static final Color FOUR_COLOR = new Color(236,224,200);
    private static final Color EIGHT_COLOR = new Color(239,178,124);
    private static final Color SIXTEEN_COLOR = new Color(243,151,104);
    private static final Color COLOR_32 = new Color(243,125,99);
    private static final Color COLOR_64 = new Color(244,96,66);
    private static final Color COLOR_128 = new Color(234,207,118);
    private static final Color COLOR_256 = new Color(237,203,103);
    private static final Color COLOR_512 = new Color(236,200,90);
    private static final Color COLOR_1024 = new Color(231,194,88);
    private static final Color COLOR_2048 = new Color(232,190,78);
    private static final Color COLOR_OTHER = new Color(61,58,51);

    public static void main(String[] args) {
        //JFrame intro info
        JFrame intro = new JFrame("2048");
        intro.setSize(450,200);
        intro.setLayout(null);
        intro.setLocationRelativeTo(null);

        //Default Square Info
        JLabel squareMessage = new JLabel("Default 4x4 Square");
        JButton squareButton = new JButton("SELECT");
        squareMessage.setBounds(30,30,200,50);
        squareButton.setBounds(30,100,100,25);
        squareButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    initBoard("4","4");
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                intro.dispose();
            }
        });
        intro.add(squareMessage);
        intro.add(squareButton);

        //Custom Info
        JLabel squareCustomMessage = new JLabel("Custom Size");
        JTextField squareField1 = new JTextField();
        JLabel x = new JLabel("x");
        JTextField squareField2 = new JTextField();
        JButton squareCustomButton = new JButton("SELECT");
        squareCustomMessage.setBounds(300,30,200,50);
        squareField1.setBounds(300,70,20,20);
        x.setBounds(340,70,20,20);
        squareField2.setBounds(370,70,20,20);
        squareCustomButton.setBounds(300,100,100,25);
        squareCustomButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               if (!squareField1.getText().equals("") && 
                        !squareField2.getText().equals("")) {
                    try {
                        initBoard(squareField1.getText(),squareField2.getText());
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                    intro.dispose();
                }
            }
        });
        intro.add(squareCustomMessage);
        intro.add(squareField1);
        intro.add(x);
        intro.add(squareField2);
        intro.add(squareCustomButton);
        
        intro.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        intro.setVisible(true);  


        
    }

    /**
     * Initiates the 2048 board and score, also places starting values
     * @param r The number of rows desired
     * @param c The number of columns desired
     * @throws FileNotFoundException
     */
    private static void initBoard(String r, String c) throws FileNotFoundException {
        //Initializing the board and frames
        sizeRow = Integer.parseInt(r);
        sizeCol = Integer.parseInt(c);
        board = new JLabel[sizeRow][sizeCol];
        score = 0;

        //Initializing high score info
        fileHS = new File(String.format(FILE_FORMAT,sizeRow,sizeCol));
        if (!fileHS.exists()) {
            PrintWriter creator = new PrintWriter(fileHS);
            creator.print("0");
            creator.close();
        }
        Scanner read = new Scanner(fileHS);
        highScore = Integer.parseInt(read.next());
        read.close();
        highScoreLabel = new JLabel(HIGH_SCORE + highScore);
        highScoreLabel.setPreferredSize(new Dimension(200,20));
        highScoreLabel.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,
                SCORE_FONT_SIZE));


        //Initializing the view of everything
        frame = new JFrame("2048");
        totPanel = new JPanel(new BorderLayout(PIX_GAP,PIX_GAP));
        mainPanel = new JPanel(new GridLayout(sizeRow,sizeCol,PIX_GAP,PIX_GAP));
        for (int i = 0; i < sizeRow; i++) {
            for (int j = 0; j < sizeCol; j++) {
                initLabel(i,j);
            }
        }
        scorePanel = new JPanel(new BorderLayout());
        curScore = new JLabel(CUR_SCORE + score);
        curScore.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,SCORE_FONT_SIZE));
        curScore.setPreferredSize(new Dimension(200,20));
        scorePanel.add(curScore,BorderLayout.WEST);
        scorePanel.add(highScoreLabel,BorderLayout.EAST);
        totPanel.add(mainPanel,BorderLayout.NORTH);
        totPanel.add(scorePanel,BorderLayout.SOUTH);
        frame.add(totPanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Adding key press functionality
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) {
                    if (moveDown()) {
                        placeNewBlock();
                    }
                }
                if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) {
                    if (moveUp()) {
                        placeNewBlock();
                    }
                }
                if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) {
                    if (moveRight()) {
                        placeNewBlock();
                    }
                }
                if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) {
                    if (moveLeft()) {
                        placeNewBlock();
                    }
                }
            }
        });
        //Calling function when window closes
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                try {
                    close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        //Starting the game
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        int numStart;
        if (sizeRow * sizeCol < SIZE_SMALL) {
            numStart = SMALL_START;
        }
        else if (sizeRow * sizeCol < SIZE_MEDIUM) {
            numStart = MEDIUM_START;
        }
        else {
            numStart = LARGE_START;
        }
        while (numStart > 0) {
            int row = (int)(Math.random() * sizeRow);
            int col = (int)(Math.random() * sizeCol);
            int num = (Math.random() < MAIN_PROB) ? MAIN_BLOCK : SECOND_BLOCK;
            if (board[row][col].getText().equals("")) {
                updateLabel(row,col,num);

                numStart--;
            }
        }
    }

    /**
     * Initiates the JLabels to represent the squares
     * @param i X location of JLabel
     * @param j Y location of JLabel
     */
    private static void initLabel(int i, int j) {
        board[i][j] = new JLabel("", JLabel.CENTER);
        board[i][j].setPreferredSize(new Dimension(SQUARE_SIZE,SQUARE_SIZE));
        board[i][j].setOpaque(true);
        board[i][j].setBackground(DEFAULT_COLOR);
        board[i][j].setForeground(SMALL_TEXT_COLOR);
        board[i][j].setFont(new Font(Font.SANS_SERIF,Font.BOLD,
                FONT_SIZE));
        mainPanel.add(board[i][j]);
    }

    /**
     * Updates the value of a given JLabel
     * @param i X location of JLabel
     * @param j Y location of JLabel
     * @param val New value of JLabel
     */
    private static void updateLabel(int i, int j, int val) {
        JLabel label = board[i][j];
        if (val == 0) {
            label.setText("");
            label.setBackground(DEFAULT_COLOR);
        }
        else {
            label.setText(val + "");
            if (val == 2) {
                label.setBackground(TWO_COLOR);
            }
            else if (val == 4) {
                label.setBackground(FOUR_COLOR);
            }
            else if (val == 8) {
                label.setBackground(EIGHT_COLOR);
            }
            else if (val == 16) {
                label.setBackground(SIXTEEN_COLOR);
            }
            else if (val == 32) {
                label.setBackground(COLOR_32);
            }
            else if (val == 64) {
                label.setBackground(COLOR_64);
            }
            else if (val == 128) {
                label.setBackground(COLOR_128);
            }
            else if (val == 256) {
                label.setBackground(COLOR_256);
            }
            else if (val == 512) {
                label.setBackground(COLOR_512);
            }
            else if (val == 1024) {
                label.setBackground(COLOR_1024);
            }
            else if (val == 2048) {
                label.setBackground(COLOR_2048);
            }
            else {
                label.setBackground(COLOR_OTHER);
            }
        }
        if (val < 8) {
            label.setForeground(SMALL_TEXT_COLOR);
        }
        else {
            label.setForeground(BIG_TEXT_COLOR);
        }
        
    } 

    /**
     * Places the new 2 or 4 block once a move has been made
     * @return True if a block was successfully placed, false otherwise
     */
    private static boolean placeNewBlock() {
        int count = 0;
        for (int i = 0; i < sizeRow; i++) {
            for (int j = 0; j < sizeCol; j++) {
                if (board[i][j].getText().equals("")) {
                    count++;
                }
            }
        }
        int place = (int)(Math.random() * count) + 1;
        count = 0;
        for (int i = 0; i < sizeRow; i++) {
            for (int j = 0; j < sizeCol; j++) {
                if (board[i][j].getText().equals("")) {
                    if (++count == place) {
                        updateLabel(i,j,((Math.random() < MAIN_PROB) ? 
                                MAIN_BLOCK : SECOND_BLOCK));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Moves all blocks down if applicable
     * @return True if a block was moved, false if not
     */
    private static boolean moveDown() {
        boolean moved = false;
        ArrayList<JLabel> merged = new ArrayList<JLabel>();
        //Go through all the blocks that could potentially go down
        for (int r = (sizeRow-1)-OFFSET; r >=0; r--) {
            for (int c = 0; c < sizeCol; c++) {
                //Checking if block is in play and should be moved
                String blockText = board[r][c].getText();
                if (!blockText.equals("")) {
                    //Now go through all the potential spaces the block could go
                    for (int i = r; i < sizeRow; i++) {
                        //If the edge is reached, place the block there
                        if (i+1 == sizeRow) {
                            updateLabel(i,c,Integer.parseInt(blockText));
                            updateLabel(r,c,0);
                            moved = true;
                        }
                        //If a block is found
                        else if (!board[i+1][c].getText().equals("")) {
                            //If they are the same number, merge them
                            if (board[i+1][c].getText().equals(blockText)) {
                                if (!merged.contains(board[i+1][c])) {
                                    merge(r,c,i+1,c);
                                    merged.add(board[i+1][c]);
                                }
                                else {
                                    updateLabel(i,c,
                                            Integer.parseInt(blockText));
                                    updateLabel(r,c,0);
                                }
                                moved = true;
                            }
                            //As long as a move would actually be made
                            else if (i != r) {
                                updateLabel(i,c,Integer.parseInt(blockText));
                                updateLabel(r,c,0);
                                moved = true;
                            }
                            break;
                        }
                    }
                }
            }
        }
        return moved;
    }

    /**
     * Moves all blocks up if applicable
     * @return True if a block was moved, false if not
     */
    private static boolean moveUp() {
        boolean moved = false;
        ArrayList<JLabel> merged = new ArrayList<JLabel>();
        //Go through all the blocks that could potentially go up
        for (int r = OFFSET; r < sizeRow; r++) {
            for (int c = 0; c < sizeCol; c++) {
                //Checking if block is in play and should be moved
                String blockText = board[r][c].getText();
                if (!blockText.equals("")) {
                    //Now go through all the potential spaces the block could go
                    for (int i = r; i >= 0; i--) {
                        //If the edge is reached, place the block there
                        if (i-1 == -1) {
                            updateLabel(i,c,Integer.parseInt(blockText));
                            updateLabel(r,c,0);
                            moved = true;
                        }
                        //If a block is found
                        else if (!board[i-1][c].getText().equals("")) {
                            //If they are the same number, merge them
                            if (board[i-1][c].getText().equals(blockText)) {
                                if (!merged.contains(board[i-1][c])) {
                                    merge(r,c,i-1,c);
                                    merged.add(board[i-1][c]);
                                }
                                else {
                                    updateLabel(i,c,
                                            Integer.parseInt(blockText));
                                    updateLabel(r,c,0);
                                }
                                moved = true;
                            }
                            //As long as a move would actually be made
                            else if (i != r) {
                                updateLabel(i,c,Integer.parseInt(blockText));
                                updateLabel(r,c,0);
                                moved = true;
                            }
                            break;
                        }
                    }
                }
            }
        }
        return moved;
    }

    /**
     * Moves all blocks left if applicable
     * @return True if a block was moved, false if not
     */
    private static boolean moveLeft() {
        boolean moved = false;
        ArrayList<JLabel> merged = new ArrayList<JLabel>();
        //Go through all the blocks that could potentially go left
        for (int r = 0; r < sizeRow; r++) {
            for (int c = OFFSET; c < sizeCol; c++) {
                //Checking if block is in play and should be moved
                String blockText = board[r][c].getText();
                if (!blockText.equals("")) {
                    //Now go through all the potential spaces the block could go
                    for (int i = c; i >= 0; i--) {
                        //If the edge is reached, place the block there
                        if (i-1 == -1) {
                            updateLabel(r,i,Integer.parseInt(blockText));
                            updateLabel(r,c,0);
                            moved = true;
                        }
                        //If a block is found
                        else if (!board[r][i-1].getText().equals("")) {
                            //If they are the same number, merge them
                            if (board[r][i-1].getText().equals(blockText)) {
                                if (!merged.contains(board[r][i-1])) {
                                    merge(r,c,r,i-1);
                                    merged.add(board[r][i-1]);
                                }
                                else {
                                    updateLabel(r,i,
                                            Integer.parseInt(blockText));
                                    updateLabel(r,c,0);
                                }
                                moved = true;
                            }
                            //As long as a move would actually be made
                            else if (i != c) {
                                updateLabel(r,i,Integer.parseInt(blockText));
                                updateLabel(r,c,0);
                                moved = true;
                            }
                            break;
                        }
                    }
                }
            }
        }
        return moved;
    }

    /**
     * Moves all blocks right if applicable
     * @return True if a block was moved, false if not
     */
    private static boolean moveRight() {
        boolean moved = false;
        ArrayList<JLabel> merged = new ArrayList<JLabel>();
        //Go through all the blocks that could potentially go right
        for (int r = 0; r < sizeRow; r++) {
            for (int c = sizeCol-1-OFFSET; c >= 0; c--) {
                //Checking if block is in play and should be moved
                String blockText = board[r][c].getText();
                if (!blockText.equals("")) {
                    //Now go through all the potential spaces the block could go
                    for (int i = c; i < sizeCol; i++) {
                        //If the edge is reached, place the block there
                        if (i+1 == sizeCol) {
                            updateLabel(r,i,Integer.parseInt(blockText));
                            updateLabel(r,c,0);
                            moved = true;
                        }
                        //If a block is found
                        else if (!board[r][i+1].getText().equals("")) {
                            //If they are the same number, merge them
                            if (board[r][i+1].getText().equals(blockText)) {
                                if (!merged.contains(board[r][i+1])) {
                                    merge(r,c,r,i+1);
                                    merged.add(board[r][i+1]);
                                }
                                else {
                                    updateLabel(r,i,
                                            Integer.parseInt(blockText));
                                    updateLabel(r,c,0);
                                }
                                moved = true;
                            }
                            //As long as a move would actually be made
                            else if (i != c) {
                                updateLabel(r,i,Integer.parseInt(blockText));
                                updateLabel(r,c,0);
                                moved = true;
                            }
                            break;
                        }
                    }
                }
            }
        }
        return moved;
    }

    /**
     * Merges two blocks together and updates the game score
     * @param x1 X-coord of block that will be lost
     * @param y1 Y-coord of block that will be lost
     * @param x2 X-coord of block that will be merged into
     * @param y2 Y-coord of block that will be merged into
     */
    private static void merge(int x1, int y1, int x2, int y2) {
        updateLabel(x1,y1,0);
        updateLabel(x2,y2,Integer.parseInt(board[x2][y2].getText()) << 1);
        score += Integer.parseInt(board[x2][y2].getText());
        curScore.setText(CUR_SCORE + score);
        if (score > highScore) {
            highScore = score;
            highScoreLabel.setText(HIGH_SCORE + highScore);
        }
    }

    /**
     * Updates the high score information if this session created a new high 
     * score
     * @throws FileNotFoundException
     */
    private static void close() throws FileNotFoundException {
        if (score == highScore) {
            PrintWriter hsWrite = new PrintWriter(fileHS);
            Date time = new Date();
            hsWrite.printf(HIGH_SCORE_FILE_FORMAT,highScore,sizeRow,sizeCol,
                    time);
            hsWrite.close();
        }
    }
}