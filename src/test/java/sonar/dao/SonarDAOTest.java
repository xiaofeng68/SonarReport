package sonar.dao;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Log4j2
class SonarDAOTest {

    private SonarDAO dao;

    @BeforeEach
    void setUp() {
        dao = new SonarDAO("http://:9000");
    }

    @AfterEach
    void tearDown() {
        dao.close();
    }

    @Test
    void listProjects() {
        val projects = dao.listProjects();
        log.info(projects);
    }

    @Test
    void listUsers() {
        val users = dao.listUsers();
        log.info(users);
    }

    @Test
    void listAuthorsByKey() {
        log.info(dao.listAuthorsOfProject("cn.mastercom.backstage:Comm"));
    }

    @Test
    void listBugOfAuthor() {
        log.info(dao.listBugOfAuthor("cn.mastercom.backstage:Comm", "huangshanfeng"));
    }

    @Test
    void listVulnerabilityOfAuthor() {
        log.info(dao.listVulnerabilityOfAuthor("cn.mastercom.backstage:Comm", "huangshanfeng"));
    }

    @Test
    void listCodeSmellOfAuthor() {
        log.info(dao.listCodeSmellOfAuthor("cn.mastercom:rf_automatic_optimization", "zhangminke"));
    }
}