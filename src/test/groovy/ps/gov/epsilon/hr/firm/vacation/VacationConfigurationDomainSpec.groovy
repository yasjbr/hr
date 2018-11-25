package ps.gov.epsilon.hr.firm.vacation

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([VacationConfiguration, ps.gov.epsilon.hr.firm.Firm, ps.gov.epsilon.hr.firm.lookups.MilitaryRank, ps.gov.epsilon.hr.firm.vacation.lookup.VacationType])
@Domain([VacationConfiguration])
@TestMixin([HibernateTestMixin])
class VacationConfigurationDomainSpec extends ConstraintUnitSpec {

    void "test VacationConfiguration all constraints"() {
        when:
        List<Map> constraints = [
                [field: "allowedValue", value: null, testResult: TestResult.FAIL],
                [field: "allowedValue", value: 32769, testResult: TestResult.FAIL],
                [field: "allowedValue", value: 32765, testResult: TestResult.PASS],
                [field: "checkForAnnualLeave", value: null, testResult: TestResult.FAIL],
                [field: "employmentPeriod", value: null, testResult: TestResult.FAIL],
                [field: "employmentPeriod", value: 32769, testResult: TestResult.FAIL],
                [field: "employmentPeriod", value: 32765, testResult: TestResult.PASS],
                [field: "religionId", value: 32765, testResult: TestResult.PASS],
                [field: "religionId", value: null, testResult: TestResult.PASS],
                [field: "religionId", value: " ", testResult: TestResult.PASS],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "frequency", value: null, testResult: TestResult.FAIL],
                [field: "frequency", value: 32769, testResult: TestResult.FAIL],
                [field: "frequency", value: 32765, testResult: TestResult.PASS],
                [field: "isBreakable", value: null, testResult: TestResult.FAIL],
                [field: "isTransferableToNewYear", value: null, testResult: TestResult.FAIL],
                [field: "isExternal", value: null, testResult: TestResult.FAIL],
                [field: "isExternal", value: true, testResult: TestResult.PASS],
                [field: "isExternal", value: false, testResult: TestResult.PASS],
                [field: "maritalStatusId", value: null, testResult: TestResult.PASS],
                [field: "maxAllowedValue", value: null, testResult: TestResult.FAIL],
                [field: "maxAllowedValue", value: 32769, testResult: TestResult.FAIL],
                [field: "maxAllowedValue", value: 32765, testResult: TestResult.PASS],
                [field: "militaryRank", value: null, testResult: TestResult.FAIL],
                [field: "militaryRank", value: ps.gov.epsilon.hr.firm.lookups.MilitaryRank.build(), testResult: TestResult.PASS],
                [field: "sexTypeAccepted", value: null, testResult: TestResult.FAIL],
                [field: "takenFully", value: null, testResult: TestResult.FAIL],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
                [field: "vacationTransferValue", value: null, testResult: TestResult.FAIL],
                [field: "vacationTransferValue", value: -2147483647, testResult: TestResult.FAIL],
                [field: "vacationTransferValue", value: 2147483645, testResult: TestResult.PASS],
                [field: "vacationType", value: null, testResult: TestResult.FAIL],
                [field: "vacationType", value: ps.gov.epsilon.hr.firm.vacation.lookup.VacationType.build(), testResult: TestResult.PASS],
        ]
        then:
        validateObject(VacationConfiguration, constraints)
    }
}
