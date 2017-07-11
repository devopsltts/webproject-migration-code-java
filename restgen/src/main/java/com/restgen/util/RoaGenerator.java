
package com.restgen.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

public class RoaGenerator {
    private static Logger logger = Logger.getLogger(RoaGenerator.class);

    public static void main(String[] args) {

        String destPath = "D:\\source\\2017-07-11\\swing2webapp\\restgen\\src\\main\\java";
        List<String> methodList = new ArrayList<String>();
        String method1 =
                "public static int save(Accountant a){\n int status=0; \n try{ \n Connection con=getCon(); \n PreparedStatement ps=con.prepareStatement(\"insert into feereport_accountant(name,password,email,contactno) values(?,?,?,?)\"); \n ps.setString(1,a.getName()); \n ps.setString(2,a.getPassword()); \n ps.setString(3,a.getEmail()); \n ps.setString(4,a.getContactno()); \n status=ps.executeUpdate(); \n con.close(); \n }catch(Exception e){System.out.println(e);} \n return status; \n }";
        String method2 =
                "public static List<Accountant> view(){ \n List<Accountant> list=new ArrayList<>(); \n try{ \n Connection con=getCon(); \n PreparedStatement ps=con.prepareStatement(\"select * from feereport_accountant\"); \n ResultSet rs=ps.executeQuery(); \n while(rs.next()){ \n Accountant a=new Accountant(); \n a.setId(rs.getInt(1)); \n a.setName(rs.getString(2)); \n a.setPassword(rs.getString(3)); \n a.setEmail(rs.getString(4)); \n a.setContactno(rs.getString(5)); \n list.add(a); \n } \n con.close(); \n }catch(Exception e){System.out.println(e);} \n return list; \n }";
        String method3 =
                "public static Connection getCon(){ \n Connection con=null; \n try{ \n Class.forName(\"com.mysql.jdbc.Driver\"); \n con=DriverManager.getConnection(\"jdbc:mysql://localhost:3306/test\",\"\",\"\"); \n }catch(Exception e){System.out.println(e);} \n return con; \n }";
        methodList.add(method1);
        methodList.add(method2);
        methodList.add(method3);
        generateRoaInterface(methodList, destPath);
    }

    public static void generateRoaInterface(List<String> rawMethodList, String outputPath) {
        
        logger.info("------------Generating ROA interfaces-------------------");
        AnnotationSpec classParam = AnnotationSpec.builder(Path.class).addMember("value", "$S", "/api").build();
        AnnotationSpec serviceAnnotation = AnnotationSpec.builder(Service.class).build();
        AnnotationSpec produceParam =
                AnnotationSpec.builder(Produces.class).addMember("value", "$S", "application/json").build();
        AnnotationSpec consumParam =
                AnnotationSpec.builder(Consumes.class).addMember("value", "$S", "application/json").build();
        TypeSpec.Builder serviceBuilder = TypeSpec.classBuilder("FeeReportRoaService");
        serviceBuilder.addModifiers(Modifier.PUBLIC);
        List<RoaMethod> roaMethodList = MethodParser.parseMethod(rawMethodList);
        for(RoaMethod method : roaMethodList) {
            AnnotationSpec path = AnnotationSpec.builder(Path.class)
                    .addMember("value", "$S", "/" + method.getMethodName().toLowerCase()).build();
            MethodSpec.Builder methodBuild = MethodSpec.methodBuilder(method.getMethodName()).addAnnotation(path)
                    .addAnnotation(getHTTPMethodType(method)).addAnnotation(produceParam).addAnnotation(consumParam)
                    .addModifiers(Modifier.PUBLIC).returns(method.getReturnType()).addStatement(method.getBody());
            List<MethodParam> params = method.getParamList();
            for(MethodParam m : params) {
                AnnotationSpec pathParam = null;
                Class paramAnnotation = getParamRequestType(m);
                ParameterSpec.Builder paramSpecBuilder =
                        ParameterSpec.builder(m.getParamType(), m.getParamName(), Modifier.FINAL);
                if(paramAnnotation != null) {
                    pathParam = AnnotationSpec.builder(getParamRequestType(m))
                            .addMember("value", "$S", m.getParamName()).build();
                    paramSpecBuilder.addAnnotation(pathParam);
                }
                ParameterSpec paramSpec = paramSpecBuilder.build();
                methodBuild.addParameter(paramSpec);
            }
            MethodSpec methodSpec = methodBuild.build();
            serviceBuilder.addMethod(methodSpec);
        }
        TypeSpec serviceSpec = serviceBuilder.addAnnotation(serviceAnnotation).addAnnotation(classParam).build();
        JavaFile javaFile = JavaFile.builder("com.huawei.rest", serviceSpec).build();
        logger.info("com.huawei.rest.FeeReportRoaService.java generated. path = " + outputPath);
        try {
            File sourcePath = new File(outputPath);
            javaFile.writeTo(sourcePath);
        } catch(IOException e) {
            logger.error(e);
        }
    }

    private static Class getHTTPMethodType(RoaMethod method) {
        if(method.getMethodName().indexOf("get") != -1 || method.getMethodName().indexOf("fetch") != -1
                || method.getMethodName().indexOf("read") != -1) {
            method.setHttpMethodType(GET.class);
            return GET.class;
        }

        if(method.getMethodName().indexOf("save") != -1 || method.getMethodName().indexOf("create") != -1) {
            method.setHttpMethodType(POST.class);
            return POST.class;
        }

        if(method.getMethodName().indexOf("update") != -1 || method.getMethodName().indexOf("modify") != -1) {
            method.setHttpMethodType(PUT.class);
            return PUT.class;
        }

        if(method.getMethodName().indexOf("delete") != -1 || method.getMethodName().indexOf("remove") != -1) {
            method.setHttpMethodType(DELETE.class);
            return DELETE.class;
        }
        return GET.class;
    }

    private static Class getParamRequestType(MethodParam mParam) {
        // NOTE : Path parameters could not be generated in current scenario.
        if(GET.class.equals(mParam.getParentMethod().getHttpMethodType())) {
            return QueryParam.class;
        } else {
            if(mParam.getParamType().equals(String.class) || mParam.getParamType().equals(Integer.class)) {
                return HeaderParam.class;
            }
            return null;
        }
    }
}
