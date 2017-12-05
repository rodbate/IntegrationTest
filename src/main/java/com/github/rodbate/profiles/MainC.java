package com.github.rodbate.profiles;


import java.util.concurrent.ConcurrentHashMap;

public class MainC {


    private static final String CREATE_TABLE_SQL_TEMPLATE =
            "create table if not exists mobile_code_log_%s (" +
                    "seq_id int(10) unsigned not null auto_increment," +
                    "phone_no char(20) not null default '' comment '手机号'," +
                    "phone_sn varchar(150) not null default '' comment '请求的手机序列号'," +
                    "mobile_code char(10) not null default '' comment '手机验证码'," +
                    "user_salt char(10) not null default '' comment '用于修改密码的独立salt'," +
                    "is_success tinyint(1) unsigned not null default '0' comment '是否验证成功'," +
                    "proxy_type tinyint(1) unsigned not null default '0' comment '短信发送平台: 0->云之讯  1->阿里大于'," +
                    "client_ip varchar(150) not null default '' comment '用户ip'," +
                    "mobile_code_next varchar(150) not null default '' comment '获取注册码的意图'," +
                    "update_time timestamp not null default current_timestamp on update current_timestamp comment '更新时间'," +
                    "primary key (seq_id)," +
                    "key update_time (update_time)," +
                    "key k1 (phone_no,is_success,update_time)" +
                    ") comment='短信验证码发送日志'";



    private static final String DROP_TABLE_SQL_TEMPLATE = "drop table if exists mobile_code_log_%s";


    //当天建表与删除过期表的标记  true 表示这两个动作已完成 直接插入数据就行;
    private final ConcurrentHashMap<String, Boolean> dayToCreateAndDropTableFlag = new ConcurrentHashMap<>();


    /*private void tryCreateNewTableAndDropExpireTable(){
        String today = todayYYYYMMddStr();
        String expireDay = todayYYYYMMddByLastMonthStr();


        Boolean initTable = dayToCreateAndDropTableFlag.get(today);
        if (initTable == null) {
            //先移除过期
            boolean remove = dayToCreateAndDropTableFlag.remove(expireDay);

            //还未初始化今天的表  和 删除已经过期的表
            Boolean old = dayToCreateAndDropTableFlag.putIfAbsent(today, Boolean.TRUE);
            if (old == null) {
                //进行表创建与删除操作
                String createTableSql = String.format(CREATE_TABLE_SQL_TEMPLATE, today);
                jt_ronald_0000.update(createTableSql);

                if (remove) {
                    String dropTableSql = String.format(DROP_TABLE_SQL_TEMPLATE, expireDay);
                    jt_ronald_0000.update(dropTableSql);
                }
            }
        }
    }


    public void add(Mobile_code_log mobile_code) {
        tryCreateNewTableAndDropExpireTable();
        add0(mobile_code);
    }


    private void add0(Mobile_code_log mobile_code){
        String sql = String.format("insert into mobile_code_log_%s", todayYYYYMMddStr());
        jt_ronald_0000.insertOne(sql, mobile_code);
    }

    public void update_is_success(Mobile_codeBo.Mobile_code mc, boolean success) {
        String sql = String.format("UPDATE mobile_code_log_%s set is_success=? where phone_no=? and mobile_code=? and user_salt=?", todayYYYYMMddStr());
        jt_ronald_0000.update(sql, success, mc.phone, mc.code, mc.salt);
    }



    //获取今天的时间日期
    private String todayYYYYMMddStr(){
        DatetimeUtils.Datetime now = DatetimeUtils.now();
        return DatetimeUtils.format(now, "yyyyMMdd");
    }

    private String todayYYYYMMddByLastMonthStr() {
        DatetimeUtils.Datetime lastMonth = DatetimeUtils.now().minusMonths(1);
        return DatetimeUtils.format(lastMonth, "yyyyMMdd");
    }*/
}
