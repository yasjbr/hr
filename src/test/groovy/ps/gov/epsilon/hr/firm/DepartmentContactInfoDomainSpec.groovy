package ps.gov.epsilon.hr.firm

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult
import spock.lang.Unroll

import java.time.ZonedDateTime

@Build([DepartmentContactInfo,ps.gov.epsilon.hr.firm.Department])
@Domain([DepartmentContactInfo])
@TestMixin([HibernateTestMixin])
class DepartmentContactInfoDomainSpec extends ConstraintUnitSpec {

    void "test DepartmentContactInfo all constraints"() {
        when:
        List<Map> constraints = [
                [field: "contactMethodId", value: null, testResult: TestResult.PASS],
                [field: "contactTypeId", value: null, testResult: TestResult.PASS],
                [field: "department", value: null, testResult: TestResult.FAIL],
                [field: "department", value: ps.gov.epsilon.hr.firm.Department.build(), testResult: TestResult.PASS],
                [field: "fromDate", value: null, testResult: TestResult.FAIL],
                [field: "fromDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "locationId", value: null, testResult: TestResult.PASS],
                [field: "toDate", value: null, testResult: TestResult.FAIL],
                [field: "toDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
                [field: "value", value: null, testResult: TestResult.PASS],
                [field: "value", value: " ", testResult: TestResult.PASS],
                [field: "value", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "value", value: getLongString(20000), testResult: TestResult.PASS],
        ]
        then:
        validateObject(DepartmentContactInfo,constraints)
    }


    void "test custom validation for value"(){

        when:
        DepartmentContactInfo departmentContactInfoFail = DepartmentContactInfo.buildWithoutValidation(value:null,locationId:null)
        DepartmentContactInfo departmentContactInfoPass = DepartmentContactInfo.buildWithoutValidation(value:"value",locationId:null)

        then:
        !departmentContactInfoFail.validate()
        departmentContactInfoFail.errors.allErrors.toString().contains("DepartmentContactInfo.value.error")
        departmentContactInfoPass.validate()
        !departmentContactInfoPass?.hasErrors()

    }

}
