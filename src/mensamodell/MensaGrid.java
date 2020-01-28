package mensamodell;

import java.util.ArrayList;
import java.util.List;

public class MensaGrid {
	private int[][] grid;
	private int rows;
	private int cols;


	public MensaGrid(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		grid = new int[rows][cols];
	}

	public int get(int row, int col) {
		return grid[row][col];
	}

	public void set(int row, int col, int val) {
		grid[row][col]  = val;
	}

	public void print() {
		for (int j = cols-1; j >= 0; j--) {
			for (int i = 0; i < rows; i++)
				if (grid[i][j] == 0)
					System.out.print("."+" ");
				else
					System.out.print(""+grid[i][j]+" ");
			System.out.println();
		}
	}

	public void setObj(Object obj) {
		if (obj instanceof Kasse) {
			Kasse k = (Kasse) obj;
			grid[k.x][k.y] = consts.GRID_KASSE;
		}

		if (obj instanceof Theke) {
			Theke t = (Theke) obj;
			for (int i = t.x - (int)(t.size.x / 8); i < t.x + (int)(t.size.x / 8); i++)
				for (int j = t.y - (int)(t.size.y / 8); j < t.y + (int)(t.size.y / 8); j++) {
//						System.out.println("i: "+i+"  j:"+j+"   t.x: "+t.x+" t.y: "+t.y);
						if (0 <= i && i < rows && 0 <= j && j < cols)
							grid[i][j] = consts.GRID_THEKE;
				}
		}

		if (obj instanceof Ausgabe) {
			Ausgabe a = (Ausgabe) obj;
			grid[a.x][a.y] = consts.GRID_AUSGABE;
		}
	}
	
	

	public List<Integer> Bresenham(int x0, int y0, int x1, int y1) {
		List<Integer> res = new ArrayList<Integer>();
    int dx =  Math.abs(x1-x0);
    int sx = x0<x1 ? 1 : -1;
    int dy = -Math.abs(y1-y0);
    int sy = y0<y1 ? 1 : -1;
    int err = dx+dy;
    while (true) {
        res.add(get(x0, y0));
        if (x0==x1 && y0==y1)
        	break;
        int e2 = 2*err;
        if (e2 >= dy) {
            err += dy; /* e_xy+e_x > 0 */
            x0 += sx;
        }
        if (e2 <= dx) {  /* e_xy+e_y < 0 */
            err += dx;
            y0 += sy;
        }
    }
    return res;
	}


	

}
