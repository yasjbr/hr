package ps.gov.epsilon.hr.firm.suspension

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([SuspensionExtensionListEmployee,ps.gov.epsilon.hr.firm.promotion.EmployeePromotion,ps.gov.epsilon.hr.firm.profile.EmploymentRecord,ps.gov.epsilon.hr.firm.suspension.SuspensionExtensionList,ps.gov.epsilon.hr.firm.suspension.SuspensionExtensionRequest])
@Domain([SuspensionExtensionListEmployee])
@TestMixin([HibernateTestMixin])
class SuspensionExtensionListEmployeeDomainSpec extends ConstraintUnitSpec {

    void "test SuspensionExtensionListEmployee all constraints"() {
        when:
        List<Map> constraints = [
                [field: "currentEmployeeMilitaryRank", value: null, testResult: TestResult.FAIL],
                [field: "currentEmployeeMilitaryRank", value: ps.gov.epsilon.hr.firm.promotion.EmployeePromotion.build(), testResult: TestResult.PASS],
                [field: "currentEmploymentRecord", value: null, testResult: TestResult.FAIL],
                [field: "currentEmploymentRecord", value: ps.gov.epsilon.hr.firm.profile.EmploymentRecord.build(), testResult: TestResult.PASS],
                [field: "effectiveDate", value: null, testResult: TestResult.FAIL],
                [field: "effectiveDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "fromDate", value: null, testResult: TestResult.FAIL],
                [field: "fromDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "periodInMonth", value: null, testResult: TestResult.FAIL],
                [field: "periodInMonth", value: 32769, testResult: TestResult.FAIL],
                [field: "periodInMonth", value: 32765, testResult: TestResult.PASS],
                [field: "recordStatus", value: null, testResult: TestResult.FAIL],
                [field: "suspensionExtensionList", value: null, testResult: TestResult.FAIL],
                [field: "suspensionExtensionList", value: ps.gov.epsilon.hr.firm.suspension.SuspensionExtensionList.build(), testResult: TestResult.PASS],
                [field: "suspensionExtensionListEmployeeNotes", value: null, testResult: TestResult.PASS],
                [field: "suspensionExtensionRequest", value: null, testResult: TestResult.FAIL],
                [field: "suspensionExtensionRequest", value: ps.gov.epsilon.hr.firm.suspension.SuspensionExtensionRequest.build(), testResult: TestResult.PASS],
                [field: "toDate", value: null, testResult: TestResult.FAIL],
                [field: "toDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(SuspensionExtensionListEmployee,constraints)
    }
}
