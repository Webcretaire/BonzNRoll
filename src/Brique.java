import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Brique extends Objet
{
	// D�claration des variables
	int type;
	int tempsE;
	BufferedImage imagesD[];
	int couleur;
	
	/**
	 * Constructeur de l'objet brique
	 * @param type Type de brique, c'est � dire brique standard (0), brique incassable (1), brique "multi-balles" (2) ou brique "invincibilit�" (3)
	 * @param x Position horizontale de la brique
	 * @param y Position verticale de la brique
	 * @param couleur � chaque couleur de brique est associ� un entier (utile uniquement pour les briques standard), pour la correspondance num�ro/couleur, voir le nom des images dans le r�pertoire "/images/"
	 */
	public Brique(int type, int x, int y, int couleur)
	{
		super(); // N�cessaire pour ne pas cr�er d'erreur, m�me si inutile d'un point de vue logique.
		this.type = type;
		try 
	    {
	        image= ImageIO.read(new File("images/brique"+type+couleur+".png"));
	        if(type !=1)
	        {
	        	imagesD = new BufferedImage[8]; 
	        	/* imagesD correspond � un tableau de 8 images qui s'encha�nent lors de la destruction d'une brique
	        	 * � la mani�re d'un gif (nous n'avons pas utilis� de gif car malgr� l'utilisation d'un buffer cela cr�ait
	        	 * un clignottement de l'image tr�s g�nant) 
	        	 */
	        	
	        	for(int i = 0; i<8; i++)
	        		imagesD[i]= ImageIO.read(new File("images/brique"+type+couleur+"D/"+(i+1)+".png"));
	        	/* Les images ont �t� nomm�es de sorte � ce qu'elle puissent �tre trouv�es gr�ce � une r�gle simple
	        	 * comme celle-ci 
	        	 */
	        }
	    } 
	    catch(Exception err)
	    {
	        System.out.println("image introuvable !");
	        System.exit(0);
	    };
		
		h= image.getHeight(null);
        l= image.getWidth(null);
        this.x=x;
        this.y=y;
        actif=true;
        this.tempsE = 0;
        BoxObjet = new Rectangle(x,y,l,h);
        this.couleur = couleur;
	}
	
	public String toString()
	{
		return x+";"+y+";"+type+";"+couleur;
	}
}
