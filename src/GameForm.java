import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GameForm extends  JFrame {
    // Swing components
    private JPanel root;
    private JButton skipButton;
    private JButton quitButton;
    private JComboBox difficultyCB;
    private JPanel GridPanel;
    private JButton Target;
    private JPanel BottomPanel;
    private JLabel ScoreLabel;
    private JLabel TimeLabel;
    private Timer timer;
    private ArrayList<JButton> buttons;

    // Variables required
    private Integer[][] RandomMatrix;
    private Color[][] ColorMatrix;
    private HashMap<String, Color> colors;
    private String[] colorsIndex;
    private Random random;
    private String currentDifficulty;
    private int timeLeft;
    private int winX;
    private int winY;
    private int score;


    private GameForm() {

        timeLeft = 10;  // keeps track of how much time is left per round
        score = 0;      // keeps track of user score | 20 = win
        currentDifficulty = "Easy";  // keeps track of the difficulty level


        /*
          A "Timer" is an class in the Swing API that is used a perform a certain action
          for every 'n' milliseconds, where n is passed as the delay to the constructor
          (n = 1000 below). The constructor also takes an ActionListener as its argument
          and the actionPerformed() method of this ActionListener will contain the code that
          runs at every 'n' interval. So here, we decrement the timeLeft for every 1 second.
         */

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(timeLeft>=0){
                    // There is still some time left so decrement and update the label
                    TimeLabel.setText("TIME LEFT: " + timeLeft-- );
                }

                else
                {
                    // Timer ran out so show the message and stop the timer.
                    Timer t = (Timer) e.getSource(); // Reference to the current timer
                    t.stop();

                    // Set score to 0 and display (according to game rules)
                    score = 0;
                    ScoreLabel.setText("SCORE: "+ score);

                    JOptionPane.showMessageDialog(GameForm.this, "You ran out of time. You lost.");
                }
            }
        });


        /*
          This HashMap will translate the color names to the corresponding Color Objects
          The reasoning behind this is that we need a way to quickly fetch colors for the buttons
          when we are populating the grid and  HashMap is very convenient to use.
          */

        colors = new HashMap<>();

        colors.put("red",new Color(255,0,0));
        colors.put("green",new Color(0,255,0));
        colors.put("blue",new Color(0,0,255));
        colors.put("magenta",new Color(255,0,255));
        colors.put("yellow",new Color(255,255,0));
        colors.put("cyan",new Color(0,255,255));
        colors.put("white",new Color(255,255,255));

        // As an alternative to the above code, we can use the static variables of the Color class

//        colors.put("red",Color.RED);
//        colors.put("green",Color.GREEN);
//        colors.put("blue",Color.BLUE);
//        colors.put("magenta",Color.MAGENTA);
//        colors.put("yellow",Color.YELLOW);
//        colors.put("cyan",Color.CYAN);
//        colors.put("white",Color.WHITE);


        /*
          There's a small problem to using HashMaps. It's that we can't randomly choose a key
          ( or maybe we can, I just didn't look into it. Feel free to check it out )
          A simple workaround is to create an array (since indices can be randomly generated)
          We can then use random.nextInt(arraySize) to get a random index for that array.
          By using this array to store Color names, we are effectively generating random color names
         */

        colorsIndex = new String[7];
        // Note that these names should match the HashMap keys EXACTLY
        colorsIndex[0] = "magenta";
        colorsIndex[1] = "yellow";
        colorsIndex[2] = "cyan";
        colorsIndex[3] = "red";
        colorsIndex[4] = "green";
        colorsIndex[5] = "blue";
        colorsIndex[6] = "white";

        // Initialize a random object
        random = new Random();

        // These two matrices are important since they will govern what appears on the 10x10 grid
        RandomMatrix = new Integer[10][10];  // random labels for the buttons between 0 and 9
        ColorMatrix = new Color[10][10];     // random matrix of colors

        Target = new JButton();              // this is the target button to be found in the grid

        ScoreLabel = new JLabel("SCORE: "+ score); // displays the score
        TimeLabel = new JLabel("TIMER");           // displays the time left

        JPanel topPanel = new JPanel();                // holds the top row of labels
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS)); // google "BoxLayout"

        root.add(topPanel, BorderLayout.NORTH);         // google "BorderLayout"

        // This is just to make the UI look nice and can be ignored
        ScoreLabel.setBorder(new EmptyBorder(0, 10, 0, 175));
        TimeLabel.setBorder(new EmptyBorder(0, 120, 0, 0));

        // Self-explanatory
        topPanel.add(ScoreLabel);
        topPanel.add(Target);
        topPanel.add(TimeLabel);

        // Bottom panel hold the buttons and the difficulty menu
        BottomPanel.setPreferredSize(new Dimension(30,30));


        /*
          Although we have two matrices that define each button, there's a problem.
          We don't have any way to GET THE INDEX of a particular button within the grid.
          Although there maybe other ways to do this, a simple workaround is to use an
          ArrayList that keeps track of all the buttons that exist within the Panel.
          That way, once a button is clicked, we can simply find the position of the clicked
          button with this ArrayList.

          Let the position be "P"
          then, the X and Y coordinates of the button within the Matrices will be:
               X = P/10;
               Y = P%10;
         */

        buttons = new ArrayList<>();
        GridPanel = new JPanel(); // Main Grid which holds the buttons

        loadGrid(); // Most important function that does most of the work.


        skipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Each skip imposes a 3-point penalty (according to game rules)
                score -=3;
                //TODO: Add code here to either disallow skips that lead to a negative score
                // OR
                // TODO: Add code that reports defeat/game over when score falls below 0
                ScoreLabel.setText("SCORE: "+ score);
                loadGrid();
            }
        });


        quitButton.addActionListener(new ActionListener() {
            // Close the window and quit the game
            @Override
            public void actionPerformed(ActionEvent e) {
                // Got this code from the internet. Don't worry too much about it
                dispatchEvent(new WindowEvent(GameForm.this, WindowEvent.WINDOW_CLOSING));

            }
        });

        difficultyCB.addActionListener(new ActionListener() {
            // This is the JComboBox which hold the difficulty modes
            @Override
            public void actionPerformed(ActionEvent e) {
                // When user is about to change difficulty, hide the grid to prevent cheating
                GridPanel.setVisible(false);

                currentDifficulty = (String)difficultyCB.getSelectedItem();
                // System.out.println("Current Diff: " +currentDifficulty);
                timer.stop(); // pause the timer (stop and pause mean the same in a Timer)

                /**
                 * This is basically messageDialog with "yes" and "no" options. We will use it
                 * to warn the use that the score gets reset we ask for confirmation.
                 * Choosing YES returns 0
                 * Choosing  NO returns 1
                 *
                 * Refer this link for further information:
                 * @https://www.mkyong.com/swing/java-swing-how-to-make-a-confirmation-dialog/
                 */
                int choice = JOptionPane.showConfirmDialog(
                        GameForm.this,
                        "Are you sure? Your current progress will be lost.",
                        "Difficulty change",
                        JOptionPane.YES_NO_OPTION);

                if(choice == 0){
                    // Use chose to switch difficulty. Restart the timer and reset score
                    timer.start();
                    score = 0;

                    ScoreLabel.setText("SCORE: "+ score);
                    GridPanel.setVisible(true);     // Bring back the grid
                    // Reload the grid ( the function detects the difficulty and works accordingly)
                    loadGrid();
                }
                else
                {
                    // User chose to stay on the same difficulty, so resume the game
                    GridPanel.setVisible(true);
                    timer.start();
                }
            }
        });

        root.add(GridPanel, BorderLayout.CENTER);
        GridPanel.setLayout(new GridLayout(10,10)); // google "GridLayout"
        GridPanel.setSize(1000,1000);

        setContentPane(root);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 600);
        setResizable(false);
        setVisible(true);

    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Look and Feel will make your app UI match the UI of the Operating System
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                new GameForm();     // Let's play!
            }
        });
    }


    /**
     *  loadGrid() is the most important function of this app.
     *
     *  Overview of what it does:
     *      1) Generate a random target button.
     *      2) Generate a random grid based on difficulty but do not allow the target to appear.
     *      3) Insert the target in a random location in the grid.
     *      4) Reload the Buttons Array to contain the new grid
     *
     *  Implementation details are given as in-line comments
     */
    private void loadGrid(){


        int numColors = 3;  // holds the number of colors to be shown on map (varies with difficulty)

        winX = random.nextInt(10);  // Generate random X coordinate for target
        winY = random.nextInt(10);  // Generate random Y coordinate for target

        // Generate a random colour and a random Label (value) for the target
        Color winColor = colors.get(colorsIndex[random.nextInt(numColors)]);
        int winInt = random.nextInt(10);

        // uncomment the following line if you wish to see target location
        //  System.out.println("Target coordinates: (: "+ winX + "," + winY +")");

        switch (currentDifficulty){
            case "Easy":
                numColors = 3;
                timeLeft = 15;
                break;

            case "Medium":
                numColors = 5;
                timeLeft = 7;
                break;

            case "Hard":
                numColors = 7;
                timeLeft= 3;
                break;
        }

        timer.restart();        // restart the timer since the map is reloaded

        /* NOTE: Here, we are actually just restarting the Timer object and not the timer of our game.
        That timer appears to be reset since we update the timeLeft variable every time loadMap() is called.
        The Timer object simply decrements the timeLeft to give us a "feel" of a countdown.
        */

        // The following statements simply prepare and display the target above the grid
        // Feel free to google the new terminologies that you see down here, if interested.
        Target.setText(" "+ winInt+ " ");
        Target.setBackground(winColor);
        Target.setBorder(new EmptyBorder(10,10,10,10));
        Target.setContentAreaFilled(false);
        Target.setOpaque(true);
        Target.setPreferredSize(new Dimension(50,50));
        Target.setBorder(BorderFactory.createRaisedBevelBorder());
        Target.setBorder(new EmptyBorder(15,15,15,15));

        // Empty the buttons List
        buttons.clear();


        GridPanel.removeAll();  // Remove all the buttons and prepare for new ones
        for(int i =0 ;i<10;i++){
            for(int j=0;j<10;j++){
                while(true){

                    // For each of the 100 buttons, generate random colors and labels
                    int RandInt = random.nextInt(10);
                    Color RandColor = colors.get(colorsIndex[random.nextInt(numColors)]);

                    /* Make sure the newly generated button does NOT resemble the Target
                   This is because we want to make sure there is ONLY ONE target in the grid
                   This is why the while loop is used here. */

                    if(RandInt!=winInt || RandColor!=winColor ){

                        // If either the color or the label is different from the target, set the button
                        RandomMatrix[i][j] = RandInt;
                        ColorMatrix[i][j] =  RandColor;
                        break;
                    }
                }

                // Now that the Grid is randomly populated, replace the button at position (winX,winY) with target

                RandomMatrix[winX][winY] = winInt;
                ColorMatrix[winX][winY] = winColor;

                // Same as previously encountered code
                JButton b = (new JButton("" + RandomMatrix[i][j]));
                b.setBackground(ColorMatrix[i][j]);
                b.setBorder(new EmptyBorder(10,10,10,10));
                b.setContentAreaFilled(false);
                b.setOpaque(true);
                b.setBorder(BorderFactory.createEtchedBorder());

                // This is the part where we add action Listeners to each button on the grid
                buttons.add(b);     // Add the new button to the list
                GridPanel.add(b);   // Add it to the grid

                // This is the part where we add action Listeners to each button on the grid
                b.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        JButton clicked = (JButton)e.getSource();
                        int selectedIndex = buttons.indexOf(clicked);

                        // Fetch the coordinates of the clicked button
                        int selectedX = selectedIndex/10;
                        int selectedY = selectedIndex%10;

                        // Self-Explanatory code
                        if(selectedX == winX && selectedY == winY){
                            score++;
                            ScoreLabel.setText("SCORE: "+ score);
                            loadGrid();
                            if(score == 20){
                                JOptionPane.showMessageDialog(GameForm.this, "You a score of 20. You win!");
                                score = 0;
                                ScoreLabel.setText("SCORE: "+ score);
                            }
                        }

                    }
                });
            }
        }

        // These two statements are required to reload the grid and make the changes visible
        GridPanel.repaint();
        GridPanel.revalidate();
    }
}
