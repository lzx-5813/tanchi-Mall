package com.TestWork;

public class InsertSort {
    public static void main(String[] args) {
        int[] arr = new int[]{4,2,3,1,5};
//        int temp = 0;
//        for(int i = 1;i<arr.length;i++){
//            for(int j = 0;j<i;j++){
//                if(arr[j] > arr[i]){
//                    temp = arr[j];
//                    arr[j] = arr[i];
//                    arr[i] = temp;
//                }
//            }
//            for (int k =0;k<arr.length;k++) {
//                System.out.println("第"+i+"次："+arr[k]);
//            }
//        }

        for (int i = 1; i < arr.length; i++) {

            // 记录要插入的数据
            int tmp = arr[i];

            // 从已经排序的序列最右边的开始比较，找到比其小的数
            int j = i;
            while (j > 0 && tmp < arr[j - 1]) {
                arr[j] = arr[j - 1];
                j--;
            }

            // 存在比其小的数，插入
            if (j != i) {
                arr[j] = tmp;
            }
            for (int k =0;k<arr.length;k++) {
//                System.out.println("第"+i+"次："+arr[k]);
            }
        }

        for (int i : arr) {
            System.out.println(i);
        }

    }
}
