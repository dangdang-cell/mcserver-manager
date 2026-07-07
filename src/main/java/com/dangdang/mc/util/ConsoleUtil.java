package com.dangdang.mc.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ConsoleUtil {
    public static void printHeader(String title) {
        System.out.println("=========================" + title + "==========================");
    }

    public static void printDivider(String title) {
        System.out.println("-------------------------------" + title + "---------------------------");
    }

    public static void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    public static void pause(Scanner scanner) {
        System.out.print("按回车继续...");
        scanner.nextLine();
        scanner.nextLine();


    }
    public static Path setPath(){
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入服务器所在文件夹路径");
        String path = String.valueOf(sc.nextLine());
        Path P = Paths.get(path);
        return P;
    }


}
