package dp;

import java.util.ArrayList;
import java.util.Collections;

public class Dp {

    public ArrayList<Goods> maxPackage(ArrayList<Goods> goods, int volume) {
        ArrayList<Goods> maxPkg = new ArrayList<>();
        biggestValueGoods(goods,volume,maxPkg);
        return maxPkg;
    }

    private int biggestValueGoods(ArrayList<Goods> goods, int volume,ArrayList<Goods> maxPage) {
        // 如果物品列表为空，就不放
        if (goods.size() == 0 || goods == null) return 0;
        int tempValue = 0;
        int maxValue = 0;
        // 遍历物品列表，找出最大可放入背包内的最大价值的物品
        for (int i = 0; i < goods.size(); i++) {
            if (goods.get(i).weight < volume) {
                ArrayList<Goods> tempPackage = new ArrayList<>();
                Goods g = goods.get(i);
                ArrayList<Goods> tempGoods = new ArrayList<>();
                // 因为是0-1背包，所以要移除已经放入的物品
                Collections.copy(tempGoods,goods);
                tempGoods.remove(g);
                tempValue = g.value + biggestValueGoods(tempGoods,volume - g.weight,tempPackage);
                if ( maxValue < tempValue) {
                    maxPage = tempPackage;
                    maxPage.add(g);
                }
            }
        }
        return maxValue;
    }

    public int arrayMax(int[] array) {
        return maxValue(array,array.length - 1);
    }

    private int maxValue(int[] array,int i) {
        if (array.length == 1 || i == 0) return array[0];
        else if (array.length == 2 || i == 1 ) return Math.max(array[0],array[1]);
        else if (i == 2) return Math.max(array[0] + array[2],array[1]);
        int temp;
        temp = array[i] + Math.max(maxValue(array,i-2),maxValue(array,i-3));
        temp = Math.max(temp,maxValue(array,i -1 ));
        return temp;
    }
}