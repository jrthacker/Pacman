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
    private final Font smallFont = new Font("Times New Roman", Font.BOLD, 24);

    private Image ii;
    private final Color dotColor = new Color(192, 192, 0); // yellow color
    private Color mazeColor;

    private boolean inGame = false;
    private boolean lifeLost = false;
    private boolean ghostScatter = false;
    private boolean ghostScared = false;
    private boolean ghostRecovering = false;

    private final int BLOCK_SIZE = 48;
    private final int BLOCKS_N = 15;
    private final int SCREEN_SIZE = BLOCKS_N * BLOCK_SIZE;
    private final int PAC_ANIM_DELAY = 2;
    private final int PACMAN_ANIM_COUNT = 4;
    private final int PACSPEED = 8;

    private int pacAnimCount = PAC_ANIM_DELAY;
    private int pacAnimDir = 1;
    private int pacmanAnimPos = 0;
    private int ghosts = 4;
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
    private int currentLevel = 0;
    
    // Constructor for the Board, load images and initialize vars and board state
    public Board() {
        loadImages();
        initBoard();
        initVariables();
        
    }
    
    // adds controls to the board and sets background
    private void initBoard() {
        addKeyListener(new Controls());
        setFocusable(true);
        setBackground(Color.black);
    }

    // provides initial values for a new game
    private void initVariables() {
        levelData = new int[BLOCKS_N * BLOCKS_N];
        mazeColor = new Color(5, 5, 200);   // blue
        d = new Dimension(1600, 1600);
        enemyX = new int[ghosts];
        enemyMoveX = new int[ghosts];
        enemyY = new int[ghosts];
        enemyMoveY = new int[ghosts];
        ghostSpeed = new int[ghosts];
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
                        enemyX[i] = 4 * BLOCK_SIZE;
                        enemyY[i] = 4 * BLOCK_SIZE;
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

    // animate pacman
    private void doAnim() {
        pacAnimCount--;
        if (pacAnimCount <= 0) {
            pacAnimCount = PAC_ANIM_DELAY;
            pacmanAnimPos = pacmanAnimPos + pacAnimDir;

            if (pacmanAnimPos == (PACMAN_ANIM_COUNT - 1) || pacmanAnimPos == 0) {
                pacAnimDir = -pacAnimDir;
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
    
    // show the introduction screen and exit controls
    private void showIntroScreen(Graphics2D g2d) {
        g2d.setColor(new Color(0, 32, 48));
        g2d.fillRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50 +  BLOCK_SIZE);
        g2d.setColor(Color.white);
        g2d.drawRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50 + BLOCK_SIZE);
        String s = "Press Enter to start and ESC to exit";
        String s2 = "Press P to Pause and M to Mute";
        Font small = new Font("Times New Roman", Font.BOLD, 24);
        FontMetrics metr = this.getFontMetrics(small);
        g2d.setColor(Color.white);
        g2d.setFont(small);
        g2d.drawString(s, (SCREEN_SIZE - metr.stringWidth(s)) / 2, SCREEN_SIZE / 2);
        g2d.drawString(s2, (SCREEN_SIZE - metr.stringWidth(s2)) / 2, SCREEN_SIZE / 2 + BLOCK_SIZE);
    }

    // draw the score at the bottom of the screen
    private void drawScore(Graphics2D g) {
        int i;
        String s;
        g.setFont(smallFont);
        g.setColor(new Color(96, 128, 255));
        s = "Level: " + level + "        Score: " + score;
        g.drawString(s, 20, SCREEN_SIZE + 16);
        for (i = 0; i < livesLeft; i++) {
            g.drawImage(pacleft2, (i * 56 + 16) + SCREEN_SIZE/2, SCREEN_SIZE + 1, this);
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
            currentLevel++;
            if (currentLevel == 4)
            	currentLevel = 0;
            initLevel(currentLevel);
        }
    }
    // if pacman collides with a ghost, reduce number of lives and if 0 then game over
    private void death() {
        livesLeft--;
        if (livesLeft == 0) {
            inGame = false;
        }
        continueLevel(); // pacman respawns after losing life
    }
    // ghost movement behavior
    private void moveGhosts(Graphics2D g2d) {
        int i;
        int pos;
        int count;
        for (i = 0; i < ghosts; i++) {
            if (enemyX[i] % BLOCK_SIZE == 0 && enemyY[i] % BLOCK_SIZE == 0) {
                pos = enemyX[i] / BLOCK_SIZE + BLOCKS_N * (int) (enemyY[i] / BLOCK_SIZE);
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
                        if ((Math.pow((enemyX[i] - pacX), 2) + Math.pow((enemyY[i] - pacY), 2)) > (64 * Math.pow(BLOCK_SIZE, 2))) {
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
                        && inGame) {
                    score += 100;
                    enemyX[i] = 2 * BLOCK_SIZE;
                    enemyY[i] = 2 * BLOCK_SIZE;
                    ghostSpeed[i] = 0;
                    if(sound)
                    	playSound(eatGhost);
                }
            } 
            // else if pacman gets too close, then pacman loses a life and respawns
            else {
                if (pacX > (enemyX[i] - 20) && pacX < (enemyX[i] + 20)
                        && pacY > (enemyY[i] - 20) && pacY < (enemyY[i] + 20)
                        && inGame) {
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
        if (pacX % BLOCK_SIZE == 0 && pacY % BLOCK_SIZE == 0) {
            pos = pacX / BLOCK_SIZE + BLOCKS_N * (int) (pacY / BLOCK_SIZE);
            ch = levelData[pos];
            if ((ch & 16) != 0) { // score points for eating pellets
                levelData[pos] = (int) (ch & 15);
                if (chompplaying)
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
                if (chompplaying)
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
        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacup1, pacX + 1, pacY + 1, this);
                break;
            case 2:
                g2d.drawImage(pacup2, pacX + 1, pacY + 1, this);
                break;
            case 3:
                g2d.drawImage(pacup3, pacX + 1, pacY + 1, this);
                break;
            default:
                g2d.drawImage(pacman, pacX + 1, pacY + 1, this);
                break;
        }
    }
    private void drawPacmanDown(Graphics2D g2d) {
        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacdown1, pacX + 1, pacY + 1, this);
                break;
            case 2:
                g2d.drawImage(pacdown2, pacX + 1, pacY + 1, this);
                break;
            case 3:
                g2d.drawImage(pacdown3, pacX + 1, pacY + 1, this);
                break;
            default:
                g2d.drawImage(pacman, pacX + 1, pacY + 1, this);
                break;
        }
    }
    private void drawPacmanLeft(Graphics2D g2d) {
        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacleft1, pacX + 1, pacY + 1, this);
                break;
            case 2:
                g2d.drawImage(pacleft2, pacX + 1, pacY + 1, this);
                break;
            case 3:
                g2d.drawImage(pacleft3, pacX + 1, pacY + 1, this);
                break;
            default:
                g2d.drawImage(pacman, pacX + 1, pacY + 1, this);
                break;
        }
    }

    private void drawPacmanRight(Graphics2D g2d) {
        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacright1, pacX + 1, pacY + 1, this);
                break;
            case 2:
                g2d.drawImage(pacright2, pacX + 1, pacY + 1, this);
                break;
            case 3:
                g2d.drawImage(pacright3, pacX + 1, pacY + 1, this);
                break;
            default:
                g2d.drawImage(pacman, pacX + 1, pacY + 1, this);
                break;
        }
    }

    // draw the maze
    private void drawMaze(Graphics2D g2d) {
        int i = 0;
        int x, y;
        for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {
                g2d.setColor(mazeColor);
                g2d.setStroke(new BasicStroke(2));
                if ((levelData[i] & 1) != 0) {  // draw barriers
                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
                }
                if ((levelData[i] & 2) != 0) { 
                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
                }
                if ((levelData[i] & 4) != 0) { 
                    g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }
                if ((levelData[i] & 8) != 0) { 
                    g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
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
        livesLeft = 4;
        score = 0;
        level = 1;
        initLevel(currentLevel);
        ghosts = 4;
        currentSpeed = 1;
    }
    
    // set level maze and rotate through level designs
    private void initLevel(int currentLevel) {
        int i;
        switch (currentLevel) {
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
        for (i = 0; i < ghosts; i++) { // respawn ghosts
            enemyY[i] = 4 * BLOCK_SIZE;
            enemyX[i] = 4 * BLOCK_SIZE;
            enemyMoveY[i] = 0;
            enemyMoveX[i] = moveX;
            moveX = -moveX;
            random = (int) (Math.random() * (currentSpeed + 1));
            if (random > currentSpeed) {
                random = currentSpeed;
            }
            ghostSpeed[i] = validSpeeds[random];
        }
        pacX = 7 * BLOCK_SIZE; // respawn pacman
        pacY = 11 * BLOCK_SIZE;
        pacMoveX = 0;
        pacMoveY = 0;
        requestX = 0;
        requestY = 0;
        viewX = -1;
        viewY = 0;
        lifeLost = false;
        
    }

    // initialize sprites and sounds
    private void loadImages() {
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
        if (inGame) {
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
            if (inGame) {
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
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                    inGame = false;
                } 

                else if (key == KeyEvent.VK_P) {
                
                    if (timer.isRunning()) {
                        timer.stop();
                    } else {
                        timer.start();
                    }
                }
                else if (key == KeyEvent.VK_M) {
                	if (sound)
                		sound = false;
                	else
                		sound = true;
                }
            } else {
                if (key == KeyEvent.VK_ENTER) {
                    inGame = true;
                    
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