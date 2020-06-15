package sonar.dao;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sonar.entities.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Log4j2
class SonarDAOTest {

    private SonarDAO dao;

    @BeforeEach
    void setUp() {
        dao = new SonarDAO("http://10.16.128.39:9000");
        dao.login();
    }

//    @AfterEach
//    void tearDown() {
//        dao.close();
//    }
//
//    @Test
//    void listProjects() {
//        val projects = dao.listProjects();
//        log.info(projects);
//    }
//
//    @Test
//    void listUsers() {
//        val users = dao.listUsers();
//        log.info(users);
//    }
    @Test
    void listIssues(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        List<Times> list = dao.listTimes("si");
        for (int i=1;i<list.size();i++){
           try{
               Date begin = list.get(i-1).getDate();
               Date end = list.get(i).getDate();
               val issues = dao.listIssues("si",df.format(begin),df.format(end),1);
               System.out.println(df.format(begin)+","+df.format(end)+","+i);
               writeExcel(issues,"/si-"+df.format(begin)+"-"+df.format(end)+".xls");
           }catch (Exception e){
               System.out.println(i);
           }
        }


    }

//    @Test
//    void listAuthorsByKey() {
//        log.info(dao.listAuthorsOfProject("cn.mastercom.backstage:Comm"));
//    }
//
//    @Test
//    void listBugOfAuthor() {
//        log.info(dao.listBugOfAuthor("cn.mastercom.backstage:Comm", "huangshanfeng"));
//    }
//
//    @Test
//    void listVulnerabilityOfAuthor() {
//        log.info(dao.listVulnerabilityOfAuthor("cn.mastercom.backstage:Comm", "huangshanfeng"));
//    }
//
//    @Test
//    void listCodeSmellOfAuthor() {
//        log.info(dao.listCodeSmellOfAuthor("cn.mastercom:rf_automatic_optimization", "zhangminke"));
//    }
        public static void writeExcel(List<Issues> list, String path) {
            try {
                // Excel底部的表名
                String sheetn = "问题清单";
                // 用JXL向新建的文件中添加内容
                File myFilePath = new File(path);
                if (!myFilePath.exists())
                    myFilePath.createNewFile();
                OutputStream outf = new FileOutputStream(path);
                WritableWorkbook wwb = Workbook.createWorkbook(outf);
                jxl.write.WritableSheet writesheet = wwb.createSheet(sheetn, 1);
                // 设置标题
                if (list.size() > 0) {
                    writesheet.addCell(new Label(0, 0, "项目"));
                    writesheet.addCell(new Label(1, 0, "类型"));
                    writesheet.addCell(new Label(2, 0, "代码路径"));
                    writesheet.addCell(new Label(3, 0, "行数"));
                    writesheet.addCell(new Label(4, 0, "信息"));
                    writesheet.addCell(new Label(5, 0, "状态"));
                    writesheet.addCell(new Label(6, 0, "处理人"));
                    writesheet.addCell(new Label(7, 0, "反馈"));
                    writesheet.addCell(new Label(8, 0, "处理时间"));
                }
                // 内容添加
                for (int i = 0,j=1; i < list.size(); i++,j++) {
                    Issues s = list.get(i);
                    writesheet.addCell(new Label(0, j, (String) s.getProject()));
                    writesheet.addCell(new Label(1, j, getZhType((String) s.getType())));
                    writesheet.addCell(new Label(2, j, (String) s.getComponent()));
                    writesheet.addCell(new Label(3, j, (String) s.getLine()));
                    writesheet.addCell(new Label(4, j, (String) s.getMessage()));
                    writesheet.addCell(new Label(5, j, (String) s.getStatus()));
                    writesheet.addCell(new Label(6, j, (String) s.getAuthor()));
                    List<Comment> cl = s.getComments();
                    StringBuilder sb = new StringBuilder();
                    for(Comment c : cl){
                        sb.append(c.getMarkdown()+";");
                    }
                    writesheet.addCell(new Label(7, j, sb.toString()));

                    writesheet.addCell(new jxl.write.DateTime(8, j, s.getUpdateDate()));
                }
                wwb.write();
                wwb.close();
            } catch (WriteException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        private static String getZhType(String type){
            if("CODE_SMELL".equals(type)){
                return "一般问题";
            }else{
                return "严重问题";
            }
        }
}