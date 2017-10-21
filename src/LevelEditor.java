import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class LevelEditor extends JFrame implements ActionListener, MouseListener, MouseMotionListener
{
	/* D�claration des variables, celles dont l'utilit� n'est pas �vidente seront
	 * d�taill�es ci-dessous ou lors de leur affectation.
	 */
	
	// --- Toolkit servant � r�cup�rer les images des objets du jeu ---
	Toolkit T=Toolkit.getDefaultToolkit();
	
	// --- Diff�rents JPanel, identifi�s par une nuance de gris diff�rente --- 
	JPanel background = new JPanel();
	JPanel barreMenu = new JPanel();
	JPanel previsu = new JPanel();
	
	// --- Graphics associ� au JPanel previsu --- 
	Graphics gPrevisu;
	
	// --- JButton qui permettent la manipulation de fichiers ---
	JButton nouveau = new JButton("Nouveau");
	JButton parcourir = new JButton("Parcourir");
	JButton save = new JButton("Enregistrer");
	
	// --- JComboBox qui permettent de sp�cifier le type de brique � placer ---
	JComboBox type;
	JComboBox couleur;
	
	// --- Fichier du niveau en cours d'�dition ---
	File level;
	
	// --- Image utilis�e comme buffer pour �viter le clignotement, avec son Graphics associ� ---
	BufferedImage imageBuffer;
	Graphics buffer;
	
	// --- Images fixes dans tous les niveaux ---
	Image wallpaper = T.getImage("images/backgroundNormal.jpg");
	Image murG = T.getImage("images/murG.png");
	Image murD = T.getImage("images/murD.png");
	Image murH = T.getImage("images/murH.png");
	
	// --- Variables et objets associ�es aux briques ---
	int nbBriques;
	Brique[] briques;
	Brique briqueTemp;
	boolean afficherBrique = false;
	
	// --- Variables num�riques utilis�es par le programme ---
	float ratioX;
	float ratioY;
	int xSouris;
	int ySouris;
	
	// --- Boite de dialogue de choix de fichier ---
	JFileChooser dialogue;
	
	/**
	 * Constructeur qui initalise tous les param�tres du programme (et les objets qui ont besoin de l'�tre au lancement,
	 * comme les JPanel et les JButton par exemple)
	 */
	public LevelEditor() 
	{
		// La fen�tre prend par d�faut toute la place disponible sur l'�cran
		GraphicsEnvironment graphicsEnvironment=GraphicsEnvironment.getLocalGraphicsEnvironment();
		setBounds(graphicsEnvironment.getMaximumWindowBounds());
		setLayout(null);
		previsu.setLayout(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		// --- Affectation des param�tres des JPanel ---
		
		setContentPane(background);
		background.setBackground(new Color(150,150,150));
		barreMenu.setBounds(0,0, background.getWidth(), 50);
		barreMenu.setBackground(new Color(100,100,100));
		previsu.setBounds((int)((background.getWidth()/2.0) - (background.getHeight()-50)*4.0/6.0 ),50, (int)((background.getHeight()-50)*4.0/3.0), background.getHeight()-50);
		previsu.setBackground(new Color(200,200,200));
		background.add(barreMenu);
		background.add(previsu);
		
		// --- Affectation des param�tres des JButton et JComboBox ---
		
		barreMenu.add(nouveau);
		nouveau.addActionListener(this);
		
		barreMenu.add(parcourir);
		parcourir.addActionListener(this);
		
		barreMenu.add(save);
		save.addActionListener(this);
		
		String[] listeTypes = {"standard", "incassable", "multi-balles", "invincibilit�"};
		type = new JComboBox(listeTypes);
		type.setPreferredSize(new Dimension(150,30));
		barreMenu.add(type);
		
		String[] listeCouleurs = {"jaune", "rose","bleu","orange","rouge","cyan","vert"};
		couleur = new JComboBox(listeCouleurs);
		couleur.setPreferredSize(new Dimension(150,30));
		barreMenu.add(couleur);
		
		// --- Initialisation du buffer ---
		
		gPrevisu  = previsu.getGraphics();
		
		imageBuffer = new BufferedImage(1200,900,BufferedImage.TYPE_INT_ARGB);
        buffer = imageBuffer.getGraphics();
        
        previsu.addMouseListener(this);
        previsu.addMouseMotionListener(this);
		
        // Rapport de r�duction (ou d'aggrandissement) entre la taille du niveau qui est fixe (1200*900) et la taille affich�e � l'�cran
        ratioX = (float) ((1200.0/previsu.getWidth()));
        ratioY = (float) ((900.0/previsu.getHeight()));
        
		repaint();
	}
	
	@Override
	public void paint(Graphics g)
	{
		barreMenu.setBounds(0,0, background.getWidth(), 50);
		barreMenu.doLayout();
		previsu.setBounds((int)((background.getWidth()/2.0) - (background.getHeight()-50)*4.0/6.0 ),50, (int)((background.getHeight()-50)*4.0/3.0), background.getHeight()-50);
		
		try
		{
			if(level != null && buffer != null)
			{
				buffer.drawImage(wallpaper, 0, 0, this);
				buffer.drawImage(murG, 0, 0, this);
				buffer.drawImage(murD, imageBuffer.getWidth()-murD.getWidth(this), 0, this);
				buffer.drawImage(murH, 0, 0, this);
				for(int i = 0; i<nbBriques; i++)
					buffer.drawImage(briques[i].image, briques[i].x, briques[i].y, this);
			}
			// Si l'utilisateur a enfonc� le bouton de la souris, on affiche un "brique temporaire" sous le pointeur
			if(afficherBrique)
			{
				buffer.drawImage(briqueTemp.image, briqueTemp.x, briqueTemp.y, this);
			}
			
			gPrevisu.drawImage(imageBuffer, 0, 0, previsu.getWidth(), previsu.getHeight(), this);
		}
		catch(NullPointerException e){}
	}

	/**
	 * Cr�e une nouvelle instance du programme.
	 * @param args Param�tre non utilis�.
	 */
	public static void main(String[] args) 
	{
		LevelEditor editeur = new LevelEditor();
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		// On se place dans le r�pertoire du programme et on cr�e une boite de dialogue pour manipuler les fichiers.
		if(e.getSource() == nouveau || e.getSource() == parcourir)
		{
			File repertoireCourant = null;
	        try {
	            repertoireCourant = new File(".").getCanonicalFile();
	        } catch(IOException err) {}
	        
	        dialogue = new JFileChooser(repertoireCourant);
		}	
	
		if(e.getSource() == nouveau)
		{
			// On affiche une bo�te de dialogue d'enregistrement
			dialogue.showSaveDialog(null);
			
			// On r�cup�re le fichier du niveau
			level = dialogue.getSelectedFile();
			
			try 
			{
				// On initialise le niveau avec toutes les briques disponibles
			    BufferedWriter fichier = new BufferedWriter(new FileWriter(level));
			    fichier.write(0+","+60+","+46+","+0);
			    fichier.newLine();
			    fichier.write(0+","+180+","+46+","+1);
			    fichier.newLine();
			    fichier.write(0+","+300+","+46+","+2);
			    fichier.newLine();
			    fichier.write(0+","+420+","+46+","+3);
			    fichier.newLine();
			    fichier.write(0+","+540+","+46+","+4);
			    fichier.newLine();
			    fichier.write(0+","+660+","+46+","+5);
			    fichier.newLine();
			    fichier.write(0+","+780+","+46+","+6);
			    fichier.newLine();
			    fichier.write(1+","+60+","+92+","+0);
			    fichier.newLine();
			    fichier.write(2+","+180+","+92+","+0);
			    fichier.newLine();
			    fichier.write(3+","+300+","+92+","+0);
			    fichier.close();
		    } 
			catch (Exception err) 
			{
		      err.printStackTrace();
		    }
			
		}
		
		if(e.getSource() == parcourir)
		{	
			// On affiche une bo�te de dialogue de choix de fichier
	        dialogue.showOpenDialog(null);
	        
	        // On r�cup�re le fichier du niveau
	        level = dialogue.getSelectedFile();
		}
		if(e.getSource() == nouveau || e.getSource() == parcourir)
		{
			try 
			{
				// Compte le nombre de briques � cr�er
				LineNumberReader  lnr = new LineNumberReader(new FileReader(level));
				lnr.skip(Long.MAX_VALUE);
				nbBriques = lnr.getLineNumber() + 1;
				lnr.close();
				
				/* On ne sait pas combien l'utilisateur va vouloir placer de briques, on est donc
				 * oblig�s de cr�er un tr�s grand tableau ce qui garantit que dans 99,9% des cas il
				 * sera assez grand pour contenir toutes les briques
				 */
				briques = new Brique[200];
				
				// On cr�e toutes les briques d�j� enregistr�es dans le niveau en parcourant le fichier correspondant.
				String ligne ;
			    BufferedReader fichier = new BufferedReader(new FileReader(level));
			    
			    int j = 0;
			    while ((ligne = fichier.readLine()) != null) 
			    {
			    	String str[]=ligne.split(",");
			    	if(str.length==4)
			    	{
			    		briques[j] = new Brique(Integer.parseInt(str[0]), Integer.parseInt(str[1]), Integer.parseInt(str[2]), Integer.parseInt(str[3]));
			    		j++;
			    	}
			    }
			    
			    fichier.close();
			} 
			catch (Exception er) 
			{
				er.printStackTrace();
			}
	        
	        repaint();
		}
		else if(e.getSource() == save)
		{
			try 
			{
				// On �crit dans le fichier du niveau les caract�ristiques des briques, ligne par ligne.
			    BufferedWriter fichier = new BufferedWriter(new FileWriter(level));

			    for(int i = 0; i<nbBriques;i++)
			    {
			    	if(briques[i].type == 0)
			    		fichier.write(briques[i].type+","+briques[i].x+","+briques[i].y+","+briques[i].couleur);
			    	else
			    		fichier.write(briques[i].type+","+briques[i].x+","+briques[i].y+","+0);
				    if(i!=nbBriques-1)
				    	fichier.newLine();
			    }

			    fichier.close();
			} 
			catch (Exception err) 
			{
			   err.printStackTrace();
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) 
	{
		// D�clench� au clic droit de la souris
		if(arg0.getButton() == MouseEvent.BUTTON3) 
		{
			for(int i = nbBriques-1; i>=0; i--)
			{
				// Teste si la souris est dans une brique
				if((int)(arg0.getX()*ratioX)>briques[i].x && (int)(arg0.getX()*ratioX)<briques[i].x + briques[i].l && (int)(arg0.getY()*ratioY)>briques[i].y && (int)(arg0.getY()*ratioY)<briques[i].y + briques[i].h)
				{
					// On cr�e un tableau de briques qui contient une case de moins que le nombre de briques cr��es.
					Brique[] briquesTemp = new Brique[nbBriques - 1];
					// On y ajoute toutes les briques sauf celle sur laquelle l'utilisateur a fait un clic droit
					for(int j = 0; j<i; j++)
						briquesTemp[j] = briques[j];
					if(i+1 < nbBriques)
					{
						for(int j = i+1; j<nbBriques; j++)
							briquesTemp[j-1] = briques[j];
					}
					// On recopie tout le tableau temporaire dans le vrai tableau de briques, donc sans celle qui a �t� supprim�e.
					for(int j = 0; j<nbBriques-1;j++)
						briques[j] = briquesTemp[j];
					briques[nbBriques] = null;
					nbBriques--;
					repaint();
				}
			}
	    }
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) 
	{
		// Correspond au bouton gauche de la souris
		if(arg0.getButton() == MouseEvent.BUTTON1)
		{
			// Si la brique � cr�er est standard, on prend en compte la couleur.
			if(type.getSelectedIndex() == 0)
				briqueTemp = new Brique(type.getSelectedIndex(), 0, 0, couleur.getSelectedIndex());
			// Sinon on met syst�matiquement 0 � la place de la couleur
			else
				briqueTemp = new Brique(type.getSelectedIndex(), 0, 0, 0);
			/* On positionne une brique temporaire sous le curseur de l'utilisateur pour qu'il puissa la placer.
			 * Les modulos servent � cr�er une sorte "d'aimantation" de sorte � ce que les briques ne soient pas 
			 * plac�es totalement n'importe comment, mais selon une "grille invisible".
			 */
	        briqueTemp.x = (int)(arg0.getX()*ratioX - arg0.getX()*ratioX%12 - briqueTemp.l/2.0);
	        briqueTemp.y = (int)(arg0.getY()*ratioY - arg0.getY()*ratioY%23 - briqueTemp.h/2.0);
			afficherBrique = true;
			repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) 
	{
		/* Quand l'utilisateur l�che le clic la brique est cr��e (on aurait pu v�rifier que la brique
		 * cr��e n'intersecte pas une brique d�j� existante, mais ce programme s'adressant majoritairement
		 * aux cr�ateurs du jeu nous n'avons pas jug� utile de prendre ce genre de pr�cautions.
		 */
		if(arg0.getButton() == MouseEvent.BUTTON1)
		{
			briques[nbBriques] = briqueTemp;
			nbBriques++;
			afficherBrique = false;		
			repaint();
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) 
	{
		// Quand la souris est d�plac�e alors que le clic est enfonc� on met � jour la position de la brique temporaire.
		if(afficherBrique)
		{
			briqueTemp.x = (int)(arg0.getX()*ratioX - arg0.getX()*ratioX%12 - briqueTemp.l/2.0);
			briqueTemp.y = (int)(arg0.getY()*ratioY - arg0.getY()*ratioY%23 - briqueTemp.h/2.0);
			repaint();
		}
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {}
}