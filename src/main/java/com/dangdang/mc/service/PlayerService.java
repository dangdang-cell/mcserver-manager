package com.dangdang.mc.service;

import com.dangdang.mc.model.BannedPlayers;
import com.dangdang.mc.model.Ops;
import com.dangdang.mc.model.Players;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

@Data
public class PlayerService {

    private static Path serverPath;

    public PlayerService(Path p) {
        serverPath = p;
    }

    public boolean opCheck(String name) {
        try {
            List<Ops> opList = opListgetter();
            for (Ops o : opList) {
                if (o.getName().equalsIgnoreCase(name)) {
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
                if (p.getName().equalsIgnoreCase(name)) {
                    return p;
                }
            }
        } catch (IOException e) {
            System.out.println("[错误] 读取玩家列表失败: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void playerCheck(String name) {
        Players p = playerCheckNoOp(name);
        if (p != null) {
            System.out.println("玩家名:" + p.getName() + "   uuid:" + p.getUuid() + "   缓存到期时间：" + p.getExpiresOn() + "    op:" + opCheck(name));
        } else {
            System.out.println("查询失败，未找到该玩家");
        }
    }

    public List<Players> getPlayerList() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Path playerpath = serverPath.resolve("usercache.json");
        return mapper.readValue(
                playerpath.toFile(),
                new TypeReference<List<Players>>() {
                }
        );
    }

    public List<Ops> opListgetter() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Path opspath = serverPath.resolve("ops.json");
        return mapper.readValue(
                opspath.toFile(),
                new TypeReference<List<Ops>>() {
                }
        );
    }

    public void playerListShow() {
        try {
            List<Players> playerslist = getPlayerList();
            System.out.println("已经有" + playerslist.size() + "名玩家注册服务器");
            if (playerslist.size() > 0) {
                for (Players p : playerslist) {
                    System.out.print("玩家名：" + p.getName());
                    System.out.print("  uuid：" + p.getUuid());
                    System.out.print("  op: " + opCheck(p.getName()));
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

    public void opListShow() {
        try {
            List<Ops> opList = opListgetter();
            System.out.println("当前有 " + opList.size() + " 名管理员");
            if (opList.size() > 0) {
                for (Ops o : opList) {
                    System.out.println("玩家名: " + o.getName() + "  UUID: " + o.getUuid() + "  等级: " + o.getLevel());
                }
            } else {
                System.out.println("暂无管理员");
            }
        } catch (IOException e) {
            System.out.println("[错误] 读取管理员列表失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void opAdd(Scanner sc) {
        try {
            List<Ops> opList = opListgetter();
            String name = sc.nextLine();
            for (Ops o : opList) {
                if (o.getName().equalsIgnoreCase(name)) {
                    System.out.println("该玩家已是op");
                    return;
                }
            }
            ObjectMapper mapper = new ObjectMapper();
            Players p = playerCheckNoOp(name);
            if (p == null) {
                String uuid = String.valueOf(UUID.randomUUID());
                opList.add(new Ops(uuid, name, 4, false));
                List<Players> playersList = getPlayerList();
                playersList.add(new Players(uuid, name, null));
                Path opspath = serverPath.resolve("ops.json");
                Path playerspath = serverPath.resolve("usercache.json");
                mapper.writeValue(playerspath.toFile(), playersList);
                mapper.writeValue(opspath.toFile(), opList);
                System.out.println("管理员添加成功（该玩家未登录过服务器，已生成临时UUID）");
            } else {
                String uuid = p.getUuid();
                opList.add(new Ops(uuid, name, 4, false));
                Path opspath = serverPath.resolve("ops.json");
                mapper.writeValue(opspath.toFile(), opList);
                System.out.println("管理员添加成功");
            }
        } catch (IOException e) {
            System.out.println("[错误] 添加管理员失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void opDelete(String name) {
        try {
            List<Ops> opList = opListgetter();
            Iterator<Ops> iterator = opList.iterator();
            boolean found = false;
            while (iterator.hasNext()) {
                Ops o = iterator.next();
                if (o.getName().equalsIgnoreCase(name)) {
                    iterator.remove();
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("该玩家不是op，请检查是否输入有误");
                return;
            }
            ObjectMapper mapper = new ObjectMapper();
            Path opspath = serverPath.resolve("ops.json");
            mapper.writeValue(opspath.toFile(), opList);
            System.out.println("管理员删除成功");
        } catch (IOException e) {
            System.out.println("[错误] 删除管理员失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
//这些是AI写的，逻辑都差不多，语法和安全性所谓的比我的好
    // ==================== 黑名单管理 ====================

    public List<BannedPlayers> getBanList() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Path banpath = serverPath.resolve("banned-players.json");
        if (!Files.exists(banpath)) {
            return new ArrayList<>();
        }
        return mapper.readValue(
                banpath.toFile(),
                new TypeReference<List<BannedPlayers>>() {
                }
        );
    }

    public void banAdd(String name, String reason) {
        try {
            List<BannedPlayers> banList = getBanList();
            for (BannedPlayers b : banList) {
                if (b.getName().equalsIgnoreCase(name)) {
                    System.out.println("该玩家已在黑名单中");
                    return;
                }
            }

            // 如果该玩家是OP，先移除OP权限
            if (opCheck(name)) {
                opDelete(name);
                System.out.println("已自动移除该玩家的管理员权限");
            }

            ObjectMapper mapper = new ObjectMapper();
            Players p = playerCheckNoOp(name);
            String uuid;
            if (p == null) {
                uuid = String.valueOf(UUID.randomUUID());
                List<Players> playersList = getPlayerList();
                playersList.add(new Players(uuid, name, null));
                Path playerspath = serverPath.resolve("usercache.json");
                mapper.writeValue(playerspath.toFile(), playersList);
            } else {
                uuid = p.getUuid();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
            String created = sdf.format(new Date());

            banList.add(new BannedPlayers(uuid, name, created, "MCServerManager", "forever", reason));
            Path banpath = serverPath.resolve("banned-players.json");
            mapper.writeValue(banpath.toFile(), banList);
            System.out.println("玩家 " + name + " 已被加入黑名单");
        } catch (IOException e) {
            System.out.println("[错误] 添加黑名单失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void banDelete(String name) {
        try {
            List<BannedPlayers> banList = getBanList();
            Iterator<BannedPlayers> iterator = banList.iterator();
            boolean found = false;
            while (iterator.hasNext()) {
                BannedPlayers b = iterator.next();
                if (b.getName().equalsIgnoreCase(name)) {
                    iterator.remove();
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("该玩家不在黑名单中");
                return;
            }
            ObjectMapper mapper = new ObjectMapper();
            Path banpath = serverPath.resolve("banned-players.json");
            mapper.writeValue(banpath.toFile(), banList);
            System.out.println("玩家 " + name + " 已从黑名单移除");
        } catch (IOException e) {
            System.out.println("[错误] 移除黑名单失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void banListShow() {
        try {
            List<BannedPlayers> banList = getBanList();
            System.out.println("当前黑名单共有 " + banList.size() + " 人");
            if (banList.size() > 0) {
                for (BannedPlayers b : banList) {
                    System.out.println("玩家名: " + b.getName() + "  原因: " + b.getReason() + "  时间: " + b.getCreated());
                }
            } else {
                System.out.println("黑名单为空");
            }
        } catch (IOException e) {
            System.out.println("[错误] 读取黑名单失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
