package mensamodell;

/**
 * @author alexs
 * 
 * Um die Studenten nicht immer direkt zu einer Theke zu senden fuegen wir path Objekte in den context. 
 * Damit koennen Laufwege dargestellt werden. Also sozusagen Checkpoints die die Studenten ablaufen um zu bestimmen Theken zu kommen. 
 *
 */

public class Path {
	
	int x;
	int y;
	
	public Path(int x, int y) {
		this.x = x;
		this.y = y;
	}
	

}
