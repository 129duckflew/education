import cn.duckflew.EduApplication;
import cn.duckflew.config.MinioConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {EduApplication.class})
@Slf4j
public class MinioTest
{
    @Autowired
    MinioConfigProperties properties;
    @Test
    public void config()
    {
        System.out.println(properties);
    }

}
