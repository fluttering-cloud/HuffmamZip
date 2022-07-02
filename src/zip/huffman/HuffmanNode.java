package zip.huffman;

import java.util.*;

public class HuffmanNode implements Comparable<HuffmanNode>{
    private Integer weight;
    private Byte date;
    private HuffmanNode leftChild;
    private HuffmanNode rightChild;

    public HuffmanNode() {}

    public HuffmanNode(Integer weight,Byte date) {
        this.weight = weight;
        this.date = date;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Byte getDate() {
        return date;
    }

    public void setDate(Byte date) {
        this.date = date;
    }

    public HuffmanNode getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(HuffmanNode leftChild) {
        this.leftChild = leftChild;
    }

    public void setRightChild(HuffmanNode rightChild) {
        this.rightChild = rightChild;
    }

    public HuffmanNode getRightChild() {
        return rightChild;
    }

    /**
     * 根据直接数组构造出对应的赫夫曼树
     * @param codeByte
     * @return
     */
    public static HuffmanNode createHuffmanTree(byte[] codeByte) {
        if (codeByte.length <= 0) return null;
        Map<Byte,Integer> codeMap = new HashMap<>();
        ArrayList<HuffmanNode> nodes = new ArrayList<>();
        Integer count;
        for (int i = 0; i < codeByte.length; i++) {
            count = codeMap.get(codeByte[i]);
            if (count == null) {
                codeMap.put(codeByte[i],1);
            }else {
                codeMap.put(codeByte[i],++count);
            }
        }

        for (Map.Entry<Byte, Integer> byteIntegerEntry : codeMap.entrySet()) {
            nodes.add(new HuffmanNode(byteIntegerEntry.getValue(),byteIntegerEntry.getKey()));
        }
        while (nodes.size() > 1) {
            Collections.sort(nodes);
            HuffmanNode parent = new HuffmanNode();
            HuffmanNode n2 = nodes.remove(1);
            HuffmanNode n1 = nodes.remove(0);
            //权值小的在左，大的在右
            parent.setLeftChild(n1);
            parent.setRightChild(n2);
            parent.setWeight(n1.getWeight() + n2.getWeight());
            nodes.add(parent);
        }
        return nodes.get(0);
    }

    // 赫夫曼树的先序遍历
    public static void preTraverse(HuffmanNode root) {
        if (root.getDate() != null) {
            System.out.println("data:" + root.getDate() + ",weight:" + root.getWeight());
        }
        if (root.getLeftChild() != null) preTraverse(root.getLeftChild());
        if (root.getLeftChild() != null) preTraverse(root.getRightChild());
    }


    /**
     * 根据赫夫曼树生成对应的赫夫曼编码
     * @param root 赫夫曼树的根节点
     * @param codes 每个节点对应的赫夫曼编码
     * @param huffmanMap 存储赫夫曼编码的集合，由外部传递
     */
    public static void huffmanCodes(HuffmanNode root,StringBuffer codes,Map<Byte,String> huffmanMap) {
        // 左分支为1，右分支为0
        if (root.getLeftChild() != null) {
            StringBuffer temp = new StringBuffer(codes);
            temp.append("1");
            huffmanCodes(root.getLeftChild(),temp,huffmanMap);
        }
        if (root.getRightChild() != null) {
            StringBuffer temp = new StringBuffer(codes);
            temp.append("0");
            huffmanCodes(root.getRightChild(),temp,huffmanMap);
        }
        if (root.getDate() != null) huffmanMap.put(root.getDate(),codes.toString());
    }

    @Override
    public int compareTo(HuffmanNode huffmanNode) {
        return this.getWeight() - huffmanNode.getWeight();
    }
}
