package com.github.rodbate.profiles

import javax.sql.DataSource

import com.alibaba.druid.util.JdbcUtils
import org.springframework.context.annotation.AnnotationConfigApplicationContext


object Main {




  def main(args: Array[String]): Unit = {

    val CREATE_TABLE_SQL_TEMPLATE: String =
      "create table if not exists mobile_code_log_%s (" + "seq_id int(10) unsigned not null auto_increment," + "phone_no char(20) not null default '' comment '手机号'," + "phone_sn varchar(150) not null default '' comment '请求的手机序列号'," + "mobile_code char(10) not null default '' comment '手机验证码'," + "user_salt char(10) not null default '' comment '用于修改密码的独立salt'," + "is_success tinyint(1) unsigned not null default '0' comment '是否验证成功'," + "proxy_type tinyint(1) unsigned not null default '0' comment '短信发送平台: 0->云之讯  1->阿里大于'," + "client_ip varchar(150) not null default '' comment '用户ip'," + "mobile_code_next varchar(150) not null default '' comment '获取注册码的意图'," + "update_time timestamp not null default current_timestamp on update current_timestamp comment '更新时间'," + "primary key (seq_id)," + "key update_time (update_time)," + "key k1 (phone_no,is_success,update_time)" + ") comment='短信验证码发送日志'"


    val DROP_TABLE_SQL_TEMPLATE: String =
    "drop table if exists mobile_code_log_%s"


    val context = new AnnotationConfigApplicationContext("com.github.rodbate.profiles")


    val ds = context.getBean(classOf[DataSource])


    val conn = ds.getConnection

    conn.setAutoCommit(true)

    val statm = conn.createStatement()

    /*val rs = statm.executeQuery("show tables like 't_%'")

    val meta = rs.getMetaData


    while (rs.next())  {
      println("catalog name : " + meta.getCatalogName(1))
      println("column type : " + meta.getColumnType(1))
      println(rs.getString(1))
    }*/

    println(statm.executeUpdate(String.format(CREATE_TABLE_SQL_TEMPLATE, "20170619")))
    println(statm.executeUpdate(String.format(DROP_TABLE_SQL_TEMPLATE, "20170618")))

  }

}