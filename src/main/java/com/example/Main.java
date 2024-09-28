package com.example;

import java.util.*;

// 注意类名必须为 Main, 不要有任何 package xxx 信息
public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while(in.hasNextInt()){
            String a = in.nextLine();
            in.close();
            String[] words = a.split(" ");
            Map<String, Integer> map = new HashMap<>();
            List<String> list = new ArrayList<>();
            int k = map.getOrDefault(a, 0)+1;
            if(k>=3){
                list.add(a);
            }
            map.put(a, k);
            for(String s:list){
                Integer i = map.getOrDefault(s, 0);
                System.out.println(s);
            }

        }


    }
}
