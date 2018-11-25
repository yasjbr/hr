package ps.gov.epsilon.hr.firm.disciplinary

import grails.buildtestdata.CircularCheckList
import grails.buildtestdata.DomainInstanceBuilder
import grails.buildtestdata.handler.NullableConstraintHandler
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import nl.flotsam.xeger.Xeger
import org.grails.core.DefaultGrailsDomainClass
import org.springframework.mock.web.MockHttpServletRequest
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryCategory
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryJudgment
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryListJudgmentSetup
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryReason
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec
import ps.police.test.utils.TestDataObject

@Integration
@Rollback
/**
 * integration test for DisciplinaryList service
 */
class DisciplinaryListIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class = DisciplinaryList
        service_domain = DisciplinaryListService
        entity_name = "disciplinaryList"
        required_properties = PCPUtils.getRequiredFields(DisciplinaryList)
        filtered_parameters = ["id"];
        primary_keys = ["encodedId","id"]
        exclude_methods = ["delete","save","autocomplete"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
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
        PagedList result = serviceInstance.searchWithRemotingValues(params)
        then:
        result.totalCount == (entity_total_count + 3)
        filtered_parameters.each { property ->
            getPropertyValue(property,result?.resultList[0]) == getPropertyValue(property,testInstance)
        }
        println("test instance searchWithRemotingValues done with totalCount ${result.totalCount}")
        println("test instance searchWithRemotingValues done with result ${result}")
    }

    /**
     * @goal test filter searchWithRemotingValues method with filter data.
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
        result.totalCount == 1
        filtered_parameters.each { property ->
            getPropertyValue(property,result?.resultList[0]) == getPropertyValue(property,testInstance)
        }
        println("test instance searchWithRemotingValues done with result ${result}")
    }


    /**
     * @goal test customSearch method.
     * @expectedResult known total count.
     */
    def "test customSearch"() {
        setup:
        println("*****************************test customSearch******************************************")
        saveEntity()
        saveEntity()
        def testInstance = saveEntity()
        Map map = [max: 10000, offset: 0]
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())

        session_parameters.each {key,value->
            PCPSessionUtils.setValue(key,getPropertyValue(value,testInstance))
        }

        when:
        PagedList result = serviceInstance.customSearch(params)
        then:
        result.totalCount == (entity_total_count + 3)
        filtered_parameters.each { property ->
            getPropertyValue(property,result?.resultList[0]) == getPropertyValue(property,testInstance)
        }
        println("test instance customSearch done with totalCount ${result.totalCount}")
        println("test instance customSearch done with result ${result}")
    }

    /**
     * @goal test filter customSearch method with filter data.
     * @expectedResult known total count.
     */
    def "test filter customSearch"() {
        setup:
        println("*****************************test filter customSearch******************************************")
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
        result = serviceInstance.customSearch(params)
        then:
        result.totalCount == 1
        filtered_parameters.each { property ->
            getPropertyValue(property,result?.resultList[0]) == getPropertyValue(property,testInstance)
        }
        println("test instance customSearch done with result ${result}")
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
     * @goal test sendData method.
     * @expectedResult valid instance.
     */
    def "test success sendData"() {
        setup:
        println("*****************************test success sendData******************************************")
        def testInstance
        def instanceToSave = saveEntity()
        Map map = [:]
        map["id"] = instanceToSave?.id
        map["fromDate"] = "22/05/2017"
        map["manualOutgoingNo"] = "123456"
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        when:
        domain_class.withTransaction { status ->
            testInstance = serviceInstance.sendData(params)
            status.setRollbackOnly()
        }
        then:
        testInstance != null
        required_properties.each { String property ->
            getPropertyValue(property,testInstance) != null
            getPropertyValue(property,testInstance) == getPropertyValue(property,map)
        }
        testInstance.errors.allErrors.size() == 0
        println("test sendData success done with instance ${testInstance}")
    }

    /**
     * @goal test sendData method.
     * @expectedResult null instance and contains errors.
     */
    def "test fail sendData"() {
        setup:
        println("*****************************test fail sendData******************************************")
        def testInstance
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        testInstance = serviceInstance.sendData(params)
        then:
        testInstance != null
        required_properties.each { String property ->
            testInstance?."${property}" == null
        }
        testInstance.errors.allErrors.size() > 0
        println("test instance sendData fail with errors ${testInstance.errors.allErrors}")
    }

    /**
     * @goal test addDisciplinaryRequestToList method.
     * @expectedResult valid instance.
     */
    def "test success addDisciplinaryRequestToList"() {
        setup:
        println("*****************************test success addDisciplinaryRequestToList******************************************")
        DisciplinaryList disciplinaryList
        DisciplinaryRequest testInstance


        //custom data
        DisciplinaryJudgment judgment1 = DisciplinaryJudgment.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"وقف عن العمل"),unitIds: [10L, 11L, 12L])
        DisciplinaryJudgment judgment2 = DisciplinaryJudgment.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"سجن"),unitIds: [10L,11L,12L])
        DisciplinaryJudgment currencyJudgment = DisciplinaryJudgment.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"غرامة مالية"),currencyIds: [1L,4L],isCurrencyUnit: true)
        DisciplinaryCategory disciplinaryCategory = DisciplinaryCategory.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"انضباطية"))
        DisciplinaryReason disciplinaryReason1 = DisciplinaryReason.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"سرقة"),disciplinaryCategories:disciplinaryCategory)
        DisciplinaryReason disciplinaryReason2 = DisciplinaryReason.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"هروب"),disciplinaryCategories:disciplinaryCategory)
        DisciplinaryListJudgmentSetup judgmentSetup = DisciplinaryListJudgmentSetup.build(disciplinaryJudgment: currencyJudgment,disciplinaryCategory:disciplinaryCategory)


        EmployeePromotion employeePromotion = EmployeePromotion.build()
        EmploymentRecord employmentRecord = EmploymentRecord.build()
        Employee employee = Employee.build(currentEmployeeMilitaryRank: employeePromotion,currentEmploymentRecord: employmentRecord)

        DisciplinaryRecordJudgment recordJudgment1 = DisciplinaryRecordJudgment.build(
                disciplinaryReasons:[disciplinaryReason1,disciplinaryReason2],
                disciplinaryJudgment:judgment1,
                value:"20",
                unitId:11L,
        )

        DisciplinaryRecordJudgment recordJudgment2 = DisciplinaryRecordJudgment.build(
                disciplinaryReasons:[disciplinaryReason1,disciplinaryReason2],
                disciplinaryJudgment:judgment2,
                value:"4",
                unitId:12L,
        )

        DisciplinaryRecordJudgment recordJudgment3 = DisciplinaryRecordJudgment.build(
                disciplinaryReasons:[disciplinaryReason1,disciplinaryReason2],
                disciplinaryJudgment:currencyJudgment,
                value:"200",
                unitId:4L,
        )

        testInstance = DisciplinaryRequest.build(
                employee:employee,
                disciplinaryCategory:disciplinaryCategory,
                disciplinaryJudgments:[recordJudgment1,recordJudgment2,recordJudgment3]
        )

        session_parameters.each {key,value->
            PCPSessionUtils.setValue(key,getPropertyValue(value,testInstance))
        }

        when:
        domain_class.withTransaction { status ->
            disciplinaryList = serviceInstance.addDisciplinaryRequestToList(testInstance)
            status.setRollbackOnly()
        }
        then:
        !disciplinaryList.hasErrors()
        testInstance.requestStatus == EnumRequestStatus.ADD_TO_LIST
        !testInstance.hasErrors()
        println("test addDisciplinaryRequestToList success done with instance ${testInstance}")
    }

    /**
     * @goal test addDisciplinaryRequestToList method.
     * @expectedResult null instance and contains errors.
     */
    def "test fail addDisciplinaryRequestToList"() {
        setup:
        println("*****************************test fail addDisciplinaryRequestToList******************************************")
        DisciplinaryList disciplinaryList
        when:
        DisciplinaryRequest disciplinaryRequest = new DisciplinaryRequest()
        disciplinaryList = serviceInstance.addDisciplinaryRequestToList(disciplinaryRequest)
        then:
        disciplinaryList.hasErrors()
        println("test instance addDisciplinaryRequestToList fail with result ${disciplinaryList}")
    }



    @Override
    DisciplinaryList saveEntity(TestDataObject tableData = null, Boolean discardSave = false) {
        if(!tableData){
            tableData = new TestDataObject()
            tableData.requiredProperties = required_properties
            tableData.domain = domain_class
            tableData.objectName = entity_name
            tableData.data = table_data?.data
            tableData?.isJoinTable = is_join_table
        }
        DisciplinaryList instance
        Map props = [:]
        if(tableData?.disableSave){
            instance = tableData?.domain?.newInstance(props)
        }else{
            tableData?.domain?.withTransaction { status ->
                Map addedMap = [:]
                if (tableData.isJoinTable) {
                    addedMap.putAll(props)
                }
                instance = tableData?.domain?.buildWithoutValidation(addedMap)

                //add details
                def disciplinaryRecordJudgment1 = DisciplinaryRecordJudgment.buildWithoutSave()
                def disciplinaryRecordJudgment2 = DisciplinaryRecordJudgment.buildWithoutSave()
                def disciplinaryRecordJudgment3 = DisciplinaryRecordJudgment.buildWithoutSave()

                instance.addToDisciplinaryRecordJudgment(disciplinaryRecordJudgment1)
                instance.addToDisciplinaryRecordJudgment(disciplinaryRecordJudgment2)
                instance.addToDisciplinaryRecordJudgment(disciplinaryRecordJudgment3)


                boolean validated = instance.validate()
                if(!validated) {
                    DomainInstanceBuilder builder = builders.get(tableData?.objectName)
                    if (!builder) {
                        builder = new DomainInstanceBuilder(new DefaultGrailsDomainClass(tableData?.domain))
                        builders.put(tableData?.objectName, builder)
                    }
                }

                once_save_properties.each {property->
                    if(PCPSessionUtils.getValue(property)){
                        instance."${property}" = PCPSessionUtils.getValue(property)
                    }
                }

                if (!discardSave) {
                    instance.save(flush: true,failOnError:true)
                }

                //set current status
                def currentStatus = CorrespondenceListStatus.buildWithoutSave(correspondenceList: instance)
                instance.currentStatus = currentStatus

                if (!discardSave) {
                    instance.save(flush: true,failOnError:true)
                }

                once_save_properties.each {property->
                    if(!PCPSessionUtils.getValue(property)){
                        PCPSessionUtils.setValue(property,instance."${property}")
                    }
                }

                status.setRollbackOnly()
            }
        }
        return instance
    }





}