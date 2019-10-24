package test.plugins.perrine.easyclemv0.registration;

import fr.univ_nantes.ec_clem.fixtures.fiducialset.TestFiducialSetFactory;
import fr.univ_nantes.ipopt.fixtures.HS071;
import org.junit.jupiter.api.Test;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.registration.likelihood.dimension2.general.Rigid2DGeneralMaxLikelihoodIpopt;

import javax.inject.Inject;

import java.util.Arrays;

import static org.coinor.Ipopt.SOLVE_SUCCEEDED;

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
    void test() {
        HS071 subjectUnderTest = new HS071();
        HS071 hs071 = new HS071();
        int status = hs071.OptimizeNLP();
        if( status == SOLVE_SUCCEEDED )
            System.out.println("\n\n*** The problem solved!");
        else
            System.out.println("\n\n*** The problem was not solved successfully!");

        double obj = hs071.getObjectiveValue();
        System.out.println("\nObjective Value = " + obj + "\n");

        double x[] = hs071.getVariableValues();
        hs071.print(x, "Primal Variable Values:");

        double constraints[] = hs071.getConstraintValues();
        hs071.print(constraints, "Constraint Values:");

        double MLB[] = hs071.getLowerBoundMultipliers();
        hs071.print(MLB, "Dual Multipliers for Variable Lower Bounds:");

        double MUB[] = hs071.getUpperBoundMultipliers();
        hs071.print(MUB, "Dual Multipliers for Variable Upper Bounds:");

        double lam[] = hs071.getConstraintMultipliers();
        hs071.print(lam, "Dual Multipliers for Constraints:");
    }

    @Test
    void test2() {
        FiducialSet simpleRotationFiducialSet = testFiducialSetFactory.getSimpleRotationFiducialSet2D();
        Rigid2DGeneralMaxLikelihoodIpopt subjectUnderTest = new Rigid2DGeneralMaxLikelihoodIpopt(simpleRotationFiducialSet);
        subjectUnderTest.OptimizeNLP();
        double[] x = subjectUnderTest.getVariableValues();
        System.out.println(Arrays.toString(x));
    }
}
