import com.dangdang.mc.service.PlayerService;
import com.dangdang.mc.util.ConsoleUtil;

private static Path P = ConsoleUtil.setPath();
static PlayerService service= new PlayerService(P);
public static void main(String[] args) {
    String title = "MCServerManager v0.1";
    Scanner sc = new Scanner(System.in);
    main:
    while (true) {
        ConsoleUtil.printHeader(title);
        System.out.println("请输入命令");
        System.out.println("[1] 玩家管理器" + "\n" + "[2] 配置文件管理" + "\n" + "[3] 日志管理" + "\n" + "[4] 退出"
        );
        try {
            int option = sc.nextInt();
            sc.nextLine();
            if (option > 0 && option < 5) {
                switch (option) {
                    case 1:
                        showPlayerMenu();
                        break;
                    case 2:
                        ConsoleUtil.pause(sc);
                        break;
                    case 3:
                        ConsoleUtil.pause(sc);
                        break;
                    case 4:
                        break main;


                }
            }
        }catch(InputMismatchException e) {
            System.out.println("无效输入，请输数字");
            sc.nextLine();
        }

    }
}

public static void showPlayerMenu() {
    Scanner sc = new Scanner(System.in);
    menuPlayServe:
    while (true) {
        ConsoleUtil.clearScreen();
        ConsoleUtil.printDivider("玩家管理");
        System.out.println("[1] 添加玩家\n" +
                "[2] 查询玩家\n" +
                "[3] 列出所有玩家\n" +
                "[4] 添加管理员\n" +
                "[5] 删除管理员\n" +
                "[6] 列出所有管理员\n" +
                "[7] 添加黑名单\n" +
                "[8] 删除黑名单\n" +
                "[9] 列出所有黑名单\n" +
                "[0] 返回上级菜单"
        );
        int option = sc.nextInt();
        if (option >= 0 && option <= 9) {
            switch (option) {
                case 0:
                    break menuPlayServe;
                case 1:
                    //暂时不清楚白名单怎么做的，等我下次了解一下再写
                    System.out.println("服务器未开启白名单，无需手动添加");
                    ConsoleUtil.pause(sc);
                    break;
                case 2:
                    System.out.println("请输入要查询的玩家名");
                        sc.nextLine();
                        String name = sc.nextLine();
                        service.playerCheck(name);
                    ConsoleUtil.pause(sc);
                    break;
                case 3:
                    service.playerListShow();
                    ConsoleUtil.pause(sc);
                    break;
                case 4:
                    service.opAdd();
                    ConsoleUtil.pause(sc);
                    break;
                case 5:
                    System.out.println("请输入要删除的op的玩家名");
                    sc.nextLine();
                    String opname = sc.nextLine();
                    service.opDelete(opname);
                    ConsoleUtil.pause(sc);
                    break;
                case 6:
                    //懒得写了，逻辑都一样，下次交给ai写
                    ConsoleUtil.pause(sc);
                    break;
                case 7:
                    ConsoleUtil.pause(sc);
                    break;
                case 8:
                    ConsoleUtil.pause(sc);
                    break;
                case 9:
                    ConsoleUtil.pause(sc);
                    break;
            }
        }
    }
}

