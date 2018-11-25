package ps.gov.epsilon.hr.firm.suspension

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([SuspensionListEmployee,ps.gov.epsilon.hr.firm.promotion.EmployeePromotion,ps.gov.epsilon.hr.firm.profile.EmploymentRecord,ps.gov.epsilon.hr.firm.profile.Employee,ps.gov.epsilon.hr.firm.suspension.SuspensionList,ps.gov.epsilon.hr.firm.suspension.SuspensionRequest])
@Domain([SuspensionListEmployee])
@TestMixin([HibernateTestMixin])
class SuspensionListEmployeeDomainSpec extends ConstraintUnitSpec {

    void "test SuspensionListEmployee all constraints"() {
        when:
        List<Map> constraints = [
                [field: "currentEmployeeMilitaryRank", value: null, testResult: TestResult.FAIL],
                [field: "currentEmployeeMilitaryRank", value: ps.gov.epsilon.hr.firm.promotion.EmployeePromotion.build(), testResult: TestResult.PASS],
                [field: "currentEmploymentRecord", value: null, testResult: TestResult.FAIL],
                [field: "currentEmploymentRecord", value: ps.gov.epsilon.hr.firm.profile.EmploymentRecord.build(), testResult: TestResult.PASS],
                [field: "effectiveDate", value: null, testResult: TestResult.FAIL],
                [field: "effectiveDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "employee", value: null, testResult: TestResult.FAIL],
                [field: "employee", value: ps.gov.epsilon.hr.firm.profile.Employee.build(), testResult: TestResult.PASS],
                [field: "fromDate", value: null, testResult: TestResult.FAIL],
                [field: "fromDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "periodInMonth", value: null, testResult: TestResult.FAIL],
                [field: "periodInMonth", value: 32769, testResult: TestResult.FAIL],
                [field: "periodInMonth", value: 32765, testResult: TestResult.PASS],
                [field: "recordStatus", value: null, testResult: TestResult.FAIL],
                [field: "suspensionList", value: null, testResult: TestResult.FAIL],
                [field: "suspensionList", value: ps.gov.epsilon.hr.firm.suspension.SuspensionList.build(), testResult: TestResult.PASS],
                [field: "suspensionListEmployeeNotes", value: null, testResult: TestResult.PASS],
                [field: "suspensionRequest", value: null, testResult: TestResult.FAIL],
                [field: "suspensionRequest", value: ps.gov.epsilon.hr.firm.suspension.SuspensionRequest.build(), testResult: TestResult.PASS],
                [field: "suspensionType", value: null, testResult: TestResult.FAIL],
                [field: "toDate", value: null, testResult: TestResult.FAIL],
                [field: "toDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(SuspensionListEmployee,constraints)
    }
}
