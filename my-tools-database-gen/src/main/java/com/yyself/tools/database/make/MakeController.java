package com.yyself.tools.database.make;

import com.squareup.javapoet.*;
import com.yyself.tool.utils.ResponseResult;
import com.yyself.tools.database.enums.MakeTypeEnum;
import com.yyself.tools.database.vo.ClassModel;
import com.yyself.tools.database.vo.DatabaseGenVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.lang.model.element.Modifier;

import static com.yyself.tool.utils.CommonUtils.lowerName;


/**
 * @author yangyu
 */

public class MakeController extends AbstractMake {

    private static final String PREFIX = "I";

    public MakeController(DatabaseGenVo vo) {
        super(vo);
    }

    @Override
    public ClassModel makeClass() {
        String currentClassName = vo.getTableName() + "Controller";
        ClassName service = ClassName.get(vo.getPackages() + ".service", PREFIX + vo.getTableName() + "Service");
        ClassName entity = ClassName.get(vo.getPackages() + ".entity", vo.getTableName());

        String serviceName = lowerName(vo.getTableName()) + "Service";
        FieldSpec controllerField = FieldSpec.builder(service, serviceName)
                .addModifiers(Modifier.PRIVATE)
                .addAnnotation(Autowired.class)
                .build();


        TypeSpec.Builder typeSpec = TypeSpec.classBuilder(currentClassName)
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("@author " + vo.getAuthor() + "\n")
                .addAnnotation(RestController.class)
                .addAnnotation(AnnotationSpec.builder(RequestMapping.class).addMember("value", "$S", "/api/" + lowerName(vo.getTableName())).build())
                .addModifiers(Modifier.PUBLIC)
                .addField(controllerField)
                // 新增
                .addMethod(add(entity, vo.getTableName(), serviceName))
                .addMethod(list(entity, vo.getTableName(), serviceName))
                .addMethod(page(entity, vo.getTableName(), serviceName))
                .addMethod(id(entity, vo.getTableName(), serviceName))
                .addMethod(updateById(entity, vo.getTableName(), serviceName))
                .addMethod(deleteById(entity, vo.getTableName(), serviceName));

        if (vo.isEnableSwagger()) {
            typeSpec.addAnnotation(AnnotationSpec.builder(Api.class).addMember("tags", "$S", vo.getTableName() + "管理").build());
        }

        return ClassModel.builder()
                .javaFile(JavaFile.builder(vo.getPackages() + ".controller", typeSpec.build()).build())
                .classType(MakeTypeEnum.CONTROLLER.name())
                .build();
    }

    private MethodSpec add(ClassName entity, String tableName, String serviceName) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("add");
        if (vo.isEnableSwagger()) {
            builder.addAnnotation(AnnotationSpec.builder(ApiOperation.class)
                    .addMember("value", "$S", "/新增").build());
        }
        return builder.addAnnotation(AnnotationSpec.builder(PostMapping.class).addMember("value", "$S", "/v1/add").build())
                .addModifiers(Modifier.PUBLIC)
                .addParameter(entity, lowerName(tableName))
                .returns(ResponseResult.class)
                .addStatement("return ResponseResult.ok(" + serviceName + ".newUpdate().insert(" + lowerName(tableName) + "))")
                .build();
    }

    private MethodSpec list(ClassName entity, String tableName, String serviceName) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("list");
        if (vo.isEnableSwagger()) {
            builder.addAnnotation(AnnotationSpec.builder(ApiOperation.class)
                    .addMember("value", "$S", "/列表").build());
        }
        return builder.addAnnotation(AnnotationSpec.builder(GetMapping.class).addMember("value", "$S", "/v1/list").build())
                .addModifiers(Modifier.PUBLIC)
                .addParameter(entity, lowerName(tableName))
                .returns(ResponseResult.class)
                .addStatement("return ResponseResult.ok(" + serviceName + ".newQuery().orderByIdDesc().list(" + lowerName(tableName) + "))")
                .build();
    }

    private MethodSpec page(ClassName entity, String tableName, String serviceName) {
        ClassName page = ClassName.get("kot.bootstarter.kotmybatis.common", "Page");
        MethodSpec.Builder builder = MethodSpec.methodBuilder("page");
        if (vo.isEnableSwagger()) {
            builder.addAnnotation(AnnotationSpec.builder(ApiOperation.class)
                    .addMember("value", "$S", "/分页").build());
        }
        return builder.addAnnotation(AnnotationSpec.builder(GetMapping.class).addMember("value", "$S", "/v1/page").build())
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterizedTypeName.get(page, entity), "page")
                .addParameter(entity, lowerName(tableName))
                .returns(ResponseResult.class)
                .addStatement("return ResponseResult.ok(" + serviceName + ".newQuery().activeLike().orderByIdDesc().selectPage(page, " + lowerName(tableName) + "))")
                .build();
    }

    private MethodSpec id(ClassName entity, String tableName, String serviceName) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("findById");
        if (vo.isEnableSwagger()) {
            builder.addAnnotation(AnnotationSpec.builder(ApiOperation.class)
                    .addMember("value", "$S", "/根据id查询").build());
            builder.addAnnotation(AnnotationSpec.builder(ApiImplicitParam.class)
                    .addMember("name", "$S", "id")
                    .addMember("value", "$S", "主键id")
                    .addMember("required", "$L", "true")
                    .build());
        }
        return builder
                .addAnnotation(AnnotationSpec.builder(GetMapping.class)
                        .addMember("value", "$S", "/v1/id").build())
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Long.class, "id")
                .returns(ResponseResult.class)
                .addStatement("return ResponseResult.ok(" + serviceName + ".newQuery().findOne(" + tableName + ".builder().id(id).build()))")
                .build();
    }

    private MethodSpec updateById(ClassName entity, String tableName, String serviceName) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("updateById");
        if (vo.isEnableSwagger()) {
            builder.addAnnotation(AnnotationSpec.builder(ApiOperation.class)
                    .addMember("value", "$S", "/根据id更新").build());
            builder.addAnnotation(AnnotationSpec.builder(ApiImplicitParam.class)
                    .addMember("name", "$S", "id")
                    .addMember("value", "$S", "主键id")
                    .addMember("required", "$L", "true")
                    .build());
        }
        return builder
                .addAnnotation(AnnotationSpec.builder(PostMapping.class)
                        .addMember("value", "$S", "/v1/updateById").build())
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(entity, lowerName(tableName)).addAnnotation(RequestBody.class).build())
                .returns(ResponseResult.class)
                .addStatement("return ResponseResult.ok(" + serviceName + ".newUpdate().updateById(" + lowerName(tableName) + "))")
                .build();
    }

    private MethodSpec deleteById(ClassName entity, String tableName, String serviceName) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("deleteById");
        if (vo.isEnableSwagger()) {
            builder.addAnnotation(AnnotationSpec.builder(ApiOperation.class)
                    .addMember("value", "$S", "/根据id删除").build());
            builder.addAnnotation(AnnotationSpec.builder(ApiImplicitParam.class)
                    .addMember("name", "$S", "id")
                    .addMember("value", "$S", "主键id")
                    .addMember("required", "$L", "true")
                    .build());
        }

        return builder
                .addAnnotation(AnnotationSpec.builder(PostMapping.class)
                        .addMember("value", "$S", "/v1/deleteById").build())
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(entity, lowerName(tableName)).addAnnotation(RequestBody.class).build())
                .returns(ResponseResult.class)
                .addStatement("return ResponseResult.ok(" + serviceName + ".newUpdate().delete(" + lowerName(tableName) + "))")
                .build();
    }

}

