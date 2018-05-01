/**
 *	@author Ariana Fairbanks
 */

package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.LineBorder;
import adapter.Controller;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Game3 extends JPanel implements KeyListener
{
	private static final long serialVersionUID = 8585306608329719982L;
	private Controller base;
	private SpringLayout theLayout;
	private JLabel shark;
	private Image sharkImg;
	private ImageIcon sharkIcon;
	private Image fishImg;
	private ImageIcon fishIcon;
	private Point defaultSharkLocation;
	private Point sharkLocation;
	private Timer displayTime;
	private ActionListener timeDisplayer;
	private int screenWidth;
	private int screenHeight;
	private int movementSpeed;
	private int sharkHeight;
	private int sharkWidth;
	private int fishWidth;
	private int fishHeight;
	private boolean up;
	private boolean down;
	private boolean right;
	private boolean left;
	private boolean playing;
	private boolean guessed1;
	private boolean guessed2;
	private boolean guessed3;
	private ActionListener screenRefresh;
	private Timer refreshTimer;
	private int frequency;
	private List<String> questionList;
	private JLabel answerLabel1;
	private JLabel answerLabel2;
	private JLabel answerLabel3;
	private JLabel scoreLabel;
	private JLabel timerLabel;
	private String question;
	private int answer;
	private int randomPlacement;
	private int score;
	private static final int GAME_PERIOD = 40;
	private int sec;
	private JLabel rightLabel;
	private JLabel wrongLabel;
	private JLabel feedbackLabel;
	private int questionBase;
	private int questionTypes; // TODO
	private JButton menu;
	private JButton help;
	private boolean needsInstructions;
	private JLabel questionLabel;
	private boolean miss;
	private int count;
	private boolean reset;
	private Image backgroundImg;
	private ImageIcon backgroundIcon;
	private JLabel background;
	private JLabel g3instruct;
	private ArrayList<Integer> answerOptions;
	private Boolean needsHelp;
	
	public Game3(Controller base)
	{
		this.base = base;
		frequency = base.getFrequency();
		questionList = base.getEquations();
		theLayout = new SpringLayout();
		question = "";
		answer = 0;
		score = 0;
		count = 0;
		timerLabel = new JLabel("Time: " + (GAME_PERIOD / 60) + ":" + (GAME_PERIOD % 60));
		scoreLabel = new JLabel("Score: 0");
		screenWidth = base.frame.getWidth();
		screenHeight = base.frame.getHeight();
		defaultSharkLocation = new Point(30, 110);
		sharkLocation = new Point();
		answerLabel1 = new JLabel();
		answerLabel2 = new JLabel();
		answerLabel3 = new JLabel();
		questionLabel = new JLabel(question);
		movementSpeed = 3;
		questionBase = 15;
		questionTypes = 0; // both, addition, subtraction
		menu = new JButton(" Exit ");
		help = new JButton(" Help ");

		setUpImages();
		shark = new JLabel(sharkIcon);
		up = false;
		down = false;
		right = false;
		left = false;
		guessed1 = false;
		guessed2 = false;
		guessed3 = false;
		playing = true;
		needsHelp = false;
		needsInstructions = base.getInstructionPreference("game3Instructions");

		setBackground();
		addKeyListener(this);
		setFocusable(true);
		requestFocus();
		setUpLayout();
		setUpTimers();
		setUpListeners();
		playGame();
	}

	private void setUpLayout()
	{
		setLayout(theLayout);
		setBorder(new LineBorder(new Color(70, 130, 180), 10));
		setBackground(new Color(213, 248, 255));
		setBackground(new Color(208, 243, 255));

		timerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		timerLabel.setForeground(new Color(70, 130, 180));
		timerLabel.setFont(new Font("Arial", Font.PLAIN, 30));
		theLayout.putConstraint(SpringLayout.NORTH, timerLabel, 10, SpringLayout.NORTH, this);
		theLayout.putConstraint(SpringLayout.WEST, timerLabel, 10, SpringLayout.WEST, this);

		scoreLabel.setFont(new Font("Arial", Font.PLAIN, 30));
		scoreLabel.setForeground(new Color(70, 130, 180));
		scoreLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		theLayout.putConstraint(SpringLayout.NORTH, scoreLabel, 0, SpringLayout.NORTH, menu);
		theLayout.putConstraint(SpringLayout.EAST, scoreLabel, -20, SpringLayout.WEST, help);
		
		questionLabel.setHorizontalAlignment(SwingConstants.LEFT);
		questionLabel.setForeground(new Color(70, 130, 180));
		questionLabel.setFont(new Font("Arial", Font.BOLD, 45));
		theLayout.putConstraint(SpringLayout.WEST, questionLabel, 350, SpringLayout.WEST, this);
		theLayout.putConstraint(SpringLayout.SOUTH, questionLabel, -25, SpringLayout.SOUTH, this);
		
		answerLabel1.setFont(new Font("Ariel", Font.PLAIN, 20));
		answerLabel1.setForeground(Color.WHITE);
		answerLabel1.setHorizontalTextPosition(SwingConstants.CENTER);
		answerLabel1.setVerticalTextPosition(SwingConstants.CENTER);
		answerLabel1.setIcon(fishIcon);
		
		answerLabel2.setFont(new Font("Ariel", Font.PLAIN, 20));
		answerLabel2.setForeground(Color.WHITE);
		answerLabel2.setHorizontalTextPosition(SwingConstants.CENTER);
		answerLabel2.setVerticalTextPosition(SwingConstants.CENTER);
		answerLabel2.setIcon(fishIcon);
		
		answerLabel3.setFont(new Font("Ariel", Font.PLAIN, 20));
		answerLabel3.setForeground(Color.WHITE);
		answerLabel3.setHorizontalTextPosition(SwingConstants.CENTER);
		answerLabel3.setVerticalTextPosition(SwingConstants.CENTER);
		answerLabel3.setIcon(fishIcon);

		answerLabel1.setBounds(answerLabel1.getX(), answerLabel1.getY(), 50, 25);
		answerLabel2.setBounds(answerLabel2.getX(), answerLabel2.getY(), 50, 25);
		answerLabel3.setBounds(answerLabel3.getX(), answerLabel3.getY(), 50, 25);
		
		feedbackLabel = new JLabel();
		theLayout.putConstraint(SpringLayout.WEST, feedbackLabel, (base.frame.getWidth()/3), SpringLayout.WEST, this);
		theLayout.putConstraint(SpringLayout.NORTH, feedbackLabel,  (base.frame.getHeight()/3), SpringLayout.NORTH, this);
		feedbackLabel.setForeground(new Color(70, 130, 180));
		feedbackLabel.setBackground(new Color(245, 245, 245));
		feedbackLabel.setFont(new Font("Arial", Font.BOLD, 35));
		
		menu.setFont(new Font("Arial", Font.PLAIN, 30));
		menu.setForeground(new Color(70, 130, 180));
		menu.setBackground(new Color(70, 130, 180));
		menu.setFocusPainted(false);
		menu.setContentAreaFilled(false);
		menu.setBorder(new LineBorder(new Color(135, 206, 250), 2));
		theLayout.putConstraint(SpringLayout.NORTH, menu, 10, SpringLayout.NORTH, this);
		theLayout.putConstraint(SpringLayout.EAST, menu, -10, SpringLayout.EAST, this);
		
		help.setFont(new Font("Arial", Font.PLAIN, 30));
		help.setForeground(new Color(70, 130, 180));
		help.setBackground(new Color(70, 130, 180));
		help.setFocusPainted(false);
		help.setContentAreaFilled(false);
		help.setBorder(new LineBorder(new Color(135, 206, 250), 2));
		theLayout.putConstraint(SpringLayout.NORTH, help, 0, SpringLayout.NORTH, menu);
		theLayout.putConstraint(SpringLayout.EAST, help, -20, SpringLayout.WEST, menu);
		
		g3instruct = new JLabel("<html>To play use the arrow keys to help the <br/> shark swim to the correct fish. <br/> "
				+ "<br/>The game ends when time runs out.</html>");
		g3instruct.setFont(new Font("Arial", Font.PLAIN, 30));
		g3instruct.setForeground(new Color(70, 130, 180));
		g3instruct.setBackground(new Color(208, 243, 255));
		
		add(answerLabel1);
		add(answerLabel2);
		add(answerLabel3);
		add(timerLabel);
		add(scoreLabel);
		add(menu);
		add(help);
		add(questionLabel);
		add(feedbackLabel);
	}
	
	private void setBackground(){
		try
		{
			backgroundImg = ImageIO.read(this.getClass().getResourceAsStream("background.jpg"));
		}
		catch (IOException ex)
		{
			System.out.println("File \"background.jpg\" is missing.");
		}
		backgroundImg = backgroundImg.getScaledInstance(base.frame.getWidth(), base.frame.getHeight(), java.awt.Image.SCALE_SMOOTH);
		backgroundIcon = new ImageIcon(backgroundImg);
		background = new JLabel(backgroundIcon);
	}
	
	private void setUpImages()
	{
		sharkWidth = (base.frame.getWidth() / 5);
		sharkHeight = (base.frame.getHeight() / 5);
		try
		{
			sharkImg = ImageIO.read(this.getClass().getResourceAsStream("shark.png"));
		}
		catch (IOException ex)
		{
			System.out.println("File \"shark.png\" is missing.");
		}

		sharkImg = sharkImg.getScaledInstance(sharkWidth, sharkHeight, java.awt.Image.SCALE_SMOOTH);
		sharkIcon = new ImageIcon(sharkImg);
		fishWidth = (base.frame.getWidth() / 9);
		fishHeight = (base.frame.getHeight() / 8);
		try
		{
			fishImg = ImageIO.read(this.getClass().getResourceAsStream("fish.png"));
		}
		catch (IOException ex)
		{
			System.out.println("File fish.png is missing.");
		}
		fishImg = fishImg.getScaledInstance(fishWidth, fishHeight, java.awt.Image.SCALE_SMOOTH);
		fishIcon = new ImageIcon(fishImg);
	}
	
	private void setUpListeners()
	{
		menu.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent onClick)
			{
				stopTimers();
				int dialogResult = JOptionPane.showConfirmDialog (null, "Your score is " + score + ".  Would you like to exit the game?","Exit game?",JOptionPane.OK_CANCEL_OPTION);
				if(dialogResult == JOptionPane.OK_OPTION){
					//add game record
					remove(shark);
					base.returnToMenu();
				}
				else{
					startTimers();
					playing = true;
				}
			}
		});
		help.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent onClick)
			{
				needsHelp = true;
			}
		});
	}

	private void setUpTimers()
	{
		sec = GAME_PERIOD - 1;
		timeDisplayer = new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if(needsInstructions){
					showInstructions();
					needsInstructions = false;
				}
				if(needsHelp){
					JOptionPane.showMessageDialog(base.messagePanel, g3instruct, "Instructions",JOptionPane.INFORMATION_MESSAGE);
					needsHelp = false;
				}
				if ((sec % 60) < 10)
				{
					timerLabel.setText("Time: " + (sec / 60) + ":0" + (sec % 60));
				}
				else
				{
					timerLabel.setText("Time: " + (sec / 60) + ":" + (sec % 60));
				}
				if(reset){
					playGame();
				}
				if(!playing)
				{
					reset = true;
				}
				if (sec == 0)
				{
					stopTimers();
					playing = false;
					System.out.println("Time's up!");
					JOptionPane.showMessageDialog(base.messagePanel, "Your score was " + score + ".", "Time's up!", JOptionPane.PLAIN_MESSAGE);
					remove(shark);
					base.returnToMenu();
				}
				sec--;
			}
		};
		displayTime = new Timer(1000, timeDisplayer);
		displayTime.start();
		displayTime.setRepeats(true);

		screenRefresh = new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				if (playing && !needsInstructions && !needsHelp)
				{
					if(miss){
						if(count >= 25){
							feedbackLabel.setText("");	
							repaint();
							count = 0;
							miss = false;
						}
						count ++;
					}
					else{
						moveShark();
						repaint();
					}
				}
			}
		};
		refreshTimer = new Timer(20, screenRefresh);
		refreshTimer.start();
		refreshTimer.setRepeats(true);
	}

	private void playGame()
	{
		getQuestion();
		feedbackLabel.setText("");
		addShark();
		questionLabel.setText(question);
		repaint();
		guessed1 = false;
		guessed2 = false;
		guessed3 = false;
		answerOptions = new ArrayList<Integer>();
		randomPlacement = Controller.rng.nextInt(3);
		for (int i = 0; i < 3; i++)
		{
			int randomAnswer = Controller.rng.nextInt(25);
			while (randomAnswer == answer || answerOptions.contains(randomAnswer))
			{
				randomAnswer = Controller.rng.nextInt(25);
			}
			if (i == randomPlacement)
			{
				randomAnswer = answer;
			}
			answerOptions.add(randomAnswer);
			switch (i)
			{
				case 0:
					answerLabel1.setText(""+randomAnswer);
					answerLabel1.setIcon(fishIcon);
					break;
				case 1:
					answerLabel2.setText("" + randomAnswer);
					answerLabel2.setIcon(fishIcon);
					break;
				case 2:
					answerLabel3.setText("" + randomAnswer);
					answerLabel3.setIcon(fishIcon);
					break;
			}
		}
		add(background);
		playing = true;
		reset = false;
	}

	private void moveShark()
	{
		if (up)
		{
			if (sharkLocation.y > 0)
			{
				sharkLocation.y -= movementSpeed;
			}
			checkAnswer();
		}
		if (down)
		{
			if (sharkLocation.y < screenHeight - sharkHeight)
			{
				sharkLocation.y += movementSpeed;
			}
			checkAnswer();
		}
		if (right)
		{
			if (sharkLocation.x < screenWidth - sharkWidth)
			{
				sharkLocation.x += movementSpeed;
			}
			checkAnswer();
		}
		if (left)
		{
			if (sharkLocation.x > 0)
			{
				sharkLocation.x -= movementSpeed;
			}
			checkAnswer();
		}
		//checkAnswer();
	}

	public void resolveKeyEvent(KeyEvent e, boolean value)
	{
		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W)
		{
			up = value;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S)
		{
			down = value;
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A)
		{
			left = value;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D)
		{
			right = value;
		}
	}

	public void keyPressed(KeyEvent e)
	{
		resolveKeyEvent(e, true);
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		resolveKeyEvent(e, false);
	}

	@Override
	public void keyTyped(KeyEvent e)
	{}

	private void getQuestion()
	{
		int random = Controller.rng.nextInt(10);
		if (frequency > 0 && random <= frequency && questionList != null)
		{
			questionFromList();
		}
		else
		{
			generateQuestion();
		}
		while (answer < 0)
		{
			generateQuestion();
		}
	}

	private void questionFromList()
	{
		int random = Controller.rng.nextInt(questionList.size());
		question = questionList.get(random);
		if (question.contains("+"))
		{
			int operator = question.indexOf("+");
			int firstInteger = Integer.parseInt(question.substring(0, operator));
			int secondInteger = Integer.parseInt(question.substring(operator + 1));
			answer = firstInteger + secondInteger;
			question = firstInteger + " + " + secondInteger + " = ? ";
		}
		else
		{
			int operator = question.indexOf("-");
			int firstInteger = Integer.parseInt(question.substring(0, operator));
			int secondInteger = Integer.parseInt(question.substring(operator + 1));
			answer = firstInteger - secondInteger;
			question = firstInteger + " - " + secondInteger + " = ? ";
		}
	}

	private void generateQuestion()
	{
		switch (questionTypes)
		{
			case 0:
				int random = Controller.rng.nextInt(2);
				if (random < 1)
				{
					generateAddition();
				}
				else
				{
					generateSubtraction();
				}
				break;
			case 1:
				generateAddition();
				break;
			case 2:
				generateSubtraction();
				break;
		}
	}

	private void generateAddition()
	{
		int firstInteger = Controller.rng.nextInt(questionBase);
		int secondInteger = Controller.rng.nextInt(questionBase);
		answer = firstInteger - secondInteger;
		question = firstInteger + " - " + secondInteger + " = ? ";
	}

	private void generateSubtraction()
	{
		int firstInteger = Controller.rng.nextInt(questionBase);
		int secondInteger = Controller.rng.nextInt(questionBase);
		answer = firstInteger + secondInteger;
		question = firstInteger + " + " + secondInteger + " = ? ";
	}

	private void addShark(){
		shark = new JLabel(sharkIcon);
		sharkLocation.x = defaultSharkLocation.x;
		sharkLocation.y = defaultSharkLocation.y;
		shark.setForeground(new Color(70, 130, 180));
		shark.setFont(new Font("Arial", Font.BOLD, 30));
		shark.setVerticalTextPosition(SwingConstants.BOTTOM);
		shark.setHorizontalTextPosition(SwingConstants.LEFT);
		add(shark);
	}
	
	private void updateScore(boolean correct)
	{
		if (correct)
		{
			score += 50;
			scoreLabel.setText("Score: " + Integer.toString(score));
			feedbackLabel.setText("Correct!  " + question.substring(0, question.indexOf("?")) + answer);
			repaint();
			playing = false;
		}
		else if (score > 0)
		{
			score -= 5;
		}
		scoreLabel.setText("Score: " + Integer.toString(score));
	}

	private void checkAnswer()
	{
		int intersect = -1;
		Rectangle sharkBounds = shark.getBounds();
		if (sharkBounds.intersects(answerLabel1.getBounds()) && !guessed1)
		{
			intersect = 0;
			guessed1 = true;
			answerLabel1.setText("");
			answerLabel1.setIcon(null);
		}
		else if (sharkBounds.intersects(answerLabel2.getBounds()) && !guessed2)
		{
			intersect = 1;
			guessed2 = true;
			answerLabel2.setText("");
			answerLabel2.setIcon(null);
		}
		else if (sharkBounds.intersects(answerLabel3.getBounds()) && !guessed3)
		{
			intersect = 2;
			guessed3 = true;
			answerLabel3.setText("");
			answerLabel3.setIcon(null);
		}

		if (intersect == randomPlacement)
		{
			System.out.println("Correct answer given.");
			updateScore(true);
			remove(shark);
		}
		else if (intersect != -1)
		{
			feedbackLabel.setText("Try again!");
			miss = true;
			updateScore(false);
		}
	}

	public void labelFlash(JLabel label)
	{
		final JLabel temp = label;
		new Thread(new Runnable()
		{
			public void run()
			{
				temp.setVisible(true);
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						temp.setVisible(false);
					}
				});
			}
		}).start();
	}

	@Override
	public void paint(Graphics g)
	{
		requestFocus();
		answerLabel1.setLocation(screenWidth - 150, 100);
		answerLabel2.setLocation(screenWidth - 150, 260);
		answerLabel3.setLocation(screenWidth - 150, 420);
		shark.setLocation(sharkLocation);
		super.paint(g);
	}
	
	private void stopTimers()
	{
		displayTime.stop();
		refreshTimer.stop();
	}
	
	private void startTimers()
	{
		displayTime.start();
		refreshTimer.start();
	}
	
	public void showInstructions(){
		Object[] options = {"Okay", "Don't show again"};
		int instructResult = JOptionPane.showOptionDialog(base.messagePanel, g3instruct, "Instructions",JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE,null, options, options[0]);
		if(instructResult == 1){
			base.setInstructionPreferences("game2Instructions");
		}	
	}

}
