public class Graph {

    int vertexSize; // 图的顶点数
    int[][] data; // 图对应的邻接矩阵

    /**
     * prim算法
     * @param graph 图对象
     * @param vertex 最小生成树的初始节点
     * @return 最小生成树的路径
     */
    public String[] prim(Graph graph, int vertex) {
        if (graph.vertexSize <= 0 || vertex < 0 || vertex >= graph.vertexSize )
            return null;
        String[] path = new String[graph.vertexSize - 1];
        int[] visited = new int[graph.vertexSize];
        visited[vertex] = 1;
        int v1 = -1;
        int v2 = -1;
        int min = Integer.MIN_VALUE;
        for (int i = 1; i < graph.vertexSize; i++) {
            for (int k = 0; k < graph.vertexSize; k++) {
                for (int j = 0; j < graph.vertexSize; j++) {
                    if (visited[k] == 1 && visited[j] != 1 && data[k][j] < min) {
                           min = data[k][j];
                           v1 = k;
                           v2 = j;
                    }
                }
            }
            path[i - 1] = new String(v1 + "->" +v2 + ",weight:" + min);
            data[v1][v2] = Integer.MAX_VALUE;
            min = Integer.MIN_VALUE;
        }
        return path;
    }
}
