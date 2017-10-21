import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Brique extends Objet
{
	// Déclaration des variables
	int type;
	int tempsE;
	BufferedImage imagesD[];
	int couleur;
	
	/**
	 * Constructeur de l'objet brique
	 * @param type Type de brique, c'est à dire brique standard (0), brique incassable (1), brique "multi-balles" (2) ou brique "invincibilité" (3)
	 * @param x Position horizontale de la brique
	 * @param y Position verticale de la brique
	 * @param couleur À chaque couleur de brique est associé un entier (utile uniquement pour les briques standard), pour la correspondance numéro/couleur, voir le nom des images dans le répertoire "/images/"
	 */
	public Brique(int type, int x, int y, int couleur)
	{
		super(); // Nécessaire pour ne pas créer d'erreur, même si inutile d'un point de vue logique.
		this.type = type;
		try 
	    {
	        image= ImageIO.read(new File("images/brique"+type+couleur+".png"));
	        if(type !=1)
	        {
	        	imagesD = new BufferedImage[8]; 
	        	/* imagesD correspond à un tableau de 8 images qui s'enchaînent lors de la destruction d'une brique
	        	 * à la manière d'un gif (nous n'avons pas utilisé de gif car malgré l'utilisation d'un buffer cela créait
	        	 * un clignottement de l'image très gênant) 
	        	 */
	        	
	        	for(int i = 0; i<8; i++)
	        		imagesD[i]= ImageIO.read(new File("images/brique"+type+couleur+"D/"+(i+1)+".png"));
	        	/* Les images ont été nommées de sorte à ce qu'elle puissent être trouvées grâce à une règle simple
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
