import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Objet 
{
	// D�claration des variables
	int x,y;                
    int h,l;                
    float direction;        
    float vitesse;          
    boolean actif;
    BufferedImage image;
    Rectangle BoxObjet;
    
	/**
	 * Constructeur d'un objet dans le cas g�n�ral (seules les briques utilisent leur propre constructeur).
	 * @param NomImage Emplacement de l'image repr�sentant l'objet.
	 * @param ax Position horizontale de l'objet.
	 * @param ay Position verticale de l'objet.
	 * @param ad Direction de l'objet (utile uniquement pour les balles, car les murs ne bougent pas et la/les raquette(s) bougent d'une autre mani�re.
	 * @param av Vitesse de l'objet (de la m�me mani�re que pour la direction, ce n'est utile que pour les balles).
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
    
        /* Hitbox de l'objet (elle est rectangulaire pour tous les objets, m�me les balles qui sont rondes,
         * mais au vu des dimensions ce n'est pas g�nant et cela simplifie �norm�ment le code). 
         */
        BoxObjet = new Rectangle(x,y,l,h); 
       
        /* Ces deux param�tres n'�tant utiles que pour les balles on aurait pu cr�er une classe "Balle" s�par�e, mais nous 
         * n'avons pas jug� cela utile pour seulement deux param�tres en plus (ce n'est pas une tr�s grosse perte de m�moire).
         */
        direction=ad;
        vitesse=av;
        
        actif = true;
	}
	
	/**
	 * Ce constructeur est appel� uniquement lors de la cr�ation d'une brique 
	 */
	public Objet() {}
	
	/**
	 * D�tecte si l'objet est entr� en collision avec un autre objet pass� en param�tre
	 * @param O L'objet avec lequel la collision doit �tre test�e.
	 * @return TRUE si les deux objets s'intersectent, FALSE sinon. 
	 */
	boolean Collision(Objet O) 
	{
        return BoxObjet.intersects(O.BoxObjet); 
    }
	
	/**
	 * D�place l'objet en mettant � jour sa position (en fonction de sa vitesse et de sa direction).
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
