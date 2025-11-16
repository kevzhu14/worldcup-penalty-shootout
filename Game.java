/* Amadis Hali + Kevin Zhu
 Game.java
 Play a series of soccer penalty shootouts to win the tournament.
 You either go through a series of slider minigames to position your shot or click on a position to attempt to save the ball.
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
import java.awt.geom.*;
import java.util.*;
import javax.sound.midi.*;
import java.awt.MouseInfo;


public class Game extends JFrame implements ActionListener{
	static GamePanel game=new GamePanel();





    public Game(GamePanel m) {
    	super("World Cup Penalty Shootout");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1024,768);
		add(game);

		javax.swing.Timer tm = new javax.swing.Timer(10, this);
		tm.setInitialDelay(1000);
		tm.start();

		setVisible(true);


    }	
    
   	public void actionPerformed(ActionEvent evt){
   		game.update();
		game.repaint();




	}
	
	public static void main(String[] arguments) {
		Game frame = new Game(game);
		Tournament t=new Tournament(game);



	

		
    }		

	public static int randint(int low, int high){
	 	return (int)(Math.random()*(high-low+1)+low);
	}
}


class GamePanel extends JPanel implements KeyListener, MouseListener{
	public String mode="";
	private int powerX,powerY,power,pdirection=DOWN,horizX,horizY,horizontal,vertX,vertY,vertical,hdirection=RIGHT,vdirection=DOWN,finalposX,finalposY,aiH,aiV,aiP,aifinalposX,aifinalposY;
	public static final int UP=1,DOWN=2,LEFT=3,RIGHT=4;
	private boolean []keys; //keyboard
	public static String page="wait";
	private Image menuscreen,buttonDown,buttonUp,gradient,flags,skillball,netvertical,nethorizontal,menuText,knockoutTree,ball;
	public static Point mouse;
	private Rectangle menuInstructions, menuPlay;
	public static int num;
	public boolean bypass1=false,bypass2=false,bypass3=false,endprompt=false,aiShoots=false,firstTime=true;
	public String[]userShots=new String[5];
	public String[]aiShots=new String[5];
	public Goalie goalie;
	public static boolean goalieDive=false,save,userReset=false,AIreset=false,onlyOnce=false,enterPressed=false,playing=false,aiwins=false,userwins=false;
	private int jumpPoint=510,userCount=0,aiCount=0,userGoals=0,aiGoals=0,wins=0;
 	private double ballX,ballY;
 	public int tick=0;

	

	public static int randint(int low, int high){
	 	return (int)(Math.random()*(high-low+1)+low);
	}
	
	
	


	public GamePanel(){
		menuscreen = new ImageIcon("menuscreen.jpg").getImage();
		buttonUp = new ImageIcon("buttonUp.png").getImage();
		buttonDown = new ImageIcon("buttonDown.png").getImage();
		gradient = new ImageIcon("gradient.jpg").getImage();
		skillball = new ImageIcon("skillball.png").getImage();
		flags = new ImageIcon("Flags.png").getImage();
		nethorizontal = new ImageIcon("nethorizontal.png").getImage();
		netvertical = new ImageIcon("netvertical.png").getImage();
		menuText = new ImageIcon("menuText.png").getImage();
		knockoutTree = new ImageIcon("knockout.png").getImage();
		ball = new ImageIcon("ball.png").getImage();
		
        

    	menuPlay = new Rectangle(419,476,200,74); //menu play button
    	
 
	
		
		

		keys = new boolean[KeyEvent.KEY_LAST+1];
		
		goalie=new Goalie(510,300,"goalieLeft","goalieRight",8); //initiates goalie sprite
		
		powerX=800; //starting positions of balls used for timed "sliders"
		powerY=113;
		horizX=300;
		horizY=113;
		vertX=601;
		vertY=120;
		
		ballX=480; //the default ball start location
		ballY=610;
		addMouseListener(this);
		addKeyListener(this);
		setFocusable(true);
		requestFocus();
	}
	public void update(){
		if(page=="wait"){
			Tournament.load(); //loads team names 
			page="menu";
		}
		if(mode=="H"){
			horizontal(); //horizontal slider
		}
		else if(mode=="V"){
			vertical();  //vertical slider
		}
		else if(mode=="P"){
			power();  //power slider
			if(endprompt==true){
     			ballcontrol(); //refer below, after true, user's ball is shot
   			}
		}
		
		if(mode=="AI"){ //refer below, ai shoots ball
			if(aiShoots==true){
				aiShot();
			}
			
		}

		mouse = MouseInfo.getPointerInfo().getLocation();
		Point offset = getLocationOnScreen();
		mouse.translate(-offset.x, -offset.y);
		
		if(userReset) { //reset user shooting screen
			resetUser();
		}
		if(AIreset){ //reset ai shooting screen
			resetAI();
		}

	}
	
	public void resetUser() {
		Goalie.x=510;
		Goalie.y=300; //goalie default pos
	   jumpPoint=510; //initial jump position of goalie
	   powerX=800;//default positions of slider balls
	   powerY=113;
	   horizX=300;
	   horizY=113;
	   vertX=601;
	   vertY=120;
	   ballX=480;
	   ballY=610;
	   hdirection=RIGHT; //default direction of balls on the sliders
	   vdirection=DOWN; 
	   pdirection=DOWN;

	   mode="H"; 
	   page="userTurn";
	   endprompt=false;
	   bypass1=false;
	   bypass2=false;
	   bypass3=false;
	   userReset=false;
	}
	public void resetAI(){
		firstTime=true; //so ai ball doesnt continuously generate infinite positions to shoot ball
		Goalie.x=510;
		Goalie.y=300;
	    jumpPoint=510;
		ballX=480;
		ballY=610;
		tick=0; //to set a delay before ai shoots
		page="aiTurn";
		mode="AI";
		aiShoots=true;
		AIreset=false;
	}


//FOR SKILL GAME -----------------------------------------

//we want to call these 3 consecutively after each one is completed,


//these 3 methods continously slide back and fourth in their directions until space is pressed
	public void horizontal(){
		if(keys[KeyEvent.VK_SPACE]){
			horizontal=(int)(Math.round((double)(horizX-200)/230*100));//to get a "percent" from clicking space on slider.
			
			bypass1=true;
			mode="V"; //calls vertical after pressing space
			keys[KeyEvent.VK_SPACE]=false;

	
		}
		
		if(hdirection==RIGHT && bypass1==false){   //to ensure the ball slides continously until space is pressed
			horizX+=1;
			if(horizX==400){
				hdirection=LEFT;
			}
		}
		else if(hdirection==LEFT && bypass1==false){
			horizX-=1;
			if(horizX==200){
				hdirection=RIGHT;
			}
		}
		
	}
	
	public void vertical(){	//up and down slider
		if(keys[KeyEvent.VK_SPACE] ){
			vertical=(int)(Math.round((double)(183-vertY)/133*100));  
			
			bypass2=true;	
			mode="P";//calls power
			keys[KeyEvent.VK_SPACE]=false;
		}
		if(vdirection==DOWN && bypass2==false){ //continuously slides up and down
			vertY+=1;
			if(vertY==183){
				vdirection=UP;
			}
		}
		else if(vdirection==UP && bypass2==false){
			vertY-=1;
			if(vertY==50){
				vdirection=DOWN;
			}
		}
	}
	



	public void power(){ //same as vertical
		if(keys[KeyEvent.VK_SPACE]&& endprompt==false){
			power=(int)(Math.round((double)(183-powerY)/133*100)); 
			
			bypass3=true; 
				   
			goalieDive=true; //prompts goalie to dive
			jumpPoint=Goalie.aiGoalie();//goalie dives to position generated by aiGoalie method
			keys[KeyEvent.VK_SPACE]=false;
			endprompt=true;

			
			
			
		}
		if(pdirection==DOWN && bypass3==false){ //to ensure it slides until pressing space
			powerY+=1;
			if(powerY==183){
				pdirection=UP;
			}
		}
		else if(pdirection==UP && bypass3==false){
			powerY-=1;
			if(powerY==50){
				pdirection=DOWN;
			}
		}				
		
	}
	

	
//--------------------------------------------------	



	public void aiShot(){ //ai shoots at random direction towards net
		
		if(firstTime) {
			aiH=(int)(Math.round((double)(randint(200,430)-200)/230*100));
        	aiV=(int)(Math.round((double)(183-randint(50,183))/133*100));  
        	aiP=(int)(Math.round((double)(183-randint(50,183))/133*100));
        	firstTime=false;
		}	 
        aifinalposX=(int)(Math.round((double)aiH/100*720+150)); //final ball position
        aifinalposY=Math.abs(250-(int)(Math.round((double)aiV/100*230-250)));
        double incY=(double)Math.abs(aifinalposY-ballY)/20.0; //increments which the original ball position moves to its final ball position
        double incX=(double)Math.abs(aifinalposX-ballX)/20.0;
        tick++; //for delay before shot
 
        if(tick>=200){
	        if(ballX>aifinalposX){ //final position is left side of net
	            ballX-=incX; 
	        }
	        if(ballY>aifinalposY){//ball moves up from starting position
	               ballY-=incY;
	        }
	        if(ballX<aifinalposX){//final position is right side of net
	            ballX+=incX;
	          
	        }
	        if(Math.abs(ballX-aifinalposX)<20){
	            ballX=aifinalposX;
	            
	        }
	        if(Math.abs(ballY-aifinalposY)<20){
	            ballY=aifinalposY;
	            
	            
	        }
	       	if(ballY==aifinalposY && ballX==aifinalposX){
    			checkcollision(); //checking collision with goalie once at final position
    		
  			}
        
	 
	        
	        
        }

        
       

    }
	
	public void ballcontrol(){  //same as aiShot, but for the user
	
		finalposX=(int)(Math.round((double)horizontal/100*720+150)); //final positions come from the values obtained from timing the pressed space on the sliders from before
		finalposY=Math.abs(250-(int)(Math.round((double)vertical/100*230-250)));
		
		double incY=(double)Math.abs(finalposY-ballY)/10.0;
		double incX=(double)Math.abs(finalposX-ballX)/10.0;
		if(ballX>finalposX){ //final position is left side of net
			ballX-=incX; 
		}
		if(ballY>finalposY){
		   	ballY-=incY;
		}
		if(ballX<finalposX){//final position is right side of net
			ballX+=incX;
		  
		}
		if(Math.abs(ballX-finalposX)<20){
			ballX=finalposX;
		}
		if(Math.abs(ballY-finalposY)<20){
		    ballY=finalposY;
		}

		if(ballY==finalposY && ballX==finalposX){
    		checkcollision();
  		}
	}
	
	public void checkcollision(){ //checking of ball touches goalie, using middle of the ball as touching point
	//getting length and  width of the frame of the current goalie sprite picture to ensure entire goalie hitbox
		if(ballX+30>=Goalie.x && ballX+30<=Goalie.x+(Goalie.picsLeft[(int)Goalie.fr].getWidth(null)) && ballY+30>=Goalie.y && ballY+30<=Goalie.y+(Goalie.picsLeft[(int)Goalie.fr].getHeight(null))){
			save=true;	//hit, saved			
		}
		else {
			save=false;	//not saved, goes in the net		
		}
		
		
		if(userCount<5 || aiCount<5){ //each party shoots a total of 5 times, alternating
		
		
			if(save==true && onlyOnce==false) { //if saved
				if(page=="userTurn") {
					userShots[userCount]="miss"; //keeping track of goal or miss in an array
					userCount+=1; //counter
					onlyOnce=true; //to prevent from repeating in the same shot
						
				}
				else if(page=="aiTurn") {  //for ai
					aiShots[aiCount]="miss";
					aiCount+=1;
					onlyOnce=true;
					
					
				}
			}
			else if(save==false && onlyOnce==false){ //if scored
				if(page=="userTurn") {
					userShots[userCount]="goal";
					userCount+=1;
					onlyOnce=true;
					
						
				}
				else if(page=="aiTurn") {
					aiShots[aiCount]="goal";
					aiCount+=1;
					onlyOnce=true;
					
				}
			}
		}
			
		if(page=="userTurn"){
			if(goalie.fr==0 && userCount<=5){ //if frame ended and player hasnt shot 5 times,
				
				AIreset=true; //ai's turn
				onlyOnce=false;		
			}
		}
		else if(page=="aiTurn"){
			
			if(goalie.fr==0 && aiCount<5){ //if frame ended and ai hasnt shot 5 times
				
				userReset=true;//users turn
				onlyOnce=false;
					
			}

		}
		if(userCount==5 || aiCount==5){ //both shoot 5 times
		
			
			determineOutcome(); 
			userCount=0; //resetting counters
			aiCount=0;
			
		}
		
		
		

	}
	public void determineOutcome(){ //determining win/loss

		for(int i=0;i<5;i++){
			if(userShots[i]=="goal"){
				userGoals+=1;	
			}
			if(aiShots[i]=="goal"){
				aiGoals+=1;
			}
		}
		
		if(userGoals>=aiGoals){ //if user wins
			userwins=true;
			wins+=1;
			
			
		}
		else if(userGoals<aiGoals){
			aiwins=true;
					
		}
		if(wins==3){
			Tournament.top2ofgroups();
			Tournament.roundof16();
			page="roundof16";
		}
		if(wins==4){
			Tournament.quarterfinals();
			page="quarters";
		}
		if(wins==5){
			Tournament.semifinals();
			page="semis";
		}
		if(wins==6){
			Tournament.finals();
			page="finals";
		}
		
		if(wins<3){ 
			page="winlossscreen";
		}
		else if(wins==7){ //7 games to win
			Tournament.finals();
			page="winner";
		}
		Arrays.fill(userShots,null); //reset arrays that keep track of goals/saves
		Arrays.fill(aiShots,null);
		
	}
	
	
	
    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;

		
    }
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }
    
	
	public void	mouseClicked(MouseEvent e){
	}
	
	public void	mouseEntered(MouseEvent e){}
	
	public void	mouseExited(MouseEvent e){}
	
	public void	mousePressed(MouseEvent e){
		if(page =="menu"){
			if(menuPlay.contains(mouse)){ //clicking on button in menu brings to team select
				page="teamselect";
			}
		}
		else if(page=="teamselect") {
			int mx = e.getX(); 
	        int my = e.getY(); 
	        
	        int startX=150;
	        int startY=200; //top left corner of rectangle drawn from loop
	        
	        int wid=88;  //width and height of each individual rectangle to separate lists
	        int high=88;
	        
	        int col=(mx-startX)/wid; 
	        int row=(my-startY)/high;
	        
	        num=col+row*8;  //to determine index/num of team selected
	        
	        

			if(num>=0 && mx>=150 && my>=200 && mx<=854 && my<=552){ //within the big selecting rectangle
				page="group"; //groupstage page
				Tournament.groupStage();
				
				

			}
		}
		else if(page=="aiTurn") {	//when ai shoots, you click to a position for the goalie dive to
			goalieDive=true;	//prompts to dive
			jumpPoint=e.getX(); //position to dive to is mouse click positions
		}

	
	}
	public void	mouseReleased(MouseEvent e){}


    public void imageInRect(Graphics g, Image img, Rectangle area){ //for button
		g.drawImage(img, area.x, area.y, area.width, area.height, null);
    }

   	
	@Override
	public void paintComponent(Graphics g){ //drawing pages
		if(page=="menu"){
			drawMenu(g);
		}
		else if(page=="teamselect"){
			drawTeamSelect(g);
		}

		else if(page=="userTurn"){
			drawuserTurn(g);
		}
		else if(page=="group"){
			drawGroups(g);
		}
		else if(page=="quarters"){
			drawquarters(g);
		}
		else if(page=="semis"){
			drawsemis(g);
		}
		else if(page=="finals"){
			drawfinals(g);
		}
		else if(page=="winner"){
			drawwinner(g);
		}
		else if(page=="aiTurn"){
			drawaiTurn(g);
		}
		else if(page=="winlossscreen"){
			drawwinloss(g);
		}
		else if(page=="roundof16"){
			drawroundof16(g);
		}


    }
	
    public void drawMenu(Graphics g){
    	g.drawImage(menuscreen,0,0,this);
    	g.drawImage(menuText,7,-243,this);
		if(menuPlay.contains(mouse)){ //if mouse hovers, button lights up
			imageInRect(g, buttonDown, menuPlay);
		}
		else{
			imageInRect(g, buttonUp, menuPlay);			
		}	   	
    }

	public void drawTeamSelect(Graphics g) {
		Font Consolas=new Font("Consolas",Font.ITALIC,50);//font constructor
		Font Consolas2=new Font("Consolas",Font.BOLD,50);//font constructor
		g.drawImage(menuscreen,0,0,this);
    	g.drawImage(flags,150,200,this);
    	g.setColor(Color.white);
    	for(int x=150; x<796; x+=88) { //drawing rectangles to separate teams
    		for(int y=200; y<529; y+=88) {
    			g.drawRect(x,y,88,88);
    		}
    	}
    	
    	
		int mx = (int)mouse.getX(); 
	    int my = (int)mouse.getY(); 
	        
	    int startX=150;
	    int startY=200;
	        
	    int wid=88;
	    int high=88;
	        
	    int col=(mx-startX)/wid;
	    int row=(my-startY)/high;
	        
	    num=col+row*8;
	    
	    g.setFont(Consolas2);
    	if(mouse.getX()>=150 && mouse.getY()>=200 && mouse.getX()<=854 && mouse.getY()<=552){ //to display team names, while hovered over a team
    		g.drawString(Tournament.allTeams.get(num).getName(),400,180);
    	}
		g.setFont(Consolas);
		g.drawString("CHOOSE A TEAM",330,100);
				
    	
    	
        
    }


    public void drawaiTurn(Graphics g){
		g.drawImage(menuscreen,0,0,this);
		
    	goalie.move(jumpPoint); //goalie sprite moves depending on where mouse clicks
    	goalie.draw(g);
    
    	g.drawImage(ball,(int)ballX,(int)ballY,this); 
    	
    
    	
    }

    public void drawuserTurn(Graphics g){ //draw skill games, goalie draws, ball draws
		g.drawImage(menuscreen,0,0,this);
		g.drawImage(nethorizontal,200,50,this);
		g.drawImage(skillball,horizX,horizY,this);
		g.drawImage(netvertical,500,50,this);
		g.drawImage(skillball,vertX,vertY,this);
    	g.drawImage(gradient,800,50,this);
    	g.drawImage(skillball,powerX,powerY,this);
    	
    		

    	goalie.move(jumpPoint);
    	goalie.draw(g);
    	
    
    	g.drawImage(ball,(int)ballX,(int)ballY,this);
    	
    }
    public void drawwinloss(Graphics g){
    	
    	
    	g.drawImage(menuscreen,0,0,this);
		Font Consolas=new Font("Consolas",Font.BOLD,50);//font constructor
		g.setFont(Consolas);
		g.setColor(Color.white);
    	if(aiwins==true){ //if ai won game 
    		g.drawString("You lost!",400,200);		
    	}
    	if(userwins==true){ //if user won game
    		g.drawString("You won!",400,200);
    	}
    	g.drawString("Press ENTER to play next match",120,400);
		if(keys[KeyEvent.VK_ENTER]){
			
			userReset=true; //start first game
		}
		
    }
    

    
    public void drawGroups(Graphics g){ //for group page, draws rectangles and puts team names in their respective groups
		g.drawImage(menuscreen,0,0,this);
		Font Consolas=new Font("Consolas",Font.BOLD,30);//font constructor
		g.setFont(Consolas);
		g.setColor(Color.white);
		g.drawString("GROUPS",455,30);
		g.setColor(new Color(0f,0f,0f,.75f ));
		
		g.fillRect(30,50,230,310);
		g.fillRect(270,50,230,310);
		g.fillRect(510,50,230,310);
		g.fillRect(750,50,230,310);
		g.fillRect(30,380,230,310);
		g.fillRect(270,380,230,310);
		g.fillRect(510,380,230,310);
		g.fillRect(750,380,230,310);
		g.setColor(Color.white);
		int d=110;
		g.drawString("A",139,75);
		for(int i=0;i<Tournament.GroupA.size();i++){
			g.drawString(Tournament.GroupA.get(i).getName(),45,d);
			d+=60;
		}
		
		d=110;
		g.drawString("B",379,75);
		for(int i=0;i<Tournament.GroupB.size();i++){
			g.drawString(Tournament.GroupB.get(i).getName(),285,d);
			d+=60;
		}
		
		d=110;
		g.drawString("C",619,75);
		for(int i=0;i<Tournament.GroupC.size();i++){
			g.drawString(Tournament.GroupC.get(i).getName(),525,d);
			d+=60;
		}

		d=110;
		g.drawString("D",859,75);
		for(int i=0;i<Tournament.GroupD.size();i++){
			g.drawString(Tournament.GroupD.get(i).getName(),765,d);
			d+=60;
		}

		d=440;
		g.drawString("E",139,405);
		for(int i=0;i<Tournament.GroupE.size();i++){
			g.drawString(Tournament.GroupE.get(i).getName(),45,d);
			d+=60;
		}
		
		d=440;
		g.drawString("F",379,405);
		for(int i=0;i<Tournament.GroupF.size();i++){
			g.drawString(Tournament.GroupF.get(i).getName(),285,d);
			d+=60;
		}
		
		d=440;
		g.drawString("G",619,405);
		for(int i=0;i<Tournament.GroupG.size();i++){
			g.drawString(Tournament.GroupG.get(i).getName(),525,d);
			d+=60;
		}
		
		d=440;
		g.drawString("H",859,405);
		for(int i=0;i<Tournament.GroupH.size();i++){
			g.drawString(Tournament.GroupH.get(i).getName(),765,d);
			d+=60;
		}
		g.drawString("Press ENTER",415,715);
		if(keys[KeyEvent.VK_ENTER]){
			
			userReset=true; //start game
		}
		
    }
    
    
	public void drawroundof16(Graphics g){ //draws bracket for round of 16
        g.drawImage(menuscreen,0,0,this);
        g.setColor(new Color(1f,1f,1f,.50f ));
        g.fillRect(50,100,900,600);
        g.setColor(Color.black);
        g.drawImage(knockoutTree,50,100,this);
        Font Consolas=new Font("Consolas",Font.ITALIC,50);//font constructor
        g.setFont(Consolas);
        g.drawString("KNOCKOUT BRACKET",300,50);
		Font Consolas2=new Font("Consolas",Font.PLAIN,20);
		g.setFont(Consolas2);
		g.drawString(Tournament.match1.get(0).getName(),83,220);
        g.drawString(Tournament.match1.get(1).getName(),83,265);
		g.drawString(Tournament.match2.get(0).getName(),83,310);
        g.drawString(Tournament.match2.get(1).getName(),83,355);
        g.drawString(Tournament.match5.get(0).getName(),83,457);
        g.drawString(Tournament.match5.get(1).getName(),83,502);
		g.drawString(Tournament.match6.get(0).getName(),83,547);
        g.drawString(Tournament.match6.get(1).getName(),83,592);
        
		g.drawString(Tournament.match3.get(0).getName(),838,220);
        g.drawString(Tournament.match3.get(1).getName(),838,265);
		g.drawString(Tournament.match4.get(0).getName(),838,310);
        g.drawString(Tournament.match4.get(1).getName(),838,355);
        g.drawString(Tournament.match7.get(0).getName(),838,457);
        g.drawString(Tournament.match7.get(1).getName(),838,502);
		g.drawString(Tournament.match8.get(0).getName(),838,547);
        g.drawString(Tournament.match8.get(1).getName(),838,592);
		if(keys[KeyEvent.VK_ENTER]){
			
			userReset=true; //start game
		}
 
    }
	public void drawquarters(Graphics g){ //draws bracket for quarterfinals
        g.drawImage(menuscreen,0,0,this);
        g.setColor(new Color(1f,1f,1f,.50f ));
        g.fillRect(50,100,900,600);
        g.setColor(Color.black);
        g.drawImage(knockoutTree,50,100,this);
        Font Consolas=new Font("Consolas",Font.ITALIC,50);//font constructor
        g.setFont(Consolas);
        g.drawString("KNOCKOUT BRACKET",300,50);
		Font Consolas2=new Font("Consolas",Font.PLAIN,20);
		g.setFont(Consolas2);
		g.drawString(Tournament.match1.get(0).getName(),83,220);
        g.drawString(Tournament.match1.get(1).getName(),83,265);
		g.drawString(Tournament.match2.get(0).getName(),83,310);
        g.drawString(Tournament.match2.get(1).getName(),83,355);
        g.drawString(Tournament.match5.get(0).getName(),83,457);
        g.drawString(Tournament.match5.get(1).getName(),83,502);
		g.drawString(Tournament.match6.get(0).getName(),83,547);
        g.drawString(Tournament.match6.get(1).getName(),83,592);
        
		g.drawString(Tournament.match3.get(0).getName(),838,220);
        g.drawString(Tournament.match3.get(1).getName(),838,265);
		g.drawString(Tournament.match4.get(0).getName(),838,310);
        g.drawString(Tournament.match4.get(1).getName(),838,355);
        g.drawString(Tournament.match7.get(0).getName(),838,457);
        g.drawString(Tournament.match7.get(1).getName(),838,502);
		g.drawString(Tournament.match8.get(0).getName(),838,547);
        g.drawString(Tournament.match8.get(1).getName(),838,592);
        
		g.drawString(Tournament.match9.get(0).getName(),212,240);
        g.drawString(Tournament.match9.get(1).getName(),212,330);
		g.drawString(Tournament.match10.get(0).getName(),212,480);
        g.drawString(Tournament.match10.get(1).getName(),212,570);
		g.drawString(Tournament.match11.get(0).getName(),708,240);
        g.drawString(Tournament.match11.get(1).getName(),708,330);
		g.drawString(Tournament.match12.get(0).getName(),708,480);
        g.drawString(Tournament.match12.get(1).getName(),708,570);
		if(keys[KeyEvent.VK_ENTER]){
			
			userReset=true; //start game
		}

        
 
    }
    
    
    
	public void drawsemis(Graphics g){ //draws for semis
        g.drawImage(menuscreen,0,0,this);
        g.setColor(new Color(1f,1f,1f,.50f ));
        g.fillRect(50,100,900,600);
        g.setColor(Color.black);
        g.drawImage(knockoutTree,50,100,this);
        Font Consolas=new Font("Consolas",Font.ITALIC,50);//font constructor
        g.setFont(Consolas);
        g.drawString("KNOCKOUT BRACKET",300,50);
		Font Consolas2=new Font("Consolas",Font.PLAIN,20);
		g.setFont(Consolas2);
		g.drawString(Tournament.match1.get(0).getName(),83,220);
        g.drawString(Tournament.match1.get(1).getName(),83,265);
		g.drawString(Tournament.match2.get(0).getName(),83,310);
        g.drawString(Tournament.match2.get(1).getName(),83,355);
        g.drawString(Tournament.match5.get(0).getName(),83,457);
        g.drawString(Tournament.match5.get(1).getName(),83,502);
		g.drawString(Tournament.match6.get(0).getName(),83,547);
        g.drawString(Tournament.match6.get(1).getName(),83,592);
        
		g.drawString(Tournament.match3.get(0).getName(),838,220);
        g.drawString(Tournament.match3.get(1).getName(),838,265);
		g.drawString(Tournament.match4.get(0).getName(),838,310);
        g.drawString(Tournament.match4.get(1).getName(),838,355);
        g.drawString(Tournament.match7.get(0).getName(),838,457);
        g.drawString(Tournament.match7.get(1).getName(),838,502);
		g.drawString(Tournament.match8.get(0).getName(),838,547);
        g.drawString(Tournament.match8.get(1).getName(),838,592);
        
		g.drawString(Tournament.match9.get(0).getName(),212,240);
        g.drawString(Tournament.match9.get(1).getName(),212,330);
		g.drawString(Tournament.match10.get(0).getName(),212,480);
        g.drawString(Tournament.match10.get(1).getName(),212,570);
		g.drawString(Tournament.match11.get(0).getName(),708,240);
        g.drawString(Tournament.match11.get(1).getName(),708,330);
		g.drawString(Tournament.match12.get(0).getName(),708,480);
        g.drawString(Tournament.match12.get(1).getName(),708,570);
        
        g.drawString(Tournament.match13.get(0).getName(),334,285);
        g.drawString(Tournament.match13.get(1).getName(),334,525);

        g.drawString(Tournament.match14.get(0).getName(),588,285);
        g.drawString(Tournament.match14.get(1).getName(),588,525);
		if(keys[KeyEvent.VK_ENTER]){
			
			userReset=true; //start game
		}
    }
    
	public void drawfinals(Graphics g){ //draws brackets for finals
        g.drawImage(menuscreen,0,0,this);
        g.setColor(new Color(1f,1f,1f,.50f ));
        g.fillRect(50,100,900,600);
        g.setColor(Color.black);
        g.drawImage(knockoutTree,50,100,this);
        Font Consolas=new Font("Consolas",Font.ITALIC,50);//font constructor
        g.setFont(Consolas);
        g.drawString("KNOCKOUT BRACKET",300,50);
		Font Consolas2=new Font("Consolas",Font.PLAIN,20);
		g.setFont(Consolas2);
		g.drawString(Tournament.match1.get(0).getName(),83,220);
        g.drawString(Tournament.match1.get(1).getName(),83,265);
		g.drawString(Tournament.match2.get(0).getName(),83,310);
        g.drawString(Tournament.match2.get(1).getName(),83,355);
        g.drawString(Tournament.match5.get(0).getName(),83,457);
        g.drawString(Tournament.match5.get(1).getName(),83,502);
		g.drawString(Tournament.match6.get(0).getName(),83,547);
        g.drawString(Tournament.match6.get(1).getName(),83,592);
        
		g.drawString(Tournament.match3.get(0).getName(),838,220);
        g.drawString(Tournament.match3.get(1).getName(),838,265);
		g.drawString(Tournament.match4.get(0).getName(),838,310);
        g.drawString(Tournament.match4.get(1).getName(),838,355);
        g.drawString(Tournament.match7.get(0).getName(),838,457);
        g.drawString(Tournament.match7.get(1).getName(),838,502);
		g.drawString(Tournament.match8.get(0).getName(),838,547);
        g.drawString(Tournament.match8.get(1).getName(),838,592);
        
		g.drawString(Tournament.match9.get(0).getName(),212,240);
        g.drawString(Tournament.match9.get(1).getName(),212,330);
		g.drawString(Tournament.match10.get(0).getName(),212,480);
        g.drawString(Tournament.match10.get(1).getName(),212,570);
		g.drawString(Tournament.match11.get(0).getName(),708,240);
        g.drawString(Tournament.match11.get(1).getName(),708,330);
		g.drawString(Tournament.match12.get(0).getName(),708,480);
        g.drawString(Tournament.match12.get(1).getName(),708,570);
        
        g.drawString(Tournament.match13.get(0).getName(),334,285);
        g.drawString(Tournament.match13.get(1).getName(),334,525);

        g.drawString(Tournament.match14.get(0).getName(),588,285);
        g.drawString(Tournament.match14.get(1).getName(),588,525);
        
        
        g.drawString(Tournament.match15.get(0).getName(),462,355);
        g.drawString(Tournament.match15.get(1).getName(),462,458);
		if(keys[KeyEvent.VK_ENTER]){
			
			userReset=true; //start game
		}
    }
	public void drawwinner(Graphics g){ //draws bracket including winner
        g.drawImage(menuscreen,0,0,this);
        g.setColor(new Color(1f,1f,1f,.50f ));
        g.fillRect(50,100,900,600);
        g.setColor(Color.black);
        g.drawImage(knockoutTree,50,100,this);
        Font Consolas=new Font("Consolas",Font.ITALIC,50);//font constructor
        g.setFont(Consolas);
        g.drawString("KNOCKOUT BRACKET",300,50);
		Font Consolas2=new Font("Consolas",Font.PLAIN,20);
		g.setFont(Consolas2);
		g.drawString(Tournament.match1.get(0).getName(),83,220);
        g.drawString(Tournament.match1.get(1).getName(),83,265);
		g.drawString(Tournament.match2.get(0).getName(),83,310);
        g.drawString(Tournament.match2.get(1).getName(),83,355);
        g.drawString(Tournament.match5.get(0).getName(),83,457);
        g.drawString(Tournament.match5.get(1).getName(),83,502);
		g.drawString(Tournament.match6.get(0).getName(),83,547);
        g.drawString(Tournament.match6.get(1).getName(),83,592);
        
		g.drawString(Tournament.match3.get(0).getName(),838,220);
        g.drawString(Tournament.match3.get(1).getName(),838,265);
		g.drawString(Tournament.match4.get(0).getName(),838,310);
        g.drawString(Tournament.match4.get(1).getName(),838,355);
        g.drawString(Tournament.match7.get(0).getName(),838,457);
        g.drawString(Tournament.match7.get(1).getName(),838,502);
		g.drawString(Tournament.match8.get(0).getName(),838,547);
        g.drawString(Tournament.match8.get(1).getName(),838,592);
        
		g.drawString(Tournament.match9.get(0).getName(),212,240);
        g.drawString(Tournament.match9.get(1).getName(),212,330);
		g.drawString(Tournament.match10.get(0).getName(),212,480);
        g.drawString(Tournament.match10.get(1).getName(),212,570);
		g.drawString(Tournament.match11.get(0).getName(),708,240);
        g.drawString(Tournament.match11.get(1).getName(),708,330);
		g.drawString(Tournament.match12.get(0).getName(),708,480);
        g.drawString(Tournament.match12.get(1).getName(),708,570);
        
        g.drawString(Tournament.match13.get(0).getName(),334,285);
        g.drawString(Tournament.match13.get(1).getName(),334,525);

        g.drawString(Tournament.match14.get(0).getName(),588,285);
        g.drawString(Tournament.match14.get(1).getName(),588,525);
        
        
        g.drawString(Tournament.match15.get(0).getName(),462,355);
        g.drawString(Tournament.match15.get(1).getName(),462,458);
		Font Consolas3=new Font("Consolas",Font.BOLD,40);
		g.setFont(Consolas3);
        g.drawString("WINNER: "+Tournament.winner.getName(),360,140);
    }
}


	


class Team { //managing team stats
	String name;
	int points;
	int wins;
	int losses;

	public Team(String line) {
		String[]stats=line.split(",");
		name=stats[0];
		points=Integer.parseInt(stats[1]);
		wins=Integer.parseInt(stats[2]);
		losses=Integer.parseInt(stats[3]);
	}
	public String toString(){
		return name;
	}
	public String getName() {
 		return name;
 	}
 	public int getPoints() {
 		return points;
 	}
 	public int getWins() {
 		return wins;
 	}
 	public int getLosses() {
 		return losses;
 	}
 	public void setPoints(int pts){
 		points=pts;
 	}
  	public void setWins(int wns){
 		wins=wns;
 	}
  	public void setLosses(int ls){
 		losses=ls;
 	}
}

class Tournament {
		
	public static GamePanel mainFrame;
	public static ArrayList<Team>allTeams=new ArrayList<Team>();
	
	public static ArrayList<Team>GroupA=new ArrayList<Team>();
	public static ArrayList<Team>GroupB=new ArrayList<Team>(); //initial groups
	public static ArrayList<Team>GroupC=new ArrayList<Team>();
	public static ArrayList<Team>GroupD=new ArrayList<Team>();
	public static ArrayList<Team>GroupE=new ArrayList<Team>();
	public static ArrayList<Team>GroupF=new ArrayList<Team>();
	public static ArrayList<Team>GroupG=new ArrayList<Team>();
	public static ArrayList<Team>GroupH=new ArrayList<Team>();
	
	public static ArrayList<Team>playedbefore=new ArrayList<Team>();

	public static ArrayList<Team>GroupAfinal=new ArrayList<Team>();
	public static ArrayList<Team>GroupBfinal=new ArrayList<Team>();
	public static ArrayList<Team>GroupCfinal=new ArrayList<Team>();//top 2 of each group
	public static ArrayList<Team>GroupDfinal=new ArrayList<Team>();
	public static ArrayList<Team>GroupEfinal=new ArrayList<Team>();
	public static ArrayList<Team>GroupFfinal=new ArrayList<Team>();
	public static ArrayList<Team>GroupGfinal=new ArrayList<Team>();
	public static ArrayList<Team>GroupHfinal=new ArrayList<Team>();
	//Round of 16
	
	public static ArrayList<Team>match1=new ArrayList<Team>();
	public static ArrayList<Team>match2=new ArrayList<Team>();
	public static ArrayList<Team>match3=new ArrayList<Team>();
	public static ArrayList<Team>match4=new ArrayList<Team>();
	public static ArrayList<Team>match5=new ArrayList<Team>();
	public static ArrayList<Team>match6=new ArrayList<Team>();
	public static ArrayList<Team>match7=new ArrayList<Team>();
	public static ArrayList<Team>match8=new ArrayList<Team>();
	
	//Quarterfinals
	
	public static ArrayList<Team>match9=new ArrayList<Team>();
	public static ArrayList<Team>match10=new ArrayList<Team>();
	public static ArrayList<Team>match11=new ArrayList<Team>();
	public static ArrayList<Team>match12=new ArrayList<Team>();
	
	//Semifinals
	
	public static ArrayList<Team>match13=new ArrayList<Team>();
	public static ArrayList<Team>match14=new ArrayList<Team>();
	
	//Final

	public static ArrayList<Team>match15=new ArrayList<Team>();
	
	public static Team user;
	public static Team opposition;
	public static Team winner;

	
	
	
	public Tournament(GamePanel m) {
		mainFrame=m;
	}
	
	public static void load() {
		try {
			Scanner inFile=new Scanner(new BufferedReader(new FileReader("teams.txt")));
			int n=Integer.parseInt(inFile.nextLine());
			for(int i=0; i<n; i++) {
				String teamStats=inFile.nextLine();
				allTeams.add(new Team(teamStats));
			}
			inFile.close();
		}
		catch(IOException ex) {
			System.out.println(ex);
		}
	}

   	
	public static int randint(int low, int high){
		return (int)(Math.random()*(high-low+1)+low);
	}
    public static void groupStage() {
		
    	user=allTeams.get(mainFrame.num);
	
        for(int i=0;i<4;i++){
            GroupA.add(allTeams.get(randint(0,allTeams.size()-1)));
            allTeams.remove(GroupA.get(i));
            GroupB.add(allTeams.get(randint(0,allTeams.size()-1)));
            allTeams.remove(GroupB.get(i));
            GroupC.add(allTeams.get(randint(0,allTeams.size()-1)));
            allTeams.remove(GroupC.get(i));
            GroupD.add(allTeams.get(randint(0,allTeams.size()-1)));
            allTeams.remove(GroupD.get(i));
            GroupE.add(allTeams.get(randint(0,allTeams.size()-1)));
            allTeams.remove(GroupE.get(i));
            GroupF.add(allTeams.get(randint(0,allTeams.size()-1)));
            allTeams.remove(GroupF.get(i));
            GroupG.add(allTeams.get(randint(0,allTeams.size()-1)));
            allTeams.remove(GroupG.get(i));
            GroupH.add(allTeams.get(randint(0,allTeams.size()-1)));
            allTeams.remove(GroupH.get(i));
        }		
		
		//ROUND ROBIN GROUP STAGE 
		
		
		
		
		for(int q=0;q<6;q++){
			Team tmp=GroupA.get(randint(0,3));
			while(checkformaxgames(tmp)==true){ //pick another team from group if 3 matches already played
				tmp=GroupA.get(randint(0,3));
			}
			Team tmp2=GroupA.get(randint(0,3));
			while(tmp2==tmp || checkformaxgames(tmp2)==true || checkifplayedbefore(tmp,tmp2)==true){
				tmp2=GroupA.get(randint(0,3));
			}

	
			
			
			if(randint(0,1)==0){
				tmp.setWins(tmp.getWins()+1);
				tmp2.setLosses(tmp2.getLosses()+1);
			}
			else{
				tmp.setLosses(tmp.getLosses()+1);
				tmp2.setWins(tmp2.getWins()+1);
			}
			playedbefore.add(tmp);
			playedbefore.add(tmp2);

		}
		

		
		
		for(int q=0;q<6;q++){
			Team tmp=GroupB.get(randint(0,3));
			while(checkformaxgames(tmp)==true){ //pick another team from group if 3 matches already played
				tmp=GroupB.get(randint(0,3));
			}
			Team tmp2=GroupB.get(randint(0,3));
			while(tmp2==tmp || checkformaxgames(tmp2)==true || checkifplayedbefore(tmp,tmp2)==true){
				tmp2=GroupB.get(randint(0,3));
			}

	
			
			
			if(randint(0,1)==0){
				tmp.setWins(tmp.getWins()+1);
				tmp2.setLosses(tmp2.getLosses()+1);
			}
			else{
				tmp.setLosses(tmp.getLosses()+1);
				tmp2.setWins(tmp2.getWins()+1);
			}
			playedbefore.add(tmp);
			playedbefore.add(tmp2);

		}

		
		for(int q=0;q<6;q++){
			Team tmp=GroupC.get(randint(0,3));
			while(checkformaxgames(tmp)==true){ //pick another team from group if 3 matches already played
				tmp=GroupC.get(randint(0,3));
			}
			Team tmp2=GroupC.get(randint(0,3));
			while(tmp2==tmp || checkformaxgames(tmp2)==true || checkifplayedbefore(tmp,tmp2)==true){
				tmp2=GroupC.get(randint(0,3));
			}

	
			
			
			if(randint(0,1)==0){
				tmp.setWins(tmp.getWins()+1);
				tmp2.setLosses(tmp2.getLosses()+1);
			}
			else{
				tmp.setLosses(tmp.getLosses()+1);
				tmp2.setWins(tmp2.getWins()+1);
			}
			playedbefore.add(tmp);
			playedbefore.add(tmp2);

		}

		
		for(int q=0;q<6;q++){
			Team tmp=GroupD.get(randint(0,3));
			while(checkformaxgames(tmp)==true){ //pick another team from group if 3 matches already played
				tmp=GroupD.get(randint(0,3));
			}
			Team tmp2=GroupD.get(randint(0,3));
			while(tmp2==tmp || checkformaxgames(tmp2)==true || checkifplayedbefore(tmp,tmp2)==true){
				tmp2=GroupD.get(randint(0,3));
			}

	
			
			
			if(randint(0,1)==0){
				tmp.setWins(tmp.getWins()+1);
				tmp2.setLosses(tmp2.getLosses()+1);
			}
			else{
				tmp.setLosses(tmp.getLosses()+1);
				tmp2.setWins(tmp2.getWins()+1);
			}
			playedbefore.add(tmp);
			playedbefore.add(tmp2);

		}

		
		
		for(int q=0;q<6;q++){
			Team tmp=GroupE.get(randint(0,3));
			while(checkformaxgames(tmp)==true){ //pick another team from group if 3 matches already played
				tmp=GroupE.get(randint(0,3));
			}
			Team tmp2=GroupE.get(randint(0,3));
			while(tmp2==tmp || checkformaxgames(tmp2)==true || checkifplayedbefore(tmp,tmp2)==true){
				tmp2=GroupE.get(randint(0,3));
			}

	
			
		
			if(randint(0,1)==0){
				tmp.setWins(tmp.getWins()+1);
				tmp2.setLosses(tmp2.getLosses()+1);
			}
			else{
				tmp.setLosses(tmp.getLosses()+1);
				tmp2.setWins(tmp2.getWins()+1);
			}
			playedbefore.add(tmp);
			playedbefore.add(tmp2);

		}

		for(int q=0;q<6;q++){
			Team tmp=GroupF.get(randint(0,3));
			while(checkformaxgames(tmp)==true){ //pick another team from group if 3 matches already played
				tmp=GroupF.get(randint(0,3));
			}
			Team tmp2=GroupF.get(randint(0,3));
			while(tmp2==tmp || checkformaxgames(tmp2)==true || checkifplayedbefore(tmp,tmp2)==true){
				tmp2=GroupF.get(randint(0,3));
			}

	
			
		
			if(randint(0,1)==0){
				tmp.setWins(tmp.getWins()+1);
				tmp2.setLosses(tmp2.getLosses()+1);
			}
			else{
				tmp.setLosses(tmp.getLosses()+1);
				tmp2.setWins(tmp2.getWins()+1);
			}
			playedbefore.add(tmp);
			playedbefore.add(tmp2);

		}


		for(int q=0;q<6;q++){
			Team tmp=GroupG.get(randint(0,3));
			while(checkformaxgames(tmp)==true){ //pick another team from group if 3 matches already played
				tmp=GroupG.get(randint(0,3));
			}
			Team tmp2=GroupG.get(randint(0,3));
			while(tmp2==tmp || checkformaxgames(tmp2)==true || checkifplayedbefore(tmp,tmp2)==true){
				tmp2=GroupG.get(randint(0,3));
			}

	
			
		
			if(randint(0,1)==0){
				tmp.setWins(tmp.getWins()+1);
				tmp2.setLosses(tmp2.getLosses()+1);
			}
			else{
				tmp.setLosses(tmp.getLosses()+1);
				tmp2.setWins(tmp2.getWins()+1);
			}
			playedbefore.add(tmp);
			playedbefore.add(tmp2);

		}


		for(int q=0;q<6;q++){
			Team tmp=GroupH.get(randint(0,3));
			while(checkformaxgames(tmp)==true){ //pick another team from group if 3 matches already played
				tmp=GroupH.get(randint(0,3));
			}
			Team tmp2=GroupH.get(randint(0,3));
			while(tmp2==tmp || checkformaxgames(tmp2)==true || checkifplayedbefore(tmp,tmp2)==true){
				tmp2=GroupH.get(randint(0,3));
			}

	
			
		
			if(randint(0,1)==0){
				tmp.setWins(tmp.getWins()+1);
				tmp2.setLosses(tmp2.getLosses()+1);
			}
			else{
				tmp.setLosses(tmp.getLosses()+1);
				tmp2.setWins(tmp2.getWins()+1);
			}
			playedbefore.add(tmp);
			playedbefore.add(tmp2);
		}		
    }
    
    
    
    
    public static boolean checkformaxgames(Team sample){ //to see if a team has already played the max # of games in group stage (3)
    	if(sample.getWins()+sample.getLosses()==3){
    		return true;
    	}
    	else{
    		return false;
    	}
    }
    public static boolean checkifplayedbefore(Team team1,Team team2){ //to see if the 2 teams have already played each other in the round-robin group stage
		for(int i=0;i<playedbefore.size();i+=2){
			if(playedbefore.get(i)==team1 && playedbefore.get(i+1)==team2||playedbefore.get(i)==team2 &&playedbefore.get(i+1)==team1){
				return true;
			}
		}
		return false;
    }
    
    public static void top2ofgroups(){
    	while(GroupA.size()!=2){
  	    	for(int i=0;i<GroupA.size();i++){
    			if(GroupA.get(i).getWins()==3 || GroupA.get(i).getWins()==2){
    				GroupAfinal.add(GroupA.get(i));
    				GroupA.remove(GroupA.get(i));	
    			}
    		}
	        while(GroupAfinal.size()<2) {
                GroupAfinal.add(GroupA.get(0));
                GroupA.remove(GroupA.get(0));
            }	
    	}


    	while(GroupB.size()!=2){
  	    	for(int i=0;i<GroupB.size();i++){
    			if(GroupB.get(i).getWins()==3 || GroupB.get(i).getWins()==2){
    				GroupBfinal.add(GroupB.get(i));
    				GroupB.remove(GroupB.get(i));
    			}
    		}
	        while(GroupBfinal.size()<2) {
                GroupBfinal.add(GroupB.get(0));
                GroupB.remove(GroupB.get(0));
                
            }   		
    	}

    	
    	while(GroupC.size()!=2){
  	    	for(int i=0;i<GroupC.size();i++){
    			if(GroupC.get(i).getWins()==3 || GroupC.get(i).getWins()==2){
    				GroupCfinal.add(GroupC.get(i));
    				GroupC.remove(GroupC.get(i));
    			}
    		}	
	        while(GroupCfinal.size()<2) {
                GroupCfinal.add(GroupC.get(0));
                GroupC.remove(GroupC.get(0));
            }	
    	}
 
    	
    	
    	while(GroupD.size()!=2){
  	    	for(int i=0;i<GroupD.size();i++){
    			if(GroupD.get(i).getWins()==3 || GroupD.get(i).getWins()==2){
    				GroupDfinal.add(GroupD.get(i));
    				GroupD.remove(GroupD.get(i));
    			}
    		}	
	        while(GroupDfinal.size()<2) {
                GroupDfinal.add(GroupD.get(0));
                GroupD.remove(GroupD.get(0));
            }	
    	}


    	while(GroupE.size()!=2){
  	    	for(int i=0;i<GroupE.size();i++){
    			if(GroupE.get(i).getWins()==3 || GroupE.get(i).getWins()==2){
    				GroupEfinal.add(GroupE.get(i));
    				GroupE.remove(GroupE.get(i));
    			}
    		}	
	        while(GroupEfinal.size()<2) {
                GroupEfinal.add(GroupE.get(0));
                GroupE.remove(GroupE.get(0));
            }	
    	}
   
    	
    	while(GroupF.size()!=2){
  	    	for(int i=0;i<GroupF.size();i++){
    			if(GroupF.get(i).getWins()==3 || GroupF.get(i).getWins()==2){
    				GroupFfinal.add(GroupF.get(i));
    				GroupF.remove(GroupF.get(i));
    			}
    		}	
	        while(GroupFfinal.size()<2) {
                GroupFfinal.add(GroupF.get(0));
                GroupF.remove(GroupF.get(0));
            }	
    	}
 
    	
    	while(GroupG.size()!=2){
  	    	for(int i=0;i<GroupG.size();i++){
    			if(GroupG.get(i).getWins()==3 || GroupG.get(i).getWins()==2){
    				GroupGfinal.add(GroupG.get(i));
    				GroupG.remove(GroupG.get(i));
    			}
    		}	
	        while(GroupGfinal.size()<2) {
                GroupGfinal.add(GroupG.get(0));
                GroupG.remove(GroupG.get(0));
            }	
    	}
  

    	while(GroupH.size()!=2){
  	    	for(int i=0;i<GroupH.size();i++){
    			if(GroupH.get(i).getWins()==3 || GroupH.get(i).getWins()==2){
    				GroupHfinal.add(GroupH.get(i));
    				GroupH.remove(GroupH.get(i));
    			}
    		}	
	        while(GroupHfinal.size()<2) {
                GroupHfinal.add(GroupH.get(0));
                GroupH.remove(GroupH.get(0));
            }	
    	}


    	
   
    	

    }
    
    
    public static void roundof16() {

    	match1.add(GroupAfinal.get(0));
    	match1.add(GroupBfinal.get(1));
    
    	
    	match2.add(GroupCfinal.get(0));
    	match2.add(GroupDfinal.get(1));

    	
    	match3.add(GroupBfinal.get(0));
    	match3.add(GroupAfinal.get(1));
    
    	
    	match4.add(GroupDfinal.get(0));
    	match4.add(GroupCfinal.get(1));
    	
    	
    	match5.add(GroupEfinal.get(0));
    	match5.add(GroupFfinal.get(1));
    	
    	
    	match6.add(GroupGfinal.get(0));
    	match6.add(GroupHfinal.get(1));
    	
    	
    	match7.add(GroupFfinal.get(0));
    	match7.add(GroupEfinal.get(1));
    
    	
    	match8.add(GroupHfinal.get(0));
    	match8.add(GroupGfinal.get(1));
    
    }
    public static void quarterfinals(){
    

    	
    	match9.add(match1.get(randint(0,match1.size()-1)));
    	match9.add(match2.get(randint(0,match2.size()-1)));
    	
    	
    	match10.add(match5.get(randint(0,match5.size()-1)));
    	match10.add(match6.get(randint(0,match6.size()-1)));

    	match11.add(match3.get(randint(0,match3.size()-1)));
    	match11.add(match4.get(randint(0,match4.size()-1)));
    
    	
    	match12.add(match7.get(randint(0,match7.size()-1)));
    	match12.add(match8.get(randint(0,match8.size()-1)));
 
    }
    public static void semifinals(){
    

    	
    	match13.add(match9.get(randint(0,match9.size()-1)));
    	match13.add(match10.get(randint(0,match10.size()-1)));
  
    	
    	match14.add(match11.get(randint(0,match11.size()-1)));
    	match14.add(match12.get(randint(0,match12.size()-1)));

    }
    public static void finals(){
    

    	
    	match15.add(match13.get(randint(0,match13.size()-1)));
    	match15.add(match14.get(randint(0,match14.size()-1)));
    
    	

    	winner=match15.get(randint(0,match15.size()-1));
    } 
    

}

class Goalie {
    public static int x,y,dir;
    public static double fr;
    public static Image[]picsLeft;
    public static Image[]picsRight;
    public static double incF=0.1;
 
 
    public static final int LEFT = 0, RIGHT = 1, WAIT = 5;
  
        
    public Goalie(int x, int y, String name1, String name2, int n){
        this.x=x;
        this.y=y;
   
    

        
        
        picsLeft = new Image[n];
        picsRight = new Image[n];        
        for(int i = 0; i<n; i++){
        	int j=i+1;
            picsLeft[i] = new ImageIcon("images/"+name1+"/"+name1+"00"+Integer.toString(j)+".png").getImage();
            if(j<=5) {
            	picsLeft[i] = picsLeft[i].getScaledInstance(92,204,Image.SCALE_SMOOTH);
            }
            else if(j==6) {
            	picsLeft[i] = picsLeft[i].getScaledInstance(200,168,Image.SCALE_SMOOTH);
            }
            else if(j==7) {
            	picsLeft[i] = picsLeft[i].getScaledInstance(208,104,Image.SCALE_SMOOTH);
            }
            else if(j==8) {
            	picsLeft[i] = picsLeft[i].getScaledInstance(196,60,Image.SCALE_SMOOTH);
            }
            picsRight[i] = new ImageIcon("images/"+name2+"/"+name2+"00"+Integer.toString(j)+".png").getImage();
			if(j<=5) {
            	picsRight[i] = picsRight[i].getScaledInstance(92,204,Image.SCALE_SMOOTH);
			}
            else if(j==6) {
            	picsRight[i] = picsRight[i].getScaledInstance(200,168,Image.SCALE_SMOOTH);
            }
            else if(j==7) {
            	picsRight[i] = picsRight[i].getScaledInstance(208,104,Image.SCALE_SMOOTH);
            }
            else if(j==8) {
            	picsRight[i] = picsRight[i].getScaledInstance(196,60,Image.SCALE_SMOOTH);
            }
        }
    }

    
    public void move(int mx) { 
        if(mx>x){
            dir = RIGHT;
            if(x!=mx) {
             	x+=10;
            }
            if(x>mx){
             	x=mx;
            }
        }
        else if(mx<x){
            dir = LEFT;
            if(x!=mx){
             	x-=10;
            }
            if(x<mx){
            	x=mx;
            }
        }
    }
    
    public static int aiGoalie(){
    	if(randint(0,1)==0){
    		return(randint(510,870));
    	}
    	else{
    		return(randint(150,510));
    	}
    }

    

	public void draw(Graphics g){
        if(GamePanel.goalieDive==true) {
             fr+=incF;
        }
        if((int)fr>=8){
        	GamePanel.goalieDive=false;
            fr=0;
            
        }
        if(dir == RIGHT){
            if(fr<7) {
                g.drawImage(picsRight[(int)fr], x-45, y, null);
            }
            if(fr>=7) {
                g.drawImage(picsRight[(int)fr], x-45, y+150, null);
            }
        }
        else if(dir == LEFT){
            if(fr<7) {
                g.drawImage(picsLeft[(int)fr], x-45, y, null);
            }
            if(fr>=7) {
                g.drawImage(picsLeft[(int)fr], x-45, y+150, null);
            }
        }
 	}
 	
 	public static int randint(int low, int high){
  		return (int)(Math.random()*(high-low+1)+low);
 	}
}

