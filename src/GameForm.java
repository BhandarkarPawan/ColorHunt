import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Random;

public class GameForm extends  JFrame implements ActionListener {
    private JPanel root;
    private JButton skipButton;
    private JButton quitButton;
    private JComboBox difficultyCB;
    private JPanel GridPanel;
    private JPanel TopPanel;
    private JButton Target;
    private JPanel BottomPanel;
    private JLabel ScoreLabel;
    private JLabel TimeLabel;
    Integer[][] RandomMatrix;
    Color[][] ColorMatrix;

    ArrayList<JButton> buttons;
    HashMap<String, Color> colors;
    String[] colorsIndex;
    Random random;
    String currentDifficulty;
    Timer timer;
    int timeLeft;
    int winX, winY, score;


    GameForm() {
        timeLeft = 10;
        score = 0;

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(timeLeft>=0){
                TimeLabel.setText("TIME LEFT: " + timeLeft-- );
                }
                else
                {
                    Timer t = (Timer) e.getSource();
                    t.stop();
                    score = 0;
                    ScoreLabel.setText("SCORE: "+ score);

                    JOptionPane.showMessageDialog(GameForm.this, "You ran out of time. You lost.");
                }
            }
        });

        currentDifficulty = "Easy";

        colors = new HashMap<>();

        colors.put("red",new Color(255,0,0));
        colors.put("green",new Color(0,255,0));
        colors.put("blue",new Color(0,0,255));
        colors.put("magenta",new Color(255,0,255));
        colors.put("yellow",new Color(255,255,0));
        colors.put("cyan",new Color(0,255,255));
        colors.put("white",new Color(255,255,255));


        colorsIndex = new String[7];

        colorsIndex[0] = "magenta";
        colorsIndex[1] = "yellow";
        colorsIndex[2] = "cyan";
        colorsIndex[3] = "red";
        colorsIndex[4] = "green";
        colorsIndex[5] = "blue";
        colorsIndex[6] = "white";


        random = new Random();

        RandomMatrix = new Integer[10][10];
        ColorMatrix = new Color[10][10];

        Target = new JButton();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(root);

        ScoreLabel = new JLabel("SCORE: "+ score);

        TimeLabel = new JLabel("TIMER");


        TopPanel = new JPanel(new FlowLayout());

        root.add(TopPanel, BorderLayout.NORTH);

        ScoreLabel.setBorder(new EmptyBorder(0, 10, 0, 175));
        TimeLabel.setBorder(new EmptyBorder(0, 120, 0, 0));

        TopPanel.add(ScoreLabel);
        TopPanel.add(Target);
        TopPanel.add(TimeLabel);

        BottomPanel.setPreferredSize(new Dimension(30,30));

        TopPanel.setLayout(new BoxLayout(TopPanel, BoxLayout.LINE_AXIS));

        buttons = new ArrayList<>();
        GridPanel = new JPanel();

        loadGrid();



        skipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                score -=3;
                ScoreLabel.setText("SCORE: "+ score);
                loadGrid();
            }
        });



        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispatchEvent(new WindowEvent(GameForm.this, WindowEvent.WINDOW_CLOSING));

            }
        });

        difficultyCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GridPanel.setVisible(false);
                currentDifficulty = (String)difficultyCB.getSelectedItem();
                System.out.println("Current Diff: " +currentDifficulty);
                timer.stop();
                int choice = JOptionPane.showConfirmDialog(
                        GameForm.this,
                        "Are you sure? Your current progress will be lost.",
                        "Difficulty change",
                        JOptionPane.YES_NO_OPTION);
                System.out.println("N: " + choice);

                if(choice == 0){
                    timer.start();
                    score = 0;
                    ScoreLabel.setText("SC  ORE: "+ score);
                    GridPanel.setVisible(true);
                    loadGrid();
                }
                else
                {
                    GridPanel.setVisible(true);
                    timer.start();
                }

            }
        });

        root.add(GridPanel, BorderLayout.CENTER);
        GridPanel.setLayout(new GridLayout(10,10));
        GridPanel.setSize(1000,1000);

        int count = 0;

        String levels[]={"Level1","Level2","Level3"};
        JComboBox jcb=new JComboBox<String>(levels);
        jcb.addActionListener(this);

        setSize(500, 600);
        setResizable(false);
        setVisible(true);

    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                new GameForm();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public void loadGrid(){
        int numColors = 3;

        winX = random.nextInt(10);
        winY = random.nextInt(10);
        Color winColor = colors.get(colorsIndex[random.nextInt(numColors)]);
        int winInt = random.nextInt(10);
      //  System.out.println("Win: "+ winX + " " + winY);
        

        switch (currentDifficulty){
            case "Easy": numColors = 3; timeLeft = 15; break;
            case "Medium": numColors = 5; timeLeft = 7;  break;
            case "Hard": numColors = 7; timeLeft= 3; break;
        }

        timer.restart();

        Target.setText(" "+ winInt+ " ");
        Target.setBackground(winColor);
        Target.setBorder(new EmptyBorder(10,10,10,10));
        Target.setContentAreaFilled(false);
        Target.setOpaque(true);
        Target.setPreferredSize(new Dimension(50,50));
        Target.setBorder(BorderFactory.createRaisedBevelBorder());
        Target.setBorder(new EmptyBorder(15,15,15,15));
        buttons.removeAll(buttons);

        GridPanel.removeAll();
        for(int i =0 ;i<10;i++){
            for(int j=0;j<10;j++){

                while(true){

                    int RandInt = random.nextInt(10);
                    Color RandColor = colors.get(colorsIndex[random.nextInt(numColors)]);

                    if(RandInt!=winInt || RandColor!=winColor ){
                        RandomMatrix[i][j] = RandInt;
                        ColorMatrix[i][j] =  RandColor;
                        break;
                    }

                }

                RandomMatrix[winX][winY] = winInt;
                ColorMatrix[winX][winY] = winColor;

                JButton b = (new JButton("" + RandomMatrix[i][j]));
                b.setBackground(ColorMatrix[i][j]);
              //  b.setForeground(new Color(255,255,255));
                b.setBorder(new EmptyBorder(10,10,10,10));
                b.setContentAreaFilled(false);
                b.setOpaque(true);
                b.setBorder(BorderFactory.createEtchedBorder());

                buttons.add(b);
                GridPanel.add(b);
                b.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        int selectedIndex = buttons.indexOf(e.getSource());

                        int selectedX = selectedIndex/10;
                        int selectedY = selectedIndex%10;
                        //System.out.println("Selected: "+ selectedX + " " + selectedY);


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
        GridPanel.repaint();
        GridPanel.revalidate();
    }
}
