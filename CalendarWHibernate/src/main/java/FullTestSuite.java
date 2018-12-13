import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({JDBCUnitTestSuite.class,JPAUnitTestSuite.class})
public class FullTestSuite {
}
