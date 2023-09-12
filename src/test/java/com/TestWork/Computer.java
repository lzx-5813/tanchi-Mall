package com.TestWork;

import java.util.Scanner;

public class Computer {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
// 输入屏幕、CPU和硬盘的销售数量
        System.out.print("请输入屏幕销售数量：");
        int screensSold = scanner.nextInt();
        System.out.print("请输入CPU销售数量：");
        int cpusSold = scanner.nextInt();
        System.out.print("请输入硬盘销售数量：");
        int hardDrivesSold = scanner.nextInt();
        System.out.println(Calculator(screensSold, cpusSold, hardDrivesSold));

    }
    public static double Calculator(int screensSold, int cpusSold, int hardDrivesSold){
        // 计算各个配件的销售额
        int screensSales = screensSold * 1800;
        int cpusSales = cpusSold * 800;
        int hardDrivesSales = hardDrivesSold * 400;
// 总销售额
        int totalSales = screensSales + cpusSales + hardDrivesSales;
// 根据销售额计算佣金
        double commission;
        if (totalSales < 100000) {
            commission = totalSales * 0.08;
        } else if (totalSales < 180000) {
            commission = totalSales * 0.1;
        } else {
            commission = 100000 * 0.08 + 80000 * 0.1 + (totalSales - 180000) * 0.15;
        }
// 输出佣金销售
        return commission;
    }
}
