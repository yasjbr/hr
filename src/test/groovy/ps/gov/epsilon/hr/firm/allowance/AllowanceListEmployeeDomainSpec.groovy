package ps.gov.epsilon.hr.firm.allowance

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([AllowanceListEmployee,ps.gov.epsilon.hr.firm.allowance.AllowanceList,ps.gov.epsilon.hr.firm.allowance.AllowanceRequest])
@Domain([AllowanceListEmployee])
@TestMixin([HibernateTestMixin])
class AllowanceListEmployeeDomainSpec extends ConstraintUnitSpec {

    void "test AllowanceListEmployee all constraints"() {
        when:
        List<Map> constraints = [
                [field: "allowanceList", value: null, testResult: TestResult.FAIL],
                [field: "allowanceList", value: ps.gov.epsilon.hr.firm.allowance.AllowanceList.build(), testResult: TestResult.PASS],
                [field: "allowanceListEmployeeNotes", value: null, testResult: TestResult.PASS],
                [field: "allowanceRequest", value: null, testResult: TestResult.PASS],
                [field: "allowanceRequest", value: ps.gov.epsilon.hr.firm.allowance.AllowanceRequest.build(), testResult: TestResult.PASS],
                [field: "recordStatus", value: null, testResult: TestResult.FAIL],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(AllowanceListEmployee,constraints)
    }
}
