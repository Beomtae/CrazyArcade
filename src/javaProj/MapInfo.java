package javaProj;
public class MapInfo {
	//state = "BROWNBLOCK", "PINKBLOCK", "ITEM","BUMB", "FREE"
	
	int size;
	Information map[][];
	
	class Information{
		int x;
		int y;
		String state = "FREE"; //기본값은 FREE
		boolean wasWall = false;	//아이템 구현을 위해 전 상태가 벽이었는지 저장
	}
	
	public MapInfo(int size) {
		this.size = size;
		map = new Information[size][size];
		
		for (int i = 0; i<size; i++) {
			   for (int j =0; j<size; j++) {
				   this.map[i][j] = new Information();
				   this.map[i][j].x = j *40;
				   this.map[i][j].y = i *40;
				   this.map[i][j].wasWall=false;
			   }
		   }
	}
	
	public void update(Information info, String st) {
		int x = info.x;
		int y = info.y;
		this.map[x][y].state = st;
	}
}