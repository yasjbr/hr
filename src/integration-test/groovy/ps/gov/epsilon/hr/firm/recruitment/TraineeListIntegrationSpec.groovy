package ps.gov.epsilon.hr.firm.recruitment

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.mock.web.MockHttpServletRequest
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo
import ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.enums.v1.EnumTrainingEvaluation
import ps.gov.epsilon.hr.firm.lookups.TrainingRejectionReason
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

import java.time.ZonedDateTime

@Integration
@Rollback
/**
 * integration test for TraineeList service
 */
class TraineeListIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  TraineeList
        service_domain =  TraineeListService
        entity_name = "traineeList"
        required_properties = PCPUtils.getRequiredFields( TraineeList)
        hashing_entity = "id"
        with_hashing_flag = false
        filtered_parameters = ["id"];
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        exclude_methods = ["delete", "autocomplete"]
    }


    /**
     * @goal test searchWithRemotingValues method.
     * @expectedResult known total count.
     */
    def "test searchWithRemotingValues"() {
        setup:
        println("*****************************test searchWithRemotingValues******************************************")
        saveEntity()
        saveEntity()
        def testInstance = saveEntity()
        Map map = [max: 10000, offset: 0]
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())

        session_parameters.each {key,value->
            PCPSessionUtils.setValue(key,getPropertyValue(value,testInstance))
        }

        when:
        def result = serviceInstance.searchWithRemotingValues(params)
        then:
//        result?.totalCount == (entity_total_count + 3)
//        filtered_parameters.each { property ->
//            getPropertyValue(property,result[0]) == getPropertyValue(property,testInstance)
//        }
        println("test instance searchWithRemotingValues done with totalCount ${result.totalCount}")
        println("test instance searchWithRemotingValues done with result ${result}")
    }

    /**
     * @goal test searchWithRemotingValues method with filter data.
     * @expectedResult known total count.
     */
    def "test filter searchWithRemotingValues"() {
        setup:
        println("*****************************test filter searchWithRemotingValues******************************************")
        saveEntity()
        saveEntity()
        def testInstance = saveEntity()
        Map map = [max: 10000, offset: 0]
        filtered_parameters.each { String property ->
            sendParams(property,testInstance,map)
        }
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        def result

        session_parameters.each {key,value->
            PCPSessionUtils.setValue(key,getPropertyValue(value,testInstance))
        }

        when:
        result = serviceInstance.searchWithRemotingValues(params)
        then:
//        result.totalCount == 1
//        filtered_parameters.each { property ->
//            getPropertyValue(property,result[0]) == getPropertyValue(property,testInstance)
//        }
        println("test instance searchWithRemotingValues done with result ${result}")
    }

    /**
     * @goal test save method.
     * @expectedResult valid instance.
     */
    def "test success save"() {
        setup:
        println("*****************************test success save******************************************")
        def testInstance
        def instanceToSave = saveEntity(null,true)
        Map map = [:]
        required_properties.each { String property ->
            def value = getPropertyValue(property, instanceToSave)
            if (value == null) {
                value = entity_name + "_" + property + "_" + counter
            }
            map.put(property, value)
        }
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        when:
        domain_class.withTransaction { status ->
            testInstance = serviceInstance.save(params)
            status.setRollbackOnly()
        }
        then:
        testInstance != null
        required_properties.each { String property ->
            getPropertyValue(property,testInstance) != null
            getPropertyValue(property,testInstance) == getPropertyValue(property,map)
        }
        testInstance.errors.allErrors.size() == 0
        println("test save success done with instance ${testInstance}")
    }

    /**
     * @goal test save method.
     * @expectedResult null instance and contains errors.
     */
    def "test fail save"() {
        setup:
        println("*****************************test fail save******************************************")
        def testInstance
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        testInstance = serviceInstance.save(params)
        then:
        testInstance != null
        required_properties.each { String property ->
            testInstance?."${property}" == null
        }
        testInstance.errors.allErrors.size() > 0
        println("test instance save fail with errors ${testInstance.errors.allErrors}")
    }

    /**
     * @goal test sendList method.
     * @expectedResult valid instance.
     */
    def "test success sendList"() {
        setup:
        println("*****************************test success sendList******************************************")
        def testInstance
        def instanceToSave = saveEntity()
        Map map = [:]
        map["id"] = instanceToSave?.id
        map["fromDate"] = "22/05/2017"
        map["manualOutgoingNo"] = "123456"
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        when:
        domain_class.withTransaction { status ->
            testInstance = serviceInstance.sendList(params)
            status.setRollbackOnly()
        }
        then:
        testInstance != null
        required_properties.each { String property ->
            getPropertyValue(property,testInstance) != null
            getPropertyValue(property,testInstance) == getPropertyValue(property,map)
        }
        testInstance.errors.allErrors.size() == 0
        println("test sendList success done with instance ${testInstance}")
    }

    /**
     * @goal test sendList method.
     * @expectedResult null instance and contains errors.
     */
    def "test fail sendList"() {
        setup:
        println("*****************************test fail sendList******************************************")
        def testInstance
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        testInstance = serviceInstance.sendList(params)
        then:
        println "testInstance: ${testInstance}"
        testInstance != null
        required_properties.each { String property ->
            testInstance?."${property}" == null
        }
        testInstance.errors.allErrors.size() > 0
        println("test instance sendList fail with errors ${testInstance.errors.allErrors}")
    }



    /**
     * @goal test receiveList method.
     * @expectedResult valid instance.
     */
    def "test success receiveList"() {
        setup:
        println("*****************************test success receiveList******************************************")
        def testInstance
        def instanceToSave = saveEntity()
        Map map = [:]
        map["id"] = instanceToSave?.id
        map["fromDate"] = "22/05/2017"
        map["manualOutgoingNo"] = "123456"
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        when:
        domain_class.withTransaction { status ->
            testInstance = serviceInstance.receiveList(params)
            status.setRollbackOnly()
        }
        then:
        testInstance != null
        required_properties.each { String property ->
            getPropertyValue(property,testInstance) != null
            getPropertyValue(property,testInstance) == getPropertyValue(property,map)
        }
        testInstance.errors.allErrors.size() == 0
        println("test receiveList success done with instance ${testInstance}")
    }

    /**
     * @goal test receiveList method.
     * @expectedResult null instance and contains errors.
     */
    def "test fail receiveList"() {
        setup:
        println("*****************************test fail receiveList******************************************")
        def testInstance
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        testInstance = serviceInstance.receiveList(params)
        then:
        println "testInstance: ${testInstance}"
        testInstance != null
        required_properties.each { String property ->
            testInstance?."${property}" == null
        }
        testInstance.errors.allErrors.size() > 0
        println("test instance receiveList fail with errors ${testInstance.errors.allErrors}")
    }


    /**
     * @goal test closeList method.
     * @expectedResult valid instance.
     */
    def "test success closeList"() {
        setup:
        println("*****************************test success closeList******************************************")
        def testInstance
        def instanceToSave = saveEntity()
        Map map = [:]
        map["id"] = instanceToSave?.id
        map["fromDate"] = "22/05/2017"
        map["manualOutgoingNo"] = "123456"
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        when:
        domain_class.withTransaction { status ->
            testInstance = serviceInstance.closeList(params)
            status.setRollbackOnly()
        }
        then:
        testInstance != null
        required_properties.each { String property ->
            getPropertyValue(property,testInstance) != null
            getPropertyValue(property,testInstance) == getPropertyValue(property,map)
        }
        testInstance.errors.allErrors.size() == 0
        println("test closeList success done with instance ${testInstance}")
    }

    /**
     * @goal test closeList method.
     * @expectedResult null instance and contains errors.
     */
    def "test fail closeList"() {
        setup:
        println("*****************************test fail closeList******************************************")
        def testInstance
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        testInstance = serviceInstance.closeList(params)
        then:
        println "testInstance: ${testInstance}"
        testInstance != null
        required_properties.each { String property ->
            testInstance?."${property}" == null
        }
        testInstance.errors.allErrors.size() > 0
        println("test instance closeList fail with errors ${testInstance.errors.allErrors}")
    }




    /**
     * @goal test addDispatchRequestToList method.
     * @expectedResult valid instance.
     */
    def "test success addApplicants"() {
        setup:
        println("*****************************test success addDispatchRequestToList******************************************")
        TraineeList traineeList
        Applicant testInstance

        def instanceToSave = saveEntity()
        println instanceToSave
        ApplicantStatusHistory applicantStatusHistory = ApplicantStatusHistory.build(applicantStatus: EnumApplicantStatus.UNDER_TRAINING, fromDate: ZonedDateTime.now(), toDate: ZonedDateTime.now().plusYears(1))
        testInstance = Applicant.build(applicantCurrentStatus:applicantStatusHistory)

        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params["checked_applicantIdsList"] = [testInstance?.id]
        params["traineeListId"] = instanceToSave?.id
        params["id"] = instanceToSave?.id

        when:
        domain_class.withTransaction { status ->
            traineeList = serviceInstance.addApplicants(params)
            status.setRollbackOnly()
        }
        then:
        !traineeList.hasErrors()
        !testInstance.hasErrors()
        println("test addApplicants success done with instance ${testInstance}")
    }

    /**
     * @goal test addDispatchRequestToList method.
     * @expectedResult null instance and contains errors.
     */
    def "test fail addApplicants"() {
        setup:
        println("*****************************test fail addDisciplinaryRequestToList******************************************")
        TraineeList traineeList
        def instanceToSave = saveEntity()
        when:
        Applicant applicant = new Applicant()
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params["traineeListId"] = instanceToSave?.id
        params["id"] = instanceToSave?.id
        traineeList = serviceInstance.addApplicants(params)
        then:
        traineeList.hasErrors()
        println("test instance addApplicants fail with result ${traineeList}")
    }



    /**
     * @goal test closeList method.
     * @expectedResult valid instance.
     */
    def "test success changeApplicantToTrainingPassed"() {
        setup:
        println("*****************************test success changeApplicantToTrainingPassed******************************************")
        def instanceToSave = saveEntity()
        ApplicantStatusHistory applicantStatusHistory = ApplicantStatusHistory.build(applicantStatus: EnumApplicantStatus.UNDER_TRAINING, fromDate: ZonedDateTime.now(), toDate: ZonedDateTime.now().plusYears(1))
        Applicant applicant = Applicant.build(applicantCurrentStatus:applicantStatusHistory)

        TraineeListEmployee traineeListEmployee = TraineeListEmployee.build(
                applicant: applicant,
                recordStatus:EnumListRecordStatus.NEW,
                traineeList:instanceToSave
        )

        println "traineeListEmployee: ${traineeListEmployee}"
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params["check_applicantTableInTraineeList"] = [applicant?.id]
        params["traineeList.id"] = instanceToSave?.id
        params["id"] = instanceToSave?.id
        params["orderNumber"] = "543"
        params["noteDate"]= "22/09/2017"
        params["note"] = "accept note"
        params["mark"] = "88"
        params.trainingEvaluation =ps.gov.epsilon.hr.enums.v1.EnumTrainingEvaluation.VERY_GOOD
        def map
        when:
        domain_class.withTransaction { status ->
            map = serviceInstance.changeApplicantToTrainingPassed(params)
            status.setRollbackOnly()
        }
        then:
        println "map: ${map}"
        map.saved == true
        map.errors.size() == 0
        println("test instance changeApplicantToTrainingPassed success is done with errors${map.errors}")
    }

    /**
     * @goal test changeApplicantToTrainingPassed method.
     * @expectedResult null instance and contains errors.
     */
    def "test fail changeApplicantToTrainingPassed"() {
        setup:
        println("*****************************test fail changeApplicantToTrainingPassed******************************************")
        def map
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        map = serviceInstance.changeApplicantToTrainingPassed(params)
        then:
        map.saved == false
        map.errors.size() > 0
        println("test instance changeApplicantToTrainingPassed fail with errors ${map.errors}")
    }


    /**
     * @goal test changeApplicantToRejected method.
     * @expectedResult valid instance.
     */
    def "test success changeApplicantToRejected"() {
        setup:
        println("*****************************test success changeApplicantToRejected******************************************")
        def instanceToSave = saveEntity()

        DescriptionInfo descriptionInfo = new DescriptionInfo(localName: "غياب")
        TrainingRejectionReason trainingRejectionReason = TrainingRejectionReason.build(
                descriptionInfo: descriptionInfo
        )

        ApplicantStatusHistory applicantStatusHistory = ApplicantStatusHistory.build(applicantStatus: EnumApplicantStatus.UNDER_TRAINING, fromDate: ZonedDateTime.now(), toDate: ZonedDateTime.now().plusYears(1))
        Applicant applicant = Applicant.build(applicantCurrentStatus:applicantStatusHistory)

        TraineeListEmployee traineeListEmployee = TraineeListEmployee.build(
                applicant: applicant,
                traineeList:instanceToSave
        )

        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params["check_applicantTableInTraineeList"] = [applicant?.id]
        params["traineeList.id"] = instanceToSave?.id
        params["id"] = instanceToSave?.id
        params["orderNumber"] = "369"
        params["noteDate"]= "22/09/2017"
        params["note"] = "reject note"
        params["trainingRejectionReason"] = trainingRejectionReason.id
        def map
        when:
        domain_class.withTransaction { status ->
            map = serviceInstance.changeApplicantToRejected(params)
            status.setRollbackOnly()
        }
        then:
        println "map: ${map}"
        map.saved == true
        map.errors.size() == 0
        println("test instance changeApplicantToRejected success is done with errors${map.errors}")
    }

    /**
     * @goal test changeApplicantToRejected method.
     * @expectedResult valid instance.
     */
    def "test fail changeApplicantToRejected with no note"() {
        setup:
        println("*****************************test fail changeApplicantToRejected with no note******************************************")
        def instanceToSave = saveEntity()

        DescriptionInfo descriptionInfo = new DescriptionInfo(localName: "غياب")
        TrainingRejectionReason trainingRejectionReason = TrainingRejectionReason.build(
                descriptionInfo: descriptionInfo
        )

        ApplicantStatusHistory applicantStatusHistory = ApplicantStatusHistory.build(applicantStatus: EnumApplicantStatus.UNDER_TRAINING, fromDate: ZonedDateTime.now(), toDate: ZonedDateTime.now().plusYears(1))
        Applicant applicant = Applicant.build(applicantCurrentStatus:applicantStatusHistory)

        TraineeListEmployee traineeListEmployee = TraineeListEmployee.build(
                applicant: applicant,
                traineeList:instanceToSave
        )

        println "applicant: ${applicant}"
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params["check_applicantTableInTraineeList"] = [traineeListEmployee?.id]
        params["traineeList.id"] = instanceToSave?.id
        params["id"] = instanceToSave?.id
        params["trainingRejectionReason"] = trainingRejectionReason.id
        def map
        when:
        domain_class.withTransaction { status ->
            map = serviceInstance.changeApplicantToRejected(params)
            status.setRollbackOnly()
        }
        then:
        println "map: ${map}"
        map.saved == false
        map.errors.size() > 0
        println("test changeApplicantToRejected failed with no note, with errors${map.errors}")
    }

    /**
     * @goal test changeApplicantToRejected method.
     * @expectedResult null instance and contains errors.
     */
    def "test fail changeApplicantToRejected"() {
        setup:
        println("*****************************test fail changeApplicantToRejected******************************************")
        def map
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        map = serviceInstance.changeApplicantToRejected(params)
        then:
        map.saved == false
        map.errors.size() > 0
        println("test instance changeApplicantToRejected fail with errors ${map.errors}")
    }


}