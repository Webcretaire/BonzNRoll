import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Jeu extends JFrame implements ActionListener, MouseListener, KeyListener, MouseMotionListener
{
	/* Déclaration des variables, celles dont l'utilité n'est pas évidente seront
	 * détaillées ci-dessous ou lors de leur affectation.
	 */
	
	// --- Les JPanel sont utilisés pour contenir les boutons du menu --- 
	JPanel backgroundPan = new JPanel();
	JPanel menuPrincipalPan = new JPanel();
	JPanel menuPausePan = new JPanel();
	JPanel levelPan = new JPanel();
	JPanel normalPan = new JPanel();
	JPanel wallpaperPan = new JPanel();
	
	// --- Images utilisées comme buffer pour éviter le clignotement, avec leurs Graphics associés ---
	Image imageBufferNormal;
	Image imageBufferTotal;
	Graphics buffer;
	Graphics bufferTotal;
	
	// --- Toolkit servant à récupérer les images des objets du jeu ---
	Toolkit T=Toolkit.getDefaultToolkit();
	
	// --- Images des différents objets du jeu ---
	Image background = T.getImage("images/background.jpg");
	Image backgroundNormal = T.getImage("images/backgroundNormal.jpg");
	Image backgroundEE = T.getImage("images/backgroundEE/K/h/e/o/p/s/Total.jpg");
	Image titre = T.getImage("images/bonzNRoll.png");
	Image imageGagne = T.getImage("images/niveauGagne.png");
	Image imagePerdu = T.getImage("images/niveauPerdu.png");
	Image levelSelect = T.getImage("images/levelSelect.png");
	Image imageAide = T.getImage("images/imageAide.jpg");
	
	// --- Images des boutons des menus ---
	ImageIcon startBouton = new ImageIcon("images/start.png");
	ImageIcon modeInfiniBouton = new ImageIcon("images/modeInfini.png");
	ImageIcon exitBouton = new ImageIcon("images/exit.png");
	ImageIcon mpBouton = new ImageIcon("images/mp.png");
	ImageIcon nsBouton = new ImageIcon("images/ns.png");
	ImageIcon rpBouton = new ImageIcon("images/rp.png");
	ImageIcon helpBouton = new ImageIcon("images/help.png");
	ImageIcon[] levelsBouton;
	
	// --- Variables utilisées dans le jeu (certaines sont des constantes) ---
	int nombreLevels = 25;
	int lvlDebloques;
	float ratio = 4.0f/3.0f; // Ratio de la partie "interactive" du jeu
	boolean afficherMenuPrincipal = true;
	boolean afficherMenuPause = false;
	boolean afficherLevel = false;
	boolean afficherAide = false;
	boolean pausePossible = false;
	int nLevel = 0;
	int nbBriques = 0;
	int ballesCrees = 0; // Nombre de balles crées dans le tableau de balles
	float ratioX;
	float ratioY;
	float vitesseBarre;
	int xPrevBarre;
	int xPrevSouris;
	boolean isMoving = false;
	boolean gauche;
	int positionBarreP;
	boolean ballStuck = true;
	int collisionBrique = -1;
	int saveX;
	int saveY;
	int centreBaX;
	int centreBaY;
	int[] lastTouch;
	float saveSpeed;
	int collisionBriqueBis = -1;
	boolean collisionBarre = false;
	float angleDeplacement;
	int vies;
	int pointsI;
	int decompteI;
	int highscore;
	int sBarre = 17; // Dernier niveau où il n'y a qu'une barre, la deuxième barre apparaît donc au niveau 18
	boolean niveauGagne = false;
	boolean niveauPerdu = false;
	boolean levelFini = false;
	boolean affInfini = false;
	int nbIncassable;
	int nbBriquesActives;
	boolean noBalls;
	boolean malPlace;
	boolean tousLvlGagnes = false;
	int numeroBalle;
	boolean transitionInfini;
	double randomX;
	double randomY;
	
	// --- JButton présents dans les menus ---
	JButton start = new JButton(startBouton);
	JButton modeInfini = new JButton(modeInfiniBouton);
	JButton exit = new JButton(exitBouton);
	JButton mp = new JButton(mpBouton);
	JButton mp2 = new JButton(mpBouton);
	JButton ns = new JButton(nsBouton);
	JButton rp = new JButton(rpBouton);
	JButton help = new JButton(helpBouton);
	
	JButton[] levels = new JButton[nombreLevels];
	
	// --- Tous les objets du jeu ---
	Brique[] briques;
	Objet barre;
	Objet barre2;
	Objet balles[];
	Objet murG;
	Objet murH;
	Objet murD;
	Objet murI;
	
	// --- Timer qui rythme les actions dans le jeu ---
	int tempsTimer = 30; // Période du timer (correspond à une fréquence de rafraîchissement d'environ 33 images par secondes)
	Timer timer;
	
	// --- Sons avec leur chemin d'accès ---
	URL urlMusiqueFond = Jeu.class.getResource("sons/fond.wav");
	AudioClip musiqueFond = Applet.newAudioClip(urlMusiqueFond);
	
	URL urlPut = Jeu.class.getResource("sons/put.wav");
	AudioClip put = Applet.newAudioClip(urlPut);
	
	URL urlPitch = Jeu.class.getResource("sons/pitch.wav");
	AudioClip pitch = Applet.newAudioClip(urlPitch);
	
	/**
	 * Constructeur qui initalise tous les paramètres du programme (et les objets qui ont besoin de l'être au lancement,
	 * comme les JPanel et les JButton par exemple)
	 */
	public Jeu() 
	{
		setIconImage(new ImageIcon("images/bonzai.png").getImage());
		setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		
		// --- Affectation des paramètres des JPanel ---
		
		backgroundPan.setLayout(null);
		menuPrincipalPan.setLayout(null);
		menuPausePan.setLayout(null);
		
		// Définit la taille de la zone "interractive" du jeu en fonction de la taille de l'écran de l'utilisateur.
        if(this.getWidth() > this.getHeight()*ratio)
        	menuPrincipalPan.setBounds((int)(this.getWidth()/2.0 - (this.getHeight() * ratio)/2.0), 0, (int)(this.getHeight()*ratio), this.getHeight());
        else
        	menuPrincipalPan.setBounds(0, (int)(this.getHeight()/2.0 - (this.getWidth()/(ratio*2.0))), this.getWidth(), (int)(this.getWidth()/ratio));
        backgroundPan.setBackground(Color.BLACK);
        setContentPane(backgroundPan);
        
        menuPrincipalPan.setBackground(new Color(255,0,0,0));
        
        menuPausePan.setBackground(new Color(0,0,255,0));
        menuPausePan.setBounds(menuPrincipalPan.getBounds());
        
        levelPan.setBounds(menuPrincipalPan.getBounds());
        levelPan.setBackground(new Color(0,255,0,0));
        
        normalPan.setBounds(menuPrincipalPan.getBounds());
        normalPan.setBackground(new Color(0,255,0,0));
        
        imageBufferNormal = new BufferedImage(1200,900,BufferedImage.TYPE_INT_ARGB);
        buffer = imageBufferNormal.getGraphics();
        imageBufferTotal = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_ARGB);
        bufferTotal = imageBufferTotal.getGraphics();
        
        // --- Affectation des paramètres des JButton ---
        
        start.setSize(startBouton.getIconWidth(), startBouton.getIconHeight());
		start.setLocation((int)(menuPrincipalPan.getWidth()/2.0 - start.getWidth()/2.0), 250);
		start.setRolloverIcon(new ImageIcon("images/startHover.png")); // Image au survol du bouton
        start.setBorderPainted(false);
        start.addActionListener(this);
        
        help.setSize(helpBouton.getIconWidth(), helpBouton.getIconHeight());
        help.setLocation((int)(menuPrincipalPan.getWidth()/2.0 - help.getWidth()/2.0), 450);
        help.setRolloverIcon(new ImageIcon("images/helpHover.png"));
        help.setBorderPainted(false);
        help.addActionListener(this);
        
        modeInfini.setSize(modeInfiniBouton.getIconWidth(),modeInfiniBouton.getIconHeight());
        modeInfini.setLocation((int)(menuPrincipalPan.getWidth()/2.0 - modeInfini.getWidth()/2.0), 350);
        modeInfini.setRolloverIcon(new ImageIcon("images/modeInfiniHover.png"));
        modeInfini.setBorderPainted(false);
        modeInfini.addActionListener(this);
        
        exit.setSize(exitBouton.getIconWidth(),exitBouton.getIconHeight());
        exit.setLocation((int)(menuPrincipalPan.getWidth()/2.0 - exit.getWidth()/2.0), 550);
        exit.setRolloverIcon(new ImageIcon("images/exitHover.png"));
        exit.setBorderPainted(false);
        exit.addActionListener(this);
        
        mp.setSize(mpBouton.getIconWidth(),mpBouton.getIconHeight());
        mp.setLocation((int)(menuPrincipalPan.getWidth()/2.0 - mp.getWidth()/2.0), 550);
        mp.setRolloverIcon(new ImageIcon("images/mpHover.png"));
        mp.setBorderPainted(false);
        mp.addActionListener(this);
        
        ns.setSize(nsBouton.getIconWidth(),nsBouton.getIconHeight());
        ns.setLocation((int)(menuPrincipalPan.getWidth()/2.0 - ns.getWidth()/2.0), 450);
        ns.setRolloverIcon(new ImageIcon("images/nsHover.png"));
        ns.setBorderPainted(false);
        ns.addActionListener(this);
        
        rp.setSize(rpBouton.getIconWidth(),rpBouton.getIconHeight());
        rp.setLocation((int)(menuPrincipalPan.getWidth()/2.0 - rp.getWidth()/2.0), 450);
        rp.setRolloverIcon(new ImageIcon("images/rpHover.png"));
        rp.setBorderPainted(false);
        rp.addActionListener(this);
        
        /* Ici on lit depuis le fichier de sauvegarde combien de niveaux ont été débloqués et 
         * quel est le record du mode infini.
         */
        try 
		{
		    BufferedReader fichier = new BufferedReader(new FileReader("save.BAR"));

		    lvlDebloques = Integer.parseInt(fichier.readLine());
		    highscore = Integer.parseInt(fichier.readLine());
		    
		    fichier.close();
		} 
		catch (Exception er) 
		{
			er.printStackTrace();
		}
        
        if(lvlDebloques >= nombreLevels)
        	tousLvlGagnes = true;
        
        levelsBouton = new ImageIcon[nombreLevels]; // Correspond aux images des boutons de sélection des niveaux
        
        for(int i = 0; i<levels.length; i++)
    	{
        	// Si le niveau est débloqué alors le bouton est activé, sinon le niveau n'est pas accessible
        	
        	if(i<= lvlDebloques)
        	{
        		levelsBouton[i] = new ImageIcon("images/levels"+(i)+".png");
        		levels[i] = new JButton(levelsBouton[i]);
        		levels[i].setRolloverIcon(new ImageIcon("images/levels"+(i)+"Hover.png"));
        		levels[i].addActionListener(this);
        	}
        	else
        	{
        		levelsBouton[i] = new ImageIcon("images/levels"+(i)+"ND.png");
        		levels[i] = new JButton(levelsBouton[i]);
        	}
    		levels[i].setBounds(180+64*i - (int)(i/10.0) * 640,300 + (int)(i/10.0)*64,levelsBouton[i].getIconWidth(),levelsBouton[i].getIconHeight());
    		levels[i].setBorderPainted(false);
    		
    		levelPan.add(levels[i]);
    	}
        
        menuPrincipalPan.add(start);
        menuPrincipalPan.add(modeInfini);
        menuPrincipalPan.add(help);
        menuPrincipalPan.add(exit);
        menuPausePan.add(mp);

        wallpaperPan.setBounds(0,0,backgroundPan.getWidth(),backgroundPan.getHeight());
     
        backgroundPan.add(wallpaperPan);
        backgroundPan.add(menuPrincipalPan);
        repaint();
        
        // --- Définition des écouteurs qui permettront l'interaction entre l'utilisateur et le programme ---
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        
        ratioX = (float) ((1200.0/normalPan.getWidth()));
        ratioY = (float) ((900.0/normalPan.getHeight()));
        
        musiqueFond.loop(); // On lance la musique de fond
	}
	
	@Override
	public void paint(Graphics g)
	{
		try
		{
			/* bufferTotal est le buffer qui sera au final affiché sur la fenêtre,
			 * il comprend à la fois l'arrière plan et buffer
			 */
			bufferTotal.drawImage(background, 0, 0, getWidth(), getHeight(), this);
			/* Même si cela nécessite probablement beaucoup de ressources, quand on est dans les menus il 
			 * faut recréer cette image à chaque fois pour éviter que les éléments semi-transparents se 
			 * superposent les uns sur les autres à chaque repaint, ainsi on repart d'un image vierge à chaque fois.
			 */
			if(nLevel <= 0 && !affInfini)
			{
				imageBufferNormal = new BufferedImage(1200,900,BufferedImage.TYPE_INT_ARGB);
				// buffer correspond uniquement à la partie "interactive" (centrale) du jeu.
				buffer = imageBufferNormal.getGraphics();
			}
			if(nLevel>0 || affInfini) // Si une partie est lancée
			{
				// On affiche le fond, puis tous les objets actifs
				if(pointsI > 5000 || (highscore > 5000 && affInfini))
					buffer.drawImage(backgroundEE, 0, 0, this); // => Petit bonus pour les plus persévérants en mode infini
				else
					buffer.drawImage(backgroundNormal, 0, 0, this); // => Pour les autres
				buffer.drawImage(murG.image, murG.x, murG.y, this);
				buffer.drawImage(murD.image, murD.x, murD.y, this);
				
				if(murI.actif && (decompteI >50 || (decompteI <= 50 && decompteI%10 < 5)))
					buffer.drawImage(murI.image, murI.x, murI.y, this);
				buffer.drawImage(barre.image, barre.x, barre.y, this);
				if((nLevel > sBarre || transitionInfini) && barre2 != null)
					buffer.drawImage(barre2.image, barre2.x, barre2.y, this);
				else
					buffer.drawImage(murH.image, murH.x, murH.y, this);
				for(int j = 0; j<ballesCrees; j++)
				{
					if(balles[j].actif)
					buffer.drawImage(balles[j].image, balles[j].x, balles[j].y, this);
				}
				for(int i = 0; i<briques.length; i++)
				{
					if(briques[i]!=null)
					{
						if(briques[i].actif)
							buffer.drawImage(briques[i].image, briques[i].x, briques[i].y, this);
						else if(briques[i].tempsE>0 && !niveauGagne && !niveauPerdu)
							buffer.drawImage(briques[i].imagesD[8-briques[i].tempsE], briques[i].x, briques[i].y, this);
					}
				}			
				
				if(afficherMenuPause)
				{
					// On dessine les lignes et le fond noir transparent (qui se répètent sur tous les menus)
					buffer.setColor(new Color(0,0,0,255));
					buffer.fillRect(0, 0, 5, imageBufferNormal.getHeight(this));
					buffer.fillRect(imageBufferNormal.getWidth(this)-5, 0, imageBufferNormal.getWidth(this), imageBufferNormal.getHeight(this));
					buffer.setColor(new Color(0,0,0,128));
					buffer.fillRect(0, 0, imageBufferNormal.getWidth(this), imageBufferNormal.getHeight(this));
					
					// Le menu "pause" sert aussi de menu "niveau suivant" et "game over" du fait de leur grande ressemblance
					if(!niveauGagne && !niveauPerdu)
						buffer.drawImage(titre, (int)(imageBufferNormal.getWidth(this)/2.0 - titre.getWidth(this)/2.0), 75, this);
					else if(niveauPerdu)
					{
						buffer.drawImage(imagePerdu, (int)(imageBufferNormal.getWidth(this)/2.0 - imagePerdu.getWidth(this)/2.0), 75, this);
						rp.repaint();
					}
					else if(niveauGagne)
					{
						buffer.drawImage(imageGagne, (int)(imageBufferNormal.getWidth(this)/2.0 - imageGagne.getWidth(this)/2.0), 75, this);
						ns.repaint();
					}
					mp.repaint();
				}
				
				buffer.setColor(Color.WHITE);
				buffer.setFont(new Font("Calibri", Font.PLAIN, 25));
				if(affInfini)
					buffer.drawString("Vies : "+vies +" Points : "+ pointsI + " Highscore : "+highscore, 10, 27);
				else
					buffer.drawString("Vies : "+vies, 10, 25);
				
				bufferTotal.drawImage(imageBufferNormal, normalPan.getX(),normalPan.getY(), normalPan.getWidth(), normalPan.getHeight(), this);
			}
			
			/* Contrairement à ce qu'on pourrait penser, 
			 * afficherLevel veut dire "afficher le menu de sélection des niveaux" et pas "afficher le niveau"
			 */
			else if(afficherLevel) 
			{
				buffer.setColor(new Color(0,0,0,255));
				buffer.fillRect(0, 0, 5, imageBufferNormal.getHeight(this));
				buffer.fillRect(imageBufferNormal.getWidth(this)-5, 0, imageBufferNormal.getWidth(this), imageBufferNormal.getHeight(this));
				buffer.setColor(new Color(0,0,0,128));
				buffer.fillRect(0, 0, imageBufferNormal.getWidth(this), imageBufferNormal.getHeight(this));
				buffer.drawImage(levelSelect, (int)(imageBufferNormal.getWidth(this)/2.0 - levelSelect.getWidth(this)/2.0), 75, this);
	
				for(int i = 0; i<levels.length; i++)
		    	{
		    		levels[i].repaint();
		    	}
				
				bufferTotal.drawImage(imageBufferNormal, normalPan.getX(),normalPan.getY(), normalPan.getWidth(), normalPan.getHeight(), this);
			}
			
			else if(afficherMenuPrincipal)
			{
				if(afficherAide)
				{
					buffer.drawImage(imageAide, 0, 0, imageBufferNormal.getWidth(this), imageBufferNormal.getHeight(this), this);
				}
				else
				{
					buffer.setColor(new Color(0,0,0,255));
					buffer.fillRect(0, 0, 5, imageBufferNormal.getHeight(this));
					buffer.fillRect(imageBufferNormal.getWidth(this)-5, 0, imageBufferNormal.getWidth(this), imageBufferNormal.getHeight(this));
					buffer.setColor(new Color(0,0,0,128));
					buffer.fillRect(0, 0, imageBufferNormal.getWidth(this), imageBufferNormal.getHeight(this));
					buffer.drawImage(titre, (int)(imageBufferNormal.getWidth(this)/2.0 - titre.getWidth(this)/2.0), 75, this);
				}
				bufferTotal.drawImage(imageBufferNormal, normalPan.getX(),normalPan.getY(), normalPan.getWidth(), normalPan.getHeight(), this);
			}
			
			g.drawImage(imageBufferTotal, 0, 0, this);
			
			/* repaint nécessaire car sinon les boutons ne s'affichent pas
			 * avant que l'utilisateur les aient survolés
			 */
			start.repaint();
			modeInfini.repaint();
		    exit.repaint();
		    help.repaint();
		    mp.repaint();
		    ns.repaint();
		    rp.repaint();
		}
		catch(Exception err){}
	}

	@Override
	public void keyPressed(KeyEvent e) 
	{
		switch(e.getKeyCode())
		{
			case 27: // Touche "Echap"
				// Si l'aide est affichée, on revient au menu principal
				if(afficherAide) 
				{
					afficherAide = false;
					menuPrincipalPan.add(start);
					menuPrincipalPan.add(modeInfini);
					menuPrincipalPan.add(help);
					menuPrincipalPan.add(exit);
					repaint();
				}
				// Si une partie est lancée, on met le jeu en pause
				if(pausePossible) 
				{
					afficherMenuPause = !afficherMenuPause;
					if(afficherMenuPause)
					{
						timer.stop();
						backgroundPan.add(menuPausePan);
						if(affInfini) // En mode infini :
						{
							try 
						    {
								/* Si le joueur met en pause le jeu, que ce soit pour quitter la partie ou 
								 * y revenir plus tard, le record est enregistré
								 */
						    	BufferedWriter fichier = new BufferedWriter(new FileWriter(new File("save.BAR")));
								fichier.write(lvlDebloques+"");
								fichier.newLine();
								fichier.write(highscore+"");
							    fichier.close();
							} 
						    catch (IOException e1) 
						    {
								e1.printStackTrace();
							}
						}
					}
					else
					{
						timer.start();
						backgroundPan.remove(menuPausePan);
					}
					repaint();
				}
				break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) 
	{
		// Lance la balle en début de partie (ou après avoir perdu une vie)
		if((nLevel >0 || affInfini) && ballStuck) 
		{
			balles[0].direction = (float) (Math.random()*(Math.PI/2.0) + (5.0*Math.PI/4.0));
			balles[0].vitesse = 12;
			ballStuck = false;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		// --- Gestion des actions à effectuer régulièrement ---
		if(e.getSource() == timer && barre != null)
		{
			/* La raquette a une vitesse nulle, l'appel de la fonction move a donc pour seul
			 * but de mettre à jour la position de sa hitbox et pas de la déplacer.
			 */
			barre.move();
			if((nLevel > sBarre || transitionInfini))
				barre2.move();
			/* Calcul de la vitesse de la raquette (en réalité on calcule la distance en pixels 
			 * entre deux positions successives de la raquette, en toute rigueur pour avoir réellement 
			 * une vitesse il aurait fallu diviser par le temps entre deux positions, mais ce n'est pas
			 * indispensable ici pour avoir un code fonctionnel)
			 */
			vitesseBarre = (float) (Math.abs(barre.BoxObjet.getX()-xPrevBarre));
			xPrevBarre = (int) barre.BoxObjet.getX();
			
			gestionBalle();
			gestionBriques();
			
			// Gestion du "mur bonus" qui doit disparaître après un certain temps.
			if(decompteI >0)
				decompteI--;
			if(decompteI <= 0 && murI.actif)
				murI.actif = false;
			
			// Quand le joueur gagne un niveau :
			if(levelFini && nLevel > 0)
			{
				timer.stop();
				niveauGagne = true;
				if(nLevel != nombreLevels)
					menuPausePan.add(ns);
				pausePossible = false;
				afficherMenuPause = true;
				backgroundPan.add(menuPausePan);
				// Si le niveau terminé est le dernier niveau débloqué, alors on débloque le suivant
				if(nLevel-1 == lvlDebloques)
				{
					lvlDebloques++;
					if(lvlDebloques >= nombreLevels)
			        	tousLvlGagnes = true;
					if(lvlDebloques<levels.length)
					{
						// On met à jour le bouton pour accéder au niveau débloqué
						levelPan.remove(levels[lvlDebloques]);
					
						levelsBouton[lvlDebloques] = new ImageIcon("images/levels"+(lvlDebloques)+".png");
		        		levels[lvlDebloques] = new JButton(levelsBouton[lvlDebloques]);
		        		levels[lvlDebloques].setRolloverIcon(new ImageIcon("images/levels"+(lvlDebloques)+"Hover.png"));
		        		levels[lvlDebloques].addActionListener(this);
		        		levels[lvlDebloques].setBounds(150+64*lvlDebloques,300,levelsBouton[lvlDebloques].getIconWidth(),levelsBouton[lvlDebloques].getIconHeight());
		        		levels[lvlDebloques].setBorderPainted(false);
		        		
		        		levelPan.add(levels[lvlDebloques]);
					}
				}
				try 
				{
					BufferedWriter fichier = new BufferedWriter(new FileWriter(new File("save.BAR")));
					fichier.write(lvlDebloques+"");
					fichier.newLine();
					fichier.write(highscore+"");
				    fichier.close();
			    } 
				catch (Exception err) 
				{
			      err.printStackTrace();
			    }
			}
			
			// Quand le joueur perd :
			if(vies <= 0 && (nLevel > 0 || affInfini))
			{
				timer.stop();
				niveauPerdu = true;
				menuPausePan.add(rp);
				pausePossible = false;
				afficherMenuPause = true;
				backgroundPan.add(menuPausePan);
				// Si on était en mode infini on enregistre le record.
				if(affInfini)
				{
				    try 
				    {
				    	BufferedWriter fichier = new BufferedWriter(new FileWriter(new File("save.BAR")));
						fichier.write(lvlDebloques+"");
						fichier.newLine();
						fichier.write(highscore+"");
					    fichier.close();
					} 
				    catch (IOException e1) 
				    {
						e1.printStackTrace();
					}
				    
				}
			}
			
			// Gestion du mode infini
			if(affInfini)
			{
				if(pointsI >= 200 && !transitionInfini)
				{
					transitionInfini = true;
					murH = null;
					if(!tousLvlGagnes)
						barre2 = new Objet("images/barre2.png",500,0,0,0);
					else
						barre2 = new Objet("images/barre2W.png",500,0,0,0);
				}
				// On met à jour le record s'il a été battu
				if(pointsI > highscore)
					highscore = pointsI;
				// Gestion de l'apparition aléatoire des briques
				int hasard = (int) (Math.random()*101);
				/* La vitesse d'apparition (probabilité d'apparition en toute rigueur) est une fonction du nombre
				 * de briques présentes, car on part du principe que moins il y a de briques plus il faut qu'elles 
				 * réapparaissent vite pour que le joueur ne se retrouve pas avec plus aucune brique à casser.
				 */
				int vitessePop = (int) (4 - nbBriquesActives/10.0);
				if(hasard <= vitessePop && vitessePop > 0)
				{
					hasard = (int) (Math.random()*101);
					int numeroBrique;
					numeroBrique = nbBriques;
					/* Par défaut on crée la nouvelle brique à la suite des autres dans le tableau de briques,
					 * mais on ne peut pas se contenter de ce système sinon le tableau serait très vite totalement
					 * rempli. Donc on teste si on ne peut pas plutôt remplacer une brique qui n'est plus active.
					 */
					for(int i = 0; i<nbBriques; i++)
					{
						if(briques[i].actif == false && briques[i].tempsE == 0)
							numeroBrique = i;
					}
					int typeB;
					int couleurB;
					// Gestion de la probabilité d'obtenir un certain type de brique
					if(hasard <=88)
					{
						typeB = 0;
						couleurB = (int)(Math.random()*6);
					}
					else if(hasard <= 95)
					{
						// Il faut restreindre le nombre de briques incassables
						if(nbIncassable <= 20)
						{
							typeB = 1;
							couleurB = 0;
							nbIncassable++;
						}
						else
						{
							typeB = 0;
							couleurB = (int)(Math.random()*6);
						}
					}
					else if(hasard <=97)
					{
						typeB = 2;
						couleurB = 0;
					}
					else
					{
						typeB = 3;
						couleurB = 0;
					}
					
					// On vérifie si la brique créée ne chevauche pas une autre brique
					do
					{
						malPlace = false;
						// Coordonnées de la brique (on s'assure qu'elle ne sera pas créée dans le mur de gauche)
						do
						{
							randomX = Math.random()*1009+36;
						}while(randomX - randomX%24 < 36);
						
						if(!transitionInfini)
							randomY = Math.random()*645+46;
						else
							randomY = Math.random()*530+161;
						briques[numeroBrique] = new Brique(typeB,(int)(randomX - randomX%24), (int)(randomY - randomY%23),couleurB);
						for(int j = 0; j<nbBriques; j++)
						{
							if(briques[j].Collision(briques[numeroBrique]) && j != numeroBrique)
								malPlace  = true;
						}
					}while(malPlace);
					nbBriquesActives++;
					// Si la brique créée a été rajoutée à la suite dans le tableau de briques (i.e. si elle n'en a pas remplacé une autre)
					if(numeroBrique == nbBriques)
						nbBriques++;
				}
			}			
			repaint();
		}
		// Vers le menu de sélection du niveau
		if(e.getSource() == start)
		{
			afficherMenuPrincipal = false;
			backgroundPan.remove(menuPrincipalPan);
			backgroundPan.add(levelPan);
			afficherLevel = true;
			repaint();
			requestFocusInWindow();
		}
		// Lance la partie en mode infini
		else if(e.getSource() == modeInfini)
		{
			afficherMenuPrincipal = false;
			backgroundPan.remove(menuPrincipalPan);
			affInfini = true;
			
			chargerNiveauInfini();
			repaint();
			requestFocusInWindow();
		}
		// Affiche l'aide
		else if(e.getSource() == help)
		{
			afficherAide = true;
			menuPrincipalPan.remove(start);
			menuPrincipalPan.remove(modeInfini);
			menuPrincipalPan.remove(help);
			menuPrincipalPan.remove(exit);
			repaint();
			requestFocusInWindow();
		}
		// Quitte le jeu
		else if(e.getSource() == exit)
			System.exit(0);
		// Vers le menu principal
		else if(e.getSource() == mp)
		{
			menuPausePan.remove(ns);
			pausePossible = false;
			afficherMenuPrincipal = true;
			backgroundPan.removeAll();
			backgroundPan.add(menuPrincipalPan);
			nLevel = 0;
			afficherLevel = false;
			affInfini = false;
			repaint();
			requestFocusInWindow();
		}
		// Vers le niveau suivant
		else if(e.getSource() == ns)
		{
			menuPausePan.remove(ns);
			if(nLevel < nombreLevels)
			{
				backgroundPan.removeAll();
				nLevel++;
				
				chargerNiveau(nLevel);
			}
		}
		// Nouvelle tentative après avoir perdu
		else if(e.getSource() == rp)
		{
			menuPausePan.remove(rp);
			backgroundPan.removeAll();
				
			if(!affInfini)
				chargerNiveau(nLevel);
			else
				chargerNiveauInfini();
		}
		// Sélection du niveau
		for(int i =  0; i < levels.length; i++)
		{
			if(e.getSource() == levels[i])
			{
				nLevel = i+1;
				chargerNiveau(nLevel);
			}
		}
	}

	/**
	 * Gère l'animation de destruction des briques.
	 */
	public void gestionBriques() 
	{
		for(int i = 0; i<nbBriques; i++)
		{
			if(briques[i].tempsE > 0)
				briques[i].tempsE--;
		}
	}

	/**
	 * Gère la trajectoire des balles (notamment les rebonds avec les autres objets)
	 */
	public void gestionBalle() 
	{
		for(int j = 0; j<ballesCrees; j++)
		{
			if(balles[j] != null && balles[j].actif)
			{
				// Evite qu'une balle puisse avoir une trajectoire parfaitement horizontale (avec une tolérance de pi/10)
				if(Math.abs(balles[j].direction - Math.PI) < Math.PI/20.0)
				{
					if(balles[j].y > 450)
						balles[j].direction += (float) (Math.PI/25.0);
					else
						balles[j].direction -= (float) (Math.PI/25.0);
				}
				// Idem verticalement
				if(Math.abs(balles[j].direction - Math.PI/2.0) < Math.PI/20.0)
				{
					if(balles[j].x > 600)
						balles[j].direction -= (float) (Math.PI/25.0);
					else
						balles[j].direction += (float) (Math.PI/25.0);
				}
					
				balles[j].move();
				
				// --- Rebond de la balle j ---
				
				/* C'est ici que le tableau lastTouch prend son importance :
				 * À chaque fois qu'on teste si la balle doit rebondir sur un objet, on teste si elle ne vient pas de rebondir
				 * sur ce même objet. Si c'est le cas on n'effectue pas le rebond. Ce système en apparence compliqué permet
				 * d'éviter que la balle reste coincée à l'intérieur d'un autre objet, ce qui pouvait arriver dans quelques 
				 * rares cas avant la mise en place de ce système (notamment quand la balle rencontrait une face latérale de la 
				 * raquette elle se mettait à rebondir à l'infini à l'intérieur de la raquette)
				 */
				
				// Collision avec une brique :
				for(int i = 0; i<nbBriques; i++)
				{
					if(balles[j].Collision(briques[i]) && briques[i].actif)
					{
						collisionBrique = i;
						if(lastTouch[j] != i)
						{	
							/* Actuellement la balle j chevauche la brique i, voire même est entièrement à l'intérieur de la brique i, car sa 
							 * trajectoire n'est pas continue, elle ne prend que des positions discrètes. Elle peut donc être à l'instant t en
							 * dehors de la brique i, et à l'instant t+1 complètement à contenue dans la brique i.
							 * Il est donc extrêmement difficile de savoir dans quel sens la balle doit repartir (on peut faire une prédiction
							 * plus ou moins valable mais qui ne fonctionnera pas dans tous les cas). On commence donc par faire "reculer" la 
							 * balle jusqu'à ce qu'elle soit tangente à la brique. Ainsi on pourra déterminer plus facilement quelle face de la
							 * brique a été touchée et donc comment effectuer le rebond.
							 */
							balles[j].direction = (float) ((balles[j].direction + Math.PI) % (2* Math.PI)); // On fait demi-tour ...
							saveSpeed = balles[j].vitesse; // ... On "sauvegarde" la vitesse de la balle ...
							balles[j].vitesse = 4; // ... On ralentit (pas besoin d'aller jusqu'à 1 sinon la balle n'avance plus du tout)  ...		
							
							while(balles[j].Collision(briques[i]))
							{
								balles[j].move(); // ... On recule ...
							}
							balles[j].direction = (float) ((balles[j].direction + Math.PI) % (2* Math.PI)); // ... On repart en marche avant ...
							balles[j].vitesse = saveSpeed; // ... Et on restaure la vitesse
							
							/* On va faire des tests sur le centre de la balle plutôt que sur le coin supérieur gauche,
							 * ce sera plus précis
							 */
							centreBaX = (int) (balles[j].x+balles[j].l/2.0);
							centreBaY = (int) (balles[j].y+balles[j].h/2.0);
							
							// Si la balle est dans la partie supérieure de la brique
							if(centreBaY < briques[i].y+briques[i].h/2.0)
							{
								if(centreBaX+5 < briques[i].x  || centreBaX-5 > briques[i].x + briques[i].l )
								{
									// Rebond sur une face verticale
									balles[j].direction = (float) (Math.PI - balles[j].direction);
									if(balles[j].direction < 0)
										balles[j].direction += 2* Math.PI;
								}
								else
								{
									// Rebond sur une face horizontale
									balles[j].direction *= -1;
									balles[j].direction += 2*Math.PI;
								}
							}
							// Sinon dans la partie inférieure de la brique
							else
							{
								if(centreBaX+5 < briques[i].x || centreBaX-5 > briques[i].x + briques[i].l)
								{
									balles[j].direction = (float) (Math.PI - balles[j].direction);
									if(balles[j].direction < 0)
										balles[j].direction += 2* Math.PI;
								}
								else
								{
									balles[j].direction *= -1;
									balles[j].direction += 2*Math.PI;
								}
							}
						lastTouch[j] = i;
						}
					}
				}
				// Collision avec le mur gauche
				if(balles[j].Collision(murG) && lastTouch[j] != 500)
				{
					lastTouch[j] = 500;
					put.play();
					balles[j].direction = (float) (Math.PI - balles[j].direction);
					if(balles[j].direction < 0)
						balles[j].direction += 2* Math.PI;
					// Utile dans très peu de cas, corrige un bug qui arrive dans quelques très rares cas 
					if(balles[j].direction > Math.PI/2.0 && balles[j].direction < 3.0*Math.PI/2.0)
						balles[j].direction = 0f;
				}
				// Collision avec le mur droit
				if(balles[j].Collision(murD) && lastTouch[j] != 550)
				{
					lastTouch[j] = 550;
					put.play();
					balles[j].direction = (float) (Math.PI - balles[j].direction);
					if(balles[j].direction < 0)
						balles[j].direction += 2* Math.PI;	
					if(balles[j].direction < Math.PI/2.0 || balles[j].direction > 3.0*Math.PI/2.0)
						balles[j].direction = (float) Math.PI;
				}
				// Collision avec le mur supérieur (s'il existe)
				if(murH != null && balles[j].Collision(murH) && lastTouch[j] != 600)
				{
					lastTouch[j] = 600;
					put.play();
					balles[j].direction *= -1;
					balles[j].direction += 2*Math.PI;
					if(balles[j].direction > Math.PI)
						balles[j].direction = (float) (Math.PI/2.0);
				}
				// Collision avec le mur bonus (s'il est actif)
				if(balles[j].Collision(murI) && murI.actif && lastTouch[j] != 650)
				{
					lastTouch[j] = 650;
					put.play();
					balles[j].direction *= -1;
					balles[j].direction += 2*Math.PI;
					balles[j].vitesse += balles[j].vitesse*0.1;
				}
				// Rebond sur la raquette inférieure
				if(balles[j].Collision(barre) && lastTouch[j] != 300)
				{
					lastTouch[j] = 300;
					put.play();
					balles[j].direction *= -1;
					balles[j].direction += 2*Math.PI;
					balles[j].move();
					
					// Modification de la trajectoire de la balle,
					angleDeplacement = vitesseBarre *0.02f;
					if(angleDeplacement > Math.PI/4.0)
						angleDeplacement = (float) (Math.PI/4.0); // => (Pour éviter des valeurs aberrantes)
					if(gauche)
						balles[j].direction -= angleDeplacement;
					if(!gauche)
						balles[j].direction += angleDeplacement;
					// et de sa vitesse.
					balles[j].vitesse+= ((vitesseBarre - 5)/100.0)*balles[j].vitesse;
					if(balles[j].vitesse < 5)
						balles[j].vitesse = 5f;
					if(balles[j].vitesse > 15)
						balles[j].vitesse = 15f;
					balles[j].direction %= 2*Math.PI;
					// Evite quelques bugs, la balle pouvait parfois aller vers le bas même après le rebond sur la barre inférieure
					if(balles[j].direction < Math.PI/2.0)
						balles[j].direction = (float) (2*Math.PI-Math.PI/10.0);
					else if(balles[j].direction < Math.PI)
						balles[j].direction = (float)(Math.PI + Math.PI/10.0);
				}
				
				// Si la raquette supérieure existe (fonctionnement similaire à celui de la raquette inférieure)
				else if ((nLevel > sBarre || transitionInfini) && balles[j].Collision(barre2) && lastTouch[j] != 400)
				{
					lastTouch[j] = 400;
					put.play();
					balles[j].direction *= -1;
					balles[j].direction += 2*Math.PI;
					balles[j].move();
					
					angleDeplacement = vitesseBarre *0.02f;
					if(angleDeplacement > Math.PI/4.0)
						angleDeplacement = (float) (Math.PI/4.0);
					if(gauche)
						balles[j].direction += angleDeplacement;
					if(!gauche)
						balles[j].direction -= angleDeplacement;
					balles[j].vitesse+= ((vitesseBarre - 5)/100.0)*balles[j].vitesse;
					if(balles[j].vitesse < 5)
						balles[j].vitesse = 5f;
					if(balles[j].vitesse > 15)
						balles[j].vitesse = 15f;
					balles[j].direction %= 2*Math.PI;
					if(balles[j].direction > Math.PI-Math.PI/10.0 && balles[j].direction <  3.0*Math.PI/2.0)
						balles[j].direction = (float)(Math.PI/10.0);
					else if(balles[j].direction >= 3.0*Math.PI/2.0)
						balles[j].direction = 0;
				}
					
				// Gestion des actions à effectuer en fonction du type de brique rencontrée (si la collision a eu lieu avec un brique)
				else if(collisionBrique >= 0)
				{
					// Si la brique n'est pas incassable, on la détruit
					if(briques[collisionBrique].type != 1)
					{
						pitch.play();
						briques[collisionBrique].tempsE = 8;
						briques[collisionBrique].actif = false;
						if(affInfini)
							nbBriquesActives--;
					}
					else
						put.play();
					
					// Différentes actions en fonction du type de brique détruite
					if(briques[collisionBrique].type == 0 && affInfini)
						pointsI += 10;
						
					if(briques[collisionBrique].type == 2)
					{
						if(ballesCrees < balles.length-1)
						{
							for(int x = 1; x<= 2; x++) // On crée 2 balles
							{
								numeroBalle = ballesCrees;
								for(int i = 0; i<ballesCrees; i++)
								{
									if(balles[i].actif == false)
										numeroBalle = i;
								}
								balles[numeroBalle] = new Objet("images/balle.png",balles[j].x,balles[j].y,(float) (Math.random()*2*Math.PI),(float) (balles[j].vitesse + 2*(Math.random()-0.5)));
								if(numeroBalle == ballesCrees)
									ballesCrees++;
							}
						}
					}
					else if(briques[collisionBrique].type == 3)
					{
						if(decompteI == 0)
						{
							// S'il n'y a pas de mur en haut, alors il y a une chance sur deux pour que le mur bonus aille en haut.
							if((nLevel > sBarre || transitionInfini) && Math.random()<0.5)
							{
								murI.y = 0;
								try 
								{
									murI.image = ImageIO.read(new File("images/murInvincibilite2.png"));
								} catch (IOException e) 
								{
									e.printStackTrace();
								}
							}
							else
							{
								murI.y = imageBufferNormal.getHeight(this)-murI.h;
								try 
								{
									murI.image = ImageIO.read(new File("images/murInvincibilite.png"));
								} catch (IOException e) 
								{
									e.printStackTrace();
								}
							}
							murI.move();
						}
						murI.actif = true;
						decompteI = 350;
					}
					
					// Vérification du nombre de briques restantes à casser
					levelFini = true;
					for(int i = 0; i < nbBriques; i++)
					{
						if(briques[i].type == 0 && briques[i].actif)
							levelFini = false;
					}
				}

				balles[j].direction %= 2*Math.PI;
			
				// Si la balle sort de l'écran
				if(balles[j].x < 0 || balles[j].x > imageBufferNormal.getWidth(this) || balles[j].y < 0 || balles[j].y > imageBufferNormal.getHeight(this))
				{
					balles[j].vitesse = 0;
					balles[j].actif = false;
					// Vérification du nombre de balles restantes
					noBalls = true;
					for(int i = 0; i < ballesCrees; i++)
					{
						if(balles[i].actif)
							noBalls = false;
					}
					// S'il n'en reste plus aucune :
					if(noBalls)
					{
						vies--;
						balles[0] = new Objet("images/balle.png",0,0,0,0);
						balles[0].x = (int) (barre.x + barre.l/2.0 - balles[j].l/2.0);
						balles[0].y = barre.y - balles[j].h;
						ballStuck = true;
						noBalls = false;
					}
				}
				
				// On réinitialise les variables pour la prochaine collision
				collisionBrique = -1;
				collisionBriqueBis = -1;
				collisionBarre = false;
			}
		}
	}

	/**
	 * Crée une nouvelle instance du programme.
	 * @param args Paramètre non utilisé.
	 */
	public static void main(String[] args) 
	{
		Jeu jeu = new Jeu();
	}
	
	/**
	 * Initialise les différentes variables et objets au lancement d'une partie. 
	 */
	public void setObjects()
	{
		transitionInfini = false;
		pointsI = 0;
		levelFini = false;
		vies = 3;
		niveauGagne = false;
		niveauPerdu = false;
		afficherMenuPause = false;
		ballStuck = true;
		pausePossible = true;
		
		if(!tousLvlGagnes)
			barre = new Objet("images/barre.png",500,850,0,0);
		else
			barre = new Objet("images/barreW.png",500,850,0,0);
		barre2 = null;
		murH = null;
		if((nLevel > sBarre || transitionInfini))
		{
			if(!tousLvlGagnes)
				barre2 = new Objet("images/barre2.png",500,0,0,0);
			else
				barre2 = new Objet("images/barre2W.png",500,0,0,0);
		}
		else
		{
			murH = new Objet("images/murH.png", 0, 0, 0, 0);
			murH.BoxObjet.height = 46;
		}
		ballesCrees = 0;
		/* On ne sait pas combien de balles sont susceptibles d'être présentes simultanément, 
		 * on prévoit donc un grand tableau pour les contenir.
		 */
		balles = new Objet[30];
		/* Le tableau lastTouch est directement lié à celui des balles. En effet la balle d'indice x dans le tableau
		 * balles est associé à un entier stocké à l'indice x du tableau lastTouch. Cet entier correspond au dernier
		 * objet que la balle x a touché (cette information sera utile pour éviter les problèmes de rebond).
		 */
		lastTouch = new int[balles.length];
		// On l'initialise à une valeur improbable en fonctionement normal
		for(int i = 0; i<lastTouch.length; i++)
			lastTouch[i] = 999;
		balles[0] = new Objet("images/balle.png",0,0,0,0);
		ballesCrees++;
		murG = new Objet("images/murG.png", 0, 0, 0, 0);
		murD = new Objet("images/murD.png", imageBufferNormal.getWidth(this)-(new ImageIcon("images/murD.png")).getIconWidth(), 0, 0, 0);
		murI = new Objet("images/murInvincibilite.png", murG.l, imageBufferNormal.getHeight(this)-(new ImageIcon("images/murInvincibilite.png")).getIconHeight(),0,0);
		murI.actif = false;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {}

	@Override
	public void mouseMoved(MouseEvent arg0) 
	{
		if((nLevel > 0 || affInfini) && barre !=null)
		{
			// Définit si la raquette se déplace vers la gauche ou la droite
			if(arg0.getX()<xPrevSouris)
				gauche = true;
			else
				gauche = false;
			
			// On gère le déplacement de la raquette en fonction de la position du curseur
			barre.x = (int)((arg0.getX()-normalPan.getX()) * ratioX -barre.l/2.0);
			// On restreint les mouvements possibles de la raquette
			if(barre.x < murG.l)
				barre.x = murG.l;
			if(barre.x > murD.x-barre.l)
				barre.x = murD.x-barre.l;
			// Si la première balle n'a pas encore été lancée, elle reste "accrochée" à la raquette
			if(ballStuck)
			{
				balles[0].x = (int) (barre.x + barre.l/2.0 - balles[0].l/2.0);
				balles[0].y = barre.y - balles[0].h;
			}
			// Si il y a 2 raquettes, celle du haut suit les mouvements horizontaux de celle du bas
			if((nLevel > sBarre || transitionInfini))
				barre2.x = barre.x;
			// On mémorise la position du curseur pour le calcul de vitesse de la raquette
			xPrevSouris = arg0.getX();
		}	
	}

	/**
	 * Initialise un niveau.
	 * @param n Le numero du niveau à charger
	 */
	public void chargerNiveau(int n)
	{
		afficherLevel = false;
		backgroundPan.remove(levelPan);
		setObjects();
		balles[0].x = (int) (barre.x + barre.l/2.0 - balles[0].l/2.0);
		balles[0].y = barre.y - balles[0].h;
		
		try 
		{
			// Compte le nombre de briques à créer
			LineNumberReader  lnr = new LineNumberReader(new FileReader(new File(n+".BARlvl")));
			lnr.skip(Long.MAX_VALUE);
			nbBriques = lnr.getLineNumber() + 1;
			lnr.close();
			
			briques = new Brique[nbBriques];
			
			String ligne ;
		    BufferedReader fichier = new BufferedReader(new FileReader(n+".BARlvl"));
		    
		    /* Chaque niveau est décrit dans un fichier *.BARlvl, chaque ligne correspondant à une brique. On parcourt donc le
		     * fichier en créant les briques correspondantes au fur et à mesure.
		     */
		    int j = 0;
		    while ((ligne = fichier.readLine()) != null)
		    {
		    	/* Découpe chaque ligne pour extraire les paramètres et les stocker dans un tableau.
		    	 * On peut noter qu'on ne fait aucune vérification sur la validité des données présentes dans les fichiers
		    	 * (notamment le fait qu'on ait bien des nombres) car ceux-ci sont créés par l'éditeur de niveau de manière
		    	 * automatique, donc ils sont forcément formatés correctement.
		    	 */
		    	String str[]=ligne.split(",");
		    	briques[j] = new Brique(Integer.parseInt(str[0]), Integer.parseInt(str[1]), Integer.parseInt(str[2]), Integer.parseInt(str[3]));
		    	j++;
		    }
		    
		    fichier.close();
		    
		    timer = new Timer(tempsTimer, this);
	        timer.start();
		} 
		catch (Exception er) 
		{
			er.printStackTrace();
		}
		repaint();
		requestFocusInWindow();
	}
	
	/**
	 * Initialise une partie en mode infini.
	 */
	public void chargerNiveauInfini() 
	{
		nbIncassable = 0;
		setObjects();
		balles[0].x = (int) (barre.x + barre.l/2.0 - balles[0].l/2.0);
		balles[0].y = barre.y - balles[0].h;
		
		/* Quand on charge un niveau, on sait exactement combien de briques doivent être créées, on peut donc ajuster la
		 * taille du tableau en conséquence. Ici on est obligé de prévoir un grand tableau car on ne sait pas combien de 
		 * briques seront présentes simultanément.
		 */
		briques = new Brique[100];
		// On génère quelques briques dès le début de la partie
		for(int i = 0; i < 10; i++)
		{
			int hasard = (int) (Math.random()*101);
			int typeB;
			int couleurB;
			if(hasard <=88)
			{
				typeB = 0;
				couleurB = (int)(Math.random()*6);
			}
			else if(hasard <= 95)
			{
				typeB = 1;
				couleurB = 0;
				nbIncassable++;
			}
			else if(hasard <=97)
			{
				typeB = 2;
				couleurB = 0;
			}
			else
			{
				typeB = 3;
				couleurB = 0;
			}
			boolean malPlace;
			do
			{
				malPlace = false;
				briques[i] = new Brique(typeB,(int)(Math.random()*1000+50), (int)(Math.random()*600+50),couleurB);
				for(int j = 0; j<i; j++)
				{
					if(briques[j].Collision(briques[i]))
						malPlace  = true;
				}
			}while(malPlace);
			nbBriques = 10;
			nbBriquesActives = 10;
		}
		
		timer = new Timer(tempsTimer, this);
        timer.start();
        
        repaint();
		requestFocusInWindow();
	}
}
