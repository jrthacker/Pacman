import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    private Dimension d;
    private final Font myFont = new Font("Times New Roman", Font.BOLD, 24);

    private Image ii;
    private final Color dotColor = new Color(192, 192, 0); // yellow color
    private Color mazeColor;

    private boolean gameRunning = false;
    private boolean lifeLost = false;
    private boolean ghostScatter = false;
    private boolean ghostScared = false;
    private boolean ghostRecovering = false;

    private final int SQUARE_SIZE = 48;
    private final int BLOCKS_N = 15;
    private final int BOARD_SIZE = BLOCKS_N * SQUARE_SIZE;
    private final int ANIMATION_DELAY = 2;
    private final int ANIMATION_COUNT = 4;
    private final int PACSPEED = 8;

    private int animatePacman = ANIMATION_DELAY;
    private int animateDirection = 1;
    private int animatePosition = 0;
    private int livesLeft, score, level;
    private int[] moveX, moveY;
    private int[] enemyX, enemyY, enemyMoveX, enemyMoveY, ghostSpeed;

    private Image redghost, pinkghost, powderghost, orangeghost, scaredghost, scaredghost2, scaredIcon, cherry;
    private Image pacman, pacup1, pacleft1, pacright1, pacdown1;
    private Image pacup2, pacdown2, pacleft2, pacright2;
    private Image pacup3, pacdown3, pacleft3, pacright3;

    private int pacX, pacY, pacMoveX, pacMoveY;
    private int requestX, requestY, viewX, viewY;
    
    private static Clip clip;
    private File beginning, eatGhost, death, eatFruit, chomp;
    private boolean chompplaying = false;
    private boolean sound = true;

   

    private final int mazeData[] = {

            19, 26, 26, 26, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,

            21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,

            37, 0, 0, 0, 17, 16, 16, 16, 64, 16, 16, 16, 16, 16, 36,

            21, 0, 0, 0, 17, 16, 16, 24, 16, 16, 16, 16, 16, 16, 20,

            17, 18, 18, 18, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 20,

            17, 16, 16, 16, 16, 16, 20, 0, 17, 16, 16, 64, 16, 24, 20,

            17, 16, 16, 32, 24, 24, 28, 0, 25, 24, 24, 24, 28, 0, 21,

            17, 16, 16, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21,

            17, 16, 16, 16, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 20,

            17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,

            17, 16, 16, 32, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,

            17, 16, 24, 24, 24,24, 16, 16, 16, 16, 16, 16, 16, 16, 20,

            17, 20, 0, 0, 0, 0, 17, 16, 16, 16, 16, 16, 32, 16, 20,

            17, 16, 18, 66, 18, 18, 16, 16, 16, 16, 16, 16, 16, 16, 20,

            25, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28

            };

            private final int mazeData2[] = {

            19, 26, 42, 26, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,

            21, 0, 0, 0, 17, 16, 16, 16, 24, 24, 24, 24, 24, 16, 20,

            69, 0, 0, 0, 17, 16, 16, 20, 0, 0, 0, 0, 0, 17, 20,

            21, 0, 0, 0, 17, 16, 32, 24, 18, 18, 18, 18, 18, 16, 20,

            17, 18, 18, 18, 16, 16, 20, 0, 17, 16, 16, 64, 16, 16, 20,

            17, 16, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 16, 24, 20,

            25, 16, 16, 64, 24, 24, 28, 0, 25, 24, 24, 40, 28, 0, 21,

            1, 17, 16, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21,

            1, 33, 16, 16, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 20,

            1, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,

            1, 17, 24, 24, 24, 24, 24, 16, 16, 16, 16, 16, 16, 16, 20,

            1, 21, 0, 0, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 20,

            1, 17, 18, 34, 18, 18, 18, 16, 16, 16, 16, 16, 16, 16, 36,

            1, 25, 24, 24, 24, 24, 24, 24, 24, 24, 16, 16, 16, 16, 20,

            9, 8, 8, 8, 8, 8, 8, 8, 8, 8, 25, 24, 24, 24, 28

            };

            private final int mazeData3[] = {

            19, 26, 26, 26, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,

            21, 0, 0, 0, 17, 16, 16, 16, 24, 40, 24, 24, 24, 16, 20,

            21, 0, 0, 0, 17, 16, 16, 20, 0, 0, 0, 0, 0, 17, 20,

            21, 0, 0, 0, 17, 16, 16, 24, 18, 18, 18, 18, 18, 16, 20,

            17, 18, 34, 18, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 20,

            17, 16, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 16, 24, 20,

            17, 16, 64, 16, 24, 24, 28, 0, 25, 24, 24, 24, 28, 0, 21,

            17, 16, 16, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21,

            17, 16, 16, 16, 18, 18, 18, 18, 18, 18, 18, 26, 18, 18, 20,

            17, 16, 16, 16, 16, 16, 16, 16, 16, 64, 20, 0, 17, 16, 20,

            17, 16, 24, 24, 24, 24, 24, 16, 16, 16, 20, 0, 33, 16, 20,

            17, 36, 0, 0, 0, 0, 0, 17, 16, 16, 20, 0, 17, 16, 20,

            17, 16, 18, 18, 18, 18, 18, 16, 16, 16, 20, 0, 17, 16, 20,

            25, 24, 24, 24, 24, 24, 24, 24, 24, 24, 16, 66, 16, 16, 20,

            11, 8, 8, 8, 8, 8, 8, 8, 8, 8, 41, 24, 24, 24, 28

            };

            private final int mazeData4[] = {

            19, 26, 26, 26, 18, 18, 18, 18, 66, 18, 18, 18, 18, 18, 22,

            21, 0, 0, 0, 17, 16, 16, 16, 24, 24, 24, 24, 24, 16, 20,

            21, 0, 0, 0, 25, 24, 16, 20, 0, 0, 0, 0, 0, 17, 36,

            21, 0, 0, 0, 0, 0, 17, 24, 18, 18, 18, 18, 18, 16, 20,

            17, 18, 18, 18, 22, 0, 21, 0, 17, 16, 16, 16, 16, 16, 68,

            17, 16, 16, 16, 20, 0, 21, 0, 17, 16, 16, 16, 16, 24, 20,

            25, 16, 32, 16, 24, 26, 28, 0, 25, 40, 24, 24, 28, 0, 21,

            1, 17, 16, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21,

            1, 17, 16, 16, 26, 18, 18, 18, 18, 18, 18, 26, 18, 18, 20,

            1, 17, 16, 20, 0, 17, 16, 16, 16, 16, 20, 0, 17, 16, 20,

            1, 17, 24, 28, 0, 41, 24, 16, 16, 16, 20, 0, 17, 16, 20,

            1, 21, 0, 0, 0, 0, 0, 17, 16, 16, 20, 0, 17, 16, 20,

            1, 17, 18, 18, 18, 18, 18, 16, 16, 16, 20, 0, 17, 16, 20,

            9, 17, 16, 64, 16, 16, 16, 16, 16, 16, 16, 18, 32, 16, 20,

            27, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28

            };
    private final int validSpeeds[] = {2, 4, 6, 8};
    private final int maxSpeed = 3;

    private int currentSpeed = 1;
    private int[] levelData;
    private Timer timer;
    private Timer scatter;
    private Timer scare;
    private Timer chase;
    private Timer recover;
    private Timer flashing;
    private int levelNum = 0;
    
    // Constructor for the Board, load images and initialize vars and board state
    public Board() {
        loadImagesSounds();
        initBoard();
        initVariables();
        
    }
    
 // initialize sprites and sounds
    private void loadImagesSounds() {
        redghost = new ImageIcon("images/redghost.png").getImage();  // enemy image
        pinkghost = new ImageIcon("images/pinkghost.png").getImage();
        powderghost = new ImageIcon("images/powderghost.png").getImage();
        orangeghost = new ImageIcon("images/orangeghost.png").getImage();
        scaredghost = new ImageIcon("images/scaredghost.png").getImage();
        scaredghost2 = new ImageIcon("images/scaredghost2.png").getImage();
        pacman = new ImageIcon("images/pacman.png").getImage(); // full pacman
        pacup1 = new ImageIcon("images/pacmanup1.png").getImage(); // begin opening
        pacup2 = new ImageIcon("images/pacmanup2.png").getImage();
        pacup3 = new ImageIcon("images/pacmanup3.png").getImage(); // completely open
        pacdown1 = new ImageIcon("images/pacmandown1.png").getImage();
        pacdown2 = new ImageIcon("images/pacmandown2.png").getImage();
        pacdown3 = new ImageIcon("images/pacmandown3.png").getImage();
        pacleft1 = new ImageIcon("images/pacmanleft1.png").getImage();
        pacleft2 = new ImageIcon("images/pacmanleft2.png").getImage();
        pacleft3 = new ImageIcon("images/pacmanleft3.png").getImage();
        pacright1 = new ImageIcon("images/pacmanright1.png").getImage();
        pacright2 = new ImageIcon("images/pacmanright2.png").getImage();
        pacright3 = new ImageIcon("images/pacmanright3.png").getImage();
        cherry = new ImageIcon("images/cherry.png").getImage();
        beginning = new File("sounds/pacman_beginning.wav");
        death = new File("sounds/pacman_death.wav");
        chomp = new File("sounds/pacman_chomp.wav");
        eatGhost = new File("sounds/pacman_eatghost.wav");
        eatFruit = new File("sounds/pacman_eatfruit.wav");
    }
    
    // adds controls to the board and sets background
    private void initBoard() {
        addKeyListener(new Controls()); // add controls
        setFocusable(true);
        setBackground(Color.black);
    }

    // provides initial values for a new game
    private void initVariables() {
        levelData = new int[BLOCKS_N * BLOCKS_N];
        mazeColor = new Color(5, 5, 200);   // blue
        d = new Dimension(1600, 1600);
        enemyX = new int[4];
        enemyMoveX = new int[4];
        enemyY = new int[4];
        enemyMoveY = new int[4];
        ghostSpeed = new int[4];
        moveX = new int[4];
        moveY = new int[4];
        timer = new Timer(40, this);
        // scatter timer, ghosts scatter to corners
        scatter = new Timer(7000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ghostScatter = false;
                scatter.stop();
                chase.start();
            }
        });
        // chase timer, ghosts chase pacman for a set time before scattering
        chase = new Timer(21000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ghostScatter = true;
                chase.stop();
                scatter.start();
            }
        });
        // recover timer, ghosts are beginning to recover from being scared
        recover = new Timer(5000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ghostRecovering = true;
                scaredIcon = scaredghost;
                flashing.start();
                recover.stop();
            }
        });
        // flashing of ghost pixels
        flashing = new Timer (250, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (scaredIcon == scaredghost)
                    scaredIcon = scaredghost2;
                else
                    scaredIcon = scaredghost;
            }
        });
        // timer for how scared the ghosts are, ghosts scatter and start to recover after 5 seconds
        scare = new Timer(10000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ghostScared = false;
                ghostScatter = false;
                ghostRecovering = false;
                scare.stop();
                flashing.stop();
                chase.start();
                int random = (int) (Math.random() * (currentSpeed + 1));
                if (random > currentSpeed) {
                    random = currentSpeed;
                }
                for (int i = 0; i < 4; i++) {
                    if (ghostSpeed[i] == 0) {
                        enemyX[i] = 4 * SQUARE_SIZE;
                        enemyY[i] = 4 * SQUARE_SIZE;
                        ghostSpeed[i] = validSpeeds[random];
                    }
                }
            }
        });
        // overall game timer
        timer.start();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        initGame();
    }
    
 // show the introduction screen and exit controls
    private void showIntroScreen(Graphics2D g) {
        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, BOARD_SIZE / 2 - 30, BOARD_SIZE - 100, 50 +  SQUARE_SIZE);
        g.setColor(Color.white);
        g.drawRect(50, BOARD_SIZE / 2 - 30, BOARD_SIZE - 100, 50 + SQUARE_SIZE);
        String introString = "Press Enter to start and ESC to exit";
        String introString2 = "Press P to Pause and M to Mute";
        FontMetrics metr = this.getFontMetrics(myFont);
        g.setColor(Color.white);
        g.setFont(myFont);
        g.drawString(introString, (BOARD_SIZE - metr.stringWidth(introString)) / 2, BOARD_SIZE / 2);
        g.drawString(introString2, (BOARD_SIZE - metr.stringWidth(introString2)) / 2, BOARD_SIZE / 2 + SQUARE_SIZE);
    }

    // animate pacman
    private void doAnim() {
    	// count down the animations 
        animatePacman--;
        
        if (animatePacman <= 0) {
            animatePacman = ANIMATION_DELAY; // reset counter
            animatePosition = animatePosition + animateDirection; 

            if (animatePosition == (ANIMATION_COUNT - 1) || animatePosition == 0) {
                animateDirection = -animateDirection;
            }
        }
    }

    // gameplay, account for lives lost or else move pacman and redraw maze with ghosts
    private void playGame(Graphics2D g2d) {
        if (lifeLost) {
            death();
        } else {
            movePacman();
            drawPacman(g2d);
            moveGhosts(g2d);
            checkMaze();
        }
    }
    
    

    // draw the score at the bottom of the screen
    private void drawScore(Graphics2D g) {
        String scoreString;
        g.setFont(myFont);
        g.setColor(new Color(96, 128, 255));
        scoreString = "Level: " + level + "        Score: " + score;
        g.drawString(scoreString, 20, BOARD_SIZE + 16);
        for (int i = 0; i < livesLeft; i++) {
            g.drawImage(pacleft2, (i * 56 + 16) + BOARD_SIZE/2, BOARD_SIZE + 1, this);
        }
    }

    // check the maze to make sure that all dots have been eaten in order to advance levels
    private void checkMaze() {
        int i = 0;
        boolean finished = true;
        while (i < BLOCKS_N * BLOCKS_N && finished) {
            if ((levelData[i] & 112) != 0) { // if there are pellets left to eat
                finished = false; // level continues
            }
            i++;
        }
        if (finished) { // if finished, increase score, level, and increase enemy speed
            score += 250;
            level += 1;
            if (currentSpeed < maxSpeed) {
                currentSpeed++;
            }
            levelNum++;
            if (levelNum == 4)
            	levelNum = 0;
            initLevel(levelNum);
        }
    }
    // if pacman collides with a ghost, reduce number of lives and if 0 then game over
    private void death() {
        livesLeft--;
        if (livesLeft == 0) {
            gameRunning = false;
        }
        continueLevel(); // pacman respawns after losing life
    }
    // ghost movement behavior
    private void moveGhosts(Graphics2D g2d) {
        int i;
        int pos;
        int count;
        for (i = 0; i < 4; i++) {
            if (enemyX[i] % SQUARE_SIZE == 0 && enemyY[i] % SQUARE_SIZE == 0) {
                pos = enemyX[i] / SQUARE_SIZE + BLOCKS_N * (int) (enemyY[i] / SQUARE_SIZE);
                count = 0;
                // check for valid moves for each ghost
                if ((levelData[pos] & 1) == 0 && enemyMoveX[i] != 1) {
                    moveX[count] = -1;
                    moveY[count] = 0;
                    count++;
                }
                if ((levelData[pos] & 2) == 0 && enemyMoveY[i] != 1) {
                    moveX[count] = 0;
                    moveY[count] = -1;
                    count++;
                }
                if ((levelData[pos] & 4) == 0 && enemyMoveX[i] != -1) {
                    moveX[count] = 1;
                    moveY[count] = 0;
                    count++;
                }
                if ((levelData[pos] & 8) == 0 && enemyMoveY[i] != -1) {
                    moveX[count] = 0;
                    moveY[count] = 1;
                    count++;
                }
                if (count == 0) {
                    if ((levelData[pos] & 15) == 15) {
                        enemyMoveX[i] = 0;
                        enemyMoveY[i] = 0;
                    } else {
                        enemyMoveX[i] = -enemyMoveX[i];
                        enemyMoveY[i] = -enemyMoveY[i];
                    }

                } else {
                    switch(i) {
                    case 0: // red ghost
                    	// scared behavior is to move up and to left
                        if (ghostScatter || ghostScared) {
                            if ((levelData[pos] & 1) == 0 && enemyMoveX[i] != 1) {
                                enemyMoveX[i] = -1;
                                enemyMoveY[i] = 0;
                            }
                            else if ((levelData[pos] & 2) == 0 && enemyMoveY[i] != 1) {
                                enemyMoveX[i] = 0;
                                enemyMoveY[i] = -1;
                            }
                            else {
                                count = (int) (Math.random() * count);
                                if (count > 3) {
                                    count = 3;
                                }
                                enemyMoveX[i] = moveX[count];
                                enemyMoveY[i] = moveY[count];
                            }
                        }
                        // chase behavior is to follow pacman directly
                        else if ((enemyX[i] - pacX) > 0 && ((levelData[pos] & 1) == 0 && enemyMoveX[i] != 1)) {
                            enemyMoveX[i] = -1;
                            enemyMoveY[i] = 0;
                        }
                        else if ((enemyY[i] - pacY) > 0 && ((levelData[pos] & 2) == 0 && enemyMoveY[i] != 1)) {
                            enemyMoveX[i] = 0;
                            enemyMoveY[i] = -1;
                        }
                        else if ((pacX - enemyX[i]) > 0 && ((levelData[pos] & 4) == 0 && enemyMoveX[i] != -1)) {
                            enemyMoveX[i] = 1;
                            enemyMoveY[i] = 0;
                        }
                        else if ((pacY - enemyY[i]) > 0 && ((levelData[pos] & 8) == 0 && enemyMoveY[i] != -1)) {
                            enemyMoveX[i] = 0;
                            enemyMoveY[i] = 1;
                        }
                        else {
                            count = (int) (Math.random() * count);
                            if (count > 3) {
                                count = 3;
                            }
                            enemyMoveX[i] = moveX[count];
                            enemyMoveY[i] = moveY[count];
                        }
                        break;
                    case 1: // pink ghost
                    	// scared behavior is to move up and to the right
                        if (ghostScatter) {
                            if ((levelData[pos] & 4) == 0 && enemyMoveX[i] != -1) {
                                enemyMoveX[i] = 1;
                                enemyMoveY[i] = 0;
                            }
                            else if ((levelData[pos] & 2) == 0 && enemyMoveY[i] != 1) {
                                enemyMoveX[i] = 0;
                                enemyMoveY[i] = -1;
                            }
                            else {
                                count = (int) (Math.random() * count);
                                if (count > 3) {
                                    count = 3;
                                }
                                enemyMoveX[i] = moveX[count];
                                enemyMoveY[i] = moveY[count];
                            }
                        }
                        // chase behavior is to try to move 4 spots ahead of pacman
                        else if ((enemyX[i] - (pacX + 4 * pacMoveX * PACSPEED)) > 0 && ((levelData[pos] & 1) == 0 && enemyMoveX[i] != 1)) {
                            enemyMoveX[i] = -1;
                            enemyMoveY[i] = 0;
                        }
                        else if ((enemyY[i] - (pacY + 4 * pacMoveY * PACSPEED)) > 0 && ((levelData[pos] & 2) == 0 && enemyMoveY[i] != 1)) {
                            enemyMoveX[i] = 0;
                            enemyMoveY[i] = -1;
                        }
                        else if (((pacX + 4 * pacMoveX * PACSPEED) - enemyX[i]) > 0 && ((levelData[pos] & 4) == 0 && enemyMoveX[i] != -1)) {
                            enemyMoveX[i] = 1;
                            enemyMoveY[i] = 0;
                        }
                        else if (((pacY + 4 * pacMoveY * PACSPEED) - enemyY[i]) > 0 && ((levelData[pos] & 8) == 0 && enemyMoveY[i] != -1)) {
                            enemyMoveX[i] = 0;
                            enemyMoveY[i] = 1;
                        }
                        else {
                            count = (int) (Math.random() * count);
                            if (count > 3) {
                                count = 3;
                            }
                            enemyMoveX[i] = moveX[count];
                            enemyMoveY[i] = moveY[count];
                        }
                        break;
                    case 2: // powder ghost
                    	// scared behavior is to move down and to the right
                        if (ghostScatter) {
                            if ((levelData[pos] & 4) == 0 && enemyMoveX[i] != -1) {
                                enemyMoveX[i] = 1;
                                enemyMoveY[i] = 0;
                            }
                            else if ((levelData[pos] & 8) == 0 && enemyMoveY[i] != -1) {
                                enemyMoveX[i] = 0;
                                enemyMoveY[i] = 1;
                            }
                            else {
                                count = (int) (Math.random() * count);
                                if (count > 3) {
                                    count = 3;
                                }
                                enemyMoveX[i] = moveX[count];
                                enemyMoveY[i] = moveY[count];
                            }
                        }
                        // chase behavior is to try corner pacman against the red ghost
                        else if ((enemyX[i] - (pacX + 2*((pacX + 2 * PACSPEED * pacMoveX) - enemyX[0]))) > 0 && ((levelData[pos] & 1) == 0 && enemyMoveX[i] != 1)) {
                            enemyMoveX[i] = -1;
                            enemyMoveY[i] = 0;
                        }
                        else if ((enemyY[i] - (pacY + 2*((pacY + 2 * PACSPEED * pacMoveY) - enemyY[0]))) > 0 && ((levelData[pos] & 2) == 0 && enemyMoveY[i] != 1)) {
                            enemyMoveX[i] = 0;
                            enemyMoveY[i] = -1;
                        }
                        else if (((pacX + 2*((pacX + 2 * PACSPEED * pacMoveX)  - enemyX[0])) - enemyX[i]) > 0 && ((levelData[pos] & 4) == 0 && enemyMoveX[i] != -1)) {
                            enemyMoveX[i] = 1;
                            enemyMoveY[i] = 0;
                        }
                        else if (((pacY + 2*((pacY + 2 * PACSPEED * pacMoveY)  - enemyY[0])) - enemyY[i]) > 0 && ((levelData[pos] & 8) == 0 && enemyMoveY[i] != -1)) {
                            enemyMoveX[i] = 0;
                            enemyMoveY[i] = 1;
                        }
                        else {
                            count = (int) (Math.random() * count);
                            if (count > 3) {
                                count = 3;
                            }
                            enemyMoveX[i] = moveX[count];
                            enemyMoveY[i] = moveY[count];
                        }
                        break;
                    case 3: // orange ghost
                    	// orange ghsot move closer to pac man if far away, but then move towards the bottom left corner if he gets too close
                        if ((Math.pow((enemyX[i] - pacX), 2) + Math.pow((enemyY[i] - pacY), 2)) > (64 * Math.pow(SQUARE_SIZE, 2))) {
                            if ((enemyX[i] - pacX) > 0 && ((levelData[pos] & 1) == 0 && enemyMoveX[i] != 1)) {
                                enemyMoveX[i] = -1;
                                enemyMoveY[i] = 0;
                            }
                            else if ((enemyY[i] - pacY) > 0 && ((levelData[pos] & 2) == 0 && enemyMoveY[i] != 1)) {
                                enemyMoveX[i] = 0;
                                enemyMoveY[i] = -1;
                            }
                            else if ((pacX - enemyX[i]) > 0 && ((levelData[pos] & 4) == 0 && enemyMoveX[i] != -1)) {
                                enemyMoveX[i] = 1;
                                enemyMoveY[i] = 0;
                            }
                            else if ((pacY - enemyY[i]) > 0 && ((levelData[pos] & 8) == 0 && enemyMoveY[i] != -1)) {
                                enemyMoveX[i] = 0;
                                enemyMoveY[i] = 1;
                            }
                            else {
                                count = (int) (Math.random() * count);
                                if (count > 3) {
                                    count = 3;
                                }
                                enemyMoveX[i] = moveX[count];
                                enemyMoveY[i] = moveY[count];
                            }
                        }
                        else {
                            if ((levelData[pos] & 1) == 0 && enemyMoveX[i] != 1) {
                                enemyMoveX[i] = -1;
                                enemyMoveY[i] = 0;
                            }
                            else if ((levelData[pos] & 8) == 0 && enemyMoveY[i] != -1) {
                                enemyMoveX[i] = 0;
                                enemyMoveY[i] = 1;
                            }
                            else {
                                count = (int) (Math.random() * count);
                                if (count > 3) {
                                    count = 3;
                                }
                                enemyMoveX[i] = moveX[count];
                                enemyMoveY[i] = moveY[count];
                            }
                        }
                        break;
                    }
                    
                }

            }
            // update ghost sprites
            enemyX[i] = enemyX[i] + (enemyMoveX[i] * ghostSpeed[i]);
            enemyY[i] = enemyY[i] + (enemyMoveY[i] * ghostSpeed[i]);
            // use timer flags to determine ghost state
            if (ghostRecovering)
                g2d.drawImage(scaredIcon, enemyX[i] + 1, enemyY[i] + 1, this);
            else if (ghostScared) {
                    g2d.drawImage(scaredghost, enemyX[i] + 1, enemyY[i] + 1, this);
            }
            else {
            	// switch case to select correct color of ghost sprite
                switch(i) {
                case 0:
                    g2d.drawImage(redghost, enemyX[i] + 1, enemyY[i] + 1, this);
                    break;
                case 1:
                    g2d.drawImage(pinkghost, enemyX[i] + 1, enemyY[i] + 1, this);
                    break;
                case 2:
                    g2d.drawImage(powderghost, enemyX[i] + 1, enemyY[i] + 1, this);
                    break;
                case 3:
                    g2d.drawImage(orangeghost, enemyX[i] + 1, enemyY[i] + 1, this);
                    break;
                }
            }
            // if ghosts are scared, pacman can collide in order to score points and eat them
            if (ghostScared) {
                if (pacX > (enemyX[i] - 20) && pacX < (enemyX[i] + 20)
                        && pacY > (enemyY[i] - 20) && pacY < (enemyY[i] + 20)
                        && gameRunning) {
                    score += 100;
                    enemyX[i] = 2 * SQUARE_SIZE;
                    enemyY[i] = 2 * SQUARE_SIZE;
                    ghostSpeed[i] = 0;
                    if(sound)
                    	playSound(eatGhost);
                }
            } 
            // else if pacman gets too close, then pacman loses a life and respawns
            else {
                if (pacX > (enemyX[i] - 20) && pacX < (enemyX[i] + 20)
                        && pacY > (enemyY[i] - 20) && pacY < (enemyY[i] + 20)
                        && gameRunning) {
                    lifeLost = true;
                    if(sound)
                    	playSound(death);
                }
            }
        }
    }
    
    // method to move pacman around the maze
    private void movePacman() {
        int pos;
        int ch;
        if (requestX == -pacMoveX && requestY == -pacMoveY) {
            pacMoveX = requestX;
            pacMoveY = requestY;
            viewX = pacMoveX;
            viewY = pacMoveY;
        }
        if (pacX % SQUARE_SIZE == 0 && pacY % SQUARE_SIZE == 0) {
            pos = pacX / SQUARE_SIZE + BLOCKS_N * (int) (pacY / SQUARE_SIZE);
            ch = levelData[pos];
            if ((ch & 16) != 0) { // score points for eating pellets
                levelData[pos] = (int) (ch & 15);
                if (chompplaying) // play sound every other pellet
                	chompplaying = false;
                else {
                	if(sound)
                		playSound(chomp);
                	chompplaying = true;
                }
                score++;
            }
            if ((ch & 32) != 0) { // collect power ups to gain points and to make ghosts vulnerable
                levelData[pos] = (int) (ch & 31);
                score++;
                if (chompplaying) // play sound every other pellet
                	chompplaying = false;
                else {
                	if(sound)
                		playSound(chomp);
                	chompplaying = true;
                }
                scare.restart(); // start scared timer and recovering timers
                recover.restart();
                scatter.stop(); // stop scatter and chase behavior timers
                chase.stop();
                ghostScared = true; // set ghost state to scared
                ghostRecovering = false;
            }
            if ((ch & 64) != 0) { // eat bonus cheery
            	levelData[pos] = (int) (ch & 63);
            	score += 100;
            	if(sound)
            		playSound(eatFruit);
            }
            // check that player is request valid moves
            if (requestX != 0 || requestY != 0) {
                if (!((requestX == -1 && requestY == 0 && (ch & 1) != 0)
                        || (requestX == 1 && requestY == 0 && (ch & 4) != 0)
                        || (requestX == 0 && requestY == -1 && (ch & 2) != 0)
                        || (requestX == 0 && requestY == 1 && (ch & 8) != 0))) {
                	// if move is valid, move pacman in that direction
                    pacMoveX = requestX;
                    pacMoveY = requestY;
                    viewX = pacMoveX; // set the direction pacman is facing for drawing
                    viewY = pacMoveY;
                }
            }
            // check that Pacman doesn't get stuck in an impossible space
            if ((pacMoveX == -1 && pacMoveY == 0 && (ch & 1) != 0)
                    || (pacMoveX == 1 && pacMoveY == 0 && (ch & 4) != 0)
                    || (pacMoveX == 0 && pacMoveY == -1 && (ch & 2) != 0)
                    || (pacMoveX == 0 && pacMoveY == 1 && (ch & 8) != 0)) {
                pacMoveX = 0;
                pacMoveY = 0;
            }
        }
        // change pacmans position according to move direction and speed
        pacX = pacX + PACSPEED * pacMoveX;
        pacY = pacY + PACSPEED * pacMoveY;
    }
    // use if else to check for pacman's direction and draw him in that direction
    private void drawPacman(Graphics2D g2d) {
        if (viewX == -1) {
            drawPacmanLeft(g2d);
        } else if (viewX == 1) {
            drawPacmanRight(g2d);
        } else if (viewY == -1) {
            drawPacmanUp(g2d);
        } else {
            drawPacmanDown(g2d);
        }
    }
    private void drawPacmanUp(Graphics2D g2d) {
        switch (animatePosition) {
            case 1:
                g2d.drawImage(pacup1, pacX, pacY, this);
                break;
            case 2:
                g2d.drawImage(pacup2, pacX, pacY, this);
                break;
            case 3:
                g2d.drawImage(pacup3, pacX, pacY, this);
                break;
            default:
                g2d.drawImage(pacman, pacX, pacY, this);
                break;
        }
    }
    private void drawPacmanDown(Graphics2D g2d) {
        switch (animatePosition) {
            case 1:
                g2d.drawImage(pacdown1, pacX, pacY, this);
                break;
            case 2:
                g2d.drawImage(pacdown2, pacX, pacY, this);
                break;
            case 3:
                g2d.drawImage(pacdown3, pacX, pacY, this);
                break;
            default:
                g2d.drawImage(pacman, pacX, pacY, this);
                break;
        }
    }
    private void drawPacmanLeft(Graphics2D g2d) {
        switch (animatePosition) {
            case 1:
                g2d.drawImage(pacleft1, pacX, pacY, this);
                break;
            case 2:
                g2d.drawImage(pacleft2, pacX, pacY, this);
                break;
            case 3:
                g2d.drawImage(pacleft3, pacX, pacY, this);
                break;
            default:
                g2d.drawImage(pacman, pacX, pacY, this);
                break;
        }
    }

    private void drawPacmanRight(Graphics2D g2d) {
        switch (animatePosition) {
            case 1:
                g2d.drawImage(pacright1, pacX, pacY, this);
                break;
            case 2:
                g2d.drawImage(pacright2, pacX, pacY, this);
                break;
            case 3:
                g2d.drawImage(pacright3, pacX, pacY, this);
                break;
            default:
                g2d.drawImage(pacman, pacX, pacY, this);
                break;
        }
    }

    // draw the maze
    private void drawMaze(Graphics2D g2d) {
        int i = 0;
        int x, y;
        for (y = 0; y < BOARD_SIZE; y += SQUARE_SIZE) {
            for (x = 0; x < BOARD_SIZE; x += SQUARE_SIZE) {
                g2d.setColor(mazeColor);
                g2d.setStroke(new BasicStroke(2));
                if ((levelData[i] & 1) != 0) {  // draw barriers
                    g2d.drawLine(x, y, x, y + SQUARE_SIZE - 1);
                }
                if ((levelData[i] & 2) != 0) { 
                    g2d.drawLine(x, y, x + SQUARE_SIZE - 1, y);
                }
                if ((levelData[i] & 4) != 0) { 
                    g2d.drawLine(x + SQUARE_SIZE - 1, y, x + SQUARE_SIZE - 1,
                            y + SQUARE_SIZE - 1);
                }
                if ((levelData[i] & 8) != 0) { 
                    g2d.drawLine(x, y + SQUARE_SIZE - 1, x + SQUARE_SIZE - 1,
                            y + SQUARE_SIZE - 1);
                }
                if ((levelData[i] & 16) != 0) { // regular pellets
                    g2d.setColor(dotColor);
                    g2d.fillRect(x + 22, y + 22, 4, 4);
                }
                if ((levelData[i] & 32) != 0) { // power pellets
                    g2d.setColor(dotColor);
                    g2d.fillRect(x + 15, y + 15, 16, 16);
                }
                if ((levelData[i] & 64) != 0) { // power pellets
                	g2d.drawImage(cherry, x, y, this);
                }
                i++;
            }
        }
        
    }

    // initialize game values
    private void initGame() {
    	currentSpeed = 1; // start at slow speed
        livesLeft = 4;
        score = 0;
        level = 1;
        initLevel(levelNum);
        
    }
    
    // set level maze and rotate through level designs
    private void initLevel(int levelNum) {
        int i;
        switch (levelNum) {
        case (0) :
        	for (i = 0; i < BLOCKS_N * BLOCKS_N; i++) {
                levelData[i] = mazeData[i];
            }
        	break;
        case(1) :
        	for (i = 0; i < BLOCKS_N * BLOCKS_N; i++) {
                levelData[i] = mazeData2[i];
            }
        	break;
        case(2) :
        	for (i = 0; i < BLOCKS_N * BLOCKS_N; i++) {
                levelData[i] = mazeData3[i];
            }
        	break;
        default :
        	for (i = 0; i < BLOCKS_N * BLOCKS_N; i++) {
                levelData[i] = mazeData4[i];
            }
        	break;
        }
        ghostRecovering = false;
        ghostScared = false;
        scatter.start();
        ghostScatter = true;
        continueLevel();
    }

    // lose life and continue level from previous state
    private void continueLevel() {
        int i;
        int moveX = 1;
        int random;
        pacX = 7 * SQUARE_SIZE; // respawn pacman
        pacY = 11 * SQUARE_SIZE;
        pacMoveX = 0;
        pacMoveY = 0;
        requestX = 0;
        requestY = 0;
        viewX = -1;
        viewY = 0;
        lifeLost = false;
        for (i = 0; i < 4; i++) { // respawn ghosts
            enemyY[i] = 4 * SQUARE_SIZE;
            enemyX[i] = 4 * SQUARE_SIZE;
            enemyMoveY[i] = 0;
            enemyMoveX[i] = moveX;
            moveX = -moveX;
            random = (int) (Math.random() * (currentSpeed + 1));
            if (random > currentSpeed) {
                random = currentSpeed;
            }
            ghostSpeed[i] = validSpeeds[random];
        }
       
        
    }

    
    // play sound effect with no pause
    public static void playSound (File sound) {
    	try {
    		clip = AudioSystem.getClip();
    		clip.open(AudioSystem.getAudioInputStream(sound));
    		clip.start();
    	}
    	catch (Exception e) {
    		
    	}
    }
    // play intro music with pause
    public static void playMusic (File sound) {
    	try {
    		clip = AudioSystem.getClip();
    		clip.open(AudioSystem.getAudioInputStream(sound));
    		clip.start();
    		Thread.sleep(clip.getMicrosecondLength()/1000);
    	}
    	catch (Exception e) {
    		
    	}
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, d.width, d.height);
        drawMaze(g2d);
        drawScore(g2d);
        doAnim();
        if (gameRunning) {
            playGame(g2d);
        } else {
            showIntroScreen(g2d);
        }
        g2d.drawImage(ii, 5, 5, this);
        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }
    
    
    // game controls
    class Controls extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (gameRunning) {
            	// arrow keys to control pacman
                if (key == KeyEvent.VK_LEFT) {
                    requestX = -1;
                    requestY = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                    requestX = 1;
                    requestY = 0;
                } else if (key == KeyEvent.VK_UP) {
                    requestX = 0;
                    requestY = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    requestX = 0;
                    requestY = 1;
                   // exit current game
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                    gameRunning = false;
                } 
                // pause button
                else if (key == KeyEvent.VK_P) {
                
                    if (timer.isRunning()) {
                        timer.stop();
                    } else {
                        timer.start();
                    }
                }
                // toggle sound
                else if (key == KeyEvent.VK_M) {
                	if (sound)
                		sound = false;
                	else
                		sound = true;
                }
            } else {
                if (key == KeyEvent.VK_ENTER) {
                    gameRunning = true;
                    
                    initGame();
                    if(sound)
                    	playMusic(beginning);
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == Event.LEFT || key == Event.RIGHT
                    || key == Event.UP || key == Event.DOWN) {
                requestX = 0;
                requestY = 0;
            }
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}