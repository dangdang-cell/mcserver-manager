package com.dangdang.mc.service;

import com.dangdang.mc.model.Ops;
import com.dangdang.mc.model.Players;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Data
@AllArgsConstructor

public class PlayerService {

private static Path serverPath;
    public PlayerService(Path p) {
        serverPath = p;
    }

    public static boolean opCheck(String name) {
        try {
            List<Ops> opList = opListgetter();
            for (Ops o : opList) {
                if (o.getName().equals(name)) {
                    return true;
                }
            }

        } catch (IOException e) {
            System.out.println("[错误] 读取op列表失败: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public Players playerCheckNoOp(String name) {
        try {
            List<Players> playerList = getPlayerList();
            for (Players p : playerList) {
                if (p.getName().equals(name)) {
                    System.out.println("查询成功，该玩家的信息为:");
                    return p;
                }
            }
        } catch (IOException e) {
            System.out.println("[错误] 读取玩家列表失败: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("查询失败，未找到该玩家");
        return null;
    }

    public void playerCheck(String name) {
        Players p = playerCheckNoOp(name);
        if (p != null) {
            System.out.println("玩家名:" + p.getName() + "   uuid:" + p.getUuid() + "   缓存到期时间：" + p.getExpiresOn() + "    op:" + opCheck(name));
        }
    }

    public List<Players> getPlayerList() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Path playerpath = serverPath.resolve("usercache.json");
        List<Players> playerList = mapper.readValue(
                playerpath.toFile(),
                new TypeReference<List<Players>>() {
                }//这写的复杂是因为它报错，AI给我说这有什么泛型擦除要用这个泛型控制工具，下面那个op列表获取方法同理
        );
        return playerList;
    }

    public static List<Ops> opListgetter() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Path opspath = serverPath.resolve("ops.json");
        List<Ops> opList = mapper.readValue(
                opspath.toFile(),
                new TypeReference<List<Ops>>() {
                }
        );
        return opList;
    }

    public void playerListShow() {
        try {
            List<Players> playerslist = getPlayerList();
            System.out.println("已经有" + playerslist.size() + "名玩家注册服务器");
            if (playerslist.size() > 0) {
                for (Players p : playerslist) {
                    System.out.print("玩家名：" + p.getName());
                    System.out.print("  uuid：" + p.getUuid());
                    System.out.print("  op: " + PlayerService.opCheck(p.getName()));
                    System.out.println();
                }
            } else {
                System.out.println("暂无玩家数据");
            }
        } catch (IOException e) {
            System.out.println("[错误] 读取玩家列表失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void opAdd(){
        //怎么添加op呢？我不知道啊。不是说不会List转json，而是我不知道uuid怎么生成的，空着不写的话启动游戏就给这条删掉了
        System.out.println("暂时不会，等待学习");
    }
    public void opDelete(String name){
        try {
            List<Ops> opList = opListgetter();
            for (Ops o : opList) {
                if (o.getName().equals(name)) {
                    opList.remove(o);
                    ObjectMapper mapper = new ObjectMapper();
                    Path opspath = serverPath.resolve("ops.json");
                    mapper.writeValue(opspath.toFile(),opList);
                    return;
                }
            }
            System.out.println("该玩家不是op，请检查是否输入有误");

        } catch (IOException e) {
            System.out.println("[错误] 读取op列表失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
