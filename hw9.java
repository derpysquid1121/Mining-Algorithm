import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class hw9 {
  public static void main(String args[]) {
    Scanner scnnr = new Scanner(System.in);
    
    int n = scnnr.nextInt();//Number of mining locations
    int m = scnnr.nextInt();//number of nodes with at lease 1 prerequisite
    scnnr.nextLine();//clear buffer
    
    int[] values = new int[n];//array that holds the value at each mine
    for(int i = 0; i < n; i++) { //for each mine
      values[i] = scnnr.nextInt(); //set the index of that mine equal to it's value
    }
    scnnr.nextLine();//clear buffer
    
    int[][] locations = new int[n+2][n+2]; //adjacency matrix for showing dependencies
    
    for(int i = 0; i < values.length; i++) { //for each mine
      if(values[i] >= 0){locations[n][i] = values[i];} //create weighted edge from s to mine
      else if(values[i] < 0){locations[i][n+1] = Math.abs(values[i]);} //same from mine to t
    }
    
    String input; //String input of each line of location with dependencies
    String[] temp; //an array that stores the location first then it's dependencies
    int row; // the location to be used to store it's dependencies in the adjacency matrix
    for(int i = 0; i < m; i++) { //for each location with dependencies
      input = scnnr.nextLine(); //String input of location & dependencies
      temp = input.split(" "); //separate input into an array by spaces
      row = Integer.parseInt(temp[0]); //take the location and use as index for row of array
      for(int j = 1; j < temp.length; j++) { //for each dependency
        locations[row -1][Integer.parseInt(temp[j])-1] = Integer.MAX_VALUE; //store a 1 to show dependency
      }
    }
    scnnr.close();
    System.out.println(minCut(locations, n, n+1, values));
  }
    private static boolean bfs(int[][] residual, int s,
                                int t, int[] p) { 
        boolean[] visited = new boolean[residual.length]; //array to show if visited or not   
        Queue<Integer> q = new LinkedList<Integer>();
        q.add(s); //add source to queue
        p[s] = -1; //no parent for source node
        visited[s] = true;
            
        while (!q.isEmpty()) { //BFS 
            int v = q.poll();
            for (int i = 0; i < residual.length; i++) {
                if (residual[v][i] > 0 && !visited[i]) {
                    q.offer(i);
                    visited[i] = true;
                    p[i] = v;
                }
            }
        }     
        return visited[t]; //if true, t was reached
    }

    private static void dfs(int[][] residual, int s,
                                boolean[] visited) {
        visited[s] = true;
        for (int i = 0; i < residual.length; i++) {
                if (residual[s][i] > 0 && !visited[i]) {
                    dfs(residual, i, visited);
                }
        }
    }
    
    //finds minimum s-t cut and sums all nodes within the cut
    private static int minCut(int[][] graph, int s, int t, int[] values) {
        int r,c;
          
        int[][] residual = new int[graph.length][graph.length]; //residual graph
        for (int i = 0; i < graph.length; i++) { //create the residual graph
            for (int j = 0; j < graph.length; j++) {
                residual[i][j] = graph[i][j];
            }
        }
        int[] p = new int[graph.length]; //keep track of path
             
        while (bfs(residual, s, t, p)) { //while there is still a path from s to t
              
            int pathFlow = Integer.MAX_VALUE;         
            for (c = t; c != s; c = p[c]) {
                r = p[c];
                pathFlow = Math.min(pathFlow, residual[r][c]); //smallest residual path
            }
            for (c = t; c != s; c = p[c]) { //residual graph changes
                r = p[c];
                residual[r][c] = residual[r][c] - pathFlow;
                residual[c][r] = residual[c][r] + pathFlow;
            }
        }
        boolean[] visited = new boolean[graph.length];     
        dfs(residual, s, visited);
        int total = 0;
        for(int i = 0; i < visited.length; i++) {
          if(visited[i] & i < graph.length - 2) {
            total += values[i];
          }
        }
        return total;
    }
}