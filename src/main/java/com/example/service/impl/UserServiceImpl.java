package com.example.service.impl;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.example.exception.*;
import com.example.mapper.DatabaseHelper;
import com.example.mapper.UserMapper;
import com.example.service.UserService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pojo.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
//git remote add origin https://github.com/Cyttyc/image-quality.git

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DatabaseHelper dbHelper;


    public User login(UserLoginDTO userLoginDTO){
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        //1.根据用户名查询数据库的数据
        User user = userMapper.getByUsername(username);
        Integer status = user.getStatus();
        //2.处理各种异常情况（用户名不存在/密码不对/账户被锁定）
        if(user == null){
            throw new AccountNotFoundException("账号不存在");
        }
        if(status==1){
            throw new AccountNotFoundException("账户已被禁用！");
        }

        //密码对比
        //对前端传来的明文密码进行加密
//        password = DigestUtils.md5DigestAsHex(password.getBytes());

        if(!password.equals(user.getPassword())){
            //密码错误
            throw new PasswordErrorException("密码错误");
        }
//ngrok http --domain=profound-warthog-admittedly.ngrok-free.app 9090
        return user;
    }

//   受试者注册
    public void register(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        //根据用户姓名查询是否存在重复用户
        User beforeUser = userMapper.getByUsername(username);
        if(beforeUser != null){
            throw new AccountExistException("用户名已存在");
        }
        if(username.length()>7){
            throw new AccountNameTooLong("用户名长度超出范围");
        }
        String regex = "^[a-zA-Z0-9]+$";

        if (!(username).matches(regex)) {
            throw new AccountNameTooLong("用户名只能包含字母和数字");
        }
        User user = new User();
        BeanUtils.copyProperties(userLoginDTO, user);
//        user.setPassword(user.getPassword());
//        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.register(user);
    }

//    受试者实验数据保存
    public Map<String, Integer> saveDate(UserDataDTO userDataDTO) {
        String username = userDataDTO.getUsername();
        Map<String, Integer> data = userDataDTO.getEloScores();
        User user = userMapper.getByUsername(username);

        if(user == null){
            throw new AccountNotFoundException("账号不存在！");
        }
        user.setUpdateTime(LocalDateTime.now());
        user.setStatus(1);
        //根据data中的数据，更新用户表中的数据
        for(Map.Entry<String, Integer> entry : data.entrySet()){
            String key = entry.getKey();
            Integer value = entry.getValue();
            // 使用反射机制更新属性
            String methodName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
            try {
                Method method = User.class.getMethod(methodName, Integer.class);
                method.invoke(user, value);
            } catch (Exception e) {
                // 如果找不到对应的 setter 方法，记录错误或处理异常
                System.err.println("Failed to set property " + key + ": " + e.getMessage());
            }
        }
        // 更新User对象到数据库
        userMapper.update(user);
        Map<String, Integer> map = new HashMap<>();
        map.put("status", user.getStatus());
        return map;
    }

//    受试者留言
    public void submitMessage(String name, String message) {
        userMapper.updateMessage(name, message);
    }

    /**
     * elo
     * @param userDataDTO
     * @return
     */
    public Map<String, Integer> elo(UserDataDTO userDataDTO) {
        String username = userDataDTO.getUsername();
        User currentUser = userMapper.getByUsername(username);
        // 获取上一个用户的ELO分数
        Map<String, Integer> eloScores = new HashMap<>();
        if (currentUser == null) {
            throw new AccountNotFoundException("账号不存在！");
        }

        if(username.equals("admin")){
            // 如果是 admin 用户，所有 ELO 分数均为 1000
            eloScores.put("CLBA", 1000);
            eloScores.put("DA", 1000);
            eloScores.put("HTBA", 1000);
            eloScores.put("lsbPanda", 1000);
            eloScores.put("refool", 1000);
            eloScores.put("ours", 1000);
            eloScores.put("Sig", 1000);
            eloScores.put("clean", 1000);
            eloScores.put("Inv", 1000);
            eloScores.put("SAA", 1000);
        }else{
            // 获取当前用户ID
            Long currentUserId = currentUser.getId();

            // 查询数据库中ID小于当前用户ID的最近一条记录，即上一个用户
            User previousUser = userMapper.getPreviousUser(currentUserId);

            if (previousUser == null) {
                throw new AccountNotFoundException("没有找到上一个用户的记录！");
            }

            eloScores.put("CLBA", previousUser.getCLBA());
            eloScores.put("DA", previousUser.getDA());
            eloScores.put("HTBA", previousUser.getHTBA());
            eloScores.put("lsbPanda", previousUser.getLsbPanda());
            eloScores.put("refool", previousUser.getRefool());
            eloScores.put("ours", previousUser.getOurs());
            eloScores.put("Sig", previousUser.getSig());
            eloScores.put("clean", previousUser.getClean());
            eloScores.put("Inv", previousUser.getInv());
            eloScores.put("SAA", previousUser.getSAA());
        }
        log.info("用户和数据：{}", eloScores);
        return eloScores;
    }

    /**
     * 过往文件夹选择保存
     * @param userSaveSelectionDTO
     * @return
     */
    public synchronized void saveSelectionHistory(UserSaveSelectionDTO userSaveSelectionDTO) {
        String username = userSaveSelectionDTO.getUsername();
        List<UserSaveSelectionDTO.SelectionItem> selectedHistory = userSaveSelectionDTO.getSelectedHistory();
        UserResult userResult = new UserResult();
        userResult.setUsername(username);
        userResult.setCreateTime(LocalDateTime.now());
        userResult.setUpdateTime(LocalDateTime.now());
        for (int i = 0; i < selectedHistory.size() ; i++) {
            UserSaveSelectionDTO.SelectionItem selectionItem = selectedHistory.get(i);
            List<String> folders = selectionItem.getFolders();
            String result = selectionItem.getResult();

            try {
                Integer value = null;
                Method method = userResult.getClass().getMethod("setRound" + (i + 1), Integer.class);
                // 根据条件选择要调用的方法和参数
                //1 获胜 0 失败 2 全赢 -1 失败
                if (result.equals(folders.get(0))) {
                    value = 1;
                } else if (result.equals(folders.get(1))) {
                    value = 0;
                } else if (result.equals("allWin")) {
                    value = 2;
                } else if (result.equals("allLoss")) {
                    value = -1;
                }
                // 调用方法
                method.invoke(userResult, value);

            } catch (Exception e) {
                e.printStackTrace(); // 处理异常
            }
        }
        log.info("用户结果：{}", userResult);
        userMapper.insertResult(userResult);
    }

    /**
     * 获得分数
     * @return
     */
    public Map<String, Integer> getElo() {
        String filePath = "src/main/resources/static/img/elo.txt";
        Map<String, Integer> score = new HashMap<>();
        for(int number =3; number<46; number++){
            boolean validResult = false;
            int attemptNumber = number;
            while (!validResult && attemptNumber < 46){
                // 使用 try-with-resources 确保 BufferedReader 在使用后正确关闭
                try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                    // 将所有行读取到一个列表中
                    List<LineWithIndex> linesWithIndex = new ArrayList<>();
                    // 逐行读取文件内容
                    String line;
                    int lineNumber = 1;
                    while ((line = br.readLine()) != null) {
                        linesWithIndex.add(new LineWithIndex(lineNumber, line));
                        lineNumber++;
                    }
                    // 打乱行的顺序
                    Collections.shuffle(linesWithIndex);
                    // 按打乱后的顺序处理每一行
                    for (LineWithIndex lineWithIndex : linesWithIndex) {
                        // 获取原始行号和内容
                        int round = lineWithIndex.getLineNumber();
                        String content = lineWithIndex.getContent();
                        // 去除行中的方括号、单引号，分隔逗号和空格
                        content = content.replace("[", "").replace("]", "").replace("'", "");
                        // 按逗号分隔字符串，得到方法名称
                        String[] parts = content.split(", ");

                        // 确保每行有两个方法名称
                        if (parts.length == 2) {
                            // 读取两个方法名称
                            String method1 = parts[0];
                            String method2 = parts[1];
                            // 查询数据库以获取这两个方法在当前轮次的胜负结果
                            // 获取当前条数据的所有轮次分数
                            String round_i = "round_" + round;
                            Integer result = dbHelper.getResult(attemptNumber, round_i);
                            // 根据结果计算分数
                            if (result != null) {
                                validResult = true;
                                int[] sc = new int[2];
                                sc[0] = score.getOrDefault(method1, 1500);
                                sc[1] = score.getOrDefault(method2, 1500);
                                int[] newScore = updateScore(sc, result);
                                score.put(method1, newScore[0]);
                                score.put(method2, newScore[1]);
                                User user = new User();
                                user.setUsername("admin");
                                try {
                                    // 获取 method1 对应的字段，并将首字母转换为大写
                                    String capitalizedMethod1 = method1.substring(0, 1).toUpperCase() + method1.substring(1);
                                    // 获取 method1 对应的 setter 方法名
                                    String setterMethod1 = "set" + capitalizedMethod1;
                                    // 调用 method1 对应的 setter 方法
                                    Method method1Setter = User.class.getMethod(setterMethod1, Integer.class);
                                    method1Setter.invoke(user, newScore[0]);
                                    // 获取 method2 对应的字段，并将首字母转换为大写
                                    String capitalizedMethod2 = method2.substring(0, 1).toUpperCase() + method2.substring(1);
                                    // 获取 method2 对应的 setter 方法名
                                    String setterMethod2 = "set" + capitalizedMethod2;
                                    // 调用 method2 对应的 setter 方法
                                    Method method2Setter = User.class.getMethod(setterMethod2, Integer.class);
                                    method2Setter.invoke(user, newScore[1]);
                                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                                    e.printStackTrace(); // 处理异常
                                }
                                userMapper.update(user);
                            } else {
                                break;
                            }
                        }
                    }
                }catch (IOException e) {
                    System.err.println("文件读取失败: " + e.getMessage());
                }
                attemptNumber++;
            }
        }
        return userMapper.getElo();
    }

    private int[] updateScore(int[] score, int result) {
        // K-factor, 可以调整影响ELO变化的程度
        int K = 40;
        int firstElo = score[0]; // 默认 ELO 为 1500
        int otherElo = score[1];

        double expectedScoreFirst = 1 / (1 + Math.pow(10, (otherElo - firstElo) / 400.0));
        double expectedScoreOther = 1 / (1 + Math.pow(10, (firstElo - otherElo) / 400.0));

        //1 获胜 0 失败 2 全赢 -1 失败
        switch (result){
            case 0:
                firstElo += (int) (K * (0 - expectedScoreFirst));
                otherElo += (int) (K * (1 - expectedScoreOther));
                break;
            case 1:
                firstElo += (int) (K * (1 - expectedScoreFirst));
                otherElo += (int) (K * (0 - expectedScoreOther));
                break;
            case -1:
                firstElo += (int) (K * (0.5 - expectedScoreFirst));
                otherElo += (int) (K * (0.5 - expectedScoreOther));
                break;
            case 2:
                firstElo += (int) (K * (0.5 - expectedScoreFirst));
                otherElo += (int) (K * (0.5 - expectedScoreOther));
                break;
        }
        score[0] = firstElo;
        score[1] = otherElo;
        return score;
    }
    // 定义一个类来保存行号和内容
    @Getter
    private static class LineWithIndex {
        private final int lineNumber;
        private final String content;

        public LineWithIndex(int lineNumber, String content) {
            this.lineNumber = lineNumber;
            this.content = content;
        }

    }
}
