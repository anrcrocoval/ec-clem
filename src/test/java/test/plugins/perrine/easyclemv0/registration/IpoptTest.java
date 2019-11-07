package test.plugins.perrine.easyclemv0.registration;

import fr.univ_nantes.ec_clem.fixtures.fiducialset.TestFiducialSetFactory;
import fr.univ_nantes.ipopt.fixtures.HS071;
import org.coinor.Ipopt;
import org.junit.jupiter.api.Test;
import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IpoptTest {

    private TestFiducialSetFactory testFiducialSetFactory;

    public IpoptTest() {
        DaggerIpoptTestComponent.create().inject(this);
    }

    @Inject
    public void setTestFiducialSetFactory(TestFiducialSetFactory testFiducialSetFactory) {
        this.testFiducialSetFactory = testFiducialSetFactory;
    }

    @Test
    void testExample() {
        HS071 hs071 = new HS071();
        int status = hs071.OptimizeNLP();
        assertEquals(Ipopt.SOLVE_SUCCEEDED, status);
    }
}
