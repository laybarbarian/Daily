package com.hp.daily.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.IntUnaryOperator;

public class NameUtil {
    private NameUtil() {
    }

    public static String getNamePrefix(int enNum,int intNum){
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for( int i = 0; i < enNum; i ++) {
            int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; // 取得大写还是小写
            stringBuilder.append((char)(choice + random.nextInt(26)));
        }
        for (int i=0;i<intNum;i++){
            int ranint=(int)(Math.random()*10);
            stringBuilder.append(ranint);
        }
        return  stringBuilder.toString();
    }

    public static Set<Integer> getNumberWithoutRepetition(int no,int max){
        Set<Integer> nos = new HashSet<>();
        Random random = new Random();
        int i;
        do {
            i=random.nextInt(max+1);
            if (!nos.contains(i))nos.add(i);
        }while (nos.size()<no);
        return  nos;
    }

    public static int getRandomNumber(int min,int max){
        Random random = new Random();
        return random.nextInt(max-min+1)+min;
    }
}
