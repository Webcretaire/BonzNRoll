import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Objet 
{
	// Déclaration des variables
	int x,y;                
    int h,l;                
    float direction;        
    float vitesse;          
    boolean actif;
    BufferedImage image;
    Rectangle BoxObjet;
    
	/**
	 * Constructeur d'un objet dans le cas général (seules les briques utilisent leur propre constructeur).
	 * @param NomImage Emplacement de l'image représentant l'objet.
	 * @param ax Position horizontale de l'objet.
	 * @param ay Position verticale de l'objet.
	 * @param ad Direction de l'objet (utile uniquement pour les balles, car les murs ne bougent pas et la/les raquette(s) bougent d'une autre manière.
	 * @param av Vitesse de l'objet (de la même manière que pour la direction, ce n'est utile que pour les balles).
	 */
	public Objet(String NomImage, int ax, int ay, float ad, float av) 
	{
		super();
        try 
        {
            image= ImageIO.read(new File(NomImage));
        } 
        catch(Exception err) 
        {
            System.out.println(NomImage+" introuvable !");            
            System.exit(0);    
        }
       
       
        h= image.getHeight(null);
        l= image.getWidth(null);
        x=ax;
        y=ay;
    
        /* Hitbox de l'objet (elle est rectangulaire pour tous les objets, même les balles qui sont rondes,
         * mais au vu des dimensions ce n'est pas gênant et cela simplifie énormément le code). 
         */
        BoxObjet = new Rectangle(x,y,l,h); 
       
        /* Ces deux paramètres n'étant utiles que pour les balles on aurait pu créer une classe "Balle" séparée, mais nous 
         * n'avons pas jugé cela utile pour seulement deux paramètres en plus (ce n'est pas une très grosse perte de mémoire).
         */
        direction=ad;
        vitesse=av;
        
        actif = true;
	}
	
	/**
	 * Ce constructeur est appelé uniquement lors de la création d'une brique 
	 */
	public Objet() {}
	
	/**
	 * Détecte si l'objet est entré en collision avec un autre objet passé en paramètre
	 * @param O L'objet avec lequel la collision doit être testée.
	 * @return TRUE si les deux objets s'intersectent, FALSE sinon. 
	 */
	boolean Collision(Objet O) 
	{
        return BoxObjet.intersects(O.BoxObjet); 
    }
	
	/**
	 * Déplace l'objet en mettant à jour sa position (en fonction de sa vitesse et de sa direction).
	 */
	public void move() 
	{
        x=x+(int)(vitesse*Math.cos(direction));
        y=y+(int)(vitesse*Math.sin(direction));
        
        BoxObjet.setLocation(x,y);
	}
	
	public String toString()
	{
		return "Location : ("+x+";"+y+") Size : ("+l+";"+h+")";
	}
}
