package sonar.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import sonar.entities.Project;
import sonar.entities.User;
import sonar.respond_entities.FacetsRespond;
import sonar.respond_entities.ProjectsRespond;
import sonar.respond_entities.UsersRespond;
import sonar.utils.$;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * 从Sonar系统提供的Web Service接口读取数据的Dao类
 */
@Log4j2
public class SonarDAO implements Closeable {

    private final HttpClient client;
    private final HttpClientConnectionManager manager;
    private final String baseUrl;
    private static Gson jsonParser = new GsonBuilder().create();
    private static Properties prop = $.getProperties("/sonar-api-url.properties");

    public SonarDAO(String baseUrl) {
        this.baseUrl = correctUrl(baseUrl);
        this.manager = new PoolingHttpClientConnectionManager(60L, TimeUnit.SECONDS);
        this.client = HttpClients.createMinimal(manager);
    }

    private String correctUrl(String baseUrl) {
        $.checkNonBlank(baseUrl);
        $.checkNonNull(baseUrl);
        if (!baseUrl.startsWith("http://"))
            baseUrl = "http://" + baseUrl;
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
    }

    /**
     * 工具方法，用来发送http请求，并将http报文通过Gson解析为Java对象
     *
     * @param client  发送http请求的客户端
     * @param propKey 配置文件里对应http请求url的key
     * @param c       json转化的类
     * @param args    http url带的参数
     * @return Gson解析后的返回的对象
     * @throws IOException 执行http请求时可能出现的IO异常
     */
    private static <T> T execute(String baseUrl, HttpClient client, String propKey, Class<T> c, Object... args) throws IOException {
        $.checkNonNull(baseUrl, client, propKey, c, args);
        $.checkNonBlank(baseUrl, propKey);
        val api = baseUrl + MessageFormat.format(prop.getProperty(propKey), args);
        HttpResponse response = client.execute(new HttpGet(api));
        val json = new InputStreamReader(response.getEntity().getContent());
        return jsonParser.fromJson(json, c);
    }

    /**
     * 列出Sonar中的所有项目
     */
    public List<Project> listProjects() {
        try {
            return execute(baseUrl, client, "search_projects", ProjectsRespond.class).getComponents();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 列出Sonar中的所有用户
     */
    public List<User> listUsers() {
        try {
            return execute(baseUrl, client, "search_users", UsersRespond.class).getUsers();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 执行sonar相应api并将数据封装成java对象后返回
     *
     * @param propKey 配置在sonar-api-url.properties文件的key
     * @param args    sonar-api-url.properties文件的key对应的sonar web service接口所需的参数
     * @return 将web service返回的json数据封装成一个java对象后返回
     */
    private FacetsRespond listFacets(String propKey, Object... args) {
        $.checkNonNull(propKey, args);
        try {
            return execute(baseUrl, client, propKey, FacetsRespond.class, args);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return new FacetsRespond();
        }
    }

    public Map<String, Integer> listAuthorsOfProject(String projectKey) {
        return listFacets("project_authors", projectKey).toMapByProperty("authors");
    }

    /**
     * 该方法在{@link #listFacets(String, Object...)}的基础上，进一步限定查询条件：查询某个作者的Bug、漏洞或异味信息
     * @param author    作者的名称
     * @return  key是问题的等级，value是问题的个数
     */
    private Map<String, Integer> listFacetsOfAuthor(String propKey, String projectKey, String author) {
        return listFacets(propKey, projectKey, author).toMapByProperty("severities");
    }

    public Map<String, Integer> listBugOfAuthor(String projectKey, String author) {
        return listFacetsOfAuthor("get_bug_from_author", projectKey, author);
    }

    public Map<String, Integer> listVulnerabilityOfAuthor(String projectKey, String author) {
        return listFacetsOfAuthor("get_vulnerability_from_author", projectKey, author);
    }

    public Map<String, Integer> listCodeSmellOfAuthor(String projectKey, String author) {
        return listFacetsOfAuthor("get_code_smell_from_author", projectKey, author);
    }

    @Override
    public void close() {
        manager.shutdown();
    }
}
