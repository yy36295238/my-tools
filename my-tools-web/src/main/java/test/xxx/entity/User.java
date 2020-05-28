package test.xxx.entity;

import io.swagger.annotations.ApiModelProperty;
import java.lang.Long;
import java.lang.String;
import java.util.Date;
import kot.bootstarter.kotmybatis.annotation.Column;
import kot.bootstarter.kotmybatis.annotation.ID;
import kot.bootstarter.kotmybatis.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tom
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_user")
public class User {
  /**
   * AUTO_INCREMENT
   */
  @ApiModelProperty(
      value = "AUTO_INCREMENT",
      dataType = "Long",
      name = "id"
  )
  @Column("id")
  @ID("id")
  private Long id;

  /**
   * '作者姓名'
   */
  @ApiModelProperty(
      value = "'作者姓名'",
      dataType = "String",
      name = "name"
  )
  @Column("name")
  @ID("name")
  private String name;

  /**
   * '创建日期'
   */
  @ApiModelProperty(
      value = "'创建日期'",
      dataType = "Date",
      name = "createTime"
  )
  @Column("create_time")
  @ID("create_time")
  private Date createTime;

  /**
   * '修改日期'
   */
  @ApiModelProperty(
      value = "'修改日期'",
      dataType = "Date",
      name = "updateTime"
  )
  @Column("update_time")
  @ID("update_time")
  private Date updateTime;
}
