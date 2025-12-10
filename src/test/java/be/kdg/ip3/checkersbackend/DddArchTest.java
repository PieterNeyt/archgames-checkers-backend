package be.kdg.ip3.checkersbackend;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import org.jmolecules.archunit.JMoleculesDddRules;

@AnalyzeClasses(packages = "be.kdg.ip3.checkersbackend")
class DddArchTest {
    @ArchTest
    void whenCheckingAllClasses_thenCodeFollowsAllDddPrinciples(final JavaClasses classes) {
        JMoleculesDddRules.all().check(classes);
    }
}
