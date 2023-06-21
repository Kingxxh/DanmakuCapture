package com.bilibili.danmu.frame;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bilibili.danmu.Main;
import com.bilibili.danmu.client.WebSocket;
import com.bilibili.danmu.pojo.AccessPackage;
import com.bilibili.danmu.pojo.Certification;
import com.bilibili.danmu.pojo.Room;
import com.bilibili.danmu.thread.HeartThread;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.charset.StandardCharsets;

public class MyFrame extends JFrame {
    private Container container;
    private Room room;//用于保存房间信息
    private CardLayout cardLayout;
    private JPanel welcome;//表格布局欢迎页
    private JPanel welcomePanel;//网管布局
    private JPanel inputPanel;//输入直播间号的页面，表格布局
    private JPanel index;//首页
    private JPanel indexPanel;//首页，也采用网格布局
    private JPanel indexMessagePanel;
    private JPanel indexMessageContentPanel;//直播间信息栏
    private JPanel danmuPanel;//弹幕页内容
    private JPanel danmuContentPanel;//弹幕真实内容
    private JScrollPane  danmuContenScrollPane;//需要将弹幕页内容加入到这个带滚动条的面板
    private WebSocket webSocket;//连接弹幕路径的webSocket
    public MyFrame(String title){
        super(title);
        this.container = this.getContentPane();//获得总页面
        this.container.setLayout(new CardLayout());//总布局使用卡片布局
        this.cardLayout = (CardLayout) container.getLayout();//获取布局器
        this.welcome = new JPanel();
        this.welcome.setLayout(new BorderLayout());//欢迎页表格布局
        this.welcomePanel = new JPanel();
        this.welcomePanel.setLayout(new GridLayout(1,2));
        this.welcome.add(this.welcomePanel,BorderLayout.CENTER);
        this.container.add("welcome",this.welcome);
        this.inputPanel = new JPanel();
        this.inputPanel.setLayout(new BorderLayout());//表格布局
        this.container.add("input",this.inputPanel);
        this.index = new JPanel();
        this.index.setLayout(new BorderLayout());//采用边框布局
        this.indexPanel = new JPanel();
        this.indexPanel.setLayout(new BorderLayout());
        this.index.add(this.indexPanel,BorderLayout.CENTER);
        this.container.add("index",this.index);

        //创建欢迎页的内容
        JPanel welcomeImagePanel = new JPanel();//欢迎页显示图片的部分
        welcomeImagePanel.setLayout(new FlowLayout());//流布局
        welcomeImagePanel.setBorder(BorderFactory.createEmptyBorder(100,0,0,0));
        JLabel welcomeImageLabel = new JLabel();//显示图片的文本
        welcomeImageLabel.setIcon(new ImageIcon(Main.class.getResource("/img/man.png")));//设置图片显示在此label
        welcomeImagePanel.add(welcomeImageLabel);//添加图片
        this.welcomePanel.add(welcomeImagePanel);//添加元素

        JPanel welcomeContentPanel = new JPanel();//用于装“平台选择下拉框”和“版本信息”
        welcomeContentPanel.setLayout(new BorderLayout());//设置2行1列
        welcomeContentPanel.setBorder(BorderFactory.createEmptyBorder(100,0,100,100));
        this.welcomePanel.add(welcomeContentPanel);
        JPanel selectPanel = new JPanel();//用于装“平台选择下拉框”
        selectPanel.setLayout(new FlowLayout());//流布局
        JLabel selectLabel = new JLabel("Live Platform Selection");
        welcomeContentPanel.add(selectLabel);
        DisabledItemsComboBox selectBox = new DisabledItemsComboBox();//创建下拉框
        selectBox.addItem("bilibili");//添加选项1
        selectBox.addItem("(To Be Continue)",true);//添加选项2，并设置不可选
        selectBox.addActionListener((ActionEvent e)->{//给下拉选择框添加监听器
            String item = (String) selectBox.getSelectedItem();
            //获取下拉列表当前选择的元素，getSelectedItem返回值数据
            //类型是Object。但是我们知道元素是String，所以可以强转成String
            if(item.equals("bilibili")) {
                this.cardLayout.show(this.container,"input");//切换到输入页
            }
        });
        selectPanel.add(selectBox);//添加下拉框
        welcomeContentPanel.add(selectPanel,BorderLayout.PAGE_START);
        JPanel messagePanel = new JPanel();//用于展示信息
        messagePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        messagePanel.setLayout(new GridLayout(6,1));//6行1列
        messagePanel.add(new JLabel("2023 Spring CSE687"));
        messagePanel.add(new JLabel("Version 2.1"));
        messagePanel.add(new JLabel("Developer: Team Avengers"));
        messagePanel.add(new JLabel("Wenzhe Ma    Xuhao Xie"));
        messagePanel.add(new JLabel("Xingyi Wang   Yiqi Liu"));
        messagePanel.add(new JLabel("Contact Information: wangxingyi_9901@163.com"));
        welcomeContentPanel.add(messagePanel,BorderLayout.CENTER);
        JPanel welcomeVersionPanel = new JPanel();
        welcomeVersionPanel.add(new JLabel("©2023 CSE687 Avengers"));
        welcomeVersionPanel.setLayout(new FlowLayout());
        this.welcome.add(welcomeVersionPanel,BorderLayout.PAGE_END);

        JPanel inputContentPanel = new JPanel();
        inputContentPanel.setLayout(new FlowLayout());
        inputContentPanel.setBorder(BorderFactory.createEmptyBorder(50,0,50,0));
        JTextField inputContentField = new JTextField(16);//用于输入直播间号
        JButton inputContentButton = new JButton("inner");
        inputContentPanel.add(new JLabel("Please Enter Live Room ID:"));
        inputContentPanel.add(inputContentField);
        inputContentPanel.add(inputContentButton);
        this.inputPanel.add(inputContentPanel,BorderLayout.PAGE_START);
        JPanel imageContentPanel = new JPanel();
        imageContentPanel.setLayout(new FlowLayout());//流布局
        imageContentPanel.setBorder(BorderFactory.createEmptyBorder(0,0,100,0));
        JLabel imageContentLabel = new JLabel();//显示图片的文本
        imageContentLabel.setIcon(new ImageIcon(Main.class.getResource("/img/bilibili.png")));//设置图片显示在此label
        imageContentPanel.add(imageContentLabel);//添加图片
        this.inputPanel.add(imageContentPanel,BorderLayout.CENTER);//添加元素
        JPanel inputVersionPanel = new JPanel();
        inputVersionPanel.add(new JLabel("©2023 CSE687 Avengers"));
        inputVersionPanel.setLayout(new FlowLayout());
        this.inputPanel.add(inputVersionPanel,BorderLayout.PAGE_END);//底部放入商标
        inputContentButton.addActionListener((ActionEvent e) -> {//给确定按钮添加点击时间
            String number = inputContentField.getText();//获取输入的内容，就是房间号
            WebSocket w = this.connectRoom(number);//尝试连接
            if(w == null){
                //w为null，结束方法
            }else{
                //w不为null，说明连接成功
                this.webSocket = w;
                HeartThread heartThread = new HeartThread(this.webSocket);//准备定时发送心跳包的线程
                heartThread.start();//执行发送心跳包的线程，并且是不放入主线程中执行，是独立出来的子线程
                inputContentField.setText("");//清空输入内容
                this.updateIndexMessageContentPanel();//更新直播间信息栏
                this.cardLayout.show(this.container,"index");//切换到首页
            }
        });

        /*contentPanel.add(contentLabel);//将三个要素添加进去
        contentPanel.add(contentField);
        contentPanel.add(contentButton);
        this.welcomePanel.add(contentPanel);//添加元素
        JPanel amessagePanel = new JPanel();//用于显示链接消息
        amessagePanel.setLayout(new FlowLayout());//流布局
        JLabel messageLabel = new JLabel();//显示消息
        messagePanel.setForeground(Color.RED);//这是错误消息，标红色
        messagePanel.add(messageLabel);
        this.welcomePanel.add(messagePanel);//添加元素*/

        //创建首页内容
        indexMessagePanel = new JPanel();
        indexMessagePanel.setLayout(new BorderLayout());
        indexMessagePanel.setBorder(BorderFactory.createEmptyBorder(50,25,50,50));
        indexMessagePanel.add(new JLabel("Live Room Information"),BorderLayout.PAGE_START);
        indexMessageContentPanel = new JPanel();
        indexMessageContentPanel.setLayout(new GridLayout(7,1));
        indexMessageContentPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        indexMessagePanel.add(indexMessageContentPanel,BorderLayout.CENTER);
        this.indexPanel.add(indexMessagePanel, BorderLayout.WEST);

        this.danmuPanel = new JPanel();//弹幕列表的panel
        this.danmuPanel.setLayout(new BorderLayout());
        this.danmuPanel.setBorder(BorderFactory.createEmptyBorder(50,0,0,25));
        JPanel danmuTitalPanel = new JPanel();
        danmuTitalPanel.setLayout(new FlowLayout());
        JLabel danmuTitalImage = new JLabel();//显示图片的文本
        danmuTitalImage.setIcon(new ImageIcon(Main.class.getResource("/img/television.png")));//设置图片显示在此label
        danmuTitalPanel.add(danmuTitalImage);
        danmuTitalPanel.add(new JLabel("Real-time Denmaku"));
        danmuPanel.add(danmuTitalPanel,BorderLayout.PAGE_START);
        this.danmuContentPanel = new JPanel();
        this.danmuContenScrollPane = new JScrollPane(danmuContentPanel);//在实例化带有滚动条的面板时，就要将嵌套的元素作为参数
        //放进去了，并且只能接受一个参数，所以JScrollPane能显示的内容只有一个，所以显示的唯一内容就是弹幕列表
        this.danmuContentPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.danmuPanel.add(this.danmuContenScrollPane,BorderLayout.CENTER);//正文
        this.indexPanel.add(this.danmuPanel,BorderLayout.CENTER);

        JPanel leavePanel = new JPanel();//离开按钮的内容
        leavePanel.setBorder(BorderFactory.createEmptyBorder(25,0,50,0));
        leavePanel.setLayout(new FlowLayout());//流布局
        JButton leaveButton = new JButton("退出直播间");
        leavePanel.add(leaveButton);
        this.indexPanel.add(leavePanel,BorderLayout.PAGE_END);

        JPanel indexVersionPanel = new JPanel();
        indexVersionPanel.add(new JLabel("©2023 CSE687 Avengers"));
        indexVersionPanel.setLayout(new FlowLayout());
        this.index.add(indexVersionPanel,BorderLayout.PAGE_END);//底部放入商标


        leaveButton.addActionListener((ActionEvent e)->{//退出该直播间的按钮
            int select = JOptionPane.showConfirmDialog(this,"是否退出直播间？","退出提示",JOptionPane.YES_NO_OPTION);
            if(select == 0){
                //select为0，点了确定
                if(this.webSocket.isOpen()){
                    //如果webSocket是开启状态，则关闭，关闭后对应的心跳进程也会关闭
                    this.webSocket.close();//关闭连接
                    this.danmuContentPanel.removeAll();//清空所有元素
                    this.danmuContenScrollPane.updateUI();
                    this.danmuContenScrollPane.repaint();//刷新页面
                    this.indexPanel.updateUI();
                    this.indexPanel.repaint();//刷新页面
                    this.index.updateUI();
                    this.index.repaint();//刷新页面
                    this.cardLayout.show(this.container,"welcome");//切换到欢迎页
                }
            }else {
                //不是0，则是点了取消
                return;//直接退出
            }
        });

        this.cardLayout.show(this.container, "welcome");//先展示欢迎页
    }
    public WebSocket connectRoom(String roomNumber){
        //参数label用于显示链接信息，roomNumber是链接的直播间号
        String url = "https://api.live.bilibili.com/room/v1/Room/get_info?room_id=" + roomNumber;
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().set(1,new StringHttpMessageConverter(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/x-www-form-urlencoded"));
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        Room room = new Room();
        if(response.getStatusCode() == HttpStatus.OK){
            //请求成功
            String json = response.getBody();
            JSONObject jsonObject = JSON.parseObject(json);
            if((Integer)jsonObject.get("code") == 0){
                //code==0，直播间在直播
                JSONObject data = jsonObject.getJSONObject("data");
                Integer roomId = (Integer)data.get("room_id");
                String name = data.getString("title");//直播间名
                room.setAreaName(data.getString("area_name"));
                room.setRoomId(roomId);//直播间号
                room.setName(name);//直播间名
                room.setOnline(data.getInteger("online"));
                url = "https://api.live.bilibili.com/room/v1/Room/room_init?id=" + roomId;
                response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
                json = response.getBody();
                jsonObject = JSONObject.parseObject(json);
                if((Integer)jsonObject.get("code") == 0){
                    data = jsonObject.getJSONObject("data");
                    if(((Integer)data.get("live_status")) == 0){
                        //为0，则是未开播
                        JOptionPane.showMessageDialog(this,"The room is not live yet!","prompt",JOptionPane.CANCEL_OPTION);
                        return null;//返回null表示链接失败
                    }else{
                        System.out.println("主播开播了");
                    }
                    room.setLiveStatus("living");//没有退出则是开播了
                    Integer userId = data.getInteger("uid");
                    room.setUserId(userId);
                    url = "https://api.live.bilibili.com/live_user/v1/Master/info?uid=" + userId;
                    response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
                    json = response.getBody();
                    jsonObject = JSONObject.parseObject(json);
                    if((Integer)jsonObject.get("code") == 0){
                        data = jsonObject.getJSONObject("data");
                        room.setFollowerNum(data.getInteger("follower_num"));
                        JSONObject info = data.getJSONObject("info");//主播的信息都在info里面
                        String username  = info.getString("uname");
                        room.setUsername(username);
                        url = "https://api.live.bilibili.com/room/v1/Danmu/getConf?room_id=" + room.getRoomId() + "&platform=pc&player=web";
                        response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
                        json = response.getBody();
                        jsonObject = JSONObject.parseObject(json);
                        if((Integer)jsonObject.get("code") == 0){
                            data = jsonObject.getJSONObject("data");
                            JSONArray hostList = data.getJSONArray("host_server_list");
                            JSONObject host = null;
                            try{
                                host = hostList.getJSONObject(hostList.size() - 1);
                                //正常的连接路径都会是数组的最后一个，所以获取最后一个元素即可
                            }catch (Exception e){
                                System.out.println(json);
                                JOptionPane.showMessageDialog(this,"Network Error !","prompt",JOptionPane.CANCEL_OPTION);
                                return null;//结束方法
                            }
                            String address = (String)host.get("host");
                            Integer port = (Integer)host.get("wss_port");
                            String webSocketUrl = "wss://" + address + ":" + port + "/sub";//webSocket的连接路径
                            room.setWebSocketUrl(webSocketUrl);
                            try{
                                WebSocket webSocket = new WebSocket(room,this.index,this.indexPanel,this.danmuContentPanel,this.danmuContenScrollPane);
                                Certification certification = new Certification(room.getRoomId());
                                json = JSONObject.toJSONString(certification);
                                byte[] jsonBytes = json.getBytes();//认证正文数据
                                int totalLength = jsonBytes.length + 16;
                                AccessPackage accessDataPackage = new AccessPackage(totalLength,jsonBytes);//认证包数据
                                try{
                                    webSocket.connectBlocking();//正式连接
                                    webSocket.send(accessDataPackage.getData().toByteArray());//发送认证包
                                }catch (Exception e){
                                    e.printStackTrace();
                                    JOptionPane.showMessageDialog(this,"Network Error !","prompt",JOptionPane.CANCEL_OPTION);
                                }
                                this.room = room;//保存room信息
                                return webSocket;//连接成功返回webSocket
                            }catch (Exception e){
                                e.printStackTrace();
                                return null;//连接失败返回null
                            }
                        }else {
                            JOptionPane.showMessageDialog(this,"Network Error !","prompt",JOptionPane.CANCEL_OPTION);
                            return null;
                        }
                    }else {
                        JOptionPane.showMessageDialog(this,"Network Error !","prompt",JOptionPane.CANCEL_OPTION);
                        return null;
                    }
                }else {
                    JOptionPane.showMessageDialog(this,"Network Error !","prompt",JOptionPane.CANCEL_OPTION);
                    return null;
                }
            }else{
                //直播不在
                JOptionPane.showMessageDialog(this,"The Live Room Doesn’t Exist ! ","prompt",JOptionPane.CANCEL_OPTION);
                return null;
            }
        }else{
            JOptionPane.showMessageDialog(this,"Network Error !","prompt",JOptionPane.CANCEL_OPTION);
            return null;
        }
    }
    private void updateIndexMessageContentPanel(){
        indexMessageContentPanel.removeAll();
        indexMessageContentPanel.add(new JLabel("Room Id:" + this.room.getRoomId()));
        indexMessageContentPanel.add(new JLabel("Room Name:" + this.room.getName()));
        indexMessageContentPanel.add(new JLabel("Presenter Name:" + this.room.getUsername()));
        indexMessageContentPanel.add(new JLabel("Area Name:" + this.room.getAreaName()));
        indexMessageContentPanel.add(new JLabel("Num of Fans:" + this.room.getFollowerNum()));
        indexMessageContentPanel.add(new JLabel("Status:" + this.room.getLiveStatus()));
        indexMessageContentPanel.add(new JLabel("Hotness:" + this.room.getOnline()));
        this.indexMessageContentPanel.updateUI();
        this.indexMessageContentPanel.repaint();//刷新页面
        this.indexMessagePanel.updateUI();
        this.indexMessagePanel.repaint();//刷新页面
        this.indexPanel.updateUI();
        this.indexPanel.repaint();//刷新页面
        this.index.updateUI();
        this.index.repaint();//刷新页面
    }
}
