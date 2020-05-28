package test.xxx.controller;

import com.yyself.tool.utils.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import java.lang.Long;
import kot.bootstarter.kotmybatis.common.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.xxx.entity.User;
import test.xxx.service.IUserService;

/**
 * @author tom
 */
@RestController
@RequestMapping("/api/v1/user")
@Api(
    tags = "管理API"
)
public class UserController {
  @Autowired
  private IUserService userService;

  @ApiOperation("/新增")
  @PostMapping("/add")
  public ResponseResult add(User user) {
    return ResponseResult.ok(userService.newUpdate().insert(user));
  }

  @ApiOperation("/列表")
  @GetMapping("/list")
  public ResponseResult list(User user) {
    return ResponseResult.ok(userService.newQuery().orderByIdDesc().list(user));
  }

  @ApiOperation("/分页")
  @GetMapping("/page")
  public ResponseResult page(Page<User> page, User user) {
    return ResponseResult.ok(userService.newQuery().activeLike().orderByIdDesc().selectPage(page, user));
  }

  @ApiOperation("/根据id查询")
  @ApiImplicitParam(
      name = "id",
      value = "主键id",
      required = true
  )
  @GetMapping("/id")
  public ResponseResult findById(Long id) {
    return ResponseResult.ok(userService.newQuery().findOne(User.builder().id(id).build()));
  }

  @ApiOperation("/根据id更新")
  @ApiImplicitParam(
      name = "id",
      value = "主键id",
      required = true
  )
  @PostMapping("/updateById")
  public ResponseResult updateById(User user) {
    return ResponseResult.ok(userService.newUpdate().updateById(user));
  }

  @ApiOperation("/根据id删除")
  @ApiImplicitParam(
      name = "id",
      value = "主键id",
      required = true
  )
  @PostMapping("/deleteById")
  public ResponseResult deleteById(User user) {
    return ResponseResult.ok(userService.newUpdate().delete(user));
  }
}
