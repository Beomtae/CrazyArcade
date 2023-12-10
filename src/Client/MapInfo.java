package Client;
//package netProg;

public class MapInfo {
    //state = "BROWNBLOCK", "PINKBLOCK", "ITEM","BUMB", "FREE"

    int size;
    Information map[][];

    class Information{
        int x;
        int y;
        String state = "FREE";
    }

    public MapInfo(int size) {
        this.size = size;
        map = new Information[size][size];

        for (int i = 0; i<size; i++) {
            for (int j =0; j<size; j++) {
                this.map[i][j] = new Information();
                this.map[i][j].x = j *40;
                this.map[i][j].y = i *40;
            }
        }
    }

    public void update(Information info, String st) {
        int x = info.x;
        int y = info.y;
        this.map[x][y].state = st;
    }
}
